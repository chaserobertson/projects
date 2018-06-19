package client.map;

import java.util.*;

import client.access.ControllerHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import debugger.Debugger;
import model.definitions.PlayerState;
import model.facade.client.ClientDevCardFacade;
import model.facade.client.ClientGeneralCommandFacade;
import model.facade.client.ClientPlayingCommandFacade;
import model.facade.server.ServerPlayingCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import model.gameboard.Hex;
import model.gameboard.Port;
import model.player.Player;
import model.structures.Colony;
import model.structures.Road;
import params.BuildRoadParams;
import params.RobPlayerParams;
import server.HTTPServerProxy;
import server.Poller;
import shared.definitions.*;
import shared.locations.*;
import client.base.*;
import client.data.*;


/**
 * Implementation for the map controller
 */
public class MapController extends Controller implements IMapController {
	//Proposed State Implementation by Matthew: In order to insure full credit for State Method, state will be called by various functions
	//Yes, it will be a very big enum. I apologize for the grotesqueness.

    public boolean inGame = false;
	private IRobView robView;
	private MapControllerState state;
	private Poller poller;
	private boolean roundDone;
	private HexLocation robberStoredPos;
	public int roadCardRemaining=0;
	public EdgeLocation roadCardSave;
	private boolean soldier=false;

	public MapControllerState getState(){return state;}

	public void setState(MapControllerState state){this.state=state;}

	public MapController(IMapView view, IRobView robView, Observable observable) {

		super(view);
		roundDone=false;

		observable.addObserver(this);

		controllerHolder.setMapController(this);

		poller = new Poller();
		//poller.setTimer(Poller.PollType.MODEL);

		setRobView(robView);
		
		initFromModel();
		refreshState();//MATTHEW ADDIN
		//state=MapControllerState.NotPlaying;
	}

	@Override
	public void update(Observable o, Object arg) {
		if(ModelReferenceFacade.getModel() == null) return;
		//The model has changed
		refreshState();
		initFromModel();
		//new proposition: Instead of doing a mid operation, have the game detect how many roads or settlements the player has
		//This lets you drop out and rejoin and still be in the correct state
		//if(true)return;
		if(ModelReferenceFacade.getModel().getPlayers().size()>=4){
			Poller.forceModelPull=false;
			if(ControllerHolder.getInstance().getPlayerWaitingController().getView().isModalShowing()){
				ControllerHolder.getInstance().getPlayerWaitingController().getView().closeModal();
			}
		}
		PlayerState gameState=GuiModelFacade.getCurrentState();
		if(getView().isModalOpen()){
			if(ModelReferenceFacade.getLocalPlayerIndex()!=GuiModelFacade.getCurrentActivePlayerIndex())((MapView)getView()).forceModalClosed();
			return;
		}
		if(ModelReferenceFacade.getModel()==null||ModelReferenceFacade.getModel().getPlayers().size()<4)return;
		if(gameState==PlayerState.FirstRound&&!roundDone&&GuiModelFacade.getCurrentActivePlayerIndex()==ModelReferenceFacade.getLocalPlayerIndex()){
			//controllerHolder.getJoinGameController().closeAllViews();
			Player player=GuiModelFacade.getLocalPlayer();
			if(player==null)return;
			if(player.getOwnedSettlementsCount()<1){
				//startMove(PieceType.SETTLEMENT,true,true);
				getView().startDrop(PieceType.SETTLEMENT,GuiModelFacade.getLocalPlayerColor(),false);
				//startMove(PieceType.SETTLEMENT,true,true);
			}
			else if(player.getOwnedRoad().size()<1){
				getView().startDrop(PieceType.ROAD,GuiModelFacade.getLocalPlayerColor(),false);
				roundDone=true;
			}
		}
		else if(gameState==PlayerState.SecondRound&&roundDone&&GuiModelFacade.isLocalPlayerTurn()){
			Player player=GuiModelFacade.getLocalPlayer();
			if(player.getOwnedSettlementsCount()<2){
				getView().startDrop(PieceType.SETTLEMENT,GuiModelFacade.getLocalPlayerColor(),false);
			}
			else if(player.getOwnedRoad().size()<2){
				getView().startDrop(PieceType.ROAD,GuiModelFacade.getLocalPlayerColor(),false);
				roundDone=false;
			}
		}
		else if(gameState==PlayerState.Robbing&&GuiModelFacade.isLocalPlayerTurn()&&!getView().isModalOpen()&&!getRobView().isModalShowing()) {
			getView().startDrop(PieceType.ROBBER, GuiModelFacade.getLocalPlayerColor(), false);
		}
	}

