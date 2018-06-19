package commands;

import params.BuildSettlementParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class BuildSettlementCommand  extends Command{
    private BuildSettlementParams params;

    public BuildSettlementCommand (BuildSettlementParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.buildSettlement(params);
    }
}
