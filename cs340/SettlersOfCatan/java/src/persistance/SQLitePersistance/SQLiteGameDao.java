package persistance.SQLitePersistance;

import com.google.gson.Gson;
import commands.ICommand;
import debugger.Debugger;
import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsmit on 12/5/2016.
 */
public class SQLiteGameDao implements IGameDao{
    private int numCommands;
    private IServerModelFacadeStripped sampleFacade=null;
    public void setSampleFacade(IServerModelFacadeStripped sampleFacade){
        this.sampleFacade=sampleFacade;
    }

    @Override
    public void setWriteModelDelay(int numCommands) {
        this.numCommands = numCommands;
    }

    @Override
    public boolean writeCommand(ICommand command, int gameID) {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Games where GameID=" + gameID + ";" );
            if(rs.next()){
                rs = stmt.executeQuery( "SELECT * FROM Commands where CommandGameID=" + gameID + ";" );
                int numCommands = 0;
                while(rs.next()){
                    numCommands++;
                }
                //Debugger.LogMessage("MaxCommands: " + this.numCommands + ", DBCommands: " + numCommands);
                if(numCommands>=this.numCommands){
                    stmt.close();
                    c.commit();
                    c.close();
                    return false;
                }
                else{
                    String sql = "INSERT INTO Commands (CommandGameID,CommandString) VALUES (?, ?)";
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(command);
                    byte[] commandAsBytes = baos.toByteArray();
                    PreparedStatement pstmt = c.prepareStatement(sql);
                    ByteArrayInputStream bais = new ByteArrayInputStream(commandAsBytes);
                    pstmt.setInt(1, gameID);
                    pstmt.setBinaryStream(2, bais, commandAsBytes.length);
                    pstmt.executeUpdate();
                    pstmt.close();
                    stmt.close();
                    c.commit();
                    c.close();
                    return true;
                }
            }
            else{
                stmt.close();
                c.commit();
                c.close();
                return false;
            }
        }
        catch(Exception e){
            Debugger.LogMessage("ERROR Writing Commands!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void writeModel(IServerModelFacadeStripped serverModel) {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            int gameID = serverModel.getGameID();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Games where GameID=" + gameID + ";" );
            String sql;
            if(!rs.next()){
                sql = "INSERT INTO Games (GameName,GameModel) " +
                        "VALUES ('" + serverModel.getGameName() + "', '" + serverModel.getLocalModelString() + "');";
                PreparedStatement pstmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating game failed, no rows affected.");
                }
                else{
                    ResultSet generatedKeys = pstmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        Debugger.LogMessage("PrevGameID=" + serverModel.getGameID());
                        serverModel.setGameID(generatedKeys.getInt(1));
                        Debugger.LogMessage("RetrievedGameID=" + serverModel.getGameID());
                    }
                    else {
                        throw new SQLException("Creating game failed, no ID obtained.");
                    }
                    generatedKeys.close();
                }
            }
            else{
                sql = "DELETE from Commands where CommandGameID=" + gameID + ";";
                stmt.executeUpdate(sql);
                c.commit();
                sql = "UPDATE Games SET GameModel = '" + serverModel.getLocalModelString() + "' where GameID=" + gameID + ";";
                stmt.executeUpdate(sql);
            }
            rs.close();
            c.commit();
            stmt.close();
            c.close();
        }
        catch (Exception e){
            Debugger.LogMessage("ERROR Writing Model!");
            e.printStackTrace();
        }
    }

    @Override
    public List<IServerModelFacadeStripped> readGames() {
        try{
            List<IServerModelFacadeStripped> allGames = new ArrayList<>();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Games;" );
            while(rs.next()){
                int id = rs.getInt("GameID");
                String name = rs.getString("GameName");
                String modelStr = rs.getString("GameModel");
                IServerModelFacadeStripped modelFacade = sampleFacade.generateServerModel(modelStr,id,name);//From Matthew, I created the "constructor"
                allGames.add(modelFacade);
            }
            rs.close();
            stmt.close();
            c.close();
            return allGames;
        }
        catch (Exception e){
            Debugger.LogMessage("ERROR Reading Games!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<ICommand> readCommands(int gameID) {
        try{
            List<ICommand> allCommands = new ArrayList<>();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Commands where CommandGameID=" + gameID + ";" );
            while(rs.next()){
                byte[] st = (byte[]) rs.getObject("CommandString");
                ByteArrayInputStream baip = new ByteArrayInputStream(st);
                ObjectInputStream ois = new ObjectInputStream(baip);
                ICommand cmd = (ICommand) ois.readObject();
                allCommands.add(cmd);
            }
            rs.close();
            stmt.close();
            c.close();
            return allCommands;
        }
        catch (Exception e){
            Debugger.LogMessage("ERROR Reading Commands!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void resetGames() {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            String sql = "DELETE * from Games;";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
        }
        catch (Exception e){
            Debugger.LogMessage("ERROR Deleting Games!");
            e.printStackTrace();
        }
    }
}
