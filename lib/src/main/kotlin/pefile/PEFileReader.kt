package pefile

import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

private val logger = LoggerFactory.getLogger("PEFileReader")

fun readPEFromFile(fileName: String): PEFile? {
    val fileBytes = try {
        File(fileName).readBytes()
    } catch (e: IOException) {
        logger.error("PEFile $fileName couldn't be read: ${e.message}")
        return null
    }

    if (fileBytes.isEmpty()) {
        logger.error("PEFile $fileName couldn't be read")
        return null
    }

    val peFile = try {
        PEFile(fileBytes, fileName)
    } catch (exception: InvalidPEFileException) {
        logger.error("PEFile $fileName is not a valid pe file: ${exception.message}")
        return null
    }

    return peFile
}