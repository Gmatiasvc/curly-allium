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
public class PanelConImagen extends JPanel  {
    private Image imagen;
    private Color colorFondo;
    
    public PanelConImagen(String ruta) {
        imagen = new ImageIcon(ruta).getImage();
        colorFondo = null;
    }
    
    public PanelConImagen(Color color) {
        colorFondo = color;
        imagen = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagen != null) {
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
        else if (colorFondo != null) {
            g.setColor(colorFondo);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
