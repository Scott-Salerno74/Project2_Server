import java.io.Serializable;

public class Item implements Serializable {

    private double price;
    private String name;
    private int stock;

    public Item(String name, double price, int stock){
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int newStock) {
        stock = newStock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price1) {
        price = price1;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

}
