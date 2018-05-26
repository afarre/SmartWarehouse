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

        public RouterMark(){}

        public RouterMark(RouterMark rm){
            this.actualX = rm.actualX;
            this.actualY = rm.actualY;
            this.vActual = rm.vActual;

            this.lastMove = rm.lastMove;

            this.steppedCells = Arrays.copyOf(rm.steppedCells, rm.steppedCells.length);
            this.products = Arrays.copyOf(rm.products, rm.products.length);
        }
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

    private boolean[] configMillorProducts;

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

        mark.vActual = 0;

        mark.actualX = wh.getEntranceX();
        mark.actualY = wh.getEntranceY();

        vMillor = (wh.getWhMatrix().length*wh.getWhMatrix()[0].length)/3;

        long initTime = System.nanoTime();
        routeRobot(config, 0, mark);
        long executionTime = System.nanoTime()-initTime;

        System.out.println();
        System.out.println("Enrutado en " + executionTime/1000 + " us");
        System.out.println("Mejor solucion: "+Arrays.toString(configMillor));
        System.out.println("Productos cogidos: "+Arrays.toString(configMillorProducts));

        wv.setTrackCost(vMillor);

        int i = 0;
        int y = wh.getEntranceY();
        int x = wh.getEntranceX();

        for (int j = 0; j < vMillor; j++){
            switch(configMillor[i]){
                case 0:
                    y--;
                    break;

                case 1:
                    x++;
                    break;

                case 2:
                    y++;
                    break;

                case 3:
                    x--;
            }
            wv.paintCell(x, y, Color.RED);
            i++;
        }
    }


    //---------------------------------------------------BACKTRACKING STUFF-------------------------------------------//

    /**
     * Procedimiento recursivo principal por backtracking para encontrar el camino mas corto del robot
     * @param x
     * @param k
     */
    private void routeRobot(int[] x, int k, RouterMark rMark){

        RouterMark mark = new RouterMark(rMark);

        x[k] = -1;
        while(x[k] < 3){
            x[k]++;
            marcar(x, k, mark);
            if (esMejorSolucion(mark) && esBuena(x, k, mark)) {
                wv.paintCell(mark.actualX, mark.actualY, Color.MAGENTA);
                if (esSolucion(x, k, mark)) {
                    System.out.println("Solución");
                    System.out.println("Con valor = "+ mark.vActual);
                    tratarSolucion(x, mark);
                } else {

                    if(esMejorSolucion(mark)) routeRobot(x, k + 1, mark);
                }
                wv.paintCell(mark.actualX, mark.actualY, Color.WHITE);
            }
            desmarcar(x, k, mark);
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

        if(m.actualX < 0 || m.actualX > wh.getWhMatrix().length-1) {
            return false;
        }
        if(m.actualY < 0 || m.actualY > wh.getWhMatrix()[0].length-1) {
            return false;
        }
        if(wh.getShelve(m.actualX, m.actualY) != null) {
            return false;
        }
        if(mark.steppedCells[m.actualX][m.actualY])
            return false;

        if((m.lastMove == 0 && x[k] == 2)||(m.lastMove == 2 && x[k] == 0))
            return false;

        if((m.lastMove == 1 && x[k] == 3)||(m.lastMove == 3 && x[k] == 1))
            return false;

        return true;
    }

    /**
     * Comprueba que la configuracion actual de camino pase por todos los productos que hay que coger para el pedido
     * @param x
     * @param k
     * @return
     */
    private boolean esSolucion(int[] x, int k, RouterMark mark) {
        boolean gotAllProducts = true;
        for(int i = 0; i < mark.products.length; i++)
            gotAllProducts = gotAllProducts && mark.products[i];

        return gotAllProducts;
    }

    /**
     * Almacena la mejor solucion encontrada hasta el momento
     * @param x configuracion solucion a almacenar
     */
    private void tratarSolucion(int[] x, RouterMark mark) {
        configMillor = Arrays.copyOf(x, x.length);
        vMillor = mark.vActual;
        configMillorProducts = Arrays.copyOf(mark.products, mark.products.length);
    }

    /**
     * Compara el valor de la solucion actual con el de la mejor encontrada hasta el momento
     * @return cierto si la actual es mejor que la almacenada como tal
     */
    private boolean esMejorSolucion(RouterMark m) {
        return m.vActual < vMillor;
    }

    /**
     * Restablece la marca de la solucion al estado anterior
     * @param x
     * @param k
     * @param mark
     */
    private void desmarcar(int[] x, int k, RouterMark mark) {
        if(wh.isInbounds(mark.actualX + 1, mark.actualY)
                && wh.getShelve(mark.actualX + 1, mark.actualY) != null){
            Shelve s = wh.getShelve(mark.actualX + 1, mark.actualY);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(mark.actualX - 1, mark.actualY)
                && wh.getShelve(mark.actualX - 1, mark.actualY) != null){
            Shelve s = wh.getShelve(mark.actualX - 1, mark.actualY);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(mark.actualX, mark.actualY + 1)
                && wh.getShelve(mark.actualX, mark.actualY + 1) != null){
            Shelve s = wh.getShelve(mark.actualX, mark.actualY + 1);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }
        if(wh.isInbounds(mark.actualX, mark.actualY - 1)
                && wh.getShelve(mark.actualX, mark.actualY - 1) != null){
            Shelve s = wh.getShelve(mark.actualX, mark.actualY - 1);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = false;
            }
        }

        switch(x[k]){
            case 0:
                mark.actualY++;
                break;

            case 1:
                mark.actualX--;
                break;

            case 2:
                mark.actualY--;
                break;

            case 3:
                mark.actualX++;
                break;
        }

        if(wh.isInbounds(mark.actualX, mark.actualY)) {
            mark.steppedCells[mark.actualX][mark.actualY] = false;
        }

        mark.vActual--;
        if(k-1 > -1)
            mark.lastMove = x[k-1];

    }

    /**
     * Establece la marca conforme la casilla en la que se encuentra el robot.
     *  @param x
     * @param k
     * @param mark
     */
    private void marcar(int[] x, int k, RouterMark mark) {
        mark.vActual++;
        mark.lastMove = x[k];

        if(wh.isInbounds(mark.actualX, mark.actualY)){
            mark.steppedCells[mark.actualX][mark.actualY] = true;
        }

        switch(x[k]){
            case 0:
                mark.actualY--;
                break;

            case 1:
                mark.actualX++;
                break;

            case 2:
                mark.actualY++;
                break;

            case 3:
                mark.actualX--;
                break;
        }

        if(wh.isInbounds(mark.actualX + 1, mark.actualY)
                && wh.getShelve(mark.actualX + 1, mark.actualY) != null){
            Shelve s = wh.getShelve(mark.actualX + 1, mark.actualY);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
        if(wh.isInbounds(mark.actualX - 1, mark.actualY)
                && wh.getShelve(mark.actualX - 1, mark.actualY) != null){
            Shelve s = wh.getShelve(mark.actualX - 1, mark.actualY);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null) {
                    mark.products[prodIndexes.get(p.getId())] = true;
                }
            }
        }
        if(wh.isInbounds(mark.actualX, mark.actualY + 1)
                && wh.getShelve(mark.actualX, mark.actualY + 1) != null){
            Shelve s = wh.getShelve(mark.actualX, mark.actualY + 1);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
        if(wh.isInbounds(mark.actualX, mark.actualY - 1)
                && wh.getShelve(mark.actualX, mark.actualY - 1) != null){
            Shelve s = wh.getShelve(mark.actualX, mark.actualY - 1);
            for(Product p : s.getShelve()){
                if (p != null && prodIndexes.get(p.getId()) != null)
                    mark.products[prodIndexes.get(p.getId())] = true;
            }
        }
    }
}


