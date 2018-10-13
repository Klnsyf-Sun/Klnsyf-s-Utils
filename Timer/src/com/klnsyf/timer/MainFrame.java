package com.klnsyf.timer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JProgressBar;
import java.awt.Font;
import java.awt.FontFormatException;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.Toolkit;

public class MainFrame {

	private JFrame timerFrame;

	private long totalTime;

	TimerThread timerThread = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.timerFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainFrame() {
		initialize();
	}

	private void initialize() {
		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		timerFrame = new JFrame();
		timerFrame.setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("icon.png")));
		timerFrame.setTitle("Timer");
		timerFrame.setBounds(100, 100, 800, 600);
		timerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 0.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0 };
		timerFrame.getContentPane().setLayout(gridBagLayout);

		JPanel labelPanel = new JPanel();
		GridBagConstraints gbcLabelPanel = new GridBagConstraints();
		gbcLabelPanel.weighty = 0.75;
		gbcLabelPanel.weightx = 0.75;
		gbcLabelPanel.fill = GridBagConstraints.BOTH;
		gbcLabelPanel.gridx = 0;
		gbcLabelPanel.gridy = 0;
		timerFrame.getContentPane().add(labelPanel, gbcLabelPanel);
		GridBagLayout gblLabelPanel = new GridBagLayout();
		gblLabelPanel.columnWidths = new int[] { 0 };
		gblLabelPanel.columnWeights = new double[] { 0.0 };
		gblLabelPanel.rowWeights = new double[] { 0.0, 0.0 };
		labelPanel.setLayout(gblLabelPanel);

		JLabel label = new JLabel("00:00.000 of 00:00.000");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbcLabel = new GridBagConstraints();
		gbcLabel.insets = new Insets(75, 50, 25, 50);
		gbcLabel.fill = GridBagConstraints.BOTH;
		gbcLabel.weighty = 0.5;
		gbcLabel.weightx = 0.5;
		gbcLabel.gridx = 0;
		gbcLabel.gridy = 0;
		labelPanel.add(label, gbcLabel);
		label.setFont(getFont(64));

		JPanel progressPanel = new JPanel();
		GridBagConstraints gbcProgressPanel = new GridBagConstraints();
		gbcProgressPanel.insets = new Insets(50, 50, 50, 50);
		gbcProgressPanel.weighty = 0.5;
		gbcProgressPanel.weightx = 0.5;
		gbcProgressPanel.fill = GridBagConstraints.BOTH;
		gbcProgressPanel.gridx = 0;
		gbcProgressPanel.gridy = 1;
		labelPanel.add(progressPanel, gbcProgressPanel);
		GridBagLayout gblProgressPanel = new GridBagLayout();
		gblProgressPanel.columnWidths = new int[] { 0, 0 };
		gblProgressPanel.rowHeights = new int[] { 0, 0 };
		gblProgressPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gblProgressPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		progressPanel.setLayout(gblProgressPanel);

		JProgressBar progressBar = new JProgressBar();
		GridBagConstraints gbcProgressBar = new GridBagConstraints();
		gbcProgressBar.insets = new Insets(25, 50, 50, 50);
		gbcProgressBar.weighty = 0.5;
		gbcProgressBar.weightx = 0.5;
		gbcProgressBar.fill = GridBagConstraints.BOTH;
		gbcProgressBar.gridx = 0;
		gbcProgressBar.gridy = 0;
		progressPanel.add(progressBar, gbcProgressBar);
		progressBar.setStringPainted(true);
		progressBar.setForeground(new Color(0x66ccff));
		progressBar.setFont(getFont(48));

		JLabel copyright = new JLabel("Copyright 2018 by Klnsyf Sun.  All Rights Reserved.");
		copyright.setFont(getFont(16));
		copyright.setForeground(Color.GRAY);
		GridBagConstraints gbcCopyright = new GridBagConstraints();
		gbcCopyright.fill = GridBagConstraints.VERTICAL;
		gbcCopyright.gridx = 0;
		gbcCopyright.gridy = 1;
		timerFrame.getContentPane().add(copyright, gbcCopyright);

		timerFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (String.valueOf(arg0.getKeyChar()).matches("\\d")) {
					totalTime *= 10;
					totalTime += Integer.parseInt(String.valueOf(arg0.getKeyChar())) * 1000;
					label.setText("00:00.000 of " + toTime(totalTime));
				} else if (arg0.getKeyChar() == '\b') {
					totalTime /= 10000;
					totalTime *= 1000;
					label.setText("00:00.000 of " + toTime(totalTime));
				} else if (arg0.getKeyChar() == '\n') {
					if (totalTime > 0) {
						if (timerThread == null || timerThread.done) {
							timerThread = new TimerThread(label, progressBar, totalTime);
							timerThread.start();
						}
					}
				} else if (arg0.getKeyChar() == 0x20) {
					if (timerThread != null) {
						if (timerThread.run) {
							timerThread.pauseThread();
						} else {
							timerThread.resumeThread();
						}
					}
				}
			}
		});

		timerFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (timerThread == null) {
					if (totalTime > 0) {
						timerThread = new TimerThread(label, progressBar, totalTime);
						timerThread.start();
					}
				} else {
					if (timerThread.done) {
						timerThread = new TimerThread(label, progressBar, totalTime);
						timerThread.start();
					} else {
						if (timerThread.run) {
							timerThread.pauseThread();
						} else {
							timerThread.resumeThread();
						}
					}
				}
			}
		});

	}

	private String checkTime(long time, int length) {
		StringBuffer str = new StringBuffer();
		for (int i = String.valueOf(time).length(); i < length; i++) {
			str.append("0");
		}
		str.append(time);
		return str.toString();
	}

	private String toTime(long time) {
		long totalMinute = time / 60000;
		long totalSecond = time % 60000 / 1000;
		long totalMilliSecond = time % 1000;
		StringBuffer str = new StringBuffer();
		return str.append(checkTime(totalMinute, 2)).append(":").append(checkTime(totalSecond, 2)).append(".")
				.append(checkTime(totalMilliSecond, 3)).toString();
	}

	public Font getFont(int size) {
		Font font = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			is = this.getClass().getClassLoader().getResourceAsStream("font.ttf");
			bis = new BufferedInputStream(is);
			font = Font.createFont(Font.TRUETYPE_FONT, bis);
			font = font.deriveFont(0, size);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bis) {
					bis.close();
				}
				if (null != is) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return font;
	}

	class TimerThread extends Thread {
		public volatile Thread blinker;

		private long startTime;
		private JLabel label;
		private JProgressBar progressBar;
		private long totalTime;

		private long base;
		public boolean run;
		public boolean done;

		public TimerThread(JLabel label, JProgressBar progressBar, long totalTime) {
			this.label = label;
			this.progressBar = progressBar;
			this.totalTime = totalTime;
		}

		public void start() {
			new SoundEffectThread().start();
			this.startTime = System.currentTimeMillis();
			run = true;
			done = false;
			blinker = new Thread(this);
			blinker.start();
		}

		public void run() {
			Thread thisThread = Thread.currentThread();
			while (blinker == thisThread) {
				if (run) {
					if (System.currentTimeMillis() - startTime + base > totalTime) {
						label.setText(toTime(totalTime) + " of " + toTime(totalTime));
						progressBar.setValue(0);
						new SoundEffectThread().start();
						this.stopThread();
						break;
					}
					label.setText(toTime(System.currentTimeMillis() - startTime + base) + " of " + toTime(totalTime));
					progressBar.setValue(
							(int) ((totalTime - (System.currentTimeMillis() - startTime + base)) * 100 / totalTime));
					if (progressBar.getValue() >= 50) {
						if (!progressBar.getForeground().equals(new Color(0x66ccff))) {
							progressBar.setForeground(new Color(0x66ccff));
							new SoundEffectThread().start();
						}
					} else if (progressBar.getValue() >= 20) {
						if (!progressBar.getForeground().equals(new Color(0x39c5bb))) {
							progressBar.setForeground(new Color(0x39c5bb));
							new SoundEffectThread().start();
						}
					} else if (progressBar.getValue() >= 10) {
						if (!progressBar.getForeground().equals(new Color(0xff6600))) {
							progressBar.setForeground(new Color(0xff6600));
							new SoundEffectThread().start();
						}
					} else if (progressBar.getValue() > 0) {
						if (!progressBar.getForeground().equals(new Color(0xff0000))) {
							progressBar.setForeground(new Color(0xff0000));
							new SoundEffectThread().start();
						}
					}
					try {
						TimerThread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		public void stopThread() {
			blinker = null;
			run = false;
			done = true;
		}

		public void pauseThread() {
			base += System.currentTimeMillis() - startTime;
			run = false;
		}

		public void resumeThread() {
			startTime = System.currentTimeMillis();
			run = true;
		}
	}

	class SoundEffectThread extends Thread {
		URL url = this.getClass().getClassLoader().getResource("beep.wav");
		AudioClip ac = Applet.newAudioClip(url);

		public void run() {
			ac.play();
		}
	}
}
