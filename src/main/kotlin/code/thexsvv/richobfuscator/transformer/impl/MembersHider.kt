package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import java.util.*

class MembersHider : Transformer("Members Hider") {

    override fun addSettings(formBuilder: FormBuilder) {}

    override fun transform(classNode: ClassNode) {
        if ((classNode.access and ACC_INTERFACE) == 0) {
            classNode.methods.stream()
                .filter { methodNode -> !methodNode.name.startsWith("<") && !methodNode.name.endsWith(">") }
                .filter { methodNode -> (methodNode.access and ACC_NATIVE) == 0 }
                .forEach { methodNode ->
                    methodNode.access = methodNode.access or ACC_BRIDGE;
                    methodNode.access = methodNode.access or ACC_SYNTHETIC;
                }
            classNode.fields.forEach { fieldNode ->
                fieldNode.access = fieldNode.access or ACC_SYNTHETIC;
            }
        }
    }
}
