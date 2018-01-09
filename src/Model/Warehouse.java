package Model;

/**
 * Created by angel on 14/12/2017.
 */
public class Warehouse {

    private Shelve[][] wh;

    public Warehouse(int maxX, int maxY){
        wh = new Shelve[maxX][maxY];
    }

    public boolean addShelve(Shelve s, int x, int y){
        if(wh[x][y] != null){
            return false;
        }

        wh[x][y] = s;
        return true;
    }

    public Shelve getShelve(int x, int y) {
        return wh[x][y];
    }
}
