package um.edu.uy.jdftech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.edu.uy.jdftech.dto.TarjetaInfoDTO;
import um.edu.uy.jdftech.exceptions.EntityNotFoundException;
import um.edu.uy.jdftech.services.MedioDePagoService;

@RestController
@RequestMapping("/api/tarjetas")
@RequiredArgsConstructor
public class TarjetaController {

    private final MedioDePagoService medioDePagoService;

    @GetMapping("/{numeroTarjeta}")
    public ResponseEntity<TarjetaInfoDTO> obtenerInfoTarjeta(@PathVariable Long numeroTarjeta) throws EntityNotFoundException {
        TarjetaInfoDTO info = medioDePagoService.obtenerInfoTarjeta(numeroTarjeta);
        return ResponseEntity.ok(info);
    }

    // Endpoint adicional para verificar existencia
    @GetMapping("/{numeroTarjeta}/existe")
    public ResponseEntity<Boolean> existeTarjeta(@PathVariable Long numeroTarjeta) {
        boolean existe = medioDePagoService.existeTarjeta(numeroTarjeta);
        return ResponseEntity.ok(existe);
    }
}
