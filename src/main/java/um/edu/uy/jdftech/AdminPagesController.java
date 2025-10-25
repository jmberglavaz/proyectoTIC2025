package um.edu.uy.jdftech;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    /* ======================
       Páginas (GET)
       ====================== */
    @GetMapping
    public String panel() {
        // templates/index-admin.html
        return "index-admin";
    }

    @GetMapping("/productos")
    public String productos() {
        // templates/productos-admin.html
        return "productos-admin";
    }

    @GetMapping("/pedidos")
    public String pedidos() {
        // templates/pedidos-admin.html
        return "pedidos-admin";
    }

    @GetMapping("/historial")
    public String historial() {
        // templates/historial-admin.html
        return "historial-admin";
    }

    @GetMapping("/administradores")
    public String administradores() {
        // templates/admins-admin.html
        return "admins-admin";
    }

    /* ======================
       Acciones (POST placeholders)
       - Por ahora solo redirigen para que los forms funcionen.
       - Después los conectamos con servicios y BDD.
       ====================== */

    // Productos: agregar/eliminar masa/salsa/queso/topping/pan/carne/aderezo/extra/bebida
    @PostMapping("/productos")
    public String gestionarProductos(@RequestParam(required = false) String nombre,
                                     @RequestParam(required = false) Integer precio,
                                     RedirectAttributes ra) {
        // TODO: llamar a servicio de catálogo
        ra.addFlashAttribute("msg", "Acción de productos recibida correctamente.");
        return "redirect:/admin/productos";
    }

    // Pedidos: cambiar estado / buscar
    @PostMapping("/pedidos")
    public String actualizarPedido(@RequestParam String pedido_id,
                                   @RequestParam String estado,
                                   RedirectAttributes ra) {
        // TODO: actualizar estado del pedido en servicio/BDD
        ra.addFlashAttribute("msg", "Pedido #" + pedido_id + " actualizado a: " + estado);
        return "redirect:/admin/pedidos";
    }

    // Historial: usualmente GET con filtros; si tuvieras acciones POST, las manejás acá
    @PostMapping("/historial")
    public String accionesHistorial(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", "Acción de historial recibida.");
        return "redirect:/admin/historial";
    }

    // Administradores: crear/eliminar/restablecer contraseña
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
