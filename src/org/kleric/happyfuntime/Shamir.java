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

	private static final BigInteger prime = new BigInteger("5992830235524142758386850633773258681119");

	private static BigInteger join(HashMap<Integer, BigInteger> shares) {
		BigInteger secret = new BigInteger("0");
		for (int a : shares.keySet()) {
			BigInteger numerator = new BigInteger("1");
			BigInteger denominator = new BigInteger("1");
			for (int b : shares.keySet()) {
				if (a == b)
					continue;
				numerator = numerator.multiply(
						new BigInteger("-" + b)).mod(prime);
				denominator = denominator.multiply(
						new BigInteger(Integer.toString(a
								- b))).mod(prime);
			}
			secret = (prime.add(secret).add(shares.get(a).multiply(numerator)
					.divide(denominator))).mod(prime);
		}
		return secret;
	}

	public static final String URL = "https://docs.google.com/feeds/download/spreadsheets/Export?key=0AnN-5p9SwIfUdHl4Rl9lRGg5VjRoR1pISVdMLWk5TGc&amp;exportFormat=csv&amp;gid=0";
	public static final String TEMP_LOCATION = "info.csv";
	public static void main(String[] args) throws IOException, ServiceException, URISyntaxException
	{
		HashMap<Integer, BigInteger> map = new HashMap<Integer, BigInteger>();
		String USERNAME = "throwaway7826@gmail.com";
		String PASSWORD = "ThisIsAThrowAway";
		String applicationName = "Shamir";	
		SpreadsheetService service =
				new SpreadsheetService(applicationName);

		service.setUserCredentials(USERNAME, PASSWORD);
		// Define the URL to request.  This should never change.
		URL SPREADSHEET_FEED_URL = new URL(
		        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
		//URL SPREADSHEET_FEED_URL = new URL(
		//		"https://spreadsheets.google.com/feeds/spreadsheets/" + key + "/private/full");

		// Make a request to the API and get all spreadsheets.
		SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
				SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		SpreadsheetEntry spreadsheet = spreadsheets.get(0);
		System.out.println(spreadsheet.getTitle().getPlainText());
		WorksheetFeed worksheetFeed = service.getFeed(
				spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
		List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
		WorksheetEntry worksheet = worksheets.get(0);
		
		// Fetch the cell feed of the worksheet.
	    URL cellFeedUrl = worksheet.getCellFeedUrl();
	    CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);

	    Integer cellBTemp = 0;
	    int keysRemaining = 50;
	    for (CellEntry cell : cellFeed.getEntries()) {
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
	    	else if(cell.getTitle().getPlainText().contains("D2"))
	    	{
	    		keysRemaining = cell.getCell().getNumericValue().intValue();
	    	}
	    }
	    
	    if(keysRemaining <= 0)
	    {
	    	System.out.println("Proper Number of Keys Available! Beginning Calculation.");
	    	String value = join(map).toString(); 
	        System.out.println(value); 
	        for (int a = 0; a < value.length(); a += 2) 
	            System.out.print(Character.toChars(54 + Integer.parseInt(value 
	                    .substring(a, a + 2)) % 26 + 11)); 
	        // remove the %26+11 if something doesn't work out in the end, it's not 
	        // supposed to be there, but with 5 data points, it didn't work without 
	        // it. 
	    }
	    else
	    {
	    	System.out.println("Not enough keys available...");
	    	System.out.println("Still need " + (keysRemaining) + " keys");
	    }
	}
}
