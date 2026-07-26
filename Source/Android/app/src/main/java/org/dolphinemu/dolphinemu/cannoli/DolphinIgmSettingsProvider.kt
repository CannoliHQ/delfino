package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.GenericIgmSettingsItem
import dev.cannoli.igm.GenericIgmSettingsScreen
import dev.cannoli.igm.IgmSettingsExit
import dev.cannoli.igm.IgmSettingsProvider

private const val DOLPHIN_MENU_KEY = "__dolphin_menu__"

class DolphinIgmSettingsProvider(
    private val config: DolphinConfig,
    private val onOpenNativeMenu: () -> Unit,
) : IgmSettingsProvider {

    override fun screen(path: List<String>): GenericIgmSettingsScreen {
        val rows = DolphinOptionCatalog.entries.map { e ->
            GenericIgmSettingsItem.Choice(e.key, e.label, currentLabel(e))
        }
        return GenericIgmSettingsScreen(
            "Settings",
            rows + GenericIgmSettingsItem.Action(DOLPHIN_MENU_KEY, "Dolphin Settings"),
        )
    }

    private fun currentLabel(e: DolphinOptionCatalog.Entry): String {
        val v = config.getInt(e.file, e.section, e.configKey, e.default)
        return e.options.firstOrNull { it.first == v }?.second ?: v.toString()
    }

    override fun cycle(itemKey: String, direction: Int) {
        val e = DolphinOptionCatalog.entries.firstOrNull { it.key == itemKey } ?: return
        val cur = config.getInt(e.file, e.section, e.configKey, e.default)
        val i = e.options.indexOfFirst { it.first == cur }.let { if (it < 0) 0 else it }
        val next = e.options[((i + direction) % e.options.size + e.options.size) % e.options.size].first
        if (next == cur) return
        config.setInt(e.file, e.section, e.configKey, next)
        config.save()
    }

    override fun activate(itemKey: String): IgmSettingsExit.Prompt? {
        if (itemKey == DOLPHIN_MENU_KEY) onOpenNativeMenu()
        return null
    }

    override fun exitPrompt(): IgmSettingsExit = IgmSettingsExit.Close

    override fun setOnChanged(callback: () -> Unit) { /* Dolphin has no async apply echo in v1 */ }
}
