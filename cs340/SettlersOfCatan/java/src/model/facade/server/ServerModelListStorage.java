package model.facade.server;

import client.data.GameInfo;
import com.sun.corba.se.spi.activation.Server;
import commands.ICommand;
import debugger.Debugger;
import model.overhead.Model;
import params.CreateGameRequest;
import persistance.interfaces.IPersistanceProvider;
import persistance.ProxyPersistanceProvider;
import server.ServerFacade;

import java.util.*;

/**
 * Created by MTAYS on 11/3/2016.
 */
public class ServerModelListStorage {

    private static Map<Integer,IServerModelFacade> modelFacades=new HashMap<>();
    private static ServerModelListStorage instance;
    public static ServerModelListStorage getInstance(){
        if(instance==null)instance=new ServerModelListStorage();
        return instance;
    }
    private ServerModelListStorage(){
        //constructor empty, its a static class.
    }
    public static void buildFromPersistance(){
        buildFromPersistance(ProxyPersistanceProvider.getInstance());
    }
    public static void buildFromPersistance(IPersistanceProvider provider){
        try {
            if (provider == null) return;
            List<IServerModelFacadeStripped> listOfModels = provider.readGames();
            if (listOfModels == null) return;
            ServerPlayingCommandFacade.AIAutoLoop=false;
            ServerFacade.getInstance().storingCommands=false;
            modelFacades = new HashMap<>(listOfModels.size());
            for (IServerModelFacadeStripped model : listOfModels) {
                if (model != null) modelFacades.put(model.getGameID(), (IServerModelFacade) model);
            }
            for (int gameID : modelFacades.keySet()) {
                List<ICommand> commands = provider.readCommands(gameID);
                if (commands != null) {
                    for (ICommand command : commands) {
                        if (command != null) {
                            command.execute();
                        }
                    }
                }
            }
        }catch (Exception e){

        }
        finally {
            ServerPlayingCommandFacade.AIAutoLoop=true;
            ServerFacade.getInstance().storingCommands=true;
        }
    }

    public static IServerModelFacade getFacade(String name){
        for (IServerModelFacade facade:modelFacades.values()) {
            if(facade.getGameName().equals(name))return facade;
        }
        return null;
    }
    public static IServerModelFacade getFacade(int gameID){
        if(modelFacades.containsKey(gameID))return modelFacades.get(gameID);
        return null;
    }
    public static IServerModelFacade getFacade(Model model){
        for(IServerModelFacade facade:modelFacades.values()){
            if(facade.getLocalModel()==model)return facade;
        }
        return null;
    }
    public static Map<Integer,IServerModelFacade> getModelFacades(){
        return modelFacades;
    }
    public static Model getModel(String name){
        for (IServerModelFacade facade:modelFacades.values()) {
            if(facade.getGameName().equals(name))return facade.getLocalModel();
        }
        return null;
    }
    public static Model getModel(int gameID){
        if(modelFacades.containsKey(gameID))return modelFacades.get(gameID).getLocalModel();
        return null;
    }
    public static List<GameInfo> getAllGameInfos(){
        List<GameInfo> result=new ArrayList<>(modelFacades.size());
        for(IServerModelFacade facade:modelFacades.values()){
            result.add(facade.getGameInfo());
        }
        return result;
    }
    public static GameInfo createGame(CreateGameRequest params){
        return generateNewGame(params.name,params.randomTiles,params.randomNumbers,params.randomPorts);
    }
    public static GameInfo generateNewGame(String name, boolean randomHexes, boolean randomChits, boolean randomPorts){
        int gameID=1;
        boolean loop=true;
        while(loop){
            loop=false;
            if(modelFacades.containsKey(gameID)){
                loop=true;
                ++gameID;
            }
        }
        ServerModelFacade result=new ServerModelFacade(name,gameID,new Model(randomHexes,randomChits,randomPorts));

        modelFacades.put(gameID,result);
        return result.getGameInfo();
    }
}
