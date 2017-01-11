package server;

import params.*;

/**
 * Created by chase on 9/16/16.
 */
public class HTTPServerProxy implements IServer {

    private static HTTPServerProxy instance;
    public static HTTPServerProxy getInstance(){
        if(instance==null)instance=new HTTPServerProxy();
        return instance;
    }
    private HTTPServerProxy(){
        //nothing
    }

    private static ClientCommunicator clientCommunicator = ClientCommunicator.getInstance();
    private int port = 8081;
    private String host = "localhost";
    private String url = "http://"+host+":"+port;

    public void setPort(int port) {
        this.port = port;
        this.url = "http://"+host+":"+port;
    }

    public void setHost(String host) {
        this.host = host;
        this.url = "http://"+host+":"+port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public int getPlayerID() {
        return clientCommunicator.getPlayerID();
    }

    public String getPlayerUsername() {
        return clientCommunicator.getPlayerUsername();
    }

    public String getPlayerPassword() { return clientCommunicator.getPlayerPassword(); }

    public int getGameIndex() {
        return clientCommunicator.getGameIndex();
    }

    public boolean isConnected(){
        String url=this.url;
        return clientCommunicator.isConnected(url);
    }

    public String userLogin(Credentials params) {
        String url = this.url + "/user/login";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String userRegister(Credentials params) {
        String url = this.url + "/user/register";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameList() {
        String url = this.url + "/games/list";
        return clientCommunicator.getResponse(url, null);
    }

    public String gameCreate(CreateGameRequest params) {
        String url = this.url + "/games/create";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameJoin(JoinGameRequest params) {
        String url = this.url + "/games/join";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameSave(SaveGameRequest params) {
        String url = this.url + "/games/save";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameLoad(LoadGameRequest params) {
        String url = this.url + "/games/load";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameModel(Version params) {
        String url = this.url + "/game/model";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameReset() {
        String url = this.url + "/game/reset";
        return clientCommunicator.getResponse(url, null);
    }

    public String gameCommandsPost(CommandList params) {
        String url = this.url + "/game/commands";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameCommandsGet() {
        String url = this.url + "/game/commands";
        return clientCommunicator.getResponse(url, null);
    }

    public String gameAddAI(AddAIRequest params)  {
        String url = this.url + "/game/addAI";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String gameListAI() {
        String url = this.url + "/game/listAI";
        return clientCommunicator.getResponse(url, null);
    }

    public String sendChat(SendChatParams params) {
        String url = this.url + "/moves/sendChat";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String rollNumber(RollNumberParams params) {
        String url = this.url + "/moves/rollNumber";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String robPlayer(RobPlayerParams params) {
        String url = this.url + "/moves/robPlayer";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String finishTurn(FinishTurnParams params) {
        String url = this.url + "/moves/finishTurn";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String buyDevCard(BuyDevCardParams params) {
        String url = this.url + "/moves/buyDevCard";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String playCardYearOfPlenty(YearOfPlentyParams params) {
        String url = this.url + "/moves/Year_of_Plenty";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String playCardRoadBuilding(RoadBuildingParams params) {
        String url = this.url + "/moves/Road_Building";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String playCardSoldier(SoldierParams params) {
        String url = this.url + "/moves/Soldier";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String playCardMonopoly(MonopolyParams params) {
        String url = this.url + "/moves/Monopoly";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String playCardMonument(MonumentParams params) {
        String url = this.url + "/moves/Monument";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String buildRoad(BuildRoadParams params) {
        String url = this.url + "/moves/buildRoad";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String buildSettlement(BuildSettlementParams params) {
        String url = this.url + "/moves/buildSettlement";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String buildCity(BuildCityParams params) {
        String url = this.url + "/moves/buildCity";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String offerTrade(OfferTradeParams params) {
        String url = this.url + "/moves/offerTrade";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String acceptTrade(AcceptTradeParams params) {
        String url = this.url + "/moves/acceptTrade";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String maritimeTrade(MaritimeTradeParams params) {
        String url = this.url + "/moves/maritimeTrade";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String discardCards(DiscardCardsParams params) {
        String url = this.url + "/moves/discardCards";
        return clientCommunicator.getResponse(url, params.toString());
    }

    public String utilChangeLogLevel(ChangeLogLevelRequest params) {
        String url = this.url + "/util/changeLogLevel";
        return clientCommunicator.getResponse(url, params.toString());
    }
}