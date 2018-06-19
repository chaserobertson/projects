package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.MonopolyCommand;
import model.definitions.EnumConverter;
import shared.definitions.ResourceType;

/**
 * Created by chase on 9/20/16.
 */
public class MonopolyParams extends MoveParam {

    public ResourceType resource;

    public MonopolyParams(int playerIndex, ResourceType resource) {
        type = "Monopoly";
        this.playerIndex = playerIndex;
        this.resource = resource;
    }

    public MonopolyParams() {
        type = "Monopoly";
    }

    public MonopolyParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.resource = EnumConverter.ResourceType(jsonObject.get("resource").getAsString());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("resource",resource.toString());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new MonopolyCommand(this);
    }
}
