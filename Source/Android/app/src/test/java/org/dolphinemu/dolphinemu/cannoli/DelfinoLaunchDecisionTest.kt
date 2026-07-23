package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.DelfinoLaunchParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DelfinoLaunchDecisionTest {

    private fun params(rom: String) = DelfinoLaunchParams(
        romPath = rom,
        cannoliRoot = "/sd/Cannoli",
        savesDir = null, saveStatesDir = null, biosDir = null, userDir = null,
        gameTitle = "G", platformTag = "gc", igmTriggerKeycodes = emptyList(),
        colors = null, displaySettings = null, inputMapping = null,
    )

    @Test
    fun nullParamsShowsMainUi() {
        assertEquals(ShowMainUi, decideLaunch(null))
    }

    @Test
    fun validParamsBootsGame() {
        val decision = decideLaunch(params("/sd/game.rvz"))
        assertTrue(decision is BootGame)
        assertEquals("/sd/game.rvz", (decision as BootGame).params.romPath)
    }

    @Test
    fun blankRomPathShowsMainUi() {
        assertEquals(ShowMainUi, decideLaunch(params("   ")))
    }
}
