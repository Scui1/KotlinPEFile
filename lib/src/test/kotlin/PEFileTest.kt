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

    @BeforeEach
    fun loadPEFile() {
        peFile = PEFileTest::class.java.getResource("SomePEFile")?.let { PEFile(it.readBytes()) }!!
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

        assertEquals(0x649217F0, debugDirectory.timeDateStamp)
        assertEquals("4D49776C-CEBF-4958-9337-F050763BA123", debugDirectory.formattedSignature())
    }
}