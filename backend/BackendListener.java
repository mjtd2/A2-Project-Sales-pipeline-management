package backend;

//This interface means that all methods which implement BackendListener will have a backendUpdated method.
public interface BackendListener
{
	public void backendUpdated();
}