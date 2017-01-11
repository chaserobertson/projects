package model.overhead;

import client.data.PlayerInfo;
import debugger.Debugger;
import model.definitions.EnumConverter;
import model.facade.shared.SerializeFacade;
import model.gameboard.GameBoard;
import model.gameboard.NodePoint;
import model.gameboard.Port;
import model.peripherals.Peripherals;
import model.peripherals.TradeOffer;
import model.player.Player;
import model.resources.CardBank;
import model.resources.DevelopmentCard;
import model.resources.ResourcePile;
import model.structures.City;
import model.structures.Colony;
import model.structures.Road;
import model.structures.Settlement;
import server.HTTPServerProxy;
import shared.definitions.CatanColor;
import shared.definitions.PortType;
import shared.locations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MTAYS on 12/6/2016.
 */
public class Model implements Serializable {
    /**
     * Description: Owned GameBoard
     */
    private GameBoard gameBoard;
    /**
     * Description: List of Players in game
     */
    private List<Player> players;
    private Peripherals peripherals;
    /**
     * Description: version number of the Model.
     */
    private int version;
    /**
     * @pre none
     * @post a new blank model is created with gameboard=null,players an emptay list, turn =0, active id=0, development card empty, dice 1 1
     */
    public Model(){
        gameBoard=new GameBoard();//Solid
        players=new ArrayList<Player>();//Not much more can be done here
        peripherals=new Peripherals();//Good
        version = 0;
    }
    public Model(boolean randomizedHexes, boolean randomizedChits, boolean randomizedPorts){//Valid only for traditional gameboard
        peripherals=new Peripherals();//always same
        players=new ArrayList<>();//Always same. Does not start with a player, requires input
        gameBoard=new GameBoard(randomizedHexes,randomizedChits,randomizedPorts);
        gameBoard.placeRobberOnDesert(peripherals.getRobber());
        version=0;
    }
    
    public int getVersion(){return version;}
    
    public void setVersion(int version){this.version=version;}
    
    public void incrementVersion(){
        //This is kinda like an update... So since its a pain I'm going to have it search for winners
        for(Player player:players){
            if(player.getVictoryPoints()>=10){
                peripherals.setWinner(player.getIndex());
                break;
            }
        }
        ++version;
    }
    
    public void decrementVersion(){
        --version;
    }
    //GameBoard
    
    public GameBoard getGameBoard(){return gameBoard;}
    
    public void setGameBoard(GameBoard gameBoard){this.gameBoard=gameBoard;}
    /**
     * @param location
     * 		-the location of the desired NodePoint
     * @pre gameBoard is initialized and valid
     * @post returns the NodePoint at the desired Location
     * @return the NodePoint at the location, or null
     */
    
    public NodePoint getNodeAt(VertexLocation location){
        return gameBoard.getNodePointAt(location);
    }
    //Players
    
    public List<Player> getPlayers(){return players;}
    
    public List<PlayerInfo> getPlayerInfos(){
        List<PlayerInfo> result=new ArrayList<>(players.size());
        for(Player player:players){
            result.add(player.generatePlayerInfo());
        }
        return result;
    }
    
    public List<CatanColor> getAllColors(){
        List<CatanColor> result=new ArrayList<>(players.size());
        for(Player player:players){
            result.add(player.getColor());
        }
        return result;
    }
    
    public Player getPlayer(){
        //This might be buggy. I had issues with it. -Matthew
        int playerID = HTTPServerProxy.getInstance().getPlayerID();
        for(Player player:players) {
            if(player.getInternetID()==playerID)return player;
        }
        return null;
    }
    
    public Player getPlayerByIndex(int index){
        for (Player player:players) {
            if(player.getIndex()==index)return player;
        }
        return null;
    }
    
    public Player getPlayerByNickname(String nickname){
        for (Player player:players) {
            if(player.getNickname().equals(nickname))return player;
        }
        return null;
    }
    
    public Player getPlayerByInternetID(int ID){
        for(Player player:players){
            if(player.getInternetID()==ID)return player;
        }
        return null;
    }
    
    public void setPlayers(List<Player> players){
        this.players=players;
        peripherals.getTurnTracker().setActivePlayers(players.size());
    }
    
    public int getPlayerCount(){return players.size();}
    
    public void addPlayer(Player player){
        players.add(player);
        peripherals.getTurnTracker().setActivePlayers(players.size());
    }
    
    public void addPlayer(int index, String name, CatanColor color, int id){
        addPlayer(new Player(index,name,color,id));
    }
    //Peripherals Commands
    
