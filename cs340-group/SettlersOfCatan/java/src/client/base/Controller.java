package client.base;

import client.access.ControllerHolder;

import java.util.Observer;

/**
 * Base class for controllers
 */
public abstract class Controller implements IController, Observer
{
	public ControllerHolder controllerHolder = ControllerHolder.getInstance();

	private IView view;
	
	protected Controller(IView view)
	{
		setView(view);
	}
	
	private void setView(IView view)
	{
		this.view = view;
	}
	
	@Override
	public IView getView()
	{
		return this.view;
	}
	
}

