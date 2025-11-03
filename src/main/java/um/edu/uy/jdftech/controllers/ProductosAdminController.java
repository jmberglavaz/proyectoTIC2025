package um.edu.uy.jdftech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.services.ToppingService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/productos")
public class ProductosAdminController {

    @Autowired
    private ToppingService toppingService;

    @PostMapping("/crear")
    public String crearTopping(
            @RequestParam String nombre,
            @RequestParam char hamburguesaOPizza,
            @RequestParam char tipo,
            @RequestParam(required = false) Double precio) {

        // Si no se proporciona precio, usar 0
        double precioFinal = (precio != null) ? precio : 0.0;

        Topping nuevoTopping = new Topping(
                nombre,
                hamburguesaOPizza,
                tipo,
                precioFinal,
                LocalDateTime.now()
        );

        toppingService.crear(nuevoTopping);
        return "redirect:/admin/productos?exito=true";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarTopping(@PathVariable Long id) {
        toppingService.delete(id);
        return "redirect:/admin/productos?eliminado=true";
    }
}
