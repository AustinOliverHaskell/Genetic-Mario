import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.*;
import java.io.*;

public class Mario extends Thread implements Comparable<Mario>
{
	private ArrayList<Integer> dna; 
	private int size;
	private int it;
	private int actionFrequency;
	private int MarioId;
	private long score;

	private boolean winner;

	public boolean continueActions;

	Mario(int id)
	{
		dna = new ArrayList<Integer>();
		size = 2000;
		it = 0;
		winner = false;
		actionFrequency = 10;
		this.MarioId = id;
		score = 0;

		continueActions = true;

		for (int i = 0; i < size; i++)
		{
			dna.add(generateKeycode());
		}
	}

	Mario(String path)
	{
		try
		{
			Scanner s = new Scanner(new File(path));
			dna = new ArrayList<Integer>();
			while (s.hasNext()){
			    dna.add(Integer.parseInt(s.next()));
			}
			s.close();

			it = 0;
			winner = false;
			actionFrequency = 10;
			this.MarioId = 0;
			score = 0;
			continueActions = true;
		}
		catch (FileNotFoundException error)
		{
			error.printStackTrace();
		}
	}

	Mario(Mario m)
	{
		dna = m.getActionList();
		size = 2000;
		it = 0;
		actionFrequency = 10;
		this.MarioId = m.getMarioId();
		score = m.getScore();
		continueActions = true;
		winner = true;
	}

	Mario(Mario a, Mario b, int id)
	{
		dna = new ArrayList<Integer>();
		size = 2000;
		it = 0;
		actionFrequency = 10;
		this.MarioId = id;
		score = 0;

		winner = false;

		continueActions = true;

		ArrayList<Integer> aList;
		ArrayList<Integer> bList;

		Random rand = new Random();

		if (rand.nextInt(2) == 1)
		{
			aList = a.getActionList();
			bList = b.getActionList();
		}
		else
		{
			aList = b.getActionList();
			bList = a.getActionList();
		}

		int i = 0;

		for (; i < size/4; i++)
		{
			dna.add(aList.get(i));
		}

		for (; i < size/2; i++)
		{
			dna.add(bList.get(i));
		}

		for (; i < size * (3/4); i++)
		{
			dna.add(aList.get(i));
		}

		for (; i < size; i++)
		{
			dna.add(bList.get(i));
		}
	}

	public void mutate(float rate)
	{
		Random rand = new Random();

		int numberOfGeneMutations = (int)(rate * (float)size);

		for (int i = 0; i < numberOfGeneMutations; i++)
		{
			dna.set(rand.nextInt(size), generateKeycode());
		}
	}

	/**
	 * Randomly Picks a keycode to be inserted in the array
	 * @return Randomly Selected Keycode
	 */
	private int generateKeycode()
	{
		Random rand = new Random();

		int retVal = rand.nextInt(6);

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
		else if (retVal == 4)
		{
			retVal = KeyEvent.VK_DOWN;
		}
		else
		{
			// Move
			retVal = KeyEvent.VK_RIGHT;
		}

		return retVal;
	}

	public int getNextAction()
	{
		int retVal = dna.get(it);
		it++;

		if (it >= dna.size())
		{
			it = dna.size()-1;
		}

		return dna.get(it);
	}

	public void flagAsWinner()
	{
		this.winner = true;
	}

	public void save(String path)
	{
		try
		{
			StringBuilder s = new StringBuilder();

			for (int i = 0; i < dna.size(); i++)
			{
				s.append(dna.get(i) + "\n");
			}

			s.append("Score: " + getScore() + "\n");

			FileWriter fileWriter = new FileWriter(path);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(s.toString());

			bufferedWriter.close();
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
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

	public ArrayList<Integer> getActionList()
	{
		return this.dna;
	}

	public int getMarioId()
	{
		return this.MarioId;
	}

	public long getScore()
	{
		return this.score;
	}

	public void setScore(long score)
	{
		this.score = score;
	}

	public void reset()
	{
		it = 0;
	}

	@Override
	public void run()
	{
		try
		{
			Robot r = new Robot();
			while(continueActions)
			{
				doNextAction(r, 70);
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
		return "It's a me! Mario #" + Integer.toString(MarioId);
	}

	public static void releaseAllKeys()
	{
		try
		{
			Robot r = new Robot();

			r.keyRelease(KeyEvent.VK_RIGHT);
			r.keyRelease(KeyEvent.VK_Z);
			r.keyRelease(KeyEvent.VK_X);
			r.keyRelease(KeyEvent.VK_A);
			r.keyRelease(KeyEvent.VK_S);
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}

	public int compareTo(Mario other)
	{
		return (int)(other.getScore() - getScore());
	}
}