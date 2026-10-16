package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.client.GameShell
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Not part of the original client: the draw distance's scene size (ClientFeatures.sceneMapSize), the
 * first unit test of the repo. MapRegion.MAP_SIZES indexes: 0 is 104 tiles, 2 is 136, 3 is 168.
 */
class ClientFeaturesTest {

    private fun sceneMapSize(distance: Double, heapMb: Int, serverMapSize: Int): Int {
        val savedDistance: Double = ClientFeatures.RENDER_DISTANCE
        val savedHeap: Int = GameShell.maxMemory
        try {
            ClientFeatures.RENDER_DISTANCE = distance
            GameShell.maxMemory = heapMb
            return ClientFeatures.sceneMapSize(serverMapSize)
        } finally {
            ClientFeatures.RENDER_DISTANCE = savedDistance
            GameShell.maxMemory = savedHeap
        }
    }

    @Test
    fun zoneOffsetsShiftByHalfTheSizeDifference() {
        assertEquals(0, ClientFeatures.serverZoneShift(104, 104))
        assertEquals(16, ClientFeatures.serverZoneShift(136, 104))
        assertEquals(32, ClientFeatures.serverZoneShift(168, 104))
        assertEquals(24, ClientFeatures.serverZoneShift(168, 120))
        assertEquals(0, ClientFeatures.serverZoneShift(168, 168))
    }

    @Test
    fun theServersSceneAtDrawDistanceOne() {
        assertEquals(0, sceneMapSize(1.0, 512, 0))
        assertEquals(1, sceneMapSize(1.0, 512, 1))
    }

    @Test
    fun aBiggerSceneForALongerDrawDistance() {
        assertEquals(2, sceneMapSize(1.5, 512, 0))
        assertEquals(3, sceneMapSize(2.0, 512, 0))
    }

    @Test
    fun neverSmallerThanTheServers() {
        assertEquals(3, sceneMapSize(1.5, 512, 3))
        assertEquals(3, sceneMapSize(1.0, 512, 3))
    }

    @Test
    fun aSmallHeapGetsASmallerScene() {
        // 168 tiles held at 512 MB; 168 and 136 both went out of memory at 256 MB
        assertEquals(2, sceneMapSize(2.0, 400, 0))
        assertEquals(0, sceneMapSize(2.0, 256, 0))
        assertEquals(0, sceneMapSize(1.5, 256, 0))
    }
}
