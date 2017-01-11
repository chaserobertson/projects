package commands;

import params.AcceptTradeParams;
import server.ServerFacade;

/**
 * Created by tsmit on 11/7/2016.
 */
public class AcceptTradeCommand extends Command{
    private AcceptTradeParams params;

    public AcceptTradeCommand(AcceptTradeParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.acceptTrade(params);
    }
}
