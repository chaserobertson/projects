package client.maritime;

import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.resources.ResourcePile;
import model.serialization.Deserializer;
import params.MaritimeTradeParams;
import server.HTTPServerProxy;
import shared.definitions.*;
import client.base.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Implementation for the maritime trade controller
 */
public class MaritimeTradeController extends Controller implements IMaritimeTradeController {

	private int tradeRatio = 4;
	private IMaritimeTradeOverlay tradeOverlay;
	private ResourceType getResource;
	private ResourceType giveResource;
	private ResourcePile availableResources;
	
	public MaritimeTradeController(IMaritimeTradeView tradeView, IMaritimeTradeOverlay tradeOverlay,
								   Observable observable) {
		
		super(tradeView);

		observable.addObserver(this);

		controllerHolder.setMaritimeTradeController(this);

		setTradeOverlay(tradeOverlay);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(!GuiModelFacade.isLocalPlayerTurn()){
			getTradeView().enableMaritimeTrade(false);
		}
		else{
			getTradeView().enableMaritimeTrade(true);
		}

	}

	public IMaritimeTradeView getTradeView() {
		
		return (IMaritimeTradeView)super.getView();
	}
	
	public IMaritimeTradeOverlay getTradeOverlay() {
		return tradeOverlay;
	}

	public void setTradeOverlay(IMaritimeTradeOverlay tradeOverlay) {
		this.tradeOverlay = tradeOverlay;
	}

	@Override
	public void startTrade() {
		//Matthew- Not certain if this helps, but I've implemented a GuiModelFacade.getPlayerBestMaritimeTrade command
		//It returns the best ratio for trading a given resourceType. I think this could be helpful.
		//I.E. if the player has no ports, it returns 4 for all. If they have a 3, it will return at least a 3 for all
		//If they have a wheat it returns a 2 for the wheat.
		getTradeOverlay().hideGiveOptions();
		getTradeOverlay().hideGetOptions();
		getTradeOverlay().setTradeEnabled(false);
		updateGiveDisplay();
		getTradeOverlay().showModal();
	}

	@Override
	public void makeTrade() {
		int playerIndex = GuiModelFacade.getCurrentActivePlayerIndex();
		MaritimeTradeParams params = new MaritimeTradeParams(playerIndex, GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, giveResource), giveResource, getResource);
		String JsonModel = HTTPServerProxy.getInstance().maritimeTrade(params);
		Model model = Deserializer.deserialize(JsonModel);
		ModelReferenceFacade.getInstance().setmodel(model);
		getTradeOverlay().closeModal();
	}

	@Override
	public void cancelTrade() {

		getTradeOverlay().closeModal();
	}

	@Override
	public void setGetResource(ResourceType resource) {
		int playerIndex = GuiModelFacade.getCurrentActivePlayerIndex();
		getTradeOverlay().selectGetOption(resource, 1);
		getTradeOverlay().setTradeEnabled(true);
		getResource = resource;
	}

	@Override
	public void setGiveResource(ResourceType resource) {
		int playerIndex = GuiModelFacade.getCurrentActivePlayerIndex();
		getTradeOverlay().selectGiveOption(resource, GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, resource));
		updateGetDisplay();
		giveResource = resource;
	}

	@Override
	public void unsetGetValue() {
		getTradeOverlay().hideGetOptions();
		getTradeOverlay().setTradeEnabled(false);
		updateGetDisplay();
		getResource = null;

	}

	@Override
	public void unsetGiveValue() {
		getTradeOverlay().hideGiveOptions();
		updateGiveDisplay();
		giveResource = null;
	}

	private void updateGetDisplay(){
		ResourceType[] allTypes = new ResourceType[5];
		allTypes[0] = ResourceType.WHEAT;
		allTypes[1] = ResourceType.WOOD;
		allTypes[2] = ResourceType.SHEEP;
		allTypes[3] = ResourceType.BRICK;
		allTypes[4] = ResourceType.ORE;
		getTradeOverlay().showGetOptions(allTypes);
	}

	private void updateGiveDisplay(){
		int playerIndex = GuiModelFacade.getCurrentActivePlayerIndex();
		availableResources = GuiModelFacade.getLocalPlayer().getResourceAggregation();
		List<ResourceType> typeList = new ArrayList<>();
		int numWheats = availableResources.getQuantityResourceType(ResourceType.WHEAT);
		int numWoods = availableResources.getQuantityResourceType(ResourceType.WOOD);
		int numSheeps = availableResources.getQuantityResourceType(ResourceType.SHEEP);
		int numBricks = availableResources.getQuantityResourceType(ResourceType.BRICK);
		int numOres = availableResources.getQuantityResourceType(ResourceType.ORE);
		if(numWheats >= GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, ResourceType.WHEAT)){
			typeList.add(ResourceType.WHEAT);
		}
		if(numWoods >= GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, ResourceType.WOOD)){
			typeList.add(ResourceType.WOOD);
		}
		if(numSheeps >= GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, ResourceType.SHEEP)){
			typeList.add(ResourceType.SHEEP);
		}
		if(numBricks >= GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, ResourceType.BRICK)){
			typeList.add(ResourceType.BRICK);
		}
		if(numOres >= GuiModelFacade.getPlayerBestMaritimeRatio(playerIndex, ResourceType.ORE)){
			typeList.add(ResourceType.ORE);
		}
		ResourceType[] typesToDisplay = new ResourceType[typeList.size()];
		for (int i = 0; i < typeList.size(); i++) {
			typesToDisplay[i] = typeList.get(i);
		}
		getTradeOverlay().showGiveOptions(typesToDisplay);
	}
}

