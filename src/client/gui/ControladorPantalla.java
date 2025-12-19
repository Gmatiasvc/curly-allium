/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author dadxu
 */
public class ControladorPantalla {
    private static Stage stage;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void cambiarEscena(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ControladorPantalla.class.getResource(fxml)
            );
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
        }
    }
}
