package persistance.MockPersistance;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MTAYS on 11/29/2016.
 */
public class MockGameDAO implements IGameDao {
    @Override
    public void setWriteModelDelay(int numCommands) {

    }

    @Override
    public boolean writeCommand(ICommand command,int gameID) {
        return true;
    }

    @Override
    public void writeModel(IServerModelFacadeStripped serverModel) {

    }

    @Override
    public List<IServerModelFacadeStripped> readGames() {
        return new ArrayList<>();
    }

    @Override
    public List<ICommand> readCommands(int gameID) {
        return new ArrayList<>();
    }

    @Override
    public void resetGames() {

    }
}
