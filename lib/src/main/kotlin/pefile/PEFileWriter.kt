package pefile

import java.io.File
import java.io.IOException

fun PEFile.writeToFile(fileName: String): File? {
    return try {
        File(fileName).also { it.writeBytes(this.bytes) }
    } catch (e: IOException) {
        return null
    }
}