package org.dolphinemu.dolphinemu.cannoli

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscSwitcherTest {

    private fun switcher(paths: List<String>, changes: MutableList<String> = mutableListOf()) =
        DiscSwitcher(paths, changeDisc = { changes.add(it) }) to changes

    @Test
    fun `disk count reflects the disc list`() {
        val (s, _) = switcher(listOf("/d1", "/d2"))
        assertEquals(2, s.getDiskCount())
    }

    @Test
    fun `single or no disc reports one disc`() {
        assertEquals(1, switcher(emptyList()).first.getDiskCount())
        assertEquals(1, switcher(listOf("/only")).first.getDiskCount())
    }

    @Test
    fun `setDiskIndex changes the disc and updates the index`() {
        val (s, changes) = switcher(listOf("/d1", "/d2"))
        s.setDiskIndex(1)
        assertEquals(listOf("/d2"), changes)
        assertEquals(1, s.getDiskIndex())
    }

    @Test
    fun `out of range index is ignored`() {
        val (s, changes) = switcher(listOf("/d1", "/d2"))
        s.setDiskIndex(5)
        assertTrue(changes.isEmpty())
        assertEquals(0, s.getDiskIndex())
    }

    @Test
    fun `disc label is one-based`() {
        assertEquals("Disc 2", switcher(listOf("/d1", "/d2")).first.getDiskLabel(1))
    }
}