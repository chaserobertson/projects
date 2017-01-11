package client.points;

import client.base.*;
import com.sun.javafx.sg.prism.NGShape;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.player.Player;

import java.util.List;
import java.util.Observable;


/**
 * Implementation for the points controller
 */
public class PointsController extends Controller implements IPointsController {

	private IGameFinishedView finishedView;
	
	/**
	 * PointsController constructor
	 * 
	 * @param view Points view
	 * @param finishedView Game finished view, which is displayed when the game is over
	 */
	public PointsController(IPointsView view, IGameFinishedView finishedView, Observable observable) {
		
		super(view);

		observable.addObserver(this);

		controllerHolder.setPointsController(this);

		setFinishedView(finishedView);
		
		initFromModel();
	}

	@Override
	public void update(Observable o, Object arg) {
		if(GuiModelFacade.getLocalPlayer() == null) return;
		int points = GuiModelFacade.getLocalPlayer().getVictoryPoints();
		if(points>10)points=10;
		if(points<0)points=0;
		getPointsView().setPoints(points);
		if(getFinishedView().isModalShowing())return;
		int winner =ModelReferenceFacade.getModel().getPeripherals().getWinner();
		if(winner>0){
			if(ModelReferenceFacade.getPlayer(winner)!=null)getFinishedView().setWinner(ModelReferenceFacade.getPlayer(winner).getNickname(),winner==ModelReferenceFacade.getLocalPlayerIndex());
			else if(ModelReferenceFacade.getModel().getPlayerByInternetID(winner)!=null)getFinishedView().setWinner(ModelReferenceFacade.getModel().getPlayerByInternetID(winner).getNickname(),winner==ModelReferenceFacade.getLocalPlayerIndex());
			else return;
			getFinishedView().showModal();
			return;
		}
		List<Player> playerList= ModelReferenceFacade.getModel().getPlayers();
		for(int i=0;i<playerList.size();++i){
			if(playerList.get(i).getVictoryPoints()>=10){
				getFinishedView().setWinner(playerList.get(i).getNickname(),playerList.get(i).getIndex()== ModelReferenceFacade.getLocalPlayerIndex());
				getFinishedView().showModal();
				break;
			}
		}
	}

	public IPointsView getPointsView() {
		
		return (IPointsView)super.getView();
	}
	
	public IGameFinishedView getFinishedView() {
		return finishedView;
	}
	public void setFinishedView(IGameFinishedView finishedView) {
		this.finishedView = finishedView;
	}

	private void initFromModel() {
		getPointsView().setPoints(0);
	}
	
}

