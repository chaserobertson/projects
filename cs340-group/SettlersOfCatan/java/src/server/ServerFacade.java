package server;

import client.data.GameInfo;
import client.data.PlayerInfo;
import debugger.Debugger;
import login.Login;
import login.ServerCredentials;
import model.definitions.EnumConverter;
import model.facade.AI.AITypes;
import model.facade.server.*;
import model.facade.shared.SerializeFacade;
import model.overhead.Model;
import model.overhead.Model;
import model.player.Player;
import params.*;
import persistance.ProxyPersistanceProvider;
import shared.definitions.CatanColor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MTAYS on 11/7/2016.
 */
public class ServerFacade implements IServer,Serializable{
    public static boolean storingCommands=true;
    private static ServerFacade instance;
    public static final String SUCCESSMESSAGE="Success";
    private static final String LOGINERRORMESSAGE="Failed to login - bad username or password.";
    private static final String REGISTERERRORMESSAGE="Failed to register - someone already has that username.";
    public static final String ERRORMESSAGE="unsuccessful";
    public static ServerFacade getInstance(){
        if(instance==null)instance=new ServerFacade();
        return instance;
    }
    private ServerFacade(){
        //Nothing doin
    }

    public int getLoginId(Credentials params){
        Login login = Login.getInstance();
        return login.getPlayerId(params.username);
    }

    public String userLogin(Credentials params){
        Login login = Login.getInstance();
        if(login.LoginPlayer(params.username, params.password)){
            return SUCCESSMESSAGE;
        }
        else{
            return LOGINERRORMESSAGE;
        }
    }

    /**
     *
     * @pre username is not null password is not null The specified
     * username is not already in use.
     *
     * @post If there is no existing user with the specified
     * username, 1. A new user account has been created with the specified
     * username and password. 2. The ServerFacade returns an HTTP 200 success response
     * with "Success" in the body. 3. The HTTP response headers set the
     * catan.user cookie to contain the identity of the logged-in player. The
     * cookie uses "Path=/", and its value contains a url-encoded JSON object of
     * the following form: { "name": STRING, "password": STRING, "playerID":
     * INTEGER }. For example, { "name": "Rick", "password": "secret",
     * "playerID": 14 }. If there is already an existing user with the specified
     * name, or the operation fails for any other reason, 1. The ServerFacade returns
     * an HTTP 400 error response, and the body contains an error message.
     *
     * Description: This method does two things: 1) Creates a new user account
     * 2) Logs the caller in to the ServerFacade as the new user, and sets their
     * catan.user HTTP cookie.
     *
     * Notes: You should be able to register any username via this call, unless
     * that username is already registered with another user. There is no method
     * for changing passwords.
     *
     * @param params
     *          param list
     * @return Success if registered and logged in,
     *            error otherwise
     */
    public String userRegister(Credentials params){
        Login login = Login.getInstance();
        if(login.RegisterPlayer(params.username, params.password)){
            ProxyPersistanceProvider.getInstance().registerUser(params);
            return SUCCESSMESSAGE;
        }
        else{
            return REGISTERERRORMESSAGE;
        }
    }

    /**
     *
     * @pre None
     *
     * @post If the operation succeeds, 1. The ServerFacade returns an HTTP
     * 200 success response. 2. The body contains a JSON array containing a list
     * of objects that contain information about the ServerFacade's games If the
     * operation fails, 1. The ServerFacade returns an HTTP 400 error response, and
     * the body contains an error message. Output JSON format: The output is a
     * JSON array of game objects. Each game object contains the title and ID of
     * a game, and an array of four player objects containing information about
     * players who have joined the game. Each player object contains the color,
     * name and ID of a player who has joined the game. Players who have not yet
     * joined the game are represented as empty JSON objects.
     *
     * Description: Returns information about all of the current games on the
     * ServerFacade.
     *
     * @return list of game objects
     */
    public String gameList(){//No need to store
        List<GameInfo> result= ServerModelListStorage.getAllGameInfos();
        return SerializeFacade.serializeGameInfoList(result);
    }

