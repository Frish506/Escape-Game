package escape;

import java.util.ArrayList;

import escape.board.LocationType;
import escape.board.StandardBoard;
import escape.board.coordinate.BetterCoordinate;
import escape.board.coordinate.Coordinate;
import escape.board.coordinate.CoordinateID;
import escape.board.coordinate.OrthoSquareCoordinate;
import escape.piece.EscapePiece;
import escape.piece.MovementPatternID;
import escape.piece.PieceAttributeID;
import escape.util.PieceTypeInitializer;
import escape.util.PieceTypeInitializer.PieceAttribute;

@SuppressWarnings("unchecked")
public class MovementRules {
	
	public static PieceAttribute getMovementAttribute(PieceTypeInitializer pti) { //This returns the PieceAttribute of either Distance or Fly //TODO Move this method?
		for(PieceAttribute pa: pti.getAttributes()) {
			PieceAttributeID thisPaId = pa.getId();
			if(thisPaId.equals(PieceAttributeID.DISTANCE) || (thisPaId.equals(PieceAttributeID.FLY))) {
				return pa;
			}
		}
		return null;
	}
	
	public static PieceAttribute getUnblockAttribute(PieceTypeInitializer pti) {
		for(PieceAttribute pa: pti.getAttributes()) {
			PieceAttributeID thisPaId = pa.getId();
			if(thisPaId.equals(PieceAttributeID.UNBLOCK)) {
				return pa;
			}
		}
		return null;
	}
	public static PieceAttribute getJumpAttribute(PieceTypeInitializer pti) {
		for(PieceAttribute pa: pti.getAttributes()) {
			PieceAttributeID thisPaId = pa.getId();
			if(thisPaId.equals(PieceAttributeID.JUMP)) {
				return pa;
			}
		}
		return null;
	}

	public static boolean initialCheck(StandardBoard theBoard, Coordinate from, Coordinate to) {
		EscapePiece startingPiece = theBoard.getPieceAt(from);
		if(startingPiece == null) return false; //If there's no piece at the starting location
		LocationType potentialBlock = theBoard.getLocationType(to);
		if(potentialBlock.equals(LocationType.BLOCK)) return false; //Can't end on blocked location
		EscapePiece potentialEndPiece = theBoard.getPieceAt(to);
		if(potentialEndPiece != null && potentialEndPiece.getPlayer().equals(startingPiece.getPlayer())) return false; //Can't end on a piece of the same type
		return true;
	}
	
	public static boolean basicDistanceCheck(PieceTypeInitializer pti, StandardBoard theBoard, BetterCoordinate from, BetterCoordinate to) { //Check if the basic distance isn't too far
		PieceAttribute movementAttribute = getMovementAttribute(pti);
		int movementAmt = movementAttribute.getIntValue();
		
		if(from.distanceTo(to) > movementAmt) return false; //If the literal distance between the coordinates is too much in general, then nope
		if(pti.getMovementPattern().equals(MovementPatternID.ORTHOGONAL)) { //If orthogonal movement pattern on a regular square board, distance between square coordinates don't give accurate ability to travel. Need between orthosquare coordinates
			OrthoSquareCoordinate orthoFrom = OrthoSquareCoordinate.makeCoordinate(from.getX(), from.getY());
			OrthoSquareCoordinate orthoTo = OrthoSquareCoordinate.makeCoordinate(to.getX(), to.getY());
			if(orthoFrom.distanceTo(orthoTo) > movementAmt) return false;
		}
		return true;
	}
	
	public static boolean checkFly(PieceTypeInitializer pti, StandardBoard theBoard, BetterCoordinate from, BetterCoordinate to) { //If it can fly and isnt' linear, we've already checked distance, so yeah it can go
		PieceAttribute movementAttribute = getMovementAttribute(pti);
		if(movementAttribute.getId().equals(PieceAttributeID.FLY) && !(pti.getMovementPattern().equals(MovementPatternID.LINEAR))) return true;
		return false;
	}
	
	
	
	private static class Node {
		Node parent;
		int dist;
		int x, y;
		Node(Node parent, int dist, int x, int y) {
			this.parent = parent;
			this.dist = dist;
			this.x = x;
			this.y = y;
		}
	}
	
