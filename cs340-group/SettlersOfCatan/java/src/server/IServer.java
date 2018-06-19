package server;

import params.*;

/**
 * Created by Will on 9/16/2016.
 *
 * All /move/* methods also have a definitions pre­condition in that they assume that the caller has
 * already logged in to the server and joined a game. This pre­condition is not repeated on each
 * move type, but should be assumed
 */
public interface IServer {

    /**
     * <h2>Non-Move Commands:</h2>
     * General preconditions:
     * @pre The server exists
     */

    /**
     *
     * @pre username is not error, password is not error
     *
     * @post If the passed-in (username, password) pair is valid, 1.
     * The server returns an HTTP 200 success response with "Success" in the
     * body. 2. The HTTP response headers set the catan.user cookie to contain
     * the identity of the logged-in player. The cookie uses "Path=/", and its
     * value contains a url-encoded JSON object of the following form: { "name":
     * STRING, "password": STRING, "playerID": INTEGER }. For example, { "name":
     * "Rick", "password": "secret", "playerID": 14 }. If the passed-in
     * (username, password) pair is not valid, or the operation fails for any
     * other reason, 1. The server returns an HTTP 400 error response, and the
     * body contains an error message.
     *
     * Description: Logs the caller in to the server, and sets their catan.user
     * HTTP cookie.
     *
     * Notes: The passed-in username and password may correspond to the
     * credentials of any registered user.
     *
     * @param params
     *          param list
     *
     * @return Success if logged in, error otherwise
     */
    String userLogin(Credentials params);

    /**
     *
     * @pre username is not null password is not null The specified
     * username is not already in use.
     *
     * @post If there is no existing user with the specified
     * username, 1. A new user account has been created with the specified
     * username and password. 2. The server returns an HTTP 200 success response
     * with "Success" in the body. 3. The HTTP response headers set the
     * catan.user cookie to contain the identity of the logged-in player. The
     * cookie uses "Path=/", and its value contains a url-encoded JSON object of
     * the following form: { "name": STRING, "password": STRING, "playerID":
     * INTEGER }. For example, { "name": "Rick", "password": "secret",
     * "playerID": 14 }. If there is already an existing user with the specified
     * name, or the operation fails for any other reason, 1. The server returns
     * an HTTP 400 error response, and the body contains an error message.
     *
     * Description: This method does two things: 1) Creates a new user account
     * 2) Logs the caller in to the server as the new user, and sets their
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
    String userRegister(Credentials params);

    /**
     *
     * @pre None
     *
     * @post If the operation succeeds, 1. The server returns an HTTP
     * 200 success response. 2. The body contains a JSON array containing a list
     * of objects that contain information about the server's games If the
     * operation fails, 1. The server returns an HTTP 400 error response, and
     * the body contains an error message. Output JSON format: The output is a
     * JSON array of game objects. Each game object contains the title and ID of
     * a game, and an array of four player objects containing information about
     * players who have joined the game. Each player object contains the color,
     * name and ID of a player who has joined the game. Players who have not yet
     * joined the game are represented as empty JSON objects.
     *
     * Description: Returns information about all of the current games on the
     * server.
     *
     * @return list of game objects
     */
    String gameList();

    /**
     * @pre name != null randomTiles, randomNumbers, and randomPorts
     * contain valid String values
     *
     * @post If the operation succeeds, 1. A new game with the
     * specified properties has been created 2. The server returns an HTTP 200
     * success response. 3. The body contains a JSON object describing the newly
     * created game If the operation fails, 1. The server returns an HTTP 400
     * error response, and the body contains an error message. Output JSON
     * format: The output is a JSON object containing information about the
     * newly created game, including its title, ID, and an array of four empty
     * player objects.
     *
     * Description: Creates a new game on the server.
     *
     * @param params
     *          param list
     * @return JSON object describing new game
     */
    String gameCreate(CreateGameRequest params);

