package com.klnsyf.randomnumbergenerator;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.RenderingHints;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame {

	private JFrame randomNumberGeneratorFrame;
	private static int X = 180;
	private static int Y = 132;
	private int amplifier = 12;
	private int fps = 60;
	private int time = 1000;
	private static boolean blink = false;
	private static int[][] board = new int[X][Y];
	private static float alpha = 1;
	private int[][][] cache = new int[fps * time / 1000][X][Y];
	private int rule = 224;
	private char[] ruleArray = new StringBuilder(Integer.toBinaryString(rule)).reverse().toString().toCharArray();
	private boolean run = false;
	private ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.randomNumberGeneratorFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainFrame() throws IOException, URISyntaxException {
		initialize();
	}

	private void initialize() throws IOException, URISyntaxException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("list")));
		String[] strs = reader.readLine().split(" ");
		reader.close();
		for (int i = 0; i < strs.length; i++) {
			list.add(strs[i]);
		}
		String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		randomNumberGeneratorFrame = new JFrame();
		randomNumberGeneratorFrame.setIconImage(
				Toolkit.getDefaultToolkit().getImage(this.getClass().getClassLoader().getResource("icon.png")));
		randomNumberGeneratorFrame.setTitle("Random Number Generator");
		randomNumberGeneratorFrame.setBounds(100, 100, 800, 600);
		randomNumberGeneratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		randomNumberGeneratorFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		randomNumberGeneratorFrame.getContentPane().add(MyPanel.getInstance(), BorderLayout.CENTER);
		randomNumberGeneratorFrame.setContentPane(MyPanel.getInstance());

		randomNumberGeneratorFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (!run) {
					random();
				}
			}
		});

		randomNumberGeneratorFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!run) {
					if (arg0.getKeyChar() == '\n' || arg0.getKeyChar() == 0x20) {
						random();
					}
				}
			}
		});

		new InitThread().start();

	}

	private void random() {
		if (list.size() > 0) {
			int index = (int) (Math.random() * list.size());
			CellularAutomataThread cellularAutomataThread = new CellularAutomataThread();
			cellularAutomataThread.start();
			new CellularAutomataCalculateThread(Integer.parseInt(list.get(index)) / 10,
					Integer.parseInt(list.get(index)) % 10, cellularAutomataThread).start();
			list.remove(index);
		} else {
			CellularAutomataThread cellularAutomataThread = new CellularAutomataThread();
			cellularAutomataThread.start();
			new CellularAutomataCalculateThread(-1, 1, cellularAutomataThread).start();
		}
	}

	static class MyPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static MyPanel myPanel = new MyPanel();
		private long count;

		public static MyPanel getInstance() {
			return MyPanel.myPanel;
		}

		public MyPanel() {
			super();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			renderStep(g);
			count++;
		}

		public void renderStep(Graphics g) {
			Graphics2D g2d = (Graphics2D) g.create();
			int width = this.getWidth();
			int height = this.getHeight();
			int border = 50;
			int blockSize = Math.min((width - 2 * border) / X, (height - 2 * border) / Y);
			int blockBorder = (int) (blockSize * 0.05);
			int borderX = border + (width - 2 * border - blockSize * X) / 2;
			int borderY = border + (height - 2 * border - blockSize * Y) / 2;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(new Color(0, 0, 0, alpha));
			for (int i = 0; i < X; i++) {
				for (int j = 0; j < Y; j++) {
					if (board[i][j] == ((count % 2 == 1 && blink) ? 0 : 1)) {
						g2d.fillRect(borderX + i * blockSize + blockBorder, borderY + j * blockSize + blockBorder,
								blockSize - 2 * blockBorder, blockSize - 2 * blockBorder);
					}
				}
			}
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			g2d.setFont(MainFrame.getFont(16));
			g2d.setColor(Color.GRAY);
			g2d.drawString("Copyright 2018 by Klnsyf Sun.  All Rights Reserved.",
					(width - g2d.getFontMetrics(MainFrame.getFont(16))
							.stringWidth("Copyright 2018 by Klnsyf Sun.  All Rights Reserved.")) / 2,
					height - 5);
			g2d.dispose();
		}
	}

	class InitThread extends Thread {
		@Override
		public void run() {
			for (int i = 0; i < X - 12; i++) {
				board[6 + i][Y / 2 - 1] = 1;
				board[6 + i][Y / 2 + 1] = 1;
			}
			MyPanel.getInstance().repaint();
		}
	}

	class CellularAutomataThread extends Thread {
		public boolean done = false;

		@Override
		public void run() {
			run = true;
			for (int i = 0; i < fps * time / 1000; i++) {
				updateCells(board, ruleArray);
				alpha = 1 - (float) i / (fps * time / 1000);
				MyPanel.getInstance().repaint();
				try {
					Thread.sleep(1000 / fps);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			done = true;
		}
	}

	class CellularAutomataCalculateThread extends Thread {
		private int number0;
		private int number1;
		private CellularAutomataThread cellularAutomataThread;

		public CellularAutomataCalculateThread(int number0, int number1,
				CellularAutomataThread cellularAutomataThread) {
			this.number0 = number0;
			this.number1 = number1;
			this.cellularAutomataThread = cellularAutomataThread;
		}

		@Override
		public void run() {
			int[][] cacheBoard = new int[X][Y];
			for (int i = 2; i < X - 2; i++) {
				for (int j = 0; j < Y; j++) {
					cacheBoard[i][j] = 1;
				}
			}
			for (int i = 2; i < X - 2; i++) {
				for (int j = 1; j < Y - 1; j++) {
					cacheBoard[i][j] = 0;
				}
			}
			drawNumber(0, number0, cacheBoard);
			drawNumber(1, number1, cacheBoard);
			for (int i = 0; i < fps * time / 1000; i++) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Y; k++) {
						cache[i][j][k] = cacheBoard[j][k];
					}
				}
				updateCells(cacheBoard, ruleArray);
			}
			while (!cellularAutomataThread.done) {
				try {
					Thread.sleep(1000 / fps);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < fps * time / 1000; i++) {
				for (int j = 0; j < X; j++) {
					for (int k = 0; k < Y; k++) {
						board[j][k] = cache[fps * time / 1000 - 1 - i][j][k];
					}
				}
				alpha = (float) i / (fps * time / 1000);
				MyPanel.getInstance().repaint();
				try {
					Thread.sleep(1000 / fps);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			run = false;
		}
	}

	private int cellWeight(int[][] cells, int x, int y) {
		int weight = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i == 0 && j == 0) {
					if (cells[x][y] == 1) {
						weight = weight + 1;
					}
				} else {
					if (cells[(x + i + X) % X][(y + j + Y) % Y] == 1) {
						weight = weight + 2;
					}
				}
			}
		}
		return weight;
	}

	private int[][] cellsWeight(int[][] cells) {
		int[][] weights = new int[X][Y];
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				weights[i][j] = cellWeight(cells, i, j);
			}
		}
		return weights;
	}

	private void updateCells(int[][] cells, char[] ruleArray) {
		int[][] weight = cellsWeight(cells);
		for (int i = 0; i < X; i++) {
			for (int j = 0; j < Y; j++) {
				cells[i][j] = ruleArray.length > weight[i][j]
						? Integer.parseInt(String.valueOf(ruleArray[weight[i][j]]))
						: 0;
			}
		}
	}

	private void drawNumber(int numberIndex, int number, int[][] board) {
		int anchorX = 0;
		int anchorY = amplifier * 3;
		switch (numberIndex) {
		case 0:
			anchorX = amplifier * 3;
			break;
		case 1:
			anchorX = amplifier * 9;
			break;
		default:
			break;
		}
		switch (number) {
		case 0:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 1:
			for (int i = amplifier * 2; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			break;
		case 2:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = amplifier; i < amplifier * 3; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 3:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 4:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = 0; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 5:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 3; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 6:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 3; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 7:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 8:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case 9:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = 0; j < amplifier * 5; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			for (int i = amplifier; i < amplifier * 2; i++) {
				for (int j = amplifier; j < amplifier * 2; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			for (int i = 0; i < amplifier * 2; i++) {
				for (int j = amplifier * 3; j < amplifier * 4; j++) {
					board[anchorX + i][anchorY + j] = 0;
				}
			}
			break;
		case -1:
			for (int i = 0; i < amplifier * 3; i++) {
				for (int j = amplifier * 2; j < amplifier * 3; j++) {
					board[anchorX + i][anchorY + j] = 1;
				}
			}
			break;
		default:
			break;
		}
	}

	private static Font getFont(int size) {
		Font font = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		try {
			is = MainFrame.class.getClassLoader().getResourceAsStream("font.ttf");
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
}
