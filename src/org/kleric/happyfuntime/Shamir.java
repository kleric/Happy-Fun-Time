package org.kleric.happyfuntime;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.net.*;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;
import com.google.gdata.data.spreadsheet.*;

public class Shamir {

	public static final String URL = "https://docs.google.com/feeds/download/spreadsheets/Export?key=0AnN-5p9SwIfUdHl4Rl9lRGg5VjRoR1pISVdMLWk5TGc&amp;exportFormat=csv&amp;gid=0";
	public static final String SPREADSHEET_NAME = "Shamir's Key, 15-151";
	private static final BigInteger prime = new BigInteger("5992830235524142758386850633773258681119");

	/** 
     * @author Rahul 
     * @param shares 
     *            Key-Value pairs of data points 
     * @return the secret number 
     */
	private static BigInteger join(HashMap<Integer, BigInteger> shares) {
		BigInteger secret = new BigInteger("0");
		for (int a : shares.keySet()) 
		{
			BigInteger numerator = new BigInteger("1");
			BigInteger denominator = new BigInteger("1");
			for (int b : shares.keySet()) 
			{
				if (a == b)
					continue;
				numerator = numerator.multiply(new BigInteger("-" + b)).mod(prime);
				denominator = denominator.multiply(new BigInteger(Integer.toString(a - b))).mod(prime);
			}
			secret = (prime.add(secret).add(shares.get(a).multiply(numerator)
					.divide(denominator))).mod(prime);
		}
		return secret;
	}
	/**
	 * @author Clark
	 * @param user - Username of google account that has the Spreadsheet
	 * @param pass -password
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	private static HashMap<Integer, BigInteger> getData(String user, String pass) throws IOException, ServiceException
	{
		HashMap<Integer, BigInteger> map = new HashMap<Integer, BigInteger>();
		String applicationName = "Shamir";	
		SpreadsheetService service =
				new SpreadsheetService(applicationName);

		service.setUserCredentials(user, pass);
		// Define the URL to request.  This should never change.
		URL SPREADSHEET_FEED_URL = new URL(
		        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		// Make a request to the API and get all spreadsheets.
		SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
				SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		SpreadsheetEntry spreadsheet = null;// = spreadsheets.get(0);
		for(SpreadsheetEntry entry : spreadsheets)
		{
			if(entry.getTitle().getPlainText().equals(SPREADSHEET_NAME))
				spreadsheet = entry;
		}
		if(spreadsheet == null)
			return null;

		WorksheetFeed worksheetFeed = service.getFeed(
				spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
		List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
		WorksheetEntry worksheet = worksheets.get(0);
		
		// Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = worksheet.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

	    Integer cellBTemp = 0;
	    for (CellEntry cell : cellFeed.getEntries()) 
	    {
	    	if(cell.getTitle().getPlainText().contains("B") && !cell.getTitle().getPlainText().contains("B1"))
	    	{
	    		//This cell is part of Key (X)
	    		cellBTemp = cell.getCell().getNumericValue().intValue();
	    	}
	    	else if(cell.getTitle().getPlainText().contains("C") && !cell.getTitle().getPlainText().contains("C1"))
	    	{
	    		map.put(cellBTemp, new BigInteger(cell.getCell().getInputValue()));
	    		System.out.println(cellBTemp + ", " + cell.getCell().getInputValue());
	    	}
	    }
	    return map;
	}
	
	public static void main(String[] args) throws IOException, ServiceException, URISyntaxException
	{
		String USERNAME = "throwaway7826@gmail.com";
		String PASSWORD = "ThisIsAThrowAway";
		if(args.length == 2)
		{
			USERNAME = args[0];
			PASSWORD = args[1];
		}

	    HashMap<Integer, BigInteger> map = getData(USERNAME, PASSWORD);
	    if(map == null)
	    {
	    	System.err.println("Unable to retrieve the data.");
	    }
	    else if(map.keySet().size() >= 50)
	    {
	    	System.out.println("Proper Number of Keys Available! Beginning Calculation.");
	    	String value = join(map).toString(); 
	        System.out.println(value); 
	        printString(value);
	    }
	    else
	    {
	    	System.out.println("Not enough keys available...");
	    	System.out.println("Still need " + (50 - map.keySet().size()) + " keys");
	    }
	}
	/** 
     * @author Rahul 
     * @param number 
     *            The BigInteger secret number 
     * @return the string answer to the question 
     */
    public static String printString(String number) { 
        String string = ""; 
        for (int a = 0; a < number.length(); a += 2) 
            string += Character.toChars(54 + Integer.parseInt(number.substring( 
                    a, a + 2))/* % 26 + 11//Enable this if you need to */); 
        return string; 
    } 
}
