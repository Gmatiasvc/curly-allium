/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.resources;

import javax.swing.JScrollBar;

/**
 *
 * @author dadxu
 */
public class ModernScrollBar extends JScrollBar {
    public ModernScrollBar() {
        setUI(new ModernScrollBarUI());
        setOpaque(false);
    }
}
