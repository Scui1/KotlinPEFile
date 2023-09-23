package pefile.datadirectory.directories

import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DebugDirectory(val timeDateStamp: Int, val signature: ByteArray) : DataDirectory(DEBUG_DIRECTORY) {
    fun formattedSignature(): String {
        val buffer = ByteBuffer.wrap(signature)
            .order(ByteOrder.LITTLE_ENDIAN)

        val parts = mutableListOf<String>()
        parts.add(buffer.getInt().toUInt().toString(16))
        parts.add(buffer.getShort().toUShort().toString(16))
        parts.add(buffer.getShort().toUShort().toString(16))

        buffer.order(ByteOrder.BIG_ENDIAN)
        val last = buffer.getLong().toULong().toString(16)
        parts.add(last.substring(0, 4))
        parts.add(last.substring(4))

        return parts.joinToString("-")
    }
}