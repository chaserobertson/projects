package client.join;

import client.access.ControllerHolder;
import client.base.*;
import client.data.PlayerInfo;
import client.misc.MessageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.serialization.Deserializer;
import params.AddAIRequest;
import params.Version;
import server.HTTPServerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Implementation for the player waiting controller
 */
public class PlayerWaitingController extends Controller implements IPlayerWaitingController {
	private List<PlayerInfo> oldList;

	public PlayerWaitingController(IPlayerWaitingView view, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setPlayerWaitingController(this);
		oldList= new ArrayList<>();
	}

	@Override
	public void update(Observable o, Object arg) {
		List<PlayerInfo> playerInfoList = GuiModelFacade.getAllPlayerInfo();
		if(playerInfoList == null) return;
		if(playerInfoList.size()<=oldList.size())return;
		PlayerInfo[] playerInfos = toPlayerInfoArray(playerInfoList);
		oldList=playerInfoList;
		getView().setPlayers(playerInfos);
		if(getView().isModalShowing()){
			getView().closeModal();
			getView().showModal();
		}
	}

	@Override
	public IPlayerWaitingView getView() {

		return (IPlayerWaitingView)super.getView();
	}

	@Override
	public void start() {
		/*
		if(GuiModelFacade.getAllPlayerInfo().size() >= 4) {
			Debugger.LogMessage("PlayerWaitingController");
			//change to playing state
			ControllerHolder.getInstance().getJoinGameController().closeAllViews();
			return;
		}*/

		PlayerInfo[] playerInfos = toPlayerInfoArray(GuiModelFacade.getAllPlayerInfo());
		getView().setPlayers(playerInfos);

		String aiList = HTTPServerProxy.getInstance().gameListAI();
		Gson gson = new GsonBuilder().create();
		JsonArray aiArray = gson.fromJson(aiList, JsonArray.class);

		String[] AIChoices = new String[aiArray.size()];
		for(int i = 0; i < aiArray.size(); i++) {
			AIChoices[i] = aiArray.get(i).getAsString();
		}
		getView().setAIChoices(AIChoices);

		getView().showModal();

		if(GuiModelFacade.getAllPlayerInfo().size() >= 4) {
			if(getView().isModalShowing())getView().closeModal();
			ControllerHolder.getInstance().getMapController().startGame();
		}
	}

	private PlayerInfo[] toPlayerInfoArray(List<PlayerInfo> playerInfos) {
		PlayerInfo[] playerInfoArray = new PlayerInfo[playerInfos.size()];
		Object[] objects = playerInfos.toArray();
		for(int i = 0; i < playerInfoArray.length; i++) {
			playerInfoArray[i] = (PlayerInfo) objects[i];
		}
		return playerInfoArray;
	}

	@Override
	public void addAI() {
		String selectedAI = getView().getSelectedAI();

		AddAIRequest addAIRequest = new AddAIRequest();
		addAIRequest.AIType=selectedAI;
		HTTPServerProxy.getInstance().gameAddAI(addAIRequest);

		String response = HTTPServerProxy.getInstance().gameModel(new Version(-1));
		if(response.equals("unsuccessful")){
			MessageView messageView = new MessageView();
			messageView.setTitle("Error!");
			messageView.setMessage("Add AI failed.");
			messageView.showModal();
			return;
		}

		Model newModel = Deserializer.deserialize(response);
		ModelReferenceFacade.getInstance().setmodel(newModel);

		if(getView().isModalShowing())getView().closeModal();
		start();
	}

}

