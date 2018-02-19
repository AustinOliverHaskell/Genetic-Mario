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
			
			game = new GameState();
			game.start();


			for (int i = 0; i < list.size(); i++)
			{
				long time = System.currentTimeMillis();
				System.out.println(list.get(i).toString());

				list.get(i).start();

				while(true)
				{
					if(GameState.winScreen)
					{
						break;
					}
					if(GameState.blackScreen)
					{
						break;
					}
				}

				list.get(i).continueActions = false;
				list.get(i).join();
				list.get(i).continueActions = true;
				list.get(i).reset();

				if (GameState.blackScreen)
				{
					time = System.currentTimeMillis() - time;
					time = time / 1000;
					System.out.println(" ------------------------------------------- ");
					System.out.println("Mario #" + list.get(i).getMarioId() + " Killed after " + time + "s");

					if (time > 250)
					{
						time = 0;
					}
					else if (time > 150)
					{
						time = time / 2;
					}

					list.get(i).setScore(time);

					// Small delay to check for game over
					Thread.sleep(500);

					GameState.updateValues = false;

					System.out.println("Disabled Screen Checking ... ");

					System.out.println("Taking Mario's hands off the keyboard ... ");
					Mario.releaseAllKeys();

					Thread.sleep(3000);

					if (GameState.gameOver)
					{	
						System.out.println("Game Over occurred ... ");
						Thread.sleep(10000);
						System.out.println("Resetting to correct level ... ");

						GameState.navigateToLevel();

						System.out.println("Completed Game Over Reset!");
					}
					else
					{
						System.out.println("Normal End Screen occurred, resetting ... ");
						GameState.enterLevel();
						System.out.println("Reset!");

						// If the last mario made it to the midpoint
						if (game.checkIfReachedMidpoint(false))
						{
							System.out.println("Previous Mario made it to midpoint ... Reseting ... ");
							GameState.resetGame();
							GameState.navigateToLevel();
							System.out.println("Reset!");
						}
					}

					GameState.updateValues = true;
					System.out.println("Enabled Screen Checking ... ");

					Thread.sleep(1500);
				}
				else
				{
					time = System.currentTimeMillis() - time;
					time = time / 1000;
					System.out.println(" ------------------------------------------- ");
					System.out.println("Mario #" + list.get(i).getMarioId() + " won in " + time + "s");

					// x2 Bonus for winning
					list.get(i).setScore(time * 2);

					GameState.updateValues = false;

					System.out.println("Disabled Screen Checking ... ");

					Thread.sleep(3000);	

					System.out.println("Resetting Enviorment ... ");
					GameState.resetGame();
					GameState.navigateToLevel();
					System.out.println("Reset!");

					GameState.updateValues = true;
					System.out.println("Enabled Screen Checking ... ");
				}
			}

			game.stop();
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}
}

