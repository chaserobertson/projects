package persistance.SQLitePersistance;

import debugger.Debugger;
import params.Credentials;
import persistance.interfaces.IUserDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tsmit on 12/5/2016.
 */
public class SQLiteUserDao implements IUserDao {
    @Override
    public void registerUser(Credentials credentials) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            String sql = "INSERT INTO Users (UserName,Password) " +
                    "VALUES ('" + credentials.username + "', '" + credentials.password + "' );";
            Debugger.LogMessage(sql);
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            Debugger.LogMessage("ERROR Registering User!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Credentials> retrieveUsers() {
        try{
            List<Credentials> allUsers = new ArrayList<>();
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM Users;" );
            while(rs.next()){
                String name = rs.getString("UserName");
                String password = rs.getString("Password");
                Credentials credentials = new Credentials(name, password);     // Can we make a constructor that either takes a model or a serialized model?
                allUsers.add(credentials);
            }
            rs.close();
            stmt.close();
            c.close();
            return allUsers;
        }
        catch (Exception e){
            System.out.print("ERROR Retrieving Users!");
            return null;
        }
    }

    @Override
    public void resetUsers() {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:catan.db");
            c.setAutoCommit(false);

            Statement stmt = c.createStatement();
            String sql = "DELETE from Users;";
            stmt.executeUpdate(sql);
            c.commit();
            stmt.close();
            c.close();
        }
        catch (Exception e){
            Debugger.LogMessage("ERROR Resetting Users!");
            e.printStackTrace();
        }
    }
}
