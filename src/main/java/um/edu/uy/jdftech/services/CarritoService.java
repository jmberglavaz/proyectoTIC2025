package um.edu.uy.jdftech.services;

import org.springframework.stereotype.Service;
import um.edu.uy.jdftech.dto.CarritoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    private final List<CarritoItem> items = new ArrayList<>();

    public List<CarritoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(CarritoItem item) {
        items.add(item);
    }

    public void removeItem(String id) {
        items.removeIf(i -> i.getId().equals(id));
    }

    public void updateQuantity(String id, int cantidad) {
        Optional<CarritoItem> item = items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();
        item.ifPresent(i -> i.setCantidad(cantidad));
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
    }

    public void clear() {
        items.clear();
    }
}
