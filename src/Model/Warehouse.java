package Model;

import java.util.ArrayList;

/**
 * Created by angel on 14/12/2017.
 */
public class Warehouse {

    private ArrayList<Shelve> wh;
    private Shelve[][] whMatrix;

    private int entranceX;
    private int entranceY;

    public Warehouse(int maxX, int maxY, int entranceX, int entranceY){
        wh = new ArrayList<>();
        this.entranceX = entranceX;
        this.entranceY = entranceY;

        whMatrix = new Shelve[maxX][maxY];
    }

    public boolean addShelve(Shelve s){

        wh.add(s);
        whMatrix[s.getX()][s.getY()] = s;
        return true;
    }

    public ArrayList<Shelve> getWH() {
        return wh;
    }

    public Shelve getShelve(int x, int y){
        return whMatrix[x][y];
    }
}
