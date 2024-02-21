// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

//// Replace this comment with your code.
    @24576
    D=A
    @1
    M=D

(LOOP)
    @16384
    D=A
    @0
    M=D
    
    @KBD
    D=M

    @SCREEN_BLACK
    D;JNE

    @SCREEN_WHITE
    0;JEQ

(SCREEN_BLACK)
    @0
    D=M
    A=M
    M=-1
    
    @0
    M=D+1
    
    @1
    D=M-D

    @SCREEN_BLACK
    D;JGT


(SCREEN_WHITE)
    @0
    D=M
    A=M
    M=0
    
    @0
    M=D+1
    
    @1
    D=M-D

    @SCREEN_WHITE
    D;JGT