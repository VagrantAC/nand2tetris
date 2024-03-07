//
//  Parser.cpp
//  parse_asm
//
//  Created by VagrantAC on 2024/3/5.
//

#include "Parser.hpp"

std::string Parser::simplify(std::string& cmd) {
    std::string new_cmd;
    for (auto &ch: cmd) {
        switch (ch) {
            case ' ':
                continue;
                break;
            case '/':
                return  "";
                break;
            default:
                new_cmd += ch;
                break;
        }
    }
    return new_cmd;
}

Parser::Parser(std::string& filename) {
    file.open(filename);
    std::string cmd;
    while (std::getline(file, cmd)) {
        cmd = simplify(cmd);
        std::cout << cmd << std::endl;
        if (cmd.length()) {
            if (cmd[0] == '(') {
                this->symbols[cmd.substr(1, cmd.length()-2)] =  int(commands.size());
            } else {
                commands.push(cmd);
            }
        }
    }
    file.close();
}
    
bool Parser::hasMoreCommands() {
    return bool(command.size());
}

void Parser::advance() {
    if (commands.empty()) {
        command = "";
    } else {
        command = commands.front();
        commands.pop();
        pc ++;
    }
}

CommandType Parser::commandType() {
    switch (command.at(0)) {
        case '@':
            return A_COMMAND;
        case '(':
            return L_COMMAND;
        default:
            return C_COMMAND;
    }
}

std::string processBinary(int value) {
    std::string str = "";
    for (int i = 0; i < 16; ++ i) {
        str += ((value >> i) & 1) ? "1" : "0";
    }
    std::reverse(str.begin(), str.end());
    return str;
}

int isNumber(std::string& str) {
    int res = 0;
    for (auto& ch: str) {
        if (ch < '0' || ch > '9') {
            return -1;
        }
        res = res * 10 + ch - '0';
    }
    return res;
}

std::string Parser::symbol() {
    if (commandType() == A_COMMAND) {
        std::string value = command.substr(1);
        int num = isNumber(value);
        if (num != -1) {
            return processBinary(num);
        }
        if (!this->symbols.count(value)) {
            int index = this->num;
            this->num ++;
            this->symbols[value] = index;
        }
        return processBinary(this->symbols[value]);
    }
    return command.substr(1, command.length()-2);
}
    
std::string Parser::dest() {
    std::string d = "000";
    
    for (auto& ch: command) {
        switch (ch) {
            case 'A':
                d[0] = '1';
                break;
            case 'D':
                d[1] = '1';
                break;
            case 'M':
                d[2] = '1';
                break;
            case '=':
                return d;
            default:
                break;
        }
    }
    return "000";
}

std::string Parser::comp() {
    std::vector<std::pair<std::string, std::string>> vec{
        std::make_pair("0", "0101010"),
        std::make_pair("1", "0111111"),
        std::make_pair("-1", "0111010"),
        std::make_pair("D", "0001100"),
        std::make_pair("A", "0110000"),
        std::make_pair("M", "1110000"),
        std::make_pair("!D", "0001101"),
        std::make_pair("!A", "0110001"),
        std::make_pair("!M", "1110001"),
        std::make_pair("-D", "0001111"),
        std::make_pair("-A", "0110011"),
        std::make_pair("-M", "1110011"),
        std::make_pair("D+1", "0011111"),
        std::make_pair("A+1", "0110111"),
        std::make_pair("M+1", "1110111"),
        std::make_pair("D-1", "0001110"),
        std::make_pair("A-1", "0110010"),
        std::make_pair("M-1", "1110010"),
        std::make_pair("D+A", "0000010"),
        std::make_pair("D+M", "1000010"),
        std::make_pair("D-A", "0010011"),
        std::make_pair("D-M", "1010011"),
        std::make_pair("A-D", "0000111"),
        std::make_pair("M-D", "1000111"),
        std::make_pair("D&A", "0000000"),
        std::make_pair("D&M", "1000000"),
        std::make_pair("D|A", "0010101"),
        std::make_pair("D|M", "1010101")
    };
    for (auto &pair: vec) {
        if (command.ends_with("="+pair.first) || command.starts_with(pair.first+";")) {
            return pair.second;
        }
    }
    return "0000000";
}

std::string Parser::jump() {
    std::vector<std::pair<std::string, std::string>> vec{
        std::make_pair(";JGT", "001"),
        std::make_pair(";JEQ", "010"),
        std::make_pair(";JGE", "011"),
        std::make_pair(";JLT", "100"),
        std::make_pair(";JNE", "101"),
        std::make_pair(";JLE", "110"),
        std::make_pair(";JMP", "111"),
    };
    for (auto &pair: vec) {
        if (command.ends_with(pair.first)) {
            return pair.second;
        }
    }
    return "000";
}

// dest=comp;jump
std::string Parser::machineLanguage() {
    switch (commandType()) {
        case A_COMMAND:
            return symbol();
        case C_COMMAND:
//            std::cout << command << std::endl;
//            std::cout << comp() << std::endl;
            return "111" + comp() + dest() + jump();
        default:
            break;
    }
    return "";
}
