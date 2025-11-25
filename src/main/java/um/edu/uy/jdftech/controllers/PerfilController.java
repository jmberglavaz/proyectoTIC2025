package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ParseException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.entitites.MedioDePago;
import um.edu.uy.jdftech.exceptions.InvalidCardException;
import um.edu.uy.jdftech.services.ClienteService;
import um.edu.uy.jdftech.services.DireccionService;
import um.edu.uy.jdftech.services.MedioDePagoService;
import um.edu.uy.jdftech.validators.ValidacionDeMetodoDePago;
import um.edu.uy.jdftech.validators.ValidationResult;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final ClienteService clienteService;
    private final DireccionService direccionService;
    private final MedioDePagoService medioDePagoService;

    @GetMapping
    public String perfil(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/login";
        }

        List<MedioDePago> mediosDePago = medioDePagoService.findByClienteId(cliente.getId());

        // DEBUG - Ver en consola
        System.out.println("=== DEBUG MEDIOS DE PAGO ===");
        System.out.println("Cliente ID: " + cliente.getId());
        System.out.println("Número de tarjetas encontradas: " + mediosDePago.size());
        for (int i = 0; i < mediosDePago.size(); i++) {
            MedioDePago tarjeta = mediosDePago.get(i);
            System.out.println("Tarjeta " + i + ": " + tarjeta.getCardNumber() +
                    " - " + tarjeta.getFirstNameOnCard() + " " + tarjeta.getLastNameOnCard());
        }
        System.out.println("============================");

        model.addAttribute("cliente", cliente);
        model.addAttribute("direcciones", direccionService.findByCliente(cliente.getId()));
        model.addAttribute("mediosDePago", mediosDePago);
        model.addAttribute("nuevaDireccion", new Direccion());

        return "user/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String nuevaPassword,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                Cliente clienteActual = clienteService.findById(cliente.getId());
                clienteActual.setPassword(nuevaPassword);
                clienteService.update(cliente.getId(), clienteActual);
                redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
            }
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones")
    public String agregarDireccion(@ModelAttribute Direccion nuevaDireccion,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                direccionService.save(nuevaDireccion, cliente.getId());
                redirectAttributes.addFlashAttribute("success", "Dirección agregada correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al agregar dirección: " + e.getMessage());
            }
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones/{id}/default")
    public String setDireccionDefault(@PathVariable Long id,
                                      HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            direccionService.setDefault(id, cliente.getId());
        }
        return "redirect:/perfil";
    }

    @PostMapping("/direcciones/{id}/delete")
    public String deleteDireccion(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                direccionService.delete(id, cliente.getId());
                redirectAttributes.addFlashAttribute("success", "Dirección eliminada correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar dirección: " + e.getMessage());
            }
        }
        return "redirect:/perfil";
    }

    @PostMapping("/medios-pago")
    public String agregarMedioDePago(@RequestParam String cardNumber,
                                     @RequestParam String cvv,
                                     @RequestParam String firstNameOnCard,
                                     @RequestParam String lastNameOnCard,
                                     @RequestParam String expirationMonth,
                                     @RequestParam String expirationYear,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                // Validar y limpiar el número de tarjeta
                String cleanCardNumber = cardNumber.replaceAll("\\s+", "").replaceAll("[^0-9]", "");
                if (!cleanCardNumber.matches("\\d{13,19}")) {
                    redirectAttributes.addFlashAttribute("error", "Número de tarjeta inválido");
                    return "redirect:/perfil";
                }

                Long cardNumberLong = Long.parseLong(cleanCardNumber);

                // Validar CVV
                if (!cvv.matches("\\d{3,4}")) {
                    redirectAttributes.addFlashAttribute("error", "CVV debe tener 3 o 4 dígitos");
                    return "redirect:/perfil";
                }
                int cvvInt = Integer.parseInt(cvv);

                // Crear la fecha manualmente
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date expirationDate = sdf.parse("01/" + expirationMonth + "/" + expirationYear);

                // Obtener el cliente actualizado desde la base de datos
                Cliente clienteActual = clienteService.findById(cliente.getId());

                // Crear el medio de pago
                MedioDePago nuevoMedioDePago = MedioDePago.builder()
                        .cardNumber(cardNumberLong)
                        .cvv(cvvInt)
                        .firstNameOnCard(firstNameOnCard.toUpperCase().trim())
                        .lastNameOnCard(lastNameOnCard.toUpperCase().trim())
                        .expirationDate(expirationDate)
                        .cliente(clienteActual)
                        .build();

                // Validar la tarjeta
                ValidacionDeMetodoDePago validador = new ValidacionDeMetodoDePago();
                ValidationResult resultado = validador.validarTarjeta(nuevoMedioDePago);

                if (!resultado.isValid()) {
                    redirectAttributes.addFlashAttribute("error", String.join(", ", resultado.getErrors()));
                    return "redirect:/perfil";
                }

                // Guardar la tarjeta
                medioDePagoService.save(nuevoMedioDePago);

                redirectAttributes.addFlashAttribute("success", "Tarjeta agregada correctamente");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al agregar tarjeta: " + e.getMessage());
            }
        }
        return "redirect:/perfil";
    }

    @PostMapping("/medios-pago/{cardNumber}/delete")
    public String deleteMedioDePago(@PathVariable Long cardNumber,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                medioDePagoService.deleteByCardNumber(cardNumber, cliente.getId());
                redirectAttributes.addFlashAttribute("success", "Tarjeta eliminada correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar tarjeta: " + e.getMessage());
            }
        }
        return "redirect:/perfil";
    }
}