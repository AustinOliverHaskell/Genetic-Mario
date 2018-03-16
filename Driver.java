import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class Driver
{
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			int populationSize = 30;
			int generations = 50;
			float rate = 0.005f;

			try
			{
				Robot robot = new Robot();

				GameState game = new GameState();
				game.start();

				System.out.println("Population Size:" + populationSize);
				System.out.println("Running for " + generations + " generations");
				System.out.println("Mutation Rate: " + rate + "%");

				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

				Thread.sleep(500);

				robot.keyPress(KeyEvent.VK_Z);
				Thread.sleep(100);
				robot.keyRelease(KeyEvent.VK_Z);

				Thread.sleep(3000);

				ArrayList <Mario> list = new ArrayList<Mario>();

				for (int i = 0; i < populationSize; i++)
				{
					list.add(new Mario(i));
				}

				for (int i = 0; i < generations; i++)
				{
					Evaluator e = new Evaluator(list, game);

					Collections.sort(list);

					System.out.println(" ----> Generation: " + i + " Complete! <-----");
					System.out.println(" ----> Mario #" + list.get(populationSize - 1).getMarioId() + " scored: " + list.get(populationSize - 1).getScore() + " <---- ");

					list.get(populationSize - 1).save("./saved/generation_" + i + ".txt");

					for (int m = 0; m < populationSize / 2; m++)
					{
						list.set(m, new Mario(list.get(m), list.get(populationSize - 1), populationSize * i + m + populationSize));
						list.get(m).mutate(rate);
					}

					for (int m = populationSize / 2; m < populationSize; m++)
					{
						list.set(m, new Mario(list.get(m)));
					}

					Thread.sleep(5000);
				}

				robot.keyPress(KeyEvent.VK_ESCAPE);
				Thread.sleep(100);
				robot.keyRelease(KeyEvent.VK_ESCAPE);

				game.end();
			}
			catch (Exception error)
			{
				error.printStackTrace();
			}
		}
		else
		{
			try
			{
				Robot robot = new Robot();

				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

				Thread.sleep(500);

				robot.keyPress(KeyEvent.VK_Z);
				Thread.sleep(100);
				robot.keyRelease(KeyEvent.VK_Z);

				Thread.sleep(3000);

				Mario m = new Mario(args[0]);
				GameState game = new GameState();

				game.start();
				m.start();

				while(!game.blackScreen)
				{
					
				}

			}
			catch (Exception error)
			{
				error.printStackTrace();
			}
		}

		System.out.println(" ----- END ----- ");
	}
}