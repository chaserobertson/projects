package model.definitions;
import debugger.Debugger;
import model.facade.AI.AITypes;
import shared.definitions.*;
import shared.locations.*;
public final class EnumConverter {
	/**
	 * @param type ResourceType to be converted
	 * @pre the given ResourceType is not null
	 * @post a unique integer conversion of the enum ResourceType
	 */
	public static int ResourceType(ResourceType type) throws NullPointerException{
		switch(type){
		case WOOD: return 0;
		case BRICK: return 1;
		case SHEEP: return 2;
		case WHEAT: return 3;
		case ORE:return 4;
		default: throw new NullPointerException();
		}
	}
	/**
	 * @param i integer to be converted to ResourceType
	 * @pre an integer between the values 0 and 4.
	 * @post a ResourceType corresponding with the given integer
	 */
	public static ResourceType ResourceType(int i) throws IllegalArgumentException{
		switch(i){
		case 0:return ResourceType.WOOD;
		case 1:return ResourceType.BRICK;
		case 2:return ResourceType.SHEEP;
		case 3:return ResourceType.WHEAT;
		case 4:return ResourceType.ORE;
		default: throw new IllegalArgumentException();
		}
	}
	public static ResourceType ResourceType(String str) throws IllegalArgumentException{
		str=str.toLowerCase();
		switch (str){
			case "wood":return ResourceType.WOOD;
			case "brick":return ResourceType.BRICK;
			case "sheep":return ResourceType.SHEEP;
			case "wheat":return ResourceType.WHEAT;
			case "ore":return ResourceType.ORE;
			default: throw new IllegalArgumentException();
		}
	}
	/**
	 * @param direction VertexDirection to be converted to integer
	 * @pre A VertexDirection not equal to null
	 * @post an integer conversion of the VertexDirection given
	 */
	public static int VertexDirection(VertexDirection direction)throws NullPointerException{
		switch(direction){
		case West:return 0;
		case NorthWest:return 1;
		case NorthEast:return 2;
		case East:return 3;
		case SouthEast:return 4;
		case SouthWest:return 5;
		default: throw new NullPointerException();
		
		}
	}
	public static String VertexDirectionToString(VertexDirection direction){
		switch (direction){
			case West:return "west";
			case NorthWest:return "northwest";
			case NorthEast:return "northeast";
			case East:return "east";
			case SouthEast:return "southeast";
			case SouthWest:return "southwest";
			default:Debugger.LogMessage("EnumConverter:VertexDirectionToString:Received null");
				return "";
		}
	}
	public static String VertexDirectionToStringSH(VertexDirection direction){
		switch (direction){
			case West:return "w";
			case NorthWest:return "nw";
			case NorthEast:return "ne";
			case East:return "e";
			case SouthEast:return "se";
			case SouthWest:return "sw";
			default:Debugger.LogMessage("EnumConverter:VertexDirectionToStringSH:Received null");
				return "";
		}
	}
	/**
	 * @param i Integer to be converted to VertexDirection
	 * @pre the given integer is between 0-5
	 * @post a VertexDirection corresponding with the given integer
	 */
	public static VertexDirection VertexDirection(int i)throws IllegalArgumentException{
		switch(i){
		case 0:return VertexDirection.West;
		case 1:return VertexDirection.NorthWest;
		case 2:return VertexDirection.NorthEast;
		case 3:return VertexDirection.East;
		case 4:return VertexDirection.SouthEast;
		case 5:return VertexDirection.SouthWest;
		default: throw new IllegalArgumentException();
		}
	}
	public static VertexDirection VertexDirection(String str){
		str=str.toLowerCase();
		switch(str){
			case "w":
			case "west":return VertexDirection.West;
			case "nw":
			case "northwest":return VertexDirection.NorthWest;
			case "ne":
			case "northeast":return VertexDirection.NorthEast;
			case "e":
			case "east":return VertexDirection.East;
			case "se":
			case "southeast":return VertexDirection.SouthEast;
			case "sw":
			case "southwest":return VertexDirection.SouthWest;
			default:return null;
		}
		//West, NorthWest, NorthEast, East, SouthEast, SouthWest;
	}
	public static VertexDirection VertexDirectionPlusPlus(int i)throws IllegalArgumentException{
		if(i>=5)i=-1;
		++i;
		return VertexDirection(i);
	}
	public static VertexDirection VertexDirectionMinusMinus(int i)throws IllegalArgumentException{
		if(i<=0)i=6;
		--i;
		return VertexDirection(i);
	}
	/**
	 * @param edgeDirection EdgeDirection to be converted to two VertexDirection
	 * @pre edgeDirection is a valid EdgeDirection not equal to null
	 * @post two VertexDirections corresponding with the two VertexLocations connecting to the given EdgeDirection
	 */
	public static VertexDirection[] EdgeDirectionToVertexDirection(EdgeDirection edgeDirection)throws NullPointerException{
		switch (edgeDirection){
		case North: return new VertexDirection[]{VertexDirection.NorthWest,VertexDirection.NorthEast};
		case NorthEast: return new VertexDirection[]{VertexDirection.NorthEast,VertexDirection.East};
		case SouthEast:  return new VertexDirection[]{VertexDirection.East,VertexDirection.SouthEast};
		case South:  return new VertexDirection[]{VertexDirection.SouthEast,VertexDirection.SouthWest};
		case SouthWest:  return new VertexDirection[]{VertexDirection.SouthWest,VertexDirection.West};
		case NorthWest: return new VertexDirection[]{VertexDirection.West,VertexDirection.NorthWest};
		default:throw new NullPointerException();
		}
	}
	public static EdgeDirection VertexDirectionsToEdgeDirection(VertexDirection vertexDirection1, VertexDirection vertexDirection2)throws IllegalArgumentException{
		switch (vertexDirection1){
			case NorthEast:
				switch (vertexDirection2){
					case NorthWest:return EdgeDirection.North;
					case East:return EdgeDirection.NorthEast;
					default:throw new IllegalArgumentException();
				}
			case NorthWest:
				switch (vertexDirection2){
					case NorthEast:return EdgeDirection.North;
					case West:return EdgeDirection.NorthWest;
					default:throw new IllegalArgumentException();
				}
			case West:
				switch (vertexDirection2){
					case NorthWest:return EdgeDirection.NorthWest;
					case SouthWest:return EdgeDirection.SouthWest;
					default:throw new IllegalArgumentException();
				}
			case SouthWest:
				switch (vertexDirection2){
					case West:return EdgeDirection.SouthWest;
					case SouthEast:return EdgeDirection.South;
					default:throw new IllegalArgumentException();
				}
			case SouthEast:
				switch (vertexDirection2){
					case SouthWest:return EdgeDirection.South;
					case East:return EdgeDirection.SouthEast;
					default:throw new IllegalArgumentException();
				}
			case East:
				switch (vertexDirection2){
					case SouthEast:return EdgeDirection.SouthEast;
					case NorthEast:return EdgeDirection.NorthEast;
					default:throw new IllegalArgumentException();
				}
			default:throw new IllegalArgumentException();
		}
	}
	public static EdgeDirection EdgeDirection(String edgeDirection){
		edgeDirection=edgeDirection.toLowerCase();
		switch(edgeDirection){
			case "n":
			case "north":return EdgeDirection.North;
			case "ne":
			case "northeast":return EdgeDirection.NorthEast;
			case "se":
			case "southeast":return EdgeDirection.SouthEast;
			case "s":
			case "south":return EdgeDirection.South;
			case "sw":
			case "southwest":return EdgeDirection.SouthWest;
			case "nw":
			case "northwest":return EdgeDirection.NorthWest;
			default:
				Debugger.LogMessage("EnumConverter:EdgeDirection: received string:"+edgeDirection+"\n");
				return null;
		}
	}
	public static String EdgeDirectionSH(EdgeDirection edgeDirection){
		switch (edgeDirection){
			case North:return "n";
			case NorthEast:return "ne";
			case SouthEast:return "se";
			case South:return "s";
			case SouthWest:return "sw";
			case NorthWest:return "nw";
			default:Debugger.LogMessage("EnumConverter:EdgeDirectionSH:Received null edgeDirection");
				return "";
		}
	}
	public static String EdgeDirection(EdgeDirection edgeDirection){
		switch (edgeDirection){
			case North:return "north";
			case NorthEast:return "northeast";
			case SouthEast:return "southeast";
			case South:return "south";
			case SouthWest:return "southwest";
			case NorthWest:return "northwest";
			default:Debugger.LogMessage("EnumConverter:EdgeDirection:Received null edgeDirection");
				return "";
		}
	}
	public static CatanColor CatanColor(String color){
		color=color.toLowerCase();
		switch(color){
			case "red":return CatanColor.RED;
			case "orange":return CatanColor.ORANGE;
			case "yellow":return CatanColor.YELLOW;
			case "blue":return CatanColor.BLUE;
			case "green":return CatanColor.GREEN;
			case "purple":return CatanColor.PURPLE;
			case "puce":return CatanColor.PUCE;
			case "white":return CatanColor.WHITE;
			case "brown":return CatanColor.BROWN;
			default:return null;
		}
	}
	public static String CatanColor(CatanColor color){
		switch(color){
			case RED:return "red";
			case ORANGE:return "orange";
			case YELLOW:return "yellow";
			case BLUE: return "blue";
			case GREEN: return "green";
			case PURPLE:return "purple";
			case PUCE:return "puce";
			case WHITE:return "white";
			case BROWN:return "brown";
			default:return "";
		}
	}
	public static CatanColor CatanColor(int i){
		switch (i) {
			case 0:
				return CatanColor.RED;
			case 1:
				return CatanColor.ORANGE;
			case 2:
				return CatanColor.YELLOW;
			case 3:
				return CatanColor.BLUE;
			case 4:
				return CatanColor.GREEN;
			case 5:
				return CatanColor.PURPLE;
			case 6:
				return CatanColor.PUCE;
			case 7:
				return CatanColor.WHITE;
			case 8:
				return CatanColor.BROWN;
			default:
				return null;
		}
	}
	public static HexType HexType(String str){
		str=str.toLowerCase();
		switch(str){
			case "wood":return HexType.WOOD;
			case "brick":return HexType.BRICK;
			case "sheep":return HexType.SHEEP;
			case "wheat":return HexType.WHEAT;
			case "ore":return HexType.ORE;
			case "desert":return HexType.DESERT;
			case "water":return HexType.WATER;
			case "":return HexType.DESERT;
			default:return null;
		}
	}
	public static String HexType(HexType type){
		switch(type){
			case WOOD:return "wood";
			case BRICK:return "brick";
			case SHEEP:return "sheep";
			case WHEAT:return "wheat";
			case ORE:return "ore";
			case DESERT:return "desert";
			case WATER:return "water";
			default:return "";
		}
	}
	public static PortType PortType(String str){
		String local=str.toLowerCase();
		switch(str){
			case "wood":return PortType.WOOD;
			case"brick":return PortType.BRICK;
			case "sheep":return PortType.SHEEP;
			case"wheat":return PortType.WHEAT;
			case "ore":return PortType.ORE;
			case "three":return PortType.THREE;
			default:return PortType.THREE;
		}
	}
	public static String PortType(PortType type){
		switch (type){
			case WOOD:return "wood";
			case BRICK:return "brick";
			case SHEEP:return "sheep";
			case WHEAT:return "wheat";
			case ORE:return "ore";
			case THREE:return "three";
			default:return null;
		}
	}
	public static PortType PortType(ResourceType resourceType){
		switch(resourceType){
			case WOOD:return PortType.WOOD;
			case BRICK:return PortType.BRICK;
			case SHEEP:return PortType.SHEEP;
			case WHEAT:return PortType.WHEAT;
			case ORE:return PortType.ORE;
			default:return null;
		}
	}
	public static PlayerState PlayerState(String str){
		String copy = str.toLowerCase();
		switch(copy){
			case "rolling":return PlayerState.Rolling;
			case "robbing":return PlayerState.Robbing;
			case "discarding":return PlayerState.Discarding;
			case "playing":return PlayerState.Playing;
			case "firstround":return PlayerState.FirstRound;
			case "secondround":return PlayerState.SecondRound;
			default: Debugger.LogMessage("EnumConverter:PlayerState:given string not recognized="+str+"\n");
				return null;
		}
	}
	public static String PlayerState(PlayerState state){
		switch(state){
			case Rolling:return "rolling";
			case Robbing:return "robbing";
			case Discarding:return "discarding";
			case Playing:return "playing";
			case FirstRound:return "firstround";
			case SecondRound:return "secondround";
			default:return "";
		}
	}
	public static int DevCardType(DevCardType type){
		switch(type){
			case SOLDIER:return 0;
			case YEAR_OF_PLENTY:return 1;
			case MONOPOLY:return 2;
			case ROAD_BUILD:return 3;
			case MONUMENT:return 4;
			default:return 0;
			//SOLDIER, YEAR_OF_PLENTY, MONOPOLY, ROAD_BUILD, MONUMENT
		}
	}
	public static DevCardType DevCardType(int i){
		switch(i){
			case 0:return DevCardType.SOLDIER;
			case 1:return DevCardType.YEAR_OF_PLENTY;
			case 2:return DevCardType.MONOPOLY;
			case 3:return DevCardType.ROAD_BUILD;
			case 4:return DevCardType.MONUMENT;
			default:return null;
		}
	}

}
