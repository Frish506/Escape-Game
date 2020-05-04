package escape.board;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.CoordinateID;

/**
 * 
 * @author henry
 * Provides the standard setLocationType method that should
 * be standard on every standard escape game board
 */
public interface StandardBoard<C extends Coordinate> extends Board<C> {	
	
	//Set the location type at the coordinate
	public void setLocationType(C coord, LocationType lt);
	
	public LocationType getLocationType(C coord);
	
	public C makeProperCoordinate(int x, int y);
	
	public CoordinateID getBoardType();
}
