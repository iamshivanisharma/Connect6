package com.shivani.connect6;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controllerr implements Initializable {
	private static final int COLUMN = 10;
	public static final int ROWS = 8;
	private static final int CIRCLE_DIAMETER = 80;
	private static Color discColor1 = null;
	private static Color discColor2 = null;
	private static String PLAYER_ONE = "Player 1";
	private static String PLAYER_TWO = "Player 2";
	private boolean isPlayerOneTurn = true;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMN]; //Structural Change
	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label PlayerNameLabel;
	@FXML
	public TextField PlayerOneTextField, PlayerTwoTextField;
	@FXML
	public ColorPicker PlayerOneColor,PlayerTwoColor;
	@FXML
	public Button SetNamesButton;

	private boolean isAllowedToInsert=true; //Flag to avoid same color disc insertion simultaneously

	public void createPlayground() {

		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);
		List<Rectangle> rectangleList = createClickableCol();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
		SetNamesButton.setOnAction(event -> {
			PLAYER_ONE=PlayerOneTextField.getText();
			PLAYER_TWO=PlayerTwoTextField.getText();
			discColor1=PlayerOneColor.getValue();
					discColor2=PlayerTwoColor.getValue();
		});
	}

	private Shape createGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((COLUMN + 1.2) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
		for (int row = 0; row < ROWS; row++) {
			for (int column = 0; column < COLUMN; column++) {
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);
				circle.setTranslateX(column * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableCol() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < COLUMN; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);
			rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee40")));
			rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(mouseEvent -> {
				if(isAllowedToInsert) {
					isAllowedToInsert=false;//when disc is dropped no more disc will be inserted
					insertDisc(new Disc(isPlayerOneTurn), column);
				}
			});
			rectangleList.add(rectangle);
		}

		return rectangleList;

	}

	private void insertDisc(Disc disc, int column) {
		int row = ROWS - 1;
		while (row >= 0) {
			if (getIfDiscPresent(row, column) == null)
				break;
			row--;
		}
		if (row < 0) { //if rows full, cannot insert any more disc
			return;
		}

		insertedDiscArray[row][column] = disc; //for structural changes
		insertedDiscPane.getChildren().add(disc);
		int currentRow = row;
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 7) + CIRCLE_DIAMETER / 4);
		isAllowedToInsert=true; //Finally, when disc is dropped allow next player to insert disc
		if (gameEnded(currentRow, column)) {
			gameOver();
		}
		translateTransition.setOnFinished(actionEvent -> {
			isPlayerOneTurn = !isPlayerOneTurn;
			PlayerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);


		});
		translateTransition.play();
	}

	private boolean gameEnded(int row, int column) {

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 5, row + 5)
				.mapToObj(r -> new Point2D(r, column))
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 5, column + 5)
				.mapToObj(col -> new Point2D(row, col))
				.collect(Collectors.toList());
		Point2D startPoint1 = new Point2D(row - 5, column + 5);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 10)
				.mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());
		Point2D startPoint2 = new Point2D(row - 5, column - 5);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 10)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());
		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int colIndexForArray = (int) point.getY();
			//Disc disc=insertedDiscArray[rowIndexForArray][colIndexForArray];
			Disc disc = getIfDiscPresent(rowIndexForArray, colIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) { //if the last inserted disc belongs to current player
				chain++;
				if (chain == 6)
					return true;
			} else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getIfDiscPresent(int row, int column) { //to prevent IndexOutOfBoundException
		if (row >= ROWS || row < 0 || column >= COLUMN || column < 0) { //if row or col invalid
			return null;


		}
		return insertedDiscArray[row][column];
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		System.out.println("Winner is: " + winner);
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect6");
		alert.setHeaderText("The Winner is :" + winner);
		alert.setContentText("Want to play again? ");
		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn, noBtn);
		Platform.runLater(() -> {
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
				//user chose yes or reset the game
				resetGame();
			} else {
				//user chose No.. so Exit the game
				Platform.exit();
				System.exit(0);

			}


		});

	}

	public void resetGame() {
		insertedDiscPane.getChildren().clear(); //remove all inserted disc from Pane
		for(int row=0;row<insertedDiscArray.length;row++)
		{
			for(int col=0;col<insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col]=null;
			}

		}
		isPlayerOneTurn=true; //let player start the game
		PlayerNameLabel.setText(PLAYER_ONE);
		PlayerOneColor.setValue(Color.WHITE);
		PlayerTwoColor.setValue(Color.WHITE);
		PlayerOneTextField.clear();
		PlayerTwoTextField.clear();
		createPlayground(); //prepare fresh playground
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(String.valueOf(discColor1)) : Color.valueOf(String.valueOf(discColor2)));
			setCenterX(CIRCLE_DIAMETER /2 );
			setCenterY(CIRCLE_DIAMETER /2 );

		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}


