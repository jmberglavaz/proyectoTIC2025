package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Pizza;
import um.edu.uy.jdftech.repositories.PizzaRepository;
import java.util.List;

@Service
public class PizzaService {

    @Autowired
    private PizzaRepository pizzaRepository;

    public List<Pizza> listarPizzas() {
        return pizzaRepository.findAll();
    }

    public Pizza crearPizza(Pizza pizza) {
        double total = pizza.getPrecioTotal();
        pizza.setPrecio(total);
        return pizzaRepository.save(pizza);
    }

    public void eliminarPizza(Long id) {
        pizzaRepository.deleteById(id);
    }
}
