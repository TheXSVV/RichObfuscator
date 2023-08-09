package code.thexsvv.richobfuscator.processor

import org.objectweb.asm.tree.ClassNode
import java.io.File

interface Processor {
    fun processFile(file: File): List<ClassNode>;
    fun processClass(bytes: ByteArray): ClassNode;

    fun dumpFile(input: File, output: File, classNodes: List<ClassNode>);
    fun dumpClass(classNode: ClassNode): ByteArray;
}
