package Model;

/**
 * Created by angel on 14/12/2017.
 */
public class Shelve {

    private Product[] shelve;

    public Shelve(){
        shelve = new Product[3];
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

}
