package commands;

import params.AddAIRequest;

/**
 * Created by tsmit on 11/7/2016.
 */
public class AddAICommand  extends Command{
    private AddAIRequest params;

    public AddAICommand (AddAIRequest params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.gameAddAI(params);
    }
}
