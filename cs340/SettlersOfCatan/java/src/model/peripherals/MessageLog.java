package model.peripherals;
import java.io.Serializable;
import java.util.*;
import model.player.*;
/**
 * Created by MTAYS on 9/21/2016.
 */
public class MessageLog implements Serializable{
    private List<ChatObject> messageLog;
    private static final int MAXMESSAGES=50;
    public MessageLog(){
        messageLog =new LinkedList<>();
    }
    public void addMessage(Player player, String message){
        if(player==null)return;
        addMessage(player.getNickname(),message);
    }
    public void addMessage(String source, String message){
        messageLog.add(new ChatObject(source,message));
        while(messageLog.size()>=MAXMESSAGES){
            messageLog.remove(0);
        }
    }
    public void clear(){
        messageLog.clear();
    }
    public int getLength(){return messageLog.size();}
    public String getSource(int i){
        if(i>=messageLog.size()||i<0)return "";
        return messageLog.get(i).source;
    }
    public String getMessage(int i){
        if(i>=messageLog.size()||i<0)return "";
        return messageLog.get(i).message;
    }
    public String toString(){
        StringBuilder result=new StringBuilder();
        for (ChatObject chatObject: messageLog) {
            result.append(chatObject.source);
            result.append(":");
            result.append(chatObject.message);
            result.append("\n");
        }
        return result.toString();
    }
    private class ChatObject implements Serializable{
        public String source;
        public String message;
        ChatObject(String source, String message){this.source=source;this.message=message;}
    }
    public String serialize(){
        //TODO:implement
        return "";
    }
    public boolean equals(MessageLog messageLog){
        if(messageLog==null)return false;
        if(!toString().equals(messageLog.toString()))return false;
        return true;
    }
}
