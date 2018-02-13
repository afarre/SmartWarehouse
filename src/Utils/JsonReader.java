package Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by angel on 13/12/2017.
 */
public class JsonReader {

    /**
     * Carga y lee un fichero json
     * @param path Directorio donde se ubica el fichero json a leer
     * @return el fichero json leido en formato JsonObject
     */
    public JsonObject lecturaObject(String path){
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(path);
            br = new BufferedReader(fr);
            jsonObject = gson.fromJson(br, JsonObject.class);

        } catch (FileNotFoundException ko) {
            System.out.println("Error! Fitxer no trobat!");
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * Carga y lee un fichero json
     * @param path Directorio donde se ubica el fichero json a leer
     * @return el fichero json leido en formato JsonArray
     */
    public JsonArray lecturaArray(String path) {
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(path);
            br = new BufferedReader(fr);
            jsonArray = gson.fromJson(br, JsonArray.class);

        } catch (FileNotFoundException ko) {
            System.out.println("Error! Fitxer no trobat!");
            return null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;

    }
}
