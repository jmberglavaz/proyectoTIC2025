package um.edu.uy.jdftech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.edu.uy.jdftech.enums.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDGIDTO {
    private Long id;
    private LocalDateTime fecha;
    private String clienteNombre;
    private String clienteCedula;
    private String clienteEmail;
    private Double total;
    private EstadoPedido estado;
    private List<String> items;

    // Método helper para construir items
    public String getItemsResumen() {
        return String.join(", ", items);
    }
}