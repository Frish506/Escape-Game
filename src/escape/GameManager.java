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
import escape.piece.PieceName;
import escape.piece.Player;
import escape.rule.Rule;
import escape.util.PieceTypeInitializer;

public class GameManager implements EscapeGameManager<BetterCoordinate> {
	
	private StandardBoard theBoard;
	private HashMap<PieceName, PieceTypeInitializer> pieceTypes = new HashMap<PieceName, PieceTypeInitializer>();
	private ArrayList<GameObserver> observers = new ArrayList<GameObserver>();
	
	public GameManager(StandardBoard board, PieceTypeInitializer[] types, Rule[] rules) {
		theBoard = board;
		for(PieceTypeInitializer pti : types) {
			if(pieceTypes.get(pti.getPieceName()) != null) throw new EscapeException("Can't have multiple pieces of the same type");
			pieceTypes.put(pti.getPieceName(), pti);
		}
	
	}
	
	@Override
	public boolean move(BetterCoordinate from, BetterCoordinate to) {
		if(from.getX() == to.getX() && from.getY() == to.getY()) return false; //Can't go to where you began
		EscapePiece movingPiece = theBoard.getPieceAt(from);
		if(movingPiece != null) {
			PieceName movingPieceName = movingPiece.getName();
			PieceTypeInitializer pti = pieceTypes.get(movingPieceName);
			if (MovementRules.initialCheck(theBoard, from, to) &&
					MovementRules.basicDistanceCheck(pti, theBoard, from, to) &&
					(MovementRules.checkFly(pti, theBoard, from, to) ||
					MovementRules.checkMovement(pti, theBoard, from, to)
					) ) {
				if(!(theBoard.getLocationType(to).equals(LocationType.EXIT)) ) { //If it lands on an Exit, leave
					theBoard.putPieceAt(movingPiece, to);
				}
				theBoard.removePiece(from);
				return true;
			};
		}

		return false;
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
	
	@Override
	public GameObserver addObserver(GameObserver observer)
	{
	    observers.add(observer);
		return observer;
	}
	
	@Override
	public GameObserver removeObserver(GameObserver observer)
	{
	    if(observers.remove(observer)) return observer;
	    return null;
	}
	
	

}
