package brickbreaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import leaderboard.LeaderboardClient;

@SuppressWarnings("serial")
public class BrickBreaker extends JPanel implements KeyListener, ActionListener, Runnable {
	//server score variables
	private static boolean scoreSent = false;
	private static int score = 0;
	// movement keys..
	private static boolean right = false;
	private static boolean left = false;
	// ..............
	// variables declaration for ball.................................
	private int ballx = 160;
	private int bally = 218;
	// variables declaration for ball.................................
	// ===============================================================
	// variables declaration for bat..................................
	private int batx = 160;
	private int baty = 245;
	// variables declaration for bat..................................
	// ===============================================================
	// variables declaration for brick...............................
	private int brickx = 70;
	private int bricky = 50;
	
	private int brickBreadth = 30;
	private int brickHeight = 20;
	// variables declaration for brick...............................
	// ===============================================================
	// declaring ball, paddle,bricks
	private Rectangle Ball = new Rectangle(ballx, bally, 5, 5);
	private Rectangle Bat = new Rectangle(batx, baty, 40, 5);
	// Rectangle Brick;// = new Rectangle(brickx, bricky, 30, 10);
	private Rectangle[] Brick = new Rectangle[12];
	
	//reverses......==>
	private double movex = -1;
	private double movey = -1;
	private boolean ballFallDown = false;
	private boolean bricksOver = false;
	private int count = 0;
	private String status;
	
	public BrickBreaker() {
		
	}
	
	// declaring ball, paddle,bricks
	
	public void paint(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, 350, 450);
		g.setColor(Color.black);
		g.fillOval(Ball.x, Ball.y, Ball.width, Ball.height);
		g.setColor(Color.green);
		g.fill3DRect(Bat.x, Bat.y, Bat.width, Bat.height, true);
		g.setColor(Color.GRAY);
		g.fillRect(0, 251, 450, 200);
		g.setColor(Color.red);
		g.drawRect(0, 0, 343, 250);
		for (int i = 0; i < Brick.length; i++) {
			if (Brick[i] != null) {
				g.fill3DRect(Brick[i].x, Brick[i].y, Brick[i].width, Brick[i].height, true);
			}
		}

		if (ballFallDown == true || bricksOver == true) {
			Font f = new Font("Arial", Font.BOLD, 20);
			g.setFont(f);
			g.drawString(status, 70, 120);
			ballFallDown = false;
			bricksOver = false;
		}
		
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, (getWidth()/2)-30, 320);
	}

	// /...Game Loop...................
	// /////////////////// When ball strikes borders......... it
	public void run() {
		// //////////// =====Creating bricks for the game===>.....
		createBricks(true);
		// ===========BRICKS created for the game new ready to use===
		// ====================================================
		// == ball reverses when touches the brick=======
		//ballFallDown == false && bricksOver == false
		while (true) {
			//   if(gameOver == true){return;}
			for (int i = 0; i < Brick.length; i++) {
				if (Brick[i] != null) {
					if (Brick[i].intersects(Ball)) {
						Brick[i] = null;
						// movex = -movex;
						score += 10;
						movey = -movey;
						count++;
					}// end of 2nd if..
				}// end of 1st if..
			}// end of for loop..
			// /////////// =================================
			if (count == Brick.length) {// check if ball hits all bricks
				try {
					Thread.sleep(400);
				} catch(Exception e) {
					//We don't really want the application to crash,
					//		but we do want it to pause for a second.
				}
				repaint();
				createBricks(false);
			}
			// /////////// =================================
			repaint();
			Ball.x += movex;
			Ball.y += movey;
			if (left == true) {
				Bat.x -= 3;
				right = false;
			}
			if (right == true) {
				Bat.x += 3;
				left = false;
			}
			if (Bat.x <= 4) {
				Bat.x = 4;
			} else if (Bat.x >= 298) {
				Bat.x = 298;
			}
			// /===== Ball reverses when strikes the bat
			if (Ball.intersects(Bat)) {
				movey = -movey;
				// if(Ball.y + Ball.width >=Bat.y)
			}
			// //=====================================
			// ....ball reverses when touches left and right boundary
			if (Ball.x <= 0 || Ball.x + Ball.height >= 343) {
				movex = -movex;
			}// if ends here
			if (Ball.y <= 0) {// ////////////////|| bally + Ball.height >= 250
				movey = -movey;
			}// if ends here.....
			if (Ball.y >= 250 && !bricksOver) {// when ball falls below bat game is over...
				ballFallDown = true;
				status = "YOU LOST THE GAME";
				//deal with sending the score to the server
				if(!scoreSent) {
					int dialog = JOptionPane.showConfirmDialog(null, "Do you want to send your score to the server?");
					if(dialog == 0) {
						String username = JOptionPane.showInputDialog("What do you want your name to be (max of 3 characters, letters will be stored as uppercase only)?");
						if(username.equals("")) {
							username = "NAN";
						} else if(username.length() > 3) {
							username = username.substring(0, 3);
						} else {
							username = username.toUpperCase();
						}
						JOptionPane.showConfirmDialog(null, "Scores: " + Arrays.toString(LeaderboardClient.getInstance().brickBreakerRequestLeaderboards(username + " " + score)));
					}
					scoreSent = true;
				}
				repaint();
			}
			try {
				Thread.sleep(10);
			} catch (Exception ex) {
			}// try catch ends here
		}// while loop ends here
	}
	// loop ends here
	// ///////..... HANDLING KEY EVENTS................//
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			left = true;
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			right = true;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT) {
			left = false;
		}
		
		if (keyCode == KeyEvent.VK_RIGHT) {
			right = false;
		}
	}
	
	@Override
 	public void keyTyped(KeyEvent arg0) {
	 
 	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals("Restart")) {
			this.restart();
		}
	}
	
	public void restart() {
		requestFocus(true);
		createBricks(true);
		repaint();
		scoreSent = false;
	}

	public void initializeVariables(int ballx, int bally, double speed){
		// ..............
		// variables declaration for ball.................................
		this.ballx = ballx;
		this.bally = bally;
		// variables declaration for ball.................................
		// ===============================================================
		// variables declaration for bat..................................
		batx = 160;
		baty = 245;
		// variables declaration for bat..................................
		// ===============================================================
		// variables declaration for brick...............................
		brickx = 70;
		bricky = 50;
		// variables declaration for brick...............................
		// ===============================================================
		// declaring ball, paddle,bricks
		Ball = new Rectangle(ballx, bally, 5, 5);
		Bat = new Rectangle(batx, baty, 40, 5);
		// Rectangle Brick;// = new Rectangle(brickx, bricky, 30, 10);
		Brick = new Rectangle[12];
		
		movex = -Math.abs(speed*movex);
		movey = -Math.abs(speed*movey);
		
		ballFallDown = false;
		bricksOver = false;
		count = 0;
		status = null;
	}
	
	public void createBricks(boolean isInit){
		// //////////// =====Creating bricks for the game===>.....
		/*
		 * creating bricks again because this for loop is out of while loop in
		 * run method
		 */
		if(isInit) {
			initializeVariables(160,218,1);
		} else {
			initializeVariables(ballx,bally,1.3);
		}
		for (int i = 0; i < Brick.length; i++) {
			Brick[i] = new Rectangle(brickx, bricky, brickBreadth, brickHeight);
			if (i == 5) {
				brickx = 70;
				bricky = (bricky + brickHeight + 2);
			}
			if (i == 9) {
				brickx = 100;
				bricky = (bricky + brickHeight + 2);
			}
			brickx += (brickBreadth+1);
		}
	}
}
