/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dadxu
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//        //FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentacion/FrmVistaCliente.fxml"));
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentacion/Login.fxml"));
//        Scene scene = new Scene(loader.load());
//        stage.setTitle("Login");
//        stage.setScene(scene);
//        stage.show();
//        
        ControladorPantalla.setStage(stage);
        ControladorPantalla.cambiarEscena("/presentacion/Registro.fxml");
        //ControladorPantalla.cambiarEscena("/presentacion/FrmVistaConductor.fxml");
        stage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }

}
