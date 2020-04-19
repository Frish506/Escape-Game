package escape.board;

import java.util.HashMap;
import java.util.Map;

import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

public class OrthoSquareBoard implements StandardBoard<OrthoSquareCoordinate> {

	Map<OrthoSquareCoordinate, LocationType> squares;
	Map<OrthoSquareCoordinate, EscapePiece> pieces;
	
	private final int xMax, yMax;
	public OrthoSquareBoard(int xMax, int yMax)
	{
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
	public void putPieceAt(EscapePiece p, OrthoSquareCoordinate coord) throws EscapeException
	{
		checkBounds(coord);
		pieces.put(coord, p);
	}
	
	public void setLocationType(OrthoSquareCoordinate c, LocationType lt) throws EscapeException
	{
		checkBounds(c);
		squares.put(c, lt);
	}
	
	public void checkBounds(OrthoSquareCoordinate coord) throws EscapeException {
		int x = coord.getX();
		int y = coord.getY();
		if(x < 1 || x > xMax || y < 1 || y > yMax) {
			throw new EscapeException("Coordinate Input Is Out of Range");
		}
	}
}
