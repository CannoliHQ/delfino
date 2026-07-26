package org.dolphinemu.dolphinemu.cannoli

object DolphinOptionCatalog {
    data class Entry(
        val key: String,
        val label: String,
        val file: String,
        val section: String,
        val configKey: String,
        val default: Int,
        // Ordered (intValue, label); cycling steps this list and wraps.
        val options: List<Pair<Int, String>>,
    )

    val entries = listOf(
        Entry(
            key = "internal_resolution",
            label = "Internal Resolution",
            file = "GFX", section = "Settings", configKey = "InternalResolution", default = 1,
            options = listOf(
                0 to "Auto",
                1 to "Native (1x)",
                2 to "2x",
                3 to "3x",
                4 to "4x",
                5 to "5x",
                6 to "6x",
            ),
        ),
        Entry(
            key = "aspect_ratio",
            label = "Aspect Ratio",
            file = "GFX", section = "Settings", configKey = "AspectRatio", default = 0,
            options = listOf(
                0 to "Auto",
                1 to "Force 16:9",
                2 to "Force 4:3",
                3 to "Stretch",
            ),
        ),
    )
}
