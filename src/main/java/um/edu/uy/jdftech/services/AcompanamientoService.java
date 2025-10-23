package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Acompanamiento;
import um.edu.uy.jdftech.repositories.AcompanamientoRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AcompanamientoService {
    private final AcompanamientoRepository acompanamientoRepository;

    public Acompanamiento createNewAcompanamiento(Acompanamiento acompanamiento) {
        return acompanamientoRepository.save(acompanamiento);
    }

    public Acompanamiento getAcompanamientoById(Long id) {
        return acompanamientoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El acompañamiento con el " + id + " no fue encontrada"));
    }

    public List<Acompanamiento> findAll() {
        return acompanamientoRepository.findAll();
    }

    public Acompanamiento updateAcompanamiento(Long id, Acompanamiento acompanamientoActualizada) {
        Acompanamiento acompanamiento = acompanamientoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("El acompañamiento con el " + id + " no se pudo actualizar porque no fue encontrada"));
        acompanamiento.setName(acompanamientoActualizada.getName());
        acompanamiento.setPrice(acompanamientoActualizada.getPrice());
        acompanamiento.setSize(acompanamientoActualizada.getSize());
        return acompanamientoRepository.save(acompanamiento);
    }

    public void deleteAcompanamiento(Long id) {
        if (!acompanamientoRepository.existsById(id)) {
            throw new EntityNotFoundException("El acompañamiento con el id " + id + " no existe");
        }
        acompanamientoRepository.deleteById(id);
    }

    public List<Acompanamiento> findBySize(String size) {
        return acompanamientoRepository.findBySize(size);
    }

    public List<Acompanamiento> findByName(String name) {
        return acompanamientoRepository.findByNameContainingIgnoreCase(name);
    }
}
