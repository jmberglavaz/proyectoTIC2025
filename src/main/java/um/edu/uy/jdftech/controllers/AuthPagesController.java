//package um.edu.uy.jdftech;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class AuthPagesController {
//
//    /* ======================
//       LOGIN
//       ====================== */
//    @GetMapping("/login")
//    public String loginView() {
//        // Renderiza templates/login.html
//        return "login";
//    }
//
//    @PostMapping("/login")
//    public String loginSubmit(@RequestParam String email,
//                              @RequestParam String password,
//                              RedirectAttributes ra) {
//        // TODO: autenticar contra tu servicio/BDD o Spring Security
//        // Ejemplo (falso): cualquier email/pass no vacío -> OK
//        if (email == null || email.isBlank() || password == null || password.isBlank()) {
//            ra.addFlashAttribute("error", "Correo y contraseña son obligatorios.");
//            return "redirect:/login";
//        }
//
//        // Si la autenticación fuera exitosa:
//        ra.addFlashAttribute("msg", "Sesión iniciada como: " + email);
//        return "redirect:/";
//    }
//
//    /* ======================
//       REGISTER
//       ====================== */
//    @GetMapping("/register")
//    public String registerView() {
//        // Renderiza templates/register.html
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String registerSubmit(@RequestParam String nombre,
//                                 @RequestParam String apellido,
//                                 @RequestParam("fecha_nacimiento") String fechaNacimiento, // podrías mapear a LocalDate luego
//                                 @RequestParam String telefono,
//                                 @RequestParam String email,
//                                 @RequestParam String password,
//                                 @RequestParam("password_confirm") String passwordConfirm,
//                                 @RequestParam(value = "domicilio") String domicilio,
//                                 @RequestParam(value = "indicaciones", required = false) String indicaciones,
//                                 @RequestParam(value = "tarjeta_numero") String tarjetaNumero,
//                                 @RequestParam(value = "tarjeta_nombre") String tarjetaNombre,
//                                 @RequestParam(value = "tarjeta_exp_mes") String tarjetaExpMes,
//                                 @RequestParam(value = "tarjeta_exp_anio") String tarjetaExpAnio,
//                                 @RequestParam(value = "tarjeta_cvv") String tarjetaCvv,
//                                 @RequestParam(value = "guardar_tarjeta", required = false) String guardarTarjeta,
//                                 RedirectAttributes ra) {
//
//        // Validaciones mínimas (placeholder)
//        if (!password.equals(passwordConfirm)) {
//            ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
//            return "redirect:/register";
//        }
//
//        // TODO: validar formato de email, existencia previa, política de contraseñas, etc.
//        // TODO: persistir usuario + domicilio; si guardarTarjeta != null, persistir medio de pago.
//        // TODO: hashear pass (BCrypt) si no usás Spring Security aún.
//
//        ra.addFlashAttribute("msg", "Cuenta creada para: " + email + ". Ahora podés iniciar sesión.");
//        return "redirect:/login";
//    }
//
//    /* ======================
//       LOGOUT (opcional simple)
//       ====================== */
//    @PostMapping("/logout")
//    public String logout(RedirectAttributes ra) {
//        // TODO: si usás Spring Security, no necesitás esto (usa /logout de Security)
//        ra.addFlashAttribute("msg", "Sesión cerrada.");
//        return "redirect:/";
//    }
//}
package um.edu.uy.jdftech.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.services.ClienteService;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class AuthPagesController {

    @Autowired
    private ClienteService clienteService;

    /* ======================
       LOGIN (con cédula)
       ====================== */
    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam Long cedula,
                              @RequestParam String password,
                              RedirectAttributes ra) {
        try {
            // PRIMERO: Agrega este método a tu ClienteService
            if (clienteService.validarLogin(cedula, password)) {
                ra.addFlashAttribute("msg", "Sesión iniciada correctamente");
                return "redirect:/";
            } else {
                ra.addFlashAttribute("error", "Cédula o contraseña incorrectos");
                return "redirect:/login";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            return "redirect:/login";
        }
    }

    /* ======================
       REGISTER (ajustado a tu ClienteService)
       ====================== */
    @GetMapping("/register")
    public String registerView() {
        return "register";
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
                                 @RequestParam String domicilio,
                                 @RequestParam(value = "indicaciones", required = false) String indicaciones,
                                 // Parámetros de tarjeta (opcionales)
                                 @RequestParam(value = "tarjeta_numero", required = false) String tarjetaNumero,
                                 @RequestParam(value = "tarjeta_nombre", required = false) String tarjetaNombre,
                                 @RequestParam(value = "tarjeta_exp_mes", required = false) String tarjetaExpMes,
                                 @RequestParam(value = "tarjeta_exp_anio", required = false) String tarjetaExpAnio,
                                 @RequestParam(value = "tarjeta_cvv", required = false) String tarjetaCvv,
                                 @RequestParam(value = "guardar_tarjeta", required = false) String guardarTarjeta,
                                 RedirectAttributes ra) {

        try {
            // Validaciones
            if (!password.equals(passwordConfirm)) {
                ra.addFlashAttribute("error", "Las contraseñas no coinciden.");
                return "redirect:/register";
            }

            // Convertir fecha
            Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaNacimiento);

            // Parsear domicilio en streetName y doorNumber
            String[] direccionParts = parseDireccion(domicilio);
            String streetName = direccionParts[0];
            String doorNumber = direccionParts[1];

            // Registrar cliente - AJUSTADO A TUS PARÁMETROS EXACTOS
            clienteService.registrarCliente(
                    cedula,           // id (Long)
                    nombre,           // firstName (String)
                    apellido,         // lastName (String)
                    birthDate,        // birthDate (Date)
                    email,            // email (String)
                    password,         // password (String)
                    telefono,         // phoneNumber (String)
                    streetName,       // streetName (String)
                    doorNumber,       // doorNumber (String)
                    indicaciones,     // addressIndications (String)
                    "Casa"            // alias (String) - dirección principal
            );

            ra.addFlashAttribute("msg", "¡Cuenta creada exitosamente! Ahora podés iniciar sesión con tu cédula.");
            return "redirect:/login";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al crear la cuenta: " + e.getMessage());
            return "redirect:/register";
        }
    }

    /* ======================
       LOGOUT
       ====================== */
    @PostMapping("/logout")
    public String logout(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", "Sesión cerrada.");
        return "redirect:/";
    }

    // MÉTODO HELPER PARA PARSEAR DIRECCIÓN
    private String[] parseDireccion(String domicilio) {
        // Ejemplo: "18 de Julio 1234" -> ["18 de Julio", "1234"]
        // Ejemplo: "Bulevar Artigas 567, Apto 301" -> ["Bulevar Artigas", "567"]

        if (domicilio == null || domicilio.trim().isEmpty()) {
            return new String[]{"", ""};
        }

        // Buscar el último número en la dirección
        String[] words = domicilio.split(" ");
        String streetName = "";
        String doorNumber = "";

        for (int i = 0; i < words.length; i++) {
            if (words[i].matches(".*\\d+.*")) {
                // Encontramos un número - todo antes es calle, este y después es número
                StringBuilder streetBuilder = new StringBuilder();
                StringBuilder numberBuilder = new StringBuilder();

                for (int j = 0; j < i; j++) {
                    if (j > 0) streetBuilder.append(" ");
                    streetBuilder.append(words[j]);
                }

                for (int j = i; j < words.length; j++) {
                    if (j > i) numberBuilder.append(" ");
                    numberBuilder.append(words[j]);
                }

                streetName = streetBuilder.toString();
                doorNumber = numberBuilder.toString();
                break;
            }
        }

        // Si no encontramos número, toda la dirección es calle
        if (streetName.isEmpty()) {
            streetName = domicilio;
            doorNumber = "S/N"; // Sin número
        }

        return new String[]{streetName, doorNumber};
    }
}