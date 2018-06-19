package client.login;

import client.base.*;
import client.misc.*;
import debugger.Debugger;
import model.facade.shared.ModelReferenceFacade;
import params.Credentials;
import server.HTTPServerProxy;

import java.util.*;


/**
 * Implementation for the login controller
 */
public class LoginController extends Controller implements ILoginController {
	public static String storedPassword=null;//Super unsafe right? Matthew is using this to pass in the password for game restart

	private IMessageView messageView;
	private IAction loginAction;
	
	/**
	 * LoginController constructor
	 * 
	 * @param view Login view
	 * @param messageView Message view (used to display error messages that occur during the login process)
	 */
	public LoginController(ILoginView view, IMessageView messageView, Observable observable) {

		super(view);

		observable.addObserver(this);

		controllerHolder.setLoginController(this);

		this.messageView = messageView;
	}

	@Override
	public void update(Observable o, Object arg) {}

	public ILoginView getLoginView() {
		return (ILoginView)super.getView();
	}
	
	public IMessageView getMessageView() {
		return messageView;
	}
	
	/**
	 * Sets the action to be executed when the user logs in
	 * 
	 * @param value The action to be executed when the user logs in
	 */
	public void setLoginAction(IAction value) {
		loginAction = value;
	}
	
	/**
	 * Returns the action to be executed when the user logs in
	 * 
	 * @return The action to be executed when the user logs in
	 */
	public IAction getLoginAction() {
		return loginAction;
	}

	@Override
	public void start() {
		getLoginView().showModal();
	}

	public void autoSignIn(String username,String password){
		storedPassword=password;
		Credentials credentials = new Credentials(username, password);
		String response = HTTPServerProxy.getInstance().userLogin(credentials);
		if (response.equals("Success")) {
			getLoginView().closeModal();
			loginAction.execute();
		}
		else {
			getMessageView().setTitle("Login Error");
			getMessageView().setMessage("Login failed - bad username or password");
			getMessageView().showModal();
		}
	}

	@Override
	public void signIn() {
		String username = getLoginView().getLoginUsername();
		String password = getLoginView().getLoginPassword();
		storedPassword=password;
        Credentials credentials = new Credentials(username, password);
        String response = HTTPServerProxy.getInstance().userLogin(credentials);
        if (response.equals("Success")) {
            getLoginView().closeModal();
            loginAction.execute();
        }
        else {
            getMessageView().setTitle("Login Error");
            getMessageView().setMessage("Login failed - bad username or password");
            getMessageView().showModal();
        }
	}

	@Override
	public void register() {
		
        String username = getLoginView().getRegisterUsername();
        String password = getLoginView().getRegisterPassword();
        String passwordRepeat = getLoginView().getRegisterPasswordRepeat();

        boolean usernameTooShort = username.length() < 3;
        boolean usernameTooLong = username.length() > 7;
        boolean passwordTooShort = password.length() < 5;
        String regex = "[[a-z]*[A-Z]*[0-9]*[_]*[-]*]*";
        if(!usernameTooShort && !usernameTooLong && !passwordTooShort && username.matches(regex) && password.matches(regex)) {

            if (password.equals(passwordRepeat)) {
                Credentials credentials = new Credentials(username, password);
                String response = HTTPServerProxy.getInstance().userRegister(credentials);
                if (response.equals("Success")) {
                    // If register succeeded
                    HTTPServerProxy.getInstance().userLogin(credentials);
                    getLoginView().closeModal();
                    loginAction.execute();
                } else {
                    // reject existing user
                    getMessageView().setTitle("Error!");
                    getMessageView().setMessage("Register failed.");
                    getMessageView().showModal();
                }
            } else {
                // reject non-matching passwords
                getMessageView().setTitle("Warning!");
                getMessageView().setMessage("Passwords don't match.");
                getMessageView().showModal();
            }
        }
        else {
            // reject invalid input
            getMessageView().setTitle("Warning!");
            getMessageView().setMessage("Invalid username or password. " +
                    "The username must be between 3 and 7 characters. " +
                    "The password must be 5 or more characters. " +
                    "Valid characters are: letters, digits, _ and -");
            getMessageView().showModal();
        }
	}

	@Override
	public void connect(){
		Debugger.LogMessage("LoginController connect called");
		try {
			int port=Integer.parseInt(getLoginView().getPort());
			if(HTTPServerProxy.getInstance().isConnected()){
				getLoginView().setIPConnectedMessage(getLoginView().getIPAddress());
				getLoginView().setPortConnectedMessage(getLoginView().getPort());
				HTTPServerProxy.getInstance().setPort(port);
				HTTPServerProxy.getInstance().setHost(getLoginView().getIPAddress());
			}
			else {
				getLoginView().setIPConnectedMessage("Error, "+getLoginView().getIPAddress());
				getLoginView().setPortConnectedMessage("Error, "+getLoginView().getPort());
			}
		}catch (Exception e){
			getLoginView().setIPConnectedMessage("Error, "+getLoginView().getIPAddress()+" not correct");
			getLoginView().setPortConnectedMessage("Error, "+getLoginView().getPort()+" not correct");
			Debugger.LogMessage("An error was thrown trying to connect!");
			e.printStackTrace();
		}
	}

}

