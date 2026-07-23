package org.dolphinemu.dolphinemu.cannoli

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity

/**
 * Owns the Cannoli IGM overlay inside EmulationActivity. Active only when the current launch came
 * from Cannoli (DelfinoSession.params != null); otherwise every method is a no-op and Dolphin keeps
 * its own in-game menu.
 */
class DelfinoIgmHost(private val activity: FragmentActivity) {

    private var overlay: IGMOverlayController? = null
    private var triggerKeycodes: Set<Int> = emptySet()
    private var quitting = false

    fun onCreate(savedInstanceState: Bundle?, onOpenNativeMenu: () -> Unit) {
        val params = DelfinoSession.params ?: return
        triggerKeycodes = params.igmTriggerKeycodes.toSet()

        val bridge = DolphinBridge(activity, onOpenNativeMenu, onQuit = ::onQuitRequested)
        val controller = IGMOverlayController(
            activity,
            bridge,
            params.gameTitle,
            buildIgmHostConfig(params.displaySettings),
            params.cannoliRoot,
            params.platformTag,
            params.platformTag,
            params.colors?.highlight,
            params.colors?.text,
            params.colors?.highlightText,
            params.colors?.accent,
            params.colors?.title,
        )
        controller.onCreate(savedInstanceState)
        controller.controller.setInputMapping(params.inputMapping)
        overlay = controller
    }

    // Returns true when the event was the IGM trigger and was consumed.
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (quitting) return false
        val o = overlay ?: return false
        if (event.action == KeyEvent.ACTION_DOWN && !o.isVisible() && event.keyCode in triggerKeycodes) {
            o.show()
            return true
        }
        return false
    }

    // Once the IGM's Quit is chosen, Dolphin's StopEmulation tears the core down asynchronously
    // over a short window. Lock the IGM out and dismiss it immediately so the trigger cannot
    // reopen it while the activity is finishing.
    private fun onQuitRequested() {
        quitting = true
        overlay?.hide()
    }

    fun onResume() = overlay?.onResume() ?: Unit
    fun onStop() = overlay?.onPause() ?: Unit
    fun onDestroy() {
        overlay?.onDestroy()
        overlay = null
        DelfinoSession.params = null
    }
}
