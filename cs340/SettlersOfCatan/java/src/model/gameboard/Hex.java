package model.gameboard;
import model.definitions.EnumConverter;
import model.peripherals.Robber;
import shared.definitions.*;
import shared.locations.*;

import java.io.Serializable;

public class Hex implements Serializable {
	/**
	 * Description: location of the hex
	 */
	private HexLocation hexLocation;
	/**
	 * Description: type of the hex
	 */
	private HexType type;
	/**
	 * Description: list of NodePoints associated with the hex
	 */
	private NodePoint[] nodePoints;
	/**
	 * Description: chitvalue of the hex
	 */
	private int chitValue;
	/**
	 * Description: Number of points on a traditional hexagram
	 */
	private final int NUMBEROFPOINTS=6;
	/**
	 * Description: pointer to the robber if valid
	 */
	private Robber robber;
	/**
	 * Description: id of the hex
	 */
	private int id;//Probably not going to be used.
	/**
	 * @param type
	 * 		-the HexType associated with the Hex
	 * @param hexLocation
	 * 		-the HexLocation of the hex
	 * @pre type and hexLocation are valid values
	 * @post a new Hex with associated type and location, chit value 7 and ID -1
	 */
	public Hex(HexType type, HexLocation hexLocation){
		this.type=type;
		this.hexLocation=hexLocation;
		nodePoints=new NodePoint[NUMBEROFPOINTS];
		robber=null;
		chitValue=7;
		id=-1;
	}
	/**
	 * @param type
	 * 		-the HexType associated with the Hex
	 * @param hexLocation
	 * 		-the HexLocation of the hex
	 * @param chitValue
	 * 		-the chitvalue of the hex
	 * @pre type and hexLocation are valid values. Chitvalue is between 2 and 12
	 * @post a new Hex with associated type and location and chit value and ID -1
	 */
	public Hex(HexType type,HexLocation hexLocation,int chitValue){//THIS IS WHAT THE JSON READS US!!!!
		this(type,hexLocation);
		this.chitValue=chitValue;
	}
	/**
	 * @param type
	 * 		-the HexType associated with the Hex
	 * @param hexLocation
	 * 		-the HexLocation of the hex
	 * @param chitValue
	 * 		-the chitvalue of the hex
	 * @param nodePoints
	 * 		-the nodePoints associated with this hex
	 * @pre type and hexLocation are valid values
	 * @post a new Hex with associated type, location, chit value, and ID -1
	 */
	public Hex(HexType type, HexLocation hexLocation, int chitValue, NodePoint[] nodePoints){
		this(type,hexLocation,chitValue);
		this.nodePoints=nodePoints;
	}
	/**
	 * @param hexLocation
	 * 		-the HexLocation of the hex
	 * @pre hexLocation is a valid value
	 * @post a new Hex with associated hexLocation, type Desert, Chitvalue 7, id -1
	 */
	public Hex(HexLocation hexLocation){
		this(HexType.DESERT,hexLocation);
	}

	public HexType getHexType(){return type;}
	public void setHexType(HexType type){this.type=type;}
	public HexLocation getHexLocation(){return hexLocation;}
	public void setHexLocation(HexLocation hexLocation){this.hexLocation=hexLocation;}
	public int getChitValue(){return chitValue;}
	public void setChitValue(int chitValue){this.chitValue=chitValue;}
	public NodePoint[] getNodePoints(){return nodePoints;}
	public void setNodePoints(NodePoint[] nodePoints){this.nodePoints=nodePoints;}
	public NodePoint getNodePoint(int index){
		if(index>=NUMBEROFPOINTS||index<0)return null;
		return nodePoints[index];
	}
	public NodePoint getNodePoint(VertexDirection direction){
		return nodePoints[EnumConverter.VertexDirection(direction)];
	}
	public Robber getRobber(){return robber;}
	public void setRobber(Robber robber){this.robber=robber;}
	/**
	 * @pre the Hex is initialized
	 * @post returns Resource Type
	 * @return returns the Resource type garnered from the Hex, returning null if its desert or there is a robber
	 */
	public ResourceType getResourceType(){
		if(robber!=null)return null;
		switch(type){
			case WOOD: return ResourceType.WOOD;
			case BRICK: return ResourceType.BRICK;
			case SHEEP: return ResourceType.SHEEP;
			case WHEAT: return ResourceType.WHEAT;
			case ORE: return ResourceType.ORE;
			default: return null;
		}
	}
	public int getID(){return id;}
	public void setID(int id){this.id=id;}
	/**
	 * @pre Hex is initialized
	 * @post A JSON string containing the Hex's information
	 * @return a JSON formatted String
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(Hex hex){
		if(hex==null)return false;
		if(!hexLocation.equals(hex.getHexLocation()))return false;
		if(!type.equals(hex.getHexType())){return false;}
		if(chitValue!=hex.getChitValue())return false;
		if(robber!=null){
			if(hex.getRobber()==null)return false;
		}
		else if(hex.getRobber()!=null)return false;

		for(int i=0;i<nodePoints.length;++i){
			if(nodePoints[i]!=null){
				if(hex.getNodePoint(i)==null)return false;
				boolean found=false;
				for(int j=0;j<nodePoints.length;++j){
					if(nodePoints[i].getNormalizedVertexLocation().equals(hex.getNodePoint(j).getNormalizedVertexLocation())){
						found=true;
						break;
					}
				}
				if(!found)return false;
			}
			else if(hex.getNodePoint(i)!=null){
				return false;
			}
		}
		return true;
	}

	public void addNodePointToHexAndLink(NodePoint nodePoint,int i)throws Exception{
		if(i>NUMBEROFPOINTS){
			System.out.print("Hex:AddnodePointTOHexAndLinkException Out of Bounds\n");
			throw new ArrayIndexOutOfBoundsException();
		}
		nodePoints[i]=nodePoint;
		nodePoint.addHex(this);
	}
	public String toString(){
		StringBuilder result=new StringBuilder();
		result.append("Hex: Location=");
		result.append(hexLocation.toString());
		result.append(" Type=");
		result.append(type.toString());
		result.append(" Chitvalue=");
		result.append(chitValue);
		result.append("\n");
		return result.toString();
	}
}
