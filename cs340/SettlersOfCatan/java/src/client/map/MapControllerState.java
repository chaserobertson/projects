package client.map;

import client.data.RobPlayerInfo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import debugger.Debugger;
import model.definitions.PlayerState;
import model.facade.client.ClientDevCardFacade;
import model.facade.client.ClientPlayingCommandFacade;
import model.facade.server.ServerPlayingCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;
import params.BuildRoadParams;
import params.RoadBuildingParams;
import shared.definitions.PieceType;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * Created by MTAYS on 10/5/2016.
 */
public enum MapControllerState {
    Rolling, Robbing, Discarding, Playing, FirstRound,SecondRound,NotPlaying;

    //As a note on implementation, most of these can just be reduced to a default return and case FirstRound do something.
    //More states might be added later on as well. So long as defaults are defined, it should work.
    public boolean canPlaceRoad(EdgeLocation edgeLocation,boolean free){
        if(this==NotPlaying)return false;
        boolean setupRound;
        if(this==FirstRound||this==SecondRound) setupRound=true;
        else setupRound=false;
        return ClientPlayingCommandFacade.canPlaceRoad(ModelReferenceFacade.getLocalPlayerIndex(),free,edgeLocation,setupRound);
    }

    /**
     * This method is called whenever the user is trying to place a settlement
     * on the map. It is called by the view for each "mouse move" event. The
     * returned value tells the view whether or not to allow the settlement to
     * be placed at the specified location.
     *
     * @param vertLoc
     *            The proposed settlement location
     * @return true if the settlement can be placed at vertLoc, false otherwise
     */
    boolean canPlaceSettlement(VertexLocation vertLoc){
        if(this==NotPlaying)return false;
        boolean setupRound;
        if(this==FirstRound||this==SecondRound) setupRound=true;
        else setupRound=false;
        return ClientPlayingCommandFacade.canPlaceSettlement(ModelReferenceFacade.getLocalPlayerIndex(),false,vertLoc,setupRound);
    }

    /**
     * This method is called whenever the user is trying to place a city on the
     * map. It is called by the view for each "mouse move" event. The returned
     * value tells the view whether or not to allow the city to be placed at the
     * specified location.
     *
     * @param vertLoc
     *            The proposed city location
     * @return true if the city can be placed at vertLoc, false otherwise
     */
    boolean canPlaceCity(VertexLocation vertLoc){
        if(this==NotPlaying)return false;
        return ClientPlayingCommandFacade.canPlaceCity(ModelReferenceFacade.getLocalPlayerIndex(),vertLoc);
    }

    /**
     * This method is called whenever the user is trying to place the robber on
     * the map. It is called by the view for each "mouse move" event. The
     * returned value tells the view whether or not to allow the robber to be
     * placed at the specified location.
     *
     * @param hexLoc
     *            The proposed robber location
     * @return true if the robber can be placed at hexLoc, false otherwise
     */
    boolean canPlaceRobber(HexLocation hexLoc){
        //if(this!=Robbing)return false;
        return ClientPlayingCommandFacade.canMoveRobber(hexLoc);
    }

    /**
     * This method is called when the user clicks the mouse to place a road.
     *
     * @param edgeLoc
     *            The road location
     */
    boolean placeRoad(EdgeLocation edgeLoc,MapController controller){
        switch (this){
            case Playing:
                if (controller.roadCardRemaining>0) {
                    if(controller.roadCardRemaining==2){
                        controller.roadCardSave=edgeLoc;
                        BuildRoadParams params=new BuildRoadParams();
                        params.roadLocation=edgeLoc;
                        params.playerIndex=ModelReferenceFacade.getLocalPlayerIndex();
                        params.setupRound=false;
                        params.free=true;
                        ServerPlayingCommandFacade.placeRoad(params,ModelReferenceFacade.getModel());
                        if(!ClientPlayingCommandFacade.canBuildARoad(ModelReferenceFacade.getLocalPlayerIndex(),true)){
                            ClientDevCardFacade.useRoadBuilding(ModelReferenceFacade.getLocalPlayerIndex(),edgeLoc,null);
                            controller.roadCardRemaining=0;
                            controller.roadCardSave=null;
                        }
                    }
                    else{
                        ClientDevCardFacade.useRoadBuilding(ModelReferenceFacade.getLocalPlayerIndex(),controller.roadCardSave,edgeLoc);
                        controller.roadCardSave=null;
                    }
                    controller.roadCardRemaining--;
                    return true;
                }
                return ClientPlayingCommandFacade.placeRoad(ModelReferenceFacade.getLocalPlayerIndex(),false,edgeLoc,false);
            case FirstRound:
            case SecondRound:
                return ClientPlayingCommandFacade.placeRoad(ModelReferenceFacade.getLocalPlayerIndex(),true,edgeLoc,true);
            default:
                Debugger.LogMessage("MapControllerState:PlaceRoad:called whilst not in a valid state");
        }
        return false;
    }

