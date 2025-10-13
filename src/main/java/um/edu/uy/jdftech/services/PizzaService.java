package um.edu.uy.jdftech.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entities.Pizza;
import um.edu.uy.jdftech.entities.Topping;
import um.edu.uy.jdftech.exceptions.EntityNotFoundException;
import um.edu.uy.jdftech.repositories.PizzaRepository;
import um.edu.uy.jdftech.repositories.ToppingRepository;


@Service
@Transactional
public class PizzaService {

    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    public Topping agregarToppingAPizza(String nombreTopping, String nombrePizza) {
        Topping topping = toppingRepository.encontrarToppingsPorNombre(nombreTopping)
        if topping == null:
            throw new EntityNotFoundException("Topping no encontrado"));
        Pizza pizza = pizzaRepository.encontrarPizzasPorNombre(nombrePizza).orElseThrow(() -> new EntityNotFoundException("Pizza no encontrada"));
        pizza.agregarTopping(topping);
        return toppingRepository.save(topping);
    }
}
