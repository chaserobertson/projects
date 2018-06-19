package commands;

import params.Version;

/**
 * Created by tsmit on 11/7/2016.
 */
public class GetGameModelCommand  extends Command{
    private Version params;

    public GetGameModelCommand(Version params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.gameModel(params);
    }
}
