package um.edu.uy.jdftech.entitites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    @Id
    @Column(name = "ADMIN_ID")
    private Long adminId;

    private Usuario usuario;
}