	public IMapView getView() {
		return (IMapView)super.getView();
	}

	public IRobView getRobView() {
		return robView;
	}

	private void setRobView(IRobView robView) {
		this.robView = robView;
	}
	
	protected void initFromModel() {
		//Matthew's code
		//Place Hexes
		List<Hex> hexes = GuiModelFacade.getHexes();
		if (hexes != null) {
			for (int i = 0; i < hexes.size(); ++i) {
				Hex hex = hexes.get(i);
				HexLocation tempLocation = hex.getHexLocation();
				getView().addHex(tempLocation, hex.getHexType());
			}
			List<HexLocation> seaHexes = generateSeaHexLocation(hexes);
			for (int i = 0; i < seaHexes.size(); ++i) {
				getView().addHex(seaHexes.get(i), HexType.WATER);
			}
			for (int i = 0; i < hexes.size(); ++i) {
				Hex hex = hexes.get(i);
				HexLocation tempLocation = hex.getHexLocation();
				if (hex.getChitValue() != 7) getView().addNumber(tempLocation, hex.getChitValue());
			}
		}

		//Place Ports
		List<Port> ports = GuiModelFacade.getPorts();
		if (ports != null) {
			for (int i = 0; i < ports.size(); ++i) {
				Port port = ports.get(i);
				getView().addPort(new EdgeLocation(port.getSeaHex(), port.getEdgeDirection()), port.getType());
			}
		}
		//Place Colonies
		List<Colony> settlements = GuiModelFacade.getSettlements();
		if (settlements != null) {
			for (int i = 0; i < settlements.size(); ++i) {
				Colony settlement = settlements.get(i);
				getView().placeSettlement(settlement.getNodePoint().getNormalizedVertexLocation(), settlement.getOwningPlayer().getColor());
			}
		}
		List<Colony> cities = GuiModelFacade.getCities();
		if (cities != null) {
			for (int i = 0; i < cities.size(); ++i) {
				Colony city = cities.get(i);
				getView().placeCity(city.getNodePoint().getNormalizedVertexLocation(), city.getOwningPlayer().getColor());
			}
		}
		//Place Roads
		List<Road> roads = GuiModelFacade.getRoads();
		if (roads != null) {
			for (int i = 0; i < roads.size(); ++i) {
				Road road = roads.get(i);
				getView().placeRoad(road.getLocation(), road.getOwningPlayer().getColor());
			}
		}
		//Place Robber
		HexLocation robberLocation = GuiModelFacade.getRobberLocation();
		if (robberLocation != null) {
			getView().placeRobber(robberLocation);
		}
	}

	//If can methods return false then it will not enable the "place" commands (automatically, not us)
	public boolean canPlaceRoad(EdgeLocation edgeLoc) {
		refreshState();
		//Debugger.LogBothGameTerminalMessage("MapController:CanPlaceRoad");
		return state.canPlaceRoad(edgeLoc,roadCardRemaining>0);
	}

	public boolean canPlaceSettlement(VertexLocation vertLoc) {
		refreshState();
		//Debugger.LogBothGameTerminalMessage("MapController:CanPlaceSettlement");
		return state.canPlaceSettlement(vertLoc);
	}

	public boolean canPlaceCity(VertexLocation vertLoc) {
		refreshState();
		//Debugger.LogBothGameTerminalMessage("MapController:CanPlaceCity");
		return state.canPlaceCity(vertLoc);
	}

	public boolean canPlaceRobber(HexLocation hexLoc) {
		refreshState();
		//Debugger.LogBothGameTerminalMessage("MapController:CanPlaceRobber");
		return state.canPlaceRobber(hexLoc);
	}

