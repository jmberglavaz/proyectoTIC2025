package um.edu.uy.jdftech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.entitites.Carrito;
import um.edu.uy.jdftech.entitites.CarritoItem;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.services.CarritoService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ClienteRepository clienteRepository;

    // Método auxiliar para obtener cliente logueado (HARDCODEADO por ahora)
    private Cliente obtenerClienteActual() {
        // TODO: Reemplazar con sesión real cuando login esté listo
        return clienteRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // GET: Ver carrito
    @GetMapping
    public String verCarrito(Model model) {
        Cliente cliente = obtenerClienteActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);
        
        BigDecimal total = carritoService.calcularTotal(carrito);
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("items", carrito.getItems());
        model.addAttribute("total", total);
        model.addAttribute("carritoVacio", carrito.getItems().isEmpty());
        model.addAttribute("page", "carrito");
        
        return "user/carrito";
    }

    // POST: Actualizar cantidad de un item
    @PostMapping("/actualizar-cantidad")
    public String actualizarCantidad(@RequestParam Long itemId, 
                                     @RequestParam Integer cantidad) {
        try {
            carritoService.actualizarCantidad(itemId, cantidad);
        } catch (Exception e) {
            // TODO: Agregar mensaje de error
            return "redirect:/carrito?error=" + e.getMessage();
        }
        return "redirect:/carrito";
    }

    // POST: Eliminar item del carrito
    @PostMapping("/eliminar")
    public String eliminarItem(@RequestParam Long itemId) {
        carritoService.eliminarItem(itemId);
        return "redirect:/carrito";
    }

    // POST: Vaciar carrito completo
    @PostMapping("/vaciar")
    public String vaciarCarrito() {
        Cliente cliente = obtenerClienteActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);
        carritoService.vaciarCarrito(carrito);
        return "redirect:/carrito";
    }

    // GET: Ir al checkout
    @GetMapping("/checkout")
    public String irAlCheckout() {
        Cliente cliente = obtenerClienteActual();
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);
        
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito?error=carrito-vacio";
        }
        
        return "redirect:/checkout";
    }
}