    /**
     *
     * @pre 1. The user has previously logged in to the server (i.e.,
     * they have a valid catan.user HTTP cookie). 2. The player may join the
     * game because 2.a They are already in the game, OR 2.b There is space in
     * the game to add a new player 3. The specified game ID is valid 4. The
     * specified color is valid (red, green, blue, yellow, puce, brown, white,
     * purple, orange)
     *
     * @post If the operation succeeds, 1. The server returns an HTTP
     * 200 success response with "Success" in the body. 2. The player is in the
     * game with the specified color (i.e. calls to /games/list method will show
     * the player in the game with the chosen color). 3. The server response
     * includes the "Set-cookie" response header setting the catan.game HTTP
     * cookie If the operation fails, 1. The server returns an HTTP 400 error
     * response, and the body contains an error message.
     *
     * Description: Adds the player to the specified game and sets their
     * catan.game cookie.
     *
     * @return Success if user is added, error otherwise
     */
    String gameJoin(JoinGameRequest params);

    /**
     * @pre there is a game to be saved by the specified name and ID
     *
     * @post game state will be saved for later reference
     */
    String gameSave(SaveGameRequest params);

    /**
     * @pre there is a game to load by the specified name
     *
     * @post game will be loaded into model
     */
    String gameLoad(LoadGameRequest params);

    /**
     *
     * @pre 1. The caller has previously logged in to the server and
     * joined a game (i.e., they have valid catan.user and catan.game HTTP
     * cookies). 2. If specified, the version number is included as the
     * "version" query parameter in the request URL, and its value is a valid
     * integer.
     *
     * @post If the operation succeeds, 1. The server returns an HTTP
     * 200 success response. 2. The response body contains JSON data a. The full
     * client model JSON is returned if the caller does not provide a version
     * number, or the provide version number does not match the version on the
     * server b. "Success" (Success in double quotes) is returned if the caller
     * provided a version number, and the version number matched the version
     * number on the server If the operation fails, 1. The server returns an
     * HTTP 400 error response, and the body contains an error message.
     *
     * Description: Returns the current state of the game in JSON format. In
     * addition to the current game state, the returned JSON also includes a
     * "version" number for the client model. The next time /game/model is
     * called, the version number from the previously retrieved model may
     * optionally be included as a query parameter in the request
     * (/game/model?version=N). The server will only return the full JSON game
     * state if its version number is not equal to N. If it is equal to N, the
     * server returns "Success" to indicate that the caller already has the
     * latest game state. This is merely an optimization. If the version number
     * is not included in the request URL, the server will return the full game
     * state.
     *
     * @param params
     *          param list
     * @return model JSON if successful, error message otherwise
     */
    String gameModel(Version params);

    /**
     * @pre user must be logged in and there must be a game to reset
     *
     * @post game will be reverted to just after initialization round
     *
     * @return new game model same as gameModel()
     */
    String gameReset();

    /**
     * @pre user is logged in and joined game
     *
     * @post commands are executed on game
     *
     * @return new game model same as gameModel()
     */
    String gameCommandsPost(CommandList params);

    /**
     * @pre user is logged in and joined game
     *
     * @post commands are executed on game
     *
     * @return new game model same as gameModel()
     */
    String gameCommandsGet();

    /**
     *
     * @pre 1. The caller has previously logged in to the server and
     * joined a game (i.e., they have valid catan.user and catan.game HTTP
     * cookies). 2. There is space in the game for another player (i.e., the
     * game is not "full"). 3. The specified "AIType" is valid (i.e., one of the
     * values returned by the /game/listAI method).
     *
     * @post If the operation succeeds, 1. The server returns an HTTP
     * 200 success response with "Success" in the body. 2. A new AI player of
     * the specified type has been added to the current game. The server
     * selected a name and color for the player. If the operation fails, 1. The
     * server returns an HTTP 400 error response, and the body contains an error
     * message.
     *
     * Description: Adds an AI player to the current game. You must
     * login and join a game before calling this method.
     *
     * @param params
     *          param list
     * @return Success or error response
     */
    String gameAddAI(AddAIRequest params);

    /**
     *
     * @pre None
     *
     * @post If the operation succeeds, 1. The server returns an HTTP
     * 200 success response. 2. The body contains a JSON string array
     * enumerating the different types of AI players. These are the values that
     * may be passed to the /game/addAI method.
     *
     * Description: Returns a list of supported AI player types.
     * Currently, LARGEST_ARMY is the only supported type.
     *
     * @return JSON or error message
     */
    String gameListAI();

