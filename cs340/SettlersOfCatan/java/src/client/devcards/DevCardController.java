package client.devcards;

import model.definitions.EnumConverter;
import model.facade.client.ClientDevCardFacade;
import model.facade.client.ClientGeneralCommandFacade;
import model.facade.client.ClientPlayingCommandFacade;
import model.facade.server.ServerDevCardFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import shared.definitions.DevCardType;
import shared.definitions.ResourceType;
import client.base.*;

import java.util.Map;
import java.util.Observable;


/**
 * "Dev card" controller implementation
 */
public class DevCardController extends Controller implements IDevCardController {

	private IBuyDevCardView buyCardView;
	private IAction soldierAction;
	private IAction roadAction;
	
	/**
	 * DevCardController constructor
	 * 
	 * @param view "Play dev card" view
	 * @param buyCardView "Buy dev card" view
	 * @param soldierAction Action to be executed when the user plays a soldier card.  It calls "mapController.playSoldierCard()".
	 * @param roadAction Action to be executed when the user plays a road building card.  It calls "mapController.playRoadBuildingCard()".
	 */
	public DevCardController(IPlayDevCardView view, IBuyDevCardView buyCardView,
							 IAction soldierAction, IAction roadAction, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setDevCardController(this);

		this.buyCardView = buyCardView;
		this.soldierAction = soldierAction;
		this.roadAction = roadAction;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(ModelReferenceFacade.getModel()==null)return;
		Map<DevCardType,Integer> ownedDevs=GuiModelFacade.getSumsOfAllDevCards(ModelReferenceFacade.getLocalPlayerIndex());
		Map<DevCardType,Integer> ownedPlayableDevs=GuiModelFacade.getSumsOfPlayableDevCards(ModelReferenceFacade.getLocalPlayerIndex());
		for(int i=0;i<5;++i){
			DevCardType type= EnumConverter.DevCardType(i);
			getPlayCardView().setCardAmount(type,ownedDevs.get(type));
			if(type.equals(DevCardType.MONUMENT)){
				getPlayCardView().setCardEnabled(type,(ownedDevs.get(type)>0&&ClientDevCardFacade.canUseMonument(ModelReferenceFacade.getLocalPlayerIndex())));
			}
			else getPlayCardView().setCardEnabled(type,(ownedPlayableDevs.get(type)>0)&& ServerDevCardFacade.canUseSomeDevCard(ModelReferenceFacade.getLocalPlayerIndex(),ModelReferenceFacade.getModel()));//This is a fancy way of saying if it has more than 0 cards enable
		}
	}

	public IPlayDevCardView getPlayCardView() {
		return (IPlayDevCardView)super.getView();
	}

	public IBuyDevCardView getBuyCardView() {
		return buyCardView;
	}

	@Override
	public void startBuyCard() {
		getBuyCardView().showModal();
	}

	@Override
	public void cancelBuyCard() {
		getBuyCardView().closeModal();
	}

	@Override
	public void buyCard() {
		ClientPlayingCommandFacade.buyDevCard(ModelReferenceFacade.getLocalPlayerIndex());
		getBuyCardView().closeModal();
	}

	@Override
	public void startPlayCard() {
		if(ModelReferenceFacade.getModel()==null)return;
		getPlayCardView().showModal();
		Map<DevCardType,Integer> ownedDevs=GuiModelFacade.getSumsOfAllDevCards(ModelReferenceFacade.getLocalPlayerIndex());
		Map<DevCardType,Integer> ownedPlayableDevs=GuiModelFacade.getSumsOfPlayableDevCards(ModelReferenceFacade.getLocalPlayerIndex());
		for(int i=0;i<5;++i){
			DevCardType type= EnumConverter.DevCardType(i);
			getPlayCardView().setCardAmount(type,ownedDevs.get(type));
			if(type.equals(DevCardType.MONUMENT)){
				getPlayCardView().setCardEnabled(type,(ownedDevs.get(type)>0&&ClientDevCardFacade.canUseMonument(ModelReferenceFacade.getLocalPlayerIndex())));
			}
			else getPlayCardView().setCardEnabled(type,(ownedPlayableDevs.get(type)>0)&& ServerDevCardFacade.canUseSomeDevCard(ModelReferenceFacade.getLocalPlayerIndex(),ModelReferenceFacade.getModel()));
		}
	}

	@Override
	public void cancelPlayCard() {

		getPlayCardView().closeModal();
	}

	@Override
	public void playMonopolyCard(ResourceType resource) {
		ClientDevCardFacade.useMonopoly(ModelReferenceFacade.getLocalPlayerIndex(),resource);
	}

	@Override
	public void playMonumentCard() {
		ClientDevCardFacade.useMonument(ModelReferenceFacade.getLocalPlayerIndex());
	}

	@Override
	public void playRoadBuildCard() {
		//the action should be taken care of in the execute function
		roadAction.execute();
	}

	@Override
	public void playSoldierCard() {
		//the action should be taken care of in the execute function
		soldierAction.execute();
	}

	@Override
	public void playYearOfPlentyCard(ResourceType resource1, ResourceType resource2) {
		ClientDevCardFacade.useYearOfPlenty(ModelReferenceFacade.getLocalPlayerIndex(),resource1,resource2);
	}

}

