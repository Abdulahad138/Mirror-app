package com.example

import com.example.model.MirrorType
import com.example.model.OpticsCalculator
import com.example.model.SimulationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testConcaveMirrorAtCenterOfCurvature() {
        // u = 2f: Image should be at v = 2f, real and inverted with magnification -1
        val state = SimulationState(
            mirrorType = MirrorType.CONCAVE,
            objectDistanceU = 2.0f,
            focalLengthF = 1.0f,
            objectHeightH = 1.0f
        )
        val result = OpticsCalculator.calculate(state)
        assertEquals(2.0f, result.v, 0.01f)
        assertEquals(-1.0f, result.magnification, 0.01f)
        assertTrue(result.isReal)
        assertFalse(result.isErect)
    }

    @Test
    fun testConcaveMirrorInsideFocus() {
        // u < f: Virtual & erect image behind mirror (v < 0)
        val state = SimulationState(
            mirrorType = MirrorType.CONCAVE,
            objectDistanceU = 0.5f,
            focalLengthF = 1.0f,
            objectHeightH = 1.0f
        )
        val result = OpticsCalculator.calculate(state)
        assertEquals(-1.0f, result.v, 0.01f)
        assertEquals(2.0f, result.magnification, 0.01f)
        assertFalse(result.isReal)
        assertTrue(result.isErect)
    }

    @Test
    fun testConvexMirrorAlwaysVirtualAndDiminished() {
        val state = SimulationState(
            mirrorType = MirrorType.CONVEX,
            objectDistanceU = 1.0f,
            focalLengthF = 1.0f,
            objectHeightH = 1.0f
        )
        val result = OpticsCalculator.calculate(state)
        // v = -(f * u) / (f + u) = -1.0 / 2.0 = -0.5
        assertEquals(-0.5f, result.v, 0.01f)
        assertEquals(0.5f, result.magnification, 0.01f)
        assertFalse(result.isReal)
        assertTrue(result.isErect)
    }
}
