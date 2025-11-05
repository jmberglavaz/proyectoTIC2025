package um.edu.uy.jdftech.controllers;

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

    // Método auxiliar para obtener cliente logueado (HARDCODEADO por ahora)
    private Cliente obtenerClienteActual() {
        // TODO: Reemplazar con sesión real cuando login esté listo
        return clienteRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // GET: Ir al pedido activo del cliente (o home si no tiene)
    @GetMapping
    public String irAlPedidoActivo(RedirectAttributes ra) {
        Cliente cliente = obtenerClienteActual();
        Optional<Pedido> pedidoActivo = pedidoService.obtenerPedidoActivo(cliente);
        
        if (pedidoActivo.isEmpty()) {
            ra.addFlashAttribute("msg", "No tenés pedidos activos");
            return "redirect:/";
        }
        
        return "redirect:/pedido/" + pedidoActivo.get().getId();
    }

    // GET: Ver un pedido específico por ID
    @GetMapping("/{pedidoId}")
    public String verPedido(@PathVariable Long pedidoId, Model model) {
        Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);
        
        // Verificar que el pedido pertenezca al cliente actual
        Cliente cliente = obtenerClienteActual();
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
        
        return "pedido";
    }

    // POST: Cancelar pedido (lo borra de la BD)
    @PostMapping("/{pedidoId}/cancelar")
    public String cancelarPedido(@PathVariable Long pedidoId, RedirectAttributes ra) {
        try {
            Pedido pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            
            // Verificar que el pedido pertenezca al cliente actual
            Cliente cliente = obtenerClienteActual();
            if (!pedido.getClient().getId().equals(cliente.getId())) {
                ra.addFlashAttribute("error", "No autorizado");
                return "redirect:/";
            }

            pedidoService.cancelarPedido(pedidoId);
            ra.addFlashAttribute("msg", "Pedido cancelado exitosamente");
            return "redirect:/"; // Va al home después de cancelar
            
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedido/" + pedidoId;
        }
    }

    // POST: Cambiar estado del pedido (solo para testing/admin)
    @PostMapping("/{pedidoId}/cambiar-estado")
    public String cambiarEstado(@PathVariable Long pedidoId, 
                                @RequestParam EstadoPedido nuevoEstado,
                                RedirectAttributes ra) {
        try {
            pedidoService.cambiarEstado(pedidoId, nuevoEstado);
            ra.addFlashAttribute("msg", "Estado actualizado");
            return "redirect:/pedido/" + pedidoId;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedido/" + pedidoId;
        }
    }
}