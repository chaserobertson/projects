package persistance.SQLitePersistance;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import params.Credentials;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;

import java.util.List;

/**
 * Created by tsmit on 12/5/2016.
 */
public class SQLitePersistanceProvider implements IPersistanceProvider{

    private IUserDao userDao;
    private IGameDao gameDao;

    public SQLitePersistanceProvider(IUserDao userDao, IGameDao gameDao,IServerModelFacadeStripped sampleFacade){
        this.userDao = userDao;
        this.gameDao = gameDao;
        if(gameDao instanceof SQLiteGameDao){
            ((SQLiteGameDao)gameDao).setSampleFacade(sampleFacade);
        }
    }

    @Override
    public void setWriteModelDelay(int numCommands) {
        gameDao.setWriteModelDelay(numCommands);
    }

    @Override
    public void registerUser(Credentials credentials) {
        userDao.registerUser(credentials);
    }

    @Override
    public List<Credentials> retrieveUsers() {
        return userDao.retrieveUsers();
    }

    @Override
    public void resetUsers() {
        userDao.resetUsers();
    }

    @Override
    public boolean writeCommand(ICommand command, int gameID) {
        return gameDao.writeCommand(command, gameID);
    }

    @Override
    public void writeModel(IServerModelFacadeStripped serverModelFacade) {
        gameDao.writeModel(serverModelFacade);
    }

    @Override
    public List<IServerModelFacadeStripped> readGames() {
        return gameDao.readGames();
    }

    @Override
    public List<ICommand> readCommands(int gameID) {
        return gameDao.readCommands(gameID);
    }

    @Override
    public void resetGames() {
        gameDao.resetGames();
    }
}
