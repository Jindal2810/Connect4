package com.internshala.connectfour;

import com.sun.deploy.security.SelectableSecurityManager;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {


    public static void main(String[] args) {
        System.out.println("Application has been launched");
        launch(args);
    }

    @Override
    public void init() throws Exception {
        System.out.println("Application started");
        super.init();
    }

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        controller = loader.getController();
        controller.createPlayground();
        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().addAll(menuBar);

        Scene scene = new Scene(rootGridPane);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu() {
        //File Menu
        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New game");
        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset game");
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit game");
        exitGame.setOnAction(event ->{
            alertBox();
        });


        //Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About Connect 4");
        aboutGame.setOnAction(event -> aboutConnect4());
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> {
            aboutMe();
        });
        SeparatorMenuItem separator = new SeparatorMenuItem();

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);
        helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private void alertBox() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Exit Game");
        alert.setContentText("Do you really want to exit the game?");

        ButtonType yesBtn=new ButtonType("Yes");
        ButtonType noBtn=new ButtonType("No");
        alert.getButtonTypes().setAll(yesBtn,noBtn);
        Optional<ButtonType> clickbtn=alert.showAndWait();
        if(clickbtn.isPresent()&&clickbtn.get()==yesBtn)
        exitGame();

    }

    private void aboutMe() {
        Alert alert = new Alert((Alert.AlertType.INFORMATION));
        alert.setTitle("About the Developer");
        alert.setHeaderText("Muskan Jindal");
        alert.setContentText("I love to play around with codes and developing new games especially for children. "+
                "Connect four is one of them."+
                "In free time ,I like to spend time with children and to play cricket");
        alert.show();
    }

    private void aboutConnect4() {
        Alert alert = new Alert((Alert.AlertType.INFORMATION));
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play?");
        alert.setContentText("Connect four is a two player connection game in which the "+
                        "the players first choose a colour and then take turns dropping coloured discs "+
                        "from the top into a seven-column,six row vertically suspended grid."+
                        "The pieces fall straight  down ,occupying next available space within the column."+
                        "The objective of the game is to be the first to form horizontal,vertical"+
                        " or diagonal line of four of one's own discs.Connect four is a solved game.+" +
                        "The first player will always win by playing the right moves.");
        alert.show();
    }


    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Application stopped");
        super.stop();
    }
}



