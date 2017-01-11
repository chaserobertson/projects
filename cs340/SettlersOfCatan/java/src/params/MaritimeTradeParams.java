package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.MaritimeTradeCommand;
import model.definitions.EnumConverter;
import shared.definitions.ResourceType;

/**
 * Created by chase on 9/20/16.
 */
public class MaritimeTradeParams extends MoveParam {

    public int ratio;
    public ResourceType inputResource;
    public ResourceType outputResource;

    public MaritimeTradeParams(int playerIndex, int ratio, ResourceType inputResource, ResourceType outputResource) {
        type = "maritimeTrade";
        this.playerIndex = playerIndex;
        this.ratio = ratio;
        this.inputResource = inputResource;
        this.outputResource = outputResource;
    }

    public MaritimeTradeParams() {
        type = "maritimeTrade";
    }

    public MaritimeTradeParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.ratio = jsonObject.get("ratio").getAsInt();
        this.inputResource = EnumConverter.ResourceType(jsonObject.get("inputResource").getAsString());
        this.outputResource = EnumConverter.ResourceType(jsonObject.get("outputResource").getAsString());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("ratio", ratio);
        jsonObject.addProperty("inputResource", inputResource.toString());
        jsonObject.addProperty("outputResource", outputResource.toString());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new MaritimeTradeCommand(this);
    }
}
