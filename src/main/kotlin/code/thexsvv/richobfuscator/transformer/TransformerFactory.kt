package code.thexsvv.richobfuscator.transformer

import code.thexsvv.richobfuscator.transformer.impl.*
import com.google.common.collect.Lists

class TransformerFactory {

    private val transformers = Lists.newLinkedList<Transformer>();

    init {
        register(FakeExceptionTransformer::class.java);
        register(FlowTransformer::class.java);
        register(IntegerTransformer::class.java);
        register(LineNumberTransformer::class.java);
        register(MembersHider::class.java);
        register(StringTransformer::class.java);
    }

    fun register(transformerClass: Class<out Transformer>) {
        try {
            transformers.add(transformerClass.getConstructor().newInstance());
        } catch (exception: Throwable) {
            println("Transformer must have empty constructor");
        }
    }

    fun getTransformer(clazz: Class<out Transformer>): Transformer? {
        return transformers.stream()
            .filter { transformer: Transformer -> transformer.javaClass == clazz }
            .findFirst()
            .orElse(null)
    }

    fun transformers(): List<Transformer> {
        return transformers;
    }

    companion object {
        private var instance: TransformerFactory? = null;

        fun getInstance(): TransformerFactory {
            return instance ?: synchronized(this) {
                instance ?: TransformerFactory().also { instance = it }
            }
        }
    }
}
