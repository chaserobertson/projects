package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Created by chase on 9/20/16.
 */
public class Credentials implements Serializable {

    public String username;
    public String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Credentials(String jsonCredentials) {
        Gson gson = new GsonBuilder().create();
        JsonObject credentials = gson.fromJson(jsonCredentials, JsonObject.class);
        this.username = gson.fromJson(credentials.get("username"), String.class);
        this.password = gson.fromJson(credentials.get("password"), String.class);
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        return jsonObject.toString();
    }
}
