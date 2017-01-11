package model.facade.shared;

import client.data.GameInfo;
import debugger.Debugger;
import model.overhead.Model;
import model.player.Player;
import server.HTTPServerProxy;

import java.util.List;
import java.util.Observable;

/**
 * Created by MTAYS on 9/21/2016.
 */
public class ModelReferenceFacade extends Observable {
    private static Model modelStore;
    private static int localPlayerIndex=-1;
    private static List<GameInfo> gameList=null;
    private static ModelReferenceFacade instance;
    public static ModelReferenceFacade getInstance(){
        if(instance==null)instance=new ModelReferenceFacade();
        return instance;
    }
    private ModelReferenceFacade(){
        //nothing
    }

    public static Model getModel(){return modelStore;}
    public static void setModel(Model Model){
        modelStore = Model;}
    public void setmodel(Model Model){
        modelStore = Model;
        setChanged();
        notifyObservers();
    }//non static version of setModel
    public static Player getPlayer(int i){
        if(modelStore == null) return null;
        if(modelStore.getPlayers().size()<=i)return null;
        return modelStore.getPlayerByIndex(i);
    }
    public static int getActivePlayer(){return modelStore.getActiveID();}
    public static int getLocalPlayerIndex(){
        //if(localPlayerIndex==-1)localPlayerIndex=0;
        if(localPlayerIndex==-1){
            if(modelStore ==null)return 0;
            if(HTTPServerProxy.getInstance()!=null){
                if(HTTPServerProxy.getInstance().getPlayerID()!=-1){
                    if(modelStore.getPlayerByInternetID(HTTPServerProxy.getInstance().getPlayerID())!=null) {
                        localPlayerIndex = modelStore.getPlayerByInternetID(HTTPServerProxy.getInstance().getPlayerID()).getIndex();
                    }
                    else return 0;
                }
                else return 0;
            }
            else return 0;
        }
        return localPlayerIndex;
    }
    public static void setLocalPlayerIndex(int i){
        localPlayerIndex=i;
    }
    public static void setLocalPlayerIndexBasedOnID(int internetID){
        localPlayerIndex=0;
        if(modelStore !=null){
            Player player= modelStore.getPlayerByInternetID(internetID);
            if(player!=null)localPlayerIndex=player.getIndex();
        }
    }
    public static int getModelVersion(){
        if(modelStore ==null){
            return -1;
        }
        return modelStore.getVersion();
    }
    public static void incrementModelVersionNumber(){//Method for indicating model has been updated server side
        if(modelStore ==null){
            Debugger.LogMessage("ModelReferenceFacade:IncrementModelVersionNumber:Model Is null");
            return;
        }
        modelStore.incrementVersion();
    }
    public static void decrementModelVersionNumber(){//Method for forcing a pull from the Server (indicate change client side)
        if(modelStore ==null){
            Debugger.LogMessage("ModelReferenceFacade:DecrementModelVersionNumber:Model Is null");
            return;
        }
        modelStore.decrementVersion();
    }

    //Chase was here
    //AH! Invasion - Matthew
    public void setGameList(List<GameInfo> gameList1) {
        gameList = gameList1;
        setChanged();
        notifyObservers();
    }
    public List<GameInfo> getGameList() {
        return gameList;
    }
    public static void initGameList(List<GameInfo> gameList1) {
        gameList = gameList1;
    }
}
