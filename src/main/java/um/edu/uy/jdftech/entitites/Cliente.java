package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Pedido> pedidos = new HashSet<>();

    public Cliente() {
    }

    // Método helper para mantener la consistencia
    public void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setClient(this);
    }

    public void removerPedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setClient(null);
    }
}
