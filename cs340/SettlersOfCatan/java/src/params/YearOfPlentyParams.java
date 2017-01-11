package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.YearOfPlentyCommand;
import shared.definitions.ResourceType;

/**
 * Created by chase on 9/20/16.
 */
public class YearOfPlentyParams extends MoveParam {

    public ResourceType resource1;
    public ResourceType resource2;

    public YearOfPlentyParams(int playerIndex, ResourceType resource1, ResourceType resource2) {
        type = "Year_of_Plenty";
        this.playerIndex = playerIndex;
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public YearOfPlentyParams() {
        type = "Year_of_Plenty";
    }

    public YearOfPlentyParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.resource1 = model.definitions.EnumConverter.ResourceType(jsonObject.get("resource1").getAsString());//This deserializes as int
        this.resource2 = model.definitions.EnumConverter.ResourceType(jsonObject.get("resource2").getAsString());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("resource1",resource1.toString());//Here is the problem. This serializes it as a string type
        jsonObject.addProperty("resource2",resource2.toString());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new YearOfPlentyCommand(this);
    }
}
