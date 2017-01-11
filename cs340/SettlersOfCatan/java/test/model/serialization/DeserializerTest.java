package model.serialization;

import junit.framework.TestCase;
import model.gameboard.Hex;
import model.overhead.Model;
import shared.locations.HexLocation;

import java.util.List;

/**
 * Created by tsmit on 9/28/2016.
 */
public class DeserializerTest extends TestCase {
    String initJSONModelTest = "{\n" +
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
    Deserializer deserializer;
    Model baseModel;

    public void setUp() throws Exception {
        super.setUp();
        baseModel = new Model();
        baseModel.initializeModelDefault();
        deserializer = new Deserializer();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDeserialize (){
        Model testModel = deserializer.deserialize(initJSONModelTest);
        Model testModel2=new Model();
        testModel2.initializeModelDefault();
        //assertTrue(testModel.equals(testModel2));


        Serializer serializer=new Serializer();
        String jsonResult=serializer.serialize(testModel);
        Model testModel3=deserializer.deserialize(jsonResult);
        assertTrue(testModel.equals(testModel3));

        List<Hex> hexes=testModel.getGameBoard().getHexes();
        for(int i=0;i<hexes.size();++i){
            HexLocation compare=hexes.get(i).getHexLocation();
            for(int j=0;j<hexes.size();++j){
                if(i!=j)assertFalse(compare.equals(hexes.get(j).getHexLocation()));
            }
        }
        //testModel=deserializer.deserialize(sample2);
        //assertFalse(sample2.equals(testModel2));
        //equals function goes here between baseModel and testModel

    }
}
