package um.edu.uy.jdftech.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.entitites.MedioDePago;
import um.edu.uy.jdftech.exceptions.InvalidCardException;
import um.edu.uy.jdftech.repositories.MedioDePagoRepository;
import um.edu.uy.jdftech.validators.ValidacionDeMetodoDePago;
import um.edu.uy.jdftech.validators.ValidationResult;

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
        return save(medioDePago);
    }
}
