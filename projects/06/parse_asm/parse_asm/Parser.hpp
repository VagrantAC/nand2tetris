//
//  Parser.hpp
//  parse_asm
//
//  Created by VagrantAC on 2024/3/5.
//

#ifndef Parser_hpp
#define Parser_hpp

#include <stdio.h>
#include <string>
#include <fstream>
#include <iostream>
#include <queue>
#include <map>

enum CommandType {
    A_COMMAND,
    C_COMMAND,
    L_COMMAND,
};

class Parser {
private:
    std::ifstream file;
    std::string command;
    std::string simplify(std::string& cmd);
    std::queue<std::string> commands;
    std::map<std::string, int> symbols{
        {"SP", 0},
        {"LCL", 1},
        {"ARG", 2},
        {"THIS", 3},
        {"THAT", 4},
        {"R0", 0},
        {"R1", 1},
        {"R2", 2},
        {"R3", 3},
        {"R4", 4},
        {"R5", 5},
        {"R6", 6},
        {"R7", 7},
        {"R8", 8},
        {"R9", 9},
        {"R10", 10},
        {"R11", 11},
        {"R12", 12},
        {"R13", 13},
        {"R14", 14},
        {"R15", 15},
        {"SCREEN", 16384},
        {"KBD", 24576},
    };
    int num = 16;
    int pc = 0;

public:
    Parser(std::string& filename);
    
    bool hasMoreCommands();
    
    void advance();
    
    CommandType commandType();
    
    std::string symbol();
    
    std::string dest();
    
    std::string comp();
    
    std::string jump();
    
    std::string machineLanguage();
};
#endif /* Parser_hpp */
