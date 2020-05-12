package escape.board;

import java.util.HashMap;
import java.util.Map;

import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.HexCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

public class HexBoard implements StandardBoard<HexCoordinate> {
	Map<HexCoordinate, LocationType> squares;
	Map<HexCoordinate, EscapePiece> pieces;
	
	private final int xRange, yRange;
	private boolean xFinite = true;
	private boolean yFinite = true;
	
	public HexBoard(int x, int y) {
		this.xRange = Math.abs(x);
		this.yRange = Math.abs(y);
		if(x==0) xFinite = false;
		if(y==0) yFinite = false;
		pieces = new HashMap<HexCoordinate, EscapePiece>();
		squares = new HashMap<HexCoordinate, LocationType>();
		
		initializeClears();
	}
	
	public void initializeClears() {
		if(xFinite && yFinite) {
			for(int x=(xRange*-1); x<=xRange; x++) {
				for(int y=(yRange*-1); y<=yRange; y++) {
					HexCoordinate clearCoord = HexCoordinate.makeCoordinate(x, y);
					squares.put(clearCoord, LocationType.CLEAR);
				}
			}
		}
	}
	
	public void setSpaceAsClear(HexCoordinate c) {

			if(squares.get(c) == null) {
				squares.put(c, LocationType.CLEAR);
			}
		
	}
	
	@Override
	public EscapePiece getPieceAt(HexCoordinate c) throws EscapeException
	{
		checkBounds(c);
		setSpaceAsClear(c);
		return pieces.get(c);
	}


	@Override
	public void putPieceAt(EscapePiece p, HexCoordinate c) throws EscapeException
	{
		if(checkBlocked(c)) throw new EscapeException("Cannot put piece at BLOCKED locaiton");
		checkBounds(c);
		setSpaceAsClear(c);
		pieces.put(c, p);
	}
	
	public void setLocationType(HexCoordinate c, LocationType lt) throws EscapeException
	{
		checkBounds(c);
		squares.put(c, lt);
	}
	
	public LocationType getLocationType(HexCoordinate c) throws EscapeException
	{
		checkBounds(c);
		setSpaceAsClear(c);
		return squares.get(c);
	}
	
	//If the x or y are finite, the board has bounds. Otherwise they can be anywhere
	public void checkBounds(HexCoordinate c) throws EscapeException {
		int x = c.getX();
		int y = c.getY();
		if(xFinite && (x < -xRange || x > xRange)) { //Test if x input is out of range
				throw new EscapeException("Coordinate Input Is Out of Range");
		}
		if(yFinite && (y < -yRange || y > yRange)) { //Test if y input is out of range
				throw new EscapeException("Coordinate Input Is Out of Range");
		}
	}
	
	//Cannot place piece at a blocked location
	public boolean checkBlocked(HexCoordinate c) {
		LocationType curr = getLocationType(c);
		if(curr != null) {
			if(getLocationType(c).equals(LocationType.BLOCK)) return true;
		}
		return false;
	}
	@Override
	public CoordinateID getBoardType() {
		return CoordinateID.HEX;
	}
	@Override
	public HexCoordinate makeProperCoordinate(int x, int y) throws EscapeException {
		return HexCoordinate.makeCoordinate(x, y);
	}

	@Override
	public void removePiece(HexCoordinate c) throws EscapeException {
		checkBounds(c);
		pieces.remove(c);
	}

}
