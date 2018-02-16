import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Evaluator
{
	private Robot robot;
	private Rectangle rect;
	private GameState game;


	Evaluator(ArrayList<Mario> list)
	{

		try
		{
			robot = new Robot();
			int it = 0;

			// Select the window at the current mouse position
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

			game = new GameState();

			game.start();

			list.get(0).start();

			while(true)
			{
				if(GameState.blackScreen)
				{
					break;
				}
			}

			list.get(0).stop();

			game.end();

		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}
}

