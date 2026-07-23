package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.DelfinoLaunchParams

data class DelfinoPathPlan(
    val stateSavesDir: String,
    val gcSavesDir: String,
    val wiiNandDir: String,
    val gbaBiosPath: String?,
)

fun planPaths(params: DelfinoLaunchParams): DelfinoPathPlan {
    val saves = params.resolvedSavesDir()
    return DelfinoPathPlan(
        stateSavesDir = params.resolvedSaveStatesDir(),
        gcSavesDir = "$saves/GC",
        wiiNandDir = "$saves/Wii",
        gbaBiosPath = params.biosDir?.let { "$it/gba_bios.bin" },
    )
}
