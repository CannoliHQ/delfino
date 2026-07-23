package org.dolphinemu.dolphinemu.cannoli

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dev.cannoli.igm.DelfinoLaunchParams
import org.dolphinemu.dolphinemu.activities.EmulationActivity
import org.dolphinemu.dolphinemu.ui.main.MainActivity
import org.dolphinemu.dolphinemu.utils.AfterDirectoryInitializationRunner
import org.dolphinemu.dolphinemu.utils.DirectoryInitialization

/**
 * Entry point that Cannoli targets explicitly with a DelfinoLaunchParams intent. With valid
 * params it boots the given ROM; otherwise it forwards to the normal Dolphin UI.
 */
class DelfinoLaunchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (val decision = decideLaunch(DelfinoLaunchParams.readFromIntent(intent))) {
            is BootGame -> bootGame(decision.params)
            ShowMainUi -> showMainUi()
        }
    }

    private fun bootGame(params: DelfinoLaunchParams) {
        DelfinoSession.params = params
        DirectoryInitialization.start(this)
        // Inject paths after directory init (config system up, base user paths rebuilt) and before
        // boot. fromIntent=true makes EmulationActivity.launch finish this activity afterward.
        AfterDirectoryInitializationRunner().runWithLifecycle(this) {
            DelfinoPaths.apply(planPaths(params))
            EmulationActivity.launch(this@DelfinoLaunchActivity, params.romPath, false, true)
        }
    }

    private fun showMainUi() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
