import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.DEBUG_DIRECTORY
import pefile.datadirectory.directories.DebugDirectory
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

        assertEquals(0x633BA1F0, debugDirectory.timeDateStamp)
        assertEquals("07B71E6D-4D55-4DB6-B3EC-4D05512F710A", debugDirectory.formattedSignature())
    }
}