package commands;

import params.BuildRoadParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class BuildRoadCommand extends Command{
    private BuildRoadParams params;

    public  BuildRoadCommand(BuildRoadParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.buildRoad(params);
    }
}