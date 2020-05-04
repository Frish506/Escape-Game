package escape;

import java.util.HashMap;

import escape.board.SquareBoard;
import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.SquareCoordinate;
import escape.piece.EscapePiece;
import escape.piece.PieceName;
import escape.util.PieceTypeInitializer;

public class SquareGameManager implements EscapeGameManager<SquareCoordinate> {
	
	private SquareBoard theBoard;
	private HashMap<PieceName, PieceTypeInitializer> pieceTypes = new HashMap<PieceName, PieceTypeInitializer>();
	
	public SquareGameManager(SquareBoard board, PieceTypeInitializer[] types) {
		theBoard = board;
		for(PieceTypeInitializer pti : types) {
			pieceTypes.put(pti.getPieceName(), pti);
		}
	
	}
	
	@Override
	public boolean move(SquareCoordinate from, SquareCoordinate to) {
		return ()
		return false;
	}

	@Override
	public EscapePiece getPieceAt(SquareCoordinate coordinate) {
		return theBoard.getPieceAt(coordinate);
	}

	@Override
	public SquareCoordinate makeCoordinate(int x, int y) {
		return SquareCoordinate.makeCoordinate(x, y);
	}

}
