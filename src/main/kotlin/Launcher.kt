import code.thexsvv.richobfuscator.RichObfuscator
import code.thexsvv.richobfuscator.session.Session
import code.thexsvv.richobfuscator.swing.CacheableTextField
import code.thexsvv.richobfuscator.swing.FormBuilder
import code.thexsvv.richobfuscator.transformer.TransformerFactory
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme
import jnafilechooser.api.JnaFileChooser
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ItemEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.*

object Launcher {

    @JvmStatic
    @Throws(Throwable::class)
    fun main(args: Array<String>) {
        FlatOneDarkIJTheme.setup();
        val frame = JFrame();
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE;
        frame.size = Dimension(600, 750);
        frame.isResizable = false;
        frame.title = "Rich Obfuscator v0.1.1";

        val inputLocationField = CacheableTextField("input");
        val selectInputButton = JButton(ImageIcon(javaClass.getResourceAsStream("/images/jarDirectory.png")?.let { resize(it, 16) }));
        selectInputButton.addActionListener {
            val fileChooser = JnaFileChooser();
            fileChooser.mode = JnaFileChooser.Mode.Files;
            fileChooser.addFilter("JAR Files (.jar)", "*.jar");
            fileChooser.showOpenDialog(null);

            if (fileChooser.selectedFile != null)
                inputLocationField.text = fileChooser.selectedFile.absolutePath;
        };

        val outputLocationField = CacheableTextField("output");
        val selectOutputButton = JButton(ImageIcon(javaClass.getResourceAsStream("/images/jarDirectory.png")?.let { resize(it, 16) }));
        selectOutputButton.addActionListener {
            val fileChooser = JnaFileChooser();
            fileChooser.mode = JnaFileChooser.Mode.Files;
            fileChooser.addFilter("JAR Files (.jar)", "*.jar");
            fileChooser.showSaveDialog(null);

            if (fileChooser.selectedFile != null)
                outputLocationField.text = fileChooser.selectedFile.absolutePath;
        };

        val runButton = JButton("Run");
        runButton.addActionListener {
            if (inputLocationField.text.isEmpty() || outputLocationField.text.isEmpty())
                JOptionPane.showInternalMessageDialog(null, "It's necessary to fill in all the fields!", "Error", JOptionPane.ERROR_MESSAGE);
            else
                RichObfuscator.getInstance().obfuscate(Session(
                        File(inputLocationField.text),
                        File(outputLocationField.text)
                ));
        };
        frame.rootPane.defaultButton = runButton;
        runButton.requestFocus();

        val textArea = JTextArea();
        textArea.preferredSize = Dimension(0, 200);
        val formBuilder = FormBuilder.createFormBuilder(frame)
                .setFormLeftIndent(8)
                .addTwoLabeledComponents("Input:", selectInputButton, inputLocationField)
                .addTwoLabeledComponents("Output:", selectOutputButton, outputLocationField)
                .addLabeledComponentFillVertically("Exclusions (package.Class or package.*):", textArea)
                .addSeparator(8);
        for (transformer in TransformerFactory.getInstance().transformers()) {
            val checkBox = JCheckBox();
            checkBox.addItemListener { event ->
                transformer.enabled = (event.stateChange == ItemEvent.SELECTED);
            }
            formBuilder.addLabeledComponent(transformer.name+":", checkBox);
            transformer.addSettings(formBuilder);
        }
        formBuilder.fillVertically()
                   .addSouthComponent(runButton);

        val panel = formBuilder.panel;
        panel.border = BorderFactory.createEmptyBorder(8, 0, 0, 0);

        frame.contentPane = panel;
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.isVisible = true;
    }

    fun resize(inputStream: InputStream, size: Int): BufferedImage {
        val image = ImageIO.read(inputStream);
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        val graphics = bufferedImage.createGraphics();
        graphics.color = Color.WHITE;
        graphics.drawImage(image, 0, 0, size, size, null);
        graphics.dispose();

        return bufferedImage;
    }
}
