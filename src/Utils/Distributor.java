package Utils;

import Model.Warehouse;
import View.WarehouseView;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase que implementa un distribuidor de productos en el almacen segun las probabilidades que tengan entre ellos de
 * aparecer juntos en un mismo pedido. Utiliza un algoritmo por backtracking con diferentes mejoras de eficiencia
 * como son marcage y poda basada en la mejor solucion encontrada hasta el momento.
 * @author Alex Vogel
 *
 */
public class Distributor {

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
        vMillor = whSize;

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

    private int vActual;

    private int[] xMillor;
    private int vMillor;

    /**
     * Funcion recursiva que halla ma mejor distribucion los productos en las diferentes estanterias
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @param m Array con el numero de elementos en cada estanteria
     */
    private void distribute(int[] x, int k, int[] m){

        x = preparaRecorrido(x, k);

        while(haySucesor(x, k)) {
            x = siguienteHermano(x, k);
            m = marcar(x, k, m);

            if (esBuena(x, k, m) && vActual < vMillor) {
                if (esSolucion(x, k)) {
                    tratarSolucion(x);
                } else {
                    distribute(x, k + 1, m);
                }
            }

            m = desmarcar(x, k, m);
        }
    }

    /**
     * Asigna el primer nodo/producto
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @return El primer nodo/producto
     */
    private int[] preparaRecorrido(int[] x, int k){
        x[k] = -1;
        return x;
    }

    /**
     * Indica si todavia hay productos por tratar
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @return Cierto si quedan productos, falso de lo contrario
     */
    private boolean haySucesor(int[] x, int k){
        return x[k] < whSize;
    }

    /**
     * Nos da el siguiente producto a tratar
     * @param x Array configuracion
     * @param k Indice del producto en el array de configuracion
     * @return Array configuracion
     */
    private int[] siguienteHermano(int[] x, int k){
        x[k]++;
        return x;
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
        if(xMillor == null || esMejorSolucion(x, xMillor)){
            xMillor = Arrays.copyOf(x, x.length);
            vMillor = vActual;
        }
    }

    /**
     * Comprueba si la configuracion actual es mas optima que la mejor hallada hasta el momento
     * @param x Array de configuracion actual
     * @param xMillor La mejor configuracion hallada hasta el momento
     * @return Cierto si la configuracion actual es mas optima que la mejor
     */
    private boolean esMejorSolucion(int[] x, int[] xMillor){
        double distanciasActual;
        double totalActual = 0;

        double distanciasMejor;
        double totalMejor = 0;

        int xLenght = x.length;
        for (int i = 0; i < xLenght; i++){
            for (int j = 0; j < xLenght; j++){
                distanciasActual = Math.sqrt(Math.pow(wh.getWH().get(x[i]).getX() - wh.getWH().get(x[j]).getX(), 2)
                        + Math.pow(wh.getWH().get(x[i]).getY() - wh.getWH().get(x[j]).getY(), 2));
                totalActual = totalActual + distanciasActual * (1- probabilities[i][j]);

                distanciasMejor = Math.sqrt(Math.pow(wh.getWH().get(xMillor[i]).getX() - wh.getWH().get(xMillor[j]).getX(), 2)
                        + Math.pow(wh.getWH().get(xMillor[i]).getY() - wh.getWH().get(xMillor[j]).getY(), 2));
                totalMejor = totalMejor + distanciasMejor * (1- probabilities[i][j]);
            }
        }
        return totalActual < totalMejor;
    }
}
