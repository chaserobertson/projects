package commands;

import params.RobPlayerParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class RobPlayerCommand  extends Command{
    private RobPlayerParams params;

    public RobPlayerCommand(RobPlayerParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.robPlayer(params);
    }
}
