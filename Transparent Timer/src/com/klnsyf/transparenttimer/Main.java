package com.klnsyf.transparenttimer;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;

public class Main extends Application {
	public static final long TOTAL_TIME = 60 * 1000;
	public static long startTime;
	public static double base;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
			Font.loadFont(getClass().getResourceAsStream("/font.ttf"), 64);
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			Scene scene = new Scene(root, primaryScreenBounds.getWidth(), 68);
			scene.setFill(null);
			primaryStage.initStyle(StageStyle.TRANSPARENT);
			primaryStage.setAlwaysOnTop(true);
			primaryStage.setScene(scene);
			primaryStage.setX(0);
			primaryStage.setY(0);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
