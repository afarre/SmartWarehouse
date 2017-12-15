package Controller;

import Utils.JsonReader;
import View.WarehouseView;
import com.google.gson.JsonObject;

import java.awt.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by angel on 14/12/2017.
 */
public class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonObject configJson;
    private JsonObject infoJson;

    /**
     * Permite la eleccion de las dintintas opciones del menu comprendidas entre 1 y 5 incluidas, con control de erorres
     */
    public void mostraMenu() {
        int i = 0;
        do {
            System.out.println("\n1. Configurar magatzem");
            System.out.println("2. Carregar productes");
            System.out.println("3. Distribuir productes");
            System.out.println("4. Servir comanda");
            System.out.println("5. Sortir\n");
            System.out.println("\nSel·lecciona una opcio:");

            i = readInt(i);

            while (i < 1 || i > 5) {
                System.out.println("Opcio del menu incorrecta! Introdueix l'opcio de nou:");
                i = readInt(i);
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
     * Ejecuta la opcion1 del menu
     */
    private void opcio1() {
        System.out.println("Introduiex la ubicació del fitxer json de configuracio: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        configJson = jsonReader.lectura(path);

        configuraEscenari();
    }

    /**
     * A partir del json de configuración genera la vista con el escenario correspondiente
     */
    private void configuraEscenari() {
        boolean map[][] = new boolean[configJson.get("dim").getAsJsonObject().get("max_x").getAsInt()][configJson.get("dim").getAsJsonObject().get("max_y").getAsInt()];

        for (int i = 0; i < configJson.get("shelves").getAsJsonArray().size(); i++){
            map[configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("x_start").getAsInt()][configJson.get("shelves").getAsJsonArray().get(i).getAsJsonObject().get("y_start").getAsInt()] = false;

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

    private void opcio2() {
        System.out.println("Introduiex la ubicació del fitxer json amb la informacio dels productes: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        infoJson = jsonReader.lectura(path);

        System.out.println("Introduiex la ubicació del fitxer de probabilitats d'aparició dels productes: ");
        path = read.nextLine();

    }

    private void opcio3() {

    }

    private void opcio4() {

    }

    private int readInt(int i){
        try {
            Scanner read = new Scanner(System.in);
            i = read.nextInt();
            return i;
        }catch (InputMismatchException ignored){
        }
        return -1;
    }

}
