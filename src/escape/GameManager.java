package escape;

import java.util.ArrayList;
import java.util.HashMap;

import escape.board.LocationType;
import escape.board.StandardBoard;
import escape.board.coordinate.BetterCoordinate;
import escape.board.coordinate.HexCoordinate;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.board.coordinate.SquareCoordinate;
import escape.exception.EscapeException;
import escape.piece.EscapePiece;
import escape.piece.PieceAttributeID;
import escape.piece.PieceName;
import escape.piece.Player;
import escape.rule.Rule;
import escape.rule.RuleID;
import escape.util.PieceTypeInitializer;
import escape.util.PieceTypeInitializer.PieceAttribute;

public class GameManager implements EscapeGameManager<BetterCoordinate> {
	
	private StandardBoard theBoard;
	private HashMap<PieceName, PieceTypeInitializer> pieceTypes = new HashMap<PieceName, PieceTypeInitializer>(); 
	public EscapeObserver theObserver = null;
	
	//Storing all the rules in variables rather than a hashmap, because checking if both the rule exists by checking it's id enum with a value in the hashmap and stuff and its value is much more code than just checking a variable
	private int turnLimit = Integer.MAX_VALUE;
	private int scoreLimit = Integer.MAX_VALUE - 1;
	private boolean remove = false;
	private boolean pointConflict = false;
	private int turn = 0;
	private int playerOneScore = 0;
	private int playerTwoScore = 0;
	public boolean playerOneTurn = true;
	public boolean gameEnded = false;
	private String endMessage = null;
	
	
	public GameManager(StandardBoard board, PieceTypeInitializer[] types, Rule[] rules) {
		theBoard = board;
		this.addObserver(new EscapeObserver());
		
		for(PieceTypeInitializer pti : types) {
			if(pieceTypes.get(pti.getPieceName()) != null) throw new EscapeException("Can't have multiple pieces of the same type");
			pieceTypes.put(pti.getPieceName(), pti);
		}
		
		if(rules != null) { //Technically rules are optional, and the older tests were made before rules were a thing
			for(Rule r: rules) {
				switch(r.getId()) {
					case POINT_CONFLICT:
						pointConflict = true;
						break;
					case REMOVE:
						remove = true;
						break;
					case SCORE:
						scoreLimit = r.getIntValue();
						break;
					case TURN_LIMIT:
						turnLimit = r.getIntValue();
				}
			}
		}
	
	}
	
