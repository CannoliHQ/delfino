package org.dolphinemu.dolphinemu.cannoli

import org.dolphinemu.dolphinemu.NativeLibrary
import org.dolphinemu.dolphinemu.features.settings.model.NativeConfig
import org.dolphinemu.dolphinemu.features.settings.model.Settings

/**
 * Applies injected Cannoli directories to Dolphin. Must run after directory initialization (so the
 * config system is up and the base user paths have been rebuilt) and before the game boots.
 */
object DelfinoPaths {

    // EXIDeviceType.MemoryCardFolder. Selects per-game .gci saves for GameCube slot A.
    private const val EXI_MEMORY_CARD_FOLDER = 8

    fun apply(plan: DelfinoPathPlan) {
        // Save states: no config key exists, so use File::SetUserPath via JNI.
        NativeLibrary.SetStateSavesDirectory(plan.stateSavesDir)

        // Wii NAND root.
        NativeConfig.setString(
            NativeConfig.LAYER_BASE, Settings.FILE_DOLPHIN, Settings.SECTION_INI_GENERAL,
            "NANDRootPath", plan.wiiNandDir,
        )

        // GameCube: GCI-folder mode with the folder under Cannoli/Saves/GC.
        NativeConfig.setInt(
            NativeConfig.LAYER_BASE, Settings.FILE_DOLPHIN, Settings.SECTION_INI_CORE,
            "SlotA", EXI_MEMORY_CARD_FOLDER,
        )
        NativeConfig.setString(
            NativeConfig.LAYER_BASE, Settings.FILE_DOLPHIN, Settings.SECTION_INI_CORE,
            "GCIFolderAPath", plan.gcSavesDir,
        )

        // GBA BIOS, only when a BIOS directory was injected.
        plan.gbaBiosPath?.let {
            NativeConfig.setString(
                NativeConfig.LAYER_BASE, Settings.FILE_DOLPHIN, Settings.SECTION_INI_GBA,
                "BIOS", it,
            )
        }
    }
}
