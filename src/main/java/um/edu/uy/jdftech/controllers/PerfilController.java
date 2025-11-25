package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.services.ClienteService;
import um.edu.uy.jdftech.services.DireccionService;

import java.util.List;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final ClienteService clienteService;
    private final DireccionService direccionService;

    @GetMapping
    public String perfil(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/login";
        }

        // Obtener cliente actualizado con direcciones
        Cliente clienteActualizado = clienteService.findById(cliente.getId());
        List<Direccion> direcciones = direccionService.findByCliente(cliente.getId());

        model.addAttribute("cliente", clienteActualizado);
        model.addAttribute("direcciones", direcciones);
        model.addAttribute("nuevaDireccion", new Direccion());

        return "user/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String nuevaPassword,
                                  HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            Cliente clienteActual = clienteService.findById(cliente.getId());
            clienteActual.setPassword(nuevaPassword);
            clienteService.update(cliente.getId(), clienteActual);
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones")
    public String agregarDireccion(@ModelAttribute Direccion nuevaDireccion,
                                   HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            direccionService.save(nuevaDireccion, cliente.getId());
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones/{id}/default")
    public String setDireccionDefault(@PathVariable Long id,
                                      HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            direccionService.setDefault(id, cliente.getId());
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones/{id}/delete")
    public String deleteDireccion(@PathVariable Long id,
                                  HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            direccionService.delete(id, cliente.getId());
        }
        return "redirect:/perfil";
    }
}