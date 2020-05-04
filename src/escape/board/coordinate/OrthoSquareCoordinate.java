package escape.board.coordinate;

import java.util.Objects;

import escape.exception.EscapeException;

public class OrthoSquareCoordinate implements BetterCoordinate {
	
	private final int x;
	private final int y;
	
	private OrthoSquareCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//Cannot make a square coordinate less than 1 because square boards don't have negative or 0 spaces
	public static OrthoSquareCoordinate makeCoordinate(int x, int y) {
		return new OrthoSquareCoordinate(x, y);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OrthoSquareCoordinate)) {
			return false;
		}
		OrthoSquareCoordinate other = (OrthoSquareCoordinate) obj;
		return getX() == other.getX() && getY() == other.getY();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	//
	@Override
	public int distanceTo(Coordinate c) throws EscapeException {
		if (!(c instanceof OrthoSquareCoordinate)) {
			throw new EscapeException("Cannot get distance from OrthoSquareCoordinate to a different coordinate type");
		}
		OrthoSquareCoordinate to = (OrthoSquareCoordinate) c;
		int absX = Math.abs(this.getX() - to.getX());
		int absY = Math.abs(this.getY() - to.getY());
		return absX + absY;
	}
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
