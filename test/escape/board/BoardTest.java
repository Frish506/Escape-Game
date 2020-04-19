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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.File;
import org.junit.jupiter.api.Test;

import escape.board.coordinate.SquareCoordinate;
import escape.piece.EscapePiece;
import escape.piece.PieceName;
import escape.piece.Player;

/**
 * Description
 * @version Apr 2, 2020
 */
class BoardTest
{
	//assertThrows(EscapeException.class, () -> {//Code that should throw errors}
	
	@Test
	void makeSquareBoard() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/BoardConfig1.xml"));
		SquareBoard theBoard = (SquareBoard) bb.makeBoard();
		assertNotNull(theBoard);
	}
	
	@Test
	void testSquareBoardPlacementAndRetrieve() throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		EscapePiece player1Piece = new EscapePiece(Player.PLAYER1, PieceName.FOX);
		SquareCoordinate location = SquareCoordinate.makeCoordinate(1, 1);
		theBoard.putPieceAt(player1Piece, location);
		
		EscapePiece retrievedPiece = theBoard.getPieceAt(location);
		assertTrue(retrievedPiece.getName().equals(PieceName.FOX));
		assertTrue(retrievedPiece.getPlayer().equals(Player.PLAYER1));
	}
	
	
	
	/*
	 * Beginning of OrthoSquareBoard tests
	 */
	
	@Test
	void makeOrthoSquareBoard() throws Exception {
		BoardBuilder osb = new BoardBuilder(new File("config/board/BoardConfig2.xml"));
		OrthoSquareBoard theBoard = (OrthoSquareBoard) osb.makeBoard();
		assertNotNull(theBoard);
	}
	
	/*
	 * Beginning of HexBoard tests
	 */
	
	@Test
	void makeHexBoard() throws Exception {
		BoardBuilder hb = new BoardBuilder(new File("config/board/BoardConfig3.xml"));
		HexBoard theBoard = (HexBoard) hb.makeBoard();
		assertNotNull(theBoard);
	}
	
}
