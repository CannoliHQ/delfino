package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.DelfinoLaunchParams

/**
 * In-process carrier for the current Cannoli launch. Set by DelfinoLaunchActivity before booting
 * and read by EmulationActivity to decide whether to show the Cannoli IGM. Null for normal
 * (non-Cannoli) Dolphin launches, which keep Dolphin's own in-game menu.
 */
object DelfinoSession {
    @Volatile
    var params: DelfinoLaunchParams? = null
}
