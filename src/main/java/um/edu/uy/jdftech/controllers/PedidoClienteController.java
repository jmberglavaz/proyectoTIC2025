package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.services.PedidoService;

import java.util.List;

@Controller
@RequestMapping("/mis-pedidos")
@RequiredArgsConstructor
public class PedidoClienteController {

    private final PedidoService pedidoService;

    @GetMapping
    public String misPedidos(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/login";
        }

        // Obtener los últimos 10 pedidos del cliente ordenados por fecha DESC
        List<Pedido> pedidos = pedidoService.findUltimosPedidosByCliente(cliente.getId(), 10);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);

        return "user/mis-pedidos";
    }
}