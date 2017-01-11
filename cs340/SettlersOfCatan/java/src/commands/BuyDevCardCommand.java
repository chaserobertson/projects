package commands;

import params.BuyDevCardParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class BuyDevCardCommand  extends Command{
    private BuyDevCardParams params;

    public BuyDevCardCommand(BuyDevCardParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.buyDevCard(params);
    }
}
