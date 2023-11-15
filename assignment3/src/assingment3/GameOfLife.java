package assingment3;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class GameOfLife extends JFrame implements Runnable, MouseListener, MouseMotionListener
{
	// member data
	private BufferStrategy strategy;
	private Graphics offscreenBuffer;
	private boolean gameState[][][]= new boolean[40][40][2];
	private int gameStateFrontBuffer = 0;
	private boolean isPlaying = false;
	private boolean initialised= false;
	private String filename = "/Users/martinhanna/Desktop/lifegame.txt";
	private String[][] arrA = new String[40][40];
	char[] myChar = new char[1600];
	private Instant start;
	private Instant finish;
	
	// constructor
	public GameOfLife()
	{
		// display the window, centred on the screen
		Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int x = screensize.width/2 - 400;
		int y = screensize.height/2 - 400;
		setBounds(x,y,800,800);
		setVisible(true);
		this.setTitle("Conways Game of Life");
		
		// initialise double-buffering
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		offscreenBuffer = strategy.getDrawGraphics();
		
		// register the Jframe itself to receive mouse events
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// initialise the game state
		for(x = 0; x < 40; x++) {
			
			for(y = 0; y < 40; y++) {
				
				gameState[x][y][0] = gameState[x][y][1] = false;
			}
		}
		
		// create and start our animation thread
		Thread t = new Thread(this);
		t.start();
		
		// set initialised to true
		initialised = true;
	}
	

	// thread's entry point
	public void run() 
	{
		while(1==1)
		{
			// 1: sleep for 1/5 sec
			try{
				Thread.sleep(200);
			}
			
			catch(InterruptedException e){}
			
			// 2: animate game objects
			if(isPlaying) {
				
				// call the method to step through the game applying the rules
				stepThroughGame();
				
			}
			
			// 3: force an application repaint
			this.repaint();
		}
	}

	private void stepThroughGame() 
	{
		int front = gameStateFrontBuffer;
		int back = (front + 1) % 2;
		
		for(int x = 0; x < 40; x++)
		{
			for(int y = 0; y < 40; y++)
			{
				int liveNeighbours = 0;
				
				for(int xx = -1; xx <= 1; xx++)
				{
					for(int yy = -1; yy <= 1; yy++)
					{
						if(xx != 0 || yy != 0)
						{
							int xxx = x+xx;
							
							if(xxx < 0)
								
								xxx = 39;
							
							else if(xxx > 39)
								
								xxx = 0;
							
							int yyy = y + yy;
							
							if(yyy < 0)
								
								yyy = 39;
							
							else if (yyy > 39)
								
								yyy = 0;
							
							if(gameState[xxx][yyy][front])
								liveNeighbours++;
						}
					}
				}
				
				if(gameState[x][y][front])
				{
					if(liveNeighbours < 2) {
						gameState[x][y][back] =false;
					}
					else if(liveNeighbours < 4) {
						gameState[x][y][back] =true;
					}
					else {
						gameState[x][y][back] =false;
					}
				}
				else
				{
					if(liveNeighbours == 3) {
						gameState[x][y][back] =true;
					}
					else {
						gameState[x][y][back] =false;
					}
				}
			}
		}
		gameStateFrontBuffer = back;
	}
	
	private void randomiseGameState()
	{
		for(int x = 0; x < 40; x++)
		{
			for(int y = 0; y < 40; y++)
			{
				gameState[x][y][gameStateFrontBuffer] = (Math.random()< 0.25);
			}
		}
	}

	// mouse events which must be implemented for MouseListener
	public void mousePressed(MouseEvent e) 
	{
		if(!isPlaying)
		{
			// start button
			int x = e.getX();
			int y = e.getY();
			
			if(x>=15 && x<=85 && y>=40 && y <= 70)
				
			{
			 start = Instant.now();
		    	// CODE HERE        
			 
				isPlaying = true;
				return;
			}
			
			// random button
			if(x>=115 && x<=215 && y>=40 && y <= 70)
			{
				randomiseGameState();
				return;
			}
			if(x>=315 && x<=385 && y>=40 && y <= 70)
			{
				// save data
				for(int a = 0; a < 40; a++)
				{
					for(int b = 0; b < 40; b++)
					{
					if(gameState[a][b][gameStateFrontBuffer])
					{
						arrA[a][b] = "1";
					}
					else{
						arrA[a][b] = "0";
					}
				}
					
				}
				
				try{
					BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
					for(int a = 0; a < 40; a++)
					{
						for(int b = 0; b < 40; b++)
						{
					writer.write(arrA[a][b]);
					}
				}
					
					writer.close();
					System.out.println("Save Complete");
				}
				catch(IOException p){
					
					System.out.println("Error Saving");
					
				}
				
				return;
			}
			
			if(x>=415 && x<=475 && y>=40 && y <= 70)
			{
				// load data
				Scanner s;
				try {
					s = new Scanner(new BufferedReader(new FileReader(filename)));
					 while (s.hasNext())
			            {
			               String str = s.next();
			               
			                myChar = str.toCharArray();
			                
			                int n = 0;
			                
			                for(int a = 0; a < 40; a++)
								{
									for(int b = 0; b < 40; b++)
									{
										if(myChar[n] == '1'){
								            		gameState[a][b][gameStateFrontBuffer] = true;
										}
								            	
								            	else{
								            		gameState[a][b][gameStateFrontBuffer] = false;
								            	}
										n++;
								            }
								}
			               
			            }
					
				// catch any file not found exceptions
				} catch (FileNotFoundException p) {

					p.printStackTrace();
				}
				
				return;
			}
			
		} else {
			
			// stop button
			int x = e.getX();
			int y = e.getY();
			
			if(x >= 15 && x <= 85 && y >= 40 && y <= 70)
				
			{
				isPlaying = false;			 
				finish = Instant.now();
				long timeElapsed = Duration.between(start, finish).toSeconds();
				System.out.println(timeElapsed);
				return;
			}
		}
		
		int x = e.getX()/20;
		int y = e.getY()/20;
		
		gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
		
		this.repaint();
	}


	public void mouseReleased(MouseEvent e) {}
	
	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {}
	
	// mouse events which must be implemented for MouseMotionListener
	
	public void mouseDragged(MouseEvent e) {	
		
		int x = e.getX()/20;
		int y = e.getY()/20;
		
		gameState[x][y][gameStateFrontBuffer] = !gameState[x][y][gameStateFrontBuffer];
		
		this.repaint();
	}

	public void mouseMoved(MouseEvent e) {}
	
	// application's paint method
	
	public void paint(Graphics g)
	{
		if(!initialised)
			return;
		
		g = offscreenBuffer; // draw to offscreen buffer
		
		// draw to offscreen buffer
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 800);
		
		// redraw all game objects
		g.setColor(Color.WHITE);
		for(int x = 0; x < 40; x++)
		{
			for(int y = 0; y < 40; y++)
			{
				if(gameState[x][y][gameStateFrontBuffer])
					g.fillRect(x*20, y*20, 20, 20);
			}
		}
		
		// display the start, random, save and load button
		if(!isPlaying)
		{
			g.setColor(Color.GREEN);
			g.fillRect(15, 40, 70, 30);
			g.fillRect(115, 40, 100, 30);
			g.fillRect(315, 40, 70, 30);
			g.fillRect(415, 40, 70, 30);
			g.setFont(new Font("Times", Font.PLAIN, 24));
			g.setColor(Color.BLACK);
			g.drawString("Start", 22, 62);
			g.drawString("Random", 122, 62);
			g.drawString("Save", 322, 62);
			g.drawString("Load", 422, 62);
		
		// display the stop button
		} else {
			g.setColor(Color.GREEN);
			g.fillRect(15, 40, 70, 30);
			g.setFont(new Font("Times", Font.PLAIN, 24));
			g.setColor(Color.BLACK);
			g.drawString("Stop", 22, 62);
			
		}
		
		// flip the buffers
		strategy.show();
	}
	
	// application entry point
	public static void main(String[] args) 
	{
		GameOfLife w = new GameOfLife();
		
	}

}
