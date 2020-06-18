import java.util.*;
public class GetDateTime {

	public static void main(String[] args)
	{
		System.out.println("GITK1");
		 System.out.println("GITHUB");
		 System.out.println("To print date and time");
		int day, month, year;
	      int second, minute, hour;
	      GregorianCalendar date = new GregorianCalendar();
	     
	      day = date.get(Calendar.DAY_OF_MONTH);
	      month = date.get(Calendar.MONTH);
	      year = date.get(Calendar.YEAR);
	     System.out.println("DATE & TIME:");
	      second = date.get(Calendar.SECOND);
	      minute = date.get(Calendar.MINUTE);
	      hour = date.get(Calendar.HOUR);

	      System.out.println("Today is  "+day+"/"+(month+1)+"/"+year);
	      System.out.println("Current time is  "+hour+" : "+minute+" : "+second);

	}

}
