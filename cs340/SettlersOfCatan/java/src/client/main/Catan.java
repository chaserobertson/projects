package client.main;

import javax.swing.*;
import javax.swing.tree.ExpandVetoException;

import client.catan.*;
import client.login.*;
import client.join.*;
import client.misc.*;
import client.base.*;
import debugger.Debugger;
import model.facade.shared.ModelReferenceFacade;
import server.ClientCommunicator;
import server.HTTPServerProxy;

import java.util.Scanner;

/**
 * Main entry point for the Catan program
 */
@SuppressWarnings("serial")
public class Catan extends JFrame
{
	
	private CatanPanel catanPanel;

	public Catan()
	{
		
		client.base.OverlayView.setWindow(this);
		
		this.setTitle("Settlers of Catan");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		catanPanel = new CatanPanel();
		this.setContentPane(catanPanel);
		
		display();
	}
	
	private void display()
	{
		pack();
		setVisible(true);
	}
	
	//
	// Main
	//
	
	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run()
			{
				new Catan();
				parseArgs(args);
				String username=parseArgsForUsername(args);
				String password=parseArgsForPassword(args);
				
				PlayerWaitingView playerWaitingView = new PlayerWaitingView();
				final PlayerWaitingController playerWaitingController = new PlayerWaitingController(
														playerWaitingView, ModelReferenceFacade.getInstance());
				playerWaitingView.setController(playerWaitingController);
				
				JoinGameView joinView = new JoinGameView();
				NewGameView newGameView = new NewGameView();
				SelectColorView selectColorView = new SelectColorView();
				MessageView joinMessageView = new MessageView();
				final JoinGameController joinController = new JoinGameController(joinView,
																				 newGameView,
																				 selectColorView,
																				 joinMessageView,
																				 ModelReferenceFacade.getInstance());
				joinController.setJoinAction(new IAction() {
					@Override
					public void execute()
					{
						playerWaitingController.start();
					}
				});
				joinView.setController(joinController);
				newGameView.setController(joinController);
				selectColorView.setController(joinController);
				joinMessageView.setController(joinController);
				
				LoginView loginView = new LoginView();
				MessageView loginMessageView = new MessageView();
				LoginController loginController = new LoginController(loginView,
																	  loginMessageView,
																	  ModelReferenceFacade.getInstance());
				loginController.setLoginAction(new IAction() {
					@Override
					public void execute()
					{
						joinController.start();
					}
				});
				loginView.setController(loginController);
				loginView.setController(loginController);
				
				loginController.start();
				//Potential auto login code which could be used
				if(username!=null&&password!=null) {
					Debugger.LogMessage("username is "+username);
					Debugger.LogMessage("password is "+password);
					loginController.autoSignIn(username,password);
				}
			}
		});
	}
	private static String parseArgsForUsername(String[] args){
		for(int i=0;i<args.length;++i){
			if(args[i].equals("-username")&&args.length>=i+1){
				return args[i+1];
			}
		}
		return null;
	}
	private static String parseArgsForPassword(String[] args){
		for(int i=0;i<args.length;++i){
			if(args[i].equals("-password")&&args.length>=i+1){
				return args[i+1];
			}
		}
		return null;
	}

	private static void parseArgs(String[] args) {
		int port = 8081;
		String host = "localhost";
		HTTPServerProxy.getInstance().setPort(port);
		HTTPServerProxy.getInstance().setHost(host);
		if(args.length < 1) return;

		for(int i=0;i<args.length;++i){
			if(args[i].equals("-port")&&args.length>=i+1){
				try {
					port = new Integer(args[i + 1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(args[i].equals("-host")&&args.length>=i+1) {
				host = args[i+1];
			}
		}

		Debugger.LogMessage("host="+host);
		Debugger.LogMessage("port="+port);
		HTTPServerProxy.getInstance().setPort(port);
		HTTPServerProxy.getInstance().setHost(host);
	}
}

