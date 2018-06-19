package client.turntracker;

import model.definitions.PlayerState;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.peripherals.Peripherals;
import model.player.Player;
import model.serialization.Deserializer;
import params.FinishTurnParams;
import server.HTTPServerProxy;
import shared.definitions.CatanColor;
import client.base.*;

import java.util.List;
import java.util.Observable;


/**
 * Implementation for the turn tracker controller
 */
public class TurnTrackerController extends Controller implements ITurnTrackerController {

    private Player localPlayer;
    private boolean initialized = false;
    private boolean playerInitialized[] = {false,false,false,false};

	public TurnTrackerController(ITurnTrackerView view, Observable observable) {
		super(view);
		observable.addObserver(this);
        controllerHolder.setTurnTrackerController(this);
	}

	@Override
	public void update(Observable o, Object arg) {
        if(!initialized) {initFromModel();}
        else {updateFromModel();}
        PlayerState currentState=GuiModelFacade.getCurrentState();
        if(currentState==null) {getView().updateGameState("End Turn",true);}
        else if(currentState==PlayerState.Playing) {
            if(ModelReferenceFacade.getLocalPlayerIndex()==GuiModelFacade.getCurrentActivePlayerIndex())getView().updateGameState("End Turn",true);
            else getView().updateGameState("Waiting",false);
        }
        else {getView().updateGameState(currentState.toString(),false);}
    }

	@Override
	public ITurnTrackerView getView() {
		return (ITurnTrackerView)super.getView();
	}

	@Override
	public void endTurn() {
        FinishTurnParams finishTurnParams = new FinishTurnParams();
        finishTurnParams.playerIndex = GuiModelFacade.getLocalPlayer().getIndex();
        String JsonModel = HTTPServerProxy.getInstance().finishTurn(finishTurnParams);
        Model model = Deserializer.deserialize(JsonModel);
        ModelReferenceFacade.getInstance().setmodel(model);
	}

	private void updateFromModel() {
        if(ModelReferenceFacade.getModel()==null) return;
        List<Player> playerList = ModelReferenceFacade.getModel().getPlayers();
        if (playerList==null) return;
        Peripherals peripherals = ModelReferenceFacade.getModel().getPeripherals();
        if (peripherals==null) return;
        for(Player player : playerList) {
            if (player!=null) {
                if (playerInitialized[player.getIndex()]==false) {
                    getView().initializePlayer(player.getIndex(),player.getNickname(),player.getColor());
                    playerInitialized[player.getIndex()]=true;
                }
                getView().updatePlayer(player.getIndex(),
                        player.getVictoryPoints(),
                        GuiModelFacade.getCurrentActivePlayerIndex() == player.getIndex(),
                        ((peripherals.getLargestArmy().getPlayer() != null) && (player.getIndex() == peripherals.getLargestArmy().getPlayer().getIndex())),
                        ((peripherals.getLongestRoad().getPlayer() != null) && (player.getIndex() == peripherals.getLongestRoad().getPlayer().getIndex()))
                );
            }
        }
    }
	
	private void initFromModel() {
        if(ModelReferenceFacade.getModel()==null) return;
        localPlayer = GuiModelFacade.getLocalPlayer();
        if(localPlayer==null) return;
        CatanColor color = localPlayer.getColor();
        if(color==null) return;
        getView().setLocalPlayerColor(color);
        List<Player> playerList = ModelReferenceFacade.getModel().getPlayers();
        if (playerList==null) return;
        Peripherals peripherals = ModelReferenceFacade.getModel().getPeripherals();
        if (peripherals==null) return;
        for(Player player : playerList) {
            getView().initializePlayer(player.getIndex(),player.getNickname(),player.getColor());//This is initialize, might only be called once
            playerInitialized[player.getIndex()]=true;
            getView().updatePlayer(player.getIndex(),
                    player.getVictoryPoints(),
                    GuiModelFacade.getCurrentActivePlayerIndex()==player.getIndex(),
                    ((peripherals.getLargestArmy().getPlayer()!=null)&&(player.getIndex()==peripherals.getLargestArmy().getPlayer().getIndex())),
                    ((peripherals.getLongestRoad().getPlayer()!=null)&&(player.getIndex()==peripherals.getLongestRoad().getPlayer().getIndex()))
            );
        }
        initialized = true;
	}

}

