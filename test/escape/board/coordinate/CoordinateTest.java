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

package escape.board.coordinate;

import static escape.board.coordinate.CoordinateID.HEX;
import static escape.board.coordinate.CoordinateID.ORTHOSQUARE;
import static escape.board.coordinate.CoordinateID.SQUARE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import escape.exception.EscapeException;

/**
 * Tests for various coordinates
 * @version Mar 28, 2020
 */
class CoordinateTest
{
    @ParameterizedTest
    @MethodSource("distanceTestProvider")
    void distanceTests(String n, Coordinate c1, Coordinate c2, int expected)
    {
        assertEquals(n, expected, c1.distanceTo(c2));
    }
   
    static Stream<Arguments> distanceTestProvider()
    {
        return Stream.of(
        	// Square coordinates
            Arguments.of("#1", mc(SQUARE, 4, 4), mc(SQUARE, 5, 4), 1),
            Arguments.of("#2", mc(SQUARE, 1, 1), mc(SQUARE, 3, 1), 2),
            Arguments.of("#3", mc(SQUARE, 4, 4), mc(SQUARE, 3, 4), 1),
            Arguments.of("#4", mc(SQUARE, 4, 4), mc(SQUARE, 4, 3), 1),
            Arguments.of("#5", mc(SQUARE, 4, 4), mc(SQUARE, 4, 5), 1),
            Arguments.of("#6", mc(SQUARE, 4, 4), mc(SQUARE, 5, 5), 1),
            Arguments.of("#7", mc(SQUARE, 4, 4), mc(SQUARE, 5, 3), 1),
            Arguments.of("#8", mc(SQUARE, 4, 4), mc(SQUARE, 3, 3), 1),
            Arguments.of("#9", mc(SQUARE, 4, 4), mc(SQUARE, 3, 5), 1),
            Arguments.of("#10", mc(SQUARE, 5, 4), mc(SQUARE, 25, 5), 20),
            Arguments.of("#11", mc(SQUARE, 25, 5), mc(SQUARE, 5, 4), 20),
            Arguments.of("#12", mc(SQUARE, 25, 5), mc(SQUARE, 25, 5), 0),
            Arguments.of("#13", mc(SQUARE, 5, 4), mc(SQUARE, 25, 4), 20),
            Arguments.of("#14", mc(SQUARE, 5, 4), mc(SQUARE, 1, 6), 4),
            Arguments.of("#25", mc(SQUARE, -5, 4), mc(SQUARE, 4, -6), 10),
            
            // OrthoSquare coordinates
            Arguments.of("#15", mc(ORTHOSQUARE, 4, 4), mc(ORTHOSQUARE, 5, 4), 1),
            Arguments.of("#16", mc(ORTHOSQUARE, 6, 6), mc(ORTHOSQUARE, 4, 4), 4),
            Arguments.of("#17", mc(ORTHOSQUARE, 6, 6), mc(ORTHOSQUARE, 8, 4), 4),
            Arguments.of("#18", mc(ORTHOSQUARE, 6, 6), mc(ORTHOSQUARE, 4, 8), 4),
            Arguments.of("#19", mc(ORTHOSQUARE, 6, 6), mc(ORTHOSQUARE, 8, 8), 4),
            Arguments.of("#20", mc(ORTHOSQUARE, 6, 6), mc(ORTHOSQUARE, 6, 6), 0),
            Arguments.of("#26", mc(ORTHOSQUARE, -6, 6), mc(ORTHOSQUARE, 6, -2), 20),
            
            // Hex coordinates
            Arguments.of("#21", mc(HEX, 0, 0), mc(HEX, 0, 1), 1),
            Arguments.of("#22", mc(HEX, -3, -1), mc(HEX, 1, 2), 7),
            Arguments.of("#23", mc(HEX, -4, 5), mc(HEX, 4, -3), 8),
            Arguments.of("#24", mc(HEX, -4, 5), mc(HEX, 4, -4), 9)
        );
    }
    
    /**
     * Helper to make a coordinate
     * @param type the type of coordinate
     * @param x
     * @param y
     * @return the coordinate instance
     */
    private static Coordinate mc(CoordinateID type, int x, int y)
    {
        return type == SQUARE ? SquareCoordinate.makeCoordinate(x, y)
        	: type == ORTHOSQUARE ? OrthoSquareCoordinate.makeCoordinate(x, y)
        	: HexCoordinate.makeCoordinate(x, y);
    }
	
	//Simple SquareCoordinate build
    @Test
    void buildSquareCoordinate() {
    	SquareCoordinate testSqCoord = SquareCoordinate.makeCoordinate(1, 1);
    	assertNotNull(testSqCoord);
    }
        
    @ParameterizedTest
    @MethodSource("testBadSquareCoordinates")
    void testFalseSquareCoordBuild(int x, int y) throws EscapeException {
    	assertThrows(EscapeException.class, () -> {
    		SquareCoordinate.makeCoordinate(x, y);
    	});
    }
    
    static Stream<Arguments> testBadSquareCoordinates() {
    	return Stream.of(
    			Arguments.of(0, 1),
    			Arguments.of(1,0),
    			Arguments.of(-1, 1),
    			Arguments.of(1, -1),
    			Arguments.of(0, 0));
    }
    
    @Test
    void testSquareCoordinateEquals() {
    	SquareCoordinate goodCoord = SquareCoordinate.makeCoordinate(1, 1);
    	SquareCoordinate gooderCoord = SquareCoordinate.makeCoordinate(1, 1);
    	SquareCoordinate badCoord = SquareCoordinate.makeCoordinate(1, 2);
    	assertTrue(goodCoord.equals(goodCoord));
    	assertTrue(goodCoord.equals(gooderCoord));
    	assertFalse(goodCoord.equals(badCoord));
    }
    
    
    
    @ParameterizedTest
    @MethodSource("squareCoordDistances")
    void squareCoordinateDistanceToTests(int fromX, int fromY, int toX, int toY, int result) {
    	SquareCoordinate from = SquareCoordinate.makeCoordinate(fromX, fromY);
    	SquareCoordinate to = SquareCoordinate.makeCoordinate(toX, toY);
    	assertEquals(from.distanceTo(to),result);
    }
    
    //Using examples he gave us
    static Stream<Arguments> squareCoordDistances() {
    	return Stream.of(
    			Arguments.of(1, 1, 2, 2, 1),
    			Arguments.of(1, 2, 3, 5, 3),
    			Arguments.of(5, 5, 7, 5, 2));
    }
    /*
     * Beginning of OrthoSquare tests
     */
    @Test
    void buildOrthoSquareCoordinate() {
    	OrthoSquareCoordinate testOrthoSquareCoord = OrthoSquareCoordinate.makeCoordinate(1, 1);
    	assertNotNull(testOrthoSquareCoord);
    }
    
    //Test what the 
    @ParameterizedTest
    @MethodSource("testBadSquareCoordinates")
    void testFalseOrthoSquareCoordBuild(int x, int y) throws EscapeException {
    	assertThrows(EscapeException.class, () -> {
    		OrthoSquareCoordinate.makeCoordinate(x, y);
    	});
    }
   
    
    @Test
    void testOrthoSquareCoordinateEquals() {
    	OrthoSquareCoordinate goodCoord = OrthoSquareCoordinate.makeCoordinate(1, 1);
    	OrthoSquareCoordinate gooderCoord = OrthoSquareCoordinate.makeCoordinate(1, 1);
    	OrthoSquareCoordinate badCoord = OrthoSquareCoordinate.makeCoordinate(1, 2);
    	assertTrue(goodCoord.equals(goodCoord));
    	assertTrue(goodCoord.equals(gooderCoord));
    	assertFalse(goodCoord.equals(badCoord));
    }
    
    @ParameterizedTest
    @MethodSource("OrthoSquareCoordDistances")
    void OrthoSquareCoordinateDistanceToTests(int fromX, int fromY, int toX, int toY, int result) {
    	OrthoSquareCoordinate from = OrthoSquareCoordinate.makeCoordinate(fromX, fromY);
    	OrthoSquareCoordinate to = OrthoSquareCoordinate.makeCoordinate(toX, toY);
    	assertEquals(from.distanceTo(to),result);
    }

    //Using examples he gave us
    static Stream<Arguments> OrthoSquareCoordDistances() {
    	return Stream.of(
    			Arguments.of(1, 1, 2, 2, 2),
    			Arguments.of(1, 2, 3, 5, 5),
                Arguments.of(5, 5, 3, 4, 3),
                Arguments.of(5, 5, 4, 3, 3));
    }
    
    /*
     * Beginning of Hex testsSt
     */
    
    @ParameterizedTest
    @MethodSource("HexCoordDistances")
    void HexCoordinateDistanceToTests(int fromX, int fromY, int toX, int toY, int result) {
    	HexCoordinate from = HexCoordinate.makeCoordinate(fromX, fromY);
    	HexCoordinate to = HexCoordinate.makeCoordinate(toX, toY);
    	assertEquals(from.distanceTo(to),result);
    }
	// Standard hex coordinates
	// The distance from (0,0) -> (-1, 2) is 2, (-1, 2) -> (2, -2) is 4.
    //Using examples he gave us
    static Stream<Arguments> HexCoordDistances() {
    	return Stream.of(
    			Arguments.of(0, 0, -1, 2, 2),
    			Arguments.of(-1, 2, 2, -2, 4),
    			Arguments.of(0, 0, 2, -2, 2),
    			Arguments.of(0, 0, -2, 2, 2));
    }
}
