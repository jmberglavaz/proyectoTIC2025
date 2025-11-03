package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Aderezo;
import um.edu.uy.jdftech.repositories.AderezoRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AderezoService {
    private final AderezoRepository aderezoRepository;

    public Aderezo createNewAderezo(Aderezo aderezo) {
        return aderezoRepository.save(aderezo);
    }

    public Aderezo getAderezoById(Long id) {
        return aderezoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El aderezo con id " + id + " no fue encontrado"));
    }

    public List<Aderezo> findAll() {
        return aderezoRepository.findAll();
    }

    public Aderezo updateAderezo(Long id, Aderezo aderezoActualizado) {
        Aderezo aderezo = getAderezoById(id);
        aderezo.setNombre(aderezoActualizado.getNombre());
        aderezo.setPrecio(aderezoActualizado.getPrecio());
        return aderezoRepository.save(aderezo);
    }

    public void deleteAderezo(Long id) {
        if (!aderezoRepository.existsById(id)) {
            throw new EntityNotFoundException("El aderezo con id " + id + " no existe");
        }
        aderezoRepository.deleteById(id);
    }

    public List<Aderezo> findByNombre(String nombre) {
        return aderezoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}
