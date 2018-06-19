package edu.byu.cs.superasteroids.ship_builder;

import edu.byu.cs.superasteroids.base.IView;
import edu.byu.cs.superasteroids.content.ContentManager;
import edu.byu.cs.superasteroids.database.DatabaseAccessObject;
import edu.byu.cs.superasteroids.drawing.DrawingHelper;
import edu.byu.cs.superasteroids.model.*;
import android.graphics.Bitmap;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaserobertson on 5/22/16.
 */
public class ShipBuildingController implements IShipBuildingController {
    private ShipBuildingActivity shipBuildingActivity = null;
    private Ship ship = Ship.SINGLETON;
    public List<MainBody> mainBodyImages = new ArrayList<>();
    public List<Cannon> cannonImages = new ArrayList<>();
    public List<ExtraParts> extraPartsImages = new ArrayList<>();
    public List<Engine> engineImages = new ArrayList<>();
    public List<PowerCore> powerCoreImages = new ArrayList<>();
    private IView currentView = null;
    private IShipBuildingView.PartSelectionView currentViewType = IShipBuildingView.PartSelectionView.MAIN_BODY;

    public ShipBuildingController(ShipBuildingActivity shipBuildingActivity) {
        this.shipBuildingActivity = shipBuildingActivity;

        DatabaseAccessObject.SINGLETON.emptyModel();
        DatabaseAccessObject.SINGLETON.fillModel();
        if(DatabaseAccessObject.SINGLETON.isEmpty()) return;

        ship.initialize();
    }

    @Override
    public void loadContent(ContentManager content) {
        if(Model.SINGLETON.isEmpty()) return;

        mainBodyImages.clear();
        cannonImages.clear();
        extraPartsImages.clear();
        engineImages.clear();
        powerCoreImages.clear();
        List<Integer> mainBodyImageIDs = new ArrayList<>();
        List<Integer> cannonImageIDs = new ArrayList<>();
        List<Integer> extraPartsImageIDs = new ArrayList<>();
        List<Integer> engineImageIDs = new ArrayList<>();
        List<Integer> powerCoreImageIDs = new ArrayList<>();

        for(VisibleObject current : Model.SINGLETON.content) {
            if(current instanceof MainBody) {
                mainBodyImages.add((MainBody)current);
                mainBodyImageIDs.add(current.imageID);
            }
            else if(current instanceof Cannon) {
                cannonImages.add((Cannon)current);
                cannonImageIDs.add(current.imageID);
            }
            else if(current instanceof ExtraParts) {
                extraPartsImages.add((ExtraParts)current);
                extraPartsImageIDs.add(current.imageID);
            }
            else if(current instanceof Engine) {
                engineImages.add((Engine)current);
                engineImageIDs.add(current.imageID);
            }
            else if(current instanceof PowerCore) {
                powerCoreImages.add((PowerCore)current);
                powerCoreImageIDs.add(current.imageID);
            }
        }

        shipBuildingActivity.setPartViewImageList(IShipBuildingView.PartSelectionView.MAIN_BODY, mainBodyImageIDs);
        shipBuildingActivity.setPartViewImageList(IShipBuildingView.PartSelectionView.CANNON, cannonImageIDs);
        shipBuildingActivity.setPartViewImageList(IShipBuildingView.PartSelectionView.EXTRA_PART, extraPartsImageIDs);
        shipBuildingActivity.setPartViewImageList(IShipBuildingView.PartSelectionView.ENGINE, engineImageIDs);
        shipBuildingActivity.setPartViewImageList(IShipBuildingView.PartSelectionView.POWER_CORE, powerCoreImageIDs);
    }

    @Override
    public void unloadContent(ContentManager content) {}

    @Override
    public void setView(IView view) {
        currentView = view;
    }

