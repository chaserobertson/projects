package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.ICommand;
import commands.JoinGameCommand;

/**
 * Created by chase on 9/23/16.
 */
public class JoinGameRequest extends GameRequiredParam{

    public Integer id;
    public String color;

    public JoinGameRequest(int id, String color) {
        this.id = id;
        this.color = color;
    }

    public JoinGameRequest(String jsonRequest) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonRequest, JsonObject.class);
        this.id = gson.fromJson(jsonObject.get("id"), Integer.class);
        this.color = gson.fromJson(jsonObject.get("color"), String.class);
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", this.id.toString());
        jsonObject.addProperty("color", this.color);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new JoinGameCommand(this);
    }
}
