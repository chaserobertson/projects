package client.access;

import client.communication.ChatController;
import client.communication.GameHistoryController;
import client.devcards.DevCardController;
import client.discard.DiscardController;
import client.domestic.DomesticTradeController;
import client.join.JoinGameController;
import client.join.PlayerWaitingController;
import client.login.LoginController;
import client.map.MapController;
import client.maritime.MaritimeTradeController;
import client.points.PointsController;
import client.resources.ResourceBarController;
import client.roll.RollController;
import client.turntracker.TurnTrackerController;
import model.facade.client.ClientDevCardFacade;
import model.peripherals.TurnTracker;

/**
 * Created by tsmit on 10/24/2016.
 */
public class ControllerHolder {
    private static ControllerHolder instance;

    public static ControllerHolder getInstance(){
        if(instance == null)instance = new ControllerHolder();
        return instance;
    }

    private ControllerHolder(){
        //nothing
    }

    private ChatController chatController;
    private GameHistoryController gameHistoryController;
    private DevCardController devCardController;
    private DiscardController discardController;
    private DomesticTradeController domesticTradeController;
    private JoinGameController joinGameController;
    private PlayerWaitingController playerWaitingController;
    private LoginController loginController;
    private MapController mapController;
    private MaritimeTradeController maritimeTradeController;
    private PointsController pointsController;
    private ResourceBarController resourceBarController;
    private RollController rollController;
    private TurnTrackerController turnTrackerController;

    public ChatController getChatController() {
        return chatController;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public GameHistoryController getGameHistoryController() {
        return gameHistoryController;
    }

    public void setGameHistoryController(GameHistoryController gameHistoryController) {
        this.gameHistoryController = gameHistoryController;
    }

    public DevCardController getDevCardController() {
        return devCardController;
    }

    public void setDevCardController(DevCardController devCardController) {
        this.devCardController = devCardController;
    }

    public DiscardController getDiscardController() {
        return discardController;
    }

    public void setDiscardController(DiscardController discardController) {
        this.discardController = discardController;
    }

    public DomesticTradeController getDomesticTradeController() {
        return domesticTradeController;
    }

    public void setDomesticTradeController(DomesticTradeController domesticTradeController) {
        this.domesticTradeController = domesticTradeController;
    }

    public JoinGameController getJoinGameController() {
        return joinGameController;
    }

    public void setJoinGameController(JoinGameController joinGameController) {
        this.joinGameController = joinGameController;
    }

    public PlayerWaitingController getPlayerWaitingController() {
        return playerWaitingController;
    }

    public void setPlayerWaitingController(PlayerWaitingController playerWaitingController) {
        this.playerWaitingController = playerWaitingController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    public MapController getMapController() {
        return mapController;
    }

    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

    public MaritimeTradeController getMaritimeTradeController() {
        return maritimeTradeController;
    }

    public void setMaritimeTradeController(MaritimeTradeController maritimeTradeController) {
        this.maritimeTradeController = maritimeTradeController;
    }

    public PointsController getPointsController() {
        return pointsController;
    }

    public void setPointsController(PointsController pointsController) {
        this.pointsController = pointsController;
    }

    public ResourceBarController getResourceBarController() {
        return resourceBarController;
    }

    public void setResourceBarController(ResourceBarController resourceBarController) {
        this.resourceBarController = resourceBarController;
    }

    public RollController getRollController() {
        return rollController;
    }

    public void setRollController(RollController rollController) {
        this.rollController = rollController;
    }

    public TurnTrackerController getTurnTrackerController() {
        return turnTrackerController;
    }

    public void setTurnTrackerController(TurnTrackerController turnTrackerController) {
        this.turnTrackerController = turnTrackerController;
    }
}
