package debugger;

import client.communication.GameHistoryController;
import client.communication.LogEntry;
import shared.definitions.CatanColor;

import java.util.LinkedList;

/**
 * Created by MTAYS on 9/26/2016.
 */
public class Debugger {

    public static void LogMessage(String message){
        /*if((message.charAt(0)=='C'&&message.charAt(1)=='o')||
                message.charAt(0)=='e'&&message.charAt(1)=='n'||
                message.charAt(0)=='c'&&message.charAt(1)=='a')return;*/

        System.out.print(message+"\n");
    }
    public static void LogGameScreenMessage(String message){
        if(true)return;
        GameHistoryController.instance.addEntry(message, CatanColor.WHITE);
    }
    public static void LogBothGameTerminalMessage(String message){
        LogMessage(message);
        LogGameScreenMessage(message);
    }
}
