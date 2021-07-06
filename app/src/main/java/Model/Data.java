package Model;

public class Data {
    String item;
    String quantity;
    String date;
    String id;
    public Data(){

    }

    public Data(String item, String quantity, String date, String id) {
        this.item = item;
        this.quantity = quantity;
        this.date = date;
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
