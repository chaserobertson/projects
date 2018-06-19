package commands;

import params.DiscardCardsParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class DiscardCommand  extends Command{
    private DiscardCardsParams params;

    public  DiscardCommand(DiscardCardsParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.discardCards(params);
    }
}
