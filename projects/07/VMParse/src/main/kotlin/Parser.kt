import java.io.File


class Parser(filename: String = "") {
    private var fileLines: List<String>
    private var fileIndex = -1;
    private val command: String
        get() = run {
            if (fileIndex >= 0 && fileIndex < fileLines.size) {
                return fileLines[fileIndex]
            }
            return ""
        }
    private fun filterCommand(cmd: String): String {
        val filtercmd = cmd.trimStart().trimEnd()
        if (filtercmd.isNotEmpty() && filtercmd[0] == '/') {
            return ""
        }
        return filtercmd
    }
    fun hasMoreCommands(): Boolean {
        return command.isNotEmpty()
    }

    fun advance() {
        this.fileIndex ++
    }

    fun commandType(): CommandType {
        val cmdTypes = commandTypeMapPrefix.filter {
            command.split(' ')[0] == it.value
        }.map { it.key  }
        return if (cmdTypes.isEmpty()) {
            CommandType.C_ARITHMETIC
        } else {
            cmdTypes[0]
        }
    }

    fun arg1(): String {
        val splitCmd = command.split(' ')
        if (splitCmd.size == 1) {
            return splitCmd[0]
        }
        return splitCmd[1]
    }

    fun arg2(): Int {
        val splitCmd = command.split(' ')
        if (splitCmd.size == 1) {
            return 0
        }
        return splitCmd[2].toInt()
    }

    init {
        fileLines = if(filename.isNotEmpty()) {
            File(filename).readLines().map {
                filterCommand(it)
            }.filter { it.isNotEmpty() }
        } else {
            listOf()
        }
    }
}