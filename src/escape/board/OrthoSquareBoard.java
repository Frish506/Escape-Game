package escape.board;

import java.util.HashMap;
import java.util.Map;

import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

public class OrthoSquareBoard implements StandardBoard<OrthoSquareCoordinate> {

	Map<OrthoSquareCoordinate, LocationType> squares;
	Map<OrthoSquareCoordinate, EscapePiece> pieces;
	
	private final int xMax, yMax;
	public OrthoSquareBoard(int xMax, int yMax) throws EscapeException
	{
		if(xMax < 1 || yMax < 1) { throw new EscapeException("Cannot make board with x or y smaller than 1"); }
		this.xMax = xMax;
		this.yMax = yMax;
		pieces = new HashMap<OrthoSquareCoordinate, EscapePiece>();
		squares = new HashMap<OrthoSquareCoordinate, LocationType>();
	}
	
	/*
	 * @see escape.board.Board#getPieceAt(escape.board.coordinate.Coordinate)
	 */
	@Override
	public EscapePiece getPieceAt(OrthoSquareCoordinate coord) throws EscapeException
	{
		checkBounds(coord);
		return pieces.get(coord);
	}


	@Override
	public void putPieceAt(EscapePiece p, OrthoSquareCoordinate c) throws EscapeException
	{
		if(checkBlocked(c)) throw new EscapeException("Cannot put piece at BLOCKED locaiton");
		checkBounds(c);
		pieces.put(c, p);
	}
	
	public void setLocationType(OrthoSquareCoordinate c, LocationType lt) throws EscapeException
	{
		checkBounds(c);
		squares.put(c, lt);
	}
	
	public LocationType getLocationType(OrthoSquareCoordinate c) throws EscapeException
	{
		checkBounds(c);
		return squares.get(c);
	}
	
	public void checkBounds(OrthoSquareCoordinate c) throws EscapeException {
		int x = c.getX();
		int y = c.getY();
		if(x < 1 || x > xMax || y < 1 || y > yMax) {
			throw new EscapeException("Coordinate Input Is Out of Range");
		}
	}
	
	//Cannot place piece at a blocked location
	public boolean checkBlocked(OrthoSquareCoordinate c) {
		LocationType curr = getLocationType(c);
		if(curr != null) {
			if(getLocationType(c).equals(LocationType.BLOCK)) return true;
		}
		return false;
	}

	@Override
	public CoordinateID getBoardType() {
		return CoordinateID.ORTHOSQUARE;
	}

	@Override
	public OrthoSquareCoordinate makeProperCoordinate(int x, int y) throws EscapeException{
		return OrthoSquareCoordinate.makeCoordinate(x, y);
	}
}
