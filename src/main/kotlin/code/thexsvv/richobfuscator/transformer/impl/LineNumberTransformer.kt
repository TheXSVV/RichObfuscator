package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LineNumberNode
import java.util.*

class LineNumberTransformer : Transformer("Line Number Remove") {

    override fun addSettings(formBuilder: FormBuilder) {}

    override fun transform(classNode: ClassNode) {
        classNode.methods.stream().forEach { methodNode ->
            Arrays.stream(methodNode.instructions.toArray())
                    .filter { insn -> insn is LineNumberNode }
                    .forEach { insn -> methodNode.instructions.remove(insn) }
        }
    }
}
