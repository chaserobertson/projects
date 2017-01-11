package commands;

/**
 * Created by tsmit on 11/7/2016.
 */
public class ListAICommand  extends Command{
    @Override
    public String execute() {
        return server.gameListAI();
    }
}