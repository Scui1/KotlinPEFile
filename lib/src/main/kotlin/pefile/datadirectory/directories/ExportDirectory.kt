package pefile.datadirectory.directories

import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.EXPORT_DIRECTORY

data class Export(val rawFuncAddress: Int, val name: String)

data class ExportDirectory(private val peFile: PEFile, private val rawAddress: Int) : DataDirectory(EXPORT_DIRECTORY) {

    private val numFunctions = peFile.readInt(rawAddress + 0x14)
    private val numNames = peFile.readInt(rawAddress + 0x18)
    private val addressOfFunctions = peFile.convertVirtualOffsetToRawOffset(peFile.readInt(rawAddress + 0x1C))
    private val addressOfNames = peFile.convertVirtualOffsetToRawOffset(peFile.readInt(rawAddress + 0x20))

    private val exports = mutableListOf<Export>()
    init {
        for (i in 0..<numFunctions) {
            val functionRawAddress = peFile.convertVirtualOffsetToRawOffset(peFile.readInt(addressOfFunctions + i * 4))
            val functionNameAddress =  peFile.convertVirtualOffsetToRawOffset(peFile.readInt(addressOfNames + i * 4))
            val functionName = peFile.readString(functionNameAddress)
            exports.add(Export(functionRawAddress, functionName))
        }
    }

    fun getExportByName(name: String): Export? {
        return exports.find { it.name == name }
    }
}