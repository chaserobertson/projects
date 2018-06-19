package params;

import com.google.gson.JsonObject;

/**
 * Created by chase on 9/23/16.
 */
public class LoadGameRequest {

    public String name;

    public LoadGameRequest(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",name);
        return jsonObject.toString();
    }
}