	public static ArrayList<Node> findDiagonalNeighbors(Node home, StandardBoard theBoard, boolean canJump, boolean canUnblock, BetterCoordinate dest) {
		ArrayList<Node> returner = new ArrayList<Node>();
		for(int x = -1; x<=1; x+=2) {
			for(int y=-1; y<=1; y+=2) {
				boolean inBounds = false;
				Coordinate c = theBoard.makeProperCoordinate(home.x + x, home.y + y);
				try { //Check if the coordinates are in bounds. Works for all 3 kinds of board
					theBoard.checkBounds(c);
					inBounds = true;
				}
				catch (Exception EscapeException) {}
				if(inBounds) { //If it's in bounds
					LocationType potentialUnpassable = theBoard.getLocationType(c);
					boolean reached = (dest.getX() == home.x + x && dest.getY() == home.y + y);
					if(potentialUnpassable.equals(LocationType.BLOCK) && !canUnblock) {} //If it can't pass through the unblock
					else if(potentialUnpassable.equals(LocationType.EXIT) && !reached) {} //Can't pass through an exit space
					else { //If either you can pass through the block, or there's just not a block there
						EscapePiece potentialPiece = theBoard.getPieceAt(c); //Get the theoretical piece
						if(potentialPiece != null && canJump && !reached) { //If there's a piece there and you can jump, check the following diagonal after the piece
							try {
								potentialPiece = theBoard.getPieceAt(theBoard.makeProperCoordinate(home.x + x + x, home.y + y + y)); //get next diagonal after the theoretical jump
								if(potentialPiece == null) { //There's not two pieces in a row, so add the spot
									Node corner = new Node(home, home.dist + 2, home.x + x + x, home.y + y + y);
									returner.add(corner);
								}
							}
							catch (Exception EscapeException) {}
						}
						else if (potentialPiece == null || reached) { //Otherwise, if there's not a piece there just get the spot. Or if it's the destination add that bish. No need to test if there is a piece there but you can't jump b/c you would just break
							Node corner = new Node(home, home.dist + 1, home.x + x, home.y + y); //Already tested if piece at destination is of same kind, so since we know it's not and we are at the dest, even though a piece is there we are good
							returner.add(corner);
						}
					}
				}
			}
		}
		return returner;
	}
	
	public static ArrayList<Node> findOrthogNeighbors(Node home, StandardBoard theBoard, boolean canJump, boolean canUnblock, BetterCoordinate dest) {
		ArrayList<Node> returner = new ArrayList<Node>();
		for(int x = -1; x<=1; x++) {
			for(int y=-1; y<=1; y++) {
				boolean inBounds = false;
				Coordinate c = theBoard.makeProperCoordinate(home.x + x, home.y + y);
				try { //Check if the coordinates are in bounds. Works for all 3 kinds of board
					theBoard.checkBounds(c);
					inBounds = true;
				}
				catch (Exception EscapeException) {}
				if(inBounds && (x==0 ^ y==0)) { //If it's in bounds, and either x or y is 0 but not both (because that's just the original spot)
					LocationType potentialUnpassable = theBoard.getLocationType(c);
					boolean reached = (dest.getX() == home.x + x && dest.getY() == home.y + y);
					if(potentialUnpassable.equals(LocationType.BLOCK) && !canUnblock) {} //If it can't pass through the unblock
					else if(potentialUnpassable.equals(LocationType.EXIT) && !reached) {} //Can't pass through an exit space
					else { //If either you can pass through the block, or there's just not a block there
						EscapePiece potentialPiece = theBoard.getPieceAt(c); //Get the theoretical piece
						if(potentialPiece != null && canJump && !reached) { //If there's a piece there and you can jump, check the following diagonal after the piece
							try {
								potentialPiece = theBoard.getPieceAt(theBoard.makeProperCoordinate(home.x + x + x, home.y + y + y)); //get next diagonal after the theoretical jump
								if(potentialPiece == null) { //There's not two pieces in a row, so add the spot
									Node corner = new Node(home, home.dist + 2, home.x + x + x, home.y + y + y);
									returner.add(corner);
								}
							}
							catch (Exception EscapeException) {}
						}
						else if (potentialPiece == null || reached) { //Otherwise, if there's not a piece there just get the spot. Or if it's the destination add that bish. No need to test if there is a piece there but you can't jump b/c you would just break
							Node corner = new Node(home, home.dist + 1, home.x + x, home.y + y); //Already tested if piece at destination is of same kind, so since we know it's not and we are at the dest, even though a piece is there we are good
							returner.add(corner);
						}
					}
				}
			}
		}
		return returner;
	}
	
