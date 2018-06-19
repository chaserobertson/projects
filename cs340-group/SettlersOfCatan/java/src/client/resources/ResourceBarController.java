package client.resources;

import java.util.*;

import client.base.*;
import debugger.Debugger;
import model.definitions.ResourceHand;
import model.facade.client.ClientDevCardFacade;
import model.facade.client.ClientPlayingCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.player.Player;
import shared.definitions.ResourceType;


/**
 * Implementation for the resource bar controller
 */
public class ResourceBarController extends Controller implements IResourceBarController {

	private Map<ResourceBarElement, IAction> elementActions;
	
	public ResourceBarController(IResourceBarView view, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setResourceBarController(this);

		elementActions = new HashMap<>();
	}

	@Override
	public void update(Observable o, Object arg) {
		//Probably some sort of initialize from Model
		initializeViewsFromModel();
	}
	private void initializeViewsFromModel(){
		//Matthew was in all things this function
		ResourceHand localPlayerHand= GuiModelFacade.getLocalPlayerResourceHand();
		if(localPlayerHand==null)return;
		getView().setElementAmount(ResourceBarElement.BRICK,localPlayerHand.getResource(ResourceType.BRICK));
		getView().setElementAmount(ResourceBarElement.WHEAT,localPlayerHand.getResource(ResourceType.WHEAT));
		getView().setElementAmount(ResourceBarElement.WOOD,localPlayerHand.getResource(ResourceType.WOOD));
		getView().setElementAmount(ResourceBarElement.SHEEP,localPlayerHand.getResource(ResourceType.SHEEP));
		getView().setElementAmount(ResourceBarElement.ORE,localPlayerHand.getResource(ResourceType.ORE));
		Player localPlayer=GuiModelFacade.getLocalPlayer();
		if(localPlayer == null) return;
		getView().setElementAmount(ResourceBarElement.ROAD,localPlayer.getRemainingRoads());
		getView().setElementAmount(ResourceBarElement.SETTLEMENT,localPlayer.getRemainingSettlements());
		getView().setElementAmount(ResourceBarElement.CITY,localPlayer.getRemainingCities());
		getView().setElementAmount(ResourceBarElement.SOLDIERS,localPlayer.getArmySize());

		boolean myTurn=ModelReferenceFacade.getLocalPlayerIndex()==GuiModelFacade.getCurrentActivePlayerIndex();
		getView().setElementEnabled(ResourceBarElement.ROAD, myTurn&&ClientPlayingCommandFacade.canBuildARoad(ModelReferenceFacade.getLocalPlayerIndex(),false));
		getView().setElementEnabled(ResourceBarElement.SETTLEMENT,myTurn&&ClientPlayingCommandFacade.canBuildSettlement(ModelReferenceFacade.getLocalPlayerIndex(),false));
		getView().setElementEnabled(ResourceBarElement.CITY,myTurn&&ClientPlayingCommandFacade.canBuildCity(ModelReferenceFacade.getLocalPlayerIndex()));
		getView().setElementEnabled(ResourceBarElement.BUY_CARD,myTurn&&ClientPlayingCommandFacade.canBuyDevCard(ModelReferenceFacade.getLocalPlayerIndex()));
		getView().setElementEnabled(ResourceBarElement.PLAY_CARD,myTurn);
		//getView().setElementEnabled(ResourceBarElement.PLAY_CARD,ClientPlayingCommandFacade.canUseAnyDevCard(ModelReferenceFacade.getLocalPlayerIndex()));
	}

	@Override
	public IResourceBarView getView() {
		return (IResourceBarView)super.getView();
	}

	/**
	 * Sets the action to be executed when the specified resource bar element is clicked by the user
	 * 
	 * @param element The resource bar element with which the action is associated
	 * @param action The action to be executed
	 */
	public void setElementAction(ResourceBarElement element, IAction action) {

		elementActions.put(element, action);
	}

	@Override
	public void buildRoad() {
		executeElementAction(ResourceBarElement.ROAD);
	}

	@Override
	public void buildSettlement() {
		executeElementAction(ResourceBarElement.SETTLEMENT);
	}

	@Override
	public void buildCity() {
		executeElementAction(ResourceBarElement.CITY);
	}

	@Override
	public void buyCard() {
		executeElementAction(ResourceBarElement.BUY_CARD);
	}

	@Override
	public void playCard() {
		executeElementAction(ResourceBarElement.PLAY_CARD);
	}
	
	private void executeElementAction(ResourceBarElement element) {
		
		if (elementActions.containsKey(element)) {
			
			IAction action = elementActions.get(element);
			action.execute();
		}
	}

}

