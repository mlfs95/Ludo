package Client;

public interface ObservableGame {

	public void addObserver(ObserverGame o);
	public void removeObserver(ObserverGame o);
	public void notifyObserver(ObserverGame o);
	
}
