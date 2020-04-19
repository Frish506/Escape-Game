package escape.board;

import java.util.HashMap;
import java.util.Map;

import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

public class HexBoard implements StandardBoard<HexCoordinate> {
	Map<HexCoordinate, LocationType> squares;
	Map<HexCoordinate, EscapePiece> pieces;
	
	private final int xRange, yRange;
	private boolean xFinite, yFinite = true;
	
	public HexBoard(int x, int y) {
		this.xRange = x;
		this.yRange = y;
		if(x==0) xFinite = false;
		if(y==0) yFinite = false;
		pieces = new HashMap<HexCoordinate, EscapePiece>();
		squares = new HashMap<HexCoordinate, LocationType>();
		
	}
	@Override
	public EscapePiece getPieceAt(HexCoordinate coord) throws EscapeException
	{
		checkBounds(coord);
		return pieces.get(coord);
	}


	@Override
	public void putPieceAt(EscapePiece p, HexCoordinate coord) throws EscapeException
	{
		checkBounds(coord);
		pieces.put(coord, p);
	}
	
	public void setLocationType(HexCoordinate c, LocationType lt) throws EscapeException
	{
		checkBounds(c);
		squares.put(c, lt);
	}
	
	public void checkBounds(HexCoordinate coord) throws EscapeException {
		if(xFinite && yFinite) {
			int x = coord.getX();
			int y = coord.getY();
			if(x < -xRange || x > xRange || y < -yRange || y > yRange) {
				throw new EscapeException("Coordinate Input Is Out of Range");
			}
		}
	}

}
