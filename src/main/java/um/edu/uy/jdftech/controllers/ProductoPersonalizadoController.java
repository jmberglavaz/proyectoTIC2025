package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.CarritoItem;
import um.edu.uy.jdftech.services.CarritoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class ProductoPersonalizadoController {

    private final CarritoService carritoService;

    public ProductoPersonalizadoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // ----------------- PIZZA -----------------

    @GetMapping("/crear-pizza")
    public String mostrarPizza(Model model) {
        model.addAttribute("page", "pizza");
        inicializarModeloPizza(model);
        return "user/crear-pizza";
    }

    @PostMapping("/crear-pizza")
    public String crearPizza(@RequestParam String tamanio,
                             @RequestParam("masa") String tipoMasa,
                             @RequestParam("salsa") String tipoSalsa,
                             @RequestParam("queso") String tipoQueso,
                             @RequestParam(value = "toppings", required = false) List<String> toppings,
                             @RequestParam(defaultValue = "1") int cantidad,
                             @RequestParam(required = false) String observaciones,
                             @RequestParam(value = "accion", required = false) String accion,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        List<String> toppingsList = toppings != null ? toppings : Collections.emptyList();

        double precioTamanio = precioTamanioPizza(tamanio);
        double precioMasa = precioMasaPizza(tipoMasa);
        double precioSalsa = precioSalsaPizza(tipoSalsa);
        double precioQueso = precioQuesoPizza(tipoQueso);

        double precioToppings = 0;
        String toppingsResumen;
        String toppingsLinea;

        if (toppingsList.isEmpty()) {
            toppingsResumen = "—";
            toppingsLinea = "Sin toppings extras · $ 0";
        } else {
            List<String> textos = new ArrayList<>();
            for (String t : toppingsList) {
                double p = precioToppingPizza(t);
                precioToppings += p;
                textos.add(nombreToppingPizza(t) + " ($ " + (int) p + ")");
            }
            toppingsResumen = String.join(", ", textos);
            if (toppingsList.size() == 1) {
                toppingsLinea = "1 topping extra · $ " + (int) precioToppings;
            } else {
                toppingsLinea = toppingsList.size() + " toppings extra · $ " + (int) precioToppings;
            }
        }

        double precioUnitario = precioTamanio + precioMasa + precioSalsa + precioQueso + precioToppings;
        int cantidadFinal = Math.max(1, cantidad);
        int totalFinal = (int) (precioUnitario * cantidadFinal);

        model.addAttribute("page", "pizza");
        model.addAttribute("tamanioSeleccionado", tamanio);
        model.addAttribute("masaSeleccionada", tipoMasa);
        model.addAttribute("salsaSeleccionada", tipoSalsa);
        model.addAttribute("quesoSeleccionado", tipoQueso);
        model.addAttribute("toppingsSeleccionados", toppingsList);
        model.addAttribute("cantidad", cantidadFinal);
        model.addAttribute("observaciones", observaciones);

        model.addAttribute("tamanioResumen",
                resumenOpcionPizza(nombreTamanioPizza(tamanio), precioTamanio));
        model.addAttribute("masaResumen",
                resumenOpcionPizza(nombreMasaPizza(tipoMasa), precioMasa));
        model.addAttribute("salsaResumen",
                resumenOpcionPizza(nombreSalsaPizza(tipoSalsa), precioSalsa));
        model.addAttribute("quesoResumen",
                resumenOpcionPizza(nombreQuesoPizza(tipoQueso), precioQueso));
        model.addAttribute("toppingsResumen", toppingsResumen);
        model.addAttribute("toppingsLinea", toppingsLinea);
        model.addAttribute("totalEstimado", "$ " + totalFinal);

        if ("agregar".equals(accion)) {
            String descripcion = construirDescripcionPizza(
                    tamanio, tipoMasa, tipoSalsa, tipoQueso, toppingsList, observaciones);
            carritoService.addItem(new CarritoItem(
                    "pizza",
                    "Pizza personalizada",
                    descripcion,
                    cantidadFinal,
                    precioUnitario
            ));
            redirectAttributes.addFlashAttribute("mensaje", "Tu pizza fue agregada al carrito");
            return "redirect:/carrito";
        }

        return "user/crear-pizza";
    }

    private void inicializarModeloPizza(Model model) {
        model.addAttribute("tamanioResumen", "—");
        model.addAttribute("masaResumen", "—");
        model.addAttribute("salsaResumen", "—");
        model.addAttribute("quesoResumen", "—");
        model.addAttribute("toppingsResumen", "—");
        model.addAttribute("toppingsLinea", "Sin toppings extras · $ 0");
        model.addAttribute("cantidad", 1);
        model.addAttribute("totalEstimado", "$ —");
    }

    private double precioTamanioPizza(String tamanio) {
        return switch (tamanio.toUpperCase()) {
            case "S" -> 320;
            case "L" -> 520;
            default -> 420; // M
        };
    }

    private double precioMasaPizza(String masa) {
        return switch (masa) {
            case "integral" -> 40;
            case "sin_gluten" -> 60;
            default -> 0; // napolitana
        };
    }

    private double precioSalsaPizza(String salsa) {
        return switch (salsa) {
            case "pomodoro" -> 35;
            case "otra" -> 45; // especial
            default -> 0; // tomate
        };
    }

    private double precioQuesoPizza(String queso) {
        return switch (queso) {
            case "roquefort" -> 70;
            case "otro" -> 40; // blend especial
            default -> 0; // muzza
        };
    }

    private double precioToppingPizza(String topping) {
        return switch (topping) {
            case "aceitunas" -> 30;
            case "albahaca" -> 25;
            case "morron" -> 25;
            case "cebolla" -> 20;
            case "champiniones" -> 35;
            case "jamon" -> 40;
            case "bacon" -> 40;
            case "anana" -> 30;
            default -> 0;
        };
    }

    private String nombreToppingPizza(String topping) {
        return switch (topping) {
            case "aceitunas" -> "Aceitunas";
            case "albahaca" -> "Albahaca";
            case "morron" -> "Morrón";
            case "cebolla" -> "Cebolla";
            case "champiniones" -> "Champiñones";
            case "jamon" -> "Jamón";
            case "bacon" -> "Bacon";
            case "anana" -> "Ananá";
            default -> topping;
        };
    }

    private String nombreTamanioPizza(String tamanio) {
        return switch (tamanio.toUpperCase()) {
            case "S" -> "Pequeña (S)";
            case "L" -> "Grande (L)";
            default -> "Mediana (M)";
        };
    }

    private String nombreMasaPizza(String masa) {
        return switch (masa) {
            case "integral" -> "Integral";
            case "sin_gluten" -> "Sin gluten";
            default -> "Napolitana";
        };
    }

    private String nombreSalsaPizza(String salsa) {
        return switch (salsa) {
            case "pomodoro" -> "Pomodoro";
            case "otra" -> "Especial";
            default -> "Tomate";
        };
    }

    private String nombreQuesoPizza(String queso) {
        return switch (queso) {
            case "roquefort" -> "Roquefort";
            case "otro" -> "Blend especial";
            default -> "Muzzarella";
        };
    }

    private String resumenOpcionPizza(String nombre, double precio) {
        if (precio <= 0) {
            return nombre + " · incluido";
        }
        return nombre + " · $ " + (int) precio;
    }

    private String construirDescripcionPizza(String tamanio, String masa, String salsa, String queso,
                                             List<String> toppings, String observaciones) {
        String toppingsTexto = toppings.isEmpty()
                ? "Sin toppings extras"
                : String.join(", ", toppings);
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Tamaño: ").append(nombreTamanioPizza(tamanio))
                .append(" • Masa: ").append(nombreMasaPizza(masa))
                .append(" • Salsa: ").append(nombreSalsaPizza(salsa))
                .append(" • Queso: ").append(nombreQuesoPizza(queso))
                .append(" • Toppings: ").append(toppingsTexto);
        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }

    // ----------------- BURGER -----------------

    @GetMapping("/crear-burger")
    public String mostrarBurger(Model model) {
        model.addAttribute("page", "burger");
        inicializarModeloBurger(model);
        return "user/crear-burger";
    }

    @PostMapping("/crear-burger")
    public String crearBurger(@RequestParam("cantidad_carnes") int cantidadCarnes,
                              @RequestParam("tipo_carne") String tipoCarne,
                              @RequestParam("tipo_pan") String tipoPan,
                              @RequestParam(value = "aderezos", required = false) List<String> aderezos,
                              @RequestParam(value = "extras", required = false) List<String> extras,
                              @RequestParam(defaultValue = "1") int cantidad,
                              @RequestParam(required = false) String observaciones,
                              @RequestParam(value = "accion", required = false) String accion,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        List<String> aderezosList = aderezos != null ? aderezos : Collections.emptyList();
        List<String> extrasList = extras != null ? extras : Collections.emptyList();

        double precioCarnes = precioCantidadCarnes(cantidadCarnes);
        double precioTipoCarne = precioTipoCarne(tipoCarne);
        double precioPan = precioTipoPan(tipoPan);

        // Aderezos sin costo
        String aderezosResumen;
        String aderezosLinea;
        int cantAderezos = aderezosList.size();
        if (cantAderezos == 0) {
            aderezosResumen = "—";
            aderezosLinea = "Sin aderezos · $ 0";
        } else {
            aderezosResumen = String.join(", ", aderezosList);
            if (cantAderezos == 1) {
                aderezosLinea = "1 aderezo · $ 0";
            } else {
                aderezosLinea = cantAderezos + " aderezos · $ 0";
            }
        }

        double totalExtras = 0;
        String extrasResumen;
        String extrasLinea;
        if (extrasList.isEmpty()) {
            extrasResumen = "—";
            extrasLinea = "Sin extras · $ 0";
        } else {
            List<String> textos = new ArrayList<>();
            for (String ex : extrasList) {
                double p = precioExtraBurger(ex);
                totalExtras += p;
                textos.add(nombreExtraBurger(ex) + " ($ " + (int) p + ")");
            }
            extrasResumen = String.join(", ", textos);
            if (extrasList.size() == 1) {
                extrasLinea = "1 extra · $ " + (int) totalExtras;
            } else {
                extrasLinea = extrasList.size() + " extras · $ " + (int) totalExtras;
            }
        }

        double precioUnitario = precioCarnes + precioTipoCarne + precioPan + totalExtras;
        int cantidadFinal = Math.max(1, cantidad);
        int totalFinal = (int) (precioUnitario * cantidadFinal);

        model.addAttribute("page", "burger");
        model.addAttribute("cantidad_carnes", cantidadCarnes);
        model.addAttribute("tipo_carne", tipoCarne);
        model.addAttribute("tipo_pan", tipoPan);
        model.addAttribute("aderezosSeleccionados", aderezosList);
        model.addAttribute("extrasSeleccionados", extrasList);
        model.addAttribute("cantidad", cantidadFinal);
        model.addAttribute("observaciones", observaciones);

        model.addAttribute("cantCarnesResumen",
                cantidadCarnes + " carnes · $ " + (int) precioCarnes);
        model.addAttribute("tipoCarneResumen",
                nombreTipoCarne(tipoCarne) + " · " + (precioTipoCarne <= 0 ? "incluido" : "$ " + (int) precioTipoCarne));
        model.addAttribute("tipoPanResumen",
                nombreTipoPan(tipoPan) + " · " + (precioPan <= 0 ? "incluido" : "$ " + (int) precioPan));
        model.addAttribute("aderezosResumen", aderezosResumen);
        model.addAttribute("extrasResumen", extrasResumen);
        model.addAttribute("aderezosLinea", aderezosLinea);
        model.addAttribute("extrasLinea", extrasLinea);
        model.addAttribute("totalEstimado", "$ " + totalFinal);

        if ("agregar".equals(accion)) {
            String descripcion = construirDescripcionHamburguesa(
                    cantidadCarnes, tipoCarne, tipoPan, aderezosList, extrasList, observaciones);
            carritoService.addItem(new CarritoItem(
                    "burger",
                    "Hamburguesa personalizada",
                    descripcion,
                    cantidadFinal,
                    precioUnitario
            ));
            redirectAttributes.addFlashAttribute("mensaje", "Tu hamburguesa fue agregada al carrito");
            return "redirect:/carrito";
        }

        return "user/crear-burger";
    }

    private void inicializarModeloBurger(Model model) {
        model.addAttribute("cantCarnesResumen", "—");
        model.addAttribute("tipoCarneResumen", "—");
        model.addAttribute("tipoPanResumen", "—");
        model.addAttribute("aderezosResumen", "—");
        model.addAttribute("extrasResumen", "—");
        model.addAttribute("aderezosLinea", "Sin aderezos · $ 0");
        model.addAttribute("extrasLinea", "Sin extras · $ 0");
        model.addAttribute("cantidad", 1);
        model.addAttribute("totalEstimado", "$ —");
    }

    private double precioCantidadCarnes(int cantidadCarnes) {
        return switch (cantidadCarnes) {
            case 1 -> 310;
            case 3 -> 520;
            default -> 420; // 2 carnes
        };
    }

    private double precioTipoCarne(String tipo) {
        return switch (tipo) {
            case "pollo" -> 25;
            case "cerdo" -> 30;
            case "salmon" -> 45;
            case "lentejas" -> 15;
            case "soja" -> 18;
            default -> 0; // vaca
        };
    }

    private double precioTipoPan(String pan) {
        return switch (pan) {
            case "integral" -> 25;
            case "sin_gluten" -> 45;
            default -> 0; // pan de papa
        };
    }

    private String nombreTipoCarne(String tipo) {
        return switch (tipo) {
            case "pollo" -> "Pollo";
            case "cerdo" -> "Cerdo";
            case "salmon" -> "Salmón";
            case "lentejas" -> "Lentejas";
            case "soja" -> "Soja";
            default -> "Vaca";
        };
    }

    private String nombreTipoPan(String pan) {
        return switch (pan) {
            case "integral" -> "Integral";
            case "sin_gluten" -> "Sin gluten";
            default -> "Pan de papa";
        };
    }

    private double precioExtraBurger(String extra) {
        return switch (extra) {
            case "extra_queso" -> 35;
            case "bacon" -> 45;
            case "huevo" -> 30;
            case "queso_azul" -> 40;
            case "cebolla" -> 20;
            case "tomate" -> 20;
            default -> 0;
        };
    }

    private String nombreExtraBurger(String extra) {
        return switch (extra) {
            case "extra_queso" -> "Queso extra";
            case "bacon" -> "Bacon";
            case "huevo" -> "Huevo";
            case "queso_azul" -> "Queso azul";
            case "cebolla" -> "Cebolla";
            case "tomate" -> "Tomate";
            default -> extra;
        };
    }

    private String construirDescripcionHamburguesa(int cantidadCarnes, String tipoCarne, String tipoPan,
                                                   List<String> aderezos, List<String> extras,
                                                   String observaciones) {
        String aderezosTexto = aderezos.isEmpty() ? "Sin aderezos" : String.join(", ", aderezos);
        String extrasTexto = extras.isEmpty() ? "Sin extras" : String.join(", ", extras);
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(cantidadCarnes).append(" carnes de ").append(nombreTipoCarne(tipoCarne))
                .append(" • Pan: ").append(nombreTipoPan(tipoPan))
                .append(" • Aderezos: ").append(aderezosTexto)
                .append(" • Extras: ").append(extrasTexto);
        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }
}
