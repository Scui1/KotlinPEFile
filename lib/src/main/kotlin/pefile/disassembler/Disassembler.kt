package pefile.disassembler

import com.github.icedland.iced.x86.Instruction
import com.github.icedland.iced.x86.dec.ByteArrayCodeReader
import com.github.icedland.iced.x86.dec.Decoder
import com.github.icedland.iced.x86.fmt.StringOutput
import com.github.icedland.iced.x86.fmt.intel.IntelFormatter
import pefile.PEFile

data class DisassembledInstruction(
    val rva: Long,
    val addressWithImageBase: Long,
    val opCodeBytes: ByteArray,
    val instruction: String
) {
    override fun toString(): String {
        val rvaString = rva.toString(16).uppercase()
        val addressString = addressWithImageBase.toString(16).uppercase()
        val bytesString = opCodeBytes.joinToString(" ") {
            it.toUByte().toString(16).uppercase().padStart(2, '0')
        }.padEnd(40, ' ')

        return "$addressString (+$rvaString) $bytesString $instruction"
    }
}

fun PEFile.disassembleAt(rawOffset: Long, size: Int): List<DisassembledInstruction> {
    val virtualAddress = this.convertRawOffsetToVirtualOffset(rawOffset.toInt())
    val imageBase = this.getImageBase64()
    val bytesToDisassemble = this.read(rawOffset.toInt(), size)

    val codeReader = ByteArrayCodeReader(bytesToDisassemble)
    val decoder = Decoder(64, codeReader)
    decoder.ip = virtualAddress.toLong()

    val instructions = mutableListOf<Instruction>()
    while (decoder.ip < virtualAddress + size) {
        instructions.add(decoder.decode())
    }

    val asmFormatter = IntelFormatter()
    asmFormatter.options.firstOperandCharIndex = 10

    return instructions.map { instruction ->
        val relativeIp = (instruction.ip - virtualAddress).toInt()
        val address = instruction.ip + imageBase
        val instructionBytes = bytesToDisassemble.slice(relativeIp until relativeIp + instruction.length)

        val formattedInstructionOutput = StringOutput()
        asmFormatter.format(instruction, formattedInstructionOutput)

        DisassembledInstruction(
            instruction.ip,
            address,
            instructionBytes.toByteArray(),
            formattedInstructionOutput.toString()
        )
    }
}

fun PEFile.disassembleToString(offset: Long, size: Int): String {
    return disassembleAt(offset, size).joinToString(separator = "\n")
}