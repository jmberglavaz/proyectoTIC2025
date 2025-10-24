package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Cliente extends Usuario {
}
