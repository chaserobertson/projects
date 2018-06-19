package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;

/**
 * Created by chase on 9/24/16.
 */
public abstract class MoveParam extends GameRequiredParam{

    public String type;
    public int playerIndex;

    public MoveParam() {}

    public MoveParam(String jsonParam) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParam, JsonObject.class);
        type = jsonObject.get("type").getAsString();
        playerIndex = jsonObject.get("playerIndex").getAsInt();
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("playerIndex",playerIndex);
        return jsonObject;
    }
}
