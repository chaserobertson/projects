package client.join;

import model.definitions.EnumConverter;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.overhead.Model;
import model.serialization.Deserializer;
import params.CreateGameRequest;
import params.JoinGameRequest;
import params.Version;
import server.HTTPServerProxy;
import server.Poller;
import shared.definitions.CatanColor;
import client.base.*;
import client.data.*;
import client.misc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Implementation for the join game controller
 */
public class JoinGameController extends Controller implements IJoinGameController {

	private INewGameView newGameView;
	private ISelectColorView selectColorView;
	private IMessageView messageView;
	private IAction joinAction;
	private List<GameInfo> gameList;
	private int JoinGameID;
	private Poller poller;
	private boolean stopThis=false;

	/**
	 * JoinGameController constructor
	 * 
	 * @param view Join game view
	 * @param newGameView New game view
	 * @param selectColorView Select color view
	 * @param messageView Message view (used to display error messages that occur while the user is joining a game)
	 */
	public JoinGameController(IJoinGameView view, INewGameView newGameView,
							  ISelectColorView selectColorView, IMessageView messageView, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setJoinGameController(this);

		poller = new Poller();
		poller.setTimer(Poller.PollType.LIST);

		setNewGameView(newGameView);
		setSelectColorView(selectColorView);
		setMessageView(messageView);
		gameList = new ArrayList<>();
		JoinGameID = -1;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(!stopThis){
			initFromServer();
		}
		else{
			closeAllViews();
		}
		//Below is Matthew's color reset
        if(getSelectColorView().isModalShowing()&&JoinGameID>-1){
            GameInfo gameInfo=null;
            for(int i=0;i<gameList.size();++i){
                if(gameList.get(i).getId()==JoinGameID){
                    gameInfo=gameList.get(i);
                    break;
                }
            }
            if(gameInfo!=null){
                List<PlayerInfo> playerInfos=gameInfo.getPlayers();
                resetColorsEnabled();
                for(int i=0;i<playerInfos.size();++i){
                    getSelectColorView().setColorEnabled(playerInfos.get(i).getColor(),false);
                }
            }
        }
	}
	private void initFromServer() {
        if (!controllerHolder.getMapController().inGame) {
            gameList = ModelReferenceFacade.getInstance().getGameList();
            PlayerInfo playerInfo = GuiModelFacade.getLocalPlayerInfo();
            if (playerInfo == null) {
                playerInfo = new PlayerInfo();
                playerInfo.setId(HTTPServerProxy.getInstance().getPlayerID());
				playerInfo.setName(HTTPServerProxy.getInstance().getPlayerUsername());
				playerInfo.setPlayerIndex(0);
            }
            getJoinGameView().setGames(gameListToArray(), playerInfo);
            if(getJoinGameView().isModalShowing()){
                getJoinGameView().closeModal();
                getJoinGameView().showModal();
            }
            //getJoinGameView().showModal();
        }
    }

	public IJoinGameView getJoinGameView() {
		return (IJoinGameView)super.getView();
	}
	
	/**
	 * Returns the action to be executed when the user joins a game
	 * 
	 * @return The action to be executed when the user joins a game
	 */
	public IAction getJoinAction() {
		return joinAction;
	}

	/**
	 * Sets the action to be executed when the user joins a game
	 * 
	 * @param value The action to be executed when the user joins a game
	 */
	public void setJoinAction(IAction value) {
		joinAction = value;
	}
	
	public INewGameView getNewGameView() {
		return newGameView;
	}

	public void setNewGameView(INewGameView newGameView) {
		this.newGameView = newGameView;
	}
	
	public ISelectColorView getSelectColorView() {
		return selectColorView;
	}

	public void setSelectColorView(ISelectColorView selectColorView) {
		this.selectColorView = selectColorView;
	}
	
	public IMessageView getMessageView() {
		return messageView;
	}

	public void setMessageView(IMessageView messageView) {
		this.messageView = messageView;
	}

	@Override
	public void start() {
        String gameListJSON = HTTPServerProxy.getInstance().gameList();
        ModelReferenceFacade.initGameList( Deserializer.deserializeGameInfoList(gameListJSON) );
        initFromServer();
		getJoinGameView().showModal();
    }

