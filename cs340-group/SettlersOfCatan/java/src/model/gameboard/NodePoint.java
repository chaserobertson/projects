package model.gameboard;
import java.io.Serializable;
import java.util.*;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.ResourceTypeQuantityPair;
import model.overhead.Model;
import model.player.Player;
import model.structures.Colony;
import model.structures.Road;
import shared.definitions.PieceType;
import shared.locations.*;

public class NodePoint implements Serializable{
	/**
	 * Description: List of Potential VertexLocation identifiers
	 */
	//private List<VertexLocation> vertexLocations;
    private VertexLocation normalizedLocation;
	/**
	 * Description: list of Neighboring hexes
	 */
	private List<Hex> hexes;
	/**
	 * Description: Colony on the nodepoin, may be null
	 */
	private Colony colony;
	/**
	 * Description: all roads with endpoints on this nodepoint
	 */
	private List<Road> roads;
	/**
	 * Description: port, if any, associated with this nodepoint
	 */
	private Port port;
	/**
	 * Description: all Nodepoints adjoining this nodepoint
	 */
	private List<NodePoint> neighbors;
	/**
	 * Description: id of this node
	 */
	private int id;

	/**
	 * @pre none
	 * @post NodePoint is initialized with empty lists of Hexes, neighbors, roads, vertexLocations and with port and Colony null and ID -1
	 */
	public NodePoint(){
		hexes=new ArrayList<Hex>();
		neighbors=new ArrayList<NodePoint>();
		roads=new ArrayList<Road>();
		colony =null;
		port=null;
		//vertexLocations=new ArrayList<VertexLocation>();
		id=-1;
	}

	public List<Hex> getHexes(){return hexes;}
	public void setHexes(List<Hex> hexes){this.hexes=hexes;}
	/**
	 * @param hex
	 * 		-the hex to be added in association with the NodePoint
	 * @pre hex is a valid hex, hexes is a valid list in NodePoint
	 * @post hex has been added to NodePoint
	 * @return true if operation was succesful. False if nodePoint already has 3 or more hexes
	 */
	public boolean addHex(Hex hex){
		//if(hexes.size()>=3) return false;
		hexes.add(hex);
		return true;
	}
	public Colony getColony(){return colony;}
	public void setColony(Colony colony){this.colony = colony;}
	public List<Road> getRoads(){return roads;}
	public void setRoads(List<Road> roads){this.roads=roads;}
	/**
	 * @param road
	 * 		-road the road to be added in assocation with NodePoint
	 * @pre road is a valid road, roads is a valid list in NodePoint
	 * @post road has been added to NodePoint
	 * @return true if operation was successful, false if nodePoint already has 3 or more roads
	 */
	public boolean addRoad(Road road){
		if(roads.size()>=3)return false;
		roads.add(road);
		return true;
	}
	public Port getPort(){return this.port;}
	public void setPort(Port port){this.port=port;}
	public List<NodePoint> getNeighbors(){return neighbors;}
	public void setNeighbors(List <NodePoint> neighbors){this.neighbors=neighbors;}
	/**
	 * @param neighbor
	 * 		-neighbor the NodePoint to be added in assocation with NodePoint
	 * @pre Neighbor is a valid NodePoint, neighbors is a valid list
	 * @post neighbor has been added to NodePoint
	 * @return true if the operation was succesful, false if it already has 3 or more neighbors
	 */
	public boolean addNeighbor(NodePoint neighbor){
		if(neighbors.size()>=3)return false;
		neighbors.add(neighbor);
		return true;
	}
	public int getID(){return id;}
	public void setID(int i){this.id=i;}
	/*public VertexLocation getVertexLocation(int i){
		if(i>=vertexLocations.size()||i<0)return null;
		return vertexLocations.get(i);
	}*/
	public VertexLocation getNormalizedVertexLocation(){
		return normalizedLocation;
	}
	/**
	 * @param i
	 * 		-the index of the VertexLocation you are overriding
	 * @param vertexLocation the vertexLocation to set
	 * @pre index i is within bounds, vertexLocation is valid
	 * @post index i in vertexLocations is set to value vertexLocation
	 */
	/*public void setVertexLocation(int i,VertexLocation vertexLocation)throws ArrayIndexOutOfBoundsException{
		if(i>=vertexLocations.size()||i<0)throw new ArrayIndexOutOfBoundsException();
		vertexLocations.set(i, vertexLocation);
	}*/
	/*public List<VertexLocation> getVertexLocations(){
		return vertexLocations;
	}*/
	/*public void setVertexLocations(List<VertexLocation> vertexLocations){
		this.vertexLocations=vertexLocations;
	}*/
	/**
	 * @param vertexLocation
	 * 		-the vertexLocation to add to the list of associated vertexLocations
	 * @pre vertexLocation is a valid position, nodepoint does not have more than 3 vertexLocations
	 * @post vertexLocation is added to associated vertexLocations
	 * @return true if operation is succesful, valse if it already has at least 3 vertexLocations
	 */
	public boolean addVertexLocation(VertexLocation vertexLocation){
		if(normalizedLocation==null){
			normalizedLocation=vertexLocation.getNormalizedLocation();
			return true;
		}
		else{
			if(vertexLocation==null)return false;
			if(!(normalizedLocation.equals(vertexLocation.getNormalizedLocation())))return false;
			return true;
		}
		/*if(vertexLocations.size()>=3)return false;
		vertexLocations.add(vertexLocation);
		addVertexLocationToSet(vertexLocation.getNormalizedLocation());
		return true;*/
	}
	public Hex getHexAt(HexLocation hexLocation){
		for(int i=0;i<hexes.size();++i){
			if(hexes.get(i).getHexLocation().equals(hexLocation))return hexes.get(i);
		}
		return null;
	}
	public Road getRoadAt(EdgeLocation edgeLocation){
		for(int i=0;i<roads.size();++i){
			if(roads.get(i).getLocation().equals(edgeLocation))return roads.get(i);
		}
		return null;
	}
	public NodePoint getNeighborAt(VertexLocation vertexLocation){
		vertexLocation=vertexLocation.getNormalizedLocation();
		for(int i=0;i<neighbors.size();++i){
			if(neighbors.get(i).getNormalizedVertexLocation().equals(vertexLocation))return neighbors.get(i);
		}
		return null;
	}