    public Peripherals getPeripherals(){return peripherals;}
    
    public void setPeripherals(Peripherals peripherals){this.peripherals=peripherals;}
    
    public int getActiveID(){return peripherals.getTurnTracker().getActiveID();}
    
    public void setActiveID(int activeID){peripherals.getTurnTracker().setActiveID(activeID);}
    
    public void advanceActiveID(){
        peripherals.getTurnTracker().advanceActiveID();
    }
    
    public List<DevelopmentCard> getDevelopmentCardDeck(){return peripherals.getGameBank().getDevelopmentCards();}
    
    public void setDevelopmentCardDeck(List<DevelopmentCard> developmentCardDeck){peripherals.getGameBank().setDevelopmentCards(developmentCardDeck);}
    
    public void addDevelopmentCard(DevelopmentCard developmentCard){peripherals.getGameBank().getDevelopmentCards().add(developmentCard);}
    /**
     * @pre developmentCards is a valid list
     * @post returns null if there is no card, else it returns the top card on the list and removes it
     * @return the top development card or null
     */
    
    public DevelopmentCard drawDevelopmentCard(){
        return peripherals.drawDevCard();
    }
    /**
     * @pre develompment cards is a valid list
     * @post the development cards are randomly reordered
     */
    
    public void shuffleDevelopmentCardDeck(){peripherals.shuffleDevelopmentCardDeck();}
    /**
     * @param developmentCards
     * 		-an array or series of developmentCards
     * @pre developmentcards is a valid list
     * @post the specified developmentcards are appended to the end of the deck of development cards
     */
    
    public void addDevelopmentCards(DevelopmentCard... developmentCards){
        CardBank gameBank=peripherals.getGameBank();
        for(DevelopmentCard developmentCard : developmentCards){
            gameBank.getDevelopmentCards().add(developmentCard);
        }
    }
    
    public ResourcePile getResourceAggregation(){return peripherals.getResourcePile();}
    
    public void setResourceAggregation(ResourcePile resourceDeck){peripherals.getGameBank().setResourcePile(resourceDeck);}
    
    public TradeOffer getTradeOffer(){return peripherals.getActiveTradeOffer();}
    
    public void setTradeOffer(TradeOffer tradeOffer){peripherals.setActiveTradeOffer(tradeOffer);}
    
    public String getChatMessages(){return peripherals.getChatLog().toString();}
    
    public void addChatMessage(Player player, String message){peripherals.getChatLog().addMessage(player,message);}
    
    public String getLogMessages(){return peripherals.getSystemLog().toString();}
    
    public void addLogMessage(String source, String message){peripherals.getSystemLog().addMessage(source,message);}
    
    public void recalculateLongestRoad(){
        Player best=null;
        Player incumbant=peripherals.getLongestRoad().getPlayer();
        int maxLength=0;
        for(Player player:players){
            int temp=player.getMaxRoadLength();
            if((
                    temp>maxLength||
                            (incumbant!=null&&incumbant.getIndex()==player.getIndex()&&temp>=maxLength)
            )&&temp>=5){
                maxLength=temp;
                best=player;
            }
        }
        try {
            peripherals.getLongestRoad().moveToPlayer(best);
        }catch(Exception e){
            Debugger.LogMessage("Model:RecalculateLongestRoad:Error moving road to player:");
            if(best==null)Debugger.LogMessage("null\n");
            else Debugger.LogMessage(best.getNickname()+"\n");
        }
    }
    /**
     * @pre game is initialized
     * @post distributes the LargestArmy award to the player with the largetArmy IF they qualify minimum requirements
     */
    
    public void recalculateLargestArmy(){
        Player best=peripherals.getLargestArmy().getPlayer();
        int maxSize=0;
        if(best!=null){
            maxSize=best.getArmySize();
        }
        for(Player player:players){
            int temp=player.getArmySize();
            if(temp>maxSize&&temp>=3){
                maxSize=temp;
                best=player;
            }
        }
        try {
            peripherals.getLargestArmy().moveToPlayer(best);
        }catch(Exception e){
            Debugger.LogMessage("Model:RecalculateLargestArmy:Error moving road to player:");
            if(best==null)Debugger.LogMessage("null\n");
            else Debugger.LogMessage(best.getNickname()+"\n");
        }
    }



    /**
     * @pre none
     * @post model is cleared of data and then is initialized with generic DevelopmentCard deck, generic Resources, a gameboard of deserts, robber placed at top, and dice with values 1 and 1
     * @post player list is empty
     */
    
