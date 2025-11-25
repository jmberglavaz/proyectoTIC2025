package um.edu.uy.jdftech.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.MedioDePago;
import um.edu.uy.jdftech.entitites.Usuario;
import um.edu.uy.jdftech.exceptions.InvalidCardException;
import um.edu.uy.jdftech.repositories.MedioDePagoRepository;
import um.edu.uy.jdftech.validators.ValidacionDeMetodoDePago;
import um.edu.uy.jdftech.validators.ValidationResult;

import um.edu.uy.jdftech.dto.TarjetaInfoDTO;
import um.edu.uy.jdftech.exceptions.EntityNotFoundException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedioDePagoService {
    private final MedioDePagoRepository medioDePagoRepository;
    private final ValidacionDeMetodoDePago validacionDeMetodoDePago;

    public MedioDePago save(MedioDePago medioDePago) throws InvalidCardException {
        ValidationResult validation = validacionDeMetodoDePago.validarTarjeta(medioDePago);

        if (!validation.isValid()) {
            throw new InvalidCardException("Medio de pago inválido: " + String.join(", ", validation.getErrors()));
        }
        return medioDePagoRepository.save(medioDePago); // Corregí el error: llamada recursiva
    }

    public TarjetaInfoDTO obtenerInfoTarjeta(Long numeroTarjeta) throws EntityNotFoundException {
        MedioDePago medio = medioDePagoRepository.findByCardNumber(numeroTarjeta)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró tarjeta con número: " + numeroTarjeta));

        Usuario cliente = medio.getCliente();

        return TarjetaInfoDTO.builder()
                .cardNumber(medio.getCardNumber())
                .firstNameOnCard(medio.getFirstNameOnCard())
                .lastNameOnCard(medio.getLastNameOnCard())
                .expirationDate(medio.getExpirationDate())
                // Datos del cliente desde Usuario
                .clienteId(cliente.getId())
                .clienteNombre(cliente.getFirstName())
                .clienteApellido(cliente.getLastName())
                .clienteEmail(cliente.getEmail())
                .clientePhoneNumber(cliente.getPhoneNumber())
                .clienteBirthDate(cliente.getBirthDate())
                .build();
    }

    public boolean existeTarjeta(Long numeroTarjeta) {
        return medioDePagoRepository.findByCardNumber(numeroTarjeta).isPresent();
    }

    // MedioDePagoService.java - Agregar estos métodos

    public List<MedioDePago> findByClienteId(Long clienteId) {
        return medioDePagoRepository.findByClienteId(clienteId);
    }

    public void deleteByCardNumber(Long cardNumber, Long clienteId) {
        MedioDePago medio = medioDePagoRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Medio de pago no encontrado"));

        if (!medio.getCliente().getId().equals(clienteId)) {
            throw new RuntimeException("No autorizado");
        }

        medioDePagoRepository.delete(medio);
    }
}