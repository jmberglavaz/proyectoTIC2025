package um.edu.uy.jdftech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.dto.BPSResponseDTO;
import um.edu.uy.jdftech.dto.TicketDGIDTO;
import um.edu.uy.jdftech.entitites.Administrador;
import um.edu.uy.jdftech.repositories.AdministradorRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganismosPublicosService {

    private final PedidoService pedidoService;
    private final AdministradorRepository administradorRepository;

    /**
     * Servicio para DGI - Obtener tickets por fecha
     */
    public List<TicketDGIDTO> obtenerTicketsDGI(LocalDate fecha) {
        LocalDateTime fechaConsulta = fecha.atStartOfDay();
        return pedidoService.obtenerTicketsPorFecha(fechaConsulta);
    }

    /**
     * Servicio para DGI - Obtener tickets por rango de fechas
     */
    public List<TicketDGIDTO> obtenerTicketsDGIPorRango(LocalDate desde, LocalDate hasta) {
        LocalDateTime desdeDateTime = desde.atStartOfDay();
        LocalDateTime hastaDateTime = hasta.atTime(23, 59, 59);
        return pedidoService.obtenerTicketsPorRangoFechas(desdeDateTime, hastaDateTime);
    }

    /**
     * Servicio para BPS - Contar cantidad de funcionarios
     */
    public BPSResponseDTO obtenerCantidadFuncionariosBPS() {
        List<Administrador> administradores = administradorRepository.findAll();

        return BPSResponseDTO.builder()
                .cantidadFuncionarios(administradores.size())
                .fechaConsulta(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();
    }

    /**
     * Método auxiliar para validar fechas
     */
    public boolean validarRangoFechas(LocalDate desde, LocalDate hasta) {
        return !desde.isAfter(hasta);
    }
}