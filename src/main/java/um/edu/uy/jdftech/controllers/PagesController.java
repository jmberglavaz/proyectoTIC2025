package um.edu.uy.jdftech.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

  // Home
  @GetMapping("/")
  public String index() {
    return "index";
  }

  // Crear pizza
  @GetMapping("/crear-pizza")
  public String crearPizza() {
    return "crear-pizza";
  }

  // Crear hamburguesa
  @GetMapping("/crear-burger")
  public String crearBurger() {
    return "crear-burger";
  }

  // Carrito
  //@GetMapping("/carrito")
  //public String carrito() {
  //  return "carrito";
  //}

  // Pedidos
  //@GetMapping("/pedido")
  //public String pedido() {
  //  return "pedido";
  //}

  // Ayuda / Términos / Privacidad - Las dejo aca por si pinta hacerlas dsps 
  @GetMapping("/ayuda")
  public String ayuda() { return "ayuda"; }

  @GetMapping("/terminos")
  public String terminos() { return "terminos"; }

  @GetMapping("/privacidad")
  public String privacidad() { return "privacidad"; }
}