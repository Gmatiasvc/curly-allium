/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 *
 * @author dadxu
 */
/**
 * UI para JComboBox completamente redondeado y sin el "rectángulo blanco".
 */
public class ModernRoundedComboBoxUI extends BasicComboBoxUI {

    private final int arc = 20; // radio de las esquinas

    @Override
    protected void installDefaults() {
        super.installDefaults();
        // Para evitar que el componente padre pinte un fondo rectangular
        comboBox.setOpaque(false);
        comboBox.setBorder(new EmptyBorder(6, 10, 6, 10));
    }

    @Override
    protected JButton createArrowButton() {
        JButton button = new JButton("▾");
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusable(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(new Color(100, 100, 100));
        return button;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        // Pintar fondo redondeado
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo del combo (puedes cambiar el color)
        Color background = comboBox.getBackground();
        if (background == null) background = Color.WHITE;

        g2.setColor(background);
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), arc, arc);

        // Borde (opcional)
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, arc, arc);

        g2.dispose();

        // Ahora pintar el resto (texto / flecha / editor)
        super.paint(g, c);
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // No pintar fondo adicional para evitar el rectángulo
        // Dejamos que paint() haga el fondo completo
    }

    @Override
    protected ListCellRenderer<Object> createRenderer() {
        // Renderer transparente para evitar rectángulo blanco en el dropdown (cada item)
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setOpaque(true); // la lista necesita opaque para mostrar seleccion
                if (isSelected) {
                    lbl.setBackground(new Color(230, 230, 230));
                    lbl.setForeground(Color.BLACK);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(new Color(70, 70, 70));
                }
                return lbl;
            }
        };
    }

    @Override
    protected void configureEditor() {
        super.configureEditor();
        // Si el combo es editable, hacer transparente el componente editor
        if (comboBox.isEditable()) {
            Component ed = comboBox.getEditor().getEditorComponent();
            if (ed instanceof JComponent) {
                JComponent j = (JComponent) ed;
                j.setOpaque(false);
                j.setBackground(new Color(0,0,0,0));
                if (j instanceof JTextField) {
                    ((JTextField) j).setBorder(null);
                }
            }
        }
    }
}

