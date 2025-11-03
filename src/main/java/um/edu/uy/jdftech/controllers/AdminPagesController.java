package um.edu.uy.jdftech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.services.ToppingService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    @Autowired
    private ToppingService toppingService; // ← AGREGAR ESTO

    /* ======================
       Páginas (GET)
       ====================== */
    @GetMapping
    public String panel() {
        return "admin/index-admin";
    }

    @GetMapping("/productos")
    public String productos() {
        return "admin/productos-admin";
    }

    @GetMapping("/pedidos")
    public String pedidos() {
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
       Productos - Crear (ACTUALIZADO)
       ====================== */
    @PostMapping("/productos/crear")
    public String crearProducto(@RequestParam String nombre,
                                @RequestParam(required = false) Double precio, // Cambiado a Double
                                @RequestParam char hamburguesaOPizza, // Cambiado a char
                                @RequestParam char tipo, // Cambiado a char
                                RedirectAttributes ra) {

        System.out.println("Creando producto:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Tipo producto: " + hamburguesaOPizza);
        System.out.println("Categoría: " + tipo);

        try {
            // Si no se proporciona precio, usar 0
            double precioFinal = (precio != null) ? precio : 0.0;

            // Crear el topping
            Topping nuevoTopping = new Topping(
                    nombre,
                    hamburguesaOPizza,
                    tipo,
                    precioFinal,
                    LocalDateTime.now()
            );

            // Guardar en la base de datos
            Topping toppingGuardado = toppingService.crear(nuevoTopping);

            System.out.println("Topping guardado con ID: " + toppingGuardado.getIdTopping());
            ra.addFlashAttribute("msg", "Producto '" + nombre + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar producto: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       Productos - Eliminar (ACTUALIZADO)
       ====================== */
    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, // Cambiado a Long
                                   RedirectAttributes ra) {
        System.out.println("Eliminando producto con ID: " + id);

        try {
            toppingService.delete(id);
            ra.addFlashAttribute("msg", "Producto eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    // Los demás métodos se mantienen igual...
    /* ======================
       Pedidos - Actualizar estado
       ====================== */
    @PostMapping("/pedidos")
    public String actualizarPedido(@RequestParam String pedido_id,
                                   @RequestParam String estado,
                                   RedirectAttributes ra) {
        // TODO: actualizar estado del pedido en servicio/BDD
        ra.addFlashAttribute("msg", "Pedido #" + pedido_id + " actualizado a: " + estado);
        return "redirect:/admin/pedidos";
    }

    /* ======================
       Historial
       ====================== */
    @PostMapping("/historial")
    public String accionesHistorial(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", "Acción de historial recibida.");
        return "redirect:/admin/historial";
    }

    /* ======================
       Administradores
       ====================== */
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