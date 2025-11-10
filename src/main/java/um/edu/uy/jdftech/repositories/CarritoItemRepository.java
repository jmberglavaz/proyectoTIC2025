package um.edu.uy.jdftech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.edu.uy.jdftech.entitites.Carrito;
import um.edu.uy.jdftech.entitites.CarritoItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    // Buscar todos los items de un carrito
    List<CarritoItem> findByCarrito(Carrito carrito);
    
    // Buscar un item específico por producto y tipo
    Optional<CarritoItem> findByCarritoAndProductoIdAndTipoProducto(
            Carrito carrito, 
            Long productoId, 
            String tipoProducto
    );
    
    // Eliminar todos los items de un carrito
    void deleteByCarrito(Carrito carrito);
}