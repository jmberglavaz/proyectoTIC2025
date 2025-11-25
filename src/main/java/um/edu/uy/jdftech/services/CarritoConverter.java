package um.edu.uy.jdftech.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.dto.CarritoItemDTO;
import um.edu.uy.jdftech.entitites.*;

import java.util.List;

/**
 * Convierte CarritoItemDTO (en memoria) a entidades Pizza/Hamburguesa (BD)
 */
@Service
public class CarritoConverter {

    @Autowired
    private ToppingService toppingService;

    @Autowired
    private AderezoService aderezoService;

    /**
     * Convierte un CarritoItemDTO de tipo "pizza" a una entidad Pizza
     */
    public Pizza convertirAPizza(CarritoItemDTO dto) {
        if (!"pizza".equals(dto.getTipo())) {
            throw new IllegalArgumentException("El item no es una pizza");
        }

        Pizza pizza = new Pizza();
        pizza.setTamanio(dto.getTamanio());

        // Agregar masa (cantidad 1)
        Topping masa = toppingService.findByIdTopping(dto.getMasaId());
        pizza.agregarTopping(masa, 1);

        // Agregar salsa (cantidad 1)
        Topping salsa = toppingService.findByIdTopping(dto.getSalsaId());
        pizza.agregarTopping(salsa, 1);

        // Agregar queso (cantidad 1)
        Topping queso = toppingService.findByIdTopping(dto.getQuesoId());
        pizza.agregarTopping(queso, 1);

        // Agregar toppings extras (si hay)
        List<Long> toppingsIds = dto.getToppingsIds();
        if (toppingsIds != null && !toppingsIds.isEmpty()) {
            for (Long toppingId : toppingsIds) {
                Topping topping = toppingService.findByIdTopping(toppingId);
                pizza.agregarTopping(topping, 1);
            }
        }

        return pizza;
    }

    /**
     * Convierte un CarritoItemDTO de tipo "burger" a una entidad Hamburguesa
     */
    public Hamburguesa convertirAHamburguesa(CarritoItemDTO dto) {
        if (!"burger".equals(dto.getTipo())) {
            throw new IllegalArgumentException("El item no es una hamburguesa");
        }

        Hamburguesa hamburguesa = new Hamburguesa();
        hamburguesa.setCant_de_carnes(dto.getCantidadCarnes());

        // Calcular precio base según cantidad de carnes
        double precioBase = switch (dto.getCantidadCarnes()) {
            case 1 -> 310;
            case 3 -> 520;
            default -> 420; // 2 carnes
        };
        hamburguesa.setPrecio_base(precioBase);

        // Agregar tipo de carne (cantidad 1)
        Topping carne = toppingService.findByIdTopping(dto.getCarneId());
        hamburguesa.agregarTopping(carne, 1);

        // Agregar pan (cantidad 1)
        Topping pan = toppingService.findByIdTopping(dto.getPanId());
        hamburguesa.agregarTopping(pan, 1);

        // Agregar aderezos (si hay)
        List<Long> aderezosIds = dto.getAderezosIds();
        if (aderezosIds != null && !aderezosIds.isEmpty()) {
            for (Long aderezoId : aderezosIds) {
                Aderezo aderezo = aderezoService.getAderezoById(aderezoId);
                hamburguesa.agregarAderezo(aderezo, 1);
            }
        }

        // Agregar extras (si hay)
        List<Long> extrasIds = dto.getExtrasIds();
        if (extrasIds != null && !extrasIds.isEmpty()) {
            for (Long extraId : extrasIds) {
                Topping extra = toppingService.findByIdTopping(extraId);
                hamburguesa.agregarTopping(extra, 1);
            }
        }

        return hamburguesa;
    }
}
