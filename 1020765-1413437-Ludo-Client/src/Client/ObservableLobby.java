package Client;
public interface ObservableLobby {

	public void addObserver(ObserverLobby o);
	public void removeObserver(ObserverLobby o);
	public void notifyObserver(ObserverLobby o);

}
