package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.entitites.Pizza;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.entitites.tablasIntermedias.PizzaTopping;
import um.edu.uy.jdftech.repositories.PizzaRepository;
import um.edu.uy.jdftech.repositories.ToppingRepository;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PizzaService {
    @Autowired
    private PizzaRepository pizzaRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    public void agregarTopping(Long pizzaId, Long toppingId, int cantidad) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));

        Topping topping = toppingRepository.findById(toppingId)
                .orElseThrow(() -> new RuntimeException("Topping no encontrado"));

        // Verificar si el topping ya existe en la pizza
        for (PizzaTopping pt : pizza.getPizzaToppings()) {
            if (pt.getTopping().getIdTopping().equals(toppingId)) {
                // Si ya existe, sumar la cantidad
                pt.setCantidad(pt.getCantidad() + cantidad);
                pizzaRepository.save(pizza);
                return;
            }
        }

        // Si no existe, crear uno nuevo
        pizza.agregarTopping(topping, cantidad);
        pizzaRepository.save(pizza);
    }

    public void removerTopping(Long pizzaId, Long toppingId) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));

        // Buscar y remover el topping
        List<PizzaTopping> toppings = pizza.getPizzaToppings();
        for (int i = 0; i < toppings.size(); i++) {
            if (toppings.get(i).getTopping().getIdTopping().equals(toppingId)) {
                toppings.remove(i);
                pizzaRepository.save(pizza);
                return;
            }
        }
    }

    public int getCantidadTopping(Long pizzaId, Long toppingId) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new RuntimeException("Pizza no encontrada"));

        // Buscar la cantidad del topping
        for (PizzaTopping pt : pizza.getPizzaToppings()) {
            if (pt.getTopping().getIdTopping().equals(toppingId)) {
                return pt.getCantidad();
            }
        }

        return 0; // Si no existe
    }

    public Pizza crearPizzaConToppings(Map<Long, Integer> toppingsConCantidad) {
        Pizza pizza = new Pizza();

        for (Map.Entry<Long, Integer> entry : toppingsConCantidad.entrySet()) {
            Topping topping = toppingRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Topping no encontrado: " + entry.getKey()));
            pizza.agregarTopping(topping, entry.getValue());
        }

        return pizzaRepository.save(pizza);
    }
}
