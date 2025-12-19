/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.resources;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author dadxu
 */
public class TextFieldRedondeado extends JTextField {
    private int radio = 15; 

    public TextFieldRedondeado(int columnas) {
        super(columnas);
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radio, radio);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(Color.GRAY);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radio, radio);
    }
}
