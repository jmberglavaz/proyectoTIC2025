package um.edu.uy.jdftech.entitites;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import um.edu.uy.jdftech.entitites.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Pedido> pedidos = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Direccion> direcciones = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MedioDePago> mediosDePago = new ArrayList<>();

    // Métodos helper
    public void agregarDireccion(Direccion direccion) {
        direcciones.add(direccion);
        direccion.setCliente(this);
    }

    public void eliminarDireccion(Direccion direccion) {
        direcciones.remove(direccion);
        direccion.setCliente(null);
    }

    public void agregarPedido(Pedido pedido) {
        pedidos.add(pedido);
        pedido.setClient(this);
    }

    public void removerPedido(Pedido pedido) {
        pedidos.remove(pedido);
        pedido.setClient(null);
    }

    public void agregarMedioDePago(MedioDePago medioDePago) {
        mediosDePago.add(medioDePago);
        medioDePago.setCliente(this);
    }

    public void removerMedioDePago(MedioDePago medioDePago) {
        mediosDePago.remove(medioDePago);
        medioDePago.setCliente(null);
    }
}