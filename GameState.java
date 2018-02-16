import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
 *  The role of this class is to detect which type of screen the game is
 *   currently on. Either in a game over state or a end screen (When the 
 *   current mario dies)
 *   <br />
 *   Extends thread to move the processing of the screencaputes off onto
 *    its own line of execution
 */
public class GameState extends Thread
{
	// Need to be volatile so that the data is synced between threads
	public static volatile boolean gameOver;
	public static volatile boolean blackScreen;

	private boolean endProcess = false;
	private Robot robot;
	private Rectangle rect;

	private int blackScreenLimit = 0;
	private int lowerLimit = 0;
	private int gameOverLimit;
	private int totalPixels;

	// The game over screen uses this exact color, not black
	private final Color gameOverColor = new Color(22, 22, 22);

	GameState()
	{
		gameOver = false;
		blackScreen = false;

		try
		{
			robot = new Robot();

			Point p = MouseInfo.getPointerInfo().getLocation();
			System.out.println("Initial Cursor Position: [" + (int)p.getX() + " , " + (int)p.getY() + "]");

			// Nudged in a bit for human error
			int width = 630;
			int height = 470;

			totalPixels = width*height;
			blackScreenLimit = (int)(0.98f * (float)totalPixels);
			lowerLimit = (int)(0.94f * (float)totalPixels);
			gameOverLimit = (int)(0.95f * (float)totalPixels);

			// Rectangle describing the portion of the screen to grab
			rect = new Rectangle((int)p.getX(), (int)p.getY(), width, height);
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}

	/**
	 * This method signals to the thread that its time to exit
	 */
	public void end()
	{
		endProcess = true;
	}

	@Override
	public void run()
	{
		while (true)
		{
			System.out.println(this.toString());
			if (endProcess)
			{
				break;
			}

			try
			{
				// Capute the area of the screen that the game is currently running in
				//  then convert to greyscale for easier processing
				//  
				//  NOTE: These objects could be make data members of this class
				BufferedImage capture = robot.createScreenCapture(rect);
				BufferedImage buffImg = new BufferedImage(capture.getWidth(), capture.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
				buffImg.getGraphics().drawImage(capture, 0, 0, null);

				blackScreen = endScreen(buffImg, false);

				gameOver = checkForGameOver(buffImg, false);

				if(blackScreen)
				{
					System.out.println("Black Screen is true");
				}

				robot.delay(1000);
			}
			catch(Exception error)
			{
				error.printStackTrace();
			}
		}
	}

	private boolean endScreen(BufferedImage buffImg, boolean save)
	{
		boolean retVal = false;

		int blackPixelCount = 0;

		try
		{
			// To speed this up the entire imge does not need to be looked at, just the middle
			// TODO: Only read half of the image
			for (int y = buffImg.getMinX(); y < buffImg.getHeight(); y++)
			{
				for (int x = buffImg.getMinY(); x < buffImg.getWidth(); x++)
				{
					Color c = new Color(buffImg.getRGB(x, y));
					if (c.equals(Color.black))
					{
						blackPixelCount++;
					}
				}
			}


			if (blackPixelCount == totalPixels)
			{
				System.out.println("Ending Screen Found!");
				retVal = true;
			}

			if (save)
			{
				File outputFile = new File("endScreen.jpg");
				ImageIO.write(buffImg, "jpg", outputFile);
			}

		}
		catch(Exception error)
		{
			error.printStackTrace();
		}

		return retVal;
	}

	private boolean checkForGameOver(BufferedImage buffImg, boolean save)
	{
		boolean retVal = false;

		int gameOverPixelCount = 0;

		try
		{

			// To speed this up the entire imge does not need to be looked at, just the middle
			for (int y = buffImg.getMinX(); y < buffImg.getHeight(); y++)
			{
				for (int x = buffImg.getMinY(); x < buffImg.getWidth(); x++)
				{
					Color c = new Color(buffImg.getRGB(x, y));

					if (c.equals(gameOverColor))
					{
						gameOverPixelCount++;
					}
				}
			}

			if (gameOverPixelCount > gameOverLimit)
			{
				retVal = true;
				System.out.println("Game Over Screen Found!");
			}

			if (save)
			{
				File outputFile = new File("gameOver.jpg");
				ImageIO.write(buffImg, "jpg", outputFile);
			}

		}
		catch(Exception error)
		{
			error.printStackTrace();
		}

		return retVal;
	}

}