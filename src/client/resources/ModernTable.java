/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.resources;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class ModernTable extends JTable {

    public ModernTable(TableModel modelo) {
        super(modelo);

        setRowHeight(42);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setFont(new Font("Segoe UI", Font.PLAIN, 15));
        //setBackground(new Color(0, 0, 245));
        //setSelectionBackground(new Color(255, 204, 51));
        setSelectionForeground(Color.BLACK);

        // Cabecera personalizada
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(0, 40));

        // Celdas
        setDefaultRenderer(Object.class, new CellRenderer());
    }

    // ================== RENDER CABECERA ==================
    private class HeaderRenderer extends DefaultTableCellRenderer {

        public HeaderRenderer() {
            setHorizontalAlignment(CENTER);
            setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
            setForeground(Color.BLACK);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            c.setBackground(new Color(230, 230, 230)); //Modificar en caso de cambiar el color de la tabla
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            return c;
        }
    }

    // ================== RENDER CELDAS ==================
    private class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            setFont(new Font("Segoe UI", Font.PLAIN, 14));

            if (isSelected) {
                c.setBackground(new Color(255, 204, 51));  
                c.setForeground(Color.BLACK);
            } else {
                // filas alternadas más suaves
                if (row % 2 == 0)
                    c.setBackground(new Color(250, 250, 250));
                else
                    c.setBackground(new Color(240, 240, 240));

                c.setForeground(Color.BLACK);
            }

            return c;
        }
    }
}


