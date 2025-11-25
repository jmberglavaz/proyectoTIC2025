package um.edu.uy.jdftech.enums;

public enum EstadoPedido {
    EN_COLA("En cola"),
    EN_PREPARACION("En preparación"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String displayName;

    EstadoPedido(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
