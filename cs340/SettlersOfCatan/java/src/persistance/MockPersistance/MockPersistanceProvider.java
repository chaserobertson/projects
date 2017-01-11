package persistance.MockPersistance;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import params.Credentials;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;

import java.util.List;

/**
 * Created by MTAYS on 11/29/2016.
 */
public class MockPersistanceProvider implements IPersistanceProvider {
    public IGameDao gameDAO=new MockGameDAO();
    public IUserDao userDAO=new MockUserDAO();

    public MockPersistanceProvider(IUserDao userDao,IGameDao gameDao){
        this.gameDAO=gameDao;
        this.userDAO=userDao;
    }

    public void setWriteModelDelay(int numCommands) {gameDAO.setWriteModelDelay(numCommands);}

    /**
     *
     * @param credentials the credentials of the user being registered
     * @pre credentials is not null, and is valid, and is not a duplicate of previous user
     * @post user is added to the database
     */
    public void registerUser(Credentials credentials){userDAO.registerUser(credentials);}

    /**
     *
     * @return a list of all the credentials stored in the database
     * @pre none
     * @post list is populated with all the credentials in the database
     */
    public List<Credentials> retrieveUsers(){return userDAO.retrieveUsers();}

    /**
     * @pre none
     * @post datbase is cleared
     */
    public void resetUsers(){userDAO.resetUsers();}
    /**
     * @param command the command which is to be stored
     * @return false if it requires the new model
     * @pre the command is a valid command, to a pre-existing game
     * @post the database stores the command, and returns false if it requires the new model
     */
    public boolean writeCommand(ICommand command,int gameID){return gameDAO.writeCommand(command,gameID);}

    /**
     *
     * @param serverModelFacade@pre serverModel is a valid serverModelFacade
     * @post the model is written into the database, overwriting the pre existing serverModel if it exists
     */
    public void writeModel(IServerModelFacadeStripped serverModelFacade){gameDAO.writeModel(serverModelFacade);}

    /**
     * @return a list containing the serverModels which were stored in the database
     * @pre none
     * @post a list containing all stored models
     */
    public List<IServerModelFacadeStripped> readGames(){return gameDAO.readGames();}

    /**
     *
     * @param gameID the gameID of the commands retrieved
     * @return List of commands related to the given gameID
     * @pre none
     * @post a list is returned containing the relevant commands, empty if it doesn't exist
     */
    public List<ICommand> readCommands(int gameID){return gameDAO.readCommands(gameID);}

    /**
     * @pre none
     * @post database is cleared
     */
    public void resetGames(){gameDAO.resetGames();}
}
