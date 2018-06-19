package model.structures;

import model.player.Player;
import model.gameboard.NodePoint;
import shared.definitions.CatanColor;

public class Settlement extends Colony {
	/**
	 * @param nodePoint
	 * 		-associated nodepoint
	 * @param player
	 * 		-associated player
	 * @param color
	 * 		-associated color
	 * @pre none
	 * @post settlement created at specified nodepoint, with player and color as specified
	 */
	public Settlement(NodePoint nodePoint, Player player, CatanColor color){
		super(nodePoint,player,color);
	}
}
