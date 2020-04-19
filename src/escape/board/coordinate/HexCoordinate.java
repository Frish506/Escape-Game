package escape.board.coordinate;

import java.util.Objects;

import escape.exception.EscapeException;

public class HexCoordinate implements Coordinate {
	
	private final int x;
	private final int y;
	
	private HexCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	

	
	public static HexCoordinate makeCoordinate(int x, int y) {
		return new HexCoordinate(x, y);
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
		if (!(obj instanceof HexCoordinate)) {
			return false;
		}
		HexCoordinate other = (HexCoordinate) obj;
		return getX() == other.getX() && getY() == other.getY();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	@Override
	public int distanceTo(Coordinate c) {
		if (!(c instanceof HexCoordinate)) {
			throw new EscapeException("Cannot get distance from HexCoordinate to a different coordinate type");
		}
		HexCoordinate to = (HexCoordinate) c;
		
		return (Math.abs(this.getY() - to.getY()) + 
			    Math.abs(this.getX() - to.getX()) +
			    Math.abs(this.getX() + this.getY() - to.getX() - to.getY())) / 2;
	}
	

}
