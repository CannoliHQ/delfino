package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.DelfinoLaunchParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DelfinoPathPlanTest {

    private fun params(cannoliRoot: String, saves: String? = null, states: String? = null, bios: String? = null) =
        DelfinoLaunchParams(
            romPath = "/sd/game.rvz",
            cannoliRoot = cannoliRoot,
            savesDir = saves,
            saveStatesDir = states,
            biosDir = bios,
            userDir = null,
            gameTitle = "G",
            platformTag = "gc",
            igmTriggerKeycodes = emptyList(),
            colors = null, displaySettings = null, inputMapping = null,
        )

    @Test
    fun defaultsDeriveFromCannoliRoot() {
        val plan = planPaths(params("/sd/Cannoli"))
        assertEquals("/sd/Cannoli/Save States", plan.stateSavesDir)
        assertEquals("/sd/Cannoli/Saves/GC", plan.gcSavesDir)
        assertEquals("/sd/Cannoli/Saves/Wii", plan.wiiNandDir)
    }

    @Test
    fun explicitSavesAndStatesOverrideRoot() {
        val plan = planPaths(params("/sd/Cannoli", saves = "/sd/S", states = "/sd/ST"))
        assertEquals("/sd/ST", plan.stateSavesDir)
        assertEquals("/sd/S/GC", plan.gcSavesDir)
        assertEquals("/sd/S/Wii", plan.wiiNandDir)
    }

    @Test
    fun biosPathIsNullWhenNoBiosDir() {
        assertNull(planPaths(params("/sd/Cannoli")).gbaBiosPath)
    }

    @Test
    fun biosPathIsFileUnderBiosDir() {
        val plan = planPaths(params("/sd/Cannoli", bios = "/sd/Cannoli/Bios"))
        assertEquals("/sd/Cannoli/Bios/gba_bios.bin", plan.gbaBiosPath)
    }
}
