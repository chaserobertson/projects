package model.gameboard;

import java.io.Serializable;
import java.util.*;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.overhead.Model;
import model.peripherals.Robber;
import model.structures.Road;
import shared.definitions.*;
import shared.locations.*;

public class GameBoard implements Serializable{
	/**
	 * Description: list of hexes in the GameBoard
	 */
	private List<Hex> hexes;
	/**
	 * Description: List of NodePoints in the GameBoard
	 */
	private List<NodePoint> nodePoints;
	/**
	 * @pre none
	 * @post a blank GameBoard object with no hexes or nodePoints
	 */
	private List<Port> ports;
	public GameBoard(){
		hexes=new ArrayList<Hex>();
		nodePoints=new ArrayList<NodePoint>();
		ports=new ArrayList<>();
	}
	public GameBoard(boolean randomizedHexes,boolean randomizedChits, boolean randomizedPorts){
		hexes=new ArrayList<Hex>();
		nodePoints=new ArrayList<NodePoint>();
		ports=new ArrayList<>();
		//Basically this function converts the arrays to lists, shuffles the appropriate ones, and then shoves them into the above lists
		List<HexLocation> hexLocations=new ArrayList<>(GameBoardConstants.HEXLOCATIONBINDING.length);
		for(int i=0;i<GameBoardConstants.HEXLOCATIONBINDING.length;++i){
			hexLocations.add(new HexLocation(GameBoardConstants.HEXLOCATIONBINDING[i][0],GameBoardConstants.HEXLOCATIONBINDING[i][1]));
		}
		List<HexType> hexTypes=new ArrayList<>(GameBoardConstants.DEFAULTHEXTYPEBINDING.length);
		for(int i=0;i<GameBoardConstants.DEFAULTHEXTYPEBINDING.length;++i){
			hexTypes.add(GameBoardConstants.DEFAULTHEXTYPEBINDING[i]);
		}
		List<Integer> chitValues=new ArrayList<>(GameBoardConstants.DEFAULTHEXCHITBINDING.length);
		for(int i=0;i<GameBoardConstants.DEFAULTHEXCHITBINDING.length;++i){
			chitValues.add(GameBoardConstants.DEFAULTHEXCHITBINDING[i]);
		}
		List<HexLocation> portLocations=new ArrayList<>(GameBoardConstants.DEFAULTPORTHEXPOSBINDING.length);
		for(int i=0;i<GameBoardConstants.DEFAULTPORTHEXPOSBINDING.length;++i){
			portLocations.add(new HexLocation(GameBoardConstants.DEFAULTPORTHEXPOSBINDING[i][0],GameBoardConstants.DEFAULTPORTHEXPOSBINDING[i][1]));
		}
		List<PortType> portTypes=new ArrayList<>(GameBoardConstants.DEFAULTPORTTYPEBINDING.length);
		for(int i=0;i<GameBoardConstants.DEFAULTPORTTYPEBINDING.length;++i){
			portTypes.add(GameBoardConstants.DEFAULTPORTTYPEBINDING[i]);
		}
		List<EdgeDirection> portDirections=new ArrayList<>(GameBoardConstants.DEFAULTPORTDIRECTIONBINDING.length);
		for(int i=0;i<GameBoardConstants.DEFAULTPORTDIRECTIONBINDING.length;++i){
			portDirections.add(GameBoardConstants.DEFAULTPORTDIRECTIONBINDING[i]);
		}
		if(randomizedHexes)shuffleList(hexTypes);
		if(randomizedChits)shuffleList(chitValues);
		if(randomizedPorts)shuffleList(portTypes);
		List<Hex> hexList = new ArrayList<>(hexLocations.size());
		for(int i=0;i<hexLocations.size();++i){
			hexList.add(new Hex(hexTypes.get(i),hexLocations.get(i),chitValues.get(i)));
		}
		try{
			initializeFromHexes(hexList);
		}catch (Exception e){
			Debugger.LogMessage("GameBoard:Constructor with 3 inputs: Error initializing hexes");
		}
		ports.clear();
		for(int i=0;i<portLocations.size();++i){
			Port temp = new Port(portTypes.get(i));
			try{temp.initializePortBindingFromSeaHex(portLocations.get(i),portDirections.get(i),this);}
			catch (Exception e) {Debugger.LogMessage("GameBoard:Constructor with 3 inputs: Error binding ports");}
			ports.add(temp);
		}
		ensure7OnDesert();
		//That should be everything.
	}
	/**
	 * @param hexes
	 * 		-the list of hexes which form the GameBoard
	 * @param nodePoints
	 * 		-the list of nodepoints which form the GameBoard
	 * @pre hexes and Nodepoints are lists of valid hexes and nodePoints
	 * @post a new Gameboard which stores the given hexes and Nodepoints
	 */
	public GameBoard(List<Hex> hexes, List<NodePoint> nodePoints){
		this.hexes=hexes;
		this.nodePoints=nodePoints;
	}
	private void ensure7OnDesert(){
		if(hexes==null)return;
		Hex hexWithSeven=null;
		for(Hex hex:hexes){
			if(hex.getChitValue()==7){
				hexWithSeven=hex;
				break;
			}
		}
		if(hexWithSeven==null)return;
		if(hexWithSeven.getHexType()==HexType.DESERT)return;
		for(Hex hex:hexes){
			if(hex.getHexType()==HexType.DESERT){
				hexWithSeven.setChitValue(hex.getChitValue());
				hex.setChitValue(7);
			}
		}
	}
	public List<Hex> getHexes(){return hexes;}
	public void setHexes(List<Hex> hexes){this.hexes=hexes;}
	public List<NodePoint> getNodePoints(){return nodePoints;}
	public void setNodePoints(List<NodePoint> nodePoints){this.nodePoints=nodePoints;}
	public List<Port> getPorts(){return ports;}
	public void setPorts(List<Port> ports){this.ports=ports;}

