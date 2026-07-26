package org.dolphinemu.dolphinemu.cannoli

import dev.cannoli.igm.GenericIgmSettingsItem
import dev.cannoli.igm.IgmSettingsExit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val DOLPHIN_MENU_KEY = "__dolphin_menu__"

private class FakeDolphinConfig(initial: Map<Triple<String, String, String>, Int> = emptyMap()) : DolphinConfig {
    val values = initial.toMutableMap()
    var saves = 0
    override fun getInt(file: String, section: String, key: String, default: Int): Int =
        values[Triple(file, section, key)] ?: default
    override fun setInt(file: String, section: String, key: String, value: Int) {
        values[Triple(file, section, key)] = value
    }
    override fun save() { saves++ }
}

class DolphinIgmSettingsProviderTest {

    private fun provider(
        config: FakeDolphinConfig = FakeDolphinConfig(),
        opened: MutableList<Unit> = mutableListOf(),
    ) = DolphinIgmSettingsProvider(config, onOpenNativeMenu = { opened.add(Unit) })

    @Test
    fun `root lists the curated settings plus a Dolphin Settings action`() {
        val items = provider().screen(emptyList()).items
        assertEquals(
            listOf("Internal Resolution", "Aspect Ratio", "Dolphin Settings"),
            items.map { it.label },
        )
        assertTrue(items[0] is GenericIgmSettingsItem.Choice)
        assertTrue(items[1] is GenericIgmSettingsItem.Choice)
        assertTrue(items[2] is GenericIgmSettingsItem.Action)
    }

    @Test
    fun `aspect ratio shows the label for its current int value`() {
        val cfg = FakeDolphinConfig(mapOf(Triple("GFX", "Settings", "AspectRatio") to 1))
        val row = provider(cfg).screen(emptyList()).items
            .filterIsInstance<GenericIgmSettingsItem.Choice>().first { it.label == "Aspect Ratio" }
        assertEquals("Force 16:9", row.value)
    }

    @Test
    fun `cycling aspect ratio writes the next int value and saves`() {
        val cfg = FakeDolphinConfig(mapOf(Triple("GFX", "Settings", "AspectRatio") to 0))
        val p = provider(cfg)
        p.cycle("aspect_ratio", 1)
        assertEquals(1, cfg.values[Triple("GFX", "Settings", "AspectRatio")])
        assertEquals(1, cfg.saves)
        assertEquals("Force 16:9",
            p.screen(emptyList()).items.filterIsInstance<GenericIgmSettingsItem.Choice>()
                .first { it.label == "Aspect Ratio" }.value)
    }

    @Test
    fun `cycling wraps at the ends of the option list`() {
        val cfg = FakeDolphinConfig(mapOf(Triple("GFX", "Settings", "AspectRatio") to 0))
        val p = provider(cfg)
        p.cycle("aspect_ratio", -1)
        assertEquals(3, cfg.values[Triple("GFX", "Settings", "AspectRatio")]) // 0 -> wrap to last (Stretch)
    }

    @Test
    fun `internal resolution cycles through its options`() {
        val cfg = FakeDolphinConfig(mapOf(Triple("GFX", "Settings", "InternalResolution") to 1))
        val p = provider(cfg)
        p.cycle("internal_resolution", 1)
        assertEquals(2, cfg.values[Triple("GFX", "Settings", "InternalResolution")])
    }

    @Test
    fun `activating Dolphin Settings opens the native menu`() {
        val opened = mutableListOf<Unit>()
        val p = provider(opened = opened)
        p.activate(DOLPHIN_MENU_KEY)
        assertEquals(1, opened.size)
    }

    @Test
    fun `exit closes with no prompt`() {
        assertTrue(provider().exitPrompt() is IgmSettingsExit.Close)
    }
}