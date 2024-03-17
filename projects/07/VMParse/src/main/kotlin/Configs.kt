enum class  CommandType {
    C_PUSH,
    C_POP,
    C_LABEL,
    C_GOTO,
    C_IF,
    C_FUNCTION,
    C_RETURN,
    C_CALL,
    C_ARITHMETIC,
}
val commandTypeMapPrefix: Map<CommandType, String> = mapOf(
    CommandType.C_PUSH to "push",
    CommandType.C_POP to "pop",
    CommandType.C_LABEL to "label",
    CommandType.C_GOTO to "goto",
    CommandType.C_IF to "if",
    CommandType.C_FUNCTION to "function",
    CommandType.C_RETURN to "return",
    CommandType.C_CALL to "call",
    CommandType.C_ARITHMETIC to ""
)