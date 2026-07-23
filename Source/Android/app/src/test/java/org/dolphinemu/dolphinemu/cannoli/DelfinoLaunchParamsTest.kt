package org.dolphinemu.dolphinemu.cannoli

import android.content.Intent
import android.os.Parcel
import dev.cannoli.igm.DELFINO_PROTOCOL_VERSION
import dev.cannoli.igm.DelfinoLaunchParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// SDK level and Application come from src/test/resources/robolectric.properties.
@RunWith(RobolectricTestRunner::class)
class DelfinoLaunchParamsTest {

    private fun sample() = DelfinoLaunchParams(
        romPath = "/sd/Roms/GC/game.rvz",
        cannoliRoot = "/sd/Cannoli",
        savesDir = null,
        saveStatesDir = null,
        biosDir = "/sd/Cannoli/Bios",
        userDir = null,
        gameTitle = "Test Game",
        platformTag = "gc",
        igmTriggerKeycodes = listOf(102, 103),
        colors = null,
        displaySettings = null,
        inputMapping = null,
    )

    @Test
    fun parcelRoundTripPreservesFields() {
        val original = sample()
        val parcel = Parcel.obtain()
        original.writeToParcel(parcel, 0)
        parcel.setDataPosition(0)
        val restored = DelfinoLaunchParams.CREATOR.createFromParcel(parcel)
        parcel.recycle()

        assertEquals(original.romPath, restored.romPath)
        assertEquals(original.cannoliRoot, restored.cannoliRoot)
        assertEquals(original.biosDir, restored.biosDir)
        assertEquals(original.gameTitle, restored.gameTitle)
        assertEquals(original.platformTag, restored.platformTag)
        assertEquals(original.igmTriggerKeycodes, restored.igmTriggerKeycodes)
    }

    @Test
    fun readFromIntentReturnsNullOnProtocolMismatch() {
        val intent = Intent()
        sample().writeToIntent(intent)
        intent.putExtra(DelfinoLaunchParams.EXTRA_PROTOCOL, DELFINO_PROTOCOL_VERSION + 1)
        assertNull(DelfinoLaunchParams.readFromIntent(intent))
    }

    @Test
    fun readFromIntentRoundTripsThroughIntent() {
        val intent = Intent()
        sample().writeToIntent(intent)
        val restored = DelfinoLaunchParams.readFromIntent(intent)
        assertEquals("/sd/Roms/GC/game.rvz", restored?.romPath)
    }

    @Test
    fun resolvedDirsDefaultToCannoliRoot() {
        val p = sample()
        assertEquals("/sd/Cannoli/Saves", p.resolvedSavesDir())
        assertEquals("/sd/Cannoli/Save States", p.resolvedSaveStatesDir())
    }

    @Test
    fun resolvedDirsUseExplicitOverrides() {
        val p = sample().copy(savesDir = "/sd/Other/Saves", saveStatesDir = "/sd/Other/States")
        assertEquals("/sd/Other/Saves", p.resolvedSavesDir())
        assertEquals("/sd/Other/States", p.resolvedSaveStatesDir())
    }
}
