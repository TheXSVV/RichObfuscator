package code.thexsvv.richobfuscator.transformer

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.utils.ASMUtils
import org.objectweb.asm.tree.ClassNode

abstract class Transformer(
    val name: String
) : ASMUtils() {

    var enabled = false;

    abstract fun addSettings(formBuilder: FormBuilder);
    abstract fun transform(classNode: ClassNode);
}
