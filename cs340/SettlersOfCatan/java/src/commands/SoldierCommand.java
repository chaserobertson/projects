package commands;

import params.SoldierParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class SoldierCommand  extends Command{
    private SoldierParams params;

    public SoldierCommand(SoldierParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.playCardSoldier(params);
    }
}