	public void placeRoad(EdgeLocation edgeLoc) {
		refreshState();
		if(state.placeRoad(edgeLoc,this)) {
			getView().placeRoad(edgeLoc, GuiModelFacade.getLocalPlayerColor());
			if(GuiModelFacade.isSetupRound()){
				controllerHolder.getTurnTrackerController().endTurn();
			}
		}
		if(roadCardRemaining>0&&!getView().isModalOpen()){
			if(ClientPlayingCommandFacade.canBuildARoad(ModelReferenceFacade.getLocalPlayerIndex(),true)){
				getView().startDrop(PieceType.ROAD,GuiModelFacade.getLocalPlayerColor(),true);
			}
		}
	}

	public void placeSettlement(VertexLocation vertLoc) {
		refreshState();
		if(state.placeSettlement(vertLoc)) {
			CatanColor color=GuiModelFacade.getLocalPlayerColor();
			if(color==null){
				Debugger.LogBothGameTerminalMessage("color is null");
				color=CatanColor.WHITE;
			}
			getView().placeSettlement(vertLoc, color);
		}
	}

	public void placeCity(VertexLocation vertLoc) {
		refreshState();
		if(state.placeCity(vertLoc)) {
			getView().placeCity(vertLoc, GuiModelFacade.getLocalPlayerColor());
		}
	}

	public void placeRobber(HexLocation hexLoc) {
		refreshState();
		if(state.placeRobber(hexLoc)) {
			getView().placeRobber(hexLoc);
			RobPlayerInfo[] infos=GuiModelFacade.getAllRobbablePlayersInfo(hexLoc);
			if(infos.length>0){
				getRobView().showModal();
				getRobView().setPlayers(infos);
				robberStoredPos=hexLoc;
			}
			else{
				if(!soldier){
					ModelReferenceFacade.getModel().getPeripherals().setCurrentState(PlayerState.Playing);//Should prevent double usage
					ClientPlayingCommandFacade.robPlayer(ModelReferenceFacade.getLocalPlayerIndex(),-1,hexLoc);
				}
				else{
					soldier=false;
					ModelReferenceFacade.getModel().getPeripherals().setCurrentState(PlayerState.Playing);//Should prevent double usage
					ClientDevCardFacade.useSoldier(ModelReferenceFacade.getLocalPlayerIndex(),-1,hexLoc);
				}
			}
		}
	}
	
	public void startMove(PieceType pieceType, boolean isFree, boolean allowDisconnected) {
		//This is called before any of the "can" methods and opens up the mini screen (automatically, we don't do that)
		refreshState();
		//state.startMove(pieceType,isFree,allowDisconnected);
		if (pieceType==PieceType.ROBBER) {
			getView().startDrop(pieceType, GuiModelFacade.getLocalPlayerColor(), false);
		} else {
			getView().startDrop(pieceType, GuiModelFacade.getLocalPlayerColor(), true);
		}
	}
	
	public void cancelMove() {
		refreshState();
		//As far as I can tell, we don't even need to implement anything here.
		state.cancelMove();
		if(roadCardSave!=null){
			getView().removeRoad(roadCardSave);
			BuildRoadParams undoParams=new BuildRoadParams(ModelReferenceFacade.getLocalPlayerIndex(),roadCardSave,true,false);
			ServerPlayingCommandFacade.undoPlaceRoad(undoParams,ModelReferenceFacade.getModel());
		}
		roadCardRemaining=0;
		roadCardSave=null;
	}
	
	public void playSoldierCard() {//Theoretically functional
		refreshState();
		soldier=true;
		state.playSoldierCard();
		getView().startDrop(PieceType.ROBBER,GuiModelFacade.getLocalPlayerColor(),false);
	}
	
	public void playRoadBuildingCard() {//Theoretically functional, uncertain
		refreshState();
		state.playRoadBuildingCard(this);
		if (roadCardRemaining!=0) {
			getView().startDrop(PieceType.ROAD,GuiModelFacade.getLocalPlayerColor(),true);
		}
	}
	
	public void robPlayer(RobPlayerInfo victim) {//Theoretically Functional, uncertain
		refreshState();
		state.robPlayer(victim,robberStoredPos,soldier);
		soldier=false;
		robberStoredPos=null;
		if(getRobView().isModalShowing())getRobView().closeModal();
	}

