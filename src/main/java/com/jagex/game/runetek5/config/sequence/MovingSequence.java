package com.jagex.game.runetek5.config.sequence;

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
public final class MovingSequence {

	public boolean hasType = false;

	public int sequenceId = -1;

	public int currentFrameDuration = 0;

	public int currentFrameId = 0;

	public int nextFrameId = -1;
}
