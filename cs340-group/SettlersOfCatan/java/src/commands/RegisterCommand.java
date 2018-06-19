package commands;

import params.Credentials;

/**
 * Created by tsmit on 11/7/2016.
 */
public class RegisterCommand  extends Command{
    private Credentials params;

    public RegisterCommand(Credentials params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.userRegister(params);
    }
}