package assingment3;

// imports needed for Conway's game of life
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import java.util.Scanner;

public class ConwaysLife extends JFrame implements Runnable, MouseListener, MouseMotionListener
{
	// member data
	private BufferStrategy strategy;
	private Graphics offscreenBuffer;
	
	// the game board variable
	private boolean gameState[][][]= new boolean[40][40][2];
	private int gameStateFrontBuffer = 0;
	private boolean isPlaying = false;
	private boolean isInitialised= false;
	
	// change the file location for a different user
	private String gameFile = "/Users/martinhanna/Desktop/gameFile.txt";
	private String[][] gameArray = new String[40][40];
	char[] gameChar = new char[1600];
	
	// constructor
	public ConwaysLife()
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
		
		// register the JFrame itself to receive mouse events
		addMouseListener(this);
		addMouseMotionListener(this);
		
		// initialise the game state by looping over the board
		for (x = 0; x < 40; x++)
			
		{
			for (y = 0; y < 40; y++)
				
			{
				// set all the cells to dead on both front and back buffers
				this.gameState[x][y][0] = this.gameState[x][y][1] = false;
			}
		}
		
		// create and start our animation thread
		Thread t = new Thread(this);
		t.start();
		
		// set initialised to true
		this.isInitialised = true;
	}
	
	// method to check if the x and y values are on the board
	public boolean isInBounds(int x, int y) {
		
		// if the x and y values are out of bounds
		if (x < 0 || x > 39 || y < 0 || y > 39) {
			
			// return false to the calling method
			return false;
			
		} else {
			
			// return true to the calling method
			return true;
		}
	}

	// thread's entry point
	public void run() 
	{
		// give welcome message
		JOptionPane.showMessageDialog(null, "Welcome to Conway's Game of Life\n"
				+ "Click/drag cells to set them alive or use the Random button\nPress Start when you are ready to play"
				+ "\nPlease see https://www.conwaylife.com/ for the rules", 
				"Welcome", JOptionPane.INFORMATION_MESSAGE);
		
		// while the game is running
		while (true)
		{
			// 1: sleep for 1/5 second
			try {
				Thread.sleep(200);
			}
			
			catch (InterruptedException e){}
			
			// 2: animate game objects
			if (this.isPlaying) {
				
				// call the method to step through the game applying the rules
				this.stepThroughGame();
				
			}
			
			// 3: force an application repaint
			this.repaint();
		}
	}

	// method to implement the logic of Conway's game of life
	private void stepThroughGame() 
	{
		int frontBuffer = this.gameStateFrontBuffer;
		int backBuffer = (frontBuffer + 1) % 2;
		
		// loop over the entire board
		for (int x = 0; x < 40; x++)
		{
			for (int y = 0; y < 40; y++)
			{
				// set the live neighbours to 0
				int aliveNeighbours = 0;
				
				// count the live neighbours of cell [x][y][0]
				for (int xx = -1; xx <= 1; xx++)
				{
					for (int yy = -1; yy <= 1; yy++)
					{
						// exclude the cell itself
						if (xx != 0 || yy != 0)
						{
							
							int xxx = x+xx;
							
							// if the value is going to be off the board
							if (xxx < 0) {
								
								// put the value back on the other side of the board
								xxx = 39;
							}
							
							// if the value is going to be off the board
							else if (xxx > 39) {
								
								// put the value back on the other side of the board
								xxx = 0;
							}
							
							
							int yyy = y + yy;
							
							// if the value is going to be off the board
							if (yyy < 0) {
								
								// put the value back on the other side of the board
								yyy = 39;
							}
							
							// if the value is going to be off the board
							else if (yyy > 39) {
								
								// put the value back on the other side of the board
								yyy = 0;
							}
							
							// if the current cell is alive
							if (this.gameState[xxx][yyy][frontBuffer]) {
								
								// increment the alive neighbours
								aliveNeighbours++;
							}
						}
					}
				}
				
				// if the current cell is alive in the front buffer
				if (this.gameState[x][y][frontBuffer])
				{
					// if the alive neighbours is less than 2
					if (aliveNeighbours < 2) {
						
						// set the cell to dead on the back buffer
						this.gameState[x][y][backBuffer] = false;
					}
					
					// else if the alive neighbours is less than 4
					else if (aliveNeighbours < 4) {
						
						// set the cell to alive on the back buffer
						this.gameState[x][y][backBuffer] = true;
					}
					
					// otherwise
					else {
						
						// set the cell to dead on the back buffer
						this.gameState[x][y][backBuffer] = false;
					}
					
				}
				
				else
				{
					// if the alive neighbours is equal to 3
					if (aliveNeighbours == 3) {
						
						// set the cell to alive on the back buffer
						this.gameState[x][y][backBuffer] = true;
					}
					else {
						
						// set the cell to dead on the back buffer
						this.gameState[x][y][backBuffer] = false;
					}
				}
			}
		}
		
		// apply the rules to the back buffer
		this.gameStateFrontBuffer = backBuffer;
	}
	
	// method to randomise the board
	private void randomiseGameState()
	{
		// loop over the full board
		for (int x = 0; x < 40; x++)
		{
			for (int y = 0; y < 40; y++)
			{
				// can change the value to have more/less cells dead/alive
				this.gameState[x][y][this.gameStateFrontBuffer] = (Math.random()< 0.25);
				
			}
		}
	}
	
	// method to reset the board back to the initial state
	private void clearGame() {
		
		// loop over the full board
		for (int x = 0; x < 40; x++)
		{
			for (int y = 0; y < 40; y++)
			{
				// set all the cells to dead
				this.gameState[x][y][this.gameStateFrontBuffer] = false;
			}
		}
		
	}

	// mouse events which must be implemented for MouseListener
	public void mousePressed(MouseEvent e) 
	{
		// if the game is not currently playing
		if (!this.isPlaying)
		{
			
			// get the x and y coordinates of the mouse press
			int x = e.getX();
			int y = e.getY();
			
			// if the start button was pressed
			if (x >= 15 && x <= 85 && y >= 40 && y <= 70)
				
			{
				// set is playing to true
				this.isPlaying = true;
				return;
			}
			
			// if the random button was pressed
			if (x >= 115 && x <= 215 && y >= 40 && y <= 70)
			{
				// randomise the board
				this.randomiseGameState();
				return;
			}
			
			// if the save button was pressed
			if (x >= 315 && x <= 385 && y >= 40 && y <= 70)
			{
				
				for (int a = 0; a < 40; a++)
				{
					for (int b = 0; b < 40; b++)
					{
						
					// if the position on the board is true
					if (this.gameState[a][b][this.gameStateFrontBuffer])
					{
						// save as a 1 in the gameArray i.e., an alive cell
						this.gameArray[a][b] = "1";
					}
					else{
						
						// save as a 0 in the gameArray i.e., a dead cell
						this.gameArray[a][b] = "0";
					}
				}
					
				}
				
				// try to write the game array to the gameFile
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(gameFile));
					
					for (int a = 0; a < 40; a++)
						
					{
						for (int b = 0; b < 40; b++)
							
						{
							// over write any array in the file
							writer.write(this.gameArray[a][b]);
					}
				}
					// close the gameFile
					writer.close();
					
					// inform the user that the pattern was saved successfully
					JOptionPane.showMessageDialog(null, "Conway's life pattern saved successfully", "Pattern Saved", JOptionPane.INFORMATION_MESSAGE);
				}
				
				// catch any problems writing to a file
				catch (IOException p){
					
					// inform the user there was an error saving the pattern
					JOptionPane.showMessageDialog(null, "Error saving Conway's life pattern", "Pattern Not Saved", JOptionPane.INFORMATION_MESSAGE);
					
				}
				
				return;
			}
			
			// if the load button was pressed
			if (x >= 415 && x <= 475 && y >= 40 && y <= 70)
			{
				
				// try to open the gameFile for reading
				try {
					Scanner s = new Scanner(new BufferedReader(new FileReader(this.gameFile)));
					
					// while there is a value in the file
					 while (s.hasNext())
						 
			            {
						 
						   // declare local variables needed for reading from the gameFile
			               String str = s.next(); 
			               
			                this.gameChar = str.toCharArray();
			                
			                int n = 0;
			                
			                for (int a = 0; a < 40; a++)
			                	
								{
									for (int b = 0; b < 40; b++)
										
									{
										// read in the alive cells
										if (this.gameChar[n] == '1'){
											
												
								            	this.gameState[a][b][this.gameStateFrontBuffer] = true;
										}
								            	
								         else {
								        	 
								            	// read in the dead cells
								            	this.gameState[a][b][this.gameStateFrontBuffer] = false;
								            		
								            	}
										
										// increment the counter
										n++;
										
								            }
								}
			               
			            }
					
				// catch any errors opening the file
				} catch (FileNotFoundException p) {
					
					p.printStackTrace();
					JOptionPane.showMessageDialog(null, "Error loading Conway's life pattern", "Pattern Not Loaded", JOptionPane.INFORMATION_MESSAGE);
				}
				
				return;
			}
			
			// if the clear button was pressed
			if (x >= 515 && x <= 585 && y >= 40 && y <= 70)
			{
				// set the full board to black
				this.clearGame();
				return;
			}
			
		} else {
			
			// get the x and y coordinates of the mouse press
			int x = e.getX();
			int y = e.getY();
			
			// if the stop button is pressed
			if (x >= 15 && x <= 85 && y >= 40 && y <= 70)
				
			{
				// stop the game
				this.isPlaying = false;
				return;
			}
			
		}
		
		// get the x and y coordinates of the mouse press
		int x = e.getX()/20;
		int y = e.getY()/20;
		
		// if the values of x and y are in the bounds
		if (isInBounds(x, y)) {
			
			this.gameState[x][y][this.gameStateFrontBuffer] = !this.gameState[x][y][this.gameStateFrontBuffer];
		
		// if they are outside the bounds
		} else {
			
			return;
		}
		
		// force the application to repaint
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
		
		// if the game is not currently in progress
		if (!this.isPlaying) {
		
			// if the values of x and y are in the bounds
			if (isInBounds(x, y)) {
				
				// invert the colour of the cell
				this.gameState[x][y][this.gameStateFrontBuffer] = !this.gameState[x][y][this.gameStateFrontBuffer];
				
				// if they are outside the bounds
			} else {
					
				return;
				
				}
				
			// repaint the graphics
			this.repaint();
		
		}
			

	}

	public void mouseMoved(MouseEvent e) {}
	
	// application's paint method
	public void paint(Graphics g)
	{
		// if the game is not initialised then exit
		if (!this.isInitialised) {
			return;
		}
		
		g = this.offscreenBuffer; // draw to off screen buffer
		
		// clear the canvas with a big black rectangle
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 800);
		
		// redraw all game objects
		g.setColor(Color.WHITE);
		for (int x = 0; x < 40; x++)
		{
			for (int y = 0; y < 40; y++)
			{
				if (this.gameState[x][y][this.gameStateFrontBuffer]) {
					g.fillRect(x * 20, y * 20, 20, 20);
				}
			}
		}
		
		// if the game is not running
		if (!this.isPlaying)
		{
			// render the start, random, save and load button rectangles
			g.setColor(Color.GREEN);
			g.fillRect(15, 40, 70, 30);
			g.fillRect(115, 40, 110, 30);
			g.fillRect(315, 40, 70, 30);
			g.fillRect(415, 40, 70, 30);
			g.fillRect(515, 40, 70, 30);
			
			// set the font of the buttons
			g.setFont(new Font("Helvetica", Font.BOLD, 24));
			g.setColor(Color.BLACK);
			
			// write the string
			g.drawString("Start", 22, 62);
			g.drawString("Random", 122, 62);
			g.drawString("Save", 322, 62);
			g.drawString("Load", 422, 62);
			g.drawString("Clear", 522, 62);
			
		// if the game is playing
		} else {
			
			// render the colour of the stop button
			g.setColor(Color.GREEN);
			g.fillRect(15, 40, 70, 30);
			
			// set the font of the stop button
			g.setFont(new Font("Helvetica", Font.BOLD, 24));
			g.setColor(Color.BLACK);
			
			// write the string
			g.drawString("Stop", 22, 62);

	
		}
		
		// flip the buffers
		this.strategy.show();
	}
	
	// application entry point
	public static void main(String[] args) 
	{
		// create new instance of the ConwaysLife object
		ConwaysLife w = new ConwaysLife();
	}

}