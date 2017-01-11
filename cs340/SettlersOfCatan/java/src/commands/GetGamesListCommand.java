package commands;

/**
 * Created by tsmit on 11/7/2016.
 */
public class GetGamesListCommand  extends Command{
    @Override
    public String execute() {
        return server.gameList();
    }
}