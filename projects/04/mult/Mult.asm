    @2
    M=0

(LOOP)
    @0
    M=M-1
    D=M

    @END
    D;JLT

    @1
    D=M

    @2
    M=M+D

    @LOOP
    0;JMP

(END)
    @END
    0;JMP