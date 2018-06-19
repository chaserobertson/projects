package persistance.FlatPersistance;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import params.Credentials;
import persistance.interfaces.IGameDao;
import persistance.interfaces.IPersistanceProvider;
import persistance.interfaces.IUserDao;

import java.io.*;
import java.util.List;

/**
 * Created by MTAYS on 12/2/2016.
 */
public class FlatPersistanceProvider implements IPersistanceProvider{
    private String dirGame=null;
    public static final String fileEnding=".cwmt";
    private final String fileGameStart="model";
    IGameDao gameDao=null;
    IUserDao userDao=null;
    public FlatPersistanceProvider(IGameDao gameDao, IUserDao userDao){
        this.gameDao=gameDao;
        this.userDao=userDao;
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
    public boolean writeCommand(ICommand command,int gameID) {
        return gameDao.writeCommand(command,gameID);
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
    public static void writeObjectToFile(String path,String fileName,Object object){
        try{
            FileOutputStream file = new FileOutputStream(path+"/"+fileName);
            ObjectOutputStream fileOOS = new ObjectOutputStream(file);
            try {
                fileOOS.writeObject(object);
            } catch (Exception e) {
                System.out.print("Exception thrown:Creating and writing");
                e.printStackTrace();
            }
            finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.print("Exception Thrown:Closing file");
                    }
                }
                if (fileOOS != null) {
                    try {
                        fileOOS.close();
                    } catch (Exception e) {
                        System.out.print("Exception Thrown:Closing FileGameOOS");
                    }
                }
            }
        } catch (Exception e){
            System.out.print("Error thrown in Writing Object to file");
        }
    }
    public static Object readObjectFromFile(String path,String fileName){
        return readObjectFromFile(path+"/"+fileName);
    }
    public static Object readObjectFromFile(String path){
        if(path==null)return null;
        Object result=null;
        File file=new File(path);
        if(!file.exists()){
            return null;
        }
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        try {

            fin = new FileInputStream(path);
            ois = new ObjectInputStream(fin);
            result= ois.readObject();

        } catch (Exception ex) {
            System.out.print("FlatPersistanceProvider:Error reading file, path="+path);
            ex.printStackTrace();
        }finally {

            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    System.out.print("FlatPersistanceProvider:Error closing File Input Stream");
                    e.printStackTrace();
                }
            }

            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.out.print("FlatPersistanceProvider:error Closing File Input Stream p2");
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    public static void deleteFile(String path){
        if(path==null)return;
        File file=new File(path);
        if(file.exists())file.delete();
    }
    public static void deleteAllInFolder(String path){
        if(path==null)return;
        File folder=new File(path);
        if(folder.exists()) {
            try {
                for (File file : folder.listFiles()) {
                    if(!file.isDirectory()&&file.getName().contains(fileEnding)){
                        file.delete();
                    }
                }
            }catch (Exception e){
                System.out.print("Exception deleting folder :"+path);
                e.printStackTrace();
            }
        }
    }
}
