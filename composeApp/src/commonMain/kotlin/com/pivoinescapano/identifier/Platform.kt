package com.pivoinescapano.identifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform