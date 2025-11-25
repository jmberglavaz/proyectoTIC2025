package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
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
        return "user/carrito";
    }

    @PostMapping("/carrito/actualizar")
    public String actualizarCantidad(@RequestParam String id,
                                     @RequestParam int cantidad) {
        carritoService.updateQuantity(id, cantidad);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarItem(@RequestParam String id) {
        carritoService.removeItem(id);
        return "redirect:/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito() {
        carritoService.clear();
        return "redirect:/carrito";
    }
}