    /**
     * @param params
     *          param list
     * @pre None (this command may be executed at any time by any player)
     * @post The chat contains your message at the end
     */
    String sendChat(SendChatParams params);

    /**
     * @param params
     *          param list
     * @pre It is your turn
     * @pre The client model’s status is ‘Rolling’
     * @post The client model’s status is now in ‘Discarding’ or ‘Robbing’ or ‘Playing’
     */
    String rollNumber(RollNumberParams params);

    /**
     * @param params
     *          param list
     * @pre The robber is not being kept in the same location
     * @pre If a player is being robbed (i.e., victimIndex != ­1), the player being robbed has resource cards
     * @post The robber is in the new location
     * @post The player being robbed (if any) gave you one of his resource cards (randomly selected)
     */
    String robPlayer(RobPlayerParams params);

    /**
     * @pre None (except the preconditions for this section)
     * @post The cards in your new dev card hand have been transferred to your old dev card hand
     * @post It is the next player’s turn
     */
    String finishTurn(FinishTurnParams params);

    /**
     * @pre You have the required resources (1 ore, 1 wheat, 1 sheep)
     * @pre There are dev cards left in the deck
     * @post You have a new card
     * @post If it is a monument card, it has been added to your old devcard hand
     * @post If it is a non­monument card, it has been added to your new devcard hand (unplayable this turn)
     */
    String buyDevCard(BuyDevCardParams params);

    /**
     * @param params
     *          param list
     * @pre The two specified resources are in the bank
     * @post You gained the two specified resources
     */
    String playCardYearOfPlenty(YearOfPlentyParams params);

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
    String playCardRoadBuilding(RoadBuildingParams params);

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
    String playCardSoldier(SoldierParams params);

    /**
     * @param params
     *          param list
     * @pre None (except the general preconditions for this section)
     * @post All of the other players have given you all of their resource cards of the specified type
     */
    String playCardMonopoly(MonopolyParams params);

    /**
     * @pre You have enough monument cards to win the game (i.e., reach 10 victory points)
     * @post You gained a victory point
     */
    String playCardMonument(MonumentParams params);

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
    String buildRoad(BuildRoadParams params);

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
    String buildSettlement(BuildSettlementParams params);

    /**
     * @param params
     *          param list
     * @pre The city location is where you currently have a settlement
     * @pre You have the required resources (2 wheat, 3 ore; 1 city)
     * @post You lost the resources required to build a city (2 wheat, 3 ore; 1 city)
     * @post The city is on the map at the specified location
     * @post You got a settlement back
     */
    String buildCity(BuildCityParams params);

    /**
     * @param params
     *          param list
     * @pre You have the resources you are offering
     * @post The trade is offered to the other player (stored in the server model)
     */
    String offerTrade(OfferTradeParams params);

    /**
     * @param params
     *          param list
     * @pre You have been offered a domestic trade
     * @pre To accept the offered trade, you have the required resources
     * @post If you accepted, you and the player who offered swap the specified resources
     * @post If you declined no resources are exchanged
     * @post The trade offer is removed
     */
    String acceptTrade(AcceptTradeParams params);

    /**
     * @param params
     *          param list
     * @pre You have the resources you are giving
     * @pre For ratios less than 4, you have the correct port for the trade
     * @post The trade has been executed (the offered resources are in the bank, and the requested resource has been received)
     */
    String maritimeTrade(MaritimeTradeParams params);

    /**
     * @param params
     *          param list
     * @pre The status of the client model is 'Discarding'
     * @pre You have over 7 cards
     * @pre You have the cards you're choosing to discard
     * @post You gave up the specified resources
     * @post If you're the last one to discard, the client model status changes to 'Robbing
     */
    String discardCards(DiscardCardsParams params);

    /**
     * The server's new changeLogLevelRequest level.
     * The following values are allowed: ALL, SEVERE, WARNING ,INFO, CONFIG, FINE, FINER, FINEST, OFF
     */
    String utilChangeLogLevel(ChangeLogLevelRequest params);
}