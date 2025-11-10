package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FavoritoController {

    // Lista temporal en memoria (solo para mostrar algo)
    private List<Map<String, String>> favoritos = new ArrayList<>();

    @GetMapping("/favoritos")
    public String verFavoritos(Model model) {
        model.addAttribute("page", "favoritos");
        model.addAttribute("favoritos", favoritos);
        return "favoritos";
    }

    @PostMapping("/favoritos/agregar")
    public String agregarAFavoritos(@RequestParam String tipo) {
        // Por ahora solo agregamos un ejemplo genérico
        favoritos.add(Map.of(
                "nombre", tipo.equals("pizza") ? "Pizza personalizada" : "Hamburguesa personalizada",
                "tipo", tipo,
                "detalle", "Ejemplo de detalle (vista previa estética)"
        ));
        return "redirect:/favoritos";
    }
}