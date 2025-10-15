package sg.com.aori.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sku")
public class Sku {
    @Id
    private String sku;
    private int quantity;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
