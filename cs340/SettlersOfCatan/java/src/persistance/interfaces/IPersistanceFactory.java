package persistance.interfaces;

import model.facade.server.IServerModelFacadeStripped;

/**
 * Created by MTAYS on 11/28/2016.
 */
public interface IPersistanceFactory {

    /**
     *
     */
    public void createDatabase();//Not certain what to make for this...

    /**
     *
     * @return a userDao implementing the interface
     * @pre none
     * @post properly formatted DAO
     */
    public IUserDao createUserDao();

    /**
     *
     * @return a gameDao implementing the interface
     * @pre none
     * @post a properly formatted DAO
     */
    public IGameDao createGameDao();

    /**
     *
     * @param userDao the userDao to be used with the persistanceProvider
     * @param gameDao the gameDao to be used with the persistanceProvider
     * @param sampleFacade
     * @return a new persistanceProvider containing the appropriate user and game daos
     * @pre user and game DAO are valid and not null
     * @post a new persistanceProvider containing those DAOS is created
     */
    public IPersistanceProvider createPersistanceProvider(IUserDao userDao, IGameDao gameDao, IServerModelFacadeStripped sampleFacade);
}
