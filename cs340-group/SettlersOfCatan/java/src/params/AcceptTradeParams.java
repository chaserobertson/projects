package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.ICommand;

/**
 * Created by chase on 9/20/16.
 */
public class AcceptTradeParams extends MoveParam {

    public int acceptor;
    public int offeror;
    public boolean willAccept;

    public AcceptTradeParams(int acceptor, int offeror, boolean willAccept) {
        type = "acceptTrade";
        this.playerIndex = acceptor;
        this.acceptor = acceptor;
        this.offeror = offeror;
        this.willAccept = willAccept;
    }

    public AcceptTradeParams() {
        type = "acceptTrade";
    }

    public AcceptTradeParams(String jsonParams) {
        super(jsonParams);
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = gson.fromJson(jsonParams, JsonObject.class);
        this.acceptor = playerIndex;
        this.willAccept = jsonObject.get("willAccept").getAsBoolean();
        //TODO: determine what the heck to do with offeror
    }

    @Override
    public String toString() {
        JsonObject jsonObject = super.toJson();
        jsonObject.addProperty("willAccept",willAccept);
        return jsonObject.toString();
    }
    @Override
    public ICommand generateCommand(){
        return new AcceptTradeCommand(this);
    }
}
