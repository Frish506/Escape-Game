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

import static escape.board.LocationType.CLEAR;
import java.io.*;
import javax.xml.bind.*;

import escape.board.coordinate.Coordinate;
import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.piece.EscapePiece;
import escape.util.*;

/**
 * A Builder class for creating Boards. It is only an example and builds
 * just Square boards. If you choose to use this
 * @version Apr 2, 2020
 */
public class BoardBuilder
{
	private EscapeGameInitializer gi;
	private BoardInitializer bi;
	/**
	 * The constructor for this takes a file name. It is either an absolute path
	 * or a path relative to the beginning of this project.
	 * @param fileName
	 * @throws Exception 
	 */
	
	public BoardBuilder(File fileName) throws Exception
	{
		JAXBContext contextObj = JAXBContext.newInstance(BoardInitializer.class);
        Unmarshaller mub = contextObj.createUnmarshaller();
        bi = (BoardInitializer)mub.unmarshal(new FileReader(fileName));
	}
	
	public BoardBuilder(EscapeGameInitializer egi) {
		gi = egi;
	}
	
	@SuppressWarnings("rawtypes")
	public StandardBoard makeBoard()
	{
		CoordinateID currId = null;
		StandardBoard board = null;
		switch(gi.getCoordinateType()) { //Check what kind of board it is, then make that board
			case SQUARE:
				board = new SquareBoard(gi.getxMax(), gi.getyMax());
				currId = CoordinateID.SQUARE;
				break;
			case ORTHOSQUARE: 
				board = new OrthoSquareBoard(gi.getxMax(), gi.getyMax());
				currId = CoordinateID.ORTHOSQUARE;
				break;
			case HEX:
				board = new HexBoard(gi.getxMax(), gi.getyMax());
				currId = CoordinateID.HEX;
				break;
		}
		
		if(gi.getCoordinateType() == null) return null; //If for any reason the creation doesn't work out, this will return null, something the game master should check for
        initializeBoard(board, currId, gi.getLocationInitializers());
        return board;
	}
	
	//Universal for all boards, implemented using the StandardBoard interface which extends Board
	private void initializeBoard(StandardBoard b, CoordinateID id, LocationInitializer... initializers)
	{
		if(initializers != null) {
			for (LocationInitializer li : initializers) {
				Coordinate c = null;
				switch(id) { //Need to know the kind of board to make the correct coordinate
					case SQUARE:
						c = SquareCoordinate.makeCoordinate(li.x, li.y);
						break;
					case ORTHOSQUARE:
						c = OrthoSquareCoordinate.makeCoordinate(li.x, li.y);
						break;
					case HEX:
						c = HexCoordinate.makeCoordinate(li.x, li.y);
						break;
				}
				if (li.pieceName != null) {
					b.putPieceAt(new EscapePiece(li.player, li.pieceName), c);
				}
				
				if (li.locationType != null && li.locationType != CLEAR) {
					b.setLocationType(c, li.locationType);
				}
			}
		}
	}
}
