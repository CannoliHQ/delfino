package org.dolphinemu.dolphinemu.cannoli

// Backs DolphinBridge's disc-switch methods without touching NativeLibrary directly, so the
// index/label logic is unit-testable outside the Dolphin runtime.
class DiscSwitcher(
    private val discPaths: List<String>,
    private val changeDisc: (String) -> Unit,
) {
    private var currentDisc = 0

    fun getDiskCount() = if (discPaths.size > 1) discPaths.size else 1
    fun getDiskIndex() = currentDisc
    fun getDiskLabel(index: Int): String? =
        if (index in discPaths.indices) "Disc ${index + 1}" else null

    fun setDiskIndex(index: Int) {
        if (index !in discPaths.indices || index == currentDisc) return
        changeDisc(discPaths[index])
        currentDisc = index
    }
}
