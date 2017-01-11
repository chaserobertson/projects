package client.domestic;

import client.data.PlayerInfo;
import model.definitions.EnumConverter;
import model.definitions.ResourceHand;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.peripherals.TradeOffer;
import model.resources.ResourcePile;
import model.serialization.Deserializer;
import params.AcceptTradeParams;
import params.OfferTradeParams;
import server.HTTPServerProxy;
import shared.definitions.*;
import client.base.*;
import client.misc.*;

import java.util.List;
import java.util.Observable;


/**
 * Domestic trade controller implementation
 */
public class DomesticTradeController extends Controller implements IDomesticTradeController {

	private IDomesticTradeOverlay tradeOverlay;
	private IWaitView waitOverlay;
	private IAcceptTradeOverlay acceptOverlay;
	//Matthew- Taking a stab at this, though I'm not super certain what its supposed to be. I think it just keeps track of a Trade Offer
	private int receiverIndex;
	private ResourceHand tradeOffer;
	private ResourceHand resourceStates;
	//The above will be added together to form the actual tradeOffer. The Acceptor has to be able to transfer Positive Resources to Offerer

	/**
	 * DomesticTradeController constructor
	 * 
	 * @param tradeView Domestic trade view (i.e., view that contains the "Domestic Trade" button)
	 * @param tradeOverlay Domestic trade overlay (i.e., view that lets the user propose a domestic trade)
	 * @param waitOverlay Wait overlay used to notify the user they are waiting for another player to accept a trade
	 * @param acceptOverlay Accept trade overlay which lets the user accept or reject a proposed trade
	 */
	public DomesticTradeController(IDomesticTradeView tradeView, IDomesticTradeOverlay tradeOverlay,
								   IWaitView waitOverlay, IAcceptTradeOverlay acceptOverlay, Observable observable) {

		super(tradeView);

		observable.addObserver(this);

		controllerHolder.setDomesticTradeController(this);

		setTradeOverlay(tradeOverlay);
		setWaitOverlay(waitOverlay);
		setAcceptOverlay(acceptOverlay);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(!GuiModelFacade.isLocalPlayerTurn()){
			getTradeView().enableDomesticTrade(false);
		}
		else{
			getTradeView().enableDomesticTrade(true);
		}
		TradeOffer tradeOffer=GuiModelFacade.getTradeOffer();
		if(tradeOffer==null&&getWaitOverlay().isModalShowing()){getWaitOverlay().closeModal();}
		if(tradeOffer!=null&&tradeOffer.getReceiver().getIndex()== ModelReferenceFacade.getLocalPlayerIndex()){
			acceptOverlay.reset();

			acceptOverlay.setPlayerName(tradeOffer.getOfferer().getNickname());
			ResourceHand offer=tradeOffer.getOffer();
			ResourceHand reverseOffer = getReverseOffer(offer);
			for(int i=0;i<ResourceType.numberOfTypes();i++){
				ResourceType type=EnumConverter.ResourceType(i);
				if(offer.getResource(type)<0){//negative is request
					acceptOverlay.addGiveResource(type,-1*offer.getResource(type));
				}
				else if(offer.getResource(type)>0){//positive is offer
					acceptOverlay.addGetResource(type,offer.getResource(type));
				}
			}
			//I reccomend reversing this...
			acceptOverlay.setAcceptEnabled(GuiModelFacade.getLocalPlayer().getResourceAggregation().canTransferResourcesTo(
					tradeOffer.getOfferer().getResourceAggregation(),offer.createOpposite()));//If can transfer resources, enable
			acceptOverlay.showModal();//This MUST be done be done last. The panel sizes elements based on inputs.
		}
	}

	public IDomesticTradeView getTradeView() {
		
		return (IDomesticTradeView)super.getView();
	}

	public IDomesticTradeOverlay getTradeOverlay() {
		return tradeOverlay;
	}

