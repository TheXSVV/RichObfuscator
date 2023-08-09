package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

class FakeExceptionTransformer : Transformer("Fake Exceptions") {

    override fun addSettings(formBuilder: FormBuilder) {}

    override fun transform(classNode: ClassNode) {
        classNode.methods.forEach { methodNode ->
            methodNode.instructions.forEach { insn ->
                if (insn is MethodInsnNode) {
                    val insnList = InsnList();
                    val startLabelNode = LabelNode();
                    val labelNode = LabelNode();

                    insnList.add(InsnNode(Opcodes.ICONST_0));
                    insnList.add(JumpInsnNode(Opcodes.IFEQ, labelNode));
                    insnList.add(startLabelNode);
                    insnList.add(LdcInsnNode("ladno"));
                    insnList.add(TypeInsnNode(Opcodes.NEW, Type.getInternalName(RuntimeException::class.java)));
                    insnList.add(InsnNode(Opcodes.DUP));
                    insnList.add(MethodInsnNode(Opcodes.INVOKESPECIAL, Type.getInternalName(RuntimeException::class.java), "<init>", "(Ljava/lang/String;)V", false));
                    insnList.add(InsnNode(Opcodes.ATHROW));
                    insnList.add(JumpInsnNode(Opcodes.GOTO, startLabelNode));
                    insnList.add(labelNode);

                    methodNode.instructions.insert(insn, insnList);
                }
            }
        }
    }
}
