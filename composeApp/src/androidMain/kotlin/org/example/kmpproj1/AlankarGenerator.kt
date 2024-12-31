package com.example.alankargenerator

object AlankarGenerator {

    private val notesMap = mapOf(
        "सा" to 1, "रे" to 2, "ग" to 3, "म" to 4, "प" to 5,
        "ध" to 6, "नि" to 7, "सां" to 8, "रें" to 9, "गं" to 10
    )

    private val reverseNotesMap = mapOf(
        "गं" to 1, "रें" to 2, "सां" to 3, "नि" to 4, "ध" to 5,
        "प" to 6, "म" to 7, "ग" to 8, "रे" to 9, "सा" to 10
    )

    private fun findKeyByValue(map: Map<String, Int>, value: Int): String? {
        return map.entries.find { it.value == value }?.key
    }

    fun generatePattern(inputNotes: List<String>, isReverse: Boolean): List<List<String>> {
        val map = if (isReverse) reverseNotesMap else notesMap
        val numericPattern = inputNotes.mapNotNull { map[it] }
        val output = mutableListOf<List<String>>()

        var stop = false
        for (j in 0..9) {
            if (stop) break

            val line = numericPattern.mapNotNull { num -> findKeyByValue(map, num + j) }
            output.add(line)

            if (line.isNotEmpty() && ((isReverse && line.last() == "सा") || (!isReverse && line.last() == "सां"))) {
                stop = true
            }
        }
        return output
    }
}
