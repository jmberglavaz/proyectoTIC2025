package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "medios_de_pago")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedioDePago {
    @Id
    @Column(name = "CARD_NUMBER")
    private Long cardNumber;

    @Column(name = "CVV", nullable = false)
    private int cvv;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstNameOnCard;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastNameOnCard;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRATION_DATE", nullable = false)
    private Date expirationDate;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}