package pefile.disassembler

import com.github.icedland.iced.x86.Instruction
import com.github.icedland.iced.x86.dec.ByteArrayCodeReader
import com.github.icedland.iced.x86.dec.Decoder
import com.github.icedland.iced.x86.fmt.StringOutput
import com.github.icedland.iced.x86.fmt.intel.IntelFormatter
import pefile.PEFile

data class DisassembledInstruction(val address: Long, val opCodeBytes: ByteArray, val instruction: String) {
    override fun toString(): String {
        val addressString = address.toString(16).uppercase()
        val bytesString = opCodeBytes.joinToString(" ") {
            it.toUByte().toString(16).uppercase().padStart(2, '0')
        }.padEnd(40, ' ')

        return "$addressString $bytesString $instruction"
    }
}

fun disassembleAt(peFile: PEFile, offset: Long, size: Int): List<DisassembledInstruction> {
    val virtualAddress = peFile.convertRawOffsetToVirtualOffset(offset.toInt())
    val bytesToDisassemble = peFile.read(offset.toInt(), size)

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
        val instructionBytes = bytesToDisassemble.slice(relativeIp until relativeIp + instruction.length)

        val formattedInstructionOutput = StringOutput()
        asmFormatter.format(instruction, formattedInstructionOutput)

        DisassembledInstruction(instruction.ip, instructionBytes.toByteArray(), formattedInstructionOutput.toString())
    }
}

fun disassembleToString(peFile: PEFile, offset: Long, size: Int): String {
    return disassembleAt(peFile, offset, size).joinToString(separator = "\n")
}