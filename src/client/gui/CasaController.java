/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.gui;

import entidades.Sesion;
import entidades.Usuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author dadxu
 */
public class CasaController implements Initializable {

    /**
     * Initializes the controller class.
     */
 
    @FXML
    private Label labelFecha;

    @FXML
    private Label labelHora;
    
    @FXML
    private Label labelNombre;
    
    @FXML
    private Label labelEmail;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = Sesion.getUsuario();
        if (u != null) {
            labelNombre.setText(u.getNombre());
            labelEmail.setText(u.getEmail());
        
}
    }    
    
}
