package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.CarritoItem;
import um.edu.uy.jdftech.services.CarritoService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductoPersonalizadoController {

    private final CarritoService carritoService;

    public ProductoPersonalizadoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/crear-pizza")
    public String mostrarPizza(Model model) {
        model.addAttribute("page", "pizza");
        return "crear-pizza";
    }

    @PostMapping("/crear-pizza")
    public String crearPizza(@RequestParam String tamanio,
                             @RequestParam("masa") String tipoMasa,
                             @RequestParam("salsa") String tipoSalsa,
                             @RequestParam("queso") String tipoQueso,
                             @RequestParam(value = "toppings", required = false) List<String> toppings,
                             @RequestParam(defaultValue = "1") int cantidad,
                             @RequestParam(required = false) String observaciones,
                             RedirectAttributes redirectAttributes) {

        List<String> toppingsList = toppings != null ? toppings : Collections.emptyList();
        double precioUnitario = calcularPrecioPizza(tamanio, toppingsList.size());
        String descripcion = construirDescripcionPizza(tamanio, tipoMasa, tipoSalsa, tipoQueso, toppingsList, observaciones);
        carritoService.addItem(new CarritoItem("pizza", "Pizza personalizada", descripcion, cantidad, precioUnitario));

        redirectAttributes.addFlashAttribute("mensaje", "Tu pizza fue agregada al carrito");
        return "redirect:/carrito";
    }

    @GetMapping("/crear-burger")
    public String mostrarBurger(Model model) {
        model.addAttribute("page", "burger");
        return "crear-burger";
    }

    @PostMapping("/crear-burger")
    public String crearBurger(@RequestParam("cantidad_carnes") int cantidadCarnes,
                              @RequestParam("tipo_carne") String tipoCarne,
                              @RequestParam("tipo_pan") String tipoPan,
                              @RequestParam(value = "aderezos", required = false) List<String> aderezos,
                              @RequestParam(value = "extras", required = false) List<String> extras,
                              @RequestParam(defaultValue = "1") int cantidad,
                              @RequestParam(required = false) String observaciones,
                              RedirectAttributes redirectAttributes) {

        List<String> aderezosList = aderezos != null ? aderezos : Collections.emptyList();
        List<String> extrasList = extras != null ? extras : Collections.emptyList();
        double precioUnitario = calcularPrecioHamburguesa(cantidadCarnes, extrasList.size(), aderezosList.size());
        String descripcion = construirDescripcionHamburguesa(cantidadCarnes, tipoCarne, tipoPan, aderezosList, extrasList, observaciones);

        carritoService.addItem(new CarritoItem("burger", "Hamburguesa personalizada", descripcion, cantidad, precioUnitario));

        redirectAttributes.addFlashAttribute("mensaje", "Tu hamburguesa fue agregada al carrito");
        return "redirect:/carrito";
    }

    private double calcularPrecioPizza(String tamanio, int cantidadToppings) {
        double base;
        switch (tamanio.toUpperCase()) {
            case "S" -> base = 320;
            case "L" -> base = 620;
            default -> base = 470;
        }
        double toppingsExtra = cantidadToppings * 45;
        return base + toppingsExtra;
    }

    private double calcularPrecioHamburguesa(int cantidadCarnes, int cantidadExtras, int cantidadAderezos) {
        double base = switch (cantidadCarnes) {
            case 1 -> 340;
            case 3 -> 520;
            default -> 430; // 2 carnes
        };
        double extras = cantidadExtras * 55;
        double aderezos = Math.max(0, cantidadAderezos - 1) * 20; // el primero sin costo
        return base + extras + aderezos;
    }

    private String construirDescripcionPizza(String tamanio, String masa, String salsa, String queso,
                                             List<String> toppings, String observaciones) {
        String toppingsTexto = toppings.isEmpty()
                ? "Sin toppings extras"
                : toppings.stream().collect(Collectors.joining(", "));
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Tamaño: ").append(tamanio)
                .append(" • Masa: ").append(masa)
                .append(" • Salsa: ").append(salsa)
                .append(" • Queso: ").append(queso)
                .append(" • Toppings: ").append(toppingsTexto);
        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }

    private String construirDescripcionHamburguesa(int cantidadCarnes, String tipoCarne, String tipoPan,
                                                   List<String> aderezos, List<String> extras,
                                                   String observaciones) {
        String aderezosTexto = aderezos.isEmpty() ? "Sin aderezos" : String.join(", ", aderezos);
        String extrasTexto = extras.isEmpty() ? "Sin extras" : String.join(", ", extras);
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(cantidadCarnes).append(" carnes de ").append(tipoCarne)
                .append(" • Pan: ").append(tipoPan)
                .append(" • Aderezos: ").append(aderezosTexto)
                .append(" • Extras: ").append(extrasTexto);
        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }
}
