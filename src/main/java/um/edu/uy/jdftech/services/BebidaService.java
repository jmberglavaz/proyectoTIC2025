package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Bebida;
import um.edu.uy.jdftech.repositories.BebidaRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BebidaService {
    private final BebidaRepository bebidaRepository;

    public Bebida createNewBebida(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    public Bebida getBebidaById(Long id) {
        return bebidaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("La bebida con el " + id + " no fue encontrada"));
    }

    public List<Bebida> findAll() {
        return bebidaRepository.findAll();
    }

    public Bebida updateBebida(Long id, Bebida bebidaActualizada) {
        Bebida bebida = bebidaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("La bebida con el " + id + " no se pudo actualizar porque no fue encontrada"));
        bebida.setName(bebidaActualizada.getName());
        bebida.setPrice(bebidaActualizada.getPrice());
        bebida.setSize(bebidaActualizada.getSize());
        return bebidaRepository.save(bebida);
    }

    public void deleteBebida(Long id) {
        if (!bebidaRepository.existsById(id)) {
            throw new EntityNotFoundException("La bebida con el id " + id + " no existe");
        }
        bebidaRepository.deleteById(id);
    }

    public List<Bebida> findBySize(String size) {
        return bebidaRepository.findBySize(size);
    }

    public List<Bebida> findByName(String name) {
        return bebidaRepository.findByNameContainingIgnoreCase(name);
    }
}
