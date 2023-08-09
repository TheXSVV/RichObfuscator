package code.thexsvv.richobfuscator.swing

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class CacheableTextField(
        private val id: String
) : JTextField() {

    init {
        columns = 43;
        val cacheFile = File(".cache");
        if (cacheFile.exists()) {
            val properties = Properties();
            val fis = FileInputStream(cacheFile);
            properties.load(fis);
            fis.close();

            if (properties.containsKey(id))
                text = properties.getProperty(id);
        }

        document.addDocumentListener(object: DocumentListener {
            override fun changedUpdate(event: DocumentEvent) {
                update();
            }

            override fun removeUpdate(event: DocumentEvent) {
                update();
            }

            override fun insertUpdate(event: DocumentEvent) {
                update();
            }

            fun update() {
                if (!cacheFile.exists())
                    cacheFile.createNewFile();
                if (!cacheFile.isFile)
                    Files.delete(cacheFile.toPath());

                val properties = Properties();
                val fis = FileInputStream(cacheFile);
                properties.load(fis);
                fis.close();

                val fos = FileOutputStream(cacheFile);
                properties[id] = text;
                properties.store(fos, "");
                fos.close();
            }
        })
    }
}
