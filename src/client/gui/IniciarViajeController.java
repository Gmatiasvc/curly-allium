/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package client.gui;

import Mapa.MapaFX;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author dadxu
 */
public class IniciarViajeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private AnchorPane panMapa;
    private MapaFX mapa;
    
    
    @FXML
    private Button btnAtras;
    @FXML
    private Button btnTresPuntos; 
    @FXML
    private ComboBox<String> cmbInicio;
    @FXML
    private ComboBox<String> cmbDestino;

 
    
    @FXML
    private void btnAtrasHoverEnter() {
        btnAtras.setStyle("-fx-background-color:#E1E1E1 ; -fx-background-radius:25");
    }

    @FXML
    private void btnAtrasHoverExit() {
        btnAtras.setStyle("-fx-background-color: transparent; -fx-background-radius:25");
    }
    
    @FXML
    private void btnTresPuntosHoverEnter() {
        btnTresPuntos.setStyle("-fx-background-color:#E1E1E1 ; -fx-background-radius:25");
    }

    @FXML
    private void btnTresPuntosHoverExit() {
        btnTresPuntos.setStyle("-fx-background-color: transparent; -fx-background-radius:25");
    }
    
    
    //Para los combobox
    private void cargarLugares() {
        List<String> lugares = List.of(
                "A", "B", "C", "D", "E", "F", "G"
        );

        cmbInicio.getItems().addAll(lugares);
        cmbDestino.getItems().addAll(lugares);
    }
    
 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMapa();
        cargarLugares();
    } 
    
    
    public void cargarMapa(){ 
        mapa = new MapaFX();
        panMapa.getChildren().add(mapa.getPane());
    }
       
    @FXML
    private void iniciarViaje() {
        // Verifica si el mapa está inicializado
        if (mapa == null) {
            System.out.println("Mapa no está inicializado correctamente.");
            return;
        }

        String inicio = cmbInicio.getValue();
        String destino = cmbDestino.getValue();

        if (inicio == null || destino == null) {
            System.out.println("Selecciona inicio y destino");
            return;
        }

        // Aquí llamas a animarRuta
        mapa.animarRuta(inicio, destino);
    }


}
