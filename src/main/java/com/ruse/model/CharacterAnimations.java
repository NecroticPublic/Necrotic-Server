package com.ruse.model;


public class CharacterAnimations {

	/** The standing animation for this player. */
	private int standingAnimation = -1;

	/** The walking animation for this player. */
	private int walkingAnimation = -1;

	/** The running animation for this player. */
	private int runningAnimation = -1;
	
	/** new shit */
	private int turnIndex = -1;
	private int turn180Index = -1;
	private int turn90CWIndex = -1;
	private int turn90CCWIndex = -1;

	/**
	 * Create a new {@link CharacterAnimations}.
	 * 
	 * @param standingAnimation
	 *            the standing animation for this player.
	 * @param walkingAnimation
	 *            the walking animation for this player.
	 * @param runningAnimation
	 *            the running animation for this player.
	 */
	public CharacterAnimations(int standingAnimation, int walkingAnimation,
			int runningAnimation, int turnIndex, int turn180Index, int turn90CWIndex, int turn90CCWIndex) {
		this.standingAnimation = standingAnimation;
		this.walkingAnimation = walkingAnimation;
		this.runningAnimation = runningAnimation;
		this.turnIndex = turnIndex;
		this.turn180Index = turn180Index;
		this.turn90CWIndex = turn90CWIndex;
		this.turn90CCWIndex = turn90CCWIndex;
	}

	/**
	 * Create a new {@link CharacterAnimations}.
	 */
	public CharacterAnimations() {

	}

	@Override
	public CharacterAnimations clone() {
		CharacterAnimations ca = new CharacterAnimations();
		ca.standingAnimation = standingAnimation;
		ca.walkingAnimation = walkingAnimation;
		ca.runningAnimation = runningAnimation;
		ca.turnIndex = turnIndex;
		ca.turn180Index = turn180Index;
		ca.turn90CWIndex = turn90CWIndex;
		ca.turn90CCWIndex = turn90CCWIndex;
		return ca;
	}

	@Override
	public String toString() {
		return "CHARACTER ANIMATIONS[standing= " + standingAnimation + ", walking= " + walkingAnimation + ", running= " + runningAnimation + ", turn = "+ turnIndex + "]";
	}

	/**
	 * Resets the animation indexes.
	 */
	public void reset() {
		standingAnimation = -1;
		walkingAnimation = -1;
		runningAnimation = -1;
		turnIndex = -1;
		turn180Index = -1;
		turn90CWIndex = -1;
		turn90CCWIndex = -1;
	}

	/**
	 * Gets the standing animation for this player.
	 * 
	 * @return the standing animation.
	 */
	public int getStandingAnimation() {
		return standingAnimation;
	}

	/**
	 * Gets the walking animation for this player.
	 * 
	 * @return the walking animation.
	 */
	public int getWalkingAnimation() {
		return walkingAnimation;
	}

	/**
	 * Gets the running animation for this player.
	 * 
	 * @return the running animation.
	 */
	public int getRunningAnimation() {
		return runningAnimation;
	}
	
	public int getTurnIndex() {
		return turnIndex;
	}
	
	public int getTurn180Index() {
		return turn180Index;
	}
	
	public int getTurn90CWIndex() {
		return turn90CWIndex;
	}
	
	public int getTurn90CCWIndex() {
		return turn90CCWIndex;
	}

	/**
	 * Sets the standing animation for this player.
	 * 
	 * @param standingAnimation
	 *            the new standing animation to set.
	 */
	public void setStandingAnimation(int standingAnimation) {
		this.standingAnimation = standingAnimation;
	}

	/**
	 * Sets the walking animation for this player.
	 * 
	 * @param walkingAnimation
	 *            the new walking animation to set.
	 */
	public void setWalkingAnimation(int walkingAnimation) {
		this.walkingAnimation = walkingAnimation;
	}

	/**
	 * Sets the running animation for this player.
	 * 
	 * @param runningAnimation
	 *            the new running animation to set.
	 */
	public void setRunningAnimation(int runningAnimation) {
		this.runningAnimation = runningAnimation;
	}
	
	public void setTurnIndex(int turnIndex) {
		this.turnIndex = turnIndex;
	}
	
	public void setTurn180Index(int turn180Index) {
		this.turn180Index = turn180Index;
	}
	
	public void setTurn90CWIndex(int turn90CWIndex) {
		this.turn90CWIndex = turn90CWIndex;
	}
	
	public void setTurn90CCWIndex(int turn90CCWIndex) {
		this.turn90CCWIndex = turn90CCWIndex;
	}
}
