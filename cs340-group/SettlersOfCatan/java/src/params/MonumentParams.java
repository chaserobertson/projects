package params;

import commands.ICommand;
import commands.MonumentCommand;

/**
 * Created by chase on 9/24/16.
 */
public class MonumentParams extends MoveParam {

    public MonumentParams(int playerIndex) {
        type = "Monument";
        this.playerIndex = playerIndex;
    }

    public MonumentParams() {
        type = "Monument";
    }

    public MonumentParams(String jsonParams) {
        super(jsonParams);
    }

    @Override
    public String toString() {
        return super.toJson().toString();
    }
    @Override
    public ICommand generateCommand(){
        return new MonumentCommand(this);
    }
}
