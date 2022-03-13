package com.ruse.world.content.portal;

import com.ruse.model.Position;

public enum portal  {

	A(new Position(3186, 5472), new Position(3192, 5471)),
	B(new Position(3192, 5472), new Position(3185, 5472)),
	
	C(new Position(3197, 5448), new Position(3205, 5445)),
	D(new Position(3204, 5445), new Position(3196, 5448)),
	
	I(new Position(3189, 5444), new Position(3187, 5459)),
	J(new Position(3187, 5460), new Position(3190, 5444)),
	
	K(new Position(3178, 5460), new Position(3168, 5457)),
	L(new Position(3168, 5456), new Position(3178, 5459)),
	
	M(new Position(3167, 5471), new Position(3172, 5473)),
	N(new Position(3171, 5473), new Position(3167, 5470)),
	
	O(new Position(3171, 5478), new Position(3166, 5478)),
	P(new Position(3167, 5478), new Position(3172, 5478)),
	
	Q(new Position(3141, 5480), new Position(3142, 5490)),
	R(new Position(3142, 5489), new Position(3142, 5480))
	;
	
	
	private Position location;
	private Position destination;
	
	private portal(Position location, Position destination) {
		this.location = location;
		this.destination = destination;
	}
	
	public Position getLocation() {
		return location;
	}
	
	public Position getDestination() {
		return destination;
	}
	
}