	public static ArrayList<Node> findOmniNeighbors(Node home, StandardBoard theBoard, boolean canJump, boolean canUnblock, BetterCoordinate dest) { //Also works for hex
		ArrayList<Node> returner = new ArrayList<Node>();
		for(int x = -1; x<=1; x++) {
			for(int y=-1; y<=1; y++) {
				boolean inBounds = false;
				Coordinate c = theBoard.makeProperCoordinate(home.x + x, home.y + y);
				try { //Check if the coordinates are in bounds. Works for all 3 kinds of board
					theBoard.checkBounds(c);
					inBounds = true;
				}
				catch (Exception EscapeException) {}
				if(inBounds && (x!=0 || y!=0) && !(theBoard.getBoardType().equals(CoordinateID.HEX) && x==y)) { //If it's in bounds, and both x and y don't equal zero, and x and y aren't equal if it's a hex board
					LocationType potentialUnpassable = theBoard.getLocationType(c);
					boolean reached = (dest.getX() == home.x + x && dest.getY() == home.y + y);
					if(potentialUnpassable.equals(LocationType.BLOCK) && !canUnblock) {} //If it can't pass through the unblock
					else if(potentialUnpassable.equals(LocationType.EXIT) && !reached) {} //Can't pass through an exit space
					else { //If either you can pass through the block, or there's just not a block there
						EscapePiece potentialPiece = theBoard.getPieceAt(c); //Get the theoretical piece
						if(potentialPiece != null && canJump && !reached) { //If there's a piece there and you can jump and it's not the destination, check the following diagonal after the piece
							try {
								potentialPiece = theBoard.getPieceAt(theBoard.makeProperCoordinate(home.x + x + x, home.y + y + y)); //get next diagonal after the theoretical jump
								if(potentialPiece == null) { //There's not two pieces in a row, so add the spot
									Node corner = new Node(home, home.dist + 2, home.x + x + x, home.y + y + y);
									returner.add(corner);
								}
							}
							catch (Exception EscapeException) {}
						}
						else if (potentialPiece == null || reached) { //Otherwise, if there's not a piece there just get the spot. Or if it's the destination add that bish. No need to test if there is a piece there but you can't jump b/c you would just break
							Node corner = new Node(home, home.dist + 1, home.x + x, home.y + y); //Already tested if piece at destination is of same kind, so since we know it's not and we are at the dest, even though a piece is there we are good
							returner.add(corner);
						}
					}
				}
			}
		}
		return returner;
	}
	
