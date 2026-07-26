package org.dolphinemu.dolphinemu.cannoli

interface DolphinConfig {
    fun getInt(file: String, section: String, key: String, default: Int): Int
    fun setInt(file: String, section: String, key: String, value: Int)
    fun save()
}
