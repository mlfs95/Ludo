package interfaces;

public interface ObservadoIF {

	void add(ObservadorIF observador);
	
	void remove(ObservadorIF observador);
	
	Object get(int i);
}
