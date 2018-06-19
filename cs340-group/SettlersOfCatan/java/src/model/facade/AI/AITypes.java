package model.facade.AI;

/**
 * Created by MTAYS on 11/22/2016.
 */
public enum AITypes {
    miniSkynet;
    public int toInt(){
        switch (this){
            case miniSkynet:return -666;
            default:return -11;
        }
    }
    public static AITypes AIType(int i){
        switch (i){
            case -666:return miniSkynet;
            default:return null;
        }
    }
    public static AITypes AIType(String type){
        type=type.toLowerCase();
        switch (type){
            case "mini skynet": return miniSkynet;
            default:return null;
        }
    }
    public AIInterface getInterface(){
        switch (this){
            case miniSkynet:return AIInterface.getInstance();
            default:return null;
        }
    }
    public static String getAITypesList(){
        return "[\n"+
                "\"Mini Skynet\"\n"+
                "]";
    }
}
