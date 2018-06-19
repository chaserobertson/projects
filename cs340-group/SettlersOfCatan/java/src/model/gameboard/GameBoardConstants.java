package model.gameboard;

import shared.definitions.HexType;
import shared.definitions.PortType;
import shared.locations.EdgeDirection;

/**
 * Created by MTAYS on 10/25/2016.
 */
public class GameBoardConstants {
    /**
     * Description: amount of hexes in a traditional game
     */
    public static final int HEXCOUNT=19;
    /**
     * Description: amount of nodes in a traditional game
     */
    public static final int NODECOUNT=54;
    /**
     * Description: amount of ports in a traditional game
     */
    public static final int PORTCOUNT=9;
    /**
     * Description: HexLocation of traditional hexes, arranged by index
     */
    public static final int[][] HEXLOCATIONBINDING=new int[][]{
            {0,-2},
            {-1,-1}, {1,-2},
            {-2,0}, {0,-1}, {2,-2},
            {-1,0}, {1,-1},
            {-2,1}, {0,0}, {2,-1},
            {-1,1}, {1,0},
            {-2,2}, {0,1}, {2,0},
            {-1,2}, {1,1},
            {0,2}
    };
    /**
     * Description: an array containing the traditional binding of vertices to hexes by index
     */
    public static final int[][] VERTEXHEXBINDING=new int[][]{
            {0,-1,-1},{0,-1,-1},//0-1
            {1,-1,-1},{1,0,-1},{2,0,-1},{2,-1,-1},//2-5
            {3,-1,-1},{3,1,-1},{4,1,0},{4,2,0},{5,2,-1},{5,-1,-1},//6-11
            {3,-1,-1},{6,3,1},{6,4,1},{7,4,2},{7,5,2},{5,-1,-1},//12-17
            {8,3,-1},{8,6,3},{9,6,4},{9,7,4},{10,7,5},{10,5,-1},//18-23
            {8,-1,-1},{11,8,6},{11,9,6},{12,9,7},{12,10,7},{10,-1,-1},//24-29
            {13,8,-1},{13,11,8},{14,11,9},{14,12,9},{15,12,10},{15,10,-1},//30-35
            {13,-1,-1},{16,13,11},{16,14,11},{17,14,12},{17,15,12},{15,-1,-1},//36-41
            {13,-1,-1},{13,16,-1},{18,16,14},{18,17,14},{15,17,-1},{17,-1,-1},//42-47
            {16,-1,-1},{16,18,-1},{17,18,-1},{17,-1,-1},//48-51
            {18,-1,-1},{18,-1,-1}//52-53
    };
    /**
     * Description: Vertices to Hex binding by index for all directions of hexes. Each vertices has three associated directions
     */
    public static final int[][] VERTEXALLHEXDIRECTIONBINDING=new int[][]{
            {1,-1,-1},{2,-1,-1},//0-1
            {1,-1,-1},{2,0,-1},{1,3,-1},{2,-1,-1},//2-5
            {1,-1,-1},{2,0,-1},{1,3,5},{2,0,4},{1,3,-1},{2,-1,-1},//6-11
            {0,-1,-1},{1,3,5},{2,0,4},{1,3,5},{2,0,4},{3,-1,-1},//12-17
            {1,5,-1},{2,0,4},{1,3,5},{2,0,4},{1,3,5},{2,4,-1},//18-23
            {0,-1,-1},{1,3,5},{2,0,4},{1,3,5},{2,0,4},{3,-1,-1},//24-29
            {1,5,-1},{2,0,4},{1,3,5},{2,0,4},{1,3,5},{2,4,-1},//30-35
            {0,-1,-1},{1,3,5},{2,0,4},{1,3,5},{2,0,4},{3,-1,-1},//36-41
            {5,-1,-1},{4,0,-1},{1,3,5},{2,0,4},{5,3,-1},{4,-1,-1},//42-47
            {5,-1,-1},{4,0,-1},{5,3,-1},{4,-1,-1},//48-51
            {5,-1,-1},{4,-1,-1}//52-53
    };
    /**
     * Description: bidning of vertices to vertices by index in traditional game
     */
    public static final int[][] VERTEXVERTEXBINDING=new int[][]{
            {1,3,-1},{0,4,-1},//0-1
            {3,7,-1},{0,2,8},{1,9,5},{4,10,-1},//2-5
            {7,12,-1},{2,6,13},{3,14,9},{4,8,15},{5,16,11},{10,17,-1},//6-11
            {6,18,-1},{7,19,14},{13,8,20},{9,21,16},{15,10,22},{11,23,-1},//12-17
            {12,24,19},{13,18,25},{14,21,26},{15,20,27},{16,23,28},{17,22,29},//18-23
            {18,30,-1},{19,26,31},{20,25,32},{21,28,32},{22,27,34},{23,35,-1},//24-29
            {24,36,31},{25,30,37},{26,33,38},{27,32,39},{28,35,40},{29,34,41},//30-35
            {30,42,-1},{31,38,43},{32,37,44},{33,40,45},{34,39,46},{35,47,-1},//36-41
            {36,43,-1},{37,42,48},{38,45,49},{39,44,50},{40,47,51},{41,46,-1},//42-47
            {43,49,-1},{44,48,52},{45,51,53},{46,50,-1},//48-51
            {49,53,-1},{50,52,-1}//52-53
    };
    /**
     * Description: binding of ports to vertices by index in traditional game
     */
    public static final int[][] PORTVERTEXBINDING=new int[][]{//TRAVELS CLOCKWISE AROUND MAP FROM 11:00
            {2,3},
            {4,5},
            {11,17},
            {29,35},
            {47,51},
            {52,53},
            {43,48},
            {24,30},
            {6,12}
    };
    /**
     * Description: port to type binding by index in traditional game
     */
    public static final PortType[] PORTTYPEBINDING=new PortType[]{
            PortType.WHEAT,
            PortType.ORE,
            PortType.THREE,
            PortType.SHEEP,
            PortType.THREE,
            PortType.THREE,
            PortType.BRICK,
            PortType.WOOD,
            PortType.THREE
    };

