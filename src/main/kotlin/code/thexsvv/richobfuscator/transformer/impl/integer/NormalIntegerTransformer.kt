package code.thexsvv.richobfuscator.transformer.impl.integer

import code.thexsvv.richobfuscator.transformer.impl.IntegerTransformer
import org.apache.commons.lang3.RandomStringUtils
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.lang.invoke.LambdaMetafactory
import java.util.*

import java.util.concurrent.atomic.AtomicInteger

class NormalIntegerTransformer : IntegerTransformer.IntegerTransformer() {

    override fun transform(classNode: ClassNode) {
        val arraySize = AtomicInteger(1);
        classNode.methods.forEach { methodNode ->
            Arrays.stream(methodNode.instructions.toArray())
                .filter { insn -> getInt(insn) != -1 }
                .forEach { arraySize.getAndIncrement() }
        }

        val fieldNode = FieldNode(
            ACC_PUBLIC or ACC_STATIC or ACC_FINAL, RandomStringUtils.randomAlphabetic(15), Type.getDescriptor(
                IntArray::class.java
            ), null, null
        );
        classNode.fields.add(fieldNode);
        
        applyToMethod(classNode, "<clinit>", InsnList().apply {
            add(LabelNode());
            add(pushInt(arraySize.get()));
            add(IntInsnNode(NEWARRAY, T_INT));
            add(
                FieldInsnNode(
                    PUTSTATIC, classNode.name, fieldNode.name, Type.getDescriptor(
                        IntArray::class.java
                    )
                )
            );
        });

        val pushList = InsnList();
        var index = 0;
        for (methodNode in classNode.methods) {
            if (methodNode.name.equals("<init>") || methodNode.name.equals("<clinit>"))
                continue;
            if (Arrays.stream(methodNode.instructions.toArray())
                    .filter { insn -> insn is InvokeDynamicInsnNode }
                    .anyMatch { idin ->
                        (idin as InvokeDynamicInsnNode).bsm.owner.equals(Type.getInternalName(LambdaMetafactory::class.java))
                    }
            ) continue;

            for (insn in methodNode.instructions) {
                val value: Int = getInt(insn);
                if (value != -1) {
                    pushList.add(LabelNode());
                    pushList.add(
                        FieldInsnNode(
                            GETSTATIC,
                            classNode.name,
                            fieldNode.name,
                            Type.getDescriptor(IntArray::class.java)
                        )
                    );
                    pushList.add(pushInt(index)); // key
                    pushList.add(pushInt(value)); // value
                    pushList.add(InsnNode(IASTORE));

                    val replaceList = InsnList();
                    replaceList.add(
                        FieldInsnNode(
                            GETSTATIC,
                            classNode.name,
                            fieldNode.name,
                            Type.getDescriptor(IntArray::class.java)
                        )
                    );
                    replaceList.add(pushInt(index));
                    replaceList.add(InsnNode(IALOAD));

                    methodNode.instructions.insert(insn, replaceList);
                    methodNode.instructions.remove(insn);

                    index++;
                }
            }
        }

        applyToMethod(classNode, "<clinit>", pushList);
    }
}
