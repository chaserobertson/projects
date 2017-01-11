package client.roll;

import client.base.*;
import model.definitions.PlayerState;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.serialization.Deserializer;
import params.RollNumberParams;
import server.HTTPServerProxy;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Implementation for the roll controller
 */
public class RollController extends Controller implements IRollController {

	private IRollResultView resultView;
	private Timer timer;
	private final long ROLLINGDELAY = 3000;
	private boolean rolled = false;


	/**
	 * RollController constructor
	 * 
	 * @param view Roll view
	 * @param resultView Roll result view
	 */
	public RollController(IRollView view, IRollResultView resultView, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setRollController(this);

		setResultView(resultView);
	}

	@Override
	public void update(Observable o, Object arg) {
        if(GuiModelFacade.getLocalPlayer() == null) return;
        boolean myTurn = GuiModelFacade.getLocalPlayer().getIndex() == GuiModelFacade.getCurrentActivePlayerIndex();
		if(GuiModelFacade.getCurrentState() == PlayerState.Rolling && myTurn && !rolled&&!getRollView().isModalShowing()) {
			if(!getRollView().isModalShowing()) getRollView().showModal();

			timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!rolled) rollDice();
                }
            }, ROLLINGDELAY);
		}
		else if(GuiModelFacade.getCurrentState() == PlayerState.Playing && myTurn) {
			//if(getResultView().isModalShowing()) getResultView().closeModal();
			if(getRollView().isModalShowing()) getRollView().closeModal();
			if(!getResultView().isModalShowing())rolled = false;
			else rolled=true;
		}
	}

	public IRollResultView getResultView() {
		return resultView;
	}
	public void setResultView(IRollResultView resultView) {
		this.resultView = resultView;
	}

	public IRollView getRollView() {
		return (IRollView)getView();
	}

	@Override
	public void rollDice() {
		if(rolled)return;
		rolled = true;
		if(timer!=null)timer.cancel();
		//timer.purge();
		timer=null;
		//int playerIndex = GuiModelFacade.getLocalPlayer().getIndex();
		int playerIndex= ModelReferenceFacade.getLocalPlayerIndex();//Routing all localPlayer index commands to this command
		RollNumberParams params = new RollNumberParams(playerIndex);
		String JsonModel = HTTPServerProxy.getInstance().rollNumber(params);
		Model model = Deserializer.deserialize(JsonModel);
		if(model!=null)ModelReferenceFacade.getInstance().setmodel(model);

		if(getRollView().isModalShowing()) getRollView().closeModal();
		getResultView().setRollValue(params.number);
		getResultView().showModal();
	}
	public void closeAllModals(){
		if(getRollView().isModalShowing())getRollView().closeModal();
		if(getResultView().isModalShowing())getResultView().closeModal();
		if(getRollView().isModalShowing())getRollView().closeModal();
		if(getResultView().isModalShowing())getResultView().closeModal();
	}
}

