
import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import pefile.datadirectory.DataDirectoryType.EXPORT_DIRECTORY
import pefile.datadirectory.directories.DebugDirectory
import pefile.datadirectory.directories.ExportDirectory
import pefile.disassembler.disassembleToString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PEFileTest {

    lateinit var peFile: PEFile
    lateinit var x64PeFile: PEFile

    @BeforeTest
    fun loadPEFile() {
        peFile = PEFileTest::class.java.getResource("SomePEFile")?.let { PEFile(it.readBytes()) }!!
        x64PeFile = PEFileTest::class.java.getResource("Somex64PEFile")?.let { PEFile(it.readBytes()) }!!
    }

    @Test
    fun imageBaseWorks() {
        assertEquals(0x180000000, x64PeFile.getImageBase64())
    }

    @Test
    fun sectionsWork() {
        assertNotNull(peFile.getSectionByName(".text"))
        assertNotNull(peFile.getSectionByName(".data"))
    }

    @Test
    fun debugDirectoryWorks() {
        val debugDirectory = peFile.getDataDirectoryByType(DEBUG_DIRECTORY)
        assertIs<DebugDirectory>(debugDirectory)

        assertEquals(0x633BA1F0, debugDirectory.getCodeViewDateTimestamp())
        assertEquals("07B71E6D-4D55-4DB6-B3EC-4D05512F710A", debugDirectory.getCodeViewSignature())
    }

    @Test
    fun exportDirectoryWorks() {
        val dir = peFile.getDataDirectoryByType(EXPORT_DIRECTORY)
        assertIs<ExportDirectory>(dir)
        val export = dir.getExportByName("_runfunc@20")
        assertNotNull(export)
        assertEquals(0x3F48, peFile.convertRawOffsetToVirtualOffset(export.rawFuncAddress))

        val dirX64 = x64PeFile.getDataDirectoryByType(EXPORT_DIRECTORY)
        assertIs<ExportDirectory>(dirX64)
        val exportx64 = dirX64.getExportByName("CreateInterface")
        assertNotNull(exportx64)
        assertEquals(0x54C0, x64PeFile.convertRawOffsetToVirtualOffset(exportx64.rawFuncAddress))
    }

    @Test
    fun testDisassembly() {
        val textSection = x64PeFile.getSectionByName(".text")!!;

        val disassembled = x64PeFile.disassembleToString( textSection.rawBase.toLong(), 0x2F)

        val expected = """
            180001000 (+1000) 48 83 EC 28                              sub       rsp,28h
            180001004 (+1004) 48 8D 0D 4D B0 06 00                     lea       rcx,[6C058h]
            18000100B (+100B) FF 15 27 00 05 00                        call      qword ptr [51038h]
            180001011 (+1011) 48 8D 05 E8 AF 06 00                     lea       rax,[6C000h]
            180001018 (+1018) 48 8D 0D B1 F6 04 00                     lea       rcx,[506D0h]
            18000101F (+101F) 48 89 05 6A C4 06 00                     mov       [6D490h],rax
            180001026 (+1026) 48 83 C4 28                              add       rsp,28h
            18000102A (+102A) E9 65 88 01 00                           jmp       0000000000019894h
        """.trimIndent()
        assertEquals(expected, disassembled)
    }
}