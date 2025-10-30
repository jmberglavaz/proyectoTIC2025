package um.edu.uy.jdftech.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.Cliente;
import um.edu.uy.jdftech.entitites.Direccion;
import um.edu.uy.jdftech.repositories.ClienteRepository;
import um.edu.uy.jdftech.repositories.DireccionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final PasswordEncoder passwordEncoder;

    public Cliente registrarCliente(Long id, String firstName, String lastName, Date birthDate, String email, String password, String phoneNumber, String streetName, String doorNumber, String addressIndications, String alias) {
        if (clienteRepository.existsById(id)) {
            throw new EntityExistsException("El cliente con cédula " + id + " ya fue registrado.");
        }

        Cliente cliente = Cliente.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .direcciones(new HashSet<>())
                .pedidos(new HashSet<>())
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);

        Direccion direccionPrincipal = Direccion.builder()
                .streetName(streetName)
                .doorNumber(doorNumber)
                .indications(addressIndications)
                .isDefect(true)
                .usuario(clienteGuardado)
                .build();

        if (alias.isEmpty()) {
            direccionPrincipal.setAlias(streetName + " " + doorNumber);
        } else {
            direccionPrincipal.setAlias(alias);
        }

        direccionRepository.save(direccionPrincipal);

        return clienteGuardado;
    }

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
        cliente.setEmail(clienteActualizado.getEmail());
        cliente.setPhoneNumber(clienteActualizado.getPhoneNumber());

        if (clienteActualizado.getPassword() != null && !clienteActualizado.getPassword().isEmpty()) {
            cliente.setPassword(passwordEncoder.encode(clienteActualizado.getPassword()));
        }
        return clienteRepository.save(cliente);
    }

    public void delete(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new EntityNotFoundException("El cliente con cédula " + idCliente + "no fue encontrado");
        }
        Cliente clienteAEliminar = findById(idCliente);
        direccionRepository.deleteAll(clienteAEliminar.getDirecciones());
        clienteRepository.deleteById(idCliente);
    }

    public List<Cliente> findByFullName(String firstName, String lastName) {
        return clienteRepository.findByFullNameContaining(firstName + " " + lastName);
    }

    public boolean validarLogin(Long cedula, String password) {
        try {
            Cliente cliente = findById(cedula);
            return passwordEncoder.matches(password, cliente.getPassword());
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
