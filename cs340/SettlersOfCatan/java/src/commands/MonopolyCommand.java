package commands;

import params.MonopolyParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class MonopolyCommand  extends Command{
    private MonopolyParams params;

    public MonopolyCommand(MonopolyParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.playCardMonopoly(params);
    }
}
