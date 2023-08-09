package code.thexsvv.richobfuscator;

import code.thexsvv.richobfuscator.processor.Processor;
import code.thexsvv.richobfuscator.transformer.impl.StringTransformer;
import code.thexsvv.richobfuscator.utils.ZalgoUtils;
import com.google.common.collect.Maps;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

public class Encrypt {

    public static String encrypt(String text) {
        byte[] bytes = text.getBytes();
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (bytes[i] ^ 0xB ^ 0xE);

        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decrypt(String text, String secretKey) {
        byte[] bytes = Base64.getDecoder().decode(text);
        for (int i = 0; i < bytes.length; i++)
            bytes[i] = (byte) (bytes[i] ^ 0xB ^ 0xE);

        return new String(bytes);
    }

    public static byte[] generateEncryptClass(String name, Processor processor) {
        ClassNode classNode = new ClassNode();
        classNode.visit(getJavaVersionAsClassVersion(), Opcodes.ACC_PUBLIC, name, null, "java/lang/Object", null);

        try {
            ClassReader classReader = new ClassReader(Encrypt.class.getResourceAsStream("/code/thexsvv/richobfuscator/Encrypt.class"));
            ClassNode transformClassNode = new ClassNode();
            classReader.accept(transformClassNode, 0);

            MethodNode decryptMethod = transformClassNode.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("decrypt"))
                    .findFirst()
                    .get();
            decryptMethod.name = StringTransformer.Companion.getDecryptName();
            decryptMethod.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
            decryptMethod.localVariables.stream()
                    .filter(localVariableNode -> localVariableNode.desc.equals("Lcode/thexsvv/richobfuscator/Encrypt;"))
                    .map(localVariableNode -> localVariableNode.desc = String.format("L%s;", classNode.name));
            Arrays.stream(decryptMethod.instructions.toArray())
                    .filter(insn -> insn instanceof FieldInsnNode)
                    .map(insn -> (FieldInsnNode) insn)
                    .filter(insn -> insn.owner.equals("code/thexsvv/richobfuscator/Encrypt"))
                    .forEach(insn -> insn.owner = classNode.name);

            Map<String, String> variablesMap = Maps.newLinkedHashMap();
            decryptMethod.localVariables.forEach(localVariableNode -> {
                String newName = ZalgoUtils.Companion.generateZalgo(40);
                variablesMap.put(localVariableNode.name, newName);
                localVariableNode.name = newName;
            });
            Arrays.stream(decryptMethod.instructions.toArray())
                    .filter(insn -> insn instanceof FieldInsnNode)
                    .map(insn -> (FieldInsnNode) insn)
                    .forEach(insn -> {
                        if (variablesMap.containsKey(insn.name))
                            insn.name = variablesMap.get(insn.name);
                    });

            classNode.methods.add(decryptMethod);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        classNode.visitEnd();
        return processor.dumpClass(classNode);
    }

    public static int getJavaVersionAsClassVersion() {
        return switch (Integer.parseInt(System.getProperty("java.version").split("\\.")[1])) {
            case 1 -> Opcodes.V1_1;
            case 2 -> Opcodes.V1_2;
            case 3 -> Opcodes.V1_3;
            case 4 -> Opcodes.V1_4;
            case 5 -> Opcodes.V1_5;
            case 6 -> Opcodes.V1_6;
            case 7 -> Opcodes.V1_7;
            case 9 -> Opcodes.V9;
            case 10 -> Opcodes.V10;
            case 11 -> Opcodes.V11;
            case 12 -> Opcodes.V12;
            case 13 -> Opcodes.V13;
            case 14 -> Opcodes.V14;
            case 15 -> Opcodes.V15;
            case 16 -> Opcodes.V16;
            case 17 -> Opcodes.V17;
            case 18 -> Opcodes.V18;
            case 19 -> Opcodes.V19;
            case 20 -> Opcodes.V20;
            case 21 -> Opcodes.V21;
            default -> Opcodes.V1_8;
        };
    }
}