	@Override
	public boolean move(BetterCoordinate from, BetterCoordinate to) {
		if(!gameEnded) { //Can't make a move if the game has ended 
			try {
				if(from.getX() == to.getX() && from.getY() == to.getY()) { //Can't go to where you began
					theObserver.notify("Cannot move to spot you are already on you silly willy!");
					return false; 
				}
				EscapePiece movingPiece = theBoard.getPieceAt(from);
				
				int movingPieceVal = -1;
				if(movingPiece != null) {
					movingPieceVal = getPieceValue(movingPiece.getName()); //If it's null this is checked and caught in initialCheck, but it's neater to return false then
					if(!playerOneTurn && movingPiece.getPlayer().equals(Player.PLAYER1)) throw new EscapeException("Can't move a PLAYER1 piece as player 2");
					if(playerOneTurn && movingPiece.getPlayer().equals(Player.PLAYER2)) throw new EscapeException("Can't move a PLAYER2 piece as player 1");
				}

				
				EscapePiece endPiece = theBoard.getPieceAt(to);
				int endPieceVal = -1;
				if(endPiece != null) endPieceVal = getPieceValue(endPiece.getName());
				
				if(!remove && endPiece!=null) { //If you can't remove, you can't collide with a piece
					theObserver.notify("Trying to land on a piece but the REMOVE rule is false for this game");
					return false; 
				}
				if(movingPiece != null) {
					PieceName movingPieceName = movingPiece.getName();
					PieceTypeInitializer pti = pieceTypes.get(movingPieceName);
					if (MovementRules.initialCheck(theBoard, from, to) &&
							MovementRules.basicDistanceCheck(pti, theBoard, from, to) &&
							(MovementRules.checkFly(pti, theBoard, from, to) ||
							MovementRules.checkMovement(pti, theBoard, from, to))) {
						
						if(theBoard.getLocationType(to).equals(LocationType.EXIT)) { //If it's an exit, just add the value but don't place a piece there
							if(playerOneTurn) playerOneScore += movingPieceVal;
							else playerTwoScore += movingPieceVal;
						}
						else if(endPiece != null && pointConflict) { //There's a piece at the end. We already tested if we could take it or not. Now we know it's there, it's an enemy piece, we can move there, and we can theoretically remove it
							//This check only matters if there's a point conflict. If not, it'll just put the piece at the end and that will automatically remove the end piece
							if(movingPieceVal > endPieceVal) theBoard.putPieceAt(movingPiece, to); //If they moving piece is more than the end piece, it gets to go there
							if(movingPieceVal == endPieceVal) theBoard.removePiece(to);
							//If it's less than or equal to it, it doesn't get to the dest and gets destroyed. Destroyed code is a few lines down and happens regardless
						}
						else theBoard.putPieceAt(movingPiece, to);
						theBoard.removePiece(from); //We've placed the piece, so now remove it
						
						if(!playerOneTurn) turn++;
						playerOneTurn = !playerOneTurn;
						testEnd();
						if(gameEnded) theObserver.notify(endMessage);
						return true;
					}
				}
			}
			catch (EscapeException ee) {
				theObserver.notify(ee.getMessage(), ee.getCause());
				return false;
			}
		}
		else {
			theObserver.notify("Game is over and " + endMessage);
		}
		
		
		return false;
	}
	
	public void testEnd() {
		if(turn >= turnLimit) {
			gameEnded = true;
			if(playerOneScore > playerTwoScore) {
				endMessage = "PLAYER1 wins";
			}
			else if (playerTwoScore > playerOneScore) {
				endMessage = "PLAYER2 wins";
			}
			else endMessage = "It's a tie!";
		}
		
		if(playerOneScore >= scoreLimit) {
			gameEnded = true;
			endMessage = "PLAYER1 wins!";
		}
		if(playerTwoScore >= scoreLimit) {
			gameEnded = true;
			endMessage = "PLAYER2 wins";
		}
	}
	

	@Override
	public EscapePiece getPieceAt(BetterCoordinate coordinate) {
		return theBoard.getPieceAt(coordinate);
	}
	
	public void putPieceAt(BetterCoordinate coordinate, PieceName name) throws EscapeException {
		if(pieceTypes.get(name) == null) throw new EscapeException("That piece type doesn't exist on this board");
		EscapePiece putter = EscapePiece.makePiece(Player.PLAYER1, name);
		theBoard.putPieceAt(putter, coordinate);
	}

	@Override
	public BetterCoordinate makeCoordinate(int x, int y) {
		switch(theBoard.getBoardType()) {
		case HEX:
			return HexCoordinate.makeCoordinate(x, y);
		case ORTHOSQUARE:
			return OrthoSquareCoordinate.makeCoordinate(x, y); 
		case SQUARE:
			return SquareCoordinate.makeCoordinate(x, y);
		default:
			return null;
		
		}
	}
	
	public int getPieceValue(PieceName thePiece) {
		PieceTypeInitializer pti = pieceTypes.get(thePiece);
		
		for(PieceAttribute pa: pti.getAttributes()) {
			PieceAttributeID thisPaId = pa.getId();
			if(thisPaId.equals(PieceAttributeID.VALUE)) {
				return pa.getIntValue();
			}
		}
		return 0;
	}
	
	@Override
	public GameObserver addObserver(GameObserver observer)
	{
	   	theObserver = (EscapeObserver) observer;
		return theObserver;
	}
	
	@Override
	public GameObserver removeObserver(GameObserver observer)
	{
	    if(theObserver != null) return observer;
	    return null;
	}
	
	
	public GameObserver getObserver() {
		return theObserver;
	}
}