	public void distributeResources(int chitValue,Model model){
		if(colony==null)return;
		for(Hex hex: hexes){
			if(hex!=null&&hex.getChitValue()==chitValue){
				if(colony.getOwningPlayer()!=null&&model.getPeripherals().getGameBank()!=null&&hex.getResourceType()!=null){
					if(model.getPeripherals().getGameBank().getResourcePile()==null){
						Debugger.LogMessage("Bank pile null");
						return;
					}
					if(colony.getOwningPlayer().getResourceAggregation()==null){
						Debugger.LogMessage("player pile null");
						return;
					}
					model.getPeripherals().getGameBank().getResourcePile().transferResourcesTo(
							colony.getOwningPlayer().getResourceAggregation(),
							new ResourceTypeQuantityPair(hex.getResourceType(),colony.getMultiplier()));
				}

			}
		}
	}
	/**
	 * @param node
	 * 		-the nodePoint with which we are checking if it shares a road
	 * @pre none
	 * @post returns whether or not it shares a road with the Node
	 * @return true if it shares a road, false if it doesnt
	 */
	public boolean sharesRoadWith(NodePoint node){
		for(int i=0;i<roads.size();++i){
			Road temp=roads.get(i);
			if(temp.getNode1()==node||temp.getNode2()==node)return true;
		}
		return false;
	}
	/**
	 * @param player
	 * 		-whether or not the NodePoint has a road of the player
	 * @pre none
	 * @post returns whether or not the nodepoint holds a road of the player
	 * @return true if the nodepoint contains a road of the player. false otherwise
	 */
	public boolean containsRoadOf(Player player){
		for(int i=0;i<roads.size();++i){
			if(roads.get(i).getOwningPlayer().getIndex()==player.getIndex())return true;
		}
		return false;
	}
	public boolean containsVertexLocation(VertexLocation vertexLocation){
		if(vertexLocation==null)return false;
		if(normalizedLocation!=null){
			if(normalizedLocation.equals(vertexLocation.getNormalizedLocation()))return true;
		}
		return false;
		/*for (VertexLocation innerLocation:vertexLocations ) {
			if(innerLocation.equals(vertexLocation))return true;
		}
		return false;*/
	}
	/*public void addVertexLocationToSet(VertexLocation vertexLocation){
		if(!containsVertexLocation(vertexLocation)){
			vertexLocations.add(vertexLocation);
		}
	}*/
	public void addNeighborToSet(NodePoint node){
		for(int i=0;i<neighbors.size();++i){
			if(neighbors.get(i)==node)return;
		}
		neighbors.add(node);
	}
	public boolean canBuildColonyHere(Player player, boolean setupRound, boolean buildingCity){
		if(colony!=null && !buildingCity) {
			return false;
		}
		if(buildingCity && colony==null) {
			return false;
		}
		if(buildingCity && (colony.getPieceType()!=PieceType.SETTLEMENT || colony.getOwningPlayer()!=player)){
			return false;
		}
		if(buildingCity)return true;//No more city checks left to perform
		if(!setupRound) {//Cities don't need to check for roads
            boolean roadConnected = false;
            for (int i = 0; i < roads.size(); ++i) {
                if (roads.get(i).getOwningPlayer() == player) {
                    roadConnected = true;
                    break;
                }
            }
            if (!roadConnected) return false;
        }
		for(int i=0;i<neighbors.size();++i){
			if(neighbors.get(i).getColony()!=null)return false;
		}
		return true;
	}
	public static EdgeLocation determineConnectingEdge(NodePoint node1, NodePoint node2){
		//Now we need to figure out WHICH hex they share...
		HexLocation hexLocation=null;
		HexLoop:
		for(Hex hex:node1.getHexes()){
			for(Hex hex2:node2.getHexes()){
				if(hex.getHexLocation().equals(hex2.getHexLocation())){
					hexLocation=hex.getHexLocation();
					break HexLoop;
				}
			}
		}
		if(hexLocation==null)return null;
		VertexDirection vd1=null;
		VertexDirection vd2=null;
		try {
			for (int i = 0; i < 6; ++i) {//Types of vertexDirection
				VertexLocation temp = new VertexLocation(hexLocation, EnumConverter.VertexDirection(i));
				if(temp.getNormalizedLocation().equals(node1.getNormalizedVertexLocation()))vd1=temp.getDir();
				if(temp.getNormalizedLocation().equals(node2.getNormalizedVertexLocation()))vd2=temp.getDir();
				if(vd1!=null&&vd2!=null)break;
			}
			if(vd1!=null&&vd2!=null){
				return new EdgeLocation(hexLocation,EnumConverter.VertexDirectionsToEdgeDirection(vd1,vd2));
			}
		}catch (Exception e){
			return null;
		}
		return null;
	}
	/**
	 * @pre none
	 * @post a JSON string is returned formatted to contain the Node's information
	 * @return A JSON string
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(NodePoint node){
		if(node==null)return false;
		if(hexes.size()!=node.getHexes().size())return false;
		if(port!=null){
			if(node.getPort()==null)return false;
			if(!port.getSeaHex().equals(node.getPort().getSeaHex()))return false;
		}
		if(roads.size()!=node.getRoads().size())return false;
		if(neighbors.size()!=node.neighbors.size())return false;
		if(roads.size()!=node.roads.size())return false;
		if(colony!=null){
			if(node.getColony()==null)return false;
			if(!colony.equals(node.getColony()))return false;
		}
		for(int i=0;i<hexes.size();++i){
			if(hexes.get(i)==null){
				boolean found=false;
				for(int j=0;j<node.getHexes().size();++j){
					if(node.getHexes().get(j)==null){
						found=true;
						break;
					}
				}
				if(!found)return false;
			}
			else{
				if(node.getHexAt(hexes.get(i).getHexLocation())==null)return false;
			}
		}
		for(int i=0;i<roads.size();++i){
			if(roads.get(i)==null){
				boolean found=false;
				for(int j=0;j<node.getRoads().size();++j){
					if(node.getRoads().get(j)==null){
						found=true;
						break;
					}
				}
				if(!found)return false;
			}
			else {
				boolean found = false;
				for (int j = 0; j < node.getRoads().size(); ++j) {
					if (!node.getRoads().get(j).getLocation().equals(roads.get(i).getLocation())) {
						found = true;
						break;
					}
				}
				if (!found) return false;
			}
		}
		for(int i=0;i<neighbors.size();++i){
			if(neighbors.get(i)==null){
				boolean found=false;
				for(int j=0;j<node.getNeighbors().size();++j){
					if(node.getNeighbors().get(j)==null)found=true;
				}
				if(!found)return false;
			}
			else{
				boolean found = false;
				for (int j = 0; j < node.getNeighbors().size(); ++j) {
					if(neighbors.get(i).getNormalizedVertexLocation().equals(node.getNeighbors().get(j).getNormalizedVertexLocation())){
						found=true;
						break;
					}
				}
				if (!found) return false;
			}
		}
		return true;
	}
}
