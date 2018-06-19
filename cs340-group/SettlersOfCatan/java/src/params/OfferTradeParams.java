package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.ICommand;
import commands.OfferTradeCommand;
import model.definitions.ResourceHand;

/**
 * Created by chase on 9/20/16.
 */
public class OfferTradeParams extends MoveParam {

    public ResourceHand offer;
    public int receiver;

    public OfferTradeParams(int playerIndex, ResourceHand offer, int receiver) {
        type = "offerTrade";
        this.playerIndex = playerIndex;
        this.offer = offer;
        this.receiver = receiver;
    }

    public OfferTradeParams() {
        type = "offerTrade";
    }

    public OfferTradeParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.offer = new ResourceHand(jsonObject.get("offer").getAsJsonObject());
        this.receiver = jsonObject.get("receiver").getAsInt();
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("offer",offer.toJson());
        jsonObject.addProperty("receiver",receiver);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new OfferTradeCommand(this);
    }
}
