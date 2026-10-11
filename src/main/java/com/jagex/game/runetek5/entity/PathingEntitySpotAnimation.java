package com.jagex.game.runetek5.entity;

/**
 * Not part of the original client: fields the jar keeps loose on their owner, grouped in one
 * class.
 */
public final class PathingEntitySpotAnimation {

	public int height;

	public int startCycle;

	public int rotation;

	public int currentFrameId = 0;

	public int spotAnimationId = -1;

	public int adjustmentType = -1;

	public int currentFrameDuration = 0;

	public int currentLoopCount = 0;

	public int nextFrameId = -1;
}
