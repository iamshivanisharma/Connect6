package com.shivani.connect6;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainConnect6 extends Application {
    private Controllerr controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader =new FXMLLoader(getClass().getResource("connectUI.fxml"));
        GridPane rootGridPane =loader.load();
        controller=loader.getController();
        controller.createPlayground();
        MenuBar menuBar=Createmenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane=(Pane)rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        Scene scene=new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 6");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public MenuBar Createmenu(){
        //File menu
        Menu file=new Menu("File");
        MenuItem newGame=new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame());
        MenuItem resetGame=new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
        MenuItem exitGame=new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());
        file.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
        Menu helpMenu=new Menu("Help");
        MenuItem aboutGame=new MenuItem("About Connect4");
        aboutGame.setOnAction(actionEvent -> aboutConnect4());
        SeparatorMenuItem separator=new SeparatorMenuItem();
        MenuItem aboutMe=new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());
        helpMenu.getItems().addAll(aboutGame,separator,aboutMe);

        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(file,helpMenu);
        return menuBar;
    }

    private void aboutMe() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Shivani Sharma");
        alert.setContentText("I love to make mobile and desktop applications.Connect6 is one of my favourite game which I implemented using Java");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect6 Game");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs." +
                " Connect Four is a solved game. The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
