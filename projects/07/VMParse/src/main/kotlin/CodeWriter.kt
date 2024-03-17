import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class CodeWriter {
    private var parser: Parser = Parser()
    private var filename = ""
    private var index = 0

    fun setFileName(filename: String) {
        parser = Parser(filename)
        this.filename = filename
    }

    private fun processJMP(judge: String, index: Int): List<String> {
        return listOf("@SP",
                "AM=M-1",
                "D=M",
                "A=A-1",
                "D=M-D",
                "@TRUE$index",
                "D;$judge",
                "@SP",
                "AM=M-1",
                "M=0",
                "@SP",
                "M=M+1",
                "@CONTINUE$index",
                "0;JMP",
                "(TRUE$index)",
                "@SP",
                "AM=M-1",
                "M=-1",
                "@SP",
                "M=M+1",
                "(CONTINUE$index)")
    }

    private fun processArithmetic(command: String): List<String> {
        when (command) {
            "add" -> {
                return listOf(
                    "@SP",
                    "AM=M-1",
                    "D=M",
                    "A=A-1",
                    "M=M+D")
            }
            "sub" -> {
                return listOf(
                    "@SP",
                    "AM=M-1",
                    "D=M",
                    "A=A-1",
                    "M=M-D")
            }
            "neg" -> return listOf(
                "@SP",
                "AM=M-1",
                "M=-M",
                "@SP",
                "M=M+1")

            "eq" -> return processJMP("JEQ", index++)
            "gt" -> return processJMP("JGT", index++)
            "lt" -> return processJMP("JLT", index++)

            "and" -> return listOf(
                "@SP",
                "AM=M-1",
                "D=M",
                "A=A-1",
                "M=M&D")
            "or" -> return listOf(
                "@SP",
                "AM=M-1",
                "D=M",
                "A=A-1",
                "M=M|D")
            "not" -> return listOf(
                "@SP",
                "AM=M-1",
                "M=!M",
                "@SP",
                "M=M+1")
        }
        return listOf()
    }

    private fun processPush(segment: String, index: Int, filename: String): List<String> {
        when (segment) {
            "argument" -> return listOf(
                "@ARG",
                "D=M",
                "@$index",
                "A=D+A",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "local" -> return listOf(
                "@LCL",
                "D=M",
                "@$index",
                "A=D+A",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "static" -> return listOf(
                "@$filename.$index",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "constant" -> return listOf(
                "@$index",
                "D=A",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "this" -> return listOf(
                "@THIS",
                "D=M",
                "@$index",
                "A=D+A",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "that" -> return listOf(
                "@THAT",
                "D=M",
                "@$index",
                "A=D+A",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
            "pointer" -> {
                val type = if (index == 0) {
                    "THIS"
                } else {
                    "THAT"
                }
                return listOf(
                    "@$type",
                    "D=M",
                    "@SP",
                    "A=M",
                    "M=D",
                    "@SP",
                    "M=M+1")
            }
            else -> return listOf(
                "@R5",
                "D=A",
                "@$index",
                "A=D+A",
                "D=M",
                "@SP",
                "A=M",
                "M=D",
                "@SP",
                "M=M+1")
        }
    }

    private fun proceessPop(segment: String, index: Int, filename: String): List<String> {
        when (segment) {
            "argument" -> return listOf(
                "@ARG",
                "D=M",
                "@$index",
                "D=D+A",
                "@R13",
                "M=D",
                "@SP",
                "AM=M-1",
                "D=M",
                "@R13",
                "A=M",
                "M=D")
            "static" -> {
                return listOf(
                    "@SP",
                    "AM=M-1",
                    "D=M",
                    "@$filename.$index",
                    "M=D")
            }
            "local" -> return listOf(
                "@LCL",
                "D=M",
                "@$index",
                "D=D+A",
                "@R13",
                "M=D",
                "@SP",
                "AM=M-1",
                "D=M",
                "@R13",
                "A=M",
                "M=D")
            "this" -> return listOf(
                "@THIS",
                "D=M",
                "@$index",
                "D=D+A",
                "@R13",
                "M=D",
                "@SP",
                "AM=M-1",
                "D=M",
                "@R13",
                "A=M",
                "M=D")
            "that" -> return listOf(
                "@THAT",
                "D=M",
                "@$index",
                "D=D+A",
                "@R13",
                "M=D",
                "@SP",
                "AM=M-1",
                "D=M",
                "@R13",
                "A=M",
                "M=D")
            "pointer" -> {
                val type = if (index == 0) {
                    "THIS"
                } else {
                    "THAT"
                }
                return listOf(
                    "@$type",
                    "D=A",
                    "@R13",
                    "M=D",
                    "@SP",
                    "AM=M-1",
                    "D=M",
                    "@R13",
                    "A=M",
                    "M=D")
            }
            else -> return listOf(
                "@R5",
                "D=A",
                "@$index",
                "D=D+A",
                "@R13",
                "M=D",
                "@SP",
                "AM=M-1",
                "D=M",
                "@R13",
                "A=M",
                "M=D")
        }
    }

    fun processCommand(filename: String) {
        parser.advance()
        val lists = mutableListOf<String>()
        while (parser.hasMoreCommands()) {
            when (parser.commandType()) {
                CommandType.C_PUSH -> lists.addAll(processPush(parser.arg1(), parser.arg2(), filename))
                CommandType.C_POP -> lists.addAll(proceessPop(parser.arg1(), parser.arg2(), filename))
                CommandType.C_LABEL -> TODO()
                CommandType.C_GOTO -> TODO()
                CommandType.C_IF -> TODO()
                CommandType.C_FUNCTION -> TODO()
                CommandType.C_RETURN -> TODO()
                CommandType.C_CALL -> TODO()
                CommandType.C_ARITHMETIC -> lists.addAll(processArithmetic(parser.arg1()))
            }
            parser.advance()
        }
        val file = File(filename)
        val writer = BufferedWriter(FileWriter(file))
        for (str in lists) {
            writer.write(str)
            writer.newLine()
        }
        writer.close()
    }
}