package code.thexsvv.richobfuscator.utils

import org.apache.commons.lang3.RandomStringUtils
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.util.*
import java.util.function.Predicate


open class ASMUtils {

    companion object {
        fun findInsnIndex(list: List<AbstractInsnNode>, predicate: Predicate<in AbstractInsnNode>): Int {
            for (i in list.indices)
                if (predicate.test(list[i]))
                    return i;

            return -1;
        }

        fun findInsnIndex(list: List<AbstractInsnNode>, opcode: Int): Int {
            for (i in list.indices)
                if (i == opcode)
                    return i;

            return -1;
        }

        fun getInt(insn: AbstractInsnNode): Int {
            var ldc = -1;
            if (insn is LdcInsnNode && insn.cst is Int)
                ldc = (insn.cst as Int);

            return when (insn.opcode) {
                ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5 -> insn.opcode - ICONST_0;
                BIPUSH, SIPUSH -> (insn as IntInsnNode).operand;
                LDC -> ldc;
                else -> -1;
            }
        }

        fun pushInt(value: Int): AbstractInsnNode {
            if (value >= -1 && value <= 5)
                return InsnNode(ICONST_0 + value);

            if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                return IntInsnNode(BIPUSH, value);

            if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                return IntInsnNode(SIPUSH, value);

            return LdcInsnNode(value);
        }

        fun arrayToInsnList(instructions: Array<AbstractInsnNode>): InsnList {
            val insnList = InsnList();

            for (instruction in instructions)
                insnList.add(instruction);

            return insnList;
        }

        fun applyToMethod(classNode: ClassNode, name: String, insnList: InsnList) {
            var method = classNode.methods.stream()
                .filter { methodNode -> methodNode.name.equals(name) }
                .findFirst()
                .orElse(null);
            if (method == null) {
                method = MethodNode(ACC_STATIC, name, "()V", null, null);
                classNode.methods.add(method);
            }

            Arrays.stream(method.instructions.toArray())
                .filter { insn -> insn.opcode == RETURN }
                .forEach { insn -> method.instructions.remove(insn) }

            method.instructions.add(insnList);

            method.instructions.add(InsnNode(RETURN));
        }

        fun generateIf(instructions1: Array<AbstractInsnNode>, instructions2: Array<AbstractInsnNode>, equalType: EqualType, instructions: InsnList): InsnList {
            val insnList = InsnList();

            val label = LabelNode();
            val label2 = LabelNode();
            val label3 = LabelNode(); // If end label

            insnList.add(label);
            insnList.add(arrayToInsnList(instructions1));
            insnList.add(arrayToInsnList(instructions2));
            when (equalType) {
                EqualType.STRING_EQ -> insnList.add(MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(String::class.java), "equals", "(Ljava/lang/Object;)Z"));
                EqualType.STRING_NEQ -> insnList.add(MethodInsnNode(INVOKEVIRTUAL, Type.getInternalName(String::class.java), "equals", "(Ljava/lang/Object;)Z"));
                EqualType.INT -> insnList.add(JumpInsnNode(IF_ICMPNE, label3));
                EqualType.NOT_INT -> insnList.add(JumpInsnNode(IF_ICMPEQ, label3));
            }
            if (equalType == EqualType.STRING_EQ || equalType == EqualType.STRING_NEQ)
                insnList.add(JumpInsnNode(if (equalType == EqualType.STRING_EQ) IFEQ else IFNE, label3));

            insnList.add(label2);
            insnList.add(instructions);

            insnList.add(label3);
            return insnList;
        }

        fun generateIf(instructions1: AbstractInsnNode, instructions2: AbstractInsnNode, equalType: EqualType, instructions: InsnList): InsnList {
            return generateIf(arrayOf(instructions1), arrayOf(instructions2), equalType, instructions);
        }

        enum class EqualType {
            STRING_EQ,
            STRING_NEQ,
            INT,
            NOT_INT
        }

        fun createLocalVariable(methodNode: MethodNode, name: String, desc: String, start: LabelNode, end: LabelNode): LocalVariableNode {
            val usedIndexes = mutableSetOf<Int>();
            methodNode.localVariables.forEach { localVariableNode: LocalVariableNode ->
                usedIndexes.add(localVariableNode.index);
            }

            var index = 0;
            while (usedIndexes.contains(index))
                index++;
            usedIndexes.add(index);

            val localVar = LocalVariableNode(name, desc, null, start, end, index);
            methodNode.localVariables.add(localVar);

            return localVar;
        }

        fun getLong(insn: AbstractInsnNode): Long {
            return when (insn.opcode) {
                LCONST_0 -> 0;
                LCONST_1 -> 1;
                LDC -> (insn as LdcInsnNode).cst as Long;
                else -> -1;
            }
        }
    }
}
