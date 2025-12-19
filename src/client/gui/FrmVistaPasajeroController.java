package client.gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author dadxu
 */

import entidades.Sesion;
import entidades.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class FrmVistaPasajeroController implements Initializable {

    // Panel derecho donde puedes cargar contenido dinámicamente
    @FXML
    private AnchorPane panContenido;

    // Botones del menú lateral
    
    @FXML
    private Button btnAmigos;

    @FXML
    private Button btnCasa;

    @FXML
    private Button btnHistorial;

    @FXML
    private Button btnIniciarViaje;

    @FXML
    private Button btnPerfil;

    @FXML
    private Button btnSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AnchorPane panelCasa = FXMLLoader.load(getClass().getResource("Casa.fxml"));
            panContenido.getChildren().clear();

            // Ajustar anclaje para que se adapte al contenedor
            AnchorPane.setTopAnchor(panelCasa, 0.0);
            AnchorPane.setBottomAnchor(panelCasa, 0.0);
            AnchorPane.setLeftAnchor(panelCasa, 0.0);
            AnchorPane.setRightAnchor(panelCasa, 0.0);

            // Agregar al contenedor principal
            panContenido.getChildren().add(panelCasa);
        } catch (IOException ex) {
        }
    }

    // Métodos para los eventos onMouseClicked
    @FXML
    private void Casa(MouseEvent event) {
        try {
            AnchorPane panelCasa = FXMLLoader.load(getClass().getResource("Casa.fxml"));
            panContenido.getChildren().clear();

            // Ajustar anclaje para que se adapte al contenedor
            AnchorPane.setTopAnchor(panelCasa, 0.0);
            AnchorPane.setBottomAnchor(panelCasa, 0.0);
            AnchorPane.setLeftAnchor(panelCasa, 0.0);
            AnchorPane.setRightAnchor(panelCasa, 0.0);

            // Agregar al contenedor principal
            panContenido.getChildren().add(panelCasa);
        } catch (IOException ex) {
        }
    }
    

    @FXML
    private void Historial(MouseEvent event) {
        try {
            AnchorPane panelIniciar = FXMLLoader.load(getClass().getResource("Historial.fxml"));
            panContenido.getChildren().clear();

            // Ajustar anclaje para que se adapte al contenedor
            AnchorPane.setTopAnchor(panelIniciar, 0.0);
            AnchorPane.setBottomAnchor(panelIniciar, 0.0);
            AnchorPane.setLeftAnchor(panelIniciar, 0.0);
            AnchorPane.setRightAnchor(panelIniciar, 0.0);

            // Agregar al contenedor principal
            panContenido.getChildren().add(panelIniciar);
        } catch (IOException ex) {
        }
    }
    
    
    
    @FXML
    private void Iniciar(MouseEvent event) {
        try {
            AnchorPane panelIniciar = FXMLLoader.load(getClass().getResource("IniciarViaje.fxml"));
            panContenido.getChildren().clear();

            // Ajustar anclaje para que se adapte al contenedor
            AnchorPane.setTopAnchor(panelIniciar, 0.0);
            AnchorPane.setBottomAnchor(panelIniciar, 0.0);
            AnchorPane.setLeftAnchor(panelIniciar, 0.0);
            AnchorPane.setRightAnchor(panelIniciar, 0.0);

            // Agregar al contenedor principal
            panContenido.getChildren().add(panelIniciar);
        } catch (IOException ex) {
        }
    }


    @FXML
    private void Perfil(MouseEvent event) {
        System.out.println("Botón Perfil clickeado");
    }

    @FXML
    private void Amigos(MouseEvent event) {
        System.out.println("Botón Amigos clickeado");
    }

    @FXML
    private void Salir(MouseEvent event) {
        System.out.println("Botón Salir clickeado");
        Platform.exit(); // Cierra la aplicación
    }
    
    
     @FXML
    void btnAmigosHoverEnter(MouseEvent event) {
        btnAmigos.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");
    }

    @FXML
    void btnAmigosHoverExit(MouseEvent event) {
        btnAmigos.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }

    @FXML
    void btnCasaHoverEnter(MouseEvent event) {
        btnCasa.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");

    }

    @FXML
    void btnCasaHoverExit(MouseEvent event) {
        btnCasa.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }

    @FXML
    void btnHistorialHoverEnter(MouseEvent event) {
        btnHistorial.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");

    }

    @FXML
    void btnHistorialHoverExit(MouseEvent event) {
        btnHistorial.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }

    @FXML
    void btnPerfilHoverEnter(MouseEvent event) {
        btnPerfil.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");
    }

    @FXML
    void btnPerfilHoverExit(MouseEvent event) {
        btnPerfil.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }

    @FXML
    void btnSalirHoverEnter(MouseEvent event) {
        btnSalir.setStyle("-fx-background-color:#464646 ; -fx-background-radius:12");

    }

    @FXML
    void btnSalirHoverExit(MouseEvent event) {
        btnSalir.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius:12");
    }
}