    /**
     * This method is called when the user clicks the mouse to place a
     * settlement.
     *
     * @param vertLoc
     *            The settlement location
     */
    boolean placeSettlement(VertexLocation vertLoc){
        switch (this){
            case Playing:
                return ClientPlayingCommandFacade.placeSettlement(ModelReferenceFacade.getLocalPlayerIndex(),false,vertLoc,false);
            case FirstRound:
            case SecondRound:
                return ClientPlayingCommandFacade.placeSettlement(ModelReferenceFacade.getLocalPlayerIndex(),true,vertLoc,true);
            default:
                Debugger.LogMessage("MapControllerState:PlaceSettlement:called whilst not in a valid state");
        }
        return false;
    }

    /**
     * This method is called when the user clicks the mouse to place a city.
     *
     * @param vertLoc
     *            The city location
     */
    boolean placeCity(VertexLocation vertLoc){
        switch (this){
            case Playing:
                return ClientPlayingCommandFacade.buildCity(ModelReferenceFacade.getLocalPlayerIndex(),vertLoc);
            default:
                Debugger.LogMessage("MapControllerState:PlaceCity:called whilst not in a valid state");
        }
        return false;
    }

    /**
     * This method is called when the user clicks the mouse to place the robber.
     *
     * @param hexLoc
     *            The robber location
     */
    boolean placeRobber(HexLocation hexLoc){
        return ClientPlayingCommandFacade.moveRobber(hexLoc);
    }

    /**
     * This method is called when the user requests to place a piece on the map
     * (road, city, or settlement)
     *
     * @param pieceType
     *            The type of piece to be placed
     * @param isFree
     *            true if the piece should not cost the player resources, false
     *            otherwise. Set to true during initial setup and when a road
     *            building card is played.
     * @param allowDisconnected
     *            true if the piece can be disconnected, false otherwise. Set to
     *            true only during initial setup.
     */
    void startMove(PieceType pieceType, boolean isFree,
                   boolean allowDisconnected){
        switch (this){
            case Playing:
                break;
            case FirstRound:
            case SecondRound:
                break;
            default:
                Debugger.LogMessage("MapControllerState:StartMove:called whilst not in a valid state");
        }
        return;
    }

    /**
     * This method is called from the modal map overlay when the cancel button
     * is pressed.
     */
    void cancelMove(){
        switch (this){
            case Playing:
                break;
            case Robbing:
                break;
            case Discarding:
                break;
            case FirstRound:
            case SecondRound:
                break;
            default:
                Debugger.LogMessage("MapControllerState:CancelMove:called whilst not in a valid state");
        }
        return;
    }

    /**
     * This method is called when the user plays a "soldier" development card.
     * It should initiate robber placement.
     */
    void playSoldierCard(){
        switch (this){
            case Playing:
                break;
            default:
                Debugger.LogMessage("MapControllerState:PlaySoldierCard:called whilst not in a valid state");
        }
        return;
    }

    /**
     * This method is called when the user plays a "road building" progress
     * development card. It should initiate the process of allowing the player
     * to place two roads.
     */
    void playRoadBuildingCard(MapController controller){
        switch (this){
            case Playing:
                controller.roadCardRemaining=2;
                break;
            default:
                Debugger.LogMessage("MapControllerState:PlayRoadBuildingCard:called whilst not in a valid state");
        }
        return;
    }

    /**
     * This method is called by the Rob View when a player to rob is selected
     * via a button click.
     *
     * @param victim
     *            The player to be robbed
     */
    void robPlayer(RobPlayerInfo victim,HexLocation robberPos,boolean soldier){
        //if(robberPos==null)robberPos=new HexLocation(0,0);//This should never happen fyi.
        if(!soldier)ClientPlayingCommandFacade.robPlayer(ModelReferenceFacade.getLocalPlayerIndex(),victim.getPlayerIndex(),robberPos);
        else ClientDevCardFacade.useSoldier(ModelReferenceFacade.getLocalPlayerIndex(),victim.getPlayerIndex(),robberPos);
        return;
    }

    static MapControllerState convertPlayerStateToMapState(PlayerState state){
        switch(state){
            case Rolling:
                return MapControllerState.Rolling;
            case Robbing:
                return MapControllerState.Robbing;
            case Discarding:
                return MapControllerState.Discarding;
            case Playing:
                return MapControllerState.Playing;
            case FirstRound:
                return MapControllerState.FirstRound;
            case SecondRound:
                return MapControllerState.SecondRound;
            default:
                return MapControllerState.NotPlaying;
        }
    }

}
