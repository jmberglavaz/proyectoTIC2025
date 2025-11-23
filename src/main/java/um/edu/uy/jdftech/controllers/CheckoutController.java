package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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

    // Método auxiliar para obtener cliente logueado
    private Cliente obtenerClienteActual(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            throw new RuntimeException("Debe iniciar sesión para realizar checkout");
        }
        return cliente;
    }

    // GET: Mostrar página de checkout
    @GetMapping("/checkout")
    public String view(HttpSession session, Model model) {
        Cliente cliente = obtenerClienteActual(session);
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);

        // Verificar que el carrito no esté vacío
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito?error=carrito-vacio";
        }

        // Obtener direcciones y medios de pago del cliente
        List<Direccion> direcciones = checkoutService.obtenerDirecciones(cliente);
        List<MedioDePago> mediosDePago = checkoutService.obtenerMediosDePago(cliente);

        // Datos del cliente
        model.addAttribute("cliente", cliente);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("mediosDePago", mediosDePago);
        model.addAttribute("carrito", carrito);
        model.addAttribute("items", carrito.getItems());
        model.addAttribute("total", carritoService.calcularTotal(carrito));
        model.addAttribute("page", "checkout");

        return "user/checkout";
    }

    // POST: Agregar nueva tarjeta (endpoint separado)
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
            // Crear fecha de expiración
            String fechaStr = "01/" + expMonth + "/" + expYear;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date expirationDate = sdf.parse(fechaStr);
            
            checkoutService.agregarMedioDePago(cliente, cardNumber, cvv, firstName, lastName, expirationDate);
            ra.addFlashAttribute("msg", "Tarjeta agregada correctamente");
            
        } catch (ParseException e) {
            ra.addFlashAttribute("error", "Fecha de expiración inválida");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al agregar tarjeta: " + e.getMessage());
        }
        
        return "redirect:/checkout";
    }

    // POST: Manejar las acciones del checkout
    @PostMapping("/checkout")
    public String submit(@RequestParam(required = false) String accion,
                         @RequestParam(required = false) Long direccion_id,
                         @RequestParam(required = false) String domicilio_nuevo,
                         @RequestParam(required = false) String indicaciones_nuevo,
                         @RequestParam(required = false) Long tarjeta_id,
                         @RequestParam(required = false) String cvv_seleccionada,
                         @RequestParam(required = false) String tarjeta_numero_nueva,
                         @RequestParam(required = false) String tarjeta_apellido_nueva,
                         @RequestParam(required = false) String tarjeta_nombre_nueva,
                         @RequestParam(required = false) String tarjeta_exp_mes_nueva,
                         @RequestParam(required = false) String tarjeta_exp_anio_nueva,
                         HttpSession session,
                         @RequestParam(required = false) Integer tarjeta_cvv_nueva,
                         RedirectAttributes ra) {

        Cliente cliente = obtenerClienteActual(session);

        try {
            // Acción: Agregar nueva dirección
            if ("agregar_direccion".equals(accion)) {
                if (domicilio_nuevo == null || domicilio_nuevo.trim().isEmpty()) {
                    ra.addFlashAttribute("error", "Debe ingresar una dirección");
                    return "redirect:/checkout";
                }
                checkoutService.agregarDireccion(cliente, domicilio_nuevo, indicaciones_nuevo);
                ra.addFlashAttribute("msg", "Dirección agregada correctamente");
                return "redirect:/checkout";
            }

            // Acción: Guardar nueva tarjeta (método antiguo, mantener por compatibilidad)
            if ("guardar_tarjeta_nueva".equals(accion)) {
                if (tarjeta_numero_nueva == null || tarjeta_nombre_nueva == null || 
                    tarjeta_apellido_nueva == null || tarjeta_exp_mes_nueva == null || 
                    tarjeta_exp_anio_nueva == null || tarjeta_cvv_nueva == null) {
                    ra.addFlashAttribute("error", "Debe completar todos los campos de la tarjeta");
                    return "redirect:/checkout";
                }

                // Convertir número de tarjeta
                Long cardNumber = Long.parseLong(tarjeta_numero_nueva.replaceAll("\\s+", ""));

                // Crear fecha de expiración
                String fechaStr = "01/" + tarjeta_exp_mes_nueva + "/" + tarjeta_exp_anio_nueva;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date expirationDate = sdf.parse(fechaStr);

                checkoutService.agregarMedioDePago(cliente, cardNumber, tarjeta_cvv_nueva, 
                                                   tarjeta_nombre_nueva, tarjeta_apellido_nueva, expirationDate);
                ra.addFlashAttribute("msg", "Tarjeta guardada correctamente");
                return "redirect:/checkout";
            }

            // Acción: Confirmar pedido
            if ("confirmar_pedido".equals(accion)) {
                if (direccion_id == null || tarjeta_id == null || cvv_seleccionada == null || cvv_seleccionada.trim().isEmpty()) {
                    ra.addFlashAttribute("error", "Debe seleccionar dirección, tarjeta e ingresar CVV");
                    return "redirect:/checkout";
                }

                // Validar que CVV sea un número de 3 dígitos
                Integer cvv;
                try {
                    cvv = Integer.parseInt(cvv_seleccionada.trim());
                    if (cvv < 0 || cvv > 999) {
                        ra.addFlashAttribute("error", "CVV debe ser un número de 3 dígitos");
                        return "redirect:/checkout";
                    }
                } catch (NumberFormatException e) {
                    ra.addFlashAttribute("error", "CVV debe ser un número válido de 3 dígitos");
                    return "redirect:/checkout";
                }

                // Crear pedido
                Pedido pedido = checkoutService.crearPedido(cliente, direccion_id, tarjeta_id, cvv);

                // Redirigir a página de pedido
                ra.addFlashAttribute("msg", "¡Pedido confirmado exitosamente!");
                return "redirect:/pedido/" + pedido.getId();
            }

        } catch (NumberFormatException e) {
            ra.addFlashAttribute("error", "Número de tarjeta inválido");
            return "redirect:/checkout";
        } catch (ParseException e) {
            ra.addFlashAttribute("error", "Fecha de expiración inválida");
            return "redirect:/checkout";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/checkout";
        }

        return "redirect:/checkout";
    }
}
