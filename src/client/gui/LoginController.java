/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.gui;

import entidades.Sesion;
import entidades.Usuario;
import java.io.IOException;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dadxu
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Button btnCrearCuenta;
        
    @FXML
    private TextField txtEmail;
    
    @FXML
    private PasswordField txtContraseña;

        
    @FXML
    private void btnIniciarSesionHoverEnter() {
        btnIniciarSesion.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");
    }

    @FXML
    private void btnIniciarSesionHoverExit() {
        btnIniciarSesion.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }
    
    @FXML
    private void btnCrearCuentaHoverEnter() {
        btnCrearCuenta.setStyle("-fx-background-color:#FFDB4D ; -fx-background-radius:12");
    }

    @FXML
    private void btnCrearCuentaHoverExit() {
        btnCrearCuenta.setStyle("-fx-background-color: #FFCC00; -fx-background-radius:12");
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    } 
     
    
    //
    //private Connection conect; 


    
    
    public void iniciarSesion(){ 
        String sql = "";

        Alert alerta; 
        String email = txtEmail.getText();
        String contraseña = txtContraseña.getText();
        if (email.isEmpty()||contraseña.isEmpty()){
            alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de Mensaje");
            alerta.setHeaderText(null);
            alerta.setContentText("Ingrese contraseña");
            alerta.showAndWait();
        }
        else {
            boolean resultado = true;
            if (resultado) {
                
                Usuario u = new Usuario(email,contraseña);
                Sesion.setUsuario(u);
                ControladorPantalla.cambiarEscena("FrmVistaPasajero.fxml");

            } 
            else {
                alerta = new Alert(AlertType.ERROR);
                alerta.setTitle("Error de Mensaje");
                alerta.setHeaderText(null);
                alerta.setContentText("Contraseña o correo incorrecto");
                alerta.showAndWait();
            }
             
        }
    } 
}
