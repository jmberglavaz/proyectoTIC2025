package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/")
    public String index() {
        return "user/index";
    }
//    @GetMapping("/login")
//    public String login() {
//        return "auth/login";
//    }

    @GetMapping("/static")
    public String staticHome() {
        return "user/index";
    }

    @GetMapping("/crear-pizza")
    public String crearPizza() {
        return "user/crear-pizza";
    }

    @GetMapping("/crear-burger")
    public String crearBurger() {
        return "user/crear-burger";
    }

    @GetMapping("/carrito")
    public String carrito() {
        return "user/carrito";
    }

    @GetMapping("/pedido")
    public String pedido() {
        return "user/pedido";
    }

    @GetMapping("/ayuda")
    public String ayuda() { return "user/ayuda"; }

    @GetMapping("/terminos")
    public String terminos() { return "user/terminos"; }

    @GetMapping("/privacidad")
    public String privacidad() { return "user/privacidad"; }
}