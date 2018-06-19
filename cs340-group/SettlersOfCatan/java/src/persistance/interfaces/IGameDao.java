package persistance.interfaces;

import model.facade.server.IServerModelFacadeStripped;

import java.util.List;
import commands.ICommand;
/**
 * Created by MTAYS on 11/28/2016.
 */
public interface IGameDao {
    /**
     *
     * @param numCommands the number of commands to store before writing entire model and resetting command storage
     * @pre numCommands is positive
     * @post max command list size is set
     */
    public void setWriteModelDelay(int numCommands);

    /**
     * @param command the command which is to be stored
     * @return false if it requires the new model
     * @pre the command is a valid command, to a pre-existing game
     * @post the database stores the command, and returns false if it requires the new model
     */
    public boolean writeCommand(ICommand command,int gameID);//Not certain this is the class wanted

    /**
     * @param serverModel the server version of the model which is required to be stored
     * @pre serverModel is a valid serverModelFacade
     * @post the model is written into the database, overwriting the pre existing serverModel if it exists
     */
    public void writeModel(IServerModelFacadeStripped serverModel);//Slightly changed from UML

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
     * @post database is cleared
     */
    public void resetGames();
}
