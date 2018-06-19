package server;

import params.*;

/**
 * Created by chase on 9/16/16.
 *
 * only returns hard-coded strings for testing purposes
 */
public class MockServerProxy implements IServer {

    public String userLogin(Credentials params) {
        return "Success";
        //return "Failed to login - bad username or password.";
    }

    public String userRegister(Credentials params) {
        return "Success";
        //return "Failed to register - someone already has that username.";
    }

    public String gameList() {
        return "[\n" +
                "  {\n" +
                "    \"title\": \"Default Game\",\n" +
                "    \"id\": 0,\n" +
                "    \"players\": [\n" +
                "      {\n" +
                "        \"color\": \"orange\",\n" +
                "        \"name\": \"Sam\",\n" +
                "        \"id\": 0\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"blue\",\n" +
                "        \"name\": \"Brooke\",\n" +
                "        \"id\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"red\",\n" +
                "        \"name\": \"Pete\",\n" +
                "        \"id\": 10\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"green\",\n" +
                "        \"name\": \"Mark\",\n" +
                "        \"id\": 11\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"AI Game\",\n" +
                "    \"id\": 1,\n" +
                "    \"players\": [\n" +
                "      {\n" +
                "        \"color\": \"orange\",\n" +
                "        \"name\": \"Pete\",\n" +
                "        \"id\": 10\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"blue\",\n" +
                "        \"name\": \"Quinn\",\n" +
                "        \"id\": -2\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"green\",\n" +
                "        \"name\": \"Miguel\",\n" +
                "        \"id\": -3\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"purple\",\n" +
                "        \"name\": \"Scott\",\n" +
                "        \"id\": -4\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"Empty Game\",\n" +
                "    \"id\": 2,\n" +
                "    \"players\": [\n" +
                "      {\n" +
                "        \"color\": \"orange\",\n" +
                "        \"name\": \"Sam\",\n" +
                "        \"id\": 0\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"blue\",\n" +
                "        \"name\": \"Brooke\",\n" +
                "        \"id\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"red\",\n" +
                "        \"name\": \"Pete\",\n" +
                "        \"id\": 10\n" +
                "      },\n" +
                "      {\n" +
                "        \"color\": \"green\",\n" +
                "        \"name\": \"Mark\",\n" +
                "        \"id\": 11\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"string\",\n" +
                "    \"id\": 3,\n" +
                "    \"players\": [\n" +
                "      {\n" +
                "        \"color\": \"blue\",\n" +
                "        \"name\": \"string\",\n" +
                "        \"id\": 12\n" +
                "      },\n" +
                "      {},\n" +
                "      {},\n" +
                "      {}\n" +
                "    ]\n" +
                "  }\n" +
                "]";
    }

    public String gameCreate(CreateGameRequest params) {
        return "{\n" +
                "  \"title\": \"string\",\n" +
                "  \"id\": 3,\n" +
                "  \"players\": [\n" +
                "    {},\n" +
                "    {},\n" +
                "    {},\n" +
                "    {}\n" +
                "  ]\n" +
                "}";
        //return "Invalid request: randomTiles has bad value";
    }

    public String gameJoin(JoinGameRequest params) {
        return "Success";
        //return "The player could not be added to the specified game.";
    }

    public String gameSave(SaveGameRequest params) {
        return "Success";
        //return "Could not save game";
    }

    public String gameLoad(LoadGameRequest params) {
        return "Success";
        //return "Could not load game";
    }

    public String gameModel(Version params) {
        return gameReset();
    }

