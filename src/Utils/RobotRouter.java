package Utils;

import Model.Product;
import Model.Shelve;
import Model.Warehouse;
import View.WarehouseView;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;


/**
 *
 */
public class RobotRouter {

    /**
     *
     */
    private class RouterMark{
        int actualX;
        int actualY;
        int vActual;

        int lastMove;

        boolean[] products;

        boolean[][] steppedCells;

    }

    /**
     *
     */
    private RouterMark mark;

    /**
     *
     */
    private int[] config;

    /**
     *
     */
    private Product[] orders;

    /**
     *
     */
    private int vMillor;

    /**
     *
     */
    private int[] configMillor;

    /**
     *
     */
    private Warehouse wh;
    private WarehouseView wv;

    private HashMap<Integer, Integer> prodIndexes;

    /**
     *
     * @param wh
     * @param orders
     */
    public RobotRouter(Warehouse wh, Product[] orders, HashMap<Integer, Integer> orderIndexes, WarehouseView view){

        wv = view;

        prodIndexes = orderIndexes;

        mark = new RouterMark();
        this.wh = wh;

        mark.lastMove = -1;

        this.orders = orders;
        mark.products = new boolean[orders.length];

        mark.steppedCells = new boolean[wh.getWhMatrix().length][wh.getWhMatrix()[0].length];

        int configLength = wh.getWhMatrix().length * wh.getWhMatrix()[0].length;

        config = new int[configLength];
        for (int i=0; i < configLength; i++) {
            config[i] = -1;
        }


    }

    /**
     * Ejecuta el enrutamiento del robot para obtener el camino minimo a todos los productos
     * comprados
     */
    public void enrutaRobot() {
        vMillor = Integer.MAX_VALUE;
        mark.vActual = 0;

        mark.actualX = wh.getEntranceX();
        mark.actualY = wh.getEntranceY();

        routeRobot(config, 0, mark);
    }


    //---------------------------------------------------BACKTRACKING STUFF-------------------------------------------//

    /**
     * Procedimiento recursivo principal por bactracking para encontrar el camino mas corto del robot
     * @param x
     * @param k
     * @param m
     */
    private void routeRobot(int[] x, int k, RouterMark m){

        x[k] = -1;

        while(x[k] < 3){
            x[k]++;

            marcar(x, k, m);

            if (esMejorSolucion() && esBuena(x, k, m)) {
                System.out.println("Buena: "+Arrays.toString(x));
                wv.paintCell(mark.actualX, mark.actualY, Color.MAGENTA);

                if (esSolucion(x, k)) {
                    System.out.println("Solucion: "+Arrays.toString(x));
                    tratarSolucion(x);
                } else {
                    if(esMejorSolucion()) routeRobot(x, k + 1, m);
                }

                wv.paintCell(mark.actualX, mark.actualY, Color.WHITE);
            }


            demarcar(x, k, m);
        }

    }

    /**
     * Comprueba que la configuracion actual sea factible y/o completable como solucion
     * @param x
     * @param k
     * @param m
     * @return
     */
    private boolean esBuena(int[] x, int k, RouterMark m) {

        if(m.actualX < 0 || m.actualX > wh.getWhMatrix().length-1)
            return false;
        if(m.actualY < 0 || m.actualY > wh.getWhMatrix()[0].length-1)
            return false;

        if(wh.getShelve(m.actualX, m.actualY) != null)
            return false;

        if(mark.steppedCells[mark.actualX][mark.actualY])
            return false;

        if((m.lastMove == 0 && x[k] == 2)||(m.lastMove == 2 && x[k] == 0))
            return false;

        if((m.lastMove == 1 && x[k] == 3)||(m.lastMove == 3 && x[k] == 1))
            return false;

        if(wh.isInbounds(mark.actualX, mark.actualY))
            mark.steppedCells[mark.actualX][mark.actualY] = true;
        return true;
    }

    /**
     * Comprueba que la configuracion actual de camino pase por todos los productos que hay que coger para el pedido
     * @param x
     * @param k
     * @return
     */
    private boolean esSolucion(int[] x, int k) {
        boolean gotAllProducts = true;
        for(int i = 0; i < mark.products.length; i++)
            gotAllProducts = gotAllProducts && mark.products[i];

        return gotAllProducts;
    }

    /**
     * Almacena la mejor solucion encontrada hasta el momento
     * @param x configuracion solucion a almacenar
     */
    private void tratarSolucion(int[] x) {

        configMillor = Arrays.copyOf(x, x.length);
        vMillor = mark.vActual;

    }

    /**
     * Compara el valor de la solucion actual con el de la mejor encontrada hasta el momento
     * @return cierto si la actual es mejor que la almacenada como tal
     */
    private boolean esMejorSolucion() {
        return mark.vActual < vMillor;
    }

    /**
     * Restablece la marca de la solucion al estado anterior
     * @param x
     * @param k
     * @param m
     */
    private void demarcar(int[] x, int k, RouterMark m) {

        if(wh.isInbounds(m.actualX+1, m.actualY)
                && wh.getShelve(m.actualX+1, m.actualY) != null){
            Shelve s = wh.getShelve(m.actualX+1, m.actualY);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(m.actualX-1, m.actualY)
                && wh.getShelve(m.actualX-1, m.actualY) != null){
            Shelve s = wh.getShelve(m.actualX-1, m.actualY);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(m.actualX, m.actualY+1)
                && wh.getShelve(m.actualX, m.actualY+1) != null){
            Shelve s = wh.getShelve(m.actualX, m.actualY+1);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(m.actualX, m.actualY-1)
                && wh.getShelve(m.actualX, m.actualY-1) != null){
            Shelve s = wh.getShelve(m.actualX, m.actualY-1);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }

        if(wh.isInbounds(mark.actualX, mark.actualY))
            mark.steppedCells[mark.actualX][mark.actualY] = false;

        switch(x[k]){
            case 0:
                m.actualY++;
                break;

            case 1:
                m.actualX--;
                break;

            case 2:
                m.actualY--;
                break;

            case 3:
                m.actualX++;
                break;
        }
        m.vActual--;
    }

    /**
     * Establece la marca conforme la casilla en la que se encuentra el robot.
     *
     * @param x
     * @param k
     * @param m
     */
    private void marcar(int[] x, int k, RouterMark m) {
        m.vActual++;
        switch(x[k]){
            case 0:
                m.actualY--;
                break;

            case 1:
                m.actualX++;
                break;

            case 2:
                m.actualY++;
                break;

            case 3:
                m.actualX--;
                break;
        }

        if(wh.isInbounds(m.actualX+1, m.actualY)
                && wh.getShelve(m.actualX+1, m.actualY) != null){
            Shelve s = wh.getShelve(m.actualX+1, m.actualY);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
        if(wh.isInbounds(m.actualX-1, m.actualY)
                && wh.getShelve(m.actualX-1, m.actualY) != null){
            Shelve s = wh.getShelve(m.actualX-1, m.actualY);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
        if(wh.isInbounds(m.actualX, m.actualY+1)
                && wh.getShelve(m.actualX, m.actualY+1) != null){
            Shelve s = wh.getShelve(m.actualX, m.actualY+1);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
        if(wh.isInbounds(m.actualX, m.actualY-1)
                && wh.getShelve(m.actualX, m.actualY-1) != null){
            Shelve s = wh.getShelve(m.actualX, m.actualY-1);
            for(Product p : s.getShelve()){
                if (p != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
    }
}


