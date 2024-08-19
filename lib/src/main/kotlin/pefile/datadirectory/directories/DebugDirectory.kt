package pefile.datadirectory.directories

import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import java.util.*
import kotlin.text.HexFormat

data class DebugDirectoryEntry(
    val timeDateStamp: Int,
    val type: DebugDirectoryType,
    val size: Int,
    val pointerToRawData: Int
)

enum class DebugDirectoryType(val value: Int) {
    IMAGE_DEBUG_TYPE_UNKNOWN(0),
    IMAGE_DEBUG_TYPE_COFF(1),
    IMAGE_DEBUG_TYPE_CODEVIEW(2),
    IMAGE_DEBUG_TYPE_FPO(3),
    IMAGE_DEBUG_TYPE_MISC(4),
    IMAGE_DEBUG_TYPE_EXCEPTION(5),
    IMAGE_DEBUG_TYPE_FIXUP(6),
    IMAGE_DEBUG_TYPE_OMAP_TO_SRC(7),
    IMAGE_DEBUG_TYPE_OMAP_FROM_SRC(8),
    IMAGE_DEBUG_TYPE_BORLAND(9),
    IMAGE_DEBUG_TYPE_RESERVED10(10),
    IMAGE_DEBUG_TYPE_CLSID(11),
    IMAGE_DEBUG_TYPE_VC_FEATURE(12),
    IMAGE_DEBUG_TYPE_POGO(13),
    IMAGE_DEBUG_TYPE_ILTCG(14),
    IMAGE_DEBUG_TYPE_MPX(15),
    IMAGE_DEBUG_TYPE_REPRO(16),
    IMAGE_DEBUG_TYPE_SPGO(18),
    IMAGE_DEBUG_TYPE_EX_DLLCHARACTERISTICS(20);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value }
    }
}

data class DebugDirectory(private val peFile: PEFile, val entries: List<DebugDirectoryEntry>) :
    DataDirectory(DEBUG_DIRECTORY) {

    fun getCodeViewDateTimestamp(): Int {
        return entries.find { it.type == DebugDirectoryType.IMAGE_DEBUG_TYPE_CODEVIEW }?.timeDateStamp ?: 0
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getCodeViewSignature(): String {
        val codeViewEntry = entries.find { it.type == DebugDirectoryType.IMAGE_DEBUG_TYPE_CODEVIEW } ?: return ""
        val signatureBytes = peFile.read(codeViewEntry.pointerToRawData + 4, 16)
        val hexBytes = signatureBytes.map { it.toUByte().toHexString(HexFormat.UpperCase) }

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