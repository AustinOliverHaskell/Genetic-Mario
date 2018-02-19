import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class Driver
{
	public static void main(String[] args)
	{
		int populationSize = 5;
		int generations = 5;
		float rate = 0.005f;

		try
		{
			Robot robot = new Robot();

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
				Evaluator e = new Evaluator(list);

				Collections.sort(list);

				System.out.println("Mario #" + list.get(populationSize - 1).getMarioId() + " scored: " + list.get(populationSize - 1).getScore());

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
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}

		System.out.println(" ----- END ----- ");
	}
}