	private void refreshState(){
		if(GuiModelFacade.getCurrentState() == null) return;
		state= MapControllerState.convertPlayerStateToMapState(GuiModelFacade.getCurrentState());
	}

	public static List<HexLocation> generateSeaHexLocation(List<Hex> islandHexes){
		List<HexLocation> result=new LinkedList<>();

		HexLocation South = new HexLocation(0,-1);
		HexLocation SouthEast=new HexLocation(1,0);
		HexLocation NorthEast=new HexLocation(1,-1);
		HexLocation North=new HexLocation(0,1);
		HexLocation NorthWest=new HexLocation(-1,0);
		HexLocation SouthWest=new HexLocation(-1,1);
		List<HexLocation> hexAdjusts=new ArrayList<>(6);
		hexAdjusts.add(South);
		hexAdjusts.add(SouthEast);
		hexAdjusts.add(NorthEast);
		hexAdjusts.add(North);
		hexAdjusts.add(NorthWest);
		hexAdjusts.add(SouthWest);
		for(int i=0;i<islandHexes.size();++i){
			for(int j=0;j<hexAdjusts.size();++j){
				HexLocation adjusted=add(islandHexes.get(i).getHexLocation(),hexAdjusts.get(j));
				if(!containsHexLoc(result,adjusted)&&!containsHexAt(islandHexes,adjusted))result.add(adjusted);
			}
		}
		return result;
	}

	private static boolean containsHexAt(List<Hex> hexList,HexLocation hexLocation){
		for(int i=0;i<hexList.size();++i){
			if(hexLocation.equals(hexList.get(i).getHexLocation()))return true;
		}
		return false;
	}

	private static HexLocation add(HexLocation hexLoc1,HexLocation hexLoc2){
		return new HexLocation(hexLoc1.getX()+hexLoc2.getX(),hexLoc1.getY()+hexLoc2.getY());
	}

	private static boolean containsHexLoc(List<HexLocation>hexLocList,HexLocation hexLocation){
		for(int i=0;i<hexLocList.size();++i){
			if(hexLocation.equals(hexLocList.get(i)))return true;
		}
		return false;
	}

	public void startGame() {
        poller.setTimer(Poller.PollType.MODEL);
		controllerHolder.getJoinGameController().closeAllViews();
        inGame = true;
		ModelReferenceFacade.getInstance().setmodel(ModelReferenceFacade.getModel());
    }

    public void startModelPoller() {
        poller.setTimer(Poller.PollType.MODEL);
    }
    public void closeModal(){
		return;
		//required because of interface weirdness.
	}

	private static final String initJSONModelTest = "{\n" +
			"\t\"deck\": {\n" +
			"\t\t\"yearOfPlenty\": 2,\n" +
			"\t\t\"monopoly\": 2,\n" +
			"\t\t\"soldier\": 14,\n" +
			"\t\t\"roadBuilding\": 2,\n" +
			"\t\t\"monument\": 5\n" +
			"\t},\n" +
			"\t\"map\": {\n" +
			"\t\t\"hexes\": [\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"brick\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": -2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 8\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wheat\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 1,\n" +
			"\t\t\t\t\t\"y\": -2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 6\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"sheep\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 2,\n" +
			"\t\t\t\t\t\"y\": -2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 10\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"sheep\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -1,\n" +
			"\t\t\t\t\t\"y\": -1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 10\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wood\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": -1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 11\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wood\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 1,\n" +
			"\t\t\t\t\t\"y\": -1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 3\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wood\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 2,\n" +
			"\t\t\t\t\t\"y\": -1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 4\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wheat\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -2,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 11\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wood\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -1,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 6\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"brick\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 1,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 5\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"ore\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 2,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 3\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"sheep\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -2,\n" +
			"\t\t\t\t\t\"y\": 1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 9\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"ore\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -1,\n" +
			"\t\t\t\t\t\"y\": 1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 9\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wheat\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": 1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 8\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"sheep\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 1,\n" +
			"\t\t\t\t\t\"y\": 1\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 12\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"ore\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -2,\n" +
			"\t\t\t\t\t\"y\": 2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 5\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"wheat\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -1,\n" +
			"\t\t\t\t\t\"y\": 2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 2\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"resource\": \"brick\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": 2\n" +
			"\t\t\t\t},\n" +
			"\t\t\t\t\"number\": 4\n" +
			"\t\t\t}\n" +
			"\t\t],\n" +
			"\t\t\"roads\": [ ],\n" +
			"\t\t\"cities\": [ ],\n" +
			"\t\t\"settlements\": [ ],\n" +
			"\t\t\"radius\": 3,\n" +
			"\t\t\"ports\": [\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 2,\n" +
			"\t\t\t\t\"resource\": \"sheep\",\n" +
			"\t\t\t\t\"direction\": \"SE\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -3,\n" +
			"\t\t\t\t\t\"y\": 0\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 2,\n" +
			"\t\t\t\t\"resource\": \"ore\",\n" +
			"\t\t\t\t\"direction\": \"NW\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 2,\n" +
			"\t\t\t\t\t\"y\": 1\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 3,\n" +
			"\t\t\t\t\"direction\": \"SW\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 3,\n" +
			"\t\t\t\t\t\"y\": -3\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 3,\n" +
			"\t\t\t\t\"direction\": \"NE\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -3,\n" +
			"\t\t\t\t\t\"y\": 2\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 2,\n" +
			"\t\t\t\t\"resource\": \"wood\",\n" +
			"\t\t\t\t\"direction\": \"NE\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -2,\n" +
			"\t\t\t\t\t\"y\": 3\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 3,\n" +
			"\t\t\t\t\"direction\": \"S\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": -1,\n" +
			"\t\t\t\t\t\"y\": -2\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 2,\n" +
			"\t\t\t\t\"resource\": \"wheat\",\n" +
			"\t\t\t\t\"direction\": \"NW\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 3,\n" +
			"\t\t\t\t\t\"y\": -1\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 2,\n" +
			"\t\t\t\t\"resource\": \"brick\",\n" +
			"\t\t\t\t\"direction\": \"S\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 1,\n" +
			"\t\t\t\t\t\"y\": -3\n" +
			"\t\t\t\t}\n" +
			"\t\t\t},\n" +
			"\t\t\t{\n" +
			"\t\t\t\t\"ratio\": 3,\n" +
			"\t\t\t\t\"direction\": \"N\",\n" +
			"\t\t\t\t\"location\": {\n" +
			"\t\t\t\t\t\"x\": 0,\n" +
			"\t\t\t\t\t\"y\": 3\n" +
			"\t\t\t\t}\n" +
			"\t\t\t}\n" +
			"\t\t],\n" +
			"\t\t\"robber\": {\n" +
			"\t\t\t\"x\": 0,\n" +
			"\t\t\t\"y\": 0\n" +
			"\t\t}\n" +
			"\t},\n" +
			"\t\"players\": [\n" +
			"\t\t{\n" +
			"\t\t\t\"resources\": {\n" +
			"\t\t\t\t\"brick\": 0,\n" +
			"\t\t\t\t\"wood\": 0,\n" +
			"\t\t\t\t\"sheep\": 0,\n" +
			"\t\t\t\t\"wheat\": 0,\n" +
			"\t\t\t\t\"ore\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"oldDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"newDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"roads\": 15,\n" +
			"\t\t\t\"cities\": 4,\n" +
			"\t\t\t\"settlements\": 5,\n" +
			"\t\t\t\"soldiers\": 0,\n" +
			"\t\t\t\"victoryPoints\": 0,\n" +
			"\t\t\t\"monuments\": 0,\n" +
			"\t\t\t\"playedDevCard\": false,\n" +
			"\t\t\t\"discarded\": false,\n" +
			"\t\t\t\"playerID\": 12,\n" +
			"\t\t\t\"playerIndex\": 0,\n" +
			"\t\t\t\"name\": \"string\",\n" +
			"\t\t\t\"color\": \"green\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"resources\": {\n" +
			"\t\t\t\t\"brick\": 0,\n" +
			"\t\t\t\t\"wood\": 0,\n" +
			"\t\t\t\t\"sheep\": 0,\n" +
			"\t\t\t\t\"wheat\": 0,\n" +
			"\t\t\t\t\"ore\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"oldDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"newDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"roads\": 15,\n" +
			"\t\t\t\"cities\": 4,\n" +
			"\t\t\t\"settlements\": 5,\n" +
			"\t\t\t\"soldiers\": 0,\n" +
			"\t\t\t\"victoryPoints\": 0,\n" +
			"\t\t\t\"monuments\": 0,\n" +
			"\t\t\t\"playedDevCard\": false,\n" +
			"\t\t\t\"discarded\": false,\n" +
			"\t\t\t\"playerID\": -2,\n" +
			"\t\t\t\"playerIndex\": 1,\n" +
			"\t\t\t\"name\": \"Quinn\",\n" +
			"\t\t\t\"color\": \"yellow\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"resources\": {\n" +
			"\t\t\t\t\"brick\": 0,\n" +
			"\t\t\t\t\"wood\": 0,\n" +
			"\t\t\t\t\"sheep\": 0,\n" +
			"\t\t\t\t\"wheat\": 0,\n" +
			"\t\t\t\t\"ore\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"oldDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"newDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"roads\": 15,\n" +
			"\t\t\t\"cities\": 4,\n" +
			"\t\t\t\"settlements\": 5,\n" +
			"\t\t\t\"soldiers\": 0,\n" +
			"\t\t\t\"victoryPoints\": 0,\n" +
			"\t\t\t\"monuments\": 0,\n" +
			"\t\t\t\"playedDevCard\": false,\n" +
			"\t\t\t\"discarded\": false,\n" +
			"\t\t\t\"playerID\": -3,\n" +
			"\t\t\t\"playerIndex\": 2,\n" +
			"\t\t\t\"name\": \"Scott\",\n" +
			"\t\t\t\"color\": \"blue\"\n" +
			"\t\t},\n" +
			"\t\t{\n" +
			"\t\t\t\"resources\": {\n" +
			"\t\t\t\t\"brick\": 0,\n" +
			"\t\t\t\t\"wood\": 0,\n" +
			"\t\t\t\t\"sheep\": 0,\n" +
			"\t\t\t\t\"wheat\": 0,\n" +
			"\t\t\t\t\"ore\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"oldDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"newDevCards\": {\n" +
			"\t\t\t\t\"yearOfPlenty\": 0,\n" +
			"\t\t\t\t\"monopoly\": 0,\n" +
			"\t\t\t\t\"soldier\": 0,\n" +
			"\t\t\t\t\"roadBuilding\": 0,\n" +
			"\t\t\t\t\"monument\": 0\n" +
			"\t\t\t},\n" +
			"\t\t\t\"roads\": 15,\n" +
			"\t\t\t\"cities\": 4,\n" +
			"\t\t\t\"settlements\": 5,\n" +
			"\t\t\t\"soldiers\": 0,\n" +
			"\t\t\t\"victoryPoints\": 0,\n" +
			"\t\t\t\"monuments\": 0,\n" +
			"\t\t\t\"playedDevCard\": false,\n" +
			"\t\t\t\"discarded\": false,\n" +
			"\t\t\t\"playerID\": -4,\n" +
			"\t\t\t\"playerIndex\": 3,\n" +
			"\t\t\t\"name\": \"Steve\",\n" +
			"\t\t\t\"color\": \"white\"\n" +
			"\t\t}\n" +
			"\t],\n" +
			"\t\"log\": {\n" +
			"\t\t\"lines\": [ ]\n" +
			"\t},\n" +
			"\t\"chat\": {\n" +
			"\t\t\"lines\": [ ]\n" +
			"\t},\n" +
			"\t\"bank\": {\n" +
			"\t\t\"brick\": 24,\n" +
			"\t\t\"wood\": 24,\n" +
			"\t\t\"sheep\": 24,\n" +
			"\t\t\"wheat\": 24,\n" +
			"\t\t\"ore\": 24\n" +
			"\t},\n" +
			"\t\"turnTracker\": {\n" +
			"\t\t\"status\": \"FirstRound\",\n" +
			"\t\t\"currentTurn\": 0,\n" +
			"\t\t\"longestRoad\": -1,\n" +
			"\t\t\"largestArmy\": -1\n" +
			"\t},\n" +
			"\t\"winner\": -1,\n" +
			"\t\"version\": 0\n" +
			"}";
}

