package um.edu.uy.jdftech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.services.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    @Autowired
    private ToppingService toppingService;

    @Autowired
    private AderezoService aderezoService;

    @Autowired
    private AcompanamientoService acompanamientoService;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private PedidoService pedidoService;

    /* ======================
       Páginas (GET) - ACTUALIZADO
       ====================== */
    @GetMapping
    public String panel() {
        return "admin/index-admin";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        // Cargar todos los toppings por categoría para pizzas
        model.addAttribute("masas", toppingService.verToppingsDeTipo('M'));
        model.addAttribute("salsas", toppingService.verToppingsDeTipo('S'));
        model.addAttribute("quesos", toppingService.verToppingsDeTipo('Q'));
        model.addAttribute("toppingsPizza", toppingService.verToppingsDeTipo('X'));

        // Cargar toppings para hamburguesas
        model.addAttribute("panes", toppingService.verToppingsDeTipo('P'));
        model.addAttribute("carnes", toppingService.verToppingsDeTipo('C'));
        model.addAttribute("extras", toppingService.verToppingsDeTipo('X'));

        // Cargar aderezos, acompañamientos y bebidas
        model.addAttribute("aderezos", aderezoService.findAll());
        model.addAttribute("acompanamientos", acompanamientoService.findAll());
        model.addAttribute("bebidas", bebidaService.findAll());

        return "admin/productos-admin";
    }

    @GetMapping("/pedidos")
    public String pedidos(@RequestParam(required = false) String numero, Model model) {
        try {
            List<Pedido> pedidos;

            if (numero != null && !numero.trim().isEmpty()) {
                // Búsqueda por número de pedido
                Long pedidoId = Long.parseLong(numero.replace("#", "").trim());
                Pedido pedido = pedidoService.findById(pedidoId).orElse(null);
                pedidos = (pedido != null) ? List.of(pedido) : Collections.emptyList();
            } else {
                // Mostrar todos los pedidos activos (no entregados)
                pedidos = pedidoService.findPedidosActivos();
            }

            model.addAttribute("pedidos", pedidos != null ? pedidos : Collections.emptyList());
        } catch (NumberFormatException e) {
            // Si el número no es válido, mostrar lista vacía
            model.addAttribute("pedidos", Collections.emptyList());
        }

        return "admin/pedidos-admin";
    }

    @GetMapping("/historial")
    public String historial() {
        return "admin/historial-admin";
    }

    @GetMapping("/administradores")
    public String administradores() {
        return "admin/admins-admin";
    }

    /* ======================
       PRODUCTOS - Toppings (Pizzas y Hamburguesas)
       ====================== */
    @PostMapping("/productos/crear")
    public String crearTopping(@RequestParam String nombre,
                               @RequestParam(required = false) Double precio,
                               @RequestParam char hamburguesaOPizza,
                               @RequestParam char tipo,
                               RedirectAttributes ra) {

        System.out.println("Creando topping:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Tipo producto: " + hamburguesaOPizza);
        System.out.println("Categoría: " + tipo);

        try {
            double precioFinal = (precio != null) ? precio : 0.0;

            Topping nuevoTopping = new Topping(
                    nombre,
                    hamburguesaOPizza,
                    tipo,
                    precioFinal,
                    LocalDateTime.now()
            );

            Topping toppingGuardado = toppingService.crear(nuevoTopping);

            System.out.println("Topping guardado con ID: " + toppingGuardado.getIdTopping());
            ra.addFlashAttribute("msg", "Producto '" + nombre + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar topping: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarTopping(@PathVariable Long id, RedirectAttributes ra) {
        System.out.println("Eliminando topping con ID: " + id);

        try {
            toppingService.delete(id);
            ra.addFlashAttribute("msg", "Producto eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar topping: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       ADEREZOS
       ====================== */
    @PostMapping("/aderezos/crear")
    public String crearAderezo(@RequestParam String nombre,
                               @RequestParam Double precio,
                               RedirectAttributes ra) {

        System.out.println("Creando aderezo:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);

        try {
            Aderezo nuevoAderezo = new Aderezo(nombre, precio);
            Aderezo aderezoGuardado = aderezoService.createNewAderezo(nuevoAderezo);

            System.out.println("Aderezo guardado con ID: " + aderezoGuardado.getIdAderezo());
            ra.addFlashAttribute("msg", "Aderezo '" + nombre + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar aderezo: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el aderezo: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/aderezos/eliminar/{id}")
    public String eliminarAderezo(@PathVariable Long id, RedirectAttributes ra) {
        System.out.println("Eliminando aderezo con ID: " + id);

        try {
            aderezoService.deleteAderezo(id);
            ra.addFlashAttribute("msg", "Aderezo eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar aderezo: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el aderezo: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       ACOMPAÑAMIENTOS
       ====================== */
    @PostMapping("/acompanamientos/crear")
    public String crearAcompanamiento(@RequestParam String name,
                                      @RequestParam String size,
                                      @RequestParam Double price,
                                      RedirectAttributes ra) {

        System.out.println("Creando acompañamiento:");
        System.out.println("Nombre: " + name);
        System.out.println("Tamaño: " + size);
        System.out.println("Precio: " + price);

        try {
            Acompanamiento nuevoAcompanamiento = Acompanamiento.builder()
                    .name(name)
                    .size(size)
                    .price(price)
                    .build();

            Acompanamiento acompanamientoGuardado = acompanamientoService.createNewAcompanamiento(nuevoAcompanamiento);

            System.out.println("Acompañamiento guardado con ID: " + acompanamientoGuardado.getId());
            ra.addFlashAttribute("msg", "Acompañamiento '" + name + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar acompañamiento: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el acompañamiento: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/acompanamientos/eliminar/{id}")
    public String eliminarAcompanamiento(@PathVariable Long id, RedirectAttributes ra) {
        System.out.println("Eliminando acompañamiento con ID: " + id);

        try {
            acompanamientoService.deleteAcompanamiento(id);
            ra.addFlashAttribute("msg", "Acompañamiento eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar acompañamiento: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el acompañamiento: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       BEBIDAS
       ====================== */
    @PostMapping("/bebidas/crear")
    public String crearBebida(@RequestParam String name,
                              @RequestParam String size,
                              @RequestParam Double price,
                              RedirectAttributes ra) {

        System.out.println("Creando bebida:");
        System.out.println("Nombre: " + name);
        System.out.println("Tamaño: " + size);
        System.out.println("Precio: " + price);

        try {
            Bebida nuevaBebida = Bebida.builder()
                    .name(name)
                    .size(size)
                    .price(price)
                    .build();

            Bebida bebidaGuardada = bebidaService.createNewBebida(nuevaBebida);

            System.out.println("Bebida guardada con ID: " + bebidaGuardada.getId());
            ra.addFlashAttribute("msg", "Bebida '" + name + "' agregada correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar bebida: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar la bebida: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/bebidas/eliminar/{id}")
    public String eliminarBebida(@PathVariable Long id, RedirectAttributes ra) {
        System.out.println("Eliminando bebida con ID: " + id);

        try {
            bebidaService.deleteBebida(id);
            ra.addFlashAttribute("msg", "Bebida eliminada correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar bebida: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar la bebida: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/pedidos")
    public String actualizarPedido(@RequestParam Long pedido_id,
                                   @RequestParam String estado,
                                   RedirectAttributes ra) {
        try {
            // Convertir el string del formulario al enum
            EstadoPedido nuevoEstado = switch(estado) {
                case "en_cola" -> EstadoPedido.EN_COLA;
                case "en_preparacion" -> EstadoPedido.EN_PREPARACION;
                case "en_camino" -> EstadoPedido.EN_CAMINO;
                case "entregado" -> EstadoPedido.ENTREGADO;
                default -> throw new IllegalArgumentException("Estado inválido: " + estado);
            };

            Pedido pedidoActualizado = pedidoService.updateStatus(pedido_id, nuevoEstado);

            ra.addFlashAttribute("msg",
                    "Pedido #" + String.format("%06d", pedido_id) +
                            " actualizado a: " + estado.replace("_", " "));

        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Error al actualizar el pedido: " + e.getMessage());
        }

        return "redirect:/admin/pedidos";
    }

    @PostMapping("/historial")
    public String accionesHistorial(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", "Acción de historial recibida.");
        return "redirect:/admin/historial";
    }

    @PostMapping("/administradores")
    public String gestionarAdministradores(@RequestParam(required = false) String nombre,
                                           @RequestParam(required = false) String apellido,
                                           @RequestParam(required = false) String email,
                                           @RequestParam(required = false) String password,
                                           @RequestParam(required = false, name = "password_confirm") String passwordConfirm,
                                           @RequestParam(required = false, name = "admin_id") String adminId,
                                           @RequestParam(required = false, name = "accion") String accion,
                                           RedirectAttributes ra) {
        // TODO: en base a 'accion' y/o presencia de adminId decidir crear/eliminar/reset
        ra.addFlashAttribute("msg", "Acción de administradores recibida.");
        return "redirect:/admin/administradores";
    }
}