    public static final HexType[] DEFAULTHEXTYPEBINDING=new HexType[]{//yes 4 woods
            HexType.BRICK,
            HexType.SHEEP, HexType.WHEAT,
            HexType.WHEAT, HexType.WOOD, HexType.SHEEP,
            HexType.WOOD, HexType.WOOD,
            HexType.SHEEP, HexType.DESERT, HexType.WOOD,
            HexType.ORE, HexType.BRICK,
            HexType.ORE, HexType.WHEAT, HexType.ORE,
            HexType.WHEAT, HexType.SHEEP,
            HexType.BRICK
    };
    public static int[] DEFAULTHEXCHITBINDING=new int[]{
            8,
            10,6,
            11,11,10,
            6,3,
            9,7,4,
            9,5,
            5,8,3,
            2,12,
            4
    };
    public static int[][] DEFAULTPORTHEXPOSBINDING=new int[][]{//Instead of other port
            {-3,0},
            {2,1},
            {3,-3},
            {-3,2},
            {-2,3},
            {-1,-2},
            {3,-1},
            {1,-3},
            {0,3}
    };
    public static PortType[] DEFAULTPORTTYPEBINDING=new PortType[]{
            PortType.SHEEP,
            PortType.ORE,
            PortType.THREE,
            PortType.THREE,
            PortType.WOOD,
            PortType.THREE,
            PortType.WHEAT,
            PortType.BRICK,
            PortType.THREE
    };
    public static EdgeDirection[] DEFAULTPORTDIRECTIONBINDING=new EdgeDirection[]{
            EdgeDirection.SouthEast,
            EdgeDirection.NorthWest,
            EdgeDirection.SouthWest,
            EdgeDirection.NorthEast,
            EdgeDirection.NorthEast,
            EdgeDirection.South,
            EdgeDirection.NorthWest,
            EdgeDirection.South,
            EdgeDirection.North
    };
}
