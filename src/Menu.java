import com.google.gson.JsonObject;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by angel on 14/12/2017.
 */
class Menu {
    private JsonReader jsonReader = new JsonReader();
    private JsonObject configJson;
    private JsonObject infoJson;

    void mostraMenu() {
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

    private void opcio1() {
        System.out.println("Introduiex la ubicació del fitxer json de configuracio: ");
        Scanner read = new Scanner(System.in);
        String path = read.nextLine();

        configJson = jsonReader.lectura(path);

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