	private GameInfo[] gameListToArray() {
		GameInfo[] gameInfos = new GameInfo[gameList.size()];
		Object[] objectArray = gameList.toArray();
		for(int i = 0; i < objectArray.length; i++) {
			gameInfos[i] = (GameInfo) objectArray[i];
		}
		return gameInfos;
	}

	@Override
	public void startCreateNewGame() {
		getNewGameView().showModal();
	}

	@Override
	public void cancelCreateNewGame() {
		getNewGameView().closeModal();
	}

	@Override
	public void createNewGame() {
		String title = getNewGameView().getTitle();
        if(title.equals("")) return;
		boolean randomNumbers = getNewGameView().getRandomlyPlaceNumbers();
		boolean randomHexes = getNewGameView().getRandomlyPlaceHexes();
		boolean randomPorts = getNewGameView().getUseRandomPorts();
		CreateGameRequest createGameRequest = new CreateGameRequest(randomHexes, randomNumbers, randomPorts, title);
		String newGame = HTTPServerProxy.getInstance().gameCreate(createGameRequest);

        GameInfo gameInfo = Deserializer.deserializeGameInfo(newGame);
        int id = gameInfo.getId();
        String color = "white";
        JoinGameRequest joinGameRequest = new JoinGameRequest(id, color);
        HTTPServerProxy.getInstance().gameJoin(joinGameRequest);

        if(getNewGameView().isModalShowing())getNewGameView().closeModal();//Matthew- This caused an assertion error randomly...

        String listJSON = HTTPServerProxy.getInstance().gameList();
        List<GameInfo> gameList = Deserializer.deserializeGameInfoList(listJSON);
        ModelReferenceFacade.getInstance().setGameList(gameList);

        initFromServer();
	}

	private void resetColorsEnabled() {
		for(CatanColor color : CatanColor.values()) {
			getSelectColorView().setColorEnabled(color, true);
		}
	}

	@Override
	public void startJoinGame(GameInfo game) {
		JoinGameID = game.getId();
		for (int i = 0; i < game.getPlayers().size(); i++) {
			if (HTTPServerProxy.getInstance().getPlayerID() == game.getPlayers().get(i).getId()) {
				ModelReferenceFacade.setLocalPlayerIndex(i);
			}
		}
		int playerID = HTTPServerProxy.getInstance().getPlayerID();
		List<PlayerInfo> players = game.getPlayers();
		for (PlayerInfo player:players) {
			if(player.getId()!= playerID){
				getSelectColorView().setColorEnabled(player.getColor(), false);
			}
		}
		getSelectColorView().showModal();
		Poller.forceModelPull=true;
	}

	@Override
	public void cancelJoinGame() {
		resetColorsEnabled();
		getJoinGameView().closeModal();
		Poller.forceModelPull=false;
	}

	@Override
	public void joinGame(CatanColor color) {
		// If join succeeded
		JoinGameRequest joinGameRequest = new JoinGameRequest(JoinGameID, EnumConverter.CatanColor(color));
		String joinGameResponse = HTTPServerProxy.getInstance().gameJoin(joinGameRequest);
		if(joinGameResponse.equals("unsuccessful")){
			getMessageView().setTitle("Error!");
			getMessageView().setMessage("Join game failed.");
			getMessageView().showModal();
			return;
		}
		String response = HTTPServerProxy.getInstance().gameModel(new Version(-1));

        poller.destroy();
        Model newModel = Deserializer.deserialize(response);
		ModelReferenceFacade.getInstance().setmodel(newModel);
        stopThis=true;
        getSelectColorView().closeModal();
        getJoinGameView().closeModal();
		joinAction.execute();
        controllerHolder.getMapController().startGame();
	}
	public void closeAllViews(){
		//Debugger.LogMessage("CloseAllViews called");
		if(getNewGameView().isModalShowing())getNewGameView().closeModal();
		if(getMessageView().isModalShowing())getMessageView().closeModal();
		if(getJoinGameView().isModalShowing())getJoinGameView().closeModal();
		if(getSelectColorView().isModalShowing())getSelectColorView().closeModal();
	}
}

