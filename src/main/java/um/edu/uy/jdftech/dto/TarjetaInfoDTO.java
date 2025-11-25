package um.edu.uy.jdftech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaInfoDTO {
    // Datos de la tarjeta
    private Long cardNumber;
    private String firstNameOnCard;
    private String lastNameOnCard;
    private Date expirationDate;

    // Datos del cliente (desde Usuario)
    private Long clienteId;
    private String clienteNombre;
    private String clienteApellido;
    private String clienteEmail;
    private String clientePhoneNumber;
    private LocalDate clienteBirthDate;
}