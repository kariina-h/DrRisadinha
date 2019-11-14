/*
 * Faculdade de Tecnologia, UNICAMP
 * Professor responsável: Ivan L. M. Ricarte
 */
package ui;

import bloggerdata.BlogJsonAccess;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import risadinha.Log;
import risadinha.Resumo;
import risadinha.ResumoStore;

/**
 * Interface gráfica para busca e apresentação de postagens
 * do blog Dr Risadinha.
 * 
 * @author ricarte at ft.unicamp.br
 */
public class BuscaRisadinha extends Application {

    private final Font titleFont = Font.font(20);
    private final Font shortFont = Font.font("System", FontPosture.ITALIC, 16);
    private final Font messageFont = Font.font(16);
    private final Font footerFont = Font.font(14);
    private final double prefWidth = 800;
    private List<Resumo> result;
    ResumoStore risadinha;

    @Override
    public void init() {
        try {
            BlogJsonAccess blog = new BlogJsonAccess("http://www.drrisadinha.org.br/", "AIzaSyBrWRP49pj7dSMObLwW3WM4s5agG5yO9ds");
            risadinha = new ResumoStore(blog);
        } catch (IOException | ClassNotFoundException ex) {
            String errorMessage = this.getClass().getSimpleName() + ".init(): " + ex.toString();
            Log.LOG_RIS.log(Level.SEVERE, errorMessage);
        }
    }

    private void showError(String message) {
        Alert errorMsg = new Alert(AlertType.ERROR);
        errorMsg.setContentText(message);
        errorMsg.showAndWait();
    }

    private void showContent(Resumo resumo) {
        double spacing = 20;
        VBox layout = new VBox(spacing);
        layout.setPrefWidth(prefWidth);

        Image imagem = new Image(resumo.getImagemUrl(), prefWidth / 3, 0, true, true);
        ImageView imgView = new ImageView();
        imgView.setImage(imagem);

        Text tituloTxt = new Text(resumo.getTitulo());
        tituloTxt.setFont(titleFont);

        Text msgCurtaTxt = new Text(resumo.getMensagemCurta());
        msgCurtaTxt.setFont(shortFont);
        TextFlow mensagemCurta = new TextFlow(msgCurtaTxt);

        VBox titleSumm = new VBox(spacing, tituloTxt, mensagemCurta);
        HBox tituloFaixa = new HBox(spacing, imgView, titleSumm);

        Text msgTxt = new Text(resumo.getMensagem());
        msgTxt.setFont(messageFont);
        TextFlow mensagemContent = new TextFlow(msgTxt);
        mensagemContent.setPrefWidth(prefWidth - 60);
        ScrollPane mensagem = new ScrollPane(mensagemContent);
        mensagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mensagem.setMinViewportHeight(prefWidth / 3);
        mensagem.setPrefViewportHeight(prefWidth / 2);
        mensagem.setFitToWidth(true);
        mensagem.setStyle("-fx-background-color:transparent;");
        Separator sep = new Separator();

        HBox pubDate = footerBand("Publicado em  ", resumo.getData().format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
        HBox autorName = footerBand("Elaborado por ", resumo.getAutor());
        HBox revBand = footerBand("Revisado por  ", resumo.getRevisores());
        HBox referencia = footerBand("Referência ", resumo.getReferencia());
        HBox labels = footerBand("Etiquetas ", resumo.getLabels());

        layout.getChildren().addAll(tituloFaixa, mensagem, sep, pubDate, autorName, revBand, referencia, labels);
        layout.setPadding(new Insets(30));
        Stage pop2 = new Stage();
        Scene scn = new Scene(layout);
        pop2.setScene(scn);
        pop2.setTitle("Fale com o Dr. Risadinha");
        pop2.show();
    }

    private HBox footerBand(String title, String line) {
        HBox band = new HBox(20);
        Text bandHeader = new Text(title);
        bandHeader.setFont(footerFont);
        Text bandLine = new Text(line);
        bandLine.setFont(footerFont);
        TextFlow footerLine = new TextFlow(bandLine);
        footerLine.setPrefWidth(prefWidth - 100);
        band.getChildren().addAll(bandHeader, footerLine);
        return band;
    }

    private HBox footerBand(String title, String[] lines) {
        StringBuilder labels = new StringBuilder();
        for (String label : lines) {
            labels.append(label).append("; ");
        }
        return footerBand(title, labels.toString());
    }

    private HBox footerBand(String title, Collection<String> lines) {
        HBox band = new HBox(20);
        VBox bandList = new VBox(25);
        TextFlow footerline = new TextFlow();
        for (String line : lines) {
            Text bandLine = new Text(line + "\n\n");
            bandLine.setFont(footerFont);
            footerline.getChildren().add(bandLine);
        }
        bandList.getChildren().add(footerline);
        if (lines.size() > 0) {
            Text bandHeader = new Text(title);
            bandHeader.setFont(footerFont);
            band.getChildren().addAll(bandHeader, bandList);
        }
        return band;
    }

    private void showResults(String search) {
        if (result.isEmpty()) {
            showError("Não há resultados para a busca por: " + search);
        } else {
            ListView<Resumo> resultadosView = new ListView<>(FXCollections.observableArrayList(result).sorted());
            resultadosView.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                        showContent(resultadosView.getSelectionModel().getSelectedItem());
                    }
                }
            });
            VBox layout = new VBox();
            layout.setSpacing(5);
            layout.setPadding(new Insets(10, 0, 0, 10));
            Text label1 = new Text(search.isEmpty() ? "Todos os resumos" : "Resultados da busca por: " + search);
            label1.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
            layout.getChildren().addAll(label1, resultadosView);
            Stage pop = new Stage();
            pop.setScene(new Scene(layout));
            pop.setWidth(620);
            pop.initModality(Modality.APPLICATION_MODAL);
            pop.show();
        }
    }

    private void performSearch(String searchString, boolean byTitle) {
        try {
            if (byTitle) {
                result = risadinha.getResumosByTitle(searchString);
            } else {
                result = risadinha.getResumosByLabel(searchString);
            }
            showResults(searchString);
        } catch (IOException ex) {
            showError(ex.toString());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        Label titleLabel = new Label("Busca por:");
        TextField stringSearch = new TextField();
        ToggleGroup group = new ToggleGroup();
        RadioButton button1 = new RadioButton("no título");
        button1.setToggleGroup(group);
        button1.setSelected(true);
        RadioButton button2 = new RadioButton("nas etiquetas");
        button2.setToggleGroup(group);
        Button btn = new Button("Buscar");

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String search = stringSearch.getText();
                performSearch(search, button1.isSelected());
            }
        });

        stringSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String search = stringSearch.getText();
                performSearch(search, button1.isSelected());
            }
        });

        root.setPadding(new Insets(30));
        root.setVgap(20);
        root.setHgap(20);
        root.add(titleLabel, 0, 0);
        root.add(stringSearch, 1, 0);
        root.add(button1, 0, 1);
        root.add(button2, 1, 1);
        root.add(btn, 1, 2);

        Scene scene = new Scene(root, 350, 150);

        primaryStage.setTitle("Buscar Dr. Risadinha");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
