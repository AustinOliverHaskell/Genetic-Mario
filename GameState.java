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
	public static volatile boolean updateValues;
	public static volatile boolean winScreen;

	private boolean endProcess = false;
	private Robot robot;
	private Rectangle rect;

	private int blackScreenLimit = 0;
	private int lowerLimit = 0;
	private int gameOverLimit;
	private int winLimit;
	private int totalPixels;

	// The game over screen uses this exact color, not black
	private final Color gameOverColor = new Color(22, 22, 22);

	private static BufferedImage startScreen;

	GameState()
	{
		updateValues = true;
		gameOver = false;
		blackScreen = false;
		winScreen = false;

		try
		{
			robot = new Robot();

			Point p = MouseInfo.getPointerInfo().getLocation();
			System.out.println("Initial Cursor Position: [" + (int)p.getX() + " , " + (int)p.getY() + "]");

			// Nudged in a bit for human error
			int width = 630;

			// No need to look at the entire screen
			int height = 470;

			totalPixels = width*height;
			blackScreenLimit = (int)(0.98f * (float)totalPixels);
			lowerLimit = (int)(0.94f * (float)totalPixels);
			gameOverLimit = (int)(0.95f * (float)totalPixels);
			winLimit = (int)(0.65f * (float)totalPixels);

			// Rectangle describing the portion of the screen to grab
			rect = new Rectangle((int)p.getX(), (int)p.getY(), width, height);

			startScreen = robot.createScreenCapture(rect);
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

	public void captureStartScreen()
	{
		try
		{
			System.out.println("Capturing new screen ... ");
			startScreen = robot.createScreenCapture(rect);
		}
		catch(Exception error)
		{

		}
	}

	@Override
	public void run()
	{
		System.out.println("GameState thread running ... ");
		while (true)
		{
			if (endProcess)
			{
				break;
			}

			try
			{
				if (updateValues)
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

					winScreen = checkForWin(buffImg, false);

				}
			}
			catch(Exception error)
			{
				error.printStackTrace();
			}
		}
		System.out.println("GameState thread dead ... ");
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

	public boolean checkIfReachedMidpoint(boolean save)
	{
		boolean retVal = false;

		int sharedPixelCount = 0;

		try
		{
			BufferedImage capture = robot.createScreenCapture(rect);

			for (int y = capture.getMinX(); y < capture.getHeight(); y++)
			{
				for (int x = capture.getMinY(); x < capture.getWidth(); x++)
				{
					Color c = new Color(capture.getRGB(x, y));
					Color other = new Color(startScreen.getRGB(x, y));

					if (c.equals(other))
					{
						sharedPixelCount++;
					}
				}
			}

			if (sharedPixelCount <= (int)(0.90f * (float)totalPixels))
			{
				System.out.println("Opened on midpoint ... Probroably ...");

				System.out.println((int) (((float)sharedPixelCount / (float)totalPixels) * 100.0f)  + "% shared pixels from original and current");

				if (save)
				{
					File outputFile = new File("check.jpg");
					ImageIO.write(capture, "jpg", outputFile);

					outputFile = new File("original.jpg");
					ImageIO.write(startScreen, "jpg", outputFile);
				}

				retVal = true;
			}
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}

		return retVal;
	}

	private boolean checkForWin(BufferedImage buffImg, boolean save)
	{
		boolean retVal = false;

		int winPixelCount = 0;

		try
		{
			// To speed this up the entire imge does not need to be looked at, just the middle
			for (int y = buffImg.getMinX(); y < buffImg.getHeight(); y++)
			{
				for (int x = buffImg.getMinY(); x < buffImg.getWidth(); x++)
				{
					Color c = new Color(buffImg.getRGB(x, y));

					if (c.equals(Color.black))
					{
						winPixelCount++;
					}
				}
			}

			if (winPixelCount > winLimit && !blackScreen)
			{
				retVal = true;
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

	public static void resetGame()
	{
		try
		{
			Robot r = new Robot();
			r.keyPress(KeyEvent.VK_ESCAPE);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_ESCAPE);

			Thread.sleep(1000);

			r.keyPress(KeyEvent.VK_DOWN);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_DOWN);

			Thread.sleep(1000);

			r.keyPress(KeyEvent.VK_DOWN);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_DOWN);

			Thread.sleep(1000);

			r.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_ENTER);

			Thread.sleep(1000);

			r.keyPress(KeyEvent.VK_LEFT);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_LEFT);

			Thread.sleep(1000);

			r.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_ENTER);

			Thread.sleep(7000);
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}

	public static void navigateToLevel()
	{
		try
		{
			Robot r = new Robot();
			for (int k = 0; k < 3; k++)
			{
				r.keyPress(KeyEvent.VK_Z);
				Thread.sleep(100);
				System.out.println("Pressed Z");
				r.keyRelease(KeyEvent.VK_Z);
				Thread.sleep(2000);
			}

			Thread.sleep(2000);

			r.keyPress(KeyEvent.VK_RIGHT);
			Thread.sleep(200);
			System.out.println("Pressed right");
			r.keyRelease(KeyEvent.VK_RIGHT);

			Thread.sleep(1500);

			r.keyPress(KeyEvent.VK_Z);
			Thread.sleep(100);
			System.out.println("Pressed Z");
			r.keyRelease(KeyEvent.VK_Z);

			Thread.sleep(1000);
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}

	public static void enterLevel()
	{
		try
		{
			Robot r = new Robot();		
			Thread.sleep(4000);
			r.keyPress(KeyEvent.VK_Z);
			Thread.sleep(100);
			r.keyRelease(KeyEvent.VK_Z);
			Thread.sleep(5000);
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}


}