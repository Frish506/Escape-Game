package escape.game;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import escape.EscapeGameBuilder;
import escape.EscapeGameManager;
import escape.GameManager;
import escape.board.coordinate.Coordinate;
import escape.piece.Player;

public class FinalEscapeGameRuleTests {
	// EXIT (6,9)
	//frog (5,7) == frog (8,7)
	//frog (5,6) > horse (8,6)
	//fox (8,5) < hummingbird (5,5)
    EscapeGameManager game;
    Coordinate strong1, weak1, strong2, weak2, equal1, equal2, exit, random;
	
    @BeforeEach
    void setup() throws Exception
    {
        EscapeGameBuilder egb = new EscapeGameBuilder(new File("config/rulesBoards/RulesTest.xml"));
        game = egb.makeGameManager();
        strong1 = game.makeCoordinate(5, 6);
        weak2 = game.makeCoordinate(8, 6);
        strong2 = game.makeCoordinate(8, 5);
        weak1 = game.makeCoordinate(5, 5);
        equal1 = game.makeCoordinate(5,7);
        equal2 = game.makeCoordinate(8, 7);
        exit = game.makeCoordinate(6, 9);
        random = game.makeCoordinate(1, 1);
    }
        
    @Test
    void testEqualCollision() throws Exception {
    	game.move(strong1, strong2);
    	assertNull(game.getPieceAt(strong1));
    	assertNull(game.getPieceAt(strong2));
    }
    
    @Test
    void testPlayer1CollisionWin() throws Exception {
    	game.move(strong1, weak2);
    	assertTrue(game.getPieceAt(weak2).getPlayer().equals(Player.PLAYER1));
    }
    
    @Test
    void testPlayerCollision2Win() throws Exception {
    	game.move(strong1, weak2);
    	game.move(strong2, weak1);
    	assertTrue(game.getPieceAt(weak1).getPlayer().equals(Player.PLAYER2));
    }
    
    @Test
    void wrongPlayer() throws Exception {
    	assertFalse(game.move(weak2, exit)); //Can't move as player 2 if it's player 1's turn
    	assertTrue(game.move(strong1, weak2));
    	assertFalse(game.move(weak1, exit)); //Vice versa
    }
    
    @Test
    void player1Win() throws Exception {
    	game.move(strong1, exit);
    	assertFalse(game.move(weak2, exit));
    }
    
    @Test
    void player2Win() throws Exception {
    	game.move(weak1, exit);
    	game.move(strong2, exit);
    	assertFalse(game.move(strong1, exit));
    }
    
    @Test
    void timeExhaustPlayer1() throws Exception {
    	System.out.println("\nTime Exhaust Player 1 Win Test");
    	game.move(weak1, exit);
    	game.move(strong2, weak1);
    	game.move(equal1, equal2);
    	game.move(weak1, random); //MOVING PLAYER 2 PIECE
    	assertFalse(game.move(equal2, exit)); //DONT BE FOOLED I'm moving a player 1 piece
    }
    
    @Test
    void timeExhaustPlayer2() throws Exception {
    	System.out.println("\nTime Exhaust Player 2 Win Test");
    	game.move(equal1, equal2);
    	game.move(weak2, exit);
    	game.move(strong1, weak2);
    	game.move(strong2, random); //MOVING PLAYER 1 PIECE
    	assertFalse(game.move(equal2, exit)); //DONT BE FOOLED I'm moving a player 2 piece
    }
    
    @Test
    void timeExhaustTie() throws Exception {
    	System.out.println("\nTie Test:");
    	game.move(strong1, weak2);
    	game.move(strong2, weak1);
    	game.move(equal1, equal2);
    	game.move(weak1, random); //Moving player 2 piece
    	assertFalse(game.move(equal2, exit)); //Moving player 1 pice
    }
    
    @Test
    void testObserverRemoval() throws Exception {
    	game.removeObserver(((GameManager)game).getObserver());
    }
}
