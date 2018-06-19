package edu.byu.cs.superasteroids.main_menu;

import edu.byu.cs.superasteroids.base.IView;
import edu.byu.cs.superasteroids.content.ContentManager;
import edu.byu.cs.superasteroids.database.DatabaseAccessObject;
import edu.byu.cs.superasteroids.database.DatabaseOpenHelper;
import edu.byu.cs.superasteroids.model.*;
import edu.byu.cs.superasteroids.ship_builder.ShipBuildingActivity;
import edu.byu.cs.superasteroids.ship_builder.ShipBuildingController;
import edu.byu.cs.superasteroids.database.Database;
/**
 * Created by chaserobertson on 5/21/16.
 */
public class MainMenuController implements IMainMenuController {
    private IMainMenuView mainActivity = null;

    public MainMenuController(IMainMenuView mainMenuView) {
        mainActivity = mainMenuView;
    }

    @Override
    public void onQuickPlayPressed() {
        DatabaseAccessObject.SINGLETON.emptyModel();
        DatabaseAccessObject.SINGLETON.fillModel();

        if(DatabaseAccessObject.SINGLETON.isEmpty()) return;

        Ship ship = Ship.SINGLETON;
        ship.initialize();
        ship.mainBody = Model.SINGLETON.mainBodies.iterator().next();
        ship.cannon = Model.SINGLETON.cannons.iterator().next();
        ship.extraParts = Model.SINGLETON.extraPartses.iterator().next();
        ship.engine = Model.SINGLETON.engines.iterator().next();
        ship.powerCore = Model.SINGLETON.powerCores.iterator().next();
        mainActivity.startGame();
    }

    @Override
    public IView getView() {
        return mainActivity;
    }

    @Override
    public void setView(IView view) {
        if(view instanceof IMainMenuView) {
            mainActivity = (IMainMenuView)view;
        }
    }
}
