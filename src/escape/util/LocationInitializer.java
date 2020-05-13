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
package escape.util;

import escape.board.LocationType;
import escape.piece.*;

/**
 * A general initializer for a board location. Since this is used
 * internally, we take a shortcut and make the instance variables
 * public rather than 
 * @version Mar 30, 2020
 */
public class LocationInitializer
{
	public int x, y;
	public LocationType locationType;
	public Player player;
	public PieceName pieceName;
	
	public LocationInitializer() 
	{
	    // needed for JAXB unmarshalling
	}
}
