package code.thexsvv.richobfuscator

import code.thexsvv.richobfuscator.processor.impl.JarProcessor
import code.thexsvv.richobfuscator.session.Session
import code.thexsvv.richobfuscator.transformer.TransformerFactory
import org.apache.logging.log4j.LogManager
import java.util.stream.Collectors

class RichObfuscator {

    private val LOGGER = LogManager.getLogger(javaClass);
    private val processor = JarProcessor();

    fun obfuscate(session: Session) {
        Thread({
            LOGGER.info("JDK Version: ${System.getProperty("java.version")}");
            LOGGER.info("Processing jar file: ${session.input.absolutePath}");
            val classes = processor.processFile(session.input);
            val factory = TransformerFactory.getInstance();
            LOGGER.info("Loaded ${classes.size} classes");
            LOGGER.info("Loaded ${factory.transformers().size} transformers");
            val enabledTransformers = factory.transformers().stream()
                    .filter { transformer -> transformer.enabled }
                    .collect(Collectors.toList());
            LOGGER.info("Enabled ${enabledTransformers.size} transformers");

            for (transformer in enabledTransformers) {
                LOGGER.info("Transforming using: ${transformer.name}");
                for (classNode in classes)
                    transformer.transform(classNode);
            }
            LOGGER.info("Transformed ${classes.size} classes");

            if (session.output.exists())
                session.output.delete();
            processor.dumpFile(session.input, session.output, classes);

            LOGGER.info("Dumped ${classes.size} classes");
            LOGGER.info("Saved to ${session.output.absolutePath}");
            //Runtime.getRuntime().exec("explorer.exe /select,${session.output.absolutePath}");
        }, "Obfuscation-Thread").start();
    }

    companion object {
        private var instance: RichObfuscator? = null;

        fun getInstance(): RichObfuscator {
            return instance ?: synchronized(this) {
                instance ?: RichObfuscator().also { instance = it }
            }
        }
    }
}
