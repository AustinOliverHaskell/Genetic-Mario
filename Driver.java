import java.awt.*;
import java.util.*;

public class Driver
{
	public static void main(String[] args)
	{

		ArrayList <Mario> list = new ArrayList<Mario>();

		for (int i = 0; i < 5; i++)
		{
			list.add(new Mario(i));
		}


		Evaluator e = new Evaluator(list);
	}
}