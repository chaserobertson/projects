package persistance.FlatPersistance;

import commands.ICommand;
import model.facade.server.IServerModelFacadeStripped;
import persistance.interfaces.IGameDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static persistance.FlatPersistance.FlatPersistanceProvider.fileEnding;

/**
 * Created by tsmit on 12/5/2016.
 */
public class FlatGameDao implements IGameDao{
    private int maxCommands=10;//DEFAULT
    private String dirGame=null;
    private String dirCommands=null;
    private final String fileGameStart="model";
    private final String fileCommandsStart="commands";
    public FlatGameDao(){
        try {
            File dirMain = new File("flatPersistenceV01");
            if(!dirMain.exists()&&!dirMain.mkdirs())throw new Exception();
            File dirGame = new File(dirMain.getPath() + "/game");
            File dirCommands = new File(dirMain.getPath() + "/commands");
            this.dirGame=dirGame.getPath();
            this.dirCommands=dirCommands.getPath();
            if((!dirGame.exists()&&!dirGame.mkdirs())||
                    (!dirCommands.exists()&&!dirCommands.mkdirs()))
                throw new Exception();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void setWriteModelDelay(int numCommands) {
        maxCommands=numCommands;
    }

    @Override
    public boolean writeCommand(ICommand command,int gameID) {
        if(command==null)return false;
        List<ICommand> preExistantList=readCommands(gameID);
        int preExistant=0;
        if(preExistantList!=null)preExistant=preExistantList.size();
        if(preExistant>=maxCommands)return false;
        preExistantList.add(command);
        FlatPersistanceProvider.writeObjectToFile(dirCommands,fileCommandsStart+Integer.toString(gameID)+ fileEnding,preExistantList);
        return true;
    }

    @Override
    public void writeModel(IServerModelFacadeStripped serverModel) {
        if(serverModel==null||dirGame==null)return;
        FlatPersistanceProvider.writeObjectToFile(dirGame,fileGameStart+Integer.toString(serverModel.getGameID())+ fileEnding,serverModel);
        FlatPersistanceProvider.writeObjectToFile(dirCommands,fileCommandsStart+Integer.toString(serverModel.getGameID())+fileEnding,new ArrayList<ICommand>());
    }

    @Override
    public List<IServerModelFacadeStripped> readGames() {
        File folder = new File(dirGame);
        if(!folder.exists())return null;
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles==null)return new ArrayList<>();
        List<IServerModelFacadeStripped> result = new ArrayList<>(listOfFiles.length);
        for(File file:listOfFiles){
            String name=file.getName();
            if(name.substring(0,fileGameStart.length()).equals(fileGameStart)&&
                    name.substring(name.length()- fileEnding.length()).equals(fileEnding)){
                //Theoretically the above will return true if the file begins with "model" end ends with "cwmt"
                Object object=FlatPersistanceProvider.readObjectFromFile(file.getPath());
                if(object!=null&&object instanceof IServerModelFacadeStripped){
                    result.add((IServerModelFacadeStripped)object);
                }
            }
            else{
                System.out.print("FlatGameDao: rejected to read file: "+file.getPath()+" because name does not match");
            }
        }
        return result;
    }

    @Override
    public List<ICommand> readCommands(int gameID) {
        List<ICommand> result=new ArrayList<>();
        try {
            String probableFileName=fileCommandsStart + Integer.toString(gameID) + fileEnding;
            String probableFile = dirCommands+"/"+probableFileName;
            File commandFile = new File(probableFile);
            if (commandFile.exists()) {
                Object object = FlatPersistanceProvider.readObjectFromFile(dirCommands,probableFileName);
                if (object != null && object instanceof List) {
                    List pulledList = (List) object;
                    if (pulledList.size() > 0) {
                        if (pulledList.get(0) instanceof ICommand) {
                            result = pulledList;
                        }
                        else{

                            System.out.print("FlatGameDao: command file \""+commandFile.getPath()+"\" does not contain ICommand");
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.print("FlatGameDao:ReadCommands:Exception thrown");
        }
        return result;
    }


    @Override
    public void resetGames() {
        FlatPersistanceProvider.deleteAllInFolder(dirGame);
        FlatPersistanceProvider.deleteAllInFolder(dirCommands);
    }
}
