package params;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import commands.AcceptTradeCommand;
import commands.FinishTurnCommand;
import commands.ICommand;

/**
 * Created by chase on 9/24/16.
 */
public class FinishTurnParams extends MoveParam {

    public FinishTurnParams(int playerIndex) {
        type = "finishTurn";
        this.playerIndex = playerIndex;
    }

    public FinishTurnParams() {
        type = "finishTurn";
    }

    public FinishTurnParams(String jsonParams) {
        super(jsonParams);
    }

    @Override
    public String toString() {
        return super.toJson().toString();
    }
    @Override
    public ICommand generateCommand(){
        return new FinishTurnCommand(this);
    }
}
