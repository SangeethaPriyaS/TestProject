import java.util.Scanner;
public class MultiplicationTable
{

	public static void main(String[] args) 
	{
		int a;
		
	    System.out.println("Enter range of numbers to print their multiplication tables .. ");

	    Scanner in = new Scanner(System.in);

	    a = in.nextInt();
	   
	    System.out.println("###Multiplication table of "+a+"##");

	    for (int i = 1; i <= 10; i++)
	    {
	    	System.out.println(a+"*"+i+" = "+(a*i));
	    }
	   
	}

}
