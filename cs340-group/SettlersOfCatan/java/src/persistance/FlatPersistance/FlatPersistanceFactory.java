package persistance.FlatPersistance;

import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceFactory;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;

/**
 * Created by tsmit on 12/5/2016.
 */
public class FlatPersistanceFactory implements IPersistanceFactory {
    @Override
    public void createDatabase() {
       //Nothing.
    }

    @Override
    public IUserDao createUserDao() {
        return new FlatUserDao();
    }

    @Override
    public IGameDao createGameDao() {
        return new FlatGameDao();
    }

    @Override
    public IPersistanceProvider createPersistanceProvider(IUserDao userDao, IGameDao gameDao, IServerModelFacadeStripped sampleFacade) {
        return new FlatPersistanceProvider(gameDao,userDao);
    }
}
