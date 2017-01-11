package commands;

import params.MaritimeTradeParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class MaritimeTradeCommand  extends Command{
    private MaritimeTradeParams params;

    public MaritimeTradeCommand(MaritimeTradeParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.maritimeTrade(params);
    }
}
