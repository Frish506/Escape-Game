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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
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
	//Simple SquareCoordinate build
    @Test
    void buildSquareCoordinate() {
    	SquareCoordinate testSqCoord = SquareCoordinate.makeCoordinate(1, 1);
    	assertNotNull(testSqCoord);
    }
    
	//assertThrows(EscapeException.class, () -> {//Code that should throw errors}
    
    @ParameterizedTest
    @MethodSource("testBadSquareCoordinates")
    void testFalseSquareCoordBuild(int x, int y) throws EscapeException {
    	assertThrows(EscapeException.class, () -> {
    		SquareCoordinate falseSqCoord = SquareCoordinate.makeCoordinate(x, y);
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
    			Arguments.of(1, 2, 3, 5, 3));
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
    		OrthoSquareCoordinate falseSqCoord = OrthoSquareCoordinate.makeCoordinate(x, y);
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
    			Arguments.of(1, 2, 3, 5, 5));
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
    			Arguments.of(-1, 2, 2, -2, 4));
    }
}
