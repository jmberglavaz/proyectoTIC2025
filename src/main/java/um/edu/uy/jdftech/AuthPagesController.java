package um.edu.uy.jdftech;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPagesController {

    /* ======================
       LOGIN
       ====================== */
    @GetMapping("/login")
    public String loginView() {
        // Renderiza templates/login.html
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              RedirectAttributes ra) {
        // TODO: autenticar contra tu servicio/BDD o Spring Security
        // Ejemplo (falso): cualquier email/pass no vacío -> OK
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            ra.addFlashAttribute("error", "Correo y contraseña son obligatorios.");
            return "redirect:/login";
        }

        // Si la autenticación fuera exitosa:
        ra.addFlashAttribute("msg", "Sesión iniciada como: " + email);
        return "redirect:/";
    }

    /* ======================
       REGISTER
       ====================== */
    @GetMapping("/register")
    public String registerView() {
        // Renderiza templates/register.html
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String nombre,
                                 @RequestParam String apellido,
                                 @RequestParam("fecha_nacimiento") String fechaNacimiento, // podrías mapear a LocalDate luego
                                 @RequestParam String telefono,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam("password_confirm") String passwordConfirm,
                                 @RequestParam(value = "domicilio") String domicilio,
                                 @RequestParam(value = "indicaciones", required = false) String indicaciones,
                                 @RequestParam(value = "tarjeta_numero") String tarjetaNumero,
                                 @RequestParam(value = "tarjeta_nombre") String tarjetaNombre,
                                 @RequestParam(value = "tarjeta_exp_mes") String tarjetaExpMes,
                                 @RequestParam(value = "tarjeta_exp_anio") String tarjetaExpAnio,
                                 @RequestParam(value = "tarjeta_cvv") String tarjetaCvv,
                                 @RequestParam(value = "guardar_tarjeta", required = false) String guardarTarjeta,
                                 RedirectAttributes ra) {

        // Validaciones mínimas (placeholder)
        if (!password.equals(passwordConfirm)) {
            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/register";
        }

        // TODO: validar formato de email, existencia previa, política de contraseñas, etc.
        // TODO: persistir usuario + domicilio; si guardarTarjeta != null, persistir medio de pago.
        // TODO: hashear pass (BCrypt) si no usás Spring Security aún.

        ra.addFlashAttribute("msg", "Cuenta creada para: " + email + ". Ahora podés iniciar sesión.");
        return "redirect:/login";
    }

    /* ======================
       LOGOUT (opcional simple)
       ====================== */
    @PostMapping("/logout")
    public String logout(RedirectAttributes ra) {
        // TODO: si usás Spring Security, no necesitás esto (usa /logout de Security)
        ra.addFlashAttribute("msg", "Sesión cerrada.");
        return "redirect:/";
    }
}
