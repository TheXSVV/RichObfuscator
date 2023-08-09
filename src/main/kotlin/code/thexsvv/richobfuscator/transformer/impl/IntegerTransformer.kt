package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import code.thexsvv.richobfuscator.transformer.impl.integer.NormalIntegerTransformer
import code.thexsvv.richobfuscator.utils.ASMUtils
import org.objectweb.asm.tree.ClassNode
import javax.swing.JComboBox

class IntegerTransformer : Transformer("Integer encryption") {

    private val comboBox = JComboBox(arrayOf("normal"));
    private val arrayTransformer = NormalIntegerTransformer();

    override fun addSettings(formBuilder: FormBuilder) {
        formBuilder.addLabeledComponent("Integer encryption type:", comboBox);
    }

    override fun transform(classNode: ClassNode) {
        when (comboBox.selectedItem) {
            "normal" -> arrayTransformer.transform(classNode);
        }
    }

    abstract class IntegerTransformer : ASMUtils() {
        abstract fun transform(classNode: ClassNode);
    }
}
