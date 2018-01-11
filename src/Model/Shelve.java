package Model;

/**
 * Created by angel on 14/12/2017.
 */
public class Shelve {

    private Product[] shelve;

    private int x;
    private int y;

    public Shelve(int x, int y){
        shelve = new Product[3];
        this.x = x;
        this.y = y;
    }

    public boolean addProduct(int z, Product p){
        if(shelve[z] != null)
            return false;

        shelve[z] = p;
        return true;
    }

    public Product get(int z){
        return shelve[z];
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