	public void distributeResources(int rollResult,Model model){
		if(nodePoints==null)return;
		for(NodePoint nodePoint:nodePoints){
			if(nodePoint!=null)nodePoint.distributeResources(rollResult,model);
		}
	}
	/**
	 * @pre none
	 * @post GameBoard is cleared of all previously stored data
	 * @post GameBoard has been filled with new Hexes and NodePoints which are interconnected in standard Catan Format
	 * @post all Hexes are Desert with chit value 7. The robber is not placed
	 */
	public void initializeGameBoardBlank(){
		hexes=new ArrayList<Hex>();
		nodePoints=new ArrayList<NodePoint>();
		ports=new ArrayList<>();
		for(int i=0;i<=GameBoardConstants.HEXCOUNT;++i){
			//Create all hexes based on hexLocationBinding. Just using the Hex(HexLocation)
			Hex temp = new Hex(new HexLocation(GameBoardConstants.HEXLOCATIONBINDING[i][0],GameBoardConstants.HEXLOCATIONBINDING[i][1]));
			temp.setID(i);
			hexes.add(temp);
		}
		for(int i=0;i<=GameBoardConstants.NODECOUNT;++i){
			//create all vertices and bind them to hexes.
			NodePoint temp = new NodePoint();
			for(int j=0;j<3;++j){
				int pos = GameBoardConstants.VERTEXALLHEXDIRECTIONBINDING[i][j];
				if(pos==-1)break;
				temp.addVertexLocation(new VertexLocation(hexes.get(GameBoardConstants.VERTEXHEXBINDING[i][j]).getHexLocation(), EnumConverter.VertexDirection(GameBoardConstants.VERTEXALLHEXDIRECTIONBINDING[i][j])));
				int pos2=GameBoardConstants.VERTEXHEXBINDING[i][j];
				temp.addHex(hexes.get(pos2));
			}
			temp.setID(i);
			nodePoints.add(temp);
		}
		for(int i=0;i<=GameBoardConstants.NODECOUNT;++i){
			//link all nodes to each other
			NodePoint temp=nodePoints.get(i);
			for(int j=0;j<3;++j){
				int pos=GameBoardConstants.VERTEXVERTEXBINDING[i][j];
				if(pos==-1)break;
				NodePoint tempNeighbor=nodePoints.get(pos);
				temp.addNeighbor(tempNeighbor);
			}
		}
		for(int i=0;i<=GameBoardConstants.PORTCOUNT;++i){
			Port tempPort=new Port(GameBoardConstants.PORTTYPEBINDING[i]);
			NodePoint tempNode=nodePoints.get(GameBoardConstants.PORTVERTEXBINDING[i][0]);
			tempPort.addNode(tempNode);
			tempNode.setPort(tempPort);
			tempNode=nodePoints.get(GameBoardConstants.PORTVERTEXBINDING[i][1]);
			tempPort.addNode(nodePoints.get(GameBoardConstants.PORTVERTEXBINDING[i][1]));
			tempNode.setPort(tempPort);
			ports.add(tempPort);
		}
	}
	public void initializeDefaultGameBoard(){
		hexes=new ArrayList<Hex>();
		nodePoints=new ArrayList<NodePoint>();
		ports=new ArrayList<>();
		for(int i=0;i<GameBoardConstants.HEXCOUNT;++i){
			//Create all hexes based on hexLocationBinding. Just using the Hex(HexLocation)
			Hex temp = new Hex(new HexLocation(GameBoardConstants.HEXLOCATIONBINDING[i][0],GameBoardConstants.HEXLOCATIONBINDING[i][1]));
			temp.setHexType(GameBoardConstants.DEFAULTHEXTYPEBINDING[i]);
			temp.setID(i);
			temp.setChitValue(GameBoardConstants.DEFAULTHEXCHITBINDING[i]);
			hexes.add(temp);
		}
		try {
			initializeFromHexes(hexes);
		}catch(Exception e){
			Debugger.LogMessage("GameBoard:InitializeDefaultGameBoard:initialize from hexes failed");
		}
		for(int i=0;i<GameBoardConstants.PORTCOUNT;++i){
			Port tempPort=new Port(GameBoardConstants.DEFAULTPORTTYPEBINDING[i]);
			try {
				tempPort.initializePortBindingFromSeaHex(new HexLocation(GameBoardConstants.DEFAULTPORTHEXPOSBINDING[i][0], GameBoardConstants.DEFAULTPORTHEXPOSBINDING[i][1]), GameBoardConstants.DEFAULTPORTDIRECTIONBINDING[i], this);
			}catch (Exception e){
				Debugger.LogMessage("GameBoard:InitializeDefaultGameBoard:Port initializing failed");
			}
			ports.add(tempPort);
		}
	}
	/**
	 * @param robber
	 * 		-the robber to be placed
	 * @pre Gameboard is initialized. robber is not null
	 * @post robber is placed on desert if available
	 * @return true if robber was placed, false if no desert was found
	 */
	public boolean placeRobberOnDesert(Robber robber){//Moves to the first desert hex found, counting from top
		for(int i=0;i<hexes.size();++i){
			if(hexes.get(i).getHexType()==HexType.DESERT){
				robber.moveTo(hexes.get(i));
				return true;
			}
		}
		return false;
	}
	/**
	 * @param location
	 * 		-location of the Nodepoint desired
	 * @pre Board is initialized
	 * @post Nodepoint at given location is returned
	 * @return the nodepoint at the given VertexLocation or null if it could not be found
	 */
	public NodePoint getNodePointAt(VertexLocation location){
		for(int i=0;i<nodePoints.size();++i){
			NodePoint temp=nodePoints.get(i);
			if(temp.getNormalizedVertexLocation().equals(location.getNormalizedLocation()))return temp;
		}
		return null;
	}
	public Hex getHexAt(HexLocation hexLocation){
		for(int i=0;i<hexes.size();++i){
			Hex temp = hexes.get(i);
			if(temp.getHexLocation().equals(hexLocation))return temp;
		}
		return null;
	}
	public Port getPortAt(HexLocation hexLocation){
		for(int i=0;i<ports.size();++i){
			Port temp=ports.get(i);
			if(temp.getSeaHex().equals(hexLocation))return temp;
		}
		return null;
	}
	public Road getRoadAt(EdgeLocation edge){
		VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edge.getDir());
		VertexLocation vLoc1=new VertexLocation(edge.getHexLoc(),vertexDirections[0]);
		VertexLocation vLoc2=new VertexLocation(edge.getHexLoc(),vertexDirections[1]);
		NodePoint node1=getNodePointAt(vLoc1);
		NodePoint node2=getNodePointAt(vLoc2);
		if(node1==null||node2==null)return null;
		return node1.getRoadAt(edge);
	}
	/**
	 * @pre Board is initialized
	 * @post A String in JSON format containing the hexes and Nodepoints associated with the Board
	 * @return a String in JSON format
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public Hex generateHexFromData(int posx,int posy, String resource, int chit){
		return new Hex(EnumConverter.HexType(resource),new HexLocation(posx,posy),chit);
	}
	public void initializeFromHexes(List<Hex> hexList)throws Exception{
		//Each hex in this list MUST already be individually constructed with Type, Chit, and Position. Especially position
		//Each hex needs to construct 6 node points. It needs to link those Node Points to its 2 neighbors it knows about
		//Nodes will be added to a map which will contain the VertexLocation normalized. If it already exists, don't add it.
		//As a notable sideeffect, each node will have the Normalized VertexPosition located in its first slot, even if it wouldnt normally have it
		hexes=hexList;
		Map<VertexLocation,NodePoint> nodePointMap=new HashMap<>();

		for(int i=0;i<hexList.size();++i){
			Hex tempHex = hexList.get(i);
			HexLocation hexLocation=tempHex.getHexLocation();
			List<VertexLocation> normalizedSurroundings=new LinkedList<>();
			//Temp is only lacking nodePoints at this point
			for(int j=0;j<6;++j){
				VertexLocation tempLocation = new VertexLocation(hexLocation,EnumConverter.VertexDirection(j));
				NodePoint tempNode;
				VertexLocation normalized = tempLocation.getNormalizedLocation();
				if(nodePointMap.containsKey(normalized)){
					tempNode=nodePointMap.get(normalized);
				}
				else{
					tempNode=new NodePoint();
					if(!tempNode.addVertexLocation(normalized)){
						Debugger.LogMessage("GameBoard:InitializeFromHexes:issues adding vertexLocation to node");
					}
					nodePointMap.put(normalized,tempNode);
				}
				normalizedSurroundings.add(normalized);
				try {
					tempHex.addNodePointToHexAndLink(tempNode, j);
				}catch(Exception e){
					//System.out.print("Gameboard:initlialization:addNodePoint toHex and LInk\n");
				}
				if(!tempNode.addVertexLocation(tempLocation)){
					Debugger.LogMessage("GameBoard:InitializeFromHexes:issues adding vertexLocation to node");
				}
			}
		}
		nodePoints=new ArrayList<NodePoint>(nodePointMap.values());
		for(int i=0;i<nodePoints.size();++i){
			linkNodePointToNeighbors(nodePoints.get(i));
		}
		//System.out.print("GameBoard:initializeFromHexes:NodeCount="+nodePoints.size()+"\n");
	}
	public boolean equals(GameBoard gameBoard){
		if(gameBoard==null)return false;
		if(hexes.size()!=gameBoard.getHexes().size())return false;
		if(nodePoints.size()!=gameBoard.getNodePoints().size())return false;
		if(ports.size()!=gameBoard.getPorts().size()){return false;}
		for(int i=0;i<hexes.size();++i){
			Hex hex=hexes.get(i);
			if(!hex.equals(gameBoard.getHexAt(hex.getHexLocation())))return false;
		}
		for(int i=0;i<nodePoints.size();++i){
			NodePoint node=nodePoints.get(i);
			if(!node.equals(gameBoard.getNodePointAt(node.getNormalizedVertexLocation())))return false;
		}
		for(int i=0;i<ports.size();++i){
			Port port=ports.get(i);
			if(!port.equals(gameBoard.getPortAt(port.getSeaHex())))return false;
		}
		return true;
	}
	private void linkNodePointToNeighbors(NodePoint nodePoint){
		VertexLocation location = nodePoint.getNormalizedVertexLocation();
		int xpos=location.getHexLoc().getX();
		int ypos=location.getHexLoc().getY();
		switch(location.getDir()){
			case NorthEast:
				NodePoint upperRight=getNodePointAt(new VertexLocation(new HexLocation(xpos+1,ypos-1),VertexDirection.NorthWest));
				if(upperRight!=null)nodePoint.addNeighbor(upperRight);
				NodePoint left=getNodePointAt(new VertexLocation(location.getHexLoc(),VertexDirection.NorthWest));
				if(left!=null)nodePoint.addNeighbor(left);
				NodePoint lowerRight=getNodePointAt(new VertexLocation(new HexLocation(xpos+1,ypos),VertexDirection.NorthWest));
				if(lowerRight!=null)nodePoint.addNeighbor(lowerRight);
				break;
			case NorthWest:
				NodePoint upperLeft=getNodePointAt(new VertexLocation(new HexLocation(xpos-1,ypos),VertexDirection.NorthEast.NorthEast));
				if(upperLeft!=null)nodePoint.addNeighbor(upperLeft);
				NodePoint right=getNodePointAt(new VertexLocation(location.getHexLoc(),VertexDirection.NorthEast));
				if(right!=null)nodePoint.addNeighbor(right);
				NodePoint lowerLeft=getNodePointAt(new VertexLocation(new HexLocation(xpos-1,ypos+1),VertexDirection.NorthEast));
				if(lowerLeft!=null)nodePoint.addNeighbor(lowerLeft);
				break;
			default:
				Debugger.LogMessage("GameBoard:LinkNodePointToNeighbors:Didn't receive normalized nodepoint location");
				return;
		}
		if(nodePoint.getNeighbors().size()<2)Debugger.LogMessage("GameBoard:LinkNodeToNeighbors:less than 2 neighbors");
		return;
	}

	private <T> void shuffleList(List<T> list){
		Collections.shuffle(list);
	}


}
