package com.klnsyf.transparenttimer;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Controller implements Initializable {

	@FXML
	private VBox vBox;

	@FXML
	private ProgressBar progressBar;

	@FXML
	private Label label;

	@FXML
	void onStartTimer(MouseEvent event) {
		if (Main.startTime == 0) {
			vBox.setOpacity(1.0);
			Main.startTime = System.currentTimeMillis();
			new SoundEffectThread().start();
			Platform.runLater(timerThread);
		}
	}

	@FXML
	void onMouseEntered(MouseEvent event) {
		Main.base += 0.5;
		vBox.setOpacity(Math.min(1.0, Main.base + 5 * Math.max(0.01, Math.min(0.1,
				(((double) System.currentTimeMillis() - (double) Main.startTime) / (double) Main.TOTAL_TIME) - 0.9))));
	}

	@FXML
	void onMouseExited(MouseEvent event) {
		Main.base -= 0.5;
		if (Main.base < 0.01) {
			Main.base = 0.01;
		}
		vBox.setOpacity(Math.min(1.0, Main.base + 5 * Math.max(0.01, Math.min(0.1,
				(((double) System.currentTimeMillis() - (double) Main.startTime) / (double) Main.TOTAL_TIME) - 0.9))));
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		label.setText(milliSec2timeString(Main.TOTAL_TIME));
	}

	private String milliSec2timeString(long milliSec) {
		int minute = (int) (milliSec / 60 / 1000);
		int second = (int) ((milliSec - minute * 60 * 1000) / 1000);
		return (minute < 10 ? "0" : "") + minute + ":" + (second < 10 ? "0" : "") + second;
	}

	Runnable timerThread = new Runnable() {

		Runnable task = new Runnable() {
			public void run() {
				Main.base += 0.5;
				while (Main.base > 0) {
					try {
						Thread.sleep((long) Math.min(50, Main.TOTAL_TIME * 0.005));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Main.base -= 0.005;
				}
				Main.base = 0.01;
				return;
			}
		};

		public void run() {
			if (System.currentTimeMillis() - Main.startTime > Main.TOTAL_TIME) {
				label.setText("00:00");
				progressBar.setProgress(0);
				new SoundEffectThread().start();
				return;
			}
			label.setText(milliSec2timeString(Main.TOTAL_TIME - (System.currentTimeMillis() - Main.startTime)));
			progressBar.setProgress(
					(((double) Main.TOTAL_TIME - ((double) System.currentTimeMillis() - (double) Main.startTime))
							/ (double) Main.TOTAL_TIME));
			vBox.setOpacity(Math.min(1.0,
					Main.base + 5 * Math.max(0.01,
							(((double) System.currentTimeMillis() - (double) Main.startTime) / (double) Main.TOTAL_TIME)
									- 0.9)));
			if (progressBar.getProgress() >= 0.5) {
				if (!progressBar.getStyle().contains("-fx-accent:#66ccff")) {
					progressBar.setStyle("-fx-accent:#66ccff");
					new SoundEffectThread().start();
					new Thread(task).start();
				}
			} else if (progressBar.getProgress() >= 0.2) {
				if (!progressBar.getStyle().contains("-fx-accent:#39c5bb")) {
					progressBar.setStyle("-fx-accent:#39c5bb");
					new SoundEffectThread().start();
					new Thread(task).start();
				}
			} else if (progressBar.getProgress() > 0.1) {
				if (!progressBar.getStyle().contains("-fx-accent:#ff6600")) {
					progressBar.setStyle("-fx-accent:#ff6600");
					new SoundEffectThread().start();
					new Thread(task).start();
				}
			}
			else if (progressBar.getProgress() > 0) {
				if (!progressBar.getStyle().contains("-fx-accent:#ff0000")) {
					progressBar.setStyle("-fx-accent:#ff0000");
					new SoundEffectThread().start();
					new Thread(task).start();
				}
			}
			Platform.runLater(this);
		}

	};

	class SoundEffectThread extends Thread {
		URL url = this.getClass().getClassLoader().getResource("beep.wav");
		AudioClip ac = Applet.newAudioClip(url);

		public void run() {
			ac.play();
		}
	}

}
