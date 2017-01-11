package model.facade.AI;

import commands.FinishTurnCommand;
import debugger.Debugger;
import model.definitions.PlayerState;
import model.facade.server.IServerModelFacade;
import model.facade.server.ServerModelListStorage;
import model.facade.server.ServerPlayingCommandFacade;
import model.overhead.Model;
import model.player.Player;
import params.*;
import persistance.ProxyPersistanceProvider;
import server.ServerFacade;

/**
 * Created by MTAYS on 11/16/2016.
 */
public class AICommands {
    public static void AIEndTurnLoop(Model model,int gameID){
        while (model.getPlayerByIndex(model.getPeripherals().getTurnTracker().getActiveID()).getInternetID() < 0) {
            Player player = model.getPlayerByIndex(model.getPeripherals().getTurnTracker().getActiveID());
            AIInterface aiInterface=retrieveInterface(player.getInternetID());
            if(aiInterface!=null)aiInterface.AITurn(player, model,gameID);
            if(model.getPeripherals().getCurrentState()==PlayerState.Discarding){
                IServerModelFacade facade= ServerModelListStorage.getFacade(gameID);
                if(facade!=null){
                    //ProxyPersistanceProvider.getInstance().writeModel(facade);
                }
                else Debugger.LogMessage("ERROR: AICommands:AIEndTurnLoop:Couldn't find facade");
                break;
            }
            FinishTurnParams params=new FinishTurnParams();
            params.playerIndex=player.getIndex();
            params.gameId=gameID;
            boolean succeded=ServerPlayingCommandFacade.endTurn(params,model,false);
            ServerFacade.getInstance().storeCommand(params);
            if(!succeded)break;
        }
    }

    public static boolean AIDiscard(Player AI, Model model,int gameID){
        AIInterface aiInterface=retrieveInterface(AI.getInternetID());
        if(aiInterface==null)return false;
        return aiInterface.AIDiscard(AI,model,gameID);
    }

    public static void AIRob(Player AI, Model model,int gameID){
        AIInterface aiInterface=retrieveInterface(AI.getInternetID());
        if(aiInterface!=null)aiInterface.AIRob(AI,model,gameID);
    }

    public static void AIReceiveTradeOffer(Player AI, Model model,int gameID){
        AIInterface aiInterface=retrieveInterface(AI.getInternetID());
        if(aiInterface!=null)aiInterface.AIReceiveTradeOffer(AI,model,gameID);
    }

    private static AIInterface retrieveInterface(int internetID){
        if(internetID>-10)return null;
        if(AITypes.AIType(internetID)!=null)return AITypes.AIType(internetID).getInterface();
        return null;
    }
}
