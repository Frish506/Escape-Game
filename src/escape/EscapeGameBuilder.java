/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2020 Gary F. Pollice
 *******************************************************************************/

package escape;

import java.io.*;
import javax.xml.bind.*;

import escape.board.BoardBuilder;
import escape.board.StandardBoard;
import escape.board.coordinate.CoordinateID;
import escape.exception.EscapeException;
import escape.piece.MovementPatternID;
import escape.piece.PieceAttributeID;
import escape.util.EscapeGameInitializer;
import escape.util.PieceTypeInitializer;
import escape.util.PieceTypeInitializer.PieceAttribute;

/**
 * This class is what a client will use to creat an instance of a game, given
 * an Escape game configuration file. The configuration file contains the 
 * information needed to create an instance of the Escape game.
 * @version Apr 22, 2020
 */
public class EscapeGameBuilder
{
    private EscapeGameInitializer gameInitializer;
    
    /**
     * The constructor takes a file that points to the Escape game
     * configuration file. It should get the necessary information 
     * to be ready to create the game manager specified by the configuration
     * file and other configuration files that it links to.
     * @param fileName the file for the Escape game configuration file.
     * @throws Exception 
     */
    public EscapeGameBuilder(File fileName) throws Exception
    {
        JAXBContext contextObj = JAXBContext.newInstance(EscapeGameInitializer.class);
        Unmarshaller mub = contextObj.createUnmarshaller();
        gameInitializer = 
            (EscapeGameInitializer)mub.unmarshal(new FileReader(fileName));
    }
    
    /**
     * Once the builder is constructed, this method creates the
     * EscapeGameManager instance.
     * @return
     */
    public EscapeGameManager makeGameManager() throws EscapeException
    {
        BoardBuilder boardMaker = new BoardBuilder(gameInitializer);
        StandardBoard theBoard = boardMaker.makeBoard();
        checkPieceTypes(theBoard, gameInitializer.getPieceTypes());
        return new GameManager(theBoard, gameInitializer.getPieceTypes(), gameInitializer.getRules());
    }
    
    public void checkPieceTypes(StandardBoard theBoard, PieceTypeInitializer[] types) throws EscapeException {
    	for(PieceTypeInitializer pti : types) { //Cycle through the pieces
    		//Check if it at least has FLY or DISTANCE
    		boolean flyOrDist = false;
    		for(PieceAttribute pa : pti.getAttributes()) {
    			if(pa.getId().equals(PieceAttributeID.DISTANCE) || pa.getId().equals(PieceAttributeID.FLY)) {
    				if(pa.getIntValue() < 1) throw new EscapeException("Gotta have a good int value for movement bruh");
    				if(flyOrDist) throw new EscapeException("Can't have multiple movement attributes");
    				flyOrDist = true;
    			}
    			
    		}
    		if(!flyOrDist) throw new EscapeException("Bruh each piece needs to have either a fly or distance attribute you FOOL");
    		CoordinateID boardType = theBoard.getBoardType();
    		switch(pti.getMovementPattern()) { //Check if the piece cannot be on the board
				case DIAGONAL:
					if(!boardType.equals(CoordinateID.SQUARE)) throw new EscapeException("Diagonally moving piece can only be on square board");
					break;
				case ORTHOGONAL:
					if(boardType.equals(CoordinateID.HEX)) throw new EscapeException("Can't have an orthogonally moving piece on a hex board");
					break;
				default: //Linear and Omni can be on any board
					break; 
    		
    		}
    	}
    }
}
