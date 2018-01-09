package Utils;

import Model.Warehouse;
import View.WarehouseView;

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

    private boolean[][] map;

    private float[][] probabilities;

    private HashMap<Integer, Integer> indexes;

    private WarehouseView warehouseView;

    public Distributor(Warehouse wh, boolean[][] map, float[][] probabilities, HashMap<Integer,
            Integer> indexes, WarehouseView view) {

        this.wh = wh;
        this.map = map;
        this.probabilities = probabilities;
        this.indexes = indexes;
        warehouseView = view;
    }

    public void distribute(){

    }

    //***************************METODOS AUXILIARES Y BACTRACKING PRINCIPAL******************************************//
}
