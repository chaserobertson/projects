package commands;

import params.FinishTurnParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class FinishTurnCommand  extends Command{
    private FinishTurnParams params;

    public FinishTurnCommand(FinishTurnParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.finishTurn(params);
    }
}