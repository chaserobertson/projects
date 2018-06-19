package params;

import com.google.gson.JsonObject;

/**
 * Created by chase on 9/23/16.
 */
public class SaveGameRequest {

    public Integer id;
    public String name;

    public SaveGameRequest(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id.toString());
        jsonObject.addProperty("name",name);
        return jsonObject.toString();
    }
}
