package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.entitites.Administrador;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.services.AdministradorService;
import um.edu.uy.jdftech.services.ClienteService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class AuthPagesController {

    private final ClienteService clienteService;

    private final AdministradorService administradorService;

    /* ======================
       LOGIN (con cédula)
       ====================== */
    @GetMapping("/login")
    public String loginView(HttpSession session) {
        // Si ya está logueado, redirigir al home
        if (session.getAttribute("cliente") != null) {
            return "redirect:/";
        }
        return "auth/login"; //
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam Long cedula,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) { // Cambiar RedirectAttributes por Model
        try {
            // Buscar cliente por cédula
            Cliente cliente = clienteService.findById(cedula);

            // Verificar contraseña (plain text)
            if (cliente.getPassword().equals(password)) {
                // Login exitoso - guardar en sesión
                session.setAttribute("cliente", cliente);
                session.setAttribute("clienteId", cliente.getId());
                session.setAttribute("clienteNombre", cliente.getFirstName());

                return "redirect:/";
            } else {
                // Contraseña incorrecta
                model.addAttribute("error", "Cédula o contraseña incorrectos");
                return "auth/login"; // Renderizar directamente sin redirect
            }
        } catch (Exception e) {
            // Cliente no encontrado u otro error
            model.addAttribute("error", "Cédula o contraseña incorrectos");
            return "auth/login"; // Renderizar directamente sin redirect
        }
    }

    /* ======================
       REGISTER
       ====================== */
    @GetMapping("/register")
    public String registerView() {
        return "auth/register"; // ← CAMBIADO: ahora apunta a auth/register
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam Long cedula,
                                 @RequestParam String nombre,
                                 @RequestParam String apellido,
                                 @RequestParam("fecha_nacimiento") String fechaNacimiento,
                                 @RequestParam String telefono,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam("password_confirm") String passwordConfirm,
                                 RedirectAttributes ra) {

        try {
            // Validaciones
            if (!password.equals(passwordConfirm)) {
                ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/register";
            }

            // Convertir fecha
            LocalDate birthDate = LocalDate.parse(fechaNacimiento);

            // Crear objeto Cliente
            Cliente nuevoCliente = Cliente.builder()
                    .id(cedula)
                    .firstName(nombre)
                    .lastName(apellido)
                    .birthDate(birthDate)
                    .email(email)
                    .password(password) // Plain text por ahora
                    .phoneNumber(telefono)
                    .build();

            // Guardar cliente sin dirección (se agregará después)
            Cliente clienteGuardado = clienteService.crear(nuevoCliente);

            ra.addFlashAttribute("msg", "¡Cuenta creada exitosamente! Ahora podés iniciar sesión con tu cédula.");
            return "redirect:/login";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear la cuenta: " + e.getMessage());
            return "redirect:/register";
        }
    }

    /* ======================
       ADMIN LOGIN
       ====================== */
    @GetMapping("/admin/login")
    public String adminLoginView(HttpSession session) {
        // Si ya está logueado como admin, redirigir al panel
        if (session.getAttribute("admin") != null) {
            return "redirect:/admin";
        }
        return "auth/login-admin";
    }

    @PostMapping("/admin/login")
    public String adminLoginSubmit(@RequestParam Long cedula,
                                   @RequestParam String password,
                                   HttpSession session,
                                   Model model) {
        try {
            // Validar credenciales de administrador
            if (administradorService.validarLogin(cedula, password)) {
                Administrador admin = administradorService.findById(cedula);

                // Login exitoso - guardar en sesión
                session.setAttribute("admin", admin);
                session.setAttribute("adminId", admin.getId());
                session.setAttribute("adminNombre", admin.getFirstName() + " " + admin.getLastName());

                return "redirect:/admin";
            } else {
                // Credenciales incorrectas
                model.addAttribute("error", "Cédula o contraseña incorrectos");
                return "auth/login-admin";
            }
        } catch (Exception e) {
            // Administrador no encontrado u otro error
            model.addAttribute("error", "Cédula o contraseña incorrectos");
            return "auth/login-admin";
        }
    }



    /* ======================
       LOGOUT
       ====================== */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("msg", "Sesión cerrada correctamente.");
        return "redirect:/";
    }
}