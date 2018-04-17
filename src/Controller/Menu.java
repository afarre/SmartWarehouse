package Controller;

import Model.Product;
import Model.Shelve;
import Model.Warehouse;
import Utils.Distributor;
import Utils.JsonReader;
import Utils.RobotRouter;
import View.WarehouseView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by angel on 14/12/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonObject configJson;
    private JsonArray prodJson;

    /**
     * Modelo del almacen donde se guardan todos los datos de estanterias
     */
    private Warehouse warehouse;

    /**
     * Mapa del almacen, donde se indica dónde hay estanterias y donde no
     */
    private boolean map[][];

    /**
     *  Matriz de adyacencias que guarda las  probabilidades de que dos productos esten juntos en un mismo pedido
     */

    private float[][] adyacencia;

    /**
     * Mapa de hash que almacena los pares idProducto-indiceDeAdyacencia para que el acceso a la matriz
     * sea lo mas rapido posible
     */
    private HashMap<Integer, Integer> indexes;

    private HashMap<Integer, String> prodNames;

    /**
     * Mapa de hash que almacena los pares idProducto-indiceDePedido para que el acceso lo mas rapido posible
     */
    private HashMap<Integer, Integer> orderIndexes;

    /**
     * Ventana que muestra el estado del almacen en cada momento
     */
    private WarehouseView warehouseView;

    /**
     * Permite la eleccion de las dintintas opciones del menu comprendidas entre 1 y 5 incluidas, con control de erorres
     */
    public void mostraMenu() {
        int i;
        do {
            System.out.println("\n1. Configurar magatzem");
            System.out.println("2. Carregar productes");
            System.out.println("3. Distribuir productes");
            System.out.println("4. Servir comanda");
            System.out.println("5. Sortir\n");
            System.out.println("\nSel·lecciona una opcio:");

            i = readInt();

            while (i < 1 || i > 5) {
                System.out.println("Opcio del menu incorrecta! Introdueix l'opcio de nou:");
                i = readInt();
            }

            switch (i){
                case 1:
                    opcio1();
                    break;
                case 2:
                    opcio2();
                    break;
                case 3:
                    if (configJson == null || prodJson == null || configJson.size() == 0 || prodJson.size() == 0){
                        System.out.println("Error! Algun dels fitxers no ha sigut introduit o no s'ha trobat.");
                    }else {
                        opcio3();
                    }
                    break;
                case 4:
                    opcio4();
                    break;
                case 5:
                    warehouseView.dispose();

            }
        }while(i != 5);
    }

    /**
     * Pide el path del fichero json de configuracion i lo lee
     */
    private void opcio1() {
        System.out.println("Introduiex la ubicació del fitxer json de configuracio: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        configJson = jsonReader.lecturaObject(path);
        if(configJson == null) return;

        if (configJson.size() != 0){
            configuraEscenari();
        }
    }

    /**
     * A partir del json de configuración genera la vista con el escenario correspondiente
     */
    private void configuraEscenari() {

        int maxX = configJson.get("dim").getAsJsonObject().get("max_x").getAsInt();
        int maxY = configJson.get("dim").getAsJsonObject().get("max_y").getAsInt();

        int entranceX = configJson.get("entrance").getAsJsonObject().get("x").getAsInt();
        int entranceY = configJson.get("entrance").getAsJsonObject().get("y").getAsInt();

        warehouse = new Warehouse(maxX, maxY, entranceX, entranceY);

        //****************MONTAGE DE LA MATRIZ DE BOOLEANOS QUE INDICA DONDE HAY ESTANTERIAS Y DONDE NO***************//
        map = new boolean[maxX][maxY];

        int size = configJson.get("shelves").getAsJsonArray().size(),
                sSize = configJson.get("shelves_config").getAsJsonArray().size();

        for (int i = 0; i < size; i++){
            for (int j = 0; j < sSize; j++){

                if (configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("id").getAsInt()
                        == configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("config").getAsInt()){

                    int len = configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt();
                    int xstart = configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt();
                    int ystart = configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt();

                    if (configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("orientation").getAsString().equals("V")){

                        for (int k = 0; k < len; k++){
                            map[xstart][ystart + k] = true;
                            warehouse.addShelve(new Shelve(xstart, ystart + k));
                        }

                    } else {

                        for (int k = 0; k < len; k++){
                            map[xstart + k] [ystart] = true;
                            warehouse.addShelve(new Shelve(xstart + k, ystart));
                        }
                    }
                }
            }
        }

        //-----------------Inicializamos la vista con la casilla de entrada al almacen--------------------------------//
        warehouseView = new WarehouseView(
                map,
                entranceX,
                entranceY
        );

        BoxListener boxListener = new BoxListener(warehouseView, warehouse);
        warehouseView.setMapMouseListener(boxListener);
        warehouseView.setVisible(false);

        for (int i = 0; i < size; i++){

            for (int j = 0; j < sSize; j++){

                if (configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("id").getAsInt()
                        == configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("config").getAsInt()){

                    if (configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("orientation").getAsString()
                            .equals("V")){

                        int len = configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt();
                        for (int k = 0; k < len; k++){
                            warehouseView.paintCell(configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt(), configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt() + k, Color.BLUE);
                        }

                    } else {

                        int len = configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt();
                        for (int k = 0; k < len; k++){
                            warehouseView.paintCell(configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt() + k, configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt(), Color.BLUE);
                        }
                    }
                }
            }
        }
    }

    /**
     * Pide el path del json con la informacion del warehouse y el path del fichero de probabilidades de productos y los trata
     */
    private void opcio2() {

        System.out.println("Introduiex la ubicació del fitxer json amb la informacio dels productes: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        prodJson = jsonReader.lecturaArray(path);
        if(prodJson == null) return;

        prodNames = new HashMap<>();
        int numberOfProducts = prodJson.size();

        for(int i = 0; i < numberOfProducts; i++){
            JsonObject prod = prodJson.get(i).getAsJsonObject();
            prodNames.put(prod.get("id").getAsInt(), prod.get("name").getAsString());
        }

        adyacencia = new float[numberOfProducts + 1][numberOfProducts + 1];
        indexes = new HashMap<>();

        int i = 0;

        System.out.println("Introduiex la ubicació del fitxer de probabilitats d'aparició dels productes: ");
        path = read.nextLine();

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));

            String line;
            String[] lineParts;
            while((line = in.readLine()) != null)
            {
                lineParts = line.split(" ");
                int p1 = Integer.parseInt(lineParts[0]);
                int p2 = Integer.parseInt(lineParts[1]);
                //System.out.println(lineParts[2]);
                float prob = Float.parseFloat(lineParts[2]);

                if(!indexes.containsKey(p1)){
                    indexes.put(p1, i);
                    i++;
                }
                if(!indexes.containsKey(p2)){
                    indexes.put(p2, i);
                    i++;
                }
                adyacencia[indexes.get(p1)][indexes.get(p2)] = prob;
            }
            in.close();
        }catch (IOException e){
            System.out.println("Error! Fitxer no trobat!");
        }
    }

    /**
     * Distribucion de productos en las diferentes estanterias del almacen
     */
    private void opcio3() {
        Distributor distributor = new Distributor(warehouse, map, adyacencia, indexes, warehouseView);
        distributor.distribute();
        int[] dist = distributor.getDistribution();

        for(int productID : indexes.keySet()){
            Product p = new Product(productID, prodNames.get(productID));
            warehouse.getWH().get(dist[indexes.get(productID)]).addProduct(p);
        }

        warehouseView.setVisible(true);
    }

    /**
     * Realizacion de pedido: calculo de la ruta mas corta de preparacion del pedido para su envio
     */
    private void opcio4() {
        System.out.println("Introduiex la ubicació del fitxer json de comandes: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        JsonArray comandesJson = jsonReader.lecturaArray(path);
        if(comandesJson == null) return;


        if (comandesJson.size() != 0 & comprovaComandes(comandesJson, prodJson.size())){

            int orderSize = comandesJson.size();
            Product[] orders = new Product[orderSize];
            orderIndexes = new HashMap<>();

            for (int i = 0; i < orderSize; i++){
                JsonObject order = comandesJson.get(i).getAsJsonObject();

                orders[i] = new Product(order.get("id").getAsInt(), order.get("name").getAsString());

                orderIndexes.put(orders[i].getId(), i);
            }

            RobotRouter router = new RobotRouter(warehouse, orders, orderIndexes, warehouseView);
            router.enrutaRobot();
        }

        //Enrutamiento del robot

        //Mostrar camino en la ventana grafica

    }


    private boolean comprovaComandes(JsonArray comandes, int prodSize) {
        int comandesSize = comandes.size();
        boolean totsTrobats = true;
        for (int i = 0; i < comandesSize; i++){
            boolean trobat = false;
            for (int j = 0; j < prodSize; j++){
                if (prodJson.get(j).getAsJsonObject().get("id").getAsInt() == comandes.get(i).getAsJsonObject().get("id").getAsInt()) {
                    trobat = true;
                    break;
                }
            }
            if (!trobat){
                totsTrobats = false;
                System.err.println("Hi ha productes a la llista de la compra no disponibles al magatzem");
            }
        }
        return totsTrobats;
    }

    /**
     * Comprueba si se ha introducido un entero por consola
     * @return El entero introducido o -1 en caso de no haber introducido un entero
     */
    private int readInt(){
        try {
            Scanner read = new Scanner(System.in);
            int i = read.nextInt();
            return i;
        }catch (InputMismatchException ignored){
        }
        return -1;
    }

}
