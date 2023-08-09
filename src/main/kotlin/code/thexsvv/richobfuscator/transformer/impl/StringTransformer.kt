package code.thexsvv.richobfuscator.transformer.impl

import code.thexsvv.richobfuscator.Encrypt
import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.Transformer
import code.thexsvv.richobfuscator.utils.ZalgoUtils
import org.apache.commons.lang3.RandomStringUtils
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.lang.invoke.LambdaMetafactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class StringTransformer : Transformer("String encryption") {

    companion object {
        val decryptName = ZalgoUtils.generateZalgo(50);
        val packageName = StringBuilder();

        init {
            for (i in 0 until 15)
                packageName.append("\n\n/");
        }
    }

    override fun addSettings(formBuilder: FormBuilder) {}

    override fun transform(classNode: ClassNode) {
        val fieldNode = FieldNode(ACC_PUBLIC or ACC_STATIC or ACC_FINAL, RandomStringUtils.randomAlphabetic(15), Type.getDescriptor(MutableMap::class.java), null, null);
        classNode.fields.add(fieldNode);

        val pushList = InsnList();
        val index = AtomicInteger();
        classNode.methods.stream()
                .filter { methodNode -> !methodNode.name.equals("<init>") }
                .filter { methodNode -> !methodNode.name.equals("<clinit>") }
                .filter { methodNode -> !methodNode.name.equals(decryptName) }
                .filter { methodNode ->
                    Arrays.stream(methodNode.instructions.toArray())
                            .filter { insn -> insn is InvokeDynamicInsnNode }
                            .noneMatch { idin -> (idin as InvokeDynamicInsnNode).bsm.owner.equals(Type.getInternalName(LambdaMetafactory::class.java)) }
                }
                .forEach { methodNode ->
                    Arrays.stream(methodNode.instructions.toArray())
                            .filter { insn -> insn is LdcInsnNode && insn.cst is String }
                            .map { insn -> insn as LdcInsnNode }
                            .forEach { insn ->
                                val insnMapKey = ZalgoUtils.generateZalgo(30);
                                val value = insn.cst as String;

                                pushList.add(LabelNode());
                                pushList.add(FieldInsnNode(GETSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                                pushList.add(LdcInsnNode(insnMapKey));
                                pushList.add(LdcInsnNode(Encrypt.encrypt(value)));
                                pushList.add(LdcInsnNode(ZalgoUtils.generateZalgo(15)));
                                pushList.add(MethodInsnNode(INVOKESTATIC, packageName.toString()+"a", decryptName, "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"));
                                pushList.add(MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(MutableMap::class.java), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"));
                                pushList.add(InsnNode(POP));

                                val replaceList = InsnList();
                                replaceList.add(FieldInsnNode(GETSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                                replaceList.add(LdcInsnNode(insnMapKey));
                                replaceList.add(MethodInsnNode(INVOKEINTERFACE, Type.getInternalName(MutableMap::class.java), "get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
                                replaceList.add(TypeInsnNode(CHECKCAST, Type.getInternalName(String::class.java)));
                                //replaceList.add(new InsnNode(POP));
                                methodNode.instructions.insert(insn, replaceList);
                                methodNode.instructions.remove(insn);
                                index.getAndIncrement();
                            }
                }

        applyToMethod(classNode, "<clinit>", InsnList().apply {
            add(TypeInsnNode(NEW, Type.getInternalName(HashMap::class.java)));
            add(InsnNode(DUP));
            add(MethodInsnNode(INVOKESPECIAL, Type.getInternalName(HashMap::class.java), "<init>", "()V"));
            add(FieldInsnNode(PUTSTATIC, classNode.name, fieldNode.name, Type.getDescriptor(MutableMap::class.java)));

            add(pushList);
        });
    }
}
