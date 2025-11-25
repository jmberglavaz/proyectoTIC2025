package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Administrador;
import um.edu.uy.jdftech.repositories.AdministradorRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdministradorService {
    private final AdministradorRepository administradorRepository;

    public Administrador crear(Administrador nuevoAdministrador) {
        if (administradorRepository.existsById(nuevoAdministrador.getId())) {
            throw new IllegalArgumentException("El administrador con cédula " + nuevoAdministrador.getId() + " ya existe");
        }
        return administradorRepository.save(nuevoAdministrador);
    }

    public Administrador findById(Long idAdministrador) {
        return administradorRepository.findById(idAdministrador).orElseThrow(() -> new EntityNotFoundException("El administrador con cédula " + idAdministrador + "no existe"));
    }

    public List<Administrador> findAll() {
        return administradorRepository.findAll();
    }

    public Administrador update(Long idAdministrador, Administrador administradorActualizado) {
        Administrador administrador = findById(idAdministrador);
        administrador.setFirstName(administradorActualizado.getFirstName());
        administrador.setLastName(administradorActualizado.getLastName());
        administrador.setBirthDate(administradorActualizado.getBirthDate());
        return administradorRepository.save(administrador);
    }

    public void delete(Long idAdministrador) {
        if (!administradorRepository.existsById(idAdministrador)) {
            throw new EntityNotFoundException("El administrador con cédula " + idAdministrador + "no fue encontrado");
        }
        administradorRepository.deleteById(idAdministrador);
    }

    public List<Administrador> findByFullName(String firstName, String lastName) {
        return administradorRepository.findByFullNameContaining(firstName + " " + lastName);
    }

    public boolean validarLogin(Long cedula, String password) {
        try {
            Administrador administrador = findById(cedula);
            return administrador.getPassword().equals(password);
        } catch (Exception e) {
            return false;
        }
    }

}
