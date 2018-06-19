package client.discard;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.PlayerState;
import model.definitions.ResourceHand;
import model.facade.client.ClientGeneralCommandFacade;
import model.facade.client.ClientPlayingCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import shared.definitions.*;
import client.base.*;
import client.misc.*;

import java.util.List;
import java.util.Observable;


/**
 * Discard controller implementation
 */
public class DiscardController extends Controller implements IDiscardController {

	private IWaitView waitView;
	private ResourceHand toDiscard;
	private ResourceHand max;
	private int maxToDiscard;
	private boolean midDiscard=false;
	/**
	 * DiscardController constructor
	 * 
	 * @param view View displayed to let the user select cards to discard
	 * @param waitView View displayed to notify the user that they are waiting for other players to discard
	 */
	public DiscardController(IDiscardView view, IWaitView waitView, Observable observable) {
		
		super(view);

		observable.addObserver(this);

		controllerHolder.setDiscardController(this);

		this.waitView = waitView;
		toDiscard=new ResourceHand(0,0,0,0,0);
	}

	@Override
	public void update(Observable o, Object arg) {
		//Theoretically DiscardController should function
		if(GuiModelFacade.getLocalPlayer() == null) return;
		else if(GuiModelFacade.getCurrentState()== PlayerState.Discarding&&//State is discarding
				!getDiscardView().isModalShowing()&&//We do not already have a discarding action running here
				!GuiModelFacade.getLocalPlayer().getHasDiscarded()&&//Player has not already discarded
				GuiModelFacade.getLocalPlayer().sumOfResourceCards()>7){//Player has more than 7 resource cards
			max = GuiModelFacade.getLocalPlayerResourceHand();
			maxToDiscard = 0;
			for (int i = 0; i < 5; ++i) {
				getDiscardView().setResourceMaxAmount(EnumConverter.ResourceType(i), max.getResource(EnumConverter.ResourceType(i)));
				getDiscardView().setResourceDiscardAmount(EnumConverter.ResourceType(i), 0);
				boolean canDiscardMore = max.getResource(i) > 0;
				getDiscardView().setResourceAmountChangeEnabled(EnumConverter.ResourceType(i), canDiscardMore, false);
				maxToDiscard += max.getResource(EnumConverter.ResourceType(i));
			}

			maxToDiscard = maxToDiscard/2;

			toDiscard=new ResourceHand(0,0,0,0,0);
			midDiscard=true;
			getDiscardView().setDiscardButtonEnabled(false);
			getDiscardView().setStateMessage("0/" + maxToDiscard);
			getDiscardView().showModal();
		}
		else if(GuiModelFacade.getCurrentState()!=PlayerState.Discarding&&getWaitView().isModalShowing()){
			Debugger.LogMessage("DiscardController:If triggered");
			if(getDiscardView().isModalShowing())getDiscardView().closeModal();
			//getWaitView().closeModal();
			if(getDiscardView().isModalShowing())getDiscardView().closeModal();
			//if(getWaitView().isModalShowing())getWaitView().closeModal();
		}
		else if (!midDiscard && getDiscardView().isModalShowing()){
			getDiscardView().closeModal();
		}

	}

	public IDiscardView getDiscardView() {
		return (IDiscardView)super.getView();
	}
	
	public IWaitView getWaitView() {
		return waitView;
	}

	@Override
	public void increaseAmount(ResourceType resource) {
		toDiscard.addResource(resource, 1);
		if(max == null){
			max = GuiModelFacade.getLocalPlayerResourceHand();
		}
		boolean canDiscardMore = max.getResource(resource) > toDiscard.getResource(resource);
		updateBtnDiscard();
		getDiscardView().setResourceDiscardAmount(resource, toDiscard.getResource(resource));
	}

	@Override
	public void decreaseAmount(ResourceType resource) {
		toDiscard.addResource(resource, -1);
		boolean greaterThanZero = toDiscard.getResource(resource) > 0;
		updateBtnDiscard();
		getDiscardView().setResourceDiscardAmount(resource, toDiscard.getResource(resource));
	}

	@Override
	public void discard() {
		getDiscardView().closeModal();
		//getWaitView().showModal();
		ClientGeneralCommandFacade.discardCards(ModelReferenceFacade.getLocalPlayerIndex(),toDiscard);
		toDiscard=new ResourceHand(0,0,0,0,0);
		max=new ResourceHand(0,0,0,0,0);
		midDiscard=false;

	}

	private void updateBtnDiscard(){
		int cardsDiscarded = 0;
		for (int i = 0; i < 5; ++i) {
			cardsDiscarded += toDiscard.getResource(EnumConverter.ResourceType(i));
		}
		getDiscardView().setStateMessage(cardsDiscarded + "/" + maxToDiscard);
		getDiscardView().setDiscardButtonEnabled(cardsDiscarded >= maxToDiscard);
		if(cardsDiscarded>=maxToDiscard){
			for(int i=0;i<ResourceType.numberOfTypes();++i){
				ResourceType type=EnumConverter.ResourceType(i);
				getDiscardView().setResourceAmountChangeEnabled(type,false,toDiscard.getResource(type)>0);
			}
		}
		else{
			for(int i=0;i<ResourceType.numberOfTypes();++i){
				ResourceType type=EnumConverter.ResourceType(i);
				getDiscardView().setResourceAmountChangeEnabled(type,toDiscard.getResource(type)<max.getResource(type),toDiscard.getResource(type)>0);
			}
		}
	}

}

