package Medicamentos;

import Medicamentos.View.TelaPrincipal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        TelaPrincipal tela = new TelaPrincipal();
        Scene scene = new Scene(tela.criarTela(), 1100, 700);
        stage.setTitle("Sistema de Estoque de Medicamentos");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