    public void initializeModelBlank(){//Creates Development cards, resource cards, sets turn number 0, creates EMPTY list players,
        //creates a gameboard where all hexes are deserts, starts dice, starts robber and places on a random tile.
        peripherals.initializePeripherals();
        players=new ArrayList<Player>();
        initializeGameBoardBlank();
        gameBoard.placeRobberOnDesert(peripherals.getRobber());
    }
    private void initializeGameBoardBlank(){
        gameBoard.initializeGameBoardBlank();
    }

    
    public void initializeModelDefault(){
        gameBoard=new GameBoard();
        gameBoard.initializeDefaultGameBoard();
        players=new ArrayList<>();
        players.add(new Player(0,"string", CatanColor.GREEN,12));
        players.add(new Player(1,"Quinn",CatanColor.YELLOW,-2));
        players.add(new Player(2,"Scott",CatanColor.BLUE,-3));
        players.add(new Player(3,"Steve",CatanColor.WHITE,-4));
        version=0;
        peripherals=new Peripherals();
        peripherals.initializeBankData(24,24,24,24,24);
        peripherals.getTurnTracker().setActivePlayers(players.size());
        gameBoard.placeRobberOnDesert(peripherals.getRobber());
    }

    
    public boolean equals(Model model){
        if(model==null)return false;
        if(version!=model.getVersion())return false;
        if(!gameBoard.equals(model.getGameBoard())) return false;
        if(!peripherals.equals(model.getPeripherals()))return false;
        if(players.size()!=model.getPlayers().size())return false;
        for(int i=0;i<players.size();++i){
            if(!players.get(i).equals(model.getPlayers().get(i)))return false;
        }
        return true;
    }


    
    public String serialize(){
        //TODO:implement
		/*notes on Deserialization
		Players must be loaded first. This is to get the player ID's loaded in. Technically ID doesn't have to match order in list, but it should
		Maps will be loaded second. Maps must be loaded in model, with Hexes getting passed to GameBoard and the rest distributed
		all others (such as chatLog) will be passed to the Peripherals.
		Only thing stored in Model will be the version Number

		* */
        return "";
    }
    
    public void initializePort(String resource, int posx, int posy, String direction, int ratio)throws Exception{
        PortType type;
        if(ratio==3)type=PortType.THREE;
        else type= EnumConverter.PortType(resource);
        Port tempPort = new Port(type);
        tempPort.initializePortBindingFromSeaHex(new HexLocation(posx,posy),EnumConverter.EdgeDirection(direction),gameBoard);
        gameBoard.getPorts().add(tempPort);
    }
    
    public void initializeRoad(int owner, int xpos, int ypos, String direction){
        Road road;
        EdgeDirection edgeDirection= EnumConverter.EdgeDirection(direction);
        HexLocation hexLocation=new HexLocation(xpos,ypos);
        VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edgeDirection);
        VertexLocation vLoc1=new VertexLocation(hexLocation,vertexDirections[0]);
        VertexLocation vLoc2=new VertexLocation(hexLocation,vertexDirections[1]);
        NodePoint node1=getNodeAt(vLoc1);
        NodePoint node2=getNodeAt(vLoc2);
        EdgeLocation edgeLocation = new EdgeLocation(hexLocation,edgeDirection);
        Player player = getPlayerByIndex(owner);
        road = new Road(player,node1,node2,edgeLocation);
        node1.addRoad(road);
        node2.addRoad(road);
        player.getOwnedRoad().add(road);
    }
    
    public void initializeColony(int owner, int xpos, int ypos, String direction, boolean settlement){
        Colony colony;
        VertexLocation tempLocation=new VertexLocation(new HexLocation(xpos,ypos), EnumConverter.VertexDirection(direction));
        NodePoint nodePoint=gameBoard.getNodePointAt(tempLocation);
        Player player= getPlayerByIndex(owner);
        if(settlement)colony=new Settlement(nodePoint,player,player.getColor());
        else colony=new City(nodePoint,player,player.getColor());
        player.getOwnedColonies().add(colony);
    }
    
    public void initializeMiscMapElements(int radius, int robberx, int robbery){//Should be everything
        peripherals.setRadius(radius);
        peripherals.moveRobber(gameBoard.getHexAt(new HexLocation(robberx,robbery)));
    }
    
    public String toString(){
        return SerializeFacade.convertModelToJSON(this);
    }
    //Only misc item to take care of is to set the version number.

}
