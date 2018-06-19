package commands;

import params.RollNumberParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class RollCommand  extends Command{
    private RollNumberParams params;

    public RollCommand(RollNumberParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.rollNumber(params);
    }
}
