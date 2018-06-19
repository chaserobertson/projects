package persistance.SQLitePersistance;

import debugger.Debugger;
import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceFactory;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;
import java.sql.*;

/**
 * Created by tsmit on 12/5/2016.
 */
public class SQLitePersistanceFactory implements IPersistanceFactory {
    private final String CREATETABLESSQL = "CREATE TABLE IF NOT EXISTS Commands (CommandID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , CommandGameID INTEGER NOT NULL , CommandString TEXT, CommandType TEXT);\n" +
            "CREATE TABLE IF NOT EXISTS Games (GameID INTEGER PRIMARY KEY  NOT NULL ,GameName TEXT,GameModel TEXT);\n" +
            "CREATE TABLE IF NOT EXISTS Users (UserID INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , UserName TEXT, Password TEXT);\n";
    @Override
    public void createDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");

            Statement stmt = c.createStatement();
            stmt.executeUpdate(CREATETABLESSQL);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            Debugger.LogMessage("ERROR creating Database!");
            e.printStackTrace();
        }
    }

    @Override
    public IUserDao createUserDao() {
        return new SQLiteUserDao();
    }

    @Override
    public IGameDao createGameDao() {
        return new SQLiteGameDao();
    }

    @Override
    public IPersistanceProvider createPersistanceProvider(IUserDao userDao, IGameDao gameDao, IServerModelFacadeStripped sampleFacade) {
        return new SQLitePersistanceProvider(userDao, gameDao,sampleFacade);
    }
}
