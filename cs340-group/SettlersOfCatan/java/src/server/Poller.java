package server;

import client.data.GameInfo;
import debugger.Debugger;
import model.facade.shared.ModelReferenceFacade;
import model.facade.shared.SerializeFacade;
import model.overhead.Model;
import model.serialization.Deserializer;
import params.Version;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Poller
 */

public class Poller {

    private final long POLLINGDELAY = 3000;
    private Timer timer;
    private IServer server;
    public enum PollType {LIST,MODEL}
    private static Poller instance;
    public static boolean forceModelPull=false;//Kinda like force pull. This forces the model to replace. - Matthew

    public Poller() {
        server = HTTPServerProxy.getInstance();
    }

    public Poller(IServer server) {
        this.server = server;
    }

    public void destroy() {
        timer.cancel();
        timer.purge();
    }

    public void setTimer(PollType pollType) {
        timer = new Timer();
        switch(pollType) {
            case LIST:
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        pollList();
                    }
                }, 0, POLLINGDELAY);
                break;
            case MODEL:
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        pollModel();
                    }
                }, 0, POLLINGDELAY);
                break;
            default:
                Debugger.LogMessage("invalid poll type");
        }
    }

    /**
     * Description: Sends a polling request to the server to check for the model
     * version number and updates the model if the version numbers are different.
     */
    private void pollModel() {
        if(ModelReferenceFacade.getModelVersion() < 0) return;
        Version clientVersion=new Version(-1);
        if(!forceModelPull) clientVersion = new Version(ModelReferenceFacade.getModelVersion());
        String serverJSON = server.gameModel(clientVersion);
        if(serverJSON.equals("True")){
            return;
        }
        Model serverModel = SerializeFacade.deserializeModelFromJSON(serverJSON);
        if ((serverModel != null && serverModel.getVersion() > clientVersion.versionNumber) || forceModelPull) {
            ModelReferenceFacade.getInstance().setmodel(serverModel);
        }
    }

    private void pollList() {
        if(ModelReferenceFacade.getInstance().getGameList() != null) {
            String listJSON = HTTPServerProxy.getInstance().gameList();
            List<GameInfo> gameList = Deserializer.deserializeGameInfoList(listJSON);
            if(!gameInfoListCompare(ModelReferenceFacade.getInstance().getGameList(),gameList)){
                ModelReferenceFacade.getInstance().setGameList(gameList);
            }
        }
    }
    private boolean gameInfoListCompare(List<GameInfo> list1,List<GameInfo> list2){
        if(list1==null)return list2==null;
        else if(list2==null)return false;
        if(list1.size()!=list2.size())return false;
        for(int i=0;i<list1.size();++i){
            if(list1.get(i)==null){
                if(list2.get(i)!=null)return false;
            }
            else if(list2.get(i)==null)return false;
            else{
                GameInfo gameInfo1=list1.get(i),gameInfo2=list2.get(i);
                if(gameInfo1==null&&gameInfo2!=null)return false;
                else if(gameInfo2==null&&gameInfo1!=null)return false;
                else if(gameInfo1==null)continue;
                if(gameInfo1.getId()!=gameInfo2.getId())return false;
                if(!gameInfo1.getTitle().equals(gameInfo2.getTitle()))return false;
                if(gameInfo1.getPlayers().size()!=gameInfo2.getPlayers().size())return false;
                for(int j=0;j<gameInfo1.getPlayers().size();++j){
                    if(gameInfo1.getPlayers().get(j)==null){
                        if(gameInfo2.getPlayers().get(j)!=null)return false;
                    }
                    else if(!gameInfo1.getPlayers().get(j).equals(gameInfo2.getPlayers().get(j)))return false;
                }
            }
        }

        return true;
    }
}