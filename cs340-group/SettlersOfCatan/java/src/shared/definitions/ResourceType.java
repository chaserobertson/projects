package shared.definitions;

import java.io.Serializable;

public enum ResourceType implements Serializable
{
	WOOD, BRICK, SHEEP, WHEAT, ORE;

	@Override
	public String toString() {
		switch (this) {
			case WOOD:
				return "wood";
			case BRICK:
				return "brick";
			case SHEEP:
				return "sheep";
			case WHEAT:
				return "wheat";
			case ORE:
				return "ore";
			default:
				return "";
		}
	}
	public static int numberOfTypes(){
		return 5;
	}
}

