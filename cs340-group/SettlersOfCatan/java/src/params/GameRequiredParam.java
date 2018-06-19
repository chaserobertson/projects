package params;

import commands.ICommand;

import java.io.Serializable;

/**
 * Created by tsmit on 11/11/2016.
 */
public abstract class GameRequiredParam implements Serializable {
    public int gameId;
    public abstract ICommand generateCommand();
}
