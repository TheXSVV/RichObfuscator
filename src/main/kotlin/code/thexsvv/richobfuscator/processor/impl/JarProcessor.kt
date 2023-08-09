package code.thexsvv.richobfuscator.processor.impl

import code.thexsvv.richobfuscator.Encrypt
import code.thexsvv.richobfuscator.processor.Processor
import code.thexsvv.richobfuscator.transformer.impl.StringTransformer
import com.google.common.collect.Lists
import com.google.common.io.ByteStreams
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.stream.Collectors

class JarProcessor : Processor {

    override fun processFile(file: File): List<ClassNode> {
        val jarFile = JarFile(file);
        val classes = Collections.list(jarFile.entries())
                .stream()
                .filter { entry -> entry.name.endsWith(".class") }
                .collect(Collectors.toList());
        val classNodes = Lists.newLinkedList<ClassNode>();
        for (entry in classes) {
            val bytes = ByteStreams.toByteArray(jarFile.getInputStream(entry));
            classNodes.add(processClass(bytes));
        }

        jarFile.close();
        return classNodes;
    }

    override fun processClass(bytes: ByteArray): ClassNode {
        val reader = ClassReader(bytes);
        val classNode = ClassNode();
        reader.accept(classNode, 0);

        return classNode;
    }

    override fun dumpFile(input: File, output: File, classNodes: List<ClassNode>) {
        val jarFile = JarFile(input);
        val jarOutputStream = JarOutputStream(Files.newOutputStream(output.toPath()));
        Collections.list(jarFile.entries()).stream()
                .filter { entry -> !entry.name.endsWith(".class") }
                .forEach { entry ->
                    jarOutputStream.putNextEntry(entry);
                    jarOutputStream.write(ByteStreams.toByteArray(jarFile.getInputStream(entry)));
                    jarOutputStream.closeEntry();
                };
        jarFile.close();

        jarOutputStream.putNextEntry(JarEntry(StringTransformer.packageName.toString()+"a.class"));
        jarOutputStream.write(Encrypt.generateEncryptClass(StringTransformer.packageName.toString()+"a", this));
        jarOutputStream.closeEntry();

        for (classNode in classNodes) {
            val bytes = dumpClass(classNode);

            jarOutputStream.putNextEntry(JarEntry("${classNode.name}.class"));
            jarOutputStream.write(bytes);
            jarOutputStream.closeEntry();
        }

        jarOutputStream.close();
    }

    override fun dumpClass(classNode: ClassNode): ByteArray {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);

        return writer.toByteArray();
    }
}
