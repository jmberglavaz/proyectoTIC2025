package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.dto.*;
import um.edu.uy.jdftech.entitites.*;
import um.edu.uy.jdftech.enums.EstadoPedido;
import um.edu.uy.jdftech.repositories.PedidoRepository;
import um.edu.uy.jdftech.services.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    @Autowired
    private ToppingService toppingService;

    @Autowired
    private AderezoService aderezoService;

    @Autowired
    private AcompanamientoService acompanamientoService;

    @Autowired
    private BebidaService bebidaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private MedioDePagoService medioDePagoService;

    @Autowired
    private OrganismosPublicosService organismosPublicosService;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Verificar que el usuario esté autenticado como administrador
     */
    private boolean verificarAdminAutenticado(HttpSession session) {
        return session.getAttribute("admin") != null;
    }

    /**
     * Redirigir al login de admin si no está autenticado
     */
    private String redirigirSiNoAutenticado(HttpSession session, RedirectAttributes ra) {
        if (!verificarAdminAutenticado(session)) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador para acceder a esta página");
            return "redirect:/admin/login";
        }
        return null;
    }

    /* ======================
       Páginas (GET)
       ====================== */
    @GetMapping
    public String panel(HttpSession session, RedirectAttributes ra) {
        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        if (session.getAttribute("admin") == null) {
            ra.addFlashAttribute("error", "Debe iniciar sesión como administrador");
            return "redirect:/admin/login";
        }
        return "admin/index-admin";
    }

    @GetMapping("/productos")
    public String productos(HttpSession session, RedirectAttributes ra, Model model) {
        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        // Cargar todos los toppings por categoría para pizzas
        model.addAttribute("masas", toppingService.verToppingsDeTipo('M'));
        model.addAttribute("salsas", toppingService.verToppingsDeTipo('S'));
        model.addAttribute("quesos", toppingService.verToppingsDeTipo('Q'));
        model.addAttribute("toppingsPizza", toppingService.verToppingsDeTipo('X'));

        // Cargar toppings para hamburguesas
        model.addAttribute("panes", toppingService.verToppingsDeTipo('P'));
        model.addAttribute("carnes", toppingService.verToppingsDeTipo('C'));
        model.addAttribute("extras", toppingService.verToppingsDeTipo('X'));

        // Cargar aderezos, acompañamientos y bebidas
        model.addAttribute("aderezos", aderezoService.findAll());
        model.addAttribute("acompanamientos", acompanamientoService.findAll());
        model.addAttribute("bebidas", bebidaService.findAll());

        return "admin/productos-admin";
    }

    @GetMapping("/pedidos")
    public String pedidos(@RequestParam(required = false) String numero,
                          @RequestParam(required = false) String clienteId,
                          @RequestParam(required = false) String estado,
                          @RequestParam(required = false) String tipo,
                          @RequestParam(required = false) String desde,
                          @RequestParam(required = false) String hasta,
                          HttpSession session, RedirectAttributes ra, Model model) {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        try {
            // TEMPORAL: Mostrar todos los pedidos sin filtros
            List<Pedido> pedidos = pedidoRepository.findAll();

            // O si quieres los últimos 10:
            // List<Pedido> pedidos = pedidoService.findLast10Orders();

            System.out.println("DEBUG - Encontrados " + (pedidos != null ? pedidos.size() : 0) + " pedidos");

            if (pedidos != null) {
                for (Pedido pedido : pedidos) {
                    System.out.println("DEBUG - Pedido #" + pedido.getId() + " - Estado: " + pedido.getStatus());
                    // Cargar relaciones para cada pedido
                    pedidoService.obtenerPedidoConDetalles(pedido.getId());
                }
            }

            model.addAttribute("pedidos", pedidos != null ? pedidos : Collections.emptyList());

        } catch (Exception e) {
            System.out.println("ERROR en pedidos admin: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("pedidos", Collections.emptyList());
            model.addAttribute("error", "Error al cargar pedidos: " + e.getMessage());
        }

        // Mantener los parámetros en el modelo
        model.addAttribute("filtros", Map.of(
                "numero", numero != null ? numero : "",
                "clienteId", clienteId != null ? clienteId : "",
                "estado", estado != null ? estado : "",
                "tipo", tipo != null ? tipo : "",
                "desde", desde != null ? desde : "",
                "hasta", hasta != null ? hasta : ""
        ));

        return "admin/pedidos-admin";
    }

    @GetMapping("/administradores")
    public String administradores(@RequestParam(required = false) String buscar,
                                  HttpSession session, RedirectAttributes ra, Model model) {
        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        try {
            List<Administrador> administradores;
            if (buscar != null && !buscar.trim().isEmpty()) {
                // Buscar por nombre completo
                administradores = administradorService.findByFullName(buscar, buscar);
            } else {
                // Listar todos
                administradores = administradorService.findAll();
            }
            model.addAttribute("administradores", administradores);
        } catch (Exception e) {
            model.addAttribute("administradores", Collections.emptyList());
            model.addAttribute("error", "Error al cargar los administradores: " + e.getMessage());
        }
        return "admin/admins-admin";
    }

    @GetMapping("/historial")
    public String historial(@RequestParam(required = false) String numero,
                            @RequestParam(required = false) String clienteId,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) String tipo,
                            @RequestParam(required = false) String desde,
                            @RequestParam(required = false) String hasta,
                            HttpSession session, RedirectAttributes ra, Model model) {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        List<Pedido> pedidos;

        // Si hay filtros aplicados, usarlos
        if (numero != null || clienteId != null || estado != null || tipo != null || desde != null || hasta != null) {
            pedidos = pedidoService.findWithFilters(numero, clienteId, estado, tipo, desde, hasta);
        } else {
            // Si no hay filtros, mostrar los últimos 10 pedidos ordenados por fecha de creación (más recientes primero)
            pedidos = pedidoService.findLast10Orders();
        }

        model.addAttribute("pedidos", pedidos != null ? pedidos : Collections.emptyList());

        // Mantener los parámetros en el modelo para que los filtros se mantengan
        model.addAttribute("filtros", Map.of(
                "numero", numero != null ? numero : "",
                "clienteId", clienteId != null ? clienteId : "",
                "estado", estado != null ? estado : "",
                "tipo", tipo != null ? tipo : "",
                "desde", desde != null ? desde : "",
                "hasta", hasta != null ? hasta : ""
        ));

        return "admin/historial-admin";
    }

    /* ======================
   CONSULTA DE TARJETAS
   ====================== */
    @GetMapping("/tarjetas")
    public String consultaTarjetas(@RequestParam(required = false) Long numeroTarjeta,
                                   HttpSession session, RedirectAttributes ra, Model model) {
        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        try {
            if (numeroTarjeta != null) {
                TarjetaInfoDTO info = medioDePagoService.obtenerInfoTarjeta(numeroTarjeta);
                model.addAttribute("infoTarjeta", info);
                model.addAttribute("encontrado", true);
            } else {
                model.addAttribute("encontrado", false);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Tarjeta no encontrada: " + e.getMessage());
            model.addAttribute("encontrado", false);
        }

        model.addAttribute("numeroTarjeta", numeroTarjeta != null ? numeroTarjeta : "");
        return "admin/tarjetas-admin";
    }

    /* ======================
       PRODUCTOS - Toppings (Pizzas y Hamburguesas)
       ====================== */
    @PostMapping("/productos/crear")
    public String crearTopping(@RequestParam String nombre,
                               @RequestParam(required = false) Double precio,
                               @RequestParam char hamburguesaOPizza,
                               @RequestParam char tipo,
                               HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Creando topping:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Tipo producto: " + hamburguesaOPizza);
        System.out.println("Categoría: " + tipo);

        try {
            double precioFinal = (precio != null) ? precio : 0.0;

            Topping nuevoTopping = new Topping(
                    null,
                    nombre,
                    hamburguesaOPizza,
                    tipo,
                    precioFinal,
                    LocalDateTime.now()
            );

            Topping toppingGuardado = toppingService.crear(nuevoTopping);

            System.out.println("Topping guardado con ID: " + toppingGuardado.getIdTopping());
            ra.addFlashAttribute("msg", "Producto '" + nombre + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar topping: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarTopping(@PathVariable Long id, HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Eliminando topping con ID: " + id);

        try {
            toppingService.delete(id);
            ra.addFlashAttribute("msg", "Producto eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar topping: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       ADEREZOS
       ====================== */
    @PostMapping("/aderezos/crear")
    public String crearAderezo(@RequestParam String nombre,
                               @RequestParam Double precio,
                               HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Creando aderezo:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);

        try {
            Aderezo nuevoAderezo = new Aderezo(null, nombre, precio);
            Aderezo aderezoGuardado = aderezoService.createNewAderezo(nuevoAderezo);

            System.out.println("Aderezo guardado con ID: " + aderezoGuardado.getIdAderezo());
            ra.addFlashAttribute("msg", "Aderezo '" + nombre + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar aderezo: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el aderezo: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/aderezos/eliminar/{id}")
    public String eliminarAderezo(@PathVariable Long id,
                                  HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;
        System.out.println("Eliminando aderezo con ID: " + id);

        try {
            aderezoService.deleteAderezo(id);
            ra.addFlashAttribute("msg", "Aderezo eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar aderezo: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el aderezo: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       ACOMPAÑAMIENTOS
       ====================== */
    @PostMapping("/acompanamientos/crear")
    public String crearAcompanamiento(@RequestParam String name,
                                      @RequestParam String size,
                                      @RequestParam Double price,
                                      HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Creando acompañamiento:");
        System.out.println("Nombre: " + name);
        System.out.println("Tamaño: " + size);
        System.out.println("Precio: " + price);

        try {
            Acompanamiento nuevoAcompanamiento = Acompanamiento.builder()
                    .name(name)
                    .size(size)
                    .price(price)
                    .build();

            Acompanamiento acompanamientoGuardado = acompanamientoService.createNewAcompanamiento(nuevoAcompanamiento);

            System.out.println("Acompañamiento guardado con ID: " + acompanamientoGuardado.getId());
            ra.addFlashAttribute("msg", "Acompañamiento '" + name + "' agregado correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar acompañamiento: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar el acompañamiento: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/acompanamientos/eliminar/{id}")
    public String eliminarAcompanamiento(@PathVariable Long id,
                                         HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;
        System.out.println("Eliminando acompañamiento con ID: " + id);

        try {
            acompanamientoService.deleteAcompanamiento(id);
            ra.addFlashAttribute("msg", "Acompañamiento eliminado correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar acompañamiento: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar el acompañamiento: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    /* ======================
       BEBIDAS
       ====================== */
    @PostMapping("/bebidas/crear")
    public String crearBebida(@RequestParam String name,
                              @RequestParam String size,
                              @RequestParam Double price,
                              HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Creando bebida:");
        System.out.println("Nombre: " + name);
        System.out.println("Tamaño: " + size);
        System.out.println("Precio: " + price);

        try {
            Bebida nuevaBebida = Bebida.builder()
                    .name(name)
                    .size(size)
                    .price(price)
                    .build();

            Bebida bebidaGuardada = bebidaService.createNewBebida(nuevaBebida);

            System.out.println("Bebida guardada con ID: " + bebidaGuardada.getId());
            ra.addFlashAttribute("msg", "Bebida '" + name + "' agregada correctamente.");

        } catch (Exception e) {
            System.out.println("ERROR al guardar bebida: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al agregar la bebida: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/bebidas/eliminar/{id}")
    public String eliminarBebida(@PathVariable Long id,
                                 HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        System.out.println("Eliminando bebida con ID: " + id);

        try {
            bebidaService.deleteBebida(id);
            ra.addFlashAttribute("msg", "Bebida eliminada correctamente.");
        } catch (Exception e) {
            System.out.println("ERROR al eliminar bebida: " + e.getMessage());
            e.printStackTrace();
            ra.addFlashAttribute("error", "Error al eliminar la bebida: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }

    @PostMapping("/pedidos")
    public String actualizarPedido(@RequestParam Long pedido_id,
                                   @RequestParam String estado,
                                   HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        try {
            // Convertir el string del formulario al enum (aceptar tanto mayúsculas como minúsculas)
            EstadoPedido nuevoEstado = switch(estado.toUpperCase()) {
                case "EN_COLA" -> EstadoPedido.EN_COLA;
                case "EN_PREPARACION" -> EstadoPedido.EN_PREPARACION;
                case "EN_CAMINO" -> EstadoPedido.EN_CAMINO;
                case "ENTREGADO" -> EstadoPedido.ENTREGADO;
                default -> throw new IllegalArgumentException("Estado inválido: " + estado);
            };

            Pedido pedidoActualizado = pedidoService.updateStatus(pedido_id, nuevoEstado);

            ra.addFlashAttribute("msg",
                    "Pedido #" + String.format("%06d", pedido_id) +
                            " actualizado a: " + nuevoEstado.getDisplayName());

        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Error al actualizar el pedido: " + e.getMessage());
        }

        return "redirect:/admin/pedidos";
    }

    @PostMapping("/administradores")
    public String gestionarAdministradores(@RequestParam(required = false) String cedula,
                                           @RequestParam(required = false) String nombre,
                                           @RequestParam(required = false) String apellido,
                                           @RequestParam(required = false) String email,
                                           @RequestParam(required = false) String password,
                                           @RequestParam(required = false, name = "password_confirm") String passwordConfirm,
                                           @RequestParam(required = false) String telefono,
                                           @RequestParam(required = false) String fechaNacimiento,
                                           @RequestParam(required = false, name = "admin_id") Long adminId,
                                           @RequestParam(required = false, name = "accion") String accion,
                                           HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;


        try {
            if ("crear".equals(accion)) {
                // Validar que las contraseñas coincidan
                if (!password.equals(passwordConfirm)) {
                    ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
                    return "redirect:/admin/administradores";
                }

                // Validar que la cédula sea un número válido
                Long cedulaLong;
                try {
                    cedulaLong = Long.parseLong(cedula);
                } catch (NumberFormatException e) {
                    ra.addFlashAttribute("error", "La cédula debe ser un número válido.");
                    return "redirect:/admin/administradores";
                }

                // Crear nuevo administrador
                Administrador nuevoAdmin = Administrador.builder()
                        .id(cedulaLong)
                        .firstName(nombre)
                        .lastName(apellido)
                        .email(email)
                        .password(password) // En producción, esto debería estar encriptado
                        .phoneNumber(telefono)
                        .birthDate(Date.valueOf(fechaNacimiento).toLocalDate())
                        .build();

                administradorService.crear(nuevoAdmin);
                ra.addFlashAttribute("msg", "Administrador '" + nombre + " " + apellido + "' creado correctamente.");

            } else if ("eliminar".equals(accion) && adminId != null) {
                // Eliminar administrador
                administradorService.delete(adminId);
                ra.addFlashAttribute("msg", "Administrador eliminado correctamente.");

            } else if ("reset_password".equals(accion) && adminId != null) {
                // Restablecer contraseña (aquí podrías generar una contraseña temporal)
                // Por ahora solo mostramos un mensaje
                ra.addFlashAttribute("msg", "Solicitud de restablecimiento de contraseña para el administrador ID: " + adminId);
            }

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }

        return "redirect:/admin/administradores";
    }

    /* ======================
   ORGANISMOS PÚBLICOS
   ====================== */
    @GetMapping("/organismos-publicos")
    public String organismosPublicos(HttpSession session, RedirectAttributes ra, Model model) {
        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        // Obtener estadísticas para mostrar en el panel
        BPSResponseDTO bpsInfo = organismosPublicosService.obtenerCantidadFuncionariosBPS();
        model.addAttribute("bpsInfo", bpsInfo);
        model.addAttribute("hoy", LocalDate.now());

        return "admin/organismos-publicos-admin";
    }

    @PostMapping("/organismos-publicos/dgi")
    public String consultarTicketsDGI(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                                      Model model,
                                      HttpSession session, RedirectAttributes ra)  {

        String redireccion = redirigirSiNoAutenticado(session, ra);
        if (redireccion != null) return redireccion;

        try {
            List<TicketDGIDTO> tickets;

            if (hasta != null) {
                // Consulta por rango
                if (fecha.isAfter(hasta)) {
                    ra.addFlashAttribute("error", "La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
                    return "redirect:/admin/organismos-publicos";
                }
                tickets = organismosPublicosService.obtenerTicketsDGIPorRango(fecha, hasta);
                model.addAttribute("rango", true);
                model.addAttribute("desde", fecha);
                model.addAttribute("hasta", hasta);
            } else {
                // Consulta por fecha única
                tickets = organismosPublicosService.obtenerTicketsDGI(fecha);
                model.addAttribute("rango", false);
                model.addAttribute("fecha", fecha);
            }

            model.addAttribute("tickets", tickets);
            model.addAttribute("hayResultados", !tickets.isEmpty());

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al consultar tickets: " + e.getMessage());
        }

        // Recargar datos BPS
        BPSResponseDTO bpsInfo = organismosPublicosService.obtenerCantidadFuncionariosBPS();
        model.addAttribute("bpsInfo", bpsInfo);
        model.addAttribute("hoy", LocalDate.now());

        return "admin/organismos-publicos-admin";
    }

    @PostMapping("/logout")  // Solo el logout de admin aquí
    public String adminLogout(HttpSession session, RedirectAttributes ra) {
        session.removeAttribute("admin");
        session.removeAttribute("adminId");
        session.removeAttribute("adminNombre");
        session.invalidate();

        ra.addFlashAttribute("msg", "Sesión de administrador cerrada correctamente.");
        return "redirect:/admin/login";
    }

    // TEMPORAL - Para debug
    @GetMapping("/pedidos/debug")
    public String pedidosDebug(HttpSession session, Model model) {
        System.out.println("=== DEBUG PEDIDOS ADMIN ===");

        // Mostrar todos los pedidos en la base de datos
        List<Pedido> todosLosPedidos = pedidoRepository.findAll();
        System.out.println("Total pedidos en BD: " + todosLosPedidos.size());

        for (Pedido p : todosLosPedidos) {
            System.out.println("Pedido #" + p.getId() +
                    " - Cliente: " + p.getClient().getId() +
                    " - Estado: " + p.getStatus() +
                    " - Fecha: " + p.getDate());
        }

        model.addAttribute("pedidos", todosLosPedidos);
        return "admin/pedidos-admin";
    }
}