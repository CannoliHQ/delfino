package org.dolphinemu.dolphinemu.cannoli

import android.content.Context
import android.graphics.Bitmap
import dev.cannoli.igm.AchievementInfo
import dev.cannoli.igm.EmulatorBridge
import dev.cannoli.igm.IgmSettingsProvider
import org.dolphinemu.dolphinemu.NativeLibrary
import org.dolphinemu.dolphinemu.features.settings.model.NativeConfig

// Global (non-game-specific) reads/writes, matching how Dolphin's own GFX settings screen
// reads and persists settings: LAYER_ACTIVE for reads, LAYER_BASE_OR_CURRENT for writes (so
// changes apply live to the running game), LAYER_BASE for save (see Settings.saveSettings()).
class NativeConfigDolphinConfig : DolphinConfig {
    override fun getInt(file: String, section: String, key: String, default: Int): Int =
        NativeConfig.getInt(NativeConfig.LAYER_ACTIVE, file, section, key, default)
    override fun setInt(file: String, section: String, key: String, value: Int) {
        NativeConfig.setInt(NativeConfig.LAYER_BASE_OR_CURRENT, file, section, key, value)
    }
    override fun save() { NativeConfig.save(NativeConfig.LAYER_BASE) }
}

/**
 * Bridges the shared Cannoli IGM to Dolphin. v1 wires the directly supported operations; state
 * thumbnails and achievements are behind capability flags or stubbed and can be filled in later.
 * onOpenNativeMenu is supplied by EmulationActivity to reach Dolphin's own menu.
 */
class DolphinBridge(
    context: Context,
    private val onOpenNativeMenu: () -> Unit,
    private val onQuit: () -> Unit = {},
    discPaths: List<String> = emptyList(),
    changeDisc: (String) -> Unit = NativeLibrary::ChangeDisc,
) : EmulatorBridge {

    private val prefs = context.getSharedPreferences("delfino_igm_toggles", Context.MODE_PRIVATE)
    private var onNativeMenuClosed: (() -> Unit)? = null
    private val discSwitcher = DiscSwitcher(discPaths, changeDisc)

    override val supportsNativeMenu = true
    override val supportsAchievements = false
    override val supportsUndo = true

    // Dolphin uses 1-based numbered state slots and has no "auto" slot. If on-device testing shows
    // the IGM passes a 0-based or auto-inclusive index, adjust only this function.
    private fun toDolphinSlot(slot: Int): Int = slot

    override fun reset() {
        // Dolphin's Android NativeLibrary exposes no in-place reset in v1. No-op for now; a thin
        // JNI reset can be added later if the IGM's Reset entry is wanted.
    }

    override fun quit() {
        onQuit()
        NativeLibrary.StopEmulation()
    }
    override fun pause() = NativeLibrary.PauseEmulation(false)
    override fun unpause() = NativeLibrary.UnPauseEmulation()
    override fun isPaused(): Boolean = !NativeLibrary.IsRunningAndUnpaused()

    override fun saveState(slot: Int) = NativeLibrary.SaveState(toDolphinSlot(slot))
    override fun loadState(slot: Int) = NativeLibrary.LoadState(toDolphinSlot(slot))
    override fun undoSaveState() = NativeLibrary.UndoSaveState()
    override fun undoLoadState() = NativeLibrary.UndoLoadState()
    override fun getStateSlotCount() = 10
    override fun getStateThumbnail(slot: Int): Bitmap? = null
    override fun stateExists(slot: Int): Boolean =
        NativeLibrary.GetUnixTimeOfStateSlot(toDolphinSlot(slot)) != 0L

    override fun getAchievements(): List<AchievementInfo> = emptyList()

    override fun getDiskCount() = discSwitcher.getDiskCount()
    override fun getDiskIndex() = discSwitcher.getDiskIndex()
    override fun setDiskIndex(index: Int) = discSwitcher.setDiskIndex(index)
    override fun getDiskLabel(index: Int): String? = discSwitcher.getDiskLabel(index)

    override fun openNativeMenu() = onOpenNativeMenu()
    override fun openAchievementsMenu() = onOpenNativeMenu()

    override fun settingsProvider(): IgmSettingsProvider =
        DolphinIgmSettingsProvider(NativeConfigDolphinConfig(), onOpenNativeMenu)

    override fun setOnNativeMenuClosed(callback: () -> Unit) {
        onNativeMenuClosed = callback
    }

    override fun getLocalToggle(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    override fun setLocalToggle(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    // Present so the ported IGMOverlayController (which mirrors ricotta) compiles. Dolphin has no
    // native IGM-visibility concept, so this is a no-op.
    fun setIGMVisible(visible: Boolean) {}
}
