package client.communication;

import java.util.*;
import java.util.List;

import client.base.*;
import model.facade.shared.GuiModelFacade;
import shared.definitions.*;


/**
 * Game history controller implementation
 */
public class GameHistoryController extends Controller implements IGameHistoryController {
	//Matthew- I'm adding in a Entry Log for the purpose of single player Logging as it were.
	//Its added into the Debugger class for usage
	private static List<LogEntry> lastInput;
	private static List<LogEntry> debugs;
	public static GameHistoryController instance;
	//

	public GameHistoryController(IGameHistoryView view, Observable observable) {
		
		super(view);

		observable.addObserver(this);

		controllerHolder.setGameHistoryController(this);

		initFromModel();

		//Also added by Matthew for Debugging
		instance=this;
		lastInput=new ArrayList<>();
		debugs=new LinkedList<>();
	}

	@Override
	public void update(Observable o, Object arg) {
		initFromModel();
	}

	@Override
	public IGameHistoryView getView() {
		
		return (IGameHistoryView)super.getView();
	}
	
	private void initFromModel() {
		//<temp>
		List<LogEntry> entries = new ArrayList<LogEntry>();
		entries=GuiModelFacade.getAllSystemLogAsLogEntries();
		if(entries==null)entries=new ArrayList<>();
		lastInput=new LinkedList<>();
		for(int i=0;i<entries.size();++i){
			lastInput.add(new LogEntry(entries.get(i).getColor(),entries.get(i).getMessage()));
		}
		if(debugs!=null){
			for(int i=0;i<debugs.size();++i){
				entries.add(debugs.get(i));
			}
		}
		getView().setEntries(entries);
	
		//</temp>
	}

	//Matthew-Added this function for outputting log entries during Debugging
	public void addEntry(String message, CatanColor color){//Not functional...
		if(true)return;
		debugs.add(new LogEntry(color,message));
		List<LogEntry> temp = new LinkedList<>();
		for(int i=0;i<lastInput.size();++i){
			temp.add(new LogEntry(lastInput.get(i).getColor(),lastInput.get(i).getMessage()));
		}
		for(int i=0;i<debugs.size();++i){
			temp.add(new LogEntry(debugs.get(i).getColor(),debugs.get(i).getMessage()));
		}
		getView().setEntries(new ArrayList<>(temp));
	}
}

