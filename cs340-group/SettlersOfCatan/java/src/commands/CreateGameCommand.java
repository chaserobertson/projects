package commands;

import params.CreateGameRequest;

/**
 * Created by tsmit on 11/7/2016.
 */
public class CreateGameCommand  extends Command{
    private CreateGameRequest params;

    public CreateGameCommand(CreateGameRequest params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.gameCreate(params);
    }
}
