package client.map;

import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class MapaFX {

    // ===== PANE RAÍZ (EL MAPA) =====
    private Pane paneMapa;

    public Pane getPane() {
        return paneMapa;
    }

    // ===== CLASES INTERNAS =====
    class Nodo {
        String nombre;
        double x, y;
        Circle c;
        Text label;

        Nodo(String n, double x, double y) {
            this.nombre = n;
            this.x = x;
            this.y = y;

            c = new Circle(x, y, 6, Color.RED);
            c.setStroke(Color.BLACK);

            label = new Text(x - 4, y - 15, n);
        }
    }

    class Arista {
        Nodo a, b;
        Line l;

        Arista(Nodo a, Nodo b) {
            this.a = a;
            this.b = b;
            l = new Line(a.x, a.y, b.x, b.y);
            l.setStroke(Color.GRAY);
            l.setStrokeWidth(4);
        }
    }

    // ===== DATOS =====
    private Map<String, Nodo> nodos = new HashMap<>();
    private List<Arista> aristas = new ArrayList<>();
    private SequentialTransition animacionActual;

    // ===== CONSTRUCTOR =====
    public MapaFX() {
        paneMapa = new Pane();
        paneMapa.setPrefSize(650, 460);
        crearMapa();
    }

    // ===== CREAR MAPA =====
    private void crearMapa() {

        Image fondo = new Image(getClass().getResource("/Mapa/trujillo.png").toExternalForm());
        ImageView fondoView = new ImageView(fondo);
        fondoView.setFitWidth(650);
        fondoView.setFitHeight(460);

        paneMapa.getChildren().add(fondoView);

        crearNodo("A", 100, 100);
        crearNodo("B", 300, 150);
        crearNodo("C", 400, 200);
        crearNodo("D", 500, 300);
        crearNodo("E", 350, 400);
        crearNodo("F", 250, 350);
        crearNodo("G", 120, 300);

        conectar("A", "B");
        conectar("B", "C");
        conectar("C", "D");
        conectar("C", "E");
        conectar("E", "F");
        conectar("F", "A");
        conectar("B", "F");
        conectar("F", "G");
        conectar("G", "A");
    }

    private void crearNodo(String nombre, double x, double y) {
        Nodo n = new Nodo(nombre, x, y);
        nodos.put(nombre, n);
        paneMapa.getChildren().addAll(n.c, n.label);
    }

    private void conectar(String a, String b) {
        Arista ar = new Arista(nodos.get(a), nodos.get(b));
        aristas.add(ar);
        paneMapa.getChildren().add(1, ar.l);
    }

    private void resetearColores() {
        nodos.values().forEach(n -> n.c.setFill(Color.RED));
        aristas.forEach(a -> a.l.setStroke(Color.GRAY));
    }

    // ===== ANIMAR RUTA =====
    public void animarRuta(String inicio, String destino) {

        if (!nodos.containsKey(inicio) || !nodos.containsKey(destino)) return;

        resetearColores();

        if (animacionActual != null) animacionActual.stop();

        List<Nodo> camino = calcularRuta(inicio, destino);
        animacionActual = crearAnimacion(camino);
        animacionActual.play();
    }

    private List<Nodo> calcularRuta(String inicio, String destino) {
        // temporal (luego Dijkstra)
        return List.of(nodos.get(inicio), nodos.get(destino));
    }

    private SequentialTransition crearAnimacion(List<Nodo> camino) {
        SequentialTransition seq = new SequentialTransition();

        for (int i = 0; i < camino.size(); i++) {
            Nodo actual = camino.get(i);

            seq.getChildren().add(
                new FillTransition(Duration.millis(400), actual.c, Color.RED, Color.YELLOW)
            );

            if (i < camino.size() - 1) {
                Nodo sig = camino.get(i + 1);
                aristas.stream()
                    .filter(a -> (a.a == actual && a.b == sig) || (a.b == actual && a.a == sig))
                    .forEach(a -> seq.getChildren().add(
                        new StrokeTransition(Duration.millis(400), a.l, Color.GRAY, Color.BLUE)
                    ));
            }
        }
        return seq;
    }
}
