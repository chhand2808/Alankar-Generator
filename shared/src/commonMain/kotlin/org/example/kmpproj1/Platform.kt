package org.example.kmpproj1

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform