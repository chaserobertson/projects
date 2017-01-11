package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import commands.*;
import debugger.Debugger;
import params.*;
import plugin.PluginManager;

public class ServerCommunicator {

    private enum RequestType {LOGIN,REGISTER,LIST,CREATE,JOIN,MODEL,ADD_AI,LIST_AI,
        SEND_CHAT,ROLL_NUMBER,ROB_PLAYER,FINISH_TURN,BUY_DEV_CARD,
        YEAR_OF_PLENTY,ROAD_BUILDING,SOLDIER,MONOPOLY,MONUMENT,
        BUILD_ROAD,BUILD_SETTLEMENT,BUILD_CITY,OFFER_TRADE,ACCEPT_TRADE,MARITIME_TRADE,DISCARD}

    public static void main(String[] args) throws Exception {
        String persistenceType = "persistance.FlatPersistance.FlatPersistanceFactory";
        int numCommands = 10;
        int port = 8081;
        boolean wipe = false;
        //parse args
        for(int i = 0; i < args.length; ++i) {
            if(args[i].equals("-persistence-type") && args.length > i) {
                persistenceType = args[i+1];
                Debugger.LogMessage("persistence type = "+persistenceType);
            }
            else if(args[i].equals("-commands-between-checkpoints") && args.length > i) {
                try {
                    numCommands = new Integer(args[i+1]);
                } catch (Exception e) {
                    numCommands = 10;
                }
                Debugger.LogMessage("num commands = "+numCommands);
            }
            else if(args[i].equals("-port") && args.length > i) {
                try {
                    port = new Integer(args[i+1]);
                } catch (Exception e) {
                    port = 8081;
                }
                Debugger.LogMessage("port = "+port);
            }
            else if(args[i].equals("-wipe") && args.length > i) {
                try {
                    wipe = new Boolean(args[i+1]);
                } catch (Exception e) {
                    wipe = false;
                }
                Debugger.LogMessage("wipe = "+wipe);
            }
        }

        PluginManager.getInstance().setPersistence(persistenceType, numCommands, wipe);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/docs/api/data", new Handlers.JSONAppender(""));
        server.createContext("/docs/api/view", new Handlers.BasicFile(""));
        createCommandContexts(server);
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static void createCommandContexts(HttpServer server) {
        server.createContext("/user/login", new Handler(RequestType.LOGIN));
        server.createContext("/user/register", new Handler(RequestType.REGISTER));
        server.createContext("/games/list", new Handler(RequestType.LIST)); //GET
        server.createContext("/games/create", new Handler(RequestType.CREATE));
        server.createContext("/games/join", new Handler(RequestType.JOIN));
        //games/save
        //games/load
        server.createContext("/game/model", new Handler(RequestType.MODEL)); //GET
        //game/reset
        //game/commands & commands GET
        server.createContext("/game/addAI", new Handler(RequestType.ADD_AI));
        server.createContext("/game/listAI", new Handler(RequestType.LIST_AI));
        server.createContext("/moves/sendChat", new Handler(RequestType.SEND_CHAT));
        server.createContext("/moves/rollNumber", new Handler(RequestType.ROLL_NUMBER));
        server.createContext("/moves/robPlayer", new Handler(RequestType.ROB_PLAYER));
        server.createContext("/moves/finishTurn", new Handler(RequestType.FINISH_TURN));
        server.createContext("/moves/buyDevCard", new Handler(RequestType.BUY_DEV_CARD));
        server.createContext("/moves/Year_of_Plenty", new Handler(RequestType.YEAR_OF_PLENTY));
        server.createContext("/moves/Road_Building", new Handler(RequestType.ROAD_BUILDING));
        server.createContext("/moves/Soldier", new Handler(RequestType.SOLDIER));
        server.createContext("/moves/Monopoly", new Handler(RequestType.MONOPOLY));
        server.createContext("/moves/Monument", new Handler(RequestType.MONUMENT));
        server.createContext("/moves/buildRoad", new Handler(RequestType.BUILD_ROAD));
        server.createContext("/moves/buildSettlement", new Handler(RequestType.BUILD_SETTLEMENT));
        server.createContext("/moves/buildCity", new Handler(RequestType.BUILD_CITY));
        server.createContext("/moves/offerTrade", new Handler(RequestType.OFFER_TRADE));
        server.createContext("/moves/acceptTrade", new Handler(RequestType.ACCEPT_TRADE));
        server.createContext("/moves/maritimeTrade", new Handler(RequestType.MARITIME_TRADE));
        server.createContext("/moves/discardCards", new Handler(RequestType.DISCARD));
        //util/changeLogLevel
    }

    private static class Handler implements HttpHandler {
        private RequestType requestType;
        private static final String ERROR_MESSAGE = "unsuccessful";
        private static final String MODEL_ERROR = "Invalid Request";
        private static final String SUCCESS = "Success";
        private static final String PATH = ";Path=/;";

        public Handler(RequestType requestType) {
            super();
            this.requestType = requestType;
        }

        private List<String> createUserCookie(Credentials credentials, int playerID) {
            String userCookie = "catan.user=";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", credentials.username);
            jsonObject.addProperty("password", credentials.password);
            jsonObject.addProperty("playerID", playerID);
            userCookie += URLEncoder.encode(jsonObject.toString());

            userCookie += PATH;

            List<String> output = new ArrayList<>();
            output.add(userCookie);
            return output;
        }

        private List<String> createGameCookie(JoinGameRequest joinGameRequest) {
            String gameCookie = "catan.game=";
            gameCookie += joinGameRequest.gameId;

            gameCookie += PATH;

            List<String> output = new ArrayList<>();
            output.add(gameCookie);
            return output;
        }

        @Override
        public void handle(HttpExchange t) throws IOException {
            String requestBody = Handlers.getRequestBody(t); // null when no requestBody body e.g. GET
            Headers requestHeaders = t.getRequestHeaders();
            List<String> cookies = requestHeaders.get("Cookie"); //look for existing cookies
            Cookie cookie = new Cookie(cookies);
            //Debugger.LogMessage(cookie.toString());

            Headers responseHeaders = t.getResponseHeaders();
            String responseBody;
            int responseCode;

            List<String> content = new ArrayList<>();
            content.add("text/html");
            content.add("application/json");
            responseHeaders.put("Content-Type", content);

            switch(requestType) {
                case LOGIN:
                    Credentials loginCredentials = new Credentials(requestBody);
                    LoginCommand loginCommand = new LoginCommand(loginCredentials);
                    responseBody = loginCommand.execute();
                    if(responseBody.equals(SUCCESS)) {
                        int playerId = ServerFacade.getInstance().getLoginId(loginCredentials);
                        responseHeaders.put("Set-cookie", createUserCookie(loginCredentials, playerId));
                    }
                    break;
                case REGISTER:
                    Credentials registerCredentials = new Credentials(requestBody);
                    RegisterCommand registerCommand = new RegisterCommand(registerCredentials);
                    responseBody = registerCommand.execute();
                    break;
                case LIST: //GET
                    GetGamesListCommand getGamesListCommand = new GetGamesListCommand();
                    responseBody = getGamesListCommand.execute();
                    break;
                case CREATE:
                    CreateGameRequest createGameRequest = new CreateGameRequest(requestBody);
                    createGameRequest.creatorId = cookie.playerID;
                    CreateGameCommand createGameCommand = new CreateGameCommand(createGameRequest);
                    responseBody = createGameCommand.execute();
                    break;
                case JOIN:
                    //Debugger.LogMessage(requestBody);
                    JoinGameRequest joinGameRequest = new JoinGameRequest(requestBody);
                    joinGameRequest.gameId = joinGameRequest.id;
                    joinGameRequest.id = cookie.playerID;
                    JoinGameCommand joinGameCommand = new JoinGameCommand(joinGameRequest);
                    responseBody = joinGameCommand.execute();
                    if(responseBody.equals(SUCCESS)) {
                        responseHeaders.put("Set-cookie", createGameCookie(joinGameRequest));
                    }
                    break;
                case MODEL:
                    Version version;
                    if(requestBody == null || requestBody.isEmpty()) {
//                        URI uri = t.getRequestURI();
//                        String params = uri.getQuery();
//                        int equalsIndex = params.indexOf('=');
//                        String versionNumber = params.substring(equalsIndex+1,params.length());
//                        version = new Version(versionNumber);
                        version = new Version(0);
                    }
                    else version = new Version(requestBody);
                    version.gameId = cookie.gameID;
                    GetGameModelCommand getGameModelCommand = new GetGameModelCommand(version);
                    responseBody = getGameModelCommand.execute();
                    break;
                case ADD_AI:
                    AddAIRequest addAIRequest = new AddAIRequest(requestBody);
                    addAIRequest.gameId = cookie.gameID;
                    AddAICommand addAICommand = new AddAICommand(addAIRequest);
                    responseBody = addAICommand.execute();
                    break;
                case LIST_AI:
                    ListAICommand listAICommand = new ListAICommand();
                    responseBody = listAICommand.execute();
                    break;
                case SEND_CHAT:
                    SendChatParams sendChatParams = new SendChatParams(requestBody);
                    sendChatParams.gameId = cookie.gameID;
                    SendChatCommand sendChatCommand = new SendChatCommand(sendChatParams);
                    responseBody = sendChatCommand.execute();
                    break;
                case ROLL_NUMBER:
                    RollNumberParams rollNumberParams = new RollNumberParams(requestBody);
                    rollNumberParams.gameId = cookie.gameID;
                    RollCommand rollCommand = new RollCommand(rollNumberParams);
                    responseBody = rollCommand.execute();
                    break;
                case ROB_PLAYER:
                    RobPlayerParams robPlayerParams = new RobPlayerParams(requestBody);
                    robPlayerParams.gameId = cookie.gameID;
                    RobPlayerCommand robPlayerCommand = new RobPlayerCommand(robPlayerParams);
                    responseBody = robPlayerCommand.execute();
                    break;
                case FINISH_TURN:
                    FinishTurnParams finishTurnParams = new FinishTurnParams(requestBody);
                    finishTurnParams.gameId = cookie.gameID;
                    FinishTurnCommand finishTurnCommand = new FinishTurnCommand(finishTurnParams);
                    responseBody = finishTurnCommand.execute();
                    break;
                case BUY_DEV_CARD:
                    BuyDevCardParams buyDevCardParams = new BuyDevCardParams(requestBody);
                    buyDevCardParams.gameId = cookie.gameID;
                    BuyDevCardCommand buyDevCardCommand = new BuyDevCardCommand(buyDevCardParams);
                    responseBody = buyDevCardCommand.execute();
                    break;
                case YEAR_OF_PLENTY:
                    YearOfPlentyParams yearOfPlentyParams = new YearOfPlentyParams(requestBody);
                    yearOfPlentyParams.gameId = cookie.gameID;
                    YearOfPlentyCommand yearOfPlentyCommand = new YearOfPlentyCommand(yearOfPlentyParams);
                    responseBody = yearOfPlentyCommand.execute();
                    break;
                case ROAD_BUILDING:
                    RoadBuildingParams roadBuildingParams = new RoadBuildingParams(requestBody);
                    roadBuildingParams.gameId = cookie.gameID;
                    RoadBuildingCommand roadBuildingCommand = new RoadBuildingCommand(roadBuildingParams);
                    responseBody = roadBuildingCommand.execute();
                    break;
                case SOLDIER:
                    SoldierParams soldierParams = new SoldierParams(requestBody);
                    soldierParams.gameId = cookie.gameID;
                    SoldierCommand soldierCommand = new SoldierCommand(soldierParams);
                    responseBody = soldierCommand.execute();
                    break;
                case MONOPOLY:
                    MonopolyParams monopolyParams = new MonopolyParams(requestBody);
                    monopolyParams.gameId = cookie.gameID;
                    MonopolyCommand monopolyCommand = new MonopolyCommand(monopolyParams);
                    responseBody = monopolyCommand.execute();
                    break;
                case MONUMENT:
                    MonumentParams monumentParams = new MonumentParams(requestBody);
                    monumentParams.gameId = cookie.gameID;
                    MonumentCommand monumentCommand = new MonumentCommand(monumentParams);
                    responseBody = monumentCommand.execute();
                    break;
                case BUILD_ROAD:
                    BuildRoadParams buildRoadParams = new BuildRoadParams(requestBody);
                    buildRoadParams.gameId = cookie.gameID;
                    BuildRoadCommand buildRoadCommand = new BuildRoadCommand(buildRoadParams);
                    responseBody = buildRoadCommand.execute();
                    break;
                case BUILD_SETTLEMENT:
                    BuildSettlementParams buildSettlementParams = new BuildSettlementParams(requestBody);
                    buildSettlementParams.gameId = cookie.gameID;
                    BuildSettlementCommand buildSettlementCommand = new BuildSettlementCommand(buildSettlementParams);
                    responseBody = buildSettlementCommand.execute();
                    break;
                case BUILD_CITY:
                    BuildCityParams buildCityParams = new BuildCityParams(requestBody);
                    buildCityParams.gameId = cookie.gameID;
                    BuildCityCommand buildCityCommand = new BuildCityCommand(buildCityParams);
                    responseBody = buildCityCommand.execute();
                    break;
                case OFFER_TRADE:
                    OfferTradeParams offerTradeParams = new OfferTradeParams(requestBody);
                    offerTradeParams.gameId = cookie.gameID;
                    OfferTradeCommand offerTradeCommand = new OfferTradeCommand(offerTradeParams);
                    responseBody = offerTradeCommand.execute();
                    break;
                case ACCEPT_TRADE:
                    AcceptTradeParams acceptTradeParams = new AcceptTradeParams(requestBody);
                    acceptTradeParams.gameId = cookie.gameID;
                    AcceptTradeCommand acceptTradeCommand = new AcceptTradeCommand(acceptTradeParams);
                    responseBody = acceptTradeCommand.execute();
                    break;
                case MARITIME_TRADE:
                    MaritimeTradeParams maritimeTradeParams = new MaritimeTradeParams(requestBody);
                    maritimeTradeParams.gameId = cookie.gameID;
                    MaritimeTradeCommand maritimeTradeCommand = new MaritimeTradeCommand(maritimeTradeParams);
                    responseBody = maritimeTradeCommand.execute();
                    break;
                case DISCARD:
                    DiscardCardsParams discardCardsParams = new DiscardCardsParams(requestBody);
                    discardCardsParams.gameId = cookie.gameID;
                    DiscardCommand discardCommand = new DiscardCommand(discardCardsParams);
                    responseBody = discardCommand.execute();
                    break;
                default:
                    responseBody = ERROR_MESSAGE;
                    break;
            }

            if(responseBody.equals(ERROR_MESSAGE) || responseBody.equals(MODEL_ERROR)) responseCode = 400;
            else responseCode = 200;

            //Debugger.LogMessage(responseBody);
            //Debugger.LogMessage(responseHeaders.entrySet().toString());

            t.sendResponseHeaders(responseCode, responseBody.length());
            OutputStream os = t.getResponseBody();
            os.write(responseBody.getBytes());
            os.close();
        }

        private class Cookie {
            public String name = null;
            public String password = null;
            public int playerID = -1;
            public int gameID = -1;

            public Cookie(List<String> cookies) {
                if(cookies == null) return;
                //Debugger.LogMessage("cookieList length "+cookies.size());
                for(String cookieString : cookies) {
                    if(cookieString == null || cookieString.length() < 4) continue;
                    cookieString = URLDecoder.decode(cookieString);
                    //Debugger.LogMessage(cookieString);

                    boolean doubleCookie = cookieString.contains(";");
                    if(doubleCookie) {
                        int semicolonIndex = cookieString.indexOf(';');
                        String firstCookie = cookieString.substring(0,semicolonIndex);
                        addCookie(firstCookie);
                        String secondCookie = cookieString.substring(semicolonIndex+2, cookieString.length());
                        addCookie(secondCookie);
                    }
                    else {
                        addCookie(cookieString);
                    }
                }
            }

            private void addCookie(String cookieString) {
                //Debugger.LogMessage("Cookie read: "+cookieString);
                int equalsIndex = cookieString.indexOf('=');
                int semicolonIndex = cookieString.length();
                String name = cookieString.substring(0, equalsIndex);
                String value = cookieString.substring(equalsIndex + 1, semicolonIndex);

                if (name.equals("catan.user")) {
                    Gson gsonBuilder = new GsonBuilder().create();
                    JsonObject cookieObject = gsonBuilder.fromJson(value, JsonObject.class);
                    this.playerID = cookieObject.get("playerID").getAsInt();
                    this.name = cookieObject.get("name").getAsString();
                    this.password = cookieObject.get("password").getAsString();
                }
                else if (name.equals("catan.game")) {
                    this.gameID = new Integer(value);
                }
            }

            @Override
            public String toString() {
                return "Cookie{" +
                        "name='" + name + '\'' +
                        ", password='" + password + '\'' +
                        ", playerID=" + playerID +
                        ", gameID=" + gameID +
                        '}';
            }
        }
    }

}