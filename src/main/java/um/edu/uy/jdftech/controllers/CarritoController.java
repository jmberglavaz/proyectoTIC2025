package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
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

    private Cliente obtenerClienteActual(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            throw new RuntimeException("Debe iniciar sesión para acceder al carrito");
        }
        return cliente;
    }

    // GET: Ver carrito
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        Cliente cliente = obtenerClienteActual(session);
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
                                     @RequestParam Integer cantidad,
                                     HttpSession session) {
        Cliente cliente = obtenerClienteActual(session);
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
    public String eliminarItem(@RequestParam Long itemId, HttpSession session) {
        Cliente cliente = obtenerClienteActual(session);
        carritoService.eliminarItem(itemId);
        return "redirect:/carrito";
    }

    // POST: Vaciar carrito completo
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session) {
        Cliente cliente = obtenerClienteActual(session);
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);
        carritoService.vaciarCarrito(carrito);
        return "redirect:/carrito";
    }

    // GET: Ir al checkout
    @GetMapping("/checkout")
    public String irAlCheckout(HttpSession session) {
        Cliente cliente = obtenerClienteActual(session);
        Carrito carrito = carritoService.obtenerOCrearCarrito(cliente);
        
        if (carrito.getItems().isEmpty()) {
            return "redirect:/carrito?error=carrito-vacio";
        }
        
        return "redirect:/checkout";
    }
}