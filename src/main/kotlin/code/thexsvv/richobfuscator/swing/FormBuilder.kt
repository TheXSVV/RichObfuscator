package code.thexsvv.richobfuscator.swing

import java.awt.*
import javax.swing.*

@Suppress("NAME_SHADOWING")
class FormBuilder private constructor(private val frame: JFrame) {

    private var myAlignLabelOnRight: Boolean;
    private var lineCount = 0;
    val panel: JPanel = JPanel(GridBagLayout());

    private var myVertical: Boolean;
    private var myVerticalGap: Int;
    private var myHorizontalGap: Int;
    private var myFormLeftIndent: Int;

    init {
        myVertical = false
        myAlignLabelOnRight = false
        myVerticalGap = 4
        myHorizontalGap = 10
        myFormLeftIndent = 0
    }

    fun addLabeledComponent(label: JComponent?, component: JComponent, labelOnTop: Boolean): FormBuilder {
        return addLabeledComponent(label, component, myVerticalGap, labelOnTop)
    }

    fun addLabeledComponent(labelText: String, component: JComponent, labelOnTop: Boolean): FormBuilder {
        return addLabeledComponent(labelText, component, myVerticalGap, labelOnTop)
    }

    @JvmOverloads
    fun addLabeledComponent(labelText: String, component: JComponent, topInset: Int = myVerticalGap, labelOnTop: Boolean = false): FormBuilder {
        val label = createLabelForComponent(labelText, component)
        return addLabeledComponent(label, component, topInset, labelOnTop)
    }

    fun addTwoLabeledComponents(labelText: String, component: JComponent, component2: JComponent): FormBuilder {
        val p = JPanel();
        p.layout = BorderLayout();
        p.add(component, BorderLayout.WEST);
        p.add(component2);

        return addLabeledComponent(labelText, p);
    }

    fun addComponent(component: JComponent): FormBuilder {
        return addLabeledComponent(null as JLabel?, component, myVerticalGap, false)
    }

    fun addSouthComponent(component: JComponent): FormBuilder {
        val southPanel = JPanel(BorderLayout())
        val p = JPanel(GridLayout(1, 0, 5, 0));
        p.border = BorderFactory.createEmptyBorder(8, 10, 8, 10);
        p.add(component);

        southPanel.add(p, BorderLayout.EAST);
        southPanel.border = BorderFactory.createEmptyBorder(8, 0, 0, 0);
        return addComponent(southPanel);
    }

    fun addComponent(component: JComponent, topInset: Int): FormBuilder {
        return addLabeledComponent(null as JLabel?, component, topInset, false)
    }

    fun addComponentFillVertically(component: JComponent, topInset: Int): FormBuilder {
        return addLabeledComponent(null, component, topInset, labelOnTop = false, fillVertically = true)
    }

    fun fillVertically(): FormBuilder {
        return addComponentFillVertically(JPanel(), 0);
    }

    @JvmOverloads
    fun addSeparator(topInset: Int = myVerticalGap): FormBuilder {
        return addComponent(JSeparator(), topInset)
    }

    @JvmOverloads
    fun addComponentToRightColumn(component: JComponent, topInset: Int = myVerticalGap): FormBuilder {
        return addLabeledComponent(JLabel(), component, topInset)
    }

    @JvmOverloads
    fun addLabeledComponent(label: JComponent?,
                            component: JComponent,
                            topInset: Int = myVerticalGap,
                            labelOnTop: Boolean = false): FormBuilder {
        val fillVertically = component is JScrollPane
        return addLabeledComponent(label, component, topInset, labelOnTop, fillVertically)
    }

    fun addLabeledComponentFillVertically(labelText: String, component: JComponent): FormBuilder {
        val label = createLabelForComponent(labelText, component)
        return addLabeledComponent(label, component, myVerticalGap, labelOnTop = true, fillVertically = true)
    }

    private fun addLabeledComponent(label: JComponent?, component: JComponent, topInset: Int, labelOnTop: Boolean, fillVertically: Boolean): FormBuilder {
        var topInset = topInset
        val c = GridBagConstraints()
        topInset = if (lineCount > 0) topInset else 0
        if (myVertical || labelOnTop || label == null) {
            c.gridwidth = 2
            c.gridx = 0
            c.gridy = lineCount
            c.weightx = 0.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.NONE
            c.anchor = getLabelAnchor(false, fillVertically)
            c.insets = Insets(topInset, myFormLeftIndent, 4, 0)
            if (label != null)
                panel.add(label, c)
            c.gridx = 0
            c.gridy = lineCount + 1
            c.weightx = 1.0
            c.weighty = getWeightY(fillVertically).toDouble()
            c.fill = getFill(component, fillVertically)
            c.anchor = GridBagConstraints.WEST
            c.insets = Insets(if (label == null) topInset else 0, myFormLeftIndent, 0, 0)
            panel.add(component, c)
            lineCount += 2
        } else {
            c.gridwidth = 1
            c.gridx = 0
            c.gridy = lineCount
            c.weightx = 0.0
            c.weighty = 0.0
            c.fill = GridBagConstraints.NONE
            c.anchor = getLabelAnchor(true, fillVertically)
            c.insets = Insets(topInset, myFormLeftIndent, 0, myHorizontalGap)
            panel.add(label, c)
            c.gridx = 1
            c.weightx = 1.0
            c.weighty = getWeightY(fillVertically).toDouble()
            c.fill = getFill(component, fillVertically)
            c.anchor = GridBagConstraints.WEST
            c.insets = Insets(topInset, 0, 0, 0)
            panel.add(component, c)
            lineCount++
        }
        return this
    }

    private fun getLabelAnchor(honorAlignment: Boolean, fillVertically: Boolean): Int {
        if (fillVertically) return if (honorAlignment && myAlignLabelOnRight) GridBagConstraints.NORTHEAST else GridBagConstraints.NORTHWEST
        return if (honorAlignment && myAlignLabelOnRight) GridBagConstraints.EAST else GridBagConstraints.WEST
    }

    private fun getFill(component: JComponent?): Int {
        return if (component is JComboBox<*> ||
                component is JSpinner ||
                component is JButton || component is JTextField && component.columns != 0) {
            GridBagConstraints.NONE
        } else GridBagConstraints.HORIZONTAL
    }

    private fun getFill(component: JComponent, fillVertically: Boolean): Int {
        return if (fillVertically) {
            GridBagConstraints.BOTH
        } else getFill(component)
    }

    fun setAlignLabelOnRight(alignLabelOnRight: Boolean): FormBuilder {
        myAlignLabelOnRight = alignLabelOnRight
        return this
    }

    fun setVertical(vertical: Boolean): FormBuilder {
        myVertical = vertical
        return this
    }

    fun setVerticalGap(verticalGap: Int): FormBuilder {
        myVerticalGap = verticalGap
        return this
    }

    fun setHorizontalGap(horizontalGap: Int): FormBuilder {
        myHorizontalGap = horizontalGap
        return this
    }

    fun setFormLeftIndent(formLeftIndent: Int): FormBuilder {
        myFormLeftIndent = formLeftIndent
        return this
    }

    companion object {
        fun createFormBuilder(frame: JFrame): FormBuilder {
            return FormBuilder(frame);
        }

        private fun createLabelForComponent(labelText: String, component: JComponent): JLabel {
            val label = JLabel(labelText);
            label.labelFor = component;

            return label;
        }

        private fun getWeightY(fillVertically: Boolean): Int {
            return if (fillVertically) 1 else 0;
        }
    }
}
