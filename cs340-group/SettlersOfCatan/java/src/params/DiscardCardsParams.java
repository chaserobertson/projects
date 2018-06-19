package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.DiscardCommand;
import commands.ICommand;
import model.definitions.ResourceHand;

/**
 * Created by chase on 9/20/16.
 */
public class DiscardCardsParams extends MoveParam {

    public ResourceHand discardedCards;

    public DiscardCardsParams(int playerIndex, ResourceHand discardedCards) {
        type = "discardCards";
        this.playerIndex = playerIndex;
        this.discardedCards = discardedCards;
    }

    public DiscardCardsParams() {
        type = "discardCards";
    }

    public DiscardCardsParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.discardedCards = new ResourceHand(jsonObject.get("discardedCards").getAsJsonObject());
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.add("discardedCards", discardedCards.toJson());
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new DiscardCommand(this);
    }
}
