package um.edu.uy.jdftech.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import um.edu.uy.jdftech.entitites.Cliente;

@Controller
public class PagesController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // Agregar info del usuario al modelo si está logueado
        if (session.getAttribute("cliente") != null) {
            model.addAttribute("clienteLogueado", true);
            model.addAttribute("clienteNombre", session.getAttribute("clienteNombre"));
        }
        return "user/index";
    }

    @GetMapping("/static")
    public String staticHome(HttpSession session, Model model) {
        if (session.getAttribute("cliente") != null) {
            model.addAttribute("clienteLogueado", true);
            model.addAttribute("clienteNombre", session.getAttribute("clienteNombre"));
        }
        return "user/index";
    }

    @GetMapping("/crear-pizza")
    public String crearPizza(HttpSession session) {
        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }
        return "user/crear-pizza";
    }

    @GetMapping("/crear-burger")
    public String crearBurger(HttpSession session) {
        if (session.getAttribute("cliente") == null) {
            return "redirect:/login";
        }
        return "user/crear-burger";
    }

    @GetMapping("/ayuda")
    public String ayuda() {
        return "user/ayuda";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "user/terminos";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "user/privacidad";
    }
}