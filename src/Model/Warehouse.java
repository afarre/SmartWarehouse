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

    public Shelve[][] getWhMatrix() {
        return whMatrix;
    }

    public Shelve getShelve(int x, int y){
        return whMatrix[x][y];
    }

    /**
     * Retorna cierto si esta dentro del tablero
     * @param x
     * @param y
     * @return
     */
    public boolean isInbounds(int x, int y){
        return x >= 0 && x < whMatrix.length
                && y >=0 && y < whMatrix[0].length;
    }

    public int getEntranceX(){
        return entranceX;
    }

    public int getEntranceY(){
        return entranceY;
    }
}
