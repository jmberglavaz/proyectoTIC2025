package um.edu.uy.jdftech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BPSResponseDTO {
    private int cantidadFuncionarios;
    private String fechaConsulta;
}