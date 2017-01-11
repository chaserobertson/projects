package persistance.interfaces;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import params.Credentials;

import java.util.List;

/**
 * Created by tsmit on 11/28/2016.
 */
public interface IPersistanceProvider {
    /**
     *
     * @param numCommands the number of commands to store before writing entire model and resetting command storage
     * @pre numCommands is positive
     * @post max command list size is set
     */
    public void setWriteModelDelay(int numCommands);

    /**
     *
     * @param credentials the credentials of the user being registered
     * @pre credentials is not null, and is valid, and is not a duplicate of previous user
     * @post user is added to the database
     */
    public void registerUser(Credentials credentials);

    /**
     *
     * @return a list of all the credentials stored in the database
     * @pre none
     * @post list is populated with all the credentials in the database
     */
    public List<Credentials> retrieveUsers();

    /**
     * @pre none
     * @post table is cleared
     */
    public void resetUsers();
    /**
     * @param command the command which is to be stored
     * @return false if it requires the new model
     * @pre the command is a valid command, to a pre-existing game
     * @post the database stores the command, and returns false if it requires the new model
     */
    public boolean writeCommand(ICommand command, int gameID);//Not certain this is the class wanted

    /**
     *
     * @param serverModelFacade@pre serverModel is a valid serverModelFacade
     * @post the model is written into the database, overwriting the pre existing serverModel if it exists
     */
    public void writeModel(IServerModelFacadeStripped serverModelFacade);//This is also used to create a new Game. AKA if its new, create new

    /**
     * @return a list containing the serverModels which were stored in the database
     * @pre none
     * @post a list containing all stored models
     */
    public List<IServerModelFacadeStripped> readGames();

    /**
     *
     * @param gameID the gameID of the commands retrieved
     * @return List of commands related to the given gameID
     * @pre none
     * @post a list is returned containing the relevant commands, empty if it doesn't exist
     */
    public List<ICommand> readCommands(int gameID);

    /**
     * @pre none
     * @post table is cleared
     */
    public void resetGames();
}
