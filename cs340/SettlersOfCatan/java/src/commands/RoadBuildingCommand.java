package commands;

import params.RoadBuildingParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class RoadBuildingCommand  extends Command{
    RoadBuildingParams params;

    public RoadBuildingCommand(RoadBuildingParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.playCardRoadBuilding(params);
    }
}
