package pefile

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PEFile")

class PEFile(val bytes: ByteArray) {
    private val reader = PEOffsetReader(bytes)
    init {
        if (!hasValidDosHeader())
            throw InvalidPEFileException("DOS Header is invalid")
    }
    private val machineType = constructMachineType()
    val architecture = constructArchitecture()
    private val sections = getModuleSections()

    fun getSectionByName(name: String): Section? {
        return sections.find { it.name == name }
    }

    fun convertRawOffsetToVirtualOffset(offset: Int, sectionName: String): Int {
        // fix difference between virtual and raw address
        val section = getSectionByName(sectionName)
        if (section == null) {
            logger.warn("Failed to find section called '$sectionName', this shouldn't happen")
            return 0
        }

        val virtualRawDifference = section.virtualBase - section.rawBase
        return offset + virtualRawDifference
    }

    fun convertVirtualOffsetToRawOffset(offset: Int): Int  {
        val section = getSectionByVirtualAddress(offset)
        if (section == null) {
            logger.warn("Couldn't find section for address 0x${offset.toString(16)}")
            return 0
        }

        val rawVirtualDifference = section.rawBase - section.virtualBase
        return offset + rawVirtualDifference
    }

    fun write(base: Int, bytes: ByteArray) {
        bytes.copyInto(this.bytes, base)
    }

    fun readIntVirtualWithBase(base: Int): Int {
        return readIntVirtual(base - getImageBase())
    }

    fun readIntVirtual(base: Int): Int {
        val toRead = convertVirtualOffsetToRawOffset(base)
        if (toRead == 0)
            return 0

        return reader.readInt(toRead)
    }

    /**
     * read a virtual address that also has the imagebase in it
     */
    fun readVirtualWithBase(base: Int, size: Int): ByteArray {
        return readVirtual(base - getImageBase(), size)
    }

    fun readVirtual(base: Int, size: Int): ByteArray {
        val toRead = convertVirtualOffsetToRawOffset(base)
        if (toRead == 0)
            return byteArrayOf()

        return reader.read(toRead, size)
    }

    fun read(base: Int, size: Int): ByteArray {
        return reader.read(base, size)
    }

    fun readInt(base: Int): Int {
        return reader.readInt(base)
    }

    fun readIntWithSize(base: Int, size: Int): Int {
        return reader.readIntWithSize(base, size)
    }

    fun getImageBase(): Int {
        return readInt(getPeHeader() + architecture.getImageBaseOffset())
    }

    private fun constructArchitecture(): IPEFileArchitecture {
        return when(machineType) {
             MachineType.INTEL386 -> PEFile32Architecture()
             MachineType.AMD64 -> PEFile64Architecture()
        }
    }

    private fun constructMachineType(): MachineType {
        val readValue = reader.readShort(getPeHeader() + 4)
        return MachineType.fromInt(readValue)
            ?: throw InvalidPEFileException("Not a valid machine architecture type")
    }

    private fun getPeHeader(): Int {
        return readInt(0x3C)
    }

    private fun getNumberOfSections(): Int {
        return reader.readShort(getPeHeader() + 6)
    }

    private fun getDataDirectories(): Int {
        return getPeHeader() + architecture.getDataDirectoriesOffset()
    }

    private fun getNumberOfRvaAndSizes(): Int {
        return readInt(getPeHeader() + architecture.getNumberOfRvaAndSizesOffset())
    }

    private fun hasValidDosHeader(): Boolean {
        val str = reader.readString(0, 2)
        return str == "MZ"
    }

    private fun getSectionByVirtualAddress(address: Int): Section? {
        return sections.find { address >= it.virtualBase && address <= it.virtualBase + it.virtualSize }
    }

    private fun getModuleSections(): List<Section> {
        val sections = mutableListOf<Section>()

        val sectionHeaders = getDataDirectories() + 8 * getNumberOfRvaAndSizes()

        for (i in 0 until getNumberOfSections()) {
            val sectionEntry = sectionHeaders + 40 * i
            val name = reader.readString(sectionEntry, 8)
            val virtualSize = readInt(sectionEntry + 8)
            val rawSize = readInt(sectionEntry + 20)
            val virtualBase = readInt(sectionEntry + 12)
            val rawBase = readInt(sectionEntry + 20)

            val section = Section(name, rawBase, virtualBase, virtualSize, rawSize)
            sections.add(section)
        }

        return sections
    }

    companion object {

    }
}