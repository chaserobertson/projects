package commands;

import server.IServer;
import server.ServerFacade;

/**
 * Created by tsmit on 12/7/2016.
 */
public class Command implements ICommand{
    IServer server = ServerFacade.getInstance();
    public String execute (){
        return null;
    }
}
