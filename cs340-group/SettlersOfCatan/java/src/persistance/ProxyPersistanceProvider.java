package persistance;

import commands.ICommand;
import model.facade.server.IServerModelFacade;
import model.facade.server.IServerModelFacadeStripped;
import model.facade.server.ServerModelFacade;
import params.Credentials;
import persistance.MockPersistance.MockPersistanceFactory;
import persistance.interfaces.IPersistanceProvider;

import java.util.List;

/**
 * Created by MTAYS on 11/29/2016.
 */
public class ProxyPersistanceProvider implements IPersistanceProvider {
    private static ProxyPersistanceProvider instance=new ProxyPersistanceProvider();
    public static ProxyPersistanceProvider getInstance(){
        if(instance==null)instance=new ProxyPersistanceProvider();
        return instance;
    }
    private static IPersistanceProvider realProvider;
    private ProxyPersistanceProvider(){
        if(realProvider==null){
            MockPersistanceFactory factory=new MockPersistanceFactory();
            realProvider=factory.createPersistanceProvider(factory.createUserDao(),factory.createGameDao(), new ServerModelFacade("not real", -1, null));
        }
    }
    public static void setPersistanceProvider(IPersistanceProvider provider){realProvider=provider;}
    public void setWriteModelDelay(int numCommands){realProvider.setWriteModelDelay(numCommands);}
    @Override
    public void registerUser(Credentials credentials) {
        realProvider.registerUser(credentials);
    }

    @Override
    public List<Credentials> retrieveUsers() {
        return realProvider.retrieveUsers();
    }

    @Override
    public void resetUsers() {
        realProvider.resetUsers();
    }

    @Override
    public boolean writeCommand(ICommand command,int gameID) {
        return realProvider.writeCommand(command,gameID);
    }

    @Override
    public void writeModel(IServerModelFacadeStripped serverModelFacade) {
        realProvider.writeModel(serverModelFacade);
    }

    @Override
    public List<IServerModelFacadeStripped> readGames() {
        return realProvider.readGames();
    }

    @Override
    public List<ICommand> readCommands(int gameID) {
        return realProvider.readCommands(gameID);
    }

    @Override
    public void resetGames() {
        realProvider.resetGames();
    }
}
