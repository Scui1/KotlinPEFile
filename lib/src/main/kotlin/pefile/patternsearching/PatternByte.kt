package patternsearching

data class PatternByte(val value: UByte, val isWildcard: Boolean = false) {
    fun matches(checkByte: UByte): Boolean = isWildcard || value == checkByte
}

fun patternBytesFromString(patternString: String): List<PatternByte> {
    return patternString.split(" ").map {
        if (it == "?" || it == "??")
            PatternByte(0u, true)
        else
            PatternByte(it.toUByte(16), false)
    }
}
