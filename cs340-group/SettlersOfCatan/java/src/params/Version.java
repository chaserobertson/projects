package params;

import commands.ICommand;

/**
 * Created by chase on 9/20/16.
 */
public class Version extends GameRequiredParam{

    public int versionNumber;

    public Version(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Version(String versionNumber) {
        this.versionNumber = new Integer(versionNumber);
    }

    @Override
    public String toString() {
        return ""+versionNumber;
    }
    @Override
    public ICommand generateCommand(){
        return null;
    }
}
