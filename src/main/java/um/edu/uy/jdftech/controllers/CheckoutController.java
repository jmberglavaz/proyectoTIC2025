package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.services.CarritoService;
import um.edu.uy.jdftech.services.CheckoutService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ClienteRepository clienteRepository;

    // =======================  AUXILIAR  =======================
    private Cliente obtenerClienteActual(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            throw new RuntimeException("Debe iniciar sesión para realizar checkout");
        }
        return cliente;
    }

    // =======================  GET /checkout  =======================
    @GetMapping("/checkout")
    public String view(HttpSession session, Model model) {

        // Carrito EN MEMORIA (mismo que usa /carrito)
        var items = carritoService.getItems();
        if (items.isEmpty()) {
            return "redirect:/carrito?error=carrito-vacio";
        }

        double subtotal = carritoService.getSubtotal();
        double costoEnvio = subtotal > 0 ? 120 : 0;
        double total = subtotal + costoEnvio;

        model.addAttribute("items", items);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", costoEnvio);
        model.addAttribute("total", total);
        model.addAttribute("page", "checkout");

        // Si hay cliente logueado, cargamos direcciones y medios de pago
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            List<Direccion> direcciones = checkoutService.obtenerDirecciones(cliente);
            List<MedioDePago> mediosDePago = checkoutService.obtenerMediosDePago(cliente);

            model.addAttribute("cliente", cliente);
            model.addAttribute("direcciones", direcciones);
            model.addAttribute("mediosDePago", mediosDePago);
        }

        return "user/checkout";
    }

    // ==================  POST /checkout/agregar-tarjeta  ==================
    @PostMapping("/checkout/agregar-tarjeta")
    public String agregarTarjeta(@RequestParam Long cardNumber,
                                 @RequestParam String firstName,
                                 @RequestParam String lastName,
                                 @RequestParam String expMonth,
                                 @RequestParam String expYear,
                                 @RequestParam Integer cvv,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Cliente cliente = obtenerClienteActual(session);

        try {
            String fechaStr = "01/" + expMonth + "/" + expYear;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date expirationDate = sdf.parse(fechaStr);

            checkoutService.agregarMedioDePago(cliente, cardNumber, cvv,
                    firstName, lastName, expirationDate);
            ra.addFlashAttribute("msg", "Tarjeta agregada correctamente");

        } catch (ParseException e) {
            ra.addFlashAttribute("error", "Fecha de expiración inválida");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al agregar tarjeta: " + e.getMessage());
        }

        return "redirect:/checkout";
    }

    // =========================  POST /checkout  =========================
    @PostMapping("/checkout")
    public String submit(@RequestParam(required = false) String accion,
                         @RequestParam(required = false) Long direccion_id,
                         @RequestParam(required = false) Long tarjeta_id,
                         @RequestParam(required = false) String cvv_seleccionada,
                         HttpSession session,
                         RedirectAttributes ra) {

        System.out.println("🔥🔥🔥 POST /checkout EJECUTADO 🔥🔥🔥");
        System.out.println("📥 Acción: " + accion);
        System.out.println("📍 Dirección ID: " + direccion_id);
        System.out.println("💳 Tarjeta ID: " + tarjeta_id);
        System.out.println("🔒 CVV: " + cvv_seleccionada);

        Cliente cliente = obtenerClienteActual(session);

        try {
            // ---- confirmar pedido ----
            if ("confirmar_pedido".equals(accion)) {
                System.out.println("🎯 CONFIRMAR PEDIDO DETECTADO");

                if (direccion_id == null || tarjeta_id == null || cvv_seleccionada == null || cvv_seleccionada.trim().isEmpty()) {
                    System.out.println("❌ FALTAN DATOS REQUERIDOS");
                    ra.addFlashAttribute("error", "Debe seleccionar dirección, tarjeta e ingresar CVV");
                    return "redirect:/checkout";
                }

                // Validar CVV
                Integer cvv;
                try {
                    cvv = Integer.parseInt(cvv_seleccionada.trim());
                    System.out.println("✅ CVV parseado: " + cvv);
                    if (cvv < 0 || cvv > 9999) {
                        System.out.println("❌ CVV fuera de rango");
                        ra.addFlashAttribute("error", "CVV debe ser un número de 3 o 4 dígitos");
                        return "redirect:/checkout";
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ CVV no es número: " + cvv_seleccionada);
                    ra.addFlashAttribute("error", "CVV debe ser un número válido de 3 o 4 dígitos");
                    return "redirect:/checkout";
                }

                // 🆕 OBTENER ITEMS DEL CARRITO EN MEMORIA
                List<CarritoItemDTO> items = carritoService.getItems();
                System.out.println("🛒 Items en carrito: " + items.size());

                if (items.isEmpty()) {
                    System.out.println("❌ CARRITO VACÍO");
                    ra.addFlashAttribute("error", "El carrito está vacío");
                    return "redirect:/checkout";
                }

                System.out.println("🔄 LLAMANDO A checkoutService.crearPedido...");

                // 🆕 PASAR LA LISTA DE ITEMS COMO PARÁMETRO
                Pedido pedido = checkoutService.crearPedido(cliente, direccion_id, tarjeta_id, cvv, items);

                System.out.println("✅ PEDIDO CREADO - ID: " + pedido.getId());

                // 🆕 VACIAR EL CARRITO DESPUÉS DE CREAR EL PEDIDO
                carritoService.clear();
                System.out.println("🗑️ Carrito vaciado");

                ra.addFlashAttribute("msg", "¡Pedido confirmado exitosamente!");
                return "redirect:/pedido/" + pedido.getId();
            }

        } catch (Exception e) {
            System.out.println("💥 ERROR: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/checkout";
    }
}
