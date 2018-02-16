import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.*;

public class Mario extends Thread
{
	private ArrayList<Integer> dna; 
	private int size;
	private int it;
	private int actionFrequency;
	private int id;

	Mario(int id)
	{
		dna = new ArrayList<Integer>();
		size = 1000;
		it = 0;
		actionFrequency = 10;
		this.id = id;

		for (int i = 0; i < size; i++)
		{
			dna.add(generateKeycode());
		}

	}

	public void mutate()
	{
		// TODO: Implement this
		// 
		// Mutation Factors are 
		//   Actions
		//   Size of Action List
		//   Frequency of Actions
	}

	/**
	 * Randomly Picks a keycode to be inserted in the array
	 * @return Randomly Selected Keycode
	 */
	private int generateKeycode()
	{
		Random rand = new Random();

		int retVal = rand.nextInt(5);

		if (retVal == 0)
		{
			// z
			retVal = KeyEvent.VK_Z;
		}
		else if (retVal == 1)
		{
			// x
			retVal = KeyEvent.VK_X;

		}
		else if (retVal == 2)
		{
			// a
			retVal = KeyEvent.VK_A;
		}
		else if (retVal == 3)
		{
			// s
			retVal = KeyEvent.VK_S;
		}
		else
		{
			// Move
			retVal = KeyEvent.VK_RIGHT;
		}

		return retVal;
	}

	private int getNextAction()
	{
		int retVal = dna.get(it);
		it++;

		if (it >= dna.size())
		{
			it = dna.size()-1;
		}

		return dna.get(it);
	}

	public void doNextAction(Robot r, int length) throws Exception
	{
		try
		{
			int key = getNextAction();

			r.keyPress(key);
			r.delay(length);
			r.keyRelease(key);
		}
		catch (Exception error)
		{
			throw error;
		}
	}

	@Override
	public void run()
	{
		try
		{
			Robot r = new Robot();
			while(true)
			{
				doNextAction(r, 50);
			}
		}
		catch(Exception error)
		{
			error.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		return "It's a me! Mario #" + Integer.toString(id);
	}
}