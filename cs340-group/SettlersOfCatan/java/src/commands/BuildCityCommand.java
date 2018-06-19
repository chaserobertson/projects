package commands;

import params.BuildCityParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class BuildCityCommand  extends Command{
    private BuildCityParams params;

    public  BuildCityCommand(BuildCityParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.buildCity(params);
    }
}