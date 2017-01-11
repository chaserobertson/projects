package model.structures;
import java.io.Serializable;
import java.util.*;

import debugger.Debugger;
import model.definitions.EnumConverter;
import model.definitions.ResourceHand;
import model.player.Player;
import model.definitions.ResourceTypeQuantityPair;
import model.gameboard.Hex;
import model.gameboard.NodePoint;
import shared.definitions.*;

public class Colony implements Serializable{
	/**
	 * Description: nodepoint on which Colony is
	 */
	private NodePoint nodePoint;
	/**
	 * Description: player who owns Colony
	 */
	private Player owningPlayer;
	/**
	 * Description: Color of Colony
	 */
	private CatanColor color;
	/**
	 * Description: PieceType of Colony
	 */
	protected PieceType pieceType;
	/**
	 * Description: multiplier of Colony's gains
	 */
	protected int multiplier=1;
	public Colony(NodePoint nodePoint, Player player, CatanColor color){
		this.nodePoint=nodePoint;
		nodePoint.setColony(this);
		owningPlayer = player;
		this.color=color;
		pieceType=PieceType.SETTLEMENT;
		multiplier=1;
	}
	public NodePoint getNodePoint(){return nodePoint;}
	public void setNodePoint(NodePoint nodePoint){this.nodePoint=nodePoint;}
	public Player getOwningPlayer(){return owningPlayer;}
	public void setOwningPlayer(Player owningPlayer){this.owningPlayer=owningPlayer;}
	public CatanColor getColor(){return color;}
	public void setColor(CatanColor color){this.color=color;}
	public PieceType getPieceType(){return pieceType;}
	public void setPieceType(PieceType pieceType){this.pieceType=pieceType;}
	public int getMultiplier(){return multiplier;}
	public void setMultiplier(int multiplier){this.multiplier=multiplier;}
	/**
	 * @pre none
	 * @post List created of resources generated from a typical turn gather
	 * @return list of Resources returned by collection from this urban structure
	 */
	public List<ResourceTypeQuantityPair> getResources(){
		List<ResourceTypeQuantityPair> result=new ArrayList<>();
		if(nodePoint==null){
			Debugger.LogMessage("Colony:GetResources:nodepoint null");
			return result;
		}
		List<Hex> adjacent = nodePoint.getHexes();
		if(adjacent==null){
			Debugger.LogMessage("Colony:GetResources:Hexes null");
			return result;
		}
		if(adjacent.size()<=0){
			Debugger.LogMessage("Colony:GetResources:hexes Adjacet <=0");
		}
		for(int i=0;i<adjacent.size();++i){
			if(adjacent.get(i)!=null) {
				ResourceType resourceRead = adjacent.get(i).getResourceType();
				if (resourceRead != null) result.add(new ResourceTypeQuantityPair(resourceRead, multiplier));
			}
		}
		return result;
	}
	/**
	 * @pre none
	 * @post A JSON formatted STring containing Colony's data
	 * @return A JSON formatted String
	 */
	public String serialize(){
		//TODO:implement
		return "";
	}
	public boolean equals(Colony colony) {
		if (colony == null) return false;
		if (nodePoint != null) {
			if (!colony.getNodePoint().getNormalizedVertexLocation().equals(nodePoint.getNormalizedVertexLocation()))
				return false;
		} else if (colony.getNodePoint() != null) return false;
		if (owningPlayer != null) {
			if (colony.getOwningPlayer().getIndex() != owningPlayer.getIndex()) return false;
		} else if (colony.getOwningPlayer() != null) return false;
		if (color != null) {
			if (!colony.getColor().equals(color)) return false;
		} else if (colony.getColor() != null) return false;
		if (pieceType != null) {
			if (!colony.getPieceType().equals(pieceType)) return false;
		} else if (colony.getPieceType() != null) return false;
		if (multiplier != colony.getMultiplier()) return false;
		return true;
	}
}