	public static boolean checkLinearMovement(PieceTypeInitializer pti, StandardBoard theBoard, BetterCoordinate from, BetterCoordinate to) {
		//Check if on the path
		int xDif = Math.abs(to.getX() - from.getX());
		int yDif = Math.abs(to.getY() - from.getY());
		
		if(xDif != 0 && yDif != 0 && xDif!=yDif) return false; //Not left or right, up or down, or diagional
		
		if(to.getX() != from.getX() && to.getY() != from.getY()) { //Either not on path or on diagonal
			if(xDif == yDif && theBoard.getBoardType() == CoordinateID.ORTHOSQUARE) return false; //On a diagonal and it's an orthosquare board
		}
		
		
		
		//Now we know it's on the path- let's grab the piece's movement structure
		PieceAttribute movementAttribute = getMovementAttribute(pti);
		PieceAttributeID movementID = movementAttribute.getId();
		
		//Check if can fly
		if (movementID.equals(PieceAttributeID.FLY)) return true; //If it can just fly there then cool dog let that bish fly
		
		//Grab the attributes
		boolean canJump = false, canUnblock = false;
		
		if(getJumpAttribute(pti) != null) {
			canJump = getJumpAttribute(pti).isBooleanValue();
		}
		
		if(getUnblockAttribute(pti) != null) {
			canUnblock = getUnblockAttribute(pti).isBooleanValue();
		}
		
		//Get the direction she's agoin
		int xDir = Integer.compare(to.getX(), from.getX());
		int yDir = Integer.compare(to.getY(), from.getY());
		
		if(xDif != 0 && yDif != 0 && xDir == yDir && theBoard.getBoardType() == CoordinateID.HEX) return false; //Hex boards don't have up-right ordown-left diags, so if it's diagonal and either of those return false

		int nextX = from.getX() + xDir;
		int nextY = from.getY() + yDir;
		
		boolean justJumped = false; //Can't jump twice in a row
		
		
		while(nextX != to.getX() || nextY != to.getY()) { //Go toward dest- check if need to jump or there's a blocked spot
			
			if(theBoard.getPieceAt(theBoard.makeProperCoordinate(nextX, nextY)) != null) { //If there's a piece in the way, check if it can jump. If it's jumping twice in a row that's a no go
				if(!canJump) return false;
				if(justJumped) return false; //Can't jump twice in a row
				justJumped = true; //Let it jump
			}
			else justJumped = false; //If there's not a piece there, it's not gonna jump
			
			switch(theBoard.getLocationType(theBoard.makeProperCoordinate(nextX, nextY))) { //If there's a block or an exit then like that's no good
				case BLOCK: //If it has unblock, should pass right through this
					if(!canUnblock) return false;
					break;
				case EXIT: //If it's exit, it falls through so false
					return false;
				case CLEAR:
					break;
			}
			nextX += xDir;
			nextY += yDir;
		}
		return true;
	}

	
	public static boolean checkNonLinearMovement(PieceTypeInitializer pti, StandardBoard theBoard, BetterCoordinate from, BetterCoordinate to) {
		//Grab the attributes
		PieceAttribute movementAttribute = getMovementAttribute(pti);
		int movementAmt = movementAttribute.getIntValue();
		MovementPatternID moveStyle = pti.getMovementPattern();
		
		boolean canJump = false, canUnblock = false;
		if(getJumpAttribute(pti) != null) {
			canJump = getJumpAttribute(pti).isBooleanValue();
		}
		
		if(getUnblockAttribute(pti) != null) {
			canUnblock = getUnblockAttribute(pti).isBooleanValue();
		}
		Node start = new Node(null, 0, from.getX(), from.getY());
		ArrayList<Node> neighbors = new ArrayList<Node>();
		neighbors.add(start);
		while(neighbors.size() > 0) {
			Node n = neighbors.get(0);
			ArrayList<Node> newNeighbors = null;
			switch(moveStyle) {
			case DIAGONAL: newNeighbors = findDiagonalNeighbors(n, theBoard, canJump, canUnblock, to); //Grab the next neighbors
				break;
			case OMNI: newNeighbors = findOmniNeighbors(n, theBoard, canJump, canUnblock, to); //Grab the next neighbors
				break;
			case ORTHOGONAL: newNeighbors = findOrthogNeighbors(n, theBoard, canJump, canUnblock, to); //Grab the next neighbors
				break;
			case LINEAR: return false; //Should've never gotten here
			}
			for(Node k : newNeighbors) { //Sort through the new neighbors
				if(k.dist <= movementAmt) { //Check if this neighbor isn't too far away to reach
					if(k.x == to.getX() && k.y == to.getY()) return true; //If we got there, and it wasn't too far away, return true
					neighbors.add(k); //Add to the END of neighbors this node. BFS because go over each node, check the neighbors, put them at the end of the list. Would be DFS if we put the nodes right after the one we are looking at. Instead we put them at the end of the arraylist
				}
			}
			neighbors.remove(0);
		}
		return false;
	}
	
	public static boolean checkMovement(PieceTypeInitializer pti, StandardBoard theBoard, BetterCoordinate from, BetterCoordinate to) {
		switch(pti.getMovementPattern()) {
		case LINEAR:
			return checkLinearMovement(pti, theBoard, from, to);
		default:
			return checkNonLinearMovement(pti, theBoard, from, to);
		
		}
	}
	
	
	
	
}
