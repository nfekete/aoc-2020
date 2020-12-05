package me.nfekete.adventofcode.y2020.day04

import me.nfekete.adventofcode.y2020.day04.Day04.filterInvalid
import me.nfekete.adventofcode.y2020.day04.Day04.isValidField
import me.nfekete.adventofcode.y2020.day04.Day04.loadPassports
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Day04Test {

    @Test
    fun `validation should pass provided examples`() {
        assertTrue(isValidField("byr" to "2002"))
        assertFalse(isValidField("byr" to "2003"))

        assertTrue(isValidField("hgt" to "60in"))
        assertTrue(isValidField("hgt" to "190cm"))
        assertFalse(isValidField("hgt" to "190in"))
        assertFalse(isValidField("hgt" to "190"))

        assertTrue(isValidField("hcl" to "#123abc"))
        assertFalse(isValidField("hcl" to "#123abz"))
        assertFalse(isValidField("hcl" to "123abc"))

        assertTrue(isValidField("ecl" to "brn"))
        assertFalse(isValidField("ecl" to "wat"))

        assertTrue(isValidField("pid" to "000000001"))
        assertFalse(isValidField("pid" to "0123456789"))
    }

    @Test
    fun `all invalid examples should lead to count 0`() {
        assertEquals(loadPassports("sample-invalid.txt").filterInvalid().count(), 0)
    }

    @Test
    fun `all valid examples should lead to count 4`() {
        assertEquals(loadPassports("sample-valid.txt").filterInvalid().count(), 4)
    }

}
