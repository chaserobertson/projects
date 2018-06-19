package commands;

import login.Login;
import params.Credentials;

/**
 * Created by tsmit on 11/7/2016.
 */
public class LoginCommand  extends Command{
    private Credentials params;

    public LoginCommand(Credentials params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.userLogin(params);
    }
}
