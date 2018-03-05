package Model;

/**
 * Created by angel on 14/12/2017.
 */
public class Shelve {

    private Product[] shelve;
    private int nextEmpty;

    private int x;
    private int y;

    public Shelve(int x, int y){
        shelve = new Product[3];
        nextEmpty = 0;

        this.x = x;
        this.y = y;
    }

    public boolean addProduct(Product p){
        if(nextEmpty == 3)
            return false;

        shelve[nextEmpty] = p;
        nextEmpty++;
        return true;
    }

    public Product get(int z){
        return shelve[z];
    }

    public Product[] getShelve(){
        return shelve;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String[] getDescription(){
        String[] descs = new String[3];

        for(int i = 0; i < 3; i++){
            if (shelve[i] != null) {
                descs[i] = "(" + x + ", "+ y +", "+ i + ")    =    " + shelve[i].getName() + "    -    " + shelve[i].getId();
            }else{
                descs[i] = "(" + x + ", "+ y +", "+ i + ")    =    EMPTY";
            }
        }

        return descs;
    }
}
