package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Created by chase on 9/20/16.
 */
public class CreateGameRequest {

    public int creatorId;
    public boolean randomTiles;
    public boolean randomNumbers;
    public boolean randomPorts;
    public String name;

    public CreateGameRequest(boolean randomTiles, boolean randomNumbers, boolean randomPorts, String name) {
        this.randomTiles = randomTiles;
        this.randomNumbers = randomNumbers;
        this.randomPorts = randomPorts;
        this.name = name;
    }

    public CreateGameRequest(String jsonRequest) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonRequest, JsonObject.class);
        this.randomTiles = gson.fromJson(jsonObject.get("randomTiles"), Boolean.class);
        this.randomNumbers = gson.fromJson(jsonObject.get("randomNumbers"), Boolean.class);
        this.randomPorts = gson.fromJson(jsonObject.get("randomPorts"), Boolean.class);
        this.name = gson.fromJson(jsonObject.get("name"), String.class);
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("randomTiles",randomTiles);
        jsonObject.addProperty("randomNumbers",randomNumbers);
        jsonObject.addProperty("randomPorts",randomPorts);
        jsonObject.addProperty("name",name);
        return jsonObject.toString();
    }
}
