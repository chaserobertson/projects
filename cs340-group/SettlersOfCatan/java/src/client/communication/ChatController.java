package client.communication;

import client.base.*;
import debugger.Debugger;
import model.facade.client.ClientGeneralCommandFacade;
import model.facade.shared.GuiModelFacade;
import model.facade.shared.ModelReferenceFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Chat controller implementation
 */
public class ChatController extends Controller implements IChatController {

	public ChatController(IChatView view, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setChatController(this);
	}

	@Override
	public IChatView getView() {
		return (IChatView)super.getView();
	}

	@Override
	public void sendMessage(String message) {
		ClientGeneralCommandFacade.sendChat(ModelReferenceFacade.getLocalPlayerIndex(),message);
	}

	@Override
	public void update(Observable o, Object arg) {
		initFromModel();
	}
	private void initFromModel(){
		List<LogEntry> entryList=GuiModelFacade.getAllChatAsLogEntries();
		if(entryList==null)entryList=new ArrayList<>();
		getView().setEntries(entryList);
	}
}

