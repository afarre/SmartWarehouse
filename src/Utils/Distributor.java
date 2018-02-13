package Utils;

import Model.Warehouse;
import View.WarehouseView;
import com.sun.javafx.geom.Vec2d;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase que implementa un distribuidor de productos en el almacen segun las probabilidades que tengan entre ellos de
 * aparecer juntos en un mismo pedido. Utiliza un algoritmo por backtracking con diferentes mejoras de eficiencia
 * como son marcage y poda basada en la mejor solucion encontrada hasta el momento.
 * @author Alex Vogel
 *
 */
public class Distributor extends  Exception{

    private Warehouse wh;
    private int whSize;

    private boolean[][] map;

    private float[][] probabilities;


    private HashMap<Integer, Integer> indexes;
    private int numberOfProducts;

    private WarehouseView warehouseView;

    public Distributor(Warehouse wh, boolean[][] map, float[][] probabilities,
                       HashMap<Integer, Integer> productIndexes, WarehouseView view) {

        this.wh = wh;
        whSize = wh.getWH().size();
        this.map = map;
        this.probabilities = probabilities;

        this.indexes = productIndexes;
        numberOfProducts = productIndexes.size();

        warehouseView = view;
    }

    public void distribute(){
        int[] configuracion = new int[numberOfProducts],
                marcage = new int[whSize + 1];

        int nivel = 0;
        xMillor = null;
        vMillorDist = Double.MAX_VALUE;
        vMillorShelves = numberOfProducts;

        long initTime = System.currentTimeMillis();
        distribute(configuracion, nivel, marcage);
        long executionTime = System.currentTimeMillis()-initTime;
        System.out.println("Distribuido en " + executionTime + " ms");
    }

    /**
     * Devuelve la mejor distribucion almacenada
     * @return la mejor distribucion
     */
    public int[] getDistribution(){
        return xMillor;
    }

    //***************************METODOS AUXILIARES Y BACTRACKING PRINCIPAL******************************************//

    private double vActualDist;
    private int vActual;

    private int[] xMillor;
    private double vMillorDist;
    private int vMillorShelves;

    /**
     * Funcion recursiva que halla ma mejor distribucion los productos en las diferentes estanterias
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @param m Array con el numero de elementos en cada estanteria
     */
    private void distribute(int[] x, int k, int[] m){

        //Prepara recorrido del nivel
        x[k] = -1;

        while(haySucesor(x, k)) {

            //Siguiente hermano
            x[k]++;

            m = marcar(x, k, m);
            vActualDist = valor(x, k+1);


            if (esMejorSolucion() && esBuena(x, k, m)) {
                if (esSolucion(x, k)) {
                    System.out.println(Arrays.toString(x));
                    System.out.println(vActualDist);
                    tratarSolucion(x);
                } else {
                    if(esMejorSolucion()) distribute(x, k + 1, m);
                }
            }

            m = desmarcar(x, k, m);
        }
    }



    /**
     * Indica si todavia hay productos por tratar
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @return Cierto si quedan productos, falso de lo contrario
     */
    private boolean haySucesor(int[] x, int k){
        return x[k] < whSize-1;
    }


    /**
     * Comprueba si se han tratado todos los productos
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @return Cierto si se han tratado todos los productos
     */
    private boolean esSolucion(int[] x, int k){
        return k == numberOfProducts-1;
    }

    /**
     * Indica si la decision de colocar el producto es correcta
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @param m Array con el numero de elementos en cada estanteria
     * @return Cierto si el producto ha sido colocado correctamente
     */
    private boolean esBuena(int[] x, int k, int[] m){
        return m[x[k]] <= 3;
    }

    /**
     * Asigna a una estanteria un producto
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @param m Array con el numero de elementos en cada estanteria
     * @return El array con el numero de elementos en cada estanteria actualizado
     */
    private int[] marcar(int[] x, int k, int[] m){
        if(m[x[k]] == 0){
            vActual++;
        }

        m[x[k]]++;

        return m;
    }

    /**
     * Desasigna de una estanteria un producto
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @param m Array con el numero de elementos en cada estanteria
     * @return El array con el numero de elementos en cada estanteria actualizado
     */
    private int[] desmarcar(int[] x, int k, int[] m){
        m[x[k]]--;
        if(m[x[k]]==0){
            vActual--;
        }
        return m;
    }

    /**
     * Actualiza la mejor configuracion encontrada hasta el momento
     * @param x Array de configuracion
     */
    private void tratarSolucion(int[] x){

            vMillorDist = vActualDist;
            vMillorShelves = vActual;
            xMillor = Arrays.copyOf(x, x.length);

    }

    /**
     * Comprueba si la configuracion actual es mas optima que la mejor hallada hasta el momento
     * @return Cierto si la configuracion actual es mas optima que la mejor
     */
    private boolean esMejorSolucion(){

        return (vActualDist < vMillorDist);
    }

    /**
     * Calcula el valor parcial o total de la configuracion que se le pase por parametro
     * @param x Configuracion a evaluar
     * @return Valor resultado de la evaluacion de la configuracion
     */
    private double valor(int x[], int mode){

        double dists;
        double total = 0;

        int size;
        if(mode == -1) size = x.length;
        else size = mode;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){

                dists = Vec2d.distance(wh.getWH().get(x[i]).getX(), wh.getWH().get(x[i]).getY(),
                        wh.getWH().get(x[j]).getX(), wh.getWH().get(x[j]).getY()
                );
                total = total + dists * (1- probabilities[i][j]);

            }
        }

        return total;
    }


}
