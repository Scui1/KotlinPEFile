package pefile

class PEOffsetReader(private val bytes: ByteArray) {

    fun readLong(base: Int): Long {
        return readLongWithSize(base, 8)
    }

    fun readInt(base: Int): Int {
        return readIntWithSize(base, 4)
    }

    fun readShort(base: Int): Int = readIntWithSize(base, 2)

    fun readLongWithSize(base: Int, size: Int): Long {
        return convertLittleEndianByteArrayToLong(read(base, size))
    }

    fun readIntWithSize(base: Int, size: Int): Int {
        return convertLittleEndianByteArrayToInt(read(base, size))
    }

    fun readString(base: Int, size: Int): String {
        return String(read(base, size)).trim { it <= ' ' } // remove null characters
    }

    fun read(base: Int, size: Int): ByteArray {
        return bytes.copyOfRange(base, base + size)
    }

    private fun convertLittleEndianByteArrayToLong(byteArray: ByteArray): Long {
        var result = 0L
        byteArray.reversedArray().forEach { byte ->
            result = (result shl 8) or (byte.toUByte().toLong() and 0xFF)
        }
        return result
    }


    private fun convertLittleEndianByteArrayToInt(byteArray: ByteArray): Int  {
        var result = 0
        byteArray.reversedArray().forEach { byte ->
            result = (result shl 8) + (byte.toUByte() and 0xFF.toUByte()).toInt()
        }
        return result
    }
}