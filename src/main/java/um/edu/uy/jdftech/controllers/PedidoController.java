package um.edu.uy.jdftech.controllers;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Pedido;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.services.PedidoService;

import java.util.Optional;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    // Método auxiliar para obtener cliente logueado
    private Cliente obtenerClienteActual(HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            throw new RuntimeException("Debe iniciar sesión para ver pedidos");
        }
        return cliente;
    }

    // GET: Ir al pedido activo del cliente (o home si no tiene)
    @GetMapping
    public String irAlPedidoActivo(HttpSession session, RedirectAttributes ra) {
        Cliente cliente = obtenerClienteActual(session);
        Optional<Pedido> pedidoActivo = pedidoService.obtenerPedidoActivo(cliente);
        
        if (pedidoActivo.isEmpty()) {
            ra.addFlashAttribute("msg", "No tenés pedidos activos");
            return "redirect:/";
        }
        
        return "redirect:/pedido/" + pedidoActivo.get().getId();
    }

    // GET: Ver un pedido específico por ID
    @GetMapping("/{pedidoId}")
    public String verPedido(@PathVariable Long pedidoId,
                            HttpSession session,
                            Model model) {
        Pedido pedido = pedidoService.obtenerPedidoConDetalles(pedidoId);

        // Verificar que el pedido pertenezca al cliente actual
        Cliente cliente = obtenerClienteActual(session);
        if (!pedido.getClient().getId().equals(cliente.getId())) {
            return "redirect:/?error=no-autorizado";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("cliente", cliente);
        model.addAttribute("page", "pedido");

        // Estados para el timeline
        model.addAttribute("enCola", pedido.getStatus() == EstadoPedido.EN_COLA);
        model.addAttribute("enPreparacion", pedido.getStatus() == EstadoPedido.EN_PREPARACION);
        model.addAttribute("enCamino", pedido.getStatus() == EstadoPedido.EN_CAMINO);
        model.addAttribute("entregado", pedido.getStatus() == EstadoPedido.ENTREGADO);

        return "user/pedido";
    }

    @PostMapping("/{pedidoId}/cancelar")
    public String cancelarPedido(@PathVariable Long pedidoId,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        try {
            System.out.println("=== INTENTANDO CANCELAR PEDIDO " + pedidoId + " ===");

            Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            System.out.println("Estado actual del pedido: " + pedido.getStatus());
            System.out.println("¿Está en cola? " + (pedido.getStatus() == EstadoPedido.EN_COLA));

            Cliente cliente = obtenerClienteActual(session);
            if (!pedido.getClient().getId().equals(cliente.getId())) {
                ra.addFlashAttribute("error", "No autorizado");
                return "redirect:/";
            }

            pedidoService.cancelarPedido(pedidoId);

            // Verificar que se canceló
            Pedido pedidoCancelado = pedidoService.obtenerPedidoPorId(pedidoId);
            System.out.println("Estado después de cancelar: " + pedidoCancelado.getStatus());

            ra.addFlashAttribute("msg", "Pedido cancelado exitosamente");
            return "redirect:/pedido/" + pedidoId;
        } catch (RuntimeException e) {
            System.out.println("ERROR al cancelar: " + e.getMessage());
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedido/" + pedidoId;
        }
    }


    // POST: Cambiar estado del pedido (solo para testing/admin)
    @PostMapping("/{pedidoId}/cambiar-estado")
    public String cambiarEstado(@PathVariable Long pedidoId,
                                @RequestParam EstadoPedido nuevoEstado,
                                HttpSession session,
                                RedirectAttributes ra) {
        Cliente cliente = obtenerClienteActual(session);
        try {
            pedidoService.cambiarEstado(pedidoId, nuevoEstado);
            ra.addFlashAttribute("msg", "Estado actualizado");
            return "redirect:/pedido/" + pedidoId;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedido/" + pedidoId;
        }
    }

    @PostMapping("/{pedidoId}/actualizar")
    public String actualizarPedido(@PathVariable Long pedidoId, HttpSession session) {
        // Esto fuerza una recarga desde la BD con todas las relaciones
        return "redirect:/pedido/" + pedidoId;
    }
}
