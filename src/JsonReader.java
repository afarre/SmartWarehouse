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
     * Carrega i llegeix el fitxer Json corresponent
     * @return el fitxer Json llegit en forma JsonObject
     */
    JsonObject lectura(){
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader("favoritePlaces.json");
            br = new BufferedReader(fr);
            jsonObject = gson.fromJson(br, JsonObject.class);
        } catch (FileNotFoundException ko) {
            ko.printStackTrace();
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
}
