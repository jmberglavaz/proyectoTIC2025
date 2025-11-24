package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.CarritoItem;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.services.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ProductoPersonalizadoController {

    private final CarritoService carritoService;
    private final ToppingService toppingService;
    private final AderezoService aderezoService;
    private final PizzaService pizzaService;
    private final HamburguesaService hamburguesaService;

    public ProductoPersonalizadoController(CarritoService carritoService,
                                           ToppingService toppingService,
                                           AderezoService aderezoService,
                                           PizzaService pizzaService,
                                           HamburguesaService hamburguesaService) {
        this.carritoService = carritoService;
        this.toppingService = toppingService;
        this.aderezoService = aderezoService;
        this.pizzaService = pizzaService;
        this.hamburguesaService = hamburguesaService;
    }

    @GetMapping("/crear-pizza")
    public String mostrarPizza(Model model, HttpSession session,
                               @RequestParam Map<String, String> allParams) {
        // VERIFICAR SI ESTÁ LOGUEADO
        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }

        // Datos dinámicos desde BD
        model.addAttribute("masas", toppingService.verToppingsDeTipoYProducto('M', 'P'));
        model.addAttribute("salsas", toppingService.verToppingsDeTipoYProducto('S', 'P'));
        model.addAttribute("quesos", toppingService.verToppingsDeTipoYProducto('Q', 'P'));
        model.addAttribute("toppingsExtras", toppingService.verToppingsDeTipoYProducto('X', 'P'));

        Map<Long, Integer> toppingsCantidad = new HashMap<>();
        String toppingsResumen = "Sin toppings extras · $ 0";
        double totalToppings = 0;

        for (String key : allParams.keySet()) {
            if (key.startsWith("toppingsCantidad[")) {
                Long toppingId = Long.parseLong(key.substring("toppingsCantidad[".length(), key.length() - 1));
                int cantidad = Integer.parseInt(allParams.get(key));
                toppingsCantidad.put(toppingId, cantidad);

                // Calcular precio si hay cantidad
                if (cantidad > 0) {
                    Topping topping = toppingService.findByIdTopping(toppingId);
                    totalToppings += topping.getPrecioTopping() * cantidad;
                }
            }
        }

        if (totalToppings > 0) {
            toppingsResumen = "Toppings extras · $ " + String.format("%.2f", totalToppings);
        }

        model.addAttribute("toppingsCantidad", toppingsCantidad);
        model.addAttribute("toppingsSeleccionadosResumen", toppingsResumen);
        model.addAttribute("page", "pizza");
        model.addAttribute("cantidad", 1);

        return "user/crear-pizza";
    }

    @PostMapping("/crear-pizza")
    public String crearPizza(@RequestParam String tamanio,
                             @RequestParam("masa") Long masaId,
                             @RequestParam("salsa") Long salsaId,
                             @RequestParam("queso") Long quesoId,
                             @RequestParam Map<String, String> allParams,
                             @RequestParam(defaultValue = "1") int cantidad,
                             @RequestParam(required = false) String observaciones,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }

        Topping masa = toppingService.findByIdTopping(masaId);
        Topping salsa = toppingService.findByIdTopping(salsaId);
        Topping queso = toppingService.findByIdTopping(quesoId);

        // Obtener toppings con cantidades para la pizza
        Map<Long, Integer> toppingsConCantidad = new HashMap<>();

        for (String key : allParams.keySet()) {
            if (key.startsWith("toppingsCantidad[")) {
                Long toppingId = Long.parseLong(key.substring("toppingsCantidad[".length(), key.length() - 1));
                int cantidadTopping = Integer.parseInt(allParams.get(key));

                if (cantidadTopping > 0) {
                    toppingsConCantidad.put(toppingId, cantidadTopping);
                }
            }
        }

        // Calcular precio total
        double precioBase = switch (tamanio.toUpperCase()) {
            case "S" -> 320;
            case "L" -> 520;
            default -> 420; // M
        };

        double precioComponentes = masa.getPrecioTopping() + salsa.getPrecioTopping() + queso.getPrecioTopping();

        // Calcular precio de toppings extras
        double precioToppingsExtras = 0;
        for (Map.Entry<Long, Integer> entry : toppingsConCantidad.entrySet()) {
            Topping topping = toppingService.findByIdTopping(entry.getKey());
            precioToppingsExtras += topping.getPrecioTopping() * entry.getValue();
        }

        double precioUnitario = precioBase + precioComponentes + precioToppingsExtras;

        String descripcion = construirDescripcionPizza(tamanio, masa, salsa, queso, toppingsConCantidad, observaciones);

        // GUARDAR EN BD usando PizzaService
        // Primero crear la pizza base
        Pizza pizza = new Pizza();
        pizza.setTamanio(tamanio);

        // Agregar toppings base (masa, salsa, queso) como toppings normales
        pizza.agregarTopping(masa, 1);
        pizza.agregarTopping(salsa, 1);
        pizza.agregarTopping(queso, 1);

        // Agregar toppings extras con sus cantidades
        for (Map.Entry<Long, Integer> entry : toppingsConCantidad.entrySet()) {
            Topping topping = toppingService.findByIdTopping(entry.getKey());
            pizza.agregarTopping(topping, entry.getValue());
        }

        // Guardar la pizza en BD
        Pizza pizzaGuardada = pizzaService.crearPizzaConToppings(toppingsConCantidad);

        carritoService.addItem(new CarritoItem("pizza", "Pizza personalizada", descripcion, cantidad, precioUnitario));

        redirectAttributes.addFlashAttribute("mensaje", "Tu pizza fue agregada al carrito y guardada en BD");
        return "redirect:/carrito";
    }

    private String construirDescripcionPizza(String tamanio, Topping masa, Topping salsa, Topping queso,
                                             Map<Long, Integer> toppingsConCantidad, String observaciones) {
        // Toppings base
        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Tamaño: ").append(tamanio)
                .append(" • Masa: ").append(masa.getNombre())
                .append(" • Salsa: ").append(salsa.getNombre())
                .append(" • Queso: ").append(queso.getNombre());

        // Toppings extras
        if (!toppingsConCantidad.isEmpty()) {
            descripcion.append(" • Toppings extras: ");
            List<String> toppingsDesc = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : toppingsConCantidad.entrySet()) {
                Topping topping = toppingService.findByIdTopping(entry.getKey());
                String toppingDesc = entry.getValue() > 1 ?
                        topping.getNombre() + " (x" + entry.getValue() + ")" : topping.getNombre();
                toppingsDesc.add(toppingDesc);
            }
            descripcion.append(String.join(", ", toppingsDesc));
        } else {
            descripcion.append(" • Toppings extras: Sin toppings extras");
        }

        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }

    @GetMapping("/crear-burger")
    public String mostrarBurger(Model model, HttpSession session) {
        // VERIFICAR SI ESTÁ LOGUEADO
        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }

        // Pasar info del cliente
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        model.addAttribute("clienteNombre", cliente.getFirstName());

        // DATOS DINÁMICOS DESDE BD
        model.addAttribute("carnes", toppingService.verToppingsDeTipoYProducto('C', 'H')); // C = carne, H = hamburguesa
        model.addAttribute("panes", toppingService.verToppingsDeTipoYProducto('P', 'H')); // P = pan
        model.addAttribute("aderezos", aderezoService.findAll()); // Todos los aderezos
        model.addAttribute("extras", toppingService.verToppingsDeTipoYProducto('X', 'H')); // X = extras

        model.addAttribute("page", "burger");
        model.addAttribute("cantidad", 1);

        return "user/crear-burger";
    }

    @PostMapping("/crear-burger")
    public String crearBurger(@RequestParam("cantidad_carnes") int cantidadCarnes,
                              @RequestParam("tipo_carne") Long carneId,
                              @RequestParam("tipo_pan") Long panId,
                              @RequestParam Map<String, String> allParams,
                              @RequestParam(defaultValue = "1") int cantidad,
                              @RequestParam(required = false) String observaciones,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }

        Topping carne = toppingService.findByIdTopping(carneId);
        Topping pan = toppingService.findByIdTopping(panId);

        Map<Long, Integer> aderezosConCantidad = new HashMap<>();
        Map<Long, Integer> extrasConCantidad = new HashMap<>();

        for (String key : allParams.keySet()) {
            if (key.startsWith("aderezosCantidad[")) {
                Long aderezoId = Long.parseLong(key.substring("aderezosCantidad[".length(), key.length() - 1));
                int cantidadAderezo = Integer.parseInt(allParams.get(key));
                if (cantidadAderezo > 0) {
                    aderezosConCantidad.put(aderezoId, cantidadAderezo);
                }
            }

            if (key.startsWith("extrasCantidad[")) {
                Long extraId = Long.parseLong(key.substring("extrasCantidad[".length(), key.length() - 1));
                int cantidadExtra = Integer.parseInt(allParams.get(key));
                if (cantidadExtra > 0) {
                    extrasConCantidad.put(extraId, cantidadExtra);
                }
            }
        }

        // Calcular precio y descripción (igual patrón que pizza)
        double precioUnitario = calcularPrecioHamburguesa(cantidadCarnes, carne, pan, extrasConCantidad, aderezosConCantidad);
        String descripcion = construirDescripcionHamburguesa(cantidadCarnes, carne, pan, aderezosConCantidad, extrasConCantidad, observaciones);

        carritoService.addItem(new CarritoItem("burger", "Hamburguesa personalizada", descripcion, cantidad, precioUnitario));

        redirectAttributes.addFlashAttribute("mensaje", "Tu hamburguesa fue agregada al carrito");
        return "redirect:/carrito";
    }

    // Calcular precio de hamburguesa
    private double calcularPrecioHamburguesa(int cantidadCarnes, Topping carne, Topping pan,
                                             Map<Long, Integer> extrasConCantidad,
                                             Map<Long, Integer> aderezosConCantidad) {
        // Precio base por cantidad de carnes
        double base = switch (cantidadCarnes) {
            case 1 -> 340;
            case 3 -> 520;
            default -> 430; // 2 carnes
        };

        // Precio de componentes base
        double precioCarne = carne.getPrecioTopping();
        double precioPan = pan.getPrecioTopping();

        // Precio de extras
        double precioExtras = 0;
        for (Map.Entry<Long, Integer> entry : extrasConCantidad.entrySet()) {
            Topping extra = toppingService.findByIdTopping(entry.getKey());
            precioExtras += extra.getPrecioTopping() * entry.getValue();
        }

        // Precio de aderezos (primer aderezo de cada tipo incluido, extras se pagan)
        double precioAderezos = 0;
        for (Map.Entry<Long, Integer> entry : aderezosConCantidad.entrySet()) {
            Aderezo aderezo = aderezoService.getAderezoById(entry.getKey());
            // El primer aderezo de cada tipo es gratis, los adicionales se pagan
            int cantidadPagada = Math.max(0, entry.getValue() - 1);
            precioAderezos += aderezo.getPrecio() * cantidadPagada;
        }

        return base + precioCarne + precioPan + precioExtras + precioAderezos;
    }

    // Construir descripción de hamburguesa
    private String construirDescripcionHamburguesa(int cantidadCarnes, Topping carne, Topping pan,
                                                   Map<Long, Integer> aderezosConCantidad,
                                                   Map<Long, Integer> extrasConCantidad,
                                                   String observaciones) {
        StringBuilder descripcion = new StringBuilder();
        descripcion.append(cantidadCarnes).append(" carne").append(cantidadCarnes > 1 ? "s" : "")
                .append(" de ").append(carne.getNombre())
                .append(" • Pan: ").append(pan.getNombre());

        // Aderezos
        if (!aderezosConCantidad.isEmpty()) {
            descripcion.append(" • Aderezos: ");
            List<String> aderezosDesc = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : aderezosConCantidad.entrySet()) {
                Aderezo aderezo = aderezoService.getAderezoById(entry.getKey());
                String aderezoDesc = entry.getValue() > 1 ?
                        aderezo.getNombre() + " (x" + entry.getValue() + ")" : aderezo.getNombre();
                aderezosDesc.add(aderezoDesc);
            }
            descripcion.append(String.join(", ", aderezosDesc));
        }

        // Extras
        if (!extrasConCantidad.isEmpty()) {
            descripcion.append(" • Extras: ");
            List<String> extrasDesc = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : extrasConCantidad.entrySet()) {
                Topping extra = toppingService.findByIdTopping(entry.getKey());
                String extraDesc = entry.getValue() > 1 ?
                        extra.getNombre() + " (x" + entry.getValue() + ")" : extra.getNombre();
                extrasDesc.add(extraDesc);
            }
            descripcion.append(String.join(", ", extrasDesc));
        }

        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }

        return descripcion.toString();
    }

    // Métodos auxiliares para resúmenes
    private String construirResumenAderezos(Map<Long, Integer> aderezosCantidad) {
        if (aderezosCantidad.isEmpty()) return "Sin aderezos";

        List<String> aderezosDesc = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : aderezosCantidad.entrySet()) {
            if (entry.getValue() > 0) {
                Aderezo aderezo = aderezoService.getAderezoById(entry.getKey());
                String desc = entry.getValue() > 1 ?
                        aderezo.getNombre() + " (x" + entry.getValue() + ")" : aderezo.getNombre();
                aderezosDesc.add(desc);
            }
        }
        return aderezosDesc.isEmpty() ? "Sin aderezos" : String.join(", ", aderezosDesc);
    }

    private String construirResumenExtras(Map<Long, Integer> extrasCantidad) {
        if (extrasCantidad.isEmpty()) return "Sin extras";

        List<String> extrasDesc = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : extrasCantidad.entrySet()) {
            if (entry.getValue() > 0) {
                Topping extra = toppingService.findByIdTopping(entry.getKey());
                String desc = entry.getValue() > 1 ?
                        extra.getNombre() + " (x" + entry.getValue() + ")" : extra.getNombre();
                extrasDesc.add(desc);
            }
        }
        return extrasDesc.isEmpty() ? "Sin extras" : String.join(", ", extrasDesc);
    }

    // Calcular total estimado para el GET
    private double calcularTotalEstimadoBurger(Map<String, String> allParams) {
        try {
            String cantidadCarnesStr = allParams.get("cantidad_carnes");
            String carneIdStr = allParams.get("tipo_carne");
            String panIdStr = allParams.get("tipo_pan");

            if (cantidadCarnesStr == null || carneIdStr == null || panIdStr == null) {
                return 0;
            }

            int cantidadCarnes = Integer.parseInt(cantidadCarnesStr);
            Topping carne = toppingService.findByIdTopping(Long.parseLong(carneIdStr));
            Topping pan = toppingService.findByIdTopping(Long.parseLong(panIdStr));

            // Calcular igual que en el POST pero solo con los datos disponibles
            double base = switch (cantidadCarnes) {
                case 1 -> 340;
                case 3 -> 520;
                default -> 430;
            };

            double precioCarne = carne.getPrecioTopping();
            double precioPan = pan.getPrecioTopping();

            // Calcular extras
            double precioExtras = 0;
            for (String key : allParams.keySet()) {
                if (key.startsWith("extrasCantidad[")) {
                    Long extraId = Long.parseLong(key.substring("extrasCantidad[".length(), key.length() - 1));
                    int cantidad = Integer.parseInt(allParams.get(key));
                    if (cantidad > 0) {
                        Topping extra = toppingService.findByIdTopping(extraId);
                        precioExtras += extra.getPrecioTopping() * cantidad;
                    }
                }
            }

            // Calcular aderezos
            double precioAderezos = 0;
            for (String key : allParams.keySet()) {
                if (key.startsWith("aderezosCantidad[")) {
                    Long aderezoId = Long.parseLong(key.substring("aderezosCantidad[".length(), key.length() - 1));
                    int cantidad = Integer.parseInt(allParams.get(key));
                    if (cantidad > 0) {
                        Aderezo aderezo = aderezoService.getAderezoById(aderezoId);
                        int cantidadPagada = Math.max(0, cantidad - 1);
                        precioAderezos += aderezo.getPrecio() * cantidadPagada;
                    }
                }
            }

            return base + precioCarne + precioPan + precioExtras + precioAderezos;

        } catch (Exception e) {
            return 0;
        }
    }
}