    //master model returner b/c no params
    public String gameReset() {
        return "{\n" +
                "  \"deck\": {\n" +
                "    \"yearOfPlenty\": 2,\n" +
                "    \"monopoly\": 2,\n" +
                "    \"soldier\": 14,\n" +
                "    \"roadBuilding\": 2,\n" +
                "    \"monument\": 5\n" +
                "  },\n" +
                "  \"map\": {\n" +
                "    \"hexes\": [\n" +
                "      {\n" +
                "        \"resource\": \"brick\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": -2\n" +
                "        },\n" +
                "        \"number\": 8\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wheat\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 1,\n" +
                "          \"y\": -2\n" +
                "        },\n" +
                "        \"number\": 6\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"sheep\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 2,\n" +
                "          \"y\": -2\n" +
                "        },\n" +
                "        \"number\": 10\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"sheep\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -1,\n" +
                "          \"y\": -1\n" +
                "        },\n" +
                "        \"number\": 10\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wood\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": -1\n" +
                "        },\n" +
                "        \"number\": 11\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wood\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 1,\n" +
                "          \"y\": -1\n" +
                "        },\n" +
                "        \"number\": 3\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wood\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 2,\n" +
                "          \"y\": -1\n" +
                "        },\n" +
                "        \"number\": 4\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wheat\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -2,\n" +
                "          \"y\": 0\n" +
                "        },\n" +
                "        \"number\": 11\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wood\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -1,\n" +
                "          \"y\": 0\n" +
                "        },\n" +
                "        \"number\": 6\n" +
                "      },\n" +
                "      {\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": 0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"brick\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 1,\n" +
                "          \"y\": 0\n" +
                "        },\n" +
                "        \"number\": 5\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"ore\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 2,\n" +
                "          \"y\": 0\n" +
                "        },\n" +
                "        \"number\": 3\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"sheep\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -2,\n" +
                "          \"y\": 1\n" +
                "        },\n" +
                "        \"number\": 9\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"ore\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -1,\n" +
                "          \"y\": 1\n" +
                "        },\n" +
                "        \"number\": 9\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wheat\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": 1\n" +
                "        },\n" +
                "        \"number\": 8\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"sheep\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 1,\n" +
                "          \"y\": 1\n" +
                "        },\n" +
                "        \"number\": 12\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"ore\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -2,\n" +
                "          \"y\": 2\n" +
                "        },\n" +
                "        \"number\": 5\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"wheat\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -1,\n" +
                "          \"y\": 2\n" +
                "        },\n" +
                "        \"number\": 2\n" +
                "      },\n" +
                "      {\n" +
                "        \"resource\": \"brick\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": 2\n" +
                "        },\n" +
                "        \"number\": 4\n" +
                "      }\n" +
                "    ],\n" +
                "    \"roads\": [],\n" +
                "    \"cities\": [],\n" +
                "    \"settlements\": [],\n" +
                "    \"radius\": 3,\n" +
                "    \"ports\": [\n" +
                "      {\n" +
                "        \"ratio\": 2,\n" +
                "        \"resource\": \"sheep\",\n" +
                "        \"direction\": \"SE\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -3,\n" +
                "          \"y\": 0\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 2,\n" +
                "        \"resource\": \"ore\",\n" +
                "        \"direction\": \"NW\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 2,\n" +
                "          \"y\": 1\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 3,\n" +
                "        \"direction\": \"SW\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 3,\n" +
                "          \"y\": -3\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 3,\n" +
                "        \"direction\": \"NE\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -3,\n" +
                "          \"y\": 2\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 2,\n" +
                "        \"resource\": \"wood\",\n" +
                "        \"direction\": \"NE\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -2,\n" +
                "          \"y\": 3\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 3,\n" +
                "        \"direction\": \"S\",\n" +
                "        \"location\": {\n" +
                "          \"x\": -1,\n" +
                "          \"y\": -2\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 2,\n" +
                "        \"resource\": \"wheat\",\n" +
                "        \"direction\": \"NW\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 3,\n" +
                "          \"y\": -1\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 2,\n" +
                "        \"resource\": \"brick\",\n" +
                "        \"direction\": \"S\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 1,\n" +
                "          \"y\": -3\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"ratio\": 3,\n" +
                "        \"direction\": \"N\",\n" +
                "        \"location\": {\n" +
                "          \"x\": 0,\n" +
                "          \"y\": 3\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"robber\": {\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 0\n" +
                "    }\n" +
                "  },\n" +
                "  \"players\": [\n" +
                "    {\n" +
                "      \"resources\": {\n" +
                "        \"brick\": 0,\n" +
                "        \"wood\": 0,\n" +
                "        \"sheep\": 0,\n" +
                "        \"wheat\": 0,\n" +
                "        \"ore\": 0\n" +
                "      },\n" +
                "      \"oldDevCards\": {\n" +
                "        \"yearOfPlenty\": 0,\n" +
                "        \"monopoly\": 0,\n" +
                "        \"soldier\": 0,\n" +
                "        \"roadBuilding\": 0,\n" +
                "        \"monument\": 0\n" +
                "      },\n" +
                "      \"newDevCards\": {\n" +
                "        \"yearOfPlenty\": 0,\n" +
                "        \"monopoly\": 0,\n" +
                "        \"soldier\": 0,\n" +
                "        \"roadBuilding\": 0,\n" +
                "        \"monument\": 0\n" +
                "      },\n" +
                "      \"roads\": 15,\n" +
                "      \"cities\": 4,\n" +
                "      \"settlements\": 5,\n" +
                "      \"soldiers\": 0,\n" +
                "      \"victoryPoints\": 0,\n" +
                "      \"monuments\": 0,\n" +
                "      \"playedDevCard\": false,\n" +
                "      \"discarded\": false,\n" +
                "      \"playerID\": 12,\n" +
                "      \"playerIndex\": 0,\n" +
                "      \"name\": \"string\",\n" +
                "      \"color\": \"blue\"\n" +
                "    },\n" +
                "    null,\n" +
                "    null,\n" +
                "    null\n" +
                "  ],\n" +
                "  \"changeLogLevelRequest\": {\n" +
                "    \"lines\": []\n" +
                "  },\n" +
                "  \"chat\": {\n" +
                "    \"lines\": []\n" +
                "  },\n" +
                "  \"bank\": {\n" +
                "    \"brick\": 24,\n" +
                "    \"wood\": 24,\n" +
                "    \"sheep\": 24,\n" +
                "    \"wheat\": 24,\n" +
                "    \"ore\": 24\n" +
                "  },\n" +
                "  \"turnTracker\": {\n" +
                "    \"status\": \"FirstRound\",\n" +
                "    \"currentTurn\": 0,\n" +
                "    \"longestRoad\": -1,\n" +
                "    \"largestArmy\": -1\n" +
                "  },\n" +
                "  \"winner\": -1,\n" +
                "  \"version\": 0\n" +
                "}";
    }

    public String gameCommandsPost(CommandList params) {
        return gameReset();
        //return "The command list is invalid.";
        //return "The command list is missing.";
    }

    public String gameCommandsGet() {
        return "[]";
    }

    public String gameAddAI(AddAIRequest params) {
        return "Success";
        //return "Could not add AI player  [LARGEST_ARMY]";
    }

    public String gameListAI() {
        return "[\n" +
            "  \"LARGEST_ARMY\"\n" +
            "]";
    }

    public String sendChat(SendChatParams params) {
        return gameReset();
    }

    public String rollNumber(RollNumberParams params) {
        return gameReset();
    }

    public String robPlayer(RobPlayerParams params) {
        return gameReset();
    }

    public String finishTurn(FinishTurnParams params) {
        return gameReset();
    }

    public String buyDevCard(BuyDevCardParams params) {
        return gameReset();
    }

    public String playCardYearOfPlenty(YearOfPlentyParams params) {
        return gameReset();
    }

    public String playCardRoadBuilding(RoadBuildingParams params) {
        return gameReset();
    }

    public String playCardSoldier(SoldierParams params) {
        return gameReset();
    }

    public String playCardMonopoly(MonopolyParams params) {
        return gameReset();
    }

    public String playCardMonument(MonumentParams params) {
        return gameReset();
    }

    public String buildRoad(BuildRoadParams params) {
        return gameReset();
    }

    public String buildSettlement(BuildSettlementParams params) {
        return gameReset();
    }

    public String buildCity(BuildCityParams params) {
        return gameReset();
    }

    public String offerTrade(OfferTradeParams params) {
        return gameReset();
    }

    public String acceptTrade(AcceptTradeParams params) {
        return gameReset();
    }

    public String maritimeTrade(MaritimeTradeParams params) {
        return gameReset();
    }

    public String discardCards(DiscardCardsParams params) {
        return gameReset();
    }

    public String utilChangeLogLevel(ChangeLogLevelRequest params) {
        return "Success";
        //return "Invalid changeLogLevelRequest level: STRING";
    }
}