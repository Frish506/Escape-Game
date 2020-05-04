/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Copyright ©2016-2020 Gary F. Pollice
 *******************************************************************************/
package escape.board;

import java.util.*;

import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;

/**
 * An example of how a Board might be implemented. This board has
 * square coordinates and finite bounds, represented by xMax and yMax.
 * All methods required by the Board interface have been implemented. Students
 * would naturally add methods based upon theire design.
 * @version Apr 2, 2020
 */
public class SquareBoard implements StandardBoard<SquareCoordinate>
{
	Map<SquareCoordinate, LocationType> squares;
	Map<SquareCoordinate, EscapePiece> pieces;
	
	private final int xMax, yMax;
	public SquareBoard(int xMax, int yMax) throws EscapeException
	{
		if(xMax < 1 || yMax < 1) { throw new EscapeException("Cannot make board with x or y smaller than 1"); }
		this.xMax = xMax;
		this.yMax = yMax;
		pieces = new HashMap<SquareCoordinate, EscapePiece>();
		squares = new HashMap<SquareCoordinate, LocationType>();
	}
	
	/*
	 * @see escape.board.Board#getPieceAt(escape.board.coordinate.Coordinate)
	 */
	@Override
	public EscapePiece getPieceAt(SquareCoordinate c) throws EscapeException
	{
		checkBounds(c);
		return pieces.get(c);
	}


	@Override
	public void putPieceAt(EscapePiece p, SquareCoordinate c) throws EscapeException
	{
		if(checkBlocked(c)) throw new EscapeException("Cannot put piece at BLOCKED locaiton");
		checkBounds(c);
		pieces.put(c, p);
	}
	
	public void setLocationType(SquareCoordinate c, LocationType lt) throws EscapeException
	{
		checkBounds(c);
		squares.put(c, lt);
	}
	
	public LocationType getLocationType(SquareCoordinate c) throws EscapeException
	{
		checkBounds(c);
		return squares.get(c);
	}
	
	public void checkBounds(SquareCoordinate c) throws EscapeException {
		int x = c.getX();
		int y = c.getY();
		if(x < 1 || x > xMax || y < 1 || y > yMax) {
			throw new EscapeException("Coordinate Input Is Out of Range");
		}
	}
	
	//Cannot place piece at a blocked location
	public boolean checkBlocked(SquareCoordinate c) {
		LocationType curr = getLocationType(c);
		if(curr != null) {
			if(getLocationType(c).equals(LocationType.BLOCK)) return true;
		}
		return false;
	}

	@Override
	public CoordinateID getBoardType() {
		return CoordinateID.SQUARE;
	}

	@Override
	public SquareCoordinate makeProperCoordinate(int x, int y) throws EscapeException {
		return SquareCoordinate.makeCoordinate(x, y);
	}
}
