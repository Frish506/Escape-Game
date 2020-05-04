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

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.piece.PieceName;
import escape.piece.Player;

/**
 * Description
 * @version Apr 2, 2020
 */
class BoardTest
{	
	@Test
	void makeSquareBoard() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoardConfig1.xml"));
		SquareBoard theBoard = (SquareBoard) bb.makeBoard();
		assertNotNull(theBoard);
	}

	@ParameterizedTest
	@MethodSource("ValidFiniteSquareBoardBounds")
	void testValidSquareBoardBounds(int x, int y) throws Exception {
		assertNotNull(new SquareBoard(x, y));
	}

	static Stream<Arguments> ValidFiniteSquareBoardBounds() {
		return Stream.of(
				Arguments.of(5, 5),
				Arguments.of(1, 1),
				Arguments.of(20,1),
				Arguments.of(1, 20),
				Arguments.of(20, 20),
				Arguments.of(1, 3),
				Arguments.of(1, 500),
				Arguments.of(500, 1));
	}

	@ParameterizedTest
	@MethodSource("InvalidFiniteSquareBoardBounds")
	void testInvalidSquareBoardBounds(int x, int y) throws Exception {
		assertThrows(EscapeException.class, () -> {
			new SquareBoard(x, y);
		});
	}

	static Stream<Arguments> InvalidFiniteSquareBoardBounds() {
		return Stream.of(
				Arguments.of(0, 2),
				Arguments.of(2, 0),
				Arguments.of(-1,2),
				Arguments.of(2, -1),
				Arguments.of(0, 0),
				Arguments.of(-1,-1),
				Arguments.of(-5, -5));
	}

	@ParameterizedTest
	@MethodSource("GoodFiniteSquareBoardCoordinatePlacements")
	void testSquareBoardPiecePlacementAndRetrieve(int x, int y) throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		EscapePiece player1Piece = new EscapePiece(Player.PLAYER1, PieceName.FOX);
		SquareCoordinate location = SquareCoordinate.makeCoordinate(x, y);
		theBoard.putPieceAt(player1Piece, location);

		EscapePiece retrievedPiece = theBoard.getPieceAt(location);
		assertTrue(retrievedPiece.getName().equals(PieceName.FOX));
		assertTrue(retrievedPiece.getPlayer().equals(Player.PLAYER1));
	}

	@ParameterizedTest
	@MethodSource("GoodFiniteSquareBoardCoordinatePlacements")
	void testSquareBoardLocationPlacementAndRetrieval(int x, int y) throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		SquareCoordinate locationCoord = SquareCoordinate.makeCoordinate(x, y);
		theBoard.setLocationType(locationCoord, LocationType.EXIT);

		LocationType shouldBeExit = theBoard.getLocationType(locationCoord);
		assertTrue(shouldBeExit.equals(LocationType.EXIT));
	}

	static Stream<Arguments> GoodFiniteSquareBoardCoordinatePlacements() {
		return Stream.of(
				Arguments.of(1, 2),
				Arguments.of(2, 1),
				Arguments.of(8, 3),
				Arguments.of(3, 8),
				Arguments.of(4, 5),
				Arguments.of(5, 4));
	}


	@Test
	void testBlockedSquarePiecePlacementFromConfigFile() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/SquareBoardConfig1.xml"));
		SquareBoard theBoard = (SquareBoard) bb.makeBoard();
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, SquareCoordinate.makeCoordinate(3, 5));
		});
	}

	@Test
	void testBlockedSquarePiecePlacementFromMake() throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		SquareCoordinate blockedCoord = SquareCoordinate.makeCoordinate(3, 3);
		theBoard.setLocationType(blockedCoord, LocationType.BLOCK);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, blockedCoord);
		});
	}

	@ParameterizedTest
	@MethodSource("BadFiniteSquareBoardCoordinatePlacements")
	void testBadSquarePiecePlacement(int x, int y) throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		SquareCoordinate badCoordinate = SquareCoordinate.makeCoordinate(x, y);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, badCoordinate);
		});
	}

	@ParameterizedTest
	@MethodSource("BadFiniteSquareBoardCoordinatePlacements")
	void testBadSquarePieceRetrieval(int x, int y) throws Exception {
		SquareBoard theBoard = new SquareBoard(8, 8);
		SquareCoordinate badCoordinate = SquareCoordinate.makeCoordinate(x, y);
		assertThrows(EscapeException.class, () -> {
			theBoard.getPieceAt(badCoordinate);
		});
	}

	static Stream<Arguments> BadFiniteSquareBoardCoordinatePlacements() {
		return Stream.of(
				Arguments.of(9, 2),
				Arguments.of(2, 9),
				Arguments.of(9,9),
				Arguments.of(10, 10));
	}


	/*
	 * Beginning of OrthoSquareBoard tests
	 */

	@Test
	void makeOrthoSquareBoard() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/OrthoSquareBoardConfig1.xml"));
		OrthoSquareBoard theBoard = (OrthoSquareBoard) bb.makeBoard();
		assertNotNull(theBoard);
	}

	@ParameterizedTest
	@MethodSource("ValidFiniteSquareBoardBounds")
	void testValidOrthoSquareBoardBounds(int x, int y) throws Exception {
		assertNotNull(new SquareBoard(x, y));
	}

	@ParameterizedTest
	@MethodSource("InvalidFiniteSquareBoardBounds")
	void testInvalidOrthoSquareBoardBounds(int x, int y) throws Exception {
		assertThrows(EscapeException.class, () -> {
			new OrthoSquareBoard(x, y);
		});
	}


	@ParameterizedTest
	@MethodSource("GoodFiniteSquareBoardCoordinatePlacements")
	void testOrthoSquareBoardPiecePlacementAndRetrieve(int x, int y) throws Exception {
		OrthoSquareBoard theBoard = new OrthoSquareBoard(8, 8);
		EscapePiece player1Piece = new EscapePiece(Player.PLAYER1, PieceName.FOX);
		OrthoSquareCoordinate location = OrthoSquareCoordinate.makeCoordinate(x, y);
		theBoard.putPieceAt(player1Piece, location);

		EscapePiece retrievedPiece = theBoard.getPieceAt(location);
		assertTrue(retrievedPiece.getName().equals(PieceName.FOX));
		assertTrue(retrievedPiece.getPlayer().equals(Player.PLAYER1));
	}

	@ParameterizedTest
	@MethodSource("GoodFiniteSquareBoardCoordinatePlacements")
	void testOrthoSquareBoardLocationPlacementAndRetrieval(int x, int y) throws Exception {
		OrthoSquareBoard theBoard = new OrthoSquareBoard(8, 8);
		OrthoSquareCoordinate locationCoord = OrthoSquareCoordinate.makeCoordinate(x, y);
		theBoard.setLocationType(locationCoord, LocationType.EXIT);

		LocationType shouldBeExit = theBoard.getLocationType(locationCoord);
		assertTrue(shouldBeExit.equals(LocationType.EXIT));
	}

	@Test
	void testBlockedOrthoSquarePiecePlacementFromConfigFile() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/OrthoSquareBoardConfig1.xml"));
		OrthoSquareBoard theBoard = (OrthoSquareBoard) bb.makeBoard();
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, OrthoSquareCoordinate.makeCoordinate(3, 5));
		});
	}

	@Test
	void testBlockedOrthoSquarePiecePlacementFromMake() throws Exception {
		OrthoSquareBoard theBoard = new OrthoSquareBoard(8, 8);
		OrthoSquareCoordinate blockedCoord = OrthoSquareCoordinate.makeCoordinate(3, 3);
		theBoard.setLocationType(blockedCoord, LocationType.BLOCK);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, blockedCoord);
		});
	}

	@ParameterizedTest
	@MethodSource("BadFiniteSquareBoardCoordinatePlacements")
	void testOrthoSquareBadPiecePlacement(int x, int y) throws Exception {
		OrthoSquareBoard theBoard = new OrthoSquareBoard(8, 8);
		OrthoSquareCoordinate badCoordinate = OrthoSquareCoordinate.makeCoordinate(x, y);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, badCoordinate);
		});
	}

	@ParameterizedTest
	@MethodSource("BadFiniteSquareBoardCoordinatePlacements")
	void testOrthoSquareBadPieceRetrieval(int x, int y) throws Exception {
		OrthoSquareBoard theBoard = new OrthoSquareBoard(8, 8);
		OrthoSquareCoordinate badCoordinate = OrthoSquareCoordinate.makeCoordinate(x, y);
		assertThrows(EscapeException.class, () -> {
			theBoard.getPieceAt(badCoordinate);
		});
	}

	/*
	 * Beginning of HexBoard tests
	 */

	/* Beginning of finite HexBoard tests */

	@Test
	void makeFiniteHexBoard() throws Exception {
		BoardBuilder hb = new BoardBuilder(new File("config/board/HexBoardConfig1.xml"));
		HexBoard theBoard = (HexBoard) hb.makeBoard();
		assertNotNull(theBoard);
	}
	
	@ParameterizedTest
	@MethodSource("ValidFiniteSquareBoardBounds")
	void testValidhexBoardBounds(int x, int y) throws Exception {
		assertNotNull(new HexBoard(x, y));
	}
	
	@ParameterizedTest
	@MethodSource("GoodFiniteHexBoardCoordinatePlacements")
	void testHexBoardPiecePlacementAndRetrieve(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(8, 8);
		EscapePiece player1Piece = new EscapePiece(Player.PLAYER1, PieceName.FOX);
		HexCoordinate location = HexCoordinate.makeCoordinate(x, y);
		theBoard.putPieceAt(player1Piece, location);

		EscapePiece retrievedPiece = theBoard.getPieceAt(location);
		assertTrue(retrievedPiece.getName().equals(PieceName.FOX));
		assertTrue(retrievedPiece.getPlayer().equals(Player.PLAYER1));
	}

	@ParameterizedTest
	@MethodSource("GoodFiniteHexBoardCoordinatePlacements")
	void testHexBoardLocationPlacementAndRetrieval(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(8, 8);
		HexCoordinate locationCoord = HexCoordinate.makeCoordinate(x, y);
		theBoard.setLocationType(locationCoord, LocationType.EXIT);

		LocationType shouldBeExit = theBoard.getLocationType(locationCoord);
		assertTrue(shouldBeExit.equals(LocationType.EXIT));
	}
	
	static Stream<Arguments> GoodFiniteHexBoardCoordinatePlacements() {
		return Stream.of(
				Arguments.of(1, 2),
				Arguments.of(2, 1),
				Arguments.of(8, 3),
				Arguments.of(3, 8),
				Arguments.of(4, 5),
				Arguments.of(5, 4),
				Arguments.of(0, 2),
				Arguments.of(2, 0),
				Arguments.of(-1,2),
				Arguments.of(2, -1),
				Arguments.of(0, 0),
				Arguments.of(-1,-1),
				Arguments.of(-5, -5));
	}

	@Test
	void testBlockedHexPiecePlacementFromConfigFile() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoardConfig1.xml"));
		HexBoard theBoard = (HexBoard) bb.makeBoard();
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, HexCoordinate.makeCoordinate(3, 5));
		});
	}

	@Test
	void testBlockedHexPiecePlacementFromMake() throws Exception {
		HexBoard theBoard = new HexBoard(8, 8);
		HexCoordinate blockedCoord = HexCoordinate.makeCoordinate(3, -3);
		theBoard.setLocationType(blockedCoord, LocationType.BLOCK);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, blockedCoord);
		});
	}

	@ParameterizedTest
	@MethodSource("BadHexCoordinatePlacements")
	void testBadHexPiecePlacement(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(8, 8);
		HexCoordinate badCoordinate = HexCoordinate.makeCoordinate(x, y);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, badCoordinate);
		});
	}

	@ParameterizedTest
	@MethodSource("BadHexCoordinatePlacements")
	void testBadHexPieceRetrieval(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(8, 8);
		HexCoordinate badCoordinate = HexCoordinate.makeCoordinate(x, y);
		assertThrows(EscapeException.class, () -> {
			theBoard.getPieceAt(badCoordinate);
		});
	}

	static Stream<Arguments> BadHexCoordinatePlacements() {
		return Stream.of(
				Arguments.of(9, 2),
				Arguments.of(2, 9),
				Arguments.of(9,9),
				Arguments.of(10, 10),
				Arguments.of(-9, 2),
				Arguments.of(2, -9),
				Arguments.of(-9, -9),
				Arguments.of(-10, -10));
	}

	/* Beginning of Infinite Hex Board tests */

	@Test
	void makeInfiniteHexBoardFromFile() throws Exception {
		BoardBuilder hb = new BoardBuilder(new File("config/board/HexBoardConfig2.xml"));
		HexBoard theBoard = (HexBoard) hb.makeBoard();
		assertNotNull(theBoard);
	}
	
	@Test
	void makeInfiniteHexBoard() throws Exception {
		assertNotNull(new HexBoard(0, 0));
	}
	
	@ParameterizedTest
	@MethodSource("InfiniteHexBoardCoordinatePlacements")
	void testInfiniteHexBoardPiecePlacementAndRetrieve(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(0, 0);
		EscapePiece player1Piece = new EscapePiece(Player.PLAYER1, PieceName.FOX);
		HexCoordinate location = HexCoordinate.makeCoordinate(x, y);
		theBoard.putPieceAt(player1Piece, location);

		EscapePiece retrievedPiece = theBoard.getPieceAt(location);
		assertTrue(retrievedPiece.getName().equals(PieceName.FOX));
		assertTrue(retrievedPiece.getPlayer().equals(Player.PLAYER1));
	}

	@ParameterizedTest
	@MethodSource("InfiniteHexBoardCoordinatePlacements")
	void testInfiniteHexBoardLocationPlacementAndRetrieval(int x, int y) throws Exception {
		HexBoard theBoard = new HexBoard(0, 0);
		HexCoordinate locationCoord = HexCoordinate.makeCoordinate(x, y);
		theBoard.setLocationType(locationCoord, LocationType.EXIT);

		LocationType shouldBeExit = theBoard.getLocationType(locationCoord);
		assertTrue(shouldBeExit.equals(LocationType.EXIT));
	}
	
	static Stream<Arguments> InfiniteHexBoardCoordinatePlacements() {
		return Stream.of(
				Arguments.of(1, 2),
				Arguments.of(2, 1),
				Arguments.of(8, 3),
				Arguments.of(3, 8),
				Arguments.of(4, 5),
				Arguments.of(5, 4),
				Arguments.of(0, 2),
				Arguments.of(2, 0),
				Arguments.of(-1,2),
				Arguments.of(2, -1),
				Arguments.of(0, 0),
				Arguments.of(-1,-1),
				Arguments.of(-5, -5),
				Arguments.of(9, 2),
				Arguments.of(2, 9),
				Arguments.of(9,9),
				Arguments.of(10, 10),
				Arguments.of(-9, 2),
				Arguments.of(2, -9),
				Arguments.of(-9, -9),
				Arguments.of(-10, -10),
				Arguments.of(1000, 1),
				Arguments.of(1, 1000),
				Arguments.of(1000, 1000),
				Arguments.of(298483, -1234712),
				Arguments.of(0, 123023),
				Arguments.of(1231519, 0));
	}
	
	@Test
	void testBlockedInfiniteHexPiecePlacementFromConfigFile() throws Exception {
		BoardBuilder bb = new BoardBuilder(new File("config/board/HexBoardConfig2.xml"));
		HexBoard theBoard = (HexBoard) bb.makeBoard();
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, HexCoordinate.makeCoordinate(3, 5));
		});
	}

	@Test
	void testBlockedInfiniteHexPiecePlacementFromMake() throws Exception {
		HexBoard theBoard = new HexBoard(0, 0);
		HexCoordinate blockedCoord = HexCoordinate.makeCoordinate(-3, 300);
		theBoard.setLocationType(blockedCoord, LocationType.BLOCK);
		EscapePiece testPut = EscapePiece.makePiece(Player.PLAYER1, PieceName.FOX);
		assertThrows(EscapeException.class, () -> {
			theBoard.putPieceAt(testPut, blockedCoord);
		});
	}

}
