package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

@Controller
public class CheckoutController {

    @GetMapping("/checkout")
    public String view() {
        return "checkout";
    }

    @PostMapping("/checkout")
    public String submit(@RequestParam(required = false) String accion,
                         RedirectAttributes ra) {

        // Manejo simple de los 3 botones/acciones que dejaste en el checkout
        if ("agregar_direccion".equals(accion)) {
            ra.addFlashAttribute("msg", "Dirección agregada (demo).");
            return "redirect:/checkout";
        }
        if ("guardar_tarjeta_nueva".equals(accion)) {
            ra.addFlashAttribute("msg", "Tarjeta guardada (demo).");
            return "redirect:/checkout";
        }

        // Por defecto o si es "confirmar_pedido": generar un pedido de mentira e ir a Pedido
        String pedidoId = String.format("%06d", new Random().nextInt(1_000_000));
        ra.addFlashAttribute("pedidoId", pedidoId);
        ra.addFlashAttribute("msg", "¡Pedido confirmado! #" + pedidoId);

        // Si ya tenés /pedido (estático), redirigí ahí:
        return "redirect:/pedido";

        // Alternativa más prolija (si querés pedido por ID):
        // return "redirect:/pedido/" + pedidoId;
    }
}
