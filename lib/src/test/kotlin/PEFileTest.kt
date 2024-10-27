import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import pefile.datadirectory.DataDirectoryType.EXPORT_DIRECTORY
import pefile.datadirectory.directories.DebugDirectory
import pefile.datadirectory.directories.ExportDirectory
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PEFileTest {

    lateinit var peFile: PEFile;
    lateinit var x64PeFile: PEFile

    @BeforeEach
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
}