package commands;

import params.SendChatParams;

/**
 * Created by tsmit on 11/7/2016.
 */
public class SendChatCommand  extends Command{
    private SendChatParams params;

    public SendChatCommand(SendChatParams params) {
        this.params = params;
    }

    @Override
    public String execute() {
        return server.sendChat(params);
    }
}