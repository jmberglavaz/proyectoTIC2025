package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

  // Home
  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("page", "home");
    return "index";
  }

  // Pedidos
  @GetMapping("/pedido")
  public String pedido(Model model) {
    model.addAttribute("page", "pedido");
    return "pedido";
  }

  // Ayuda / Términos / Privacidad - Las dejo aca por si pinta hacerlas dsps 
  @GetMapping("/ayuda")
  public String ayuda() { return "ayuda"; }

  @GetMapping("/terminos")
  public String terminos() { return "terminos"; }

  @GetMapping("/privacidad")
  public String privacidad() { return "privacidad"; }
}