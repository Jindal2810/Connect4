package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
    private static final int COLUMNS=7;
    private static final int ROWS=6;
    private static final int CIRCLE_DIAMETER=120;
    private static final String discColor1="#24303E";
    private static final String discColor2="#4CAA88";
    private static String Player_One="Player One";
    private static String Player_Two="Player Two";
    private boolean isPlayerOneTurn=true;
    private Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS]; //For structural changes :for developers

@FXML
public Button setNamesButton;
@FXML
public TextField playerOneTextField;
@FXML
public TextField playerTwoTextField;

    @FXML
    public GridPane rootGridPane;
    @FXML
    public Pane insertedDiscsPane;
    @FXML
    public Label playerNameLabel;

    private boolean isAllowedToInsert=true;   //Flag to avoid same colour disc being added
    public void createPlayground() {
        Shape rectangleWithHoles = gameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);
        List<Rectangle> rectangleList = createClickableColumns();
        for (Rectangle rectangle : rectangleList) {
            rootGridPane.add(rectangle, 0, 1);
        }
        setNamesButton.setOnAction(event -> {

    Player_One=playerOneTextField.getText();

    Player_Two=playerTwoTextField.getText();
    playerNameLabel.setText(Player_One);
        });

    }
    private Shape gameStructuralGrid(){
        Shape rectangleWithHoles= new Rectangle((COLUMNS+1)*(CIRCLE_DIAMETER),(ROWS+1)*(CIRCLE_DIAMETER));

        for(int row=0;row<ROWS;row++){
            for(int column=0;column<COLUMNS;column++){

                Circle circle=new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);
                circle.setTranslateY(row *(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
                circle.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);

                rectangleWithHoles =Shape.subtract(rectangleWithHoles,circle);
            }
        }
        rectangleWithHoles.setFill(Color.WHITE);

        return rectangleWithHoles;
    }
    private List<Rectangle> createClickableColumns() {
        List<Rectangle> rectangleList = new ArrayList<>();
        for (int col = 0; col < COLUMNS; col++) {
            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * (CIRCLE_DIAMETER));
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
            rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
            final int column=col;
            rectangle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert=false;  //When disc is being dropped then.
                    // no more disc will be inserted
                    insertDisc(new Disc(isPlayerOneTurn), column);
                }});
            rectangleList.add(rectangle);

        }
        return rectangleList;
    }
    private void insertDisc(Disc disc,int col){
       int row =ROWS-1;
        while(row >=0){
            if(getDiscIfPresent(row,col)==null)

                break;
            row--;
        }
       if(row<0) //if row is full,then no more disc will be inserted
            return;//if we didn't write this code then also this game work properly
  insertedDiscsArray[row][col]=disc;
        insertedDiscsPane.getChildren().add(disc);
        disc.setTranslateX((col)*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row *(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
 final int currentRow=row;
        translateTransition.setOnFinished(event ->  {
isAllowedToInsert=true;
            if(gameEnded(currentRow,col)){
gameOver();
return;
            }
            isPlayerOneTurn=!isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn?Player_One:Player_Two);
        });
        translateTransition.play();

    }



    private boolean gameEnded(int row, int col) {

       List<Point2D> verticalPoints= IntStream.rangeClosed(row-3,row+3) //  row=3,col=3   range of row values=0,1,2,3,4,5
       // List<Point2D> verticalPoints= IntStream.rangeClosed(0,5)
                .mapToObj(r->new Point2D(r,col))  //0,3 1,3 2,3 3,3 4,3 5,3--->Point2D x,y
                .collect(Collectors.toList());

        List<Point2D> horizontalPoints= IntStream.rangeClosed(col-3,col+3) //range of col values=0,1,2,3,4,5
                .mapToObj(c->new Point2D(row,c))  //0,3 1,3 2,3 3,3 4,3 5,3--->Point2D x,y
                .collect(Collectors.toList());

        Point2D startPoint1=new Point2D(row-3,col+3);
        List<Point2D> diagonal1Points=IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint1.add(i,-i))
                .collect(Collectors.toList());

        Point2D startPoint2=new Point2D(row-3,col-3);
        List<Point2D> diagonal2Points=IntStream.rangeClosed(0,6)
                .mapToObj(i-> startPoint2.add(i,i))
                .collect(Collectors.toList());

        boolean isEnded=checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);
        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {
        int chain=0;

        for(Point2D point:points){
            int rowIndexForArray=(int)point.getX();
            int columnIndexForArray=(int)point.getY();

            Disc disc=getDiscIfPresent(rowIndexForArray,columnIndexForArray);

            if(disc !=null&&disc.isPlayerOneMove==isPlayerOneTurn){       //if the last inserted disc belongs to current player
                chain++;
                if(chain==4){
                    return true;
                }
            }
            else
            {
                chain=0;
            }
        }
        return false;
    }
    private Disc getDiscIfPresent(int row,int column){   //To prevent array index out of bound exception
        if(row>=ROWS||row<0||column>=COLUMNS||column<0)
            return null;
        return insertedDiscsArray[row][column];
    }

    private void gameOver() {
        String winner=isPlayerOneTurn?Player_One:Player_Two;
        System.out.println("The winner is "+winner);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The winner is "+winner);
        alert.setContentText("Want to play again?");

        ButtonType yesBtn=new ButtonType("Yes");
ButtonType noBtn=new ButtonType("No,Exit");
alert.getButtonTypes().setAll(yesBtn,noBtn);

Platform.runLater( ()-> {
    Optional<ButtonType> btnClicked=  alert.showAndWait();
    if(btnClicked.isPresent()&&btnClicked.get()==yesBtn){
        resetGame();
    }else{
        Platform.exit();
        System.exit(0);
    }

});


    }

   public  void resetGame() {
        insertedDiscsPane.getChildren().clear();  //Remove all inserted discs from pane
        for(int row=0;row<insertedDiscsArray.length;row++){
            for(int col=0;col<insertedDiscsArray[row].length;col++){
                insertedDiscsArray[row][col]=null;       //Structurally make all insertedDiscsArray elements=null
            }
        }
      //  isPlayerOneTurn=true;
      //  playerNameLabel.setText(Player_One);
        createPlayground();

    }

    private static class Disc extends Circle{
private final boolean isPlayerOneMove;

        private Disc(boolean isPlayerOneMove) {
            this.isPlayerOneMove=isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER/2);
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);
            setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}
