package escape;

public class EscapeObserver implements GameObserver {

	@Override
	public void notify(String message) {
		System.out.println(message);

	}

	@Override
	public void notify(String message, Throwable cause) {
		System.out.println(message);
	}
}
