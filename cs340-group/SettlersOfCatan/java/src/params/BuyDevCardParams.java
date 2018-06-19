package params;

import commands.AcceptTradeCommand;
import commands.BuyDevCardCommand;
import commands.ICommand;

/**
 * Created by chase on 9/24/16.
 */
public class BuyDevCardParams extends MoveParam {

    public BuyDevCardParams(int playerIndex) {
        type = "buyDevCard";
        this.playerIndex = playerIndex;
    }

    public BuyDevCardParams() {
        type = "buyDevCard";
    }

    public BuyDevCardParams(String jsonParams) {
        super(jsonParams);
    }

    @Override
    public String toString() {
        return super.toJson().toString();
    }
    @Override
    public ICommand generateCommand(){
        return new BuyDevCardCommand(this);
    }
}
