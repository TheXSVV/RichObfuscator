# RichObfuscator

<h4 align="center">
    Heavy obfuscation using modern methods with GUI
</h4>

<div align="center">
    <a href="https://kotlinlang.org">
        <img alt="kotlin" src="https://img.shields.io/badge/Language-Kotlin-7f52ff">
    </a>
    <a href="https://asm.ow2.io">
        <img alt="asm" src="https://img.shields.io/badge/API-ASM-blue">
    </a>
</div>

---

### Current transformers:

| Transformer          | Type         | Description                                          | License Type |
|----------------------|--------------|------------------------------------------------------|--------------|
| `String Encryption`  | **normal**   | Encrypting strings using default encryption methods  | Community    |
| `String Encryption`  | **whitebox** | Encrypting strings using white-box encryption method | Enterprice   |
| `Integer Encryption` | **normal**   | Encrypting integers using default math methods       | Community    |
| `Members Hider`      | ------------ | Hides methods in the class for many decompilers      | Community    |
| `Fake Exceptions`    | ------------ | Adding fake jumps with invalid exceptions            | Community    |
| `Main Flow`          | **normal**   | The main flow obfuscation                            | Community    |
| `Main Flow`          | **heavy**    | The main heavy flow obfuscation                      | Enterprice   |
| `Loop Unrolling`     | ------------ | Replaces goto type constructs with equivalent loops  | Enterprice   |
| `Line Number Remove` | ------------ | Removes the line numbers for methods                 | Community    |

### Usage
To use RichObfuscator, follow these steps:

1. Download the jar from releases
2. Change the settings and run obfuscation process

If obfuscation fails submit the issue here

### Building
To build RichObfuscator, follow these steps:
1. Clone git project:
```
git clone https://github.com/TheXSVV/RichObfuscator
```
2. Compile using gradle:
```
cd RichObfuscator
gradle build
```
