/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import entidades.Viaje;
import estructuraDeDatos.ListaEnlazada;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author dadxu
 */
public class HistorialController {

    private ListaEnlazada<Viaje> listaViajes = new ListaEnlazada<>();

    @FXML
    private Button btnAtras;
    @FXML
    private Button btnTresPuntos;

    @FXML
    private Button btnFiltro;

    @FXML
    private TableView<Viaje> tblViajes;

    @FXML
    private TableColumn<Viaje, String> colOrigen;

    @FXML
    private TableColumn<Viaje, String> colDestino;

    @FXML
    private TableColumn<Viaje, Double> colDistancia;

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

    @FXML
    private void btnFiltroHoverEnter() {
        btnFiltro.setStyle("-fx-background-color:#E1E1E1 ; -fx-background-radius:25");
    }

    @FXML
    private void btnFiltroHoverExit() {
        btnFiltro.setStyle("-fx-background-color: transparent; -fx-background-radius:25");
    }

    //MODIFICAR DESPUEES
    public void btnFiltro() {

    }

    @FXML
    public void initialize() {
        colOrigen.setCellValueFactory(new PropertyValueFactory<>("origen"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));

        listaViajes.agregarElementoAlFinal(
                new Viaje("A", "M", 8)
        );
        listaViajes.agregarElementoAlFinal(
                new Viaje("B", "C", 3)
        );
        listaViajes.agregarElementoAlFinal(
                new Viaje("C", "B", 5)
        );
        listaViajes.agregarElementoAlFinal(
                new Viaje("D", "B", 7)
        );
        listaViajes.agregarElementoAlFinal(
                new Viaje("M", "A", 3)
        );

        tblViajes.setItems(listaViajes.mostrarTabla());
    }

}
