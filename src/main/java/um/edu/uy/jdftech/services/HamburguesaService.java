package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Hamburguesa;
import um.edu.uy.jdftech.repositories.HamburguesaRepository;
import java.util.List;

@Service
public class HamburguesaService {

    @Autowired
    private HamburguesaRepository hamburguesaRepository;

    public List<Hamburguesa> listarHamburguesas() {
        return hamburguesaRepository.findAll();
    }

    public Hamburguesa crearHamburguesa(Hamburguesa hamburguesa) {
        double total = hamburguesa.getPrecioTotal();
        hamburguesa.setPrecio_base(total);
        return hamburguesaRepository.save(hamburguesa);
    }

    public void eliminarHamburguesa(Long id) {
        hamburguesaRepository.deleteById(id);
    }
}