    @Override
    public void onSlideView(IShipBuildingView.ViewDirection direction) {
        if(currentViewType.equals(IShipBuildingView.PartSelectionView.MAIN_BODY)) {

            if (direction.equals(IShipBuildingView.ViewDirection.UP)) {
                currentViewType = IShipBuildingView.PartSelectionView.ENGINE;
            } else if (direction.equals(IShipBuildingView.ViewDirection.DOWN)) {
                currentViewType = IShipBuildingView.PartSelectionView.POWER_CORE;
            } else if (direction.equals(IShipBuildingView.ViewDirection.LEFT)) {
                currentViewType = IShipBuildingView.PartSelectionView.CANNON;
            } else if (direction.equals(IShipBuildingView.ViewDirection.RIGHT)) {
                currentViewType = IShipBuildingView.PartSelectionView.EXTRA_PART;
            }
        }
        else if(currentViewType.equals(IShipBuildingView.PartSelectionView.ENGINE)) {
            if(direction.equals(IShipBuildingView.ViewDirection.DOWN)) {
                currentViewType = IShipBuildingView.PartSelectionView.MAIN_BODY;
            }
        } else if(currentViewType.equals(IShipBuildingView.PartSelectionView.POWER_CORE)) {
            if(direction.equals(IShipBuildingView.ViewDirection.UP)) {
                currentViewType = IShipBuildingView.PartSelectionView.MAIN_BODY;
            }
        } else if(currentViewType.equals(IShipBuildingView.PartSelectionView.CANNON)) {
            if(direction.equals(IShipBuildingView.ViewDirection.RIGHT)) {
                currentViewType = IShipBuildingView.PartSelectionView.MAIN_BODY;
            }
        } else if(currentViewType.equals(IShipBuildingView.PartSelectionView.EXTRA_PART)) {
            if(direction.equals(IShipBuildingView.ViewDirection.LEFT)) {
                currentViewType = IShipBuildingView.PartSelectionView.MAIN_BODY;
            }
        }

        shipBuildingActivity.animateToView(currentViewType, inverse(direction));
    }

    private IShipBuildingView.ViewDirection inverse(IShipBuildingView.ViewDirection direction) {
        switch (direction) {
            case UP: return IShipBuildingView.ViewDirection.DOWN;
            case DOWN: return IShipBuildingView.ViewDirection.UP;
            case RIGHT: return IShipBuildingView.ViewDirection.LEFT;
            case LEFT: return IShipBuildingView.ViewDirection.RIGHT;
        }
        return null;
    }

    @Override
    public void onViewLoaded(IShipBuildingView.PartSelectionView partView) {

        switch (partView) {
            case MAIN_BODY: shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.UP, true, "Power Core");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.DOWN, true, "Engine");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.LEFT, true, "Left Wing");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.RIGHT, true, "Cannon");
                break;
            case CANNON: shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.UP, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.DOWN, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.LEFT, true, "Main Body");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.RIGHT, false, "");
                break;
            case EXTRA_PART: shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.UP, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.DOWN, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.LEFT, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.RIGHT, true, "Main Body");
                break;
            case ENGINE: shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.UP, true, "Main Body");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.DOWN, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.LEFT, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.RIGHT, false, "");
                break;
            case POWER_CORE: shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.UP, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.DOWN, true, "Main Body");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.LEFT, false, "");
                shipBuildingActivity.setArrow(partView, IShipBuildingView.ViewDirection.RIGHT, false, "");
                break;
        }
    }

    @Override
    public void onStartGamePressed() {
        shipBuildingActivity.startGame();
    }

    @Override
    public void onResume() {
        ship.reposition();
    }

    @Override
    public void onPartSelected(int index) {
        ShipPart attachment = null;
        if(currentViewType.equals(IShipBuildingView.PartSelectionView.MAIN_BODY)) {
            attachment = mainBodyImages.get(index);
            //attachment.position.x = shipBuildingActivity.getWallpaperDesiredMinimumWidth() / 2;
            //attachment.position.y = shipBuildingActivity.getWallpaperDesiredMinimumHeight() / 2;
        }
        else if(currentViewType.equals(IShipBuildingView.PartSelectionView.CANNON)) {
            attachment = cannonImages.get(index);
        }
        else if(currentViewType.equals(IShipBuildingView.PartSelectionView.EXTRA_PART)) {
            attachment = extraPartsImages.get(index);
        }
        else if(currentViewType.equals(IShipBuildingView.PartSelectionView.ENGINE)) {
            attachment = engineImages.get(index);
        }
        else if(currentViewType.equals(IShipBuildingView.PartSelectionView.POWER_CORE)) {
            attachment = powerCoreImages.get(index);
        }

        ship.attach(attachment);
        if(ship.isInitialized()) shipBuildingActivity.setStartGameButton(true);
    }

    @Override
    public void update(double elapsedTime) {}

    @Override
    public void draw() {
        ship.reposition();
        ship.draw();
    }

    @Override
    public IView getView() {
        return currentView;
    }

}
