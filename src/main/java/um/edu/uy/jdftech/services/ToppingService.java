package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import um.edu.uy.jdftech.entitites.Topping;
import um.edu.uy.jdftech.repositories.ToppingRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ToppingService {

    @Autowired
    private ToppingRepository toppingRepository;

    public Topping findByIdTopping(Long idTopping) {
        return toppingRepository.findByIdTopping(idTopping).orElseThrow(() -> new EntityNotFoundException("El topping con id " + idTopping + "no existe"));
    }

    List<Topping> verToppingsAgregadosRecientemente() {
        return toppingRepository.encontrarUltimos10ToppingsAgregados();
    }

    List<Topping> verToppingsDeTipo(char tipo) {
        return toppingRepository.encontrarToppingsDeTipo(tipo);
    }

    List<Topping> verToppingsDesdeHastaFecha(LocalDateTime from, LocalDateTime to) {
        return toppingRepository.encontrarDesdeHastaFecha(from, to);
    }

    public Topping crear(Topping nuevoTopping) {
        return toppingRepository.save(nuevoTopping);
    }

    public Topping update(Long idTopping, Topping toppingActualizado) {
        Topping topping = findByIdTopping(idTopping);
        return toppingRepository.save(topping);
    }

    public void delete(Long idTopping) {
        if (!toppingRepository.existsById(idTopping)) {
            throw new EntityNotFoundException("El topping con id " + idTopping + "no fue encontrado");
        }
        toppingRepository.deleteById(idTopping);
    }

    public List<Topping> findByName(String nombre) {
        return toppingRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
