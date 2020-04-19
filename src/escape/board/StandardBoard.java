package escape.board;

import escape.board.coordinate.Coordinate;

/**
 * 
 * @author henry
 * Provides the standard setLocationType method that should
 * be standard on every standard escape game board
 */
public interface StandardBoard<C extends Coordinate> extends Board<C> {	
	
	//Set the location type at the coordinate
	public void setLocationType(C coord, LocationType lt);
}
