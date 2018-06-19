package commands;

import params.JoinGameRequest;

/**
 * Created by tsmit on 11/7/2016.
 */
public class JoinGameCommand  extends Command{
    private JoinGameRequest params;

    public JoinGameCommand(JoinGameRequest params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.gameJoin(params);
    }
}