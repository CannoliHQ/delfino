package org.dolphinemu.dolphinemu.cannoli

import android.view.KeyEvent
import dev.cannoli.igm.BatteryDisplayMode
import dev.cannoli.igm.IGMHostConfig
import dev.cannoli.igm.IgmDisplaySettings
import dev.cannoli.igm.TimeFormatMode
import dev.cannoli.ui.ButtonLabelSet
import dev.cannoli.ui.ConfirmButton

/**
 * Builds the IGM host config from the launch display settings, mirroring ricotta's derivation and
 * adding the geometry fields the pinned IGMHostConfig requires. Falls back to sane defaults when no
 * display settings were provided.
 */
fun buildIgmHostConfig(ds: IgmDisplaySettings?): IGMHostConfig {
    val fontSize = ds?.fontSizeSp ?: 24
    return IGMHostConfig(
        fontSizeSp = fontSize,
        lineHeightSp = fontSize + 10,
        scaleFactor = fontSize / 22f,
        portraitMarginPx = ds?.portraitMarginPx ?: 0,
        geometryWidthPct = ds?.geometryWidthPct ?: 100,
        geometryHeightPct = ds?.geometryHeightPct ?: 100,
        geometryXPct = ds?.geometryXPct ?: 0,
        geometryYPct = ds?.geometryYPct ?: 0,
        showWifi = ds?.showWifi ?: true,
        showBluetooth = ds?.showBluetooth ?: true,
        showVpn = ds?.showVpn ?: false,
        showClock = ds?.showClock ?: true,
        batteryDisplay = ds?.batteryDisplay ?: BatteryDisplayMode.ICON,
        timeFormat = ds?.timeFormat ?: TimeFormatMode.TWELVE_HOUR,
        buttonLabelSet = ds?.buttonLabelSet ?: ButtonLabelSet.PLUMBER,
        confirmButton = ds?.confirmButton ?: ConfirmButton.SOUTH,
        keyCodeName = { KeyEvent.keyCodeToString(it) },
    )
}
