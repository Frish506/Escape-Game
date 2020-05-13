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

package escape.game;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import escape.EscapeGameBuilder;
import escape.EscapeGameManager;
import escape.GameManager;
import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.PieceName;
import escape.util.PieceTypeInitializer;


/**
 * Description
 * @version Apr 24, 2020
 */
class BetaEscapeGameTests
{
    
    /**
     * Example of how the game manager tests will be structured.
     * @throws Exception
     */
    @Test
    void testBadGameInitializer1() throws Exception
    {
        EscapeGameBuilder egb 
            = new EscapeGameBuilder(new File("config/BrokenEscapeGame1.xml"));
        assertThrows(EscapeException.class, () -> {
        	EscapeGameManager emg = egb.makeGameManager();
		});
        // Exercise the game now: make moves, check the board, etc.
    }
    
    @Test
    void testBadGameInitializer2() throws Exception
    {
        EscapeGameBuilder egb 
            = new EscapeGameBuilder(new File("config/BrokenEscapeGame2.xml"));
        assertThrows(EscapeException.class, () -> {
        	EscapeGameManager emg = egb.makeGameManager();
		});
        // Exercise the game now: make moves, check the board, etc.
    }
    
    @ParameterizedTest
    @MethodSource("gameFileFeeder")
    void testGoodGameInitializer(File f) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(f);
    	EscapeGameManager emg = egb.makeGameManager();
    }
    
    //Using examples he gave us
    static Stream<Arguments> gameFileFeeder() {
    	return Stream.of(
    			Arguments.of(new File("config/WorkingSquareGame.xml")),
    			Arguments.of(new File("config/WorkingHexGame.xml")),
                Arguments.of(new File("config/WorkingOrthoSquareGame.xml")));
    }
    
    @ParameterizedTest
    @MethodSource("linearOrthoSquareMovementDestinations")
    void testLinearOrthoSquareMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingOrthoSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	OrthoSquareCoordinate fr = OrthoSquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	OrthoSquareCoordinate to = OrthoSquareCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    }
    

    static Stream<Arguments> linearOrthoSquareMovementDestinations() {
    	return Stream.of(
    			Arguments.of(5, 2),
    			Arguments.of(5, 8),
                Arguments.of(6, 5),
                Arguments.of(3, 5));
    }
    
    @ParameterizedTest
    @MethodSource("failedLinearOrthoSquareMovementDestinations")
    void testFailedLinearOrthoSquareMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingOrthoSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	OrthoSquareCoordinate fr = OrthoSquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	emg.putPieceAt(OrthoSquareCoordinate.makeCoordinate(7, 5), PieceName.HORSE);
    	OrthoSquareCoordinate to = OrthoSquareCoordinate.makeCoordinate(x, y);
    	assertFalse(emg.move(fr, to));
    }
    

    static Stream<Arguments> failedLinearOrthoSquareMovementDestinations() {
    	return Stream.of(
    			Arguments.of(6, 2),
    			Arguments.of(4, 8),
                Arguments.of(8, 5), //Trying to jump over a piece
                Arguments.of(2, 5), //Trying to get through a blocked space
                Arguments.of(1, 1));
    }
    
    @ParameterizedTest
    @MethodSource("nonlinearOrthoSquareMovementDestinations")
    void nonLinearOrthoSquareMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingOrthoSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	OrthoSquareCoordinate fr = OrthoSquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.FOX);
    	OrthoSquareCoordinate to = OrthoSquareCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    }
    

    static Stream<Arguments> nonlinearOrthoSquareMovementDestinations() {
    	return Stream.of(
    			Arguments.of(4, 4),
    			Arguments.of(6, 6),
                Arguments.of(6, 5),
                Arguments.of(3, 5));
    }
    
    @Test
    void testBlockedSpace() throws Exception { //Test pieces that don't have unblock can't go through blocked spaces
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.FROG);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(5, 7);
    	assertFalse(emg.move(fr, to));
    }
    
    @Test
    void testUnblockingSpace() throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(5, 7);
    	assertTrue(emg.move(fr, to));
    }
    
    @Test
    void testDoubleJumpFail() throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/SquareJumpTestGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.FROG);
    	emg.putPieceAt(SquareCoordinate.makeCoordinate(5, 6), PieceName.FROG);
    	emg.putPieceAt(SquareCoordinate.makeCoordinate(5, 7), PieceName.FROG);
    	emg.putPieceAt(SquareCoordinate.makeCoordinate(6, 5), PieceName.FROG);
    	SquareCoordinate successfulJump = SquareCoordinate.makeCoordinate(7, 5);
    	SquareCoordinate unsuccessfulDoubleJump = SquareCoordinate.makeCoordinate(5, 8);
    	assertTrue(emg.move(fr, successfulJump));
    	assertFalse(emg.move(fr, unsuccessfulDoubleJump));
    }
    
    @ParameterizedTest
    @MethodSource("linearSquareMovementDestinations")
    void testLinearSquareMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(5, 5);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    }
    
    //Using examples he gave us
    static Stream<Arguments> linearSquareMovementDestinations() {
    	return Stream.of(
    			Arguments.of(1, 1),
    			Arguments.of(3, 3),
                Arguments.of(8, 8),
                Arguments.of(2, 5));
    }
    
    @ParameterizedTest
    @MethodSource("squareOmniMovementDestinations")
    void testSquareOmniMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingSquareGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(1, 1);
    	emg.putPieceAt(fr, PieceName.FOX);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    	
    }
    
    //Using examples he gave us
    static Stream<Arguments> squareOmniMovementDestinations() {
    	return Stream.of(
    			Arguments.of(2, 2),
    			Arguments.of(3, 5),
                Arguments.of(3, 4),
                Arguments.of(3, 3),
                Arguments.of(6, 6));
    }
    

    
    @ParameterizedTest
    @MethodSource("hexLinearMovementDestinations")
    void testHexLinearMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingHexGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	HexCoordinate fr = HexCoordinate.makeCoordinate(0, 0);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	HexCoordinate to = HexCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    	
    }
    
    //Using examples he gave us
    static Stream<Arguments> hexLinearMovementDestinations() {
    	return Stream.of(
    			Arguments.of(1, -1),
    			Arguments.of(-1, 1),
                Arguments.of(0, 2),
                Arguments.of(2, 0),
                Arguments.of(4, 0));
    }
    
    @ParameterizedTest
    @MethodSource("hexLinearFailedMovementDestinations")
    void testHexLinearFailedMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingHexGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	HexCoordinate fr = HexCoordinate.makeCoordinate(0, 0);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	HexCoordinate to = HexCoordinate.makeCoordinate(x, y);
    	assertFalse(emg.move(fr, to));
    	
    }
    
    //Using examples he gave us
    static Stream<Arguments> hexLinearFailedMovementDestinations() {
    	return Stream.of(
    			Arguments.of(1, 1),
    			Arguments.of(-1, -1),
                Arguments.of(8, 2),
                Arguments.of(2, 8),
                Arguments.of(2, 1));
    }
    
    @ParameterizedTest
    @MethodSource("hexOmniMovementDestinations")
    void testHexOmnirMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingHexGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	HexCoordinate fr = HexCoordinate.makeCoordinate(0, 0);
    	emg.putPieceAt(fr, PieceName.FOX);
    	HexCoordinate to = HexCoordinate.makeCoordinate(x, y);
    	assertTrue(emg.move(fr, to));
    	
    }
    
    //Using examples he gave us
    static Stream<Arguments> hexOmniMovementDestinations() {
    	return Stream.of(
    			Arguments.of(1, -1),
    			Arguments.of(-1, -1),
                Arguments.of(3, 2),
                Arguments.of(2, 3),
                Arguments.of(1, 0));
    }
    
    @ParameterizedTest
    @MethodSource("hexOmniFailedMovementDestinations")
    void testHexOmniFailedMovement(int x, int y) throws Exception {
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WorkingHexGame.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	HexCoordinate fr = HexCoordinate.makeCoordinate(0, 0);
    	emg.putPieceAt(fr, PieceName.HORSE);
    	HexCoordinate to = HexCoordinate.makeCoordinate(x, y);
    	assertFalse(emg.move(fr, to));
    	
    }
    
    //Using examples he gave us
    static Stream<Arguments> hexOmniFailedMovementDestinations() {
    	return Stream.of(
    			Arguments.of(4, 1),
                Arguments.of(8, 2),
                Arguments.of(2, 8));
    }
    
    @ParameterizedTest
    @MethodSource("wildinAnimals")
    void testLongShot(PieceName animal) throws Exception{
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WildinBoard.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(1, 1);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(8, 8);
    	emg.putPieceAt(fr, animal);
    	assertTrue(emg.move(fr, to));
    }
    
    static Stream<Arguments> wildinAnimals() {
    	return Stream.of(
    			Arguments.of(PieceName.FOX),
    			Arguments.of(PieceName.HUMMINGBIRD));
    }
    

    @Test
    void testBadLongShot() throws Exception{
        EscapeGameBuilder egb 
        = new EscapeGameBuilder(new File("config/WildinBadBoard.xml"));
    	GameManager emg = (GameManager) egb.makeGameManager();
    	SquareCoordinate fr = SquareCoordinate.makeCoordinate(8, 4);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(8, 8);
    	emg.putPieceAt(fr, PieceName.FOX);
    	assertFalse(emg.move(fr, to));
    }

    
    

    
    
}
