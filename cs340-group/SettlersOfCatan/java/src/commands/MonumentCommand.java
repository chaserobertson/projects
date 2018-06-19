package commands;

import params.MonumentParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class MonumentCommand  extends Command{
    private MonumentParams params;

    public  MonumentCommand(MonumentParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.playCardMonument(params);
    }
}