    /**
     * @pre name != null randomTiles, randomNumbers, and randomPorts
     * contain valid String values
     *
     * @post If the operation succeeds, 1. A new game with the
     * specified properties has been created 2. The ServerFacade returns an HTTP 200
     * success response. 3. The body contains a JSON object describing the newly
     * created game If the operation fails, 1. The ServerFacade returns an HTTP 400
     * error response, and the body contains an error message. Output JSON
     * format: The output is a JSON object containing information about the
     * newly created game, including its title, ID, and an array of four empty
     * player objects.
     *
     * Description: Creates a new game on the ServerFacade.
     *
     * @param params
     *          param list
     * @return JSON object describing new game
     */
    public String gameCreate(CreateGameRequest params){
        GameInfo newGame=ServerModelListStorage.createGame(params);//This returns a gameinfo fyi
        ServerCredentials creds=Login.getInstance().getPlayer(params.creatorId);
        if(creds==null){
            creds=new ServerCredentials(-1,"Not registered","wheeee");
        }
        PlayerInfo autojoin=new PlayerInfo();
        autojoin.setId(params.creatorId);
        autojoin.setName("test");
        autojoin.setColor(CatanColor.WHITE);
        ServerModelListStorage.getFacade(newGame.getId()).addPlayer(autojoin);
        ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(newGame.getId()));
        return SerializeFacade.serializeGameInfo(ServerModelListStorage.getFacade(newGame.getId()).getGameInfo()).toString();
    }

    /**
     *
     * @pre 1. The user has previously logged in to the ServerFacade (i.e.,
     * they have a valid catan.user HTTP cookie). 2. The player may join the
     * game because 2.a They are already in the game, OR 2.b There is space in
     * the game to add a new player 3. The specified game ID is valid 4. The
     * specified color is valid (red, green, blue, yellow, puce, brown, white,
     * purple, orange)
     *
     * @post If the operation succeeds, 1. The ServerFacade returns an HTTP
     * 200 success response with "Success" in the body. 2. The player is in the
     * game with the specified color (i.e. calls to /games/list method will show
     * the player in the game with the chosen color). 3. The ServerFacade response
     * includes the "Set-cookie" response header setting the catan.game HTTP
     * cookie If the operation fails, 1. The ServerFacade returns an HTTP 400 error
     * response, and the body contains an error message.
     *
     * Description: Adds the player to the specified game and sets their
     * catan.game cookie.
     *
     * @return Success if user is added, error otherwise
     */
    public String gameJoin(JoinGameRequest params){
        Model model=getModel(params);
        if(model!=null){
            if(model.getPlayerByInternetID(params.id)==null){
                if(model.getPlayers().size()<4){
                    String username=Login.getInstance().getPlayer(params.id).getUsername();
                    if(username==null)username="PLACEHOLDER2.0";
                    model.addPlayer(new Player(model.getPlayers().size(),username, EnumConverter.CatanColor(params.color),params.id));
                }
                else return ERRORMESSAGE;
            }
            model.incrementVersion();
            model.getPlayerByInternetID(params.id).setColor(EnumConverter.CatanColor(params.color));
            if(ServerModelListStorage.getFacade(params.gameId)==null){
                Debugger.LogMessage("Facade was null");
                Debugger.LogMessage(Integer.toString(params.gameId));
                Debugger.LogMessage(Integer.toString(params.id));
                Debugger.LogMessage(Integer.toString(ServerModelListStorage.getModelFacades().size()));
                Debugger.LogMessage("GameInfos:");
                for(Map.Entry<Integer,IServerModelFacade> entry:ServerModelListStorage.getModelFacades().entrySet()){
                    Debugger.LogMessage("Key="+Integer.toString(entry.getKey()));
                    Debugger.LogMessage("internalID="+entry.getValue().getGameID());
                }
            }
            ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(params.gameId));
            return SUCCESSMESSAGE;
        }
        return ERRORMESSAGE;
    }

    /**
     * @pre there is a game to be saved by the specified name and ID
     *
     * @post game state will be saved for later reference
     */
    public String gameSave(SaveGameRequest params){
        if(ServerModelListStorage.getFacade(params.id)!=null){
            ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(params.id));
            return SUCCESSMESSAGE;
        }
        else return ERRORMESSAGE;
    }

    /**
     * @pre there is a game to load by the specified name
     *
     * @post game will be loaded into model
     */
    public String gameLoad(LoadGameRequest params){
        return ERRORMESSAGE;}

    /**
     *
     * @pre 1. The caller has previously logged in to the ServerFacade and
     * joined a game (i.e., they have valid catan.user and catan.game HTTP
     * cookies). 2. If specified, the version number is included as the
     * "version" query parameter in the request URL, and its value is a valid
     * integer.
     *
     * @post If the operation succeeds, 1. The ServerFacade returns an HTTP
     * 200 success response. 2. The response body contains JSON data a. The full
     * client model JSON is returned if the caller does not provide a version
     * number, or the provide version number does not match the version on the
     * ServerFacade b. "Success" (Success in double quotes) is returned if the caller
     * provided a version number, and the version number matched the version
     * number on the ServerFacade If the operation fails, 1. The ServerFacade returns an
     * HTTP 400 error response, and the body contains an error message.
     *
     * Description: Returns the current state of the game in JSON format. In
     * addition to the current game state, the returned JSON also includes a
     * "version" number for the client model. The next time /game/model is
     * called, the version number from the previously retrieved model may
     * optionally be included as a query parameter in the request
     * (/game/model?version=N). The ServerFacade will only return the full JSON game
     * state if its version number is not equal to N. If it is equal to N, the
     * ServerFacade returns "Success" to indicate that the caller already has the
     * latest game state. This is merely an optimization. If the version number
     * is not included in the request URL, the ServerFacade will return the full game
     * state.
     *
     * @param params
     *          param list
     * @return model JSON if successful, error message otherwise
     */
    public String gameModel(Version params){
        Model model = getModel(params);
        if(model==null){
            return "False";
        }
        if(model!=null&&model.getVersion()!=params.versionNumber)return SerializeFacade.convertModelToJSON(model);
        return "True";
    }

    /**
     * @pre user must be logged in and there must be a game to reset
     *
     * @post game will be reverted to just after initialization round
     *
     * @return new game model same as gameModel()
     */
    public String gameReset(){
        ProxyPersistanceProvider.getInstance().resetGames();
        return SUCCESSMESSAGE;
    }

    /**
     * @pre user is logged in and joined game
     *
     * @post commands are executed on game
     *
     * @return new game model same as gameModel()
     */
    public String gameCommandsPost(CommandList params){
        //TODO:implement
        return ERRORMESSAGE;
    }

    /**
     * @pre user is logged in and joined game
     *
     * @post commands are executed on game
     *
     * @return new game model same as gameModel()
     */
    public String gameCommandsGet(){
        //TODO:implement
        return ERRORMESSAGE;
    }

    /**
     *
     * @pre 1. The caller has previously logged in to the ServerFacade and
     * joined a game (i.e., they have valid catan.user and catan.game HTTP
     * cookies). 2. There is space in the game for another player (i.e., the
     * game is not "full"). 3. The specified "AIType" is valid (i.e., one of the
     * values returned by the /game/listAI method).
     *
     * @post If the operation succeeds, 1. The ServerFacade returns an HTTP
     * 200 success response with "Success" in the body. 2. A new AI player of
     * the specified type has been added to the current game. The ServerFacade
     * selected a name and color for the player. If the operation fails, 1. The
     * ServerFacade returns an HTTP 400 error response, and the body contains an error
     * message.
     *
     * Description: Adds an AI player to the current game. You must
     * login and join a game before calling this method.
     *
     * @param params
     *          param list
     * @return Success or error response
     */
    public String gameAddAI(AddAIRequest params){
        IServerModelFacade facade=ServerModelListStorage.getFacade(params.gameId);
        if(facade==null||facade.getLocalModel()==null)return "Failure";
        facade.addAI(params);
        facade.getLocalModel().incrementVersion();
        storeCommand(params);
        return "Success";
    }

    /**
     *
     * @pre None
     *
     * @post If the operation succeeds, 1. The ServerFacade returns an HTTP
     * 200 success response. 2. The body contains a JSON string array
     * enumerating the different types of AI players. These are the values that
     * may be passed to the /game/addAI method.
     *
     * Description: Returns a list of supported AI player types.
     * Currently, LARGEST_ARMY is the only supported type.
     *
     * @return JSON or error message
     */
    public String gameListAI(){
        return AITypes.getAITypesList();
    }

    /**
     * @param params
     *          param list
     * @pre None (this command may be executed at any time by any player)
     * @post The chat contains your message at the end
     */
    public String sendChat(SendChatParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(params==null)return SerializeFacade.convertModelToJSON(model);
        if(!ServerGeneralCommandFacade.sendChat(params,model)){
            Debugger.LogMessage("Server has rejected send Chat command");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre It is your turn
     * @pre The client model’s status is ‘Rolling’
     * @post The client model’s status is now in ‘Discarding’ or ‘Robbing’ or ‘Playing’
     */
    public String rollNumber(RollNumberParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerGeneralCommandFacade.roll(params,getModel(params))){
            Debugger.LogMessage("Server has rejected roll number request");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     */
    public String robPlayer(RobPlayerParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        try {
            if (!ServerPlayingCommandFacade.robPlayer(params, getModel(params))) {
                Debugger.LogMessage("Server rejected Rob Player command");
            }
            else storeCommand(params);
        }catch (Exception e){
            Debugger.LogMessage("Some exception got thrown by ServerFacade:RobPlayer");
        }
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @pre None (except the preconditions for this section)
     * @post The cards in your new dev card hand have been transferred to your old dev card hand
     * @post It is the next player’s turn
     */
    public String finishTurn(FinishTurnParams params){
        Model model=getModel(params);
        if(model==null){
            return ERRORMESSAGE;
        }
        storeCommand(params);
        if(!ServerPlayingCommandFacade.endTurn(params,getModel(params))){
            if(storingCommands)ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(params.gameId));
            Debugger.LogMessage("Server has rejected finishTurn");
            Debugger.LogMessage(params.toString());
        }
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @pre You have the required resources (1 ore, 1 wheat, 1 sheep)
     * @pre There are dev cards left in the deck
     * @post You have a new card
     * @post If it is a monument card, it has been added to your old devcard hand
     * @post If it is a non­monument card, it has been added to your new devcard hand (unplayable this turn)
     */
    public String buyDevCard(BuyDevCardParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerPlayingCommandFacade.buyDevCard(params,getModel(params))){
            Debugger.LogMessage("Server has rejected buy dev card");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The two specified resources are in the bank
     * @post You gained the two specified resources
     */
    public String playCardYearOfPlenty(YearOfPlentyParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerDevCardFacade.useYearOfPlenty(params,getModel(params))){
            Debugger.LogMessage("Server Rejected Year of Plenty");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The first road location (spot1) is connected to one of your roads.
     * @pre The second road location (spot2) is connected to one of your roads or to the first road location (spot1)
     * @pre Neither road location is on water
     * @pre You have at least two unused roads
     * @post You have two fewer unused roads
     * @post Two new roads appear on the map at the specified locations
     * @post If applicable, "longest road" has been awarded to the player with the longest road
     */
    public String playCardRoadBuilding(RoadBuildingParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerDevCardFacade.useRoadBuilding(params,getModel(params))){
            Debugger.LogMessage("Server has rejected RoadBuilding DevCard");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     * @post If applicable, "largest army" has been awarded to the player who has played the most soldier cards
     * @post You are not allowed to play other development cards during this turn (except for monument cards, which may still be played
     */
    public String playCardSoldier(SoldierParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerDevCardFacade.useSoldier(params,getModel(params))){
            Debugger.LogMessage("Server has rejected play soldier card");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre None (except the general preconditions for this section)
     * @post All of the other players have given you all of their resource cards of the specified type
     */
    public String playCardMonopoly(MonopolyParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerDevCardFacade.useMonopoly(params,getModel(params))){
            Debugger.LogMessage("Monopoly rejected by server");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @pre You have enough monument cards to win the game (i.e., reach 10 victory points)
     * @post You gained a victory point
     */
    public String playCardMonument(MonumentParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerDevCardFacade.useMonument(params,getModel(params))){
            Debugger.LogMessage("Server has rejected monument");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The road location is open
     * @pre The road location is connected to another road owned by the player
     * @pre The road location is not on water
     * @pre You have the required resources (1 wood, 1 brick; 1 road)
     * @pre Setup round: Must be placed by settlement owned by the player with no adjacent road
     * @post You lost the resources required to build a road (1 wood, 1 brick; 1 road)
     * @post The road is on the map at the specified location
     * @post If applicable, "longest road" has been awarded to the player with the longest road
     */
    public String buildRoad(BuildRoadParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerPlayingCommandFacade.placeRoad(params,getModel(params))){
            Debugger.LogMessage("Server has rejected build road");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The settlement location is open
     * @pre The settlement location is not on water
     * @pre The settlement location is connected to one of your roads except during setup
     * @pre You have the required resources (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @pre The settlement cannot be placed adjacent to another settlement
     * @post You lost the resources required to build a settlement (1 wood, 1 brick, 1 wheat, 1 sheep; 1 settlement)
     * @post The settlement is on the map at the specified location
     */
    public String buildSettlement(BuildSettlementParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerPlayingCommandFacade.placeSettlement(params,getModel(params))){
            Debugger.LogMessage("Server has rejected build settlement");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The city location is where you currently have a settlement
     * @pre You have the required resources (2 wheat, 3 ore; 1 city)
     * @post You lost the resources required to build a city (2 wheat, 3 ore; 1 city)
     * @post The city is on the map at the specified location
     * @post You got a settlement back
     */
    public String buildCity(BuildCityParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerPlayingCommandFacade.buildCity(params,getModel(params))){
            Debugger.LogMessage("Server has rejected build city");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre You have the resources you are offering
     * @post The trade is offered to the other player (stored in the ServerFacade model)
     */
    public String offerTrade(OfferTradeParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        storeCommand(params);
        if(!ServerPlayingCommandFacade.offerTrade(params,getModel(params))){
            Debugger.LogMessage("Server has rejected offer trade");
            if(storingCommands)ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(params.gameId));
        }
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre You have been offered a domestic trade
     * @pre To accept the offered trade, you have the required resources
     * @post If you accepted, you and the player who offered swap the specified resources
     * @post If you declined no resources are exchanged
     * @post The trade offer is removed
     */
    public String acceptTrade(AcceptTradeParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(ServerGeneralCommandFacade.acceptTrade(params,model)) {
            storeCommand(params);
            return SerializeFacade.convertModelToJSON(model);
        }
        else Debugger.LogMessage("Server has rejected accept trade");
        return ERRORMESSAGE;
    }

    /**
     * @param params
     *          param list
     * @pre You have the resources you are giving
     * @pre For ratios less than 4, you have the correct port for the trade
     * @post The trade has been executed (the offered resources are in the bank, and the requested resource has been received)
     */
    public String maritimeTrade(MaritimeTradeParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerPlayingCommandFacade.maritimeTrade(params,getModel(params))){
            Debugger.LogMessage("ServerFacade:MaritimeTrade: rejected by server");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * @param params
     *          param list
     * @pre The status of the client model is 'Discarding'
     * @pre You have over 7 cards
     * @pre You have the cards you're choosing to discard
     * @post You gave up the specified resources
     * @post If you're the last one to discard, the client model status changes to 'Robbing
     */
    public String discardCards(DiscardCardsParams params){
        Model model=getModel(params);
        if(model==null)return ERRORMESSAGE;
        if(!ServerGeneralCommandFacade.discardCards(params,getModel(params))){
            Debugger.LogMessage("Server has rejected discard cards");
        }
        else storeCommand(params);
        return SerializeFacade.convertModelToJSON(model);
    }

    /**
     * The ServerFacade's new changeLogLevelRequest level.
     * The following values are allowed: ALL, SEVERE, WARNING ,INFO, CONFIG, FINE, FINER, FINEST, OFF
     */
    public String utilChangeLogLevel(ChangeLogLevelRequest params){
        //TODO: implement
        return ERRORMESSAGE;
    }

    private Model getModel(GameRequiredParam params){
        Model Model =ServerModelListStorage.getModel(params.gameId);
        if(Model ==null) {
            Debugger.LogMessage("ServerFacade:GetModel:Couldn't find desired one, pulled the 0");
            Model =ServerModelListStorage.getModel(0);
        }
        return Model;
    }
    public void storeCommand(GameRequiredParam command){
        //Storing commands should be false when rebuilding from persistence, else commands get double written.
        if(storingCommands&&!ProxyPersistanceProvider.getInstance().writeCommand(command.generateCommand(),command.gameId)){
            ProxyPersistanceProvider.getInstance().writeModel(ServerModelListStorage.getFacade(command.gameId));
        }
    }
}
