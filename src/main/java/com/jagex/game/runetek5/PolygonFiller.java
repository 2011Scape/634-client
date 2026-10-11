package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public final class PolygonFiller {

	@OriginalMember(owner = "client!df", name = "a", descriptor = "I")
	public static int nextEdgeIndex;

	@OriginalMember(owner = "client!df", name = "b", descriptor = "I")
	public static int scanlineY;

	@OriginalMember(owner = "client!df", name = "c", descriptor = "I")
	public static int activeEdgeStart;

	@OriginalMember(owner = "client!df", name = "d", descriptor = "I")
	public static int edgeBufferSize;

	@OriginalMember(owner = "client!df", name = "e", descriptor = "I")
	public static int scanlineStartX;

	@OriginalMember(owner = "client!df", name = "g", descriptor = "I")
	public static int scanlineEndX;

	@OriginalMember(owner = "client!df", name = "h", descriptor = "I")
	public static int scanEdgeIndex;

	@OriginalMember(owner = "client!df", name = "f", descriptor = "[I")
	public static int[] edgeBuffer;

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(I)V")
	public static void initializeScanline(@OriginalArg(0) int topY) {
		if (edgeBufferSize < 0) {
			scanEdgeIndex = 0;
			nextEdgeIndex = 0;
			activeEdgeStart = 0;
			scanlineY = 2147483646;
			return;
		}
		PolygonFiller.sortEdgesByY(0, edgeBufferSize);
		@Pc(17) int y = edgeBuffer[1];
		if (y < topY) {
			y = topY;
		}
		@Pc(26) int edge;
		for (edge = 0; edge < edgeBufferSize; edge += 4) {
			@Pc(33) int startY = edgeBuffer[edge + 1];
			if (y < startY) {
				break;
			}
			@Pc(40) int x1 = edgeBuffer[edge];
			@Pc(46) int x2 = edgeBuffer[edge + 2];
			@Pc(52) int y2 = edgeBuffer[edge + 3];
			@Pc(62) int step = (x2 - x1 << 16) / (y2 - startY);
			@Pc(68) int x = (x1 << 16) + 32768;
			edgeBuffer[edge] = x + (y - startY) * step;
			edgeBuffer[edge + 2] = step;
		}
		activeEdgeStart = 0;
		nextEdgeIndex = edge;
		scanEdgeIndex = edge;
		scanlineY = y - 1;
	}

	@OriginalMember(owner = "client!df", name = "a", descriptor = "([III)V")
	public static void addEdges(@OriginalArg(0) int[] vertices, @OriginalArg(2) int count) {
		@Pc(5) int required = edgeBufferSize + (count << 1);
		@Pc(16) int index;
		if (edgeBuffer == null || edgeBuffer.length < required) {
			@Pc(14) int[] grown = new int[required];
			for (index = 0; index < edgeBufferSize; index++) {
				grown[index] = edgeBuffer[index];
			}
			edgeBuffer = grown;
		}
		@Pc(33) int length = count;
		@Pc(37) int previous = length - 2;
		for (index = 0; index < length; index += 2) {
			@Pc(46) int previousY = vertices[previous + 1];
			@Pc(52) int y = vertices[index + 1];
			if (previousY < y) {
				edgeBuffer[edgeBufferSize++] = vertices[previous];
				edgeBuffer[edgeBufferSize++] = previousY;
				edgeBuffer[edgeBufferSize++] = vertices[index];
				edgeBuffer[edgeBufferSize++] = y;
			} else if (y < previousY) {
				edgeBuffer[edgeBufferSize++] = vertices[index];
				edgeBuffer[edgeBufferSize++] = y;
				edgeBuffer[edgeBufferSize++] = vertices[previous];
				edgeBuffer[edgeBufferSize++] = previousY;
			}
			previous = index;
		}
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "()V")
	public static void resetEdgeBuffer() {
		edgeBufferSize = 0;
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "(I)Z")
	public static boolean advanceScanline(@OriginalArg(0) int maxY) {
		@Pc(1) int end = nextEdgeIndex;
		@Pc(3) int cursor = scanEdgeIndex;
		@Pc(5) int y = scanlineY;
		while (cursor >= end) {
			y++;
			scanlineY = y;
			if (y >= maxY) {
				return false;
			}
			@Pc(16) int start = activeEdgeStart;
			@Pc(23) int local23;
			@Pc(30) int local30;
			while (end < edgeBufferSize) {
				int y1 = edgeBuffer[end + 1];
				if (y < y1) {
					break;
				}
				int x1 = edgeBuffer[end];
				@Pc(36) int x2 = edgeBuffer[end + 2];
				@Pc(42) int y2 = edgeBuffer[end + 3];
				@Pc(52) int step = (x2 - x1 << 16) / (y2 - y1);
				@Pc(58) int x = (x1 << 16) + 32768;
				edgeBuffer[end] = x;
				edgeBuffer[end + 2] = step;
				end += 4;
			}
			for (int edgeIndex = start; edgeIndex < end; edgeIndex += 4) {
				int edgeEndY = edgeBuffer[edgeIndex + 3];
				if (y >= edgeEndY) {
					edgeBuffer[edgeIndex] = edgeBuffer[start];
					edgeBuffer[edgeIndex + 1] = edgeBuffer[start + 1];
					edgeBuffer[edgeIndex + 2] = edgeBuffer[start + 2];
					edgeBuffer[edgeIndex + 3] = edgeBuffer[start + 3];
					start += 4;
				}
			}
			if (start == edgeBufferSize) {
				edgeBufferSize = 0;
				return false;
			}
			PolygonFiller.sortActiveEdgesByX(start, end);
			activeEdgeStart = start;
			nextEdgeIndex = end;
			cursor = start;
		}
		scanlineStartX = edgeBuffer[cursor] >> 16;
		scanlineEndX = edgeBuffer[cursor + 4] >> 16;
		edgeBuffer[cursor] += edgeBuffer[cursor + 2];
		edgeBuffer[cursor + 4] += edgeBuffer[cursor + 6];
		cursor += 8;
		scanEdgeIndex = cursor;
		return true;
	}

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(II)V")
	public static void sortEdgesByY(@OriginalArg(0) int from, @OriginalArg(1) int to) {
		if (to <= from + 4) {
			return;
		}
		@Pc(7) int store = from;
		@Pc(11) int pivotX1 = PolygonFiller.edgeBuffer[from];
		@Pc(17) int pivotY1 = PolygonFiller.edgeBuffer[from + 1];
		@Pc(23) int pivotX2 = PolygonFiller.edgeBuffer[from + 2];
		@Pc(29) int pivotY2 = PolygonFiller.edgeBuffer[from + 3];
		for (@Pc(33) int edge = from + 4; edge < to; edge += 4) {
			@Pc(40) int startY = PolygonFiller.edgeBuffer[edge + 1];
			if (startY < pivotY1) {
				PolygonFiller.edgeBuffer[store] = PolygonFiller.edgeBuffer[edge];
				PolygonFiller.edgeBuffer[store + 1] = startY;
				PolygonFiller.edgeBuffer[store + 2] = PolygonFiller.edgeBuffer[edge + 2];
				PolygonFiller.edgeBuffer[store + 3] = PolygonFiller.edgeBuffer[edge + 3];
				store += 4;
				PolygonFiller.edgeBuffer[edge] = PolygonFiller.edgeBuffer[store];
				PolygonFiller.edgeBuffer[edge + 1] = PolygonFiller.edgeBuffer[store + 1];
				PolygonFiller.edgeBuffer[edge + 2] = PolygonFiller.edgeBuffer[store + 2];
				PolygonFiller.edgeBuffer[edge + 3] = PolygonFiller.edgeBuffer[store + 3];
			}
		}
		PolygonFiller.edgeBuffer[store] = pivotX1;
		PolygonFiller.edgeBuffer[store + 1] = pivotY1;
		PolygonFiller.edgeBuffer[store + 2] = pivotX2;
		PolygonFiller.edgeBuffer[store + 3] = pivotY2;
		sortEdgesByY(from, store);
		sortEdgesByY(store + 4, to);
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "(II)V")
	public static void sortActiveEdgesByX(@OriginalArg(0) int from, @OriginalArg(1) int to) {
		while (true) {
			if (to >= from + 8) {
				@Pc(2) boolean sorted = true;
				for (@Pc(6) int edge = from + 4; edge < to; edge += 4) {
					@Pc(13) int left = PolygonFiller.edgeBuffer[edge - 4];
					@Pc(17) int right = PolygonFiller.edgeBuffer[edge];
					if (left > right) {
						sorted = false;
						PolygonFiller.edgeBuffer[edge - 4] = right;
						PolygonFiller.edgeBuffer[edge] = left;
						left = PolygonFiller.edgeBuffer[edge - 2];
						PolygonFiller.edgeBuffer[edge - 2] = PolygonFiller.edgeBuffer[edge + 2];
						PolygonFiller.edgeBuffer[edge + 2] = left;
						left = PolygonFiller.edgeBuffer[edge - 1];
						PolygonFiller.edgeBuffer[edge - 1] = PolygonFiller.edgeBuffer[edge + 3];
						PolygonFiller.edgeBuffer[edge + 3] = left;
					}
				}
				if (!sorted) {
					to -= 4;
					continue;
				}
			}
			return;
		}
	}
}
