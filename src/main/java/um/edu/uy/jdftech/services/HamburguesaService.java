package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.uy.jdftech.entitites.Hamburguesa;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.entitites.tablasIntermedias.HamburguesaTopping;
import um.edu.uy.jdftech.repositories.HamburguesaRepository;
import um.edu.uy.jdftech.repositories.ToppingRepository;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HamburguesaService {
    @Autowired
    private HamburguesaRepository hamburguesaRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    public void agregarTopping(Long hamburguesaId, Long toppingId, int cantidad) {
        Hamburguesa hamburguesa = hamburguesaRepository.findById(hamburguesaId)
                .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));

        Topping topping = toppingRepository.findById(toppingId)
                .orElseThrow(() -> new RuntimeException("Topping no encontrado"));

        // Verificar si el topping ya existe en la hamburguesa
        for (HamburguesaTopping ht : hamburguesa.getHamburguesaToppings()) {
            if (ht.getTopping().getIdTopping().equals(toppingId)) {
                // Si ya existe, sumar la cantidad
                ht.setCantidad(ht.getCantidad() + cantidad);
                hamburguesaRepository.save(hamburguesa);
                return;
            }
        }

        // Si no existe, crear uno nuevo
        hamburguesa.agregarTopping(topping, cantidad);
        hamburguesaRepository.save(hamburguesa);
    }

    public void removerTopping(Long hamburguesaId, Long toppingId) {
        Hamburguesa hamburguesa = hamburguesaRepository.findById(hamburguesaId)
                .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));

        // Buscar y remover el topping
        List<HamburguesaTopping> toppings = hamburguesa.getHamburguesaToppings();
        for (int i = 0; i < toppings.size(); i++) {
            if (toppings.get(i).getTopping().getIdTopping().equals(toppingId)) {
                toppings.remove(i);
                hamburguesaRepository.save(hamburguesa);
                return;
            }
        }
    }

    public int getCantidadTopping(Long hamburguesaId, Long toppingId) {
        Hamburguesa hamburguesa = hamburguesaRepository.findById(hamburguesaId)
                .orElseThrow(() -> new RuntimeException("Hamburguesa no encontrada"));

        // Buscar la cantidad del topping
        for (HamburguesaTopping ht : hamburguesa.getHamburguesaToppings()) {
            if (ht.getTopping().getIdTopping().equals(toppingId)) {
                return ht.getCantidad();
            }
        }

        return 0; // Si no existe
    }

    public Hamburguesa crearHamburguesaConToppings(Map<Long, Integer> toppingsConCantidad) {
        Hamburguesa hamburguesa = new Hamburguesa();

        for (Map.Entry<Long, Integer> entry : toppingsConCantidad.entrySet()) {
            Topping topping = toppingRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Topping no encontrado: " + entry.getKey()));
            hamburguesa.agregarTopping(topping, entry.getValue());
        }

        return hamburguesaRepository.save(hamburguesa);
    }
}
