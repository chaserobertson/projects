package model.gameboard;
import java.io.Serializable;
import java.util.*;

import model.definitions.EnumConverter;
import shared.definitions.*;
import shared.locations.*;

public class Port implements Serializable {
	/**
	 * Description: type of the port
	 */
	private PortType type;
	private List<NodePoint> nodePoints;
	private EdgeDirection edgeDirection;
	private HexLocation seaHex;
	/**
	 * @param type
	 * 		-the type of the port created
	 * @pre none
	 * @post Port is initialized with type specified
	 */
	public Port(PortType type){
		this.type=type;
		nodePoints=new ArrayList<NodePoint>(2);}
	public PortType getType(){return type;}
	public void setType(PortType type){this.type=type;}
	public List<NodePoint> getNodePoints(){
		return nodePoints;
	}
	/**
	 * @pre nodePoitns is a valid list
	 * @post node is added to NodePoints
	 */
	public void addNode(NodePoint node){
		nodePoints.add(node);
	}
	public EdgeDirection getEdgeDirection(){return edgeDirection;}
	public void setEdgeDirection(EdgeDirection edgeDirection){this.edgeDirection=edgeDirection;}
	public HexLocation getSeaHex(){return seaHex;}
	public void setSeaHex(HexLocation seaHex){this.seaHex=seaHex;}
	public void initializePortBindingFromSeaHex(HexLocation hexLocation, EdgeDirection edgeDirection, GameBoard gameBoard)throws Exception{//ties the port in based on position
		//EdgeDirection might need to be reversed, uncertain how its set. This is assumings its from the sea's perspective
		VertexDirection[] vertexDirections = EnumConverter.EdgeDirectionToVertexDirection(edgeDirection);
		VertexLocation location1=(new VertexLocation(hexLocation,vertexDirections[0]));
		VertexLocation location2=(new VertexLocation(hexLocation,vertexDirections[1]));
		NodePoint node1=gameBoard.getNodePointAt(location1);
		NodePoint node2=gameBoard.getNodePointAt(location2);

		if(node1==null){
			System.out.print("Port:InitializePortBindingFromSeaHex:node1 is null = "+location1.toString()+"\n");
			throw new Exception();
		}
		if(node2==null){
			System.out.print("Port:InitializePortBindingFromSeaHex:node1 is null = "+location2.toString()+"\n");
			throw new Exception();
		}
		nodePoints.add(node1);
		nodePoints.add(node2);
		node1.setPort(this);
		node2.setPort(this);
		seaHex=hexLocation;
		this.edgeDirection=edgeDirection;
	}
	/**
	 * @pre none
	 * @post Json String formatted to contain all the information neccesary to construct the port
	 * @return JSON formatted String
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(Port port){
		if(port==null)return false;
		if(!type.equals(port.getType()))return false;
		if(!seaHex.equals(port.getSeaHex()))return false;
		if(!edgeDirection.equals(port.getEdgeDirection()))return false;
		if(nodePoints.size()!=port.getNodePoints().size())return false;
		for(int i=0;i<nodePoints.size();++i){
			boolean found=false;
			for(int j=0;j<port.getNodePoints().size();++j){
				if(port.getNodePoints().get(j).getNormalizedVertexLocation().equals(nodePoints.get(i).getNormalizedVertexLocation()))found=true;
			}
			if(!found)return false;
		}
		return true;
	}
}
