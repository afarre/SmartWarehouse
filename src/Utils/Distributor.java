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
                marcage = new int[whSize];
        int nivel = 0;
        xMillor = null;
        vMillor = whSize;

        distribute(configuracion, nivel, marcage);
    }

    private int[] getDistribution(){
        return xMillor;
    }

    //***************************METODOS AUXILIARES Y BACTRACKING PRINCIPAL******************************************//

    private int vActual;

    private int[] xMillor;
    private int vMillor;

    private int[][] vDistancias;

    private void distribute(int[] x, int k, int[] m){

        x = preparaRecorrido(x, k);

        while(haySucesor(x, k)) {
            siguienteHermano(x, k);
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

    private int[] preparaRecorrido(int[] x, int k){
        x[k] = 0;
        return x;
    }

    private boolean haySucesor(int[] x, int k){
        return x[k] < whSize;
    }

    private int[] siguienteHermano(int[] x, int k){
        x[k]++;
        return x;
    }

    private boolean esSolucion(int[] x, int k){
        return k == numberOfProducts-1;
    }

    private boolean esBuena(int[] x, int k, int[] m){
        return m[x[k]] <= 3;
    }

    private int[] marcar(int[] x, int k, int[] m){
        if(m[x[k]]==0){
            vActual++;
        }
        m[x[k]]++;
        return m;
    }

    private int[] desmarcar(int[] x, int k, int[] m){
        m[x[k]]--;
        if(m[x[k]]==0){
            vActual--;
        }
        return m;
    }

    private void tratarSolucion(int[] x){
        if(xMillor == null || esMejorSolucion(x, xMillor)){
            xMillor = Arrays.copyOf(x, x.length);
            vMillor = vActual;
            return;
        }
    }

    private boolean esMejorSolucion(int[] x, int[] xMillor){
        return false;
    }
}
