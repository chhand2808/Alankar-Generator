package org.example.kmpproj1

import kotlin.random.Random

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        val firstWord = if (Random.nextBoolean()) "Hi!" else "Hello!"

        return "$firstWord Guess what this is! > ${platform.name.reversed()}!\n${daysPhrase()}"
    }
}