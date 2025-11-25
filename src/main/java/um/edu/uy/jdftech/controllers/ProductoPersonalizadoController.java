package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
import um.edu.uy.jdftech.entitites.Aderezo;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.services.AderezoService;
import um.edu.uy.jdftech.services.CarritoService;
import um.edu.uy.jdftech.services.ToppingService;

import java.util.*;

@Controller
public class ProductoPersonalizadoController {

    private final CarritoService carritoService;
    private final ToppingService toppingService;
    private final AderezoService aderezoService;

    public ProductoPersonalizadoController(CarritoService carritoService,
                                           ToppingService toppingService,
                                           AderezoService aderezoService) {
        this.carritoService = carritoService;
        this.toppingService = toppingService;
        this.aderezoService = aderezoService;
    }

    // ============================================================
    //                      PIZZA
    // ============================================================

    @GetMapping("/crear-pizza")
    public String mostrarPizza(HttpSession session, Model model, RedirectAttributes ra) {
        // ⭐ Verificar si hay cliente logueado
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debés iniciar sesión para crear una pizza");
            return "redirect:/login";
        }

        model.addAttribute("page", "pizza");
        cargarOpcionesPizza(model);
        inicializarModeloPizza(model);
        return "user/crear-pizza";
    }

    @PostMapping("/crear-pizza")
    public String crearPizza(@RequestParam String tamanio,
                             @RequestParam("masa") Long masaId,
                             @RequestParam("salsa") Long salsaId,
                             @RequestParam("queso") Long quesoId,
                             @RequestParam(value = "toppings", required = false) List<Long> toppingsIds,
                             @RequestParam(defaultValue = "1") int cantidad,
                             @RequestParam(required = false) String observaciones,
                             @RequestParam(value = "accion", required = false) String accion,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        // ⭐ Verificar si hay cliente logueado
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Debés iniciar sesión para crear una pizza");
            return "redirect:/login";
        }

        List<Long> toppingsIdsSeguros = toppingsIds != null ? toppingsIds : Collections.emptyList();

        // Recuperar toppings base desde BD
        Topping masa = toppingService.findByIdTopping(masaId);
        Topping salsa = toppingService.findByIdTopping(salsaId);
        Topping queso = toppingService.findByIdTopping(quesoId);

        // Recuperar toppings extra
        List<Topping> toppingsSeleccionados = new ArrayList<>();
        for (Long idTop : toppingsIdsSeguros) {
            toppingsSeleccionados.add(toppingService.findByIdTopping(idTop));
        }

        // Precios
        double precioTamanio = precioTamanioPizza(tamanio);
        double precioMasa = masa.getPrecioTopping();
        double precioSalsa = salsa.getPrecioTopping();
        double precioQueso = queso.getPrecioTopping();

        double precioToppings = 0;
        String toppingsResumen;
        String toppingsLinea;

        if (toppingsSeleccionados.isEmpty()) {
            toppingsResumen = "—";
            toppingsLinea = "Sin toppings extras · $ 0";
        } else {
            List<String> textos = new ArrayList<>();
            for (Topping t : toppingsSeleccionados) {
                double p = t.getPrecioTopping();
                precioToppings += p;
                textos.add(t.getNombre() + " ($ " + (int) p + ")");
            }
            toppingsResumen = String.join(", ", textos);
            if (toppingsSeleccionados.size() == 1) {
                toppingsLinea = "1 topping extra · $ " + (int) precioToppings;
            } else {
                toppingsLinea = toppingsSeleccionados.size() + " toppings extra · $ " + (int) precioToppings;
            }
        }

        double precioUnitario = precioTamanio + precioMasa + precioSalsa + precioQueso + precioToppings;
        int cantidadFinal = Math.max(1, cantidad);
        int totalFinal = (int) (precioUnitario * cantidadFinal);

        // Recargar opciones dinámicas
        model.addAttribute("page", "pizza");
        cargarOpcionesPizza(model);

        // Mantener selección
        model.addAttribute("tamanioSeleccionado", tamanio);
        model.addAttribute("masaSeleccionadaId", masaId);
        model.addAttribute("salsaSeleccionadaId", salsaId);
        model.addAttribute("quesoSeleccionadoId", quesoId);
        model.addAttribute("toppingsSeleccionadosIds", toppingsIdsSeguros);
        model.addAttribute("cantidad", cantidadFinal);
        model.addAttribute("observaciones", observaciones);

        // Resúmenes
        model.addAttribute("tamanioResumen",
                resumenOpcionPizza(nombreTamanioPizza(tamanio), precioTamanio));
        model.addAttribute("masaResumen",
                resumenOpcionPizza(masa.getNombre(), precioMasa));
        model.addAttribute("salsaResumen",
                resumenOpcionPizza(salsa.getNombre(), precioSalsa));
        model.addAttribute("quesoResumen",
                resumenOpcionPizza(queso.getNombre(), precioQueso));
        model.addAttribute("toppingsResumen", toppingsResumen);
        model.addAttribute("toppingsLinea", toppingsLinea);
        model.addAttribute("totalEstimado", "$ " + totalFinal);

        if ("agregar".equals(accion)) {
            // ⭐ CREAR CONFIGURACIÓN ESTRUCTURADA
            Map<String, Object> configuracion = new HashMap<>();
            configuracion.put("tamanio", tamanio);
            configuracion.put("masaId", masaId);
            configuracion.put("salsaId", salsaId);
            configuracion.put("quesoId", quesoId);
            configuracion.put("toppingsIds", toppingsIdsSeguros);

            String descripcion = construirDescripcionPizza(
                    tamanio, masa, salsa, queso, toppingsSeleccionados, observaciones);

            // ⭐ USAR NUEVO CONSTRUCTOR CON CONFIGURACIÓN
            carritoService.addItem(new CarritoItemDTO(
                    "pizza",
                    "Pizza personalizada",
                    descripcion,
                    cantidadFinal,
                    precioUnitario,
                    configuracion  // ⭐ NUEVO PARÁMETRO
            ));

            redirectAttributes.addFlashAttribute("mensaje", "Tu pizza fue agregada al carrito");
            return "redirect:/carrito";
        }

        return "user/crear-pizza";
    }

    private void cargarOpcionesPizza(Model model) {
        // P = pizza
        model.addAttribute("masasPizza",
                toppingService.verToppingsDeTipoYProducto('M', 'P'));
        model.addAttribute("salsasPizza",
                toppingService.verToppingsDeTipoYProducto('S', 'P'));
        model.addAttribute("quesosPizza",
                toppingService.verToppingsDeTipoYProducto('Q', 'P'));
        model.addAttribute("toppingsPizzaExtras",
                toppingService.verToppingsDeTipoYProducto('X', 'P'));
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

    private String nombreTamanioPizza(String tamanio) {
        return switch (tamanio.toUpperCase()) {
            case "S" -> "Pequeña (S)";
            case "L" -> "Grande (L)";
            default -> "Mediana (M)";
        };
    }

    private String resumenOpcionPizza(String nombre, double precio) {
        if (precio <= 0) {
            return nombre + " · incluido";
        }
        return nombre + " · $ " + (int) precio;
    }

    private String construirDescripcionPizza(String tamanio,
                                             Topping masa,
                                             Topping salsa,
                                             Topping queso,
                                             List<Topping> toppings,
                                             String observaciones) {
        String toppingsTexto = toppings.isEmpty()
                ? "Sin toppings extras"
                : String.join(", ", toppings.stream().map(Topping::getNombre).toList());

        StringBuilder descripcion = new StringBuilder();
        descripcion.append("Tamaño: ").append(nombreTamanioPizza(tamanio))
                .append(" • Masa: ").append(masa.getNombre())
                .append(" • Salsa: ").append(salsa.getNombre())
                .append(" • Queso: ").append(queso.getNombre())
                .append(" • Toppings: ").append(toppingsTexto);

        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }

    // ============================================================
    //                      HAMBURGUESA
    // ============================================================

    @GetMapping("/crear-burger")
    public String mostrarBurger(HttpSession session, Model model, RedirectAttributes ra) {
        // ⭐ Verificar si hay cliente logueado
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            ra.addFlashAttribute("error", "Debés iniciar sesión para crear una hamburguesa");
            return "redirect:/login";
        }

        model.addAttribute("page", "burger");
        cargarOpcionesBurger(model);
        inicializarModeloBurger(model);
        return "user/crear-burger";
    }

    @PostMapping("/crear-burger")
    public String crearBurger(@RequestParam("cantidad_carnes") int cantidadCarnes,
                              @RequestParam("tipo_carne") Long carneId,
                              @RequestParam("tipo_pan") Long panId,
                              @RequestParam(value = "aderezos", required = false) List<Long> aderezosIds,
                              @RequestParam(value = "extras", required = false) List<Long> extrasIds,
                              @RequestParam(defaultValue = "1") int cantidad,
                              @RequestParam(required = false) String observaciones,
                              @RequestParam(value = "accion", required = false) String accion,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        // ⭐ Verificar si hay cliente logueado
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("error", "Debés iniciar sesión para crear una hamburguesa");
            return "redirect:/login";
        }

        List<Long> aderezosIdsSeguros = aderezosIds != null ? aderezosIds : Collections.emptyList();
        List<Long> extrasIdsSeguros = extrasIds != null ? extrasIds : Collections.emptyList();

        // Carne y pan
        Topping carne = toppingService.findByIdTopping(carneId);
        Topping pan = toppingService.findByIdTopping(panId);

        // Aderezos desde BD
        List<Aderezo> aderezosSeleccionados = new ArrayList<>();
        for (Long idAd : aderezosIdsSeguros) {
            aderezosSeleccionados.add(aderezoService.getAderezoById(idAd));
        }

        // Extras desde BD
        List<Topping> extrasSeleccionados = new ArrayList<>();
        for (Long idEx : extrasIdsSeguros) {
            extrasSeleccionados.add(toppingService.findByIdTopping(idEx));
        }

        // Precios
        double precioCarnes = precioCantidadCarnes(cantidadCarnes);
        double precioTipoCarne = carne.getPrecioTopping();
        double precioPan = pan.getPrecioTopping();

        // Aderezos: sin costo
        String aderezosResumen;
        String aderezosLinea;
        int cantAderezos = aderezosSeleccionados.size();
        if (cantAderezos == 0) {
            aderezosResumen = "—";
            aderezosLinea = "Sin aderezos · $ 0";
        } else {
            List<String> nombres = new ArrayList<>();
            for (Aderezo a : aderezosSeleccionados) {
                nombres.add(a.getNombre());
            }
            aderezosResumen = String.join(", ", nombres);
            if (cantAderezos == 1) {
                aderezosLinea = "1 aderezo · $ 0";
            } else {
                aderezosLinea = cantAderezos + " aderezos · $ 0";
            }
        }

        // Extras
        double totalExtras = 0;
        String extrasResumen;
        String extrasLinea;
        if (extrasSeleccionados.isEmpty()) {
            extrasResumen = "—";
            extrasLinea = "Sin extras · $ 0";
        } else {
            List<String> textos = new ArrayList<>();
            for (Topping ex : extrasSeleccionados) {
                double p = ex.getPrecioTopping();
                totalExtras += p;
                textos.add(ex.getNombre() + " ($ " + (int) p + ")");
            }
            extrasResumen = String.join(", ", textos);
            if (extrasSeleccionados.size() == 1) {
                extrasLinea = "1 extra · $ " + (int) totalExtras;
            } else {
                extrasLinea = extrasSeleccionados.size() + " extras · $ " + (int) totalExtras;
            }
        }

        double precioUnitario = precioCarnes + precioTipoCarne + precioPan + totalExtras;
        int cantidadFinal = Math.max(1, cantidad);
        int totalFinal = (int) (precioUnitario * cantidadFinal);

        model.addAttribute("page", "burger");
        cargarOpcionesBurger(model);

        // Mantener selección
        model.addAttribute("cantidad_carnes", cantidadCarnes);
        model.addAttribute("carneSeleccionadaId", carneId);
        model.addAttribute("panSeleccionadoId", panId);
        model.addAttribute("aderezosSeleccionadosIds", aderezosIdsSeguros);
        model.addAttribute("extrasSeleccionadosIds", extrasIdsSeguros);
        model.addAttribute("cantidad", cantidadFinal);
        model.addAttribute("observaciones", observaciones);

        // Resúmenes
        model.addAttribute("cantCarnesResumen",
                cantidadCarnes + " carnes · $ " + (int) precioCarnes);
        model.addAttribute("tipoCarneResumen",
                carne.getNombre() + " · " + (precioTipoCarne <= 0 ? "incluido" : "$ " + (int) precioTipoCarne));
        model.addAttribute("tipoPanResumen",
                pan.getNombre() + " · " + (precioPan <= 0 ? "incluido" : "$ " + (int) precioPan));
        model.addAttribute("aderezosResumen", aderezosResumen);
        model.addAttribute("extrasResumen", extrasResumen);
        model.addAttribute("aderezosLinea", aderezosLinea);
        model.addAttribute("extrasLinea", extrasLinea);
        model.addAttribute("totalEstimado", "$ " + totalFinal);

        if ("agregar".equals(accion)) {
            // ⭐ CREAR CONFIGURACIÓN ESTRUCTURADA
            Map<String, Object> configuracion = new HashMap<>();
            configuracion.put("cantidadCarnes", cantidadCarnes);
            configuracion.put("carneId", carneId);
            configuracion.put("panId", panId);
            configuracion.put("aderezosIds", aderezosIdsSeguros);
            configuracion.put("extrasIds", extrasIdsSeguros);

            String descripcion = construirDescripcionHamburguesa(
                    cantidadCarnes, carne, pan, aderezosSeleccionados, extrasSeleccionados, observaciones);

            // ⭐ USAR NUEVO CONSTRUCTOR CON CONFIGURACIÓN
            carritoService.addItem(new CarritoItemDTO(
                    "burger",
                    "Hamburguesa personalizada",
                    descripcion,
                    cantidadFinal,
                    precioUnitario,
                    configuracion  // ⭐ NUEVO PARÁMETRO
            ));

            redirectAttributes.addFlashAttribute("mensaje", "Tu hamburguesa fue agregada al carrito");
            return "redirect:/carrito";
        }

        return "user/crear-burger";
    }

    private void cargarOpcionesBurger(Model model) {
        // H = hamburguesa
        model.addAttribute("carnesBurger",
                toppingService.verToppingsDeTipoYProducto('C', 'H'));
        model.addAttribute("panesBurger",
                toppingService.verToppingsDeTipoYProducto('P', 'H'));
        model.addAttribute("extrasBurger",
                toppingService.verToppingsDeTipoYProducto('X', 'H'));

        model.addAttribute("aderezosBurger", aderezoService.findAll());
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

    private String construirDescripcionHamburguesa(int cantidadCarnes,
                                                   Topping tipoCarne,
                                                   Topping tipoPan,
                                                   List<Aderezo> aderezos,
                                                   List<Topping> extras,
                                                   String observaciones) {
        String aderezosTexto = aderezos.isEmpty()
                ? "Sin aderezos"
                : String.join(", ", aderezos.stream().map(Aderezo::getNombre).toList());
        String extrasTexto = extras.isEmpty()
                ? "Sin extras"
                : String.join(", ", extras.stream().map(Topping::getNombre).toList());

        StringBuilder descripcion = new StringBuilder();
        descripcion.append(cantidadCarnes).append(" carnes de ").append(tipoCarne.getNombre())
                .append(" • Pan: ").append(tipoPan.getNombre())
                .append(" • Aderezos: ").append(aderezosTexto)
                .append(" • Extras: ").append(extrasTexto);

        if (observaciones != null && !observaciones.isBlank()) {
            descripcion.append(" • Obs: ").append(observaciones.trim());
        }
        return descripcion.toString();
    }
}
