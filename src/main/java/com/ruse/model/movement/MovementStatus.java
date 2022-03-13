package com.ruse.model.movement;

/**
 * Represents a player's movement status, whether they are standing still,
 * moving, frozen or stunned.
 * 
 * @author relex lawl
 */
public enum MovementStatus {
	NONE,
	MOVING,
	FROZEN,
	STUNNED,
	CANNOT_MOVE;
}