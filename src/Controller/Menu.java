package Controller;

import Utils.JsonReader;
import View.WarehouseView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by angel on 14/12/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonObject configJson;
    private JsonArray infoJson;

    private float[][] adyacencia;

    /**
     * Indica si el almacen ya esta configurado para poder distribuir los diferentes producotos en las diferentes estanterias
     */
    private boolean whReadyDist;


    public Menu(){
      whReadyDist = false;
    }

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
                    if (configJson == null || infoJson == null || configJson.size() == 0 || infoJson.size() == 0){
                        System.out.println("Error! Algun dels fitxers no ha sigut introduit o no s'ha trobat.");
                    }else {
                        opcio3();
                    }
                    break;
                case 4:
                    opcio4();
                    break;
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

        if (configJson.size() != 0){
            configuraEscenari();
        }
    }

    /**
     * A partir del json de configuración genera la vista con el escenario correspondiente
     */
    private void configuraEscenari() {
        boolean map[][] = new boolean[configJson.get("dim").getAsJsonObject().get("max_x").getAsInt()][configJson.get("dim").getAsJsonObject().get("max_y").getAsInt()];

        for (int i = 0; i < configJson.get("shelves").getAsJsonArray().size(); i++){
            for (int j = 0; j < configJson.get("shelves_config").getAsJsonArray().size(); j++){
                if (configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("id").getAsInt() == configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("config").getAsInt()){
                    if (configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("orientation").getAsString().equals("V")){
                        for (int k = 0; k < configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt(); k++){
                            map[configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt()][configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt() + k] = true;
                        }
                    } else {
                        for (int k = 0; k < configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt(); k++){
                            map[configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt() + k][configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt()] = true;
                        }
                    }
                }
            }
        }

        WarehouseView warehouseView = new WarehouseView(map, configJson.get("entrance").getAsJsonObject().get("x").getAsInt(), configJson.get("entrance").getAsJsonObject().get("y").getAsInt());
        BoxListener boxListener = new BoxListener(warehouseView);
        warehouseView.setMapMouseListener(boxListener);


        for (int i = 0; i < configJson.get("shelves").getAsJsonArray().size(); i++){
            for (int j = 0; j < configJson.get("shelves_config").getAsJsonArray().size(); j++){
                if (configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("id").getAsInt() == configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("config").getAsInt()){
                    if (configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("orientation").getAsString().equals("V")){
                        for (int k = 0; k < configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt(); k++){
                            warehouseView.paintCell(configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt(), configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt() + k, Color.BLUE);
                        }
                    } else {
                        for (int k = 0; k < configJson.get("shelves_config").getAsJsonArray().get(j).getAsJsonObject().get("length").getAsInt(); k++){
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

        infoJson = jsonReader.lecturaArray(path);

        adyacencia = new float[infoJson.size() + 1][infoJson.size() + 1];

        System.out.println("Introduiex la ubicació del fitxer de probabilitats d'aparició dels productes: ");
        path = read.nextLine();

        try {
            BufferedReader in = new BufferedReader(new FileReader(path));

            String line;
            while((line = in.readLine()) != null)
            {
                int p1 = Character.getNumericValue(line.charAt(0));
                int p2 = Character.getNumericValue(line.charAt(2));
                System.out.println(line.substring(4, 7));
                float prob = Float.parseFloat(line.substring(4, 7));
                adyacencia[p1][p2] = prob;
            }
            in.close();
            whReadyDist = true;
        }catch (IOException e){
            System.out.println("Error! Fitxer no trobat!");
        }
    }

    /**
     * Distribucion de productos en las diferentes estanterias del almacen
     */
    private void opcio3() {
        if(!whReadyDist){
            System.out.println("Antes de distribuir los productos se debe configurar el almacen! Asegurese de que ha llevado a cabo con éxito las opciones 1 y 2!");
            return;
        }

    }

    /**
     * Realizacion de pedido: calculo de la ruta mas corta de preparacion del pedido para su envio
     */
    private void opcio4() {

    }

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
