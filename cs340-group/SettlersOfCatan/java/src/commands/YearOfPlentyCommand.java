package commands;

import debugger.Debugger;
import params.YearOfPlentyParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class YearOfPlentyCommand  extends Command{
    private YearOfPlentyParams params;

    public YearOfPlentyCommand(YearOfPlentyParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.playCardYearOfPlenty(params);
    }
}
