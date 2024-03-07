//
//  main.cpp
//  parse_asm
//
//  Created by VagrantAC on 2024/2/29.
//

#include <string>
#include <fstream>
#include <iostream>
#include <algorithm>
#include "Parser.hpp"


int main(int argc, const char * argv[]) {
    std::string filename = "06/pong/PongL.asm";
    std::ofstream outHask("06/pong/PongLGen.hack");
    Parser* parser = new Parser(filename);
    
    parser->advance();
    while (parser->hasMoreCommands()) {
        outHask << parser->machineLanguage() << std::endl;
        parser->advance();
    }
    outHask.close();
    return 0;
}
