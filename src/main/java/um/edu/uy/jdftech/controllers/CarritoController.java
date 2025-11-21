package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.services.CarritoService;

@Controller
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        model.addAttribute("page", "carrito");
        model.addAttribute("items", carritoService.getItems());
        double subtotal = carritoService.getSubtotal();
        double costoEnvio = subtotal > 0 ? 120 : 0;
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", costoEnvio);
        model.addAttribute("total", subtotal + costoEnvio);
        return "carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam String id,
                                     @RequestParam int cantidad,
                                     RedirectAttributes redirectAttributes) {
        carritoService.updateQuantity(id, cantidad);
        redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarItem(@RequestParam String id, RedirectAttributes redirectAttributes) {
        carritoService.removeItem(id);
        redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado del carrito");
        return "redirect:/carrito";
    }
}
