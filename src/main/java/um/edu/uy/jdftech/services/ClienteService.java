package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.repositories.ClienteRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public Cliente crear(Cliente nuevoCliente) {
        if (clienteRepository.existsById(nuevoCliente.getId())) {
            throw new IllegalArgumentException("El cliente con cédula " + nuevoCliente.getId() + " ya existe");
        }
        return clienteRepository.save(nuevoCliente);
    }

    public Cliente findById(Long idCliente) {
        return clienteRepository.findById(idCliente).orElseThrow(() -> new EntityNotFoundException("El cliente con cédula " + idCliente + "no existe"));
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente update(Long idCliente, Cliente clienteActualizado) {
        Cliente cliente = findById(idCliente);
        cliente.setFirstName(clienteActualizado.getFirstName());
        cliente.setLastName(clienteActualizado.getLastName());
        cliente.setBirthDate(clienteActualizado.getBirthDate());
        return clienteRepository.save(cliente);
    }

    public void delete(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new EntityNotFoundException("El cliente con cédula " + idCliente + "no fue encontrado");
        }
        clienteRepository.deleteById(idCliente);
    }

    public List<Cliente> findByFullName(String firstName, String lastName) {
        return clienteRepository.findByFullNameContaining(firstName + " " + lastName);
    }


}
