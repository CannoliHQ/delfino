package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.DelfinoLaunchParams

sealed interface LaunchDecision

data class BootGame(val params: DelfinoLaunchParams) : LaunchDecision

object ShowMainUi : LaunchDecision

fun decideLaunch(params: DelfinoLaunchParams?): LaunchDecision {
    if (params == null || params.romPath.isBlank()) return ShowMainUi
    return BootGame(params)
}
