package persistance.MockPersistance;

import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceFactory;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;

/**
 * Created by MTAYS on 11/29/2016.
 */
public class MockPersistanceFactory implements IPersistanceFactory {
    @Override
    public void createDatabase() {

    }

    @Override
    public IUserDao createUserDao() {
        return new MockUserDAO();
    }

    @Override
    public IGameDao createGameDao() {
        return new MockGameDAO();
    }

    @Override
    public IPersistanceProvider createPersistanceProvider(IUserDao userDao, IGameDao gameDao, IServerModelFacadeStripped sampleFacade) {
        return new MockPersistanceProvider(userDao,gameDao);
    }
}
