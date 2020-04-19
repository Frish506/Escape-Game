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
package escape.board.coordinate;

import java.util.Objects;

import escape.exception.EscapeException;

/**
 * This is an example of how a SquareCoordinate might be organized.
 * 
 * @version Mar 27, 2020
 */
public class SquareCoordinate implements Coordinate {
	private final int x;
	private final int y;

	private SquareCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static SquareCoordinate makeCoordinate(int x, int y) throws EscapeException
    {
    	if(x<1 || y<1) { throw new EscapeException("Square Coordinates must have x&y coordinates of 1 or above"); }
    	return new SquareCoordinate(x, y);
    }

	//Implements simple Chebyshev distance (Max of abs(from.x - to.x) & abs(from.y - to.y) )
	@Override
	public int distanceTo(Coordinate c) throws EscapeException {
		if (!(c instanceof SquareCoordinate)) {
			throw new EscapeException("Cannot get distance from SquareCoordinate to a different coordinate type");
		}
		SquareCoordinate to = (SquareCoordinate) c;
		int absX = Math.abs(this.getX() - to.getX());
		int absY = Math.abs(this.getY() - to.getY());
		return Math.max(absX, absY);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}


	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SquareCoordinate)) {
			return false;
		}
		SquareCoordinate other = (SquareCoordinate) obj;
		return x == other.getX() && y == other.getY();
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
