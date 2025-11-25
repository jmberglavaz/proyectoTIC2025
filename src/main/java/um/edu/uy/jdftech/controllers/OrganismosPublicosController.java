package um.edu.uy.jdftech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.dto.BPSResponseDTO;
import um.edu.uy.jdftech.dto.TicketDGIDTO;
import um.edu.uy.jdftech.services.OrganismosPublicosService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organismos-publicos")
@RequiredArgsConstructor
public class OrganismosPublicosController {

    private final OrganismosPublicosService organismosPublicosService;

    /**
     * Servicio para DGI - Tickets por fecha específica
     */
    @GetMapping("/dgi/tickets")
    public ResponseEntity<List<TicketDGIDTO>> obtenerTicketsDGI(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<TicketDGIDTO> tickets = organismosPublicosService.obtenerTicketsDGI(fecha);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Servicio para DGI - Tickets por rango de fechas
     */
    @GetMapping("/dgi/tickets/rango")
    public ResponseEntity<?> obtenerTicketsDGIRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (!organismosPublicosService.validarRangoFechas(desde, hasta)) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "La fecha 'desde' no puede ser posterior a la fecha 'hasta'")
            );
        }

        List<TicketDGIDTO> tickets = organismosPublicosService.obtenerTicketsDGIPorRango(desde, hasta);
        return ResponseEntity.ok(tickets);
    }

    /**
     * Servicio para BPS - Cantidad de funcionarios
     */
    @GetMapping("/bps/funcionarios")
    public ResponseEntity<BPSResponseDTO> obtenerCantidadFuncionariosBPS() {
        BPSResponseDTO response = organismosPublicosService.obtenerCantidadFuncionariosBPS();
        return ResponseEntity.ok(response);
    }
}