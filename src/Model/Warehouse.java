package Model;

import java.util.ArrayList;

/**
 * Created by angel on 14/12/2017.
 */
public class Warehouse {

    private ArrayList<Shelve> wh;

    private int entranceX;
    private int entranceY;

    public Warehouse(int maxX, int maxY, int entranceX, int entranceY){
        wh = new ArrayList<>();
        this.entranceX = entranceX;
        this.entranceY = entranceY;
    }

    public boolean addShelve(Shelve s){

        wh.add(s);
        return true;
    }

    public ArrayList<Shelve> getWH() {
        return wh;
    }
}
