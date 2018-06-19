package model.structures;
import model.player.Player;
import model.gameboard.NodePoint;
import shared.locations.*;

import javax.xml.soap.Node;
import java.io.Serializable;

public class Road implements Serializable {
	/**
	 * Description: Player in possession of the road
	 */
	private Player owningPlayer;
	/**
	 * Description: First endpoint of road
	 */
	private NodePoint node1;
	/**
	 * Description: second endpoint of road
	 */
	private NodePoint node2;
	/**
	 * Description: location on board
	 */
	private EdgeLocation location;
	private boolean touched=false;
	/**
	 * @param owningPlayer
	 * 		-player who owns the road
	 * @param node1
	 * 		-one end point of the road
	 * @param node2
	 * 		-the other end point of the road
	 * @param location
	 * 		-the EdgeLocation of the road
	 * @pre all parameters are non null and valid
	 * @post road is initialized with the parameters
	 */
	public Road(Player owningPlayer,NodePoint node1, NodePoint node2, EdgeLocation location){
		this.owningPlayer=owningPlayer;
		this.node1=node1;
		this.node2=node2;
		this.location=location;
	}
	public NodePoint getNode1(){return node1;}
	public void setNode1(NodePoint node){node1=node;}
	public NodePoint getNode2(){return node2;}
	public void setNode2(NodePoint node){node2=node;}
	public EdgeLocation getLocation(){return location;}
	public void setLocation(EdgeLocation location){this.location=location;}
	public Player getOwningPlayer(){return owningPlayer;}
	public void setOwningPlayer(Player player){owningPlayer=player;}
	public int maxLengthFromHere(NodePoint predecessor){
		touched=true;
		int result=1;
		int max=0;
		if(predecessor!=node1&&(node1.getColony()==null||node1.getColony().getOwningPlayer()==owningPlayer)) {
			for (int i = 0; i < node1.getRoads().size(); ++i) {
				Road road = node1.getRoads().get(i);
				int temp = 0;
				if (!road.touched && road.getOwningPlayer().getIndex() == owningPlayer.getIndex()) {
					temp = road.maxLengthFromHere(node1);
					if (temp > max) max = temp;
				}
			}
		}
		if(predecessor!=node2&&(node2.getColony()==null||node2.getColony().getOwningPlayer()==owningPlayer)) {
			for (int i = 0; i < node2.getRoads().size(); ++i) {
				Road road = node2.getRoads().get(i);
				int temp = 0;
				if (!road.touched && road.getOwningPlayer().getIndex() == owningPlayer.getIndex()) {
					temp = road.maxLengthFromHere(node2);
					if (temp > max) max = temp;
				}
			}
		}
		result=result+max;
		touched=false;
		return result;
	}
	/**
	 * @pre none
	 * @post JSON formatted string containing the relevant information of Road
	 * @return String in JSON format
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(Road road){
		if(road==null)return false;
		if(!node1.getNormalizedVertexLocation().equals(road.getNode1().getNormalizedVertexLocation())){
			if(!(node2.getNormalizedVertexLocation().equals(road.getNode1().getNormalizedVertexLocation())&&
			node1.getNormalizedVertexLocation().equals(road.getNode2().getNormalizedVertexLocation())))return false;
		}
		if(owningPlayer!=null){
			if(road.getOwningPlayer()==null)return false;
			if(owningPlayer.getIndex()!=road.getOwningPlayer().getIndex())return false;
		}
		else if(road.owningPlayer!=null)return false;
		if(!location.equals(road.getLocation()))return false;
		return true;
	}
}
