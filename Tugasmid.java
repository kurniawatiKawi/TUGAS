package tugasmid;

import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import javafx.scene.control.ScrollPane;
/**
 *
 * @author RamLah
 */
public class Tugasmid extends Application {

    private static FlowPane fpContainer = new FlowPane();
    private static TextArea textArea1 = new TextArea();
    private static TextArea textArea2 = new TextArea();
    private static TextArea textArea3 = new TextArea();
    private static int MaxFrekuensi = 0;
    private static Color[] colors = {
        Color.hsb(168, 0.86, 0.74),
        Color.hsb(145, 0.77, 0.80),
        Color.hsb(204, 0.76, 0.86),
        Color.hsb(283, 0.51, 0.71),
        Color.CRIMSON,
        Color.DEEPSKYBLUE,
        Color.ORANGE,
        Color.GREEN};

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 5, 5, 5));
        gridPane.setHgap(5);
        //col constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        //row constraints
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.ALWAYS);
        row1.setPercentHeight(40);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.ALWAYS);
        row2.setPercentHeight(560);
        //set constrain untuk gridPane
        gridPane.getColumnConstraints().addAll(col1, col2, col3);
        gridPane.getRowConstraints().addAll(row1, row2);

        Label labelURL = new Label("URL");
        gridPane.add(labelURL, 0, 0);

        TextField tfURL = new TextField();
        tfURL.setText("https://www.google.com/");
        gridPane.add(tfURL, 1, 0);

        Button buttonRUN = new Button("Extract");
        gridPane.add(buttonRUN, 2, 0);
        buttonRUN.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    String url = tfURL.getText();
                    miner(url);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        TabPane tabPane = new TabPane();
        gridPane.add(tabPane, 0, 1, 3, 2);
        Tab tab1 = new Tab();
        tab1.setText("Text Hasil Ekstraksi");
        tab1.setContent(textArea1);
        textArea1.setWrapText(true);

        Tab tab2 = new Tab();
        tab2.setText("Frekuensi Kata");
        tab2.setContent(textArea2);

        Tab tab3 = new Tab();
        tab3.setText("Word Cloud");
        ScrollPane scrollpane = new ScrollPane (fpContainer);
        tab3.setContent(scrollpane);

        tabPane.getSelectionModel().select(0);
        tabPane.getTabs().addAll(tab1, tab2, tab3);

        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Web Miner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void generateWordCloud(ArrayList<Vertex> vertices) {
        fpContainer.getChildren().clear();
        int MaxFonSize = 100;
        int baseFontSize = 8;
        int rasio = (MaxFonSize - baseFontSize) / MaxFrekuensi;
        for (Vertex vertex : vertices) {
            //System.out.println(node.kata + " : " + node.frekuensi);
            Text text = new Text(" " + vertex.kata + "");
            int size = baseFontSize + rasio * vertex.frekuensi;
            text.setFont(Font.font("Arial", FontWeight.BOLD, size));
            int indexColor = randomBetweenInt(0, colors.length);
            Color color = colors[indexColor];
            text.setFill(color);
            fpContainer.getChildren().add(text);
        }
    }

    private static int randomBetweenInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }

    private static void miner(String URL) throws IOException {
        //Input URL
        String url = URL;

        //Load Dokumen Web
        Document document = Jsoup.connect(url).get();

        //Ekstraksi data text
        String text = document.text();
        textArea1.setText(text);

        //hitung frekuensi tiap kata
        String[] arrayText = text.trim().split("\\s+");
        ArrayList<Vertex> vertices = hitungFrekuensi(arrayText);
        generateWordCloud(vertices);
        
        String hasilHitungFrekuensi = hasilHitungFrekuensiToString(vertices);
        textArea2.setText(hasilHitungFrekuensi);
    }
    private static String hasilHitungFrekuensiToString(ArrayList<Vertex>vertices) {
        vertices= sort(vertices);
        //konversi hasil perhitungan frekuensi ke String /StringBuffer
        StringBuffer sb = new StringBuffer();
        for (Vertex vertex : vertices) {
            sb.append(vertex.kata + " : " + vertex.frekuensi + "\n");
            
        }
        return sb.toString();
        
    }

    private static ArrayList<Vertex> hitungFrekuensi(String[] arrayText) {
        MaxFrekuensi = 0;
        ArrayList<Vertex> vertices = new ArrayList<>();
        for (String kata : arrayText) {
            //konversi text ke UPPERCASE
            String s1 = kata.toUpperCase();

            //memeriksa apakah s1 sudah ada di list nodes
            boolean ada = false;
            for (Vertex vertex : vertices) {
                if (s1.equals(vertex.kata)) {
                    vertex.frekuensi++;
                    if (vertex.frekuensi > MaxFrekuensi) {
                        MaxFrekuensi = vertex.frekuensi;
                    }
                    ada = true;
                    break;
                }
            }
            if (ada == false) {
                //tambahkan node baru ke list nodes
                Vertex vertex = new Vertex(s1, 1);
                vertices.add(vertex);
            }
        }
        return vertices;
    }

    private static ArrayList<Vertex> sort(ArrayList<Vertex> vertices) {
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = 1 + i; j < vertices.size(); j++) {
                if (vertices.get(i).frekuensi < vertices.get(j).frekuensi) {
                    //tukar
                    Vertex node1 = new Vertex(vertices.get(i).kata, vertices.get(i).frekuensi);
                    Vertex node2 = new Vertex(vertices.get(j).kata, vertices.get(j).frekuensi);
                    vertices.set(j, node1);
                    vertices.set(i, node2);
                }
            }
        }
        return vertices;
    }

    public static void main(String[] args) {
        launch(args);
    }

}

class Vertex {

    String kata;
    int frekuensi;

    public Vertex(String kata, int frekuensi) {
        super();
        this.kata = kata;
        this.frekuensi = frekuensi;
    }

}