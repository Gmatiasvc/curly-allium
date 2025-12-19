package client.resources;


/**
 * ModernPopupMenu - popup moderno con items personalizados.
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class ModernPopupMenu extends JPopupMenu {

    public ModernPopupMenu() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(new Color(225, 225, 225));

        // Bordes redondeados
        setUI(new javax.swing.plaf.basic.BasicPopupMenuUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);

                g2.dispose();
            }
        });

        // 🔥 Cuando el popup se abre, todo vuelve a estado normal
        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                for (Component c : getComponents()) {
                    if (c instanceof JMenuItem item) {
                        item.getModel().setRollover(false);
                        item.getModel().setArmed(false);
                        item.getModel().setSelected(false);
                        item.repaint();
                    }
                }
            }

            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });
    }

    // ------------------------------------------------------------
    // Método para agregar items modernos
    // ------------------------------------------------------------
    public JMenuItem addModernItem(String text) {

        JMenuItem item = new JMenuItem(text) {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Hover amarillo
                if (getModel().isRollover()) {
                    g2.setColor(new Color(225, 225, 225));
                } else {
                    g2.setColor(getBackground());
                }

                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();

                super.paintComponent(g);
            }
        };

        // Evita fondo azul del L&F
        item.setUI(new BasicMenuItemUI() {
            @Override
            protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {}
        });

        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setForeground(Color.BLACK);
        item.setBorder(new EmptyBorder(8, 12, 8, 12));
        item.setBackground(Color.WHITE);
        item.setOpaque(false);

        // Hover controlado manualmente
        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.getModel().setRollover(true);
                item.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.getModel().setRollover(false);
                item.repaint();
            }
        });

        add(item);
        return item;
    }
}
