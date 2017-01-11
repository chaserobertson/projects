package commands;

import java.io.Serializable;

/**
 * Created by tsmit on 11/7/2016.
 */
public interface ICommand extends Serializable {
    /**
     * All of the commands for the command pattern should have this function which uses data passed in from the
     * constructor of the command and stored for later use as well.
     * @pre the cando for the command in the server facade returns true
     * @post the command is called and executed on the server facade and returns a JSON of the updated model
     */
    public String execute ();
}
