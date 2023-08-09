package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import code.thexsvv.richobfuscator.utils.RandomUtils
import code.thexsvv.richobfuscator.utils.ZalgoUtils
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.objectweb.asm.tree.analysis.BasicValue
import java.util.*

@Suppress("LABEL_NAME_CLASH")
class FlowTransformer : Transformer("Main flow (-noverify)") {

    override fun addSettings(formBuilder: FormBuilder) {}

    override fun transform(classNode: ClassNode) {
        val fieldNode = FieldNode(
            ACC_PUBLIC or ACC_STATIC,
            ZalgoUtils.generateZalgo(40),
            Type.INT_TYPE.descriptor,
            null,
            RandomUtils.nextInt(10000, Int.MAX_VALUE)
        )
        classNode.fields.add(fieldNode);

        classNode.methods.stream()
            .filter { methodNode -> !methodNode.name.equals("<clinit>") }
            .filter { methodNode -> !methodNode.name.equals("<init>") }
            .forEach { methodNode ->
                Arrays.stream(methodNode.instructions.toArray())
                    .filter { insn -> insn is MethodInsnNode }
                    .forEach { insn ->
                        var next: AbstractInsnNode = insn.next;
                        while (next !is LabelNode && next.next != null)
                            next = next.next;
                        if (next !is LabelNode)
                            return@forEach;
                        
                        val after = InsnList();
                        val label1 = LabelNode();
                        val label2 = LabelNode();
                        val label3 = LabelNode();
                        val label4 = LabelNode();
                        val label5 = LabelNode();
                        after.add(LabelNode());

                        when (RandomUtils.nextInt(0, 1)) {
                            0 -> {
                                after.add(
                                    FieldInsnNode(
                                        GETSTATIC,
                                        classNode.name,
                                        fieldNode.name,
                                        fieldNode.desc
                                    )
                                );
                                after.add(pushInt(RandomUtils.nextInt(1000, Int.MAX_VALUE)));
                                after.add(InsnNode(INEG));
                                after.add(JumpInsnNode(IF_ICMPNE, label1));
                                after.add(
                                    FieldInsnNode(
                                        GETSTATIC,
                                        classNode.name,
                                        fieldNode.name,
                                        fieldNode.desc
                                    )
                                );
                                after.add(JumpInsnNode(IFGE, label1));
                                after.add(InsnNode(ACONST_NULL));
                                after.add(InsnNode(ATHROW));
                            }
                            1 -> {
                                after.add(InsnNode(ACONST_NULL));
                                after.add(TypeInsnNode(INSTANCEOF, Type.VOID_TYPE.internalName));
                                after.add(JumpInsnNode(IFEQ, label1));
                                after.add(InsnNode(ACONST_NULL));
                                after.add(InsnNode(ATHROW));
                            }
                        }
                        after.add(label1);

                        after.add(LabelNode());
                        after.add(FieldInsnNode(GETSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                        after.add(
                            LookupSwitchInsnNode(
                                label5,
                                intArrayOf(
                                    RandomUtils.nextInt(10000, Int.MAX_VALUE),
                                    RandomUtils.nextInt(10000, Int.MAX_VALUE),
                                    -RandomUtils.nextInt(10000, Int.MAX_VALUE)
                                ),
                                arrayOf(label2, label3, label4)
                            )
                        );
                        after.add(label2);
                        after.add(InsnNode(NOP));
                        after.add(JumpInsnNode(GOTO, label5));
                        after.add(label3);
                        after.add(pushInt(RandomUtils.nextInt(1000, Int.MAX_VALUE)));
                        after.add(pushInt(RandomUtils.nextInt(1, 500)));
                        after.add(JumpInsnNode(IF_ICMPNE, label5));
                        after.add(LdcInsnNode(ZalgoUtils.generateZalgo(50)));
                        after.add(TypeInsnNode(NEW, Type.getInternalName(RuntimeException::class.java)));
                        after.add(InsnNode(DUP_X1));
                        after.add(InsnNode(SWAP));
                        after.add(
                            MethodInsnNode(
                                INVOKESPECIAL,
                                Type.getInternalName(RuntimeException::class.java),
                                "<init>",
                                "(Ljava/lang/String;)V"
                            )
                        );
                        after.add(InsnNode(ATHROW));

                        //after.add(new FieldInsnNode(PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                        after.add(JumpInsnNode(GOTO, label5));
                        after.add(label4);
                        after.add(InsnNode(ACONST_NULL));
                        after.add(InsnNode(ATHROW));
                        after.add(JumpInsnNode(GOTO, label5));
                        after.add(label5);

                        // pizdec
                        val index = methodNode.maxLocals+3;
                        after.add(pushInt(1));
                        after.add(VarInsnNode(ISTORE, index));
                        after.add(VarInsnNode(ILOAD, index));
                        after.add(JumpInsnNode(IFEQ, label1));
                        after.add(VarInsnNode(ILOAD, index));
                        after.add(pushInt(1));
                        after.add(JumpInsnNode(IF_ICMPNE, label1));

                        methodNode.instructions.insert(insn, after);
                    }
            }
    }

    private fun getFirstLabel(insnList: InsnList): LabelNode? {
        var labelNode: LabelNode? = null
        for (insn in insnList)
            if (insn is LabelNode && labelNode == null)
                labelNode = insn;
        return labelNode;
    }

    private fun getEndLabel(insnList: InsnList): LabelNode? {
        var labelNode: LabelNode? = null;
        for (i in 0 until insnList.size()) {
            val insn = insnList[i];
            if (i != insnList.size() - 1 && insn is LabelNode)
                labelNode = insn;
        }

        return labelNode;
    }

    private fun createIntArray(index: Int, array: Array<IntArray>): InsnList {
        val insnList = InsnList();

        insnList.add(pushInt(array.size));
        insnList.add(TypeInsnNode(ANEWARRAY, "[I"));
        insnList.add(VarInsnNode(ASTORE, index));

        for (i in array.indices) {
            val innerArray = array[i];

            insnList.add(VarInsnNode(ALOAD, index));
            insnList.add(pushInt(i));
            insnList.add(InsnNode(DUP2));
            insnList.add(pushInt(innerArray.size));
            insnList.add(IntInsnNode(NEWARRAY, T_INT));

            for (j in innerArray.indices) {
                insnList.add(InsnNode(DUP));
                insnList.add(pushInt(j));

                if (innerArray[j] == 0)
                    insnList.add(InsnNode(ICONST_0));
                else {
                    val multiplyValue = RandomUtils.nextInt(100, 5000);
                    insnList.add(pushInt(innerArray[j] * multiplyValue));
                    insnList.add(pushInt(multiplyValue));
                    insnList.add(InsnNode(IDIV));
                }

                insnList.add(InsnNode(IASTORE));
            }

            insnList.add(InsnNode(AASTORE));
        }

        return insnList;
    }
}