	public void setTradeOverlay(IDomesticTradeOverlay tradeOverlay) {
		this.tradeOverlay = tradeOverlay;
	}

	public IWaitView getWaitOverlay() {
		return waitOverlay;
	}

	public void setWaitOverlay(IWaitView waitView) {
		this.waitOverlay = waitView;
	}

	public IAcceptTradeOverlay getAcceptOverlay() {
		return acceptOverlay;
	}

	public void setAcceptOverlay(IAcceptTradeOverlay acceptOverlay) {
		this.acceptOverlay = acceptOverlay;
	}

	@Override
	public void startTrade() {
		if(ModelReferenceFacade.getModel()==null)return;//REJECT!
		receiverIndex = -1;
		tradeOffer = new ResourceHand(0,0,0,0,0);
		resourceStates = new ResourceHand(0,0,0,0,0);
		getTradeOverlay().showModal();
		List<PlayerInfo> playerInfos=GuiModelFacade.getAllPlayerInfo();
		for(int i=0;i<playerInfos.size();++i){
			if(playerInfos.get(i).getPlayerIndex()==ModelReferenceFacade.getLocalPlayerIndex()){
				playerInfos.remove(i);
				break;
			}
		}
		PlayerInfo[] temp=new PlayerInfo[playerInfos.size()];
		for(int i=0;i<playerInfos.size();++i){
			temp[i]=playerInfos.get(i);
		}
		getTradeOverlay().setPlayers(temp);
		getTradeOverlay().reset();
		getTradeOverlay().setCancelEnabled(true);
		getTradeOverlay().setPlayerSelectionEnabled(true);
		getTradeOverlay().setResourceSelectionEnabled(true);
		for(int i=0;i<ResourceType.numberOfTypes();++i){
			ResourceType type=EnumConverter.ResourceType(i);
			ResourcePile localPile=GuiModelFacade.getLocalPlayerResourcePile();
			getTradeOverlay().setResourceAmountChangeEnabled(type,localPile.getQuantityResourceType(type)>0,false);
			getTradeOverlay().setResourceAmount(type,"0");
		}
	}

	@Override
	public void decreaseResourceAmount(ResourceType resource) {
		if(resourceStates.getResource(resource)>0){								//receiving state
			tradeOffer.addResource(resource,-1);
			boolean canDecrease= tradeOffer.getResource(resource)>0;
			getTradeOverlay().setResourceAmountChangeEnabled(resource,true,canDecrease);
			getTradeOverlay().setResourceAmount(resource,Integer.toString(-1*tradeOffer.getResource(resource)));
		}
		else if(resourceStates.getResource(resource)<0){						//offering state
			tradeOffer.addResource(resource,1);
			boolean canDecrease= tradeOffer.getResource(resource)<0;
			getTradeOverlay().setResourceAmountChangeEnabled(resource,true,canDecrease);
			getTradeOverlay().setResourceAmount(resource,Integer.toString(tradeOffer.getResource(resource)));
		}
		updateTradeState();
	}

	@Override
	public void increaseResourceAmount(ResourceType resource) {
		if(resourceStates.getResource(resource)<0){								//offering state
			ResourceHand playerHand = GuiModelFacade.getLocalPlayerResourceHand();
			tradeOffer.addResource(resource,-1);
			boolean canAdd = playerHand.getResource(resource) > tradeOffer.getResource(resource)*-1;
			getTradeOverlay().setResourceAmountChangeEnabled(resource,canAdd,true);
			getTradeOverlay().setResourceAmount(resource,Integer.toString(-1*tradeOffer.getResource(resource)));
		}
		else if(resourceStates.getResource(resource)>0){						//receiving state
			tradeOffer.addResource(resource,1);
			getTradeOverlay().setResourceAmountChangeEnabled(resource,true,true);
			getTradeOverlay().setResourceAmount(resource,Integer.toString(tradeOffer.getResource(resource)));
		}
		updateTradeState();
	}

