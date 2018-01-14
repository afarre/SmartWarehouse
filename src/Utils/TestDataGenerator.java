package Utils;

import Model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Genera los archivos de test de informacion de manera aleatoria para las pruebas de los algoritmos
 * de distribucion y de camino minimo
 */
public class TestDataGenerator {



    public static void main(String[] args) {

        int NUMBER_OF_PRODUCTS = 10;

        ArrayList<Product> plist = new ArrayList<>();

        Random r = new Random();

        try {
            FileWriter fw = new FileWriter(new File("products.json"));
            fw.append("[\n");

            int id = r.nextInt();
            if(id < 0) id = -id;

            Product p = new Product(id, "P1");
            plist.add(p);
            fw.append("  {\n    \"id\": "+id+",\n    \"name\": \"P1\"\n  }");

            for(int i=1; i < NUMBER_OF_PRODUCTS; i++){
                id = r.nextInt();
                if(id < 0) id = -id;
                p = new Product(id, "P"+(i+1));
                fw.append(",\n  {\n    \"id\": "+id+",\n    \"name\": \"P"+(i+1)+"\"\n  }");
                plist.add(p);
            }
            fw.append("\n]");
            fw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            FileWriter fw = new FileWriter(new File("probs.txt"));

            for(Product p : plist){
                for(Product l : plist){
                    if(p == l){
                        fw.append(p.getId()+" "+l.getId()+" "+Float.toString(0.000000f)+"\n");
                    }else{
                        fw.append(p.getId()+" "+l.getId()+" "+Float.toString(r.nextFloat())+"\n");
                    }
                }
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
