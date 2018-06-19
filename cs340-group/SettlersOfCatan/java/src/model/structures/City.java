package model.structures;
import model.player.Player;
import model.gameboard.NodePoint;
import shared.definitions.*;
public class City extends Colony {
	/**
	 * @param nodePoint
	 * 		-nodepoint associated with the city
	 * @param player
	 * 		-player associated with the city
	 * @param color
	 * 		-color of the city
	 * @pre nodePoint, player, and color are not null
	 * @post a new city constructed at the nodepoint indicated with the associated player and color
	 */
	public City(NodePoint nodePoint, Player player, CatanColor color){
		super(nodePoint,player,color);
		multiplier=2;
		pieceType=PieceType.CITY;
	}
}