	@Override
	public void sendTradeOffer() {
		OfferTradeParams trade = new OfferTradeParams(GuiModelFacade.getCurrentActivePlayerIndex(),getReverseOffer(tradeOffer),receiverIndex);
		getTradeOverlay().closeModal();
		getWaitOverlay().showModal();
		String JsonModel = HTTPServerProxy.getInstance().offerTrade(trade);
		Model model = Deserializer.deserialize(JsonModel);
		ModelReferenceFacade.getInstance().setmodel(model);
	}

	@Override
	public void setPlayerToTradeWith(int playerIndex) {
		this.receiverIndex=playerIndex;
		updateTradeState();
	}

	@Override
	public void setResourceToReceive(ResourceType resource) {
		tradeOffer.setResource(resource,0);
		resourceStates.setResource(resource, 1);
		tradeOverlay.setResourceAmountChangeEnabled(resource,true,false);
		getTradeOverlay().setResourceAmount(resource,"0");
		updateTradeState();
	}

	@Override
	public void setResourceToSend(ResourceType resource) {
		tradeOffer.setResource(resource,0);
		resourceStates.setResource(resource, -1);
		ResourceHand playerHand = GuiModelFacade.getLocalPlayerResourceHand();
		boolean canAdd = playerHand.getResource(resource) > 0;
		tradeOverlay.setResourceAmountChangeEnabled(resource,canAdd,false);
		getTradeOverlay().setResourceAmount(resource,"0");
		updateTradeState();
	}

	@Override
	public void unsetResource(ResourceType resource) {
		tradeOffer.setResource(resource,0);
		resourceStates.setResource(resource, 0);
		tradeOverlay.setResourceAmountChangeEnabled(resource,false,false);
		tradeOverlay.setResourceAmount(resource,"0");
		updateTradeState();
	}

	@Override
	public void cancelTrade() {
		tradeOffer=null;
		resourceStates=null;
		receiverIndex=-1;
		getTradeOverlay().closeModal();
	}

	@Override
	public void acceptTrade(boolean willAccept) {
		TradeOffer tradeOffer=GuiModelFacade.getTradeOffer();
		if(tradeOffer == null){
			getAcceptOverlay().closeModal();
			return;
		}
		AcceptTradeParams params = new AcceptTradeParams(tradeOffer.getReceiver().getIndex(), tradeOffer.getOfferer().getIndex(), willAccept);
		String JsonModel = HTTPServerProxy.getInstance().acceptTrade(params);
		Model model = Deserializer.deserialize(JsonModel);
		ModelReferenceFacade.getInstance().setmodel(model);
		getAcceptOverlay().closeModal();
	}

	private void updateTradeState(){
		boolean isSending = false;
		boolean isReceiving = false;
		for(int resource = 0; resource < ResourceType.numberOfTypes(); resource++){
			if(resourceStates.getResource(resource) > 0 && tradeOffer.getResource(resource) > 0){
				isSending = true;
			}
			else if(resourceStates.getResource(resource) < 0 && tradeOffer.getResource(resource) < 0){
				isReceiving = true;
			}
		}
		boolean playerSelected = receiverIndex > -1;
		if(isSending && isReceiving && playerSelected){
			getTradeOverlay().setStateMessage("Trade");
			getTradeOverlay().setTradeEnabled(true);
		}
		else if(!(isSending && isReceiving)){
			getTradeOverlay().setStateMessage("set the trade you want to make");
			getTradeOverlay().setTradeEnabled(false);
		}
		else{
			getTradeOverlay().setStateMessage("set the trade partner");
			getTradeOverlay().setTradeEnabled(false);
		}
	}

	private ResourceHand getReverseOffer(ResourceHand offer){
		ResourceHand reverseOffer = new ResourceHand(0,0,0,0,0);
		for(int i = 0; i < 5; i++){
			reverseOffer.setResource(i, offer.getResource(i) * -1);
		}
		return reverseOffer;
	}
}

