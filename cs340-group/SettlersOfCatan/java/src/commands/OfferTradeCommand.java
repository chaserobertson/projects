package commands;

import params.OfferTradeParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class OfferTradeCommand  extends Command{
    private OfferTradeParams params;

    public OfferTradeCommand(OfferTradeParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.offerTrade(params);
    }
}
