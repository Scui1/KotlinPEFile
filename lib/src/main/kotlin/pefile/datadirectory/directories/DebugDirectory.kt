package pefile.datadirectory.directories

import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import java.util.*
import kotlin.text.HexFormat

data class DebugDirectory(val timeDateStamp: Int, val signature: ByteArray) : DataDirectory(DEBUG_DIRECTORY) {
    @OptIn(ExperimentalStdlibApi::class)
    fun formattedSignature(): String {
        val hexBytes = signature.map { it.toUByte().toHexString(HexFormat.UpperCase) }

        return listOf(
            hexBytes.subList(0, 4).reversed().joinToString(""),
            hexBytes.subList(4, 6).reversed().joinToString(""),
            hexBytes.subList(6, 8).reversed().joinToString(""),
            hexBytes.subList(8, 10).joinToString(""),
            hexBytes.subList(10, 16).joinToString("")
        )
            .joinToString("-")
            .uppercase(Locale.getDefault())
    }
}