package org.kleric.happyfuntime; 
  
import java.io.IOException; 
import java.math.BigInteger; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Random; 
import java.util.Scanner; 
import java.net.*; 
  
import com.google.gdata.client.spreadsheet.SpreadsheetService; 
import com.google.gdata.data.spreadsheet.SpreadsheetEntry; 
import com.google.gdata.data.spreadsheet.SpreadsheetFeed; 
import com.google.gdata.util.ServiceException; 
import com.google.gdata.data.spreadsheet.*; 

public class ShamirSolver {
	public static final String URL = "https://docs.google.com/feeds/download/spreadsheets/Export?key=0AnN-5p9SwIfUdHl4Rl9lRGg5VjRoR1pISVdMLWk5TGc&amp;exportFormat=csv&amp;gid=0"; 
    public static final String SPREADSHEET_NAME = "Shamir's Key, 15-151"; 
    // The minimum number of keys 
    private static final int requiredNumber = 50; 
    private static final BigInteger prime = new BigInteger( 
            "5992830235524142758386850633773258681119"); 
	/** 
     * @author Clark 
     * @param user 
     *            - Username of google account that has the Spreadsheet 
     * @param pass 
     *            -password 
     * @return 
     * @throws IOException 
     * @throws ServiceException 
     */
    private static HashMap<Integer, BigInteger> getData(String user, String pass) 
            throws IOException, ServiceException { 
        HashMap<Integer, BigInteger> map = new HashMap<Integer, BigInteger>(); 
        String applicationName = "Shamir"; 
        SpreadsheetService service = new SpreadsheetService(applicationName); 
  
        service.setUserCredentials(user, pass); 
        // Define the URL to request. This should never change. 
        URL SPREADSHEET_FEED_URL = new URL( 
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full"); 
        // Make a request to the API and get all spreadsheets. 
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, 
                SpreadsheetFeed.class); 
        List<SpreadsheetEntry> spreadsheets = feed.getEntries(); 
  
        SpreadsheetEntry spreadsheet = null;// = spreadsheets.get(0); 
        for (SpreadsheetEntry entry : spreadsheets) { 
            if (entry.getTitle().getPlainText().equals(SPREADSHEET_NAME)) 
                spreadsheet = entry; 
        } 
        if (spreadsheet == null) 
            return null; 
  
        WorksheetFeed worksheetFeed = service.getFeed( 
                spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class); 
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries(); 
        WorksheetEntry worksheet = worksheets.get(0); 
  
        // Fetch the cell feed of the worksheet. 
        URL cellFeedUrl = worksheet.getCellFeedUrl(); 
        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class); 
  
        Integer cellBTemp = 0; 
        for (CellEntry cell : cellFeed.getEntries()) { 
            if (cell.getTitle().getPlainText().contains("B") 
                    && !cell.getTitle().getPlainText().equals("B1")) { 
                // This cell is part of Key (X) 
                cellBTemp = cell.getCell().getNumericValue().intValue(); 
            } else if (cell.getTitle().getPlainText().contains("C") 
                    && !cell.getTitle().getPlainText().equals("C1")) { 
                map.put(cellBTemp, new BigInteger(cell.getCell() 
                        .getInputValue())); 
                System.out.println(cellBTemp + ", "
                        + cell.getCell().getInputValue()); 
            } 
        } 
        return map; 
    } 
  
    public static void solve(String[] args, boolean force) throws IOException, 
            ServiceException { 
        int requiredNumber = 50; 
        String USERNAME = "throwaway7826@gmail.com"; 
        String PASSWORD = "ThisIsAThrowAway"; 
        if (args.length == 2) { 
            USERNAME = args[0]; 
            PASSWORD = args[1]; 
        } 
  
        HashMap<Integer, BigInteger> map = getData(USERNAME, PASSWORD); 
        if (map == null) { 
            System.err.println("Unable to retrieve the data."); 
        } else if (force || map.keySet().size() >= requiredNumber) { 
            if (!force) 
                System.out.println("Proper Number of Keys"
                        + " Available! Beginning Calculation."); 
            String value = new Shamir(prime).join(map).toString(); 
            System.out.println(value); 
            System.out.println(value.length());
            System.out.println(decode(value)); 
        } else { 
            System.out.println("Not enough keys available..."); 
            System.out.println("Still need "
                    + (requiredNumber - map.keySet().size()) + " keys"); 
        } 
    } 
	public static void main (String [] args) throws IOException, ServiceException
	{
		// WELCOME TO THE PLAYGROUND!!!! 
		System.out.println("Welcome to playground."); 
		Random random = new Random(); 
		Scanner scanner = new Scanner(System.in); 
		while (true) { 
			System.out.print(">"); 
			String in = scanner.nextLine(); 
			if (in.equals("exit")) 
				break; 
			else if (in.matches("decode.+")) 
				System.out.println(decode(in.substring("decode ".length()))); 
			else if (in.matches("encode.+")) 
				System.out.println(encode(in.substring("encode ".length()))); 
			else if (in.matches("prime.+")) 
				System.out.println(BigInteger.probablePrime( 
						Integer.parseInt(in.substring("prime ".length())), 
						random)); 
			else if (in.matches("join.+")) { 
				HashMap<Integer, BigInteger> map = new HashMap<Integer, BigInteger>(); 
				String[] sets = in.substring("join ".length()) 
						.replaceAll("\\s+", "").split(","); 
				BigInteger prime = new BigInteger(sets[0]); 
				for (int a = 1; a < sets.length; a += 2) 
					map.put(Integer.parseInt(sets[a]), new BigInteger( 
							sets[a + 1])); 
				String value = new Shamir(prime).join(map).toString(); 
				System.out.println(value); 
			} else if (in.matches("split.+")) { 
				String[] sets = in.substring("split ".length()) 
						.replaceAll("\\s+", "").split(","); 
				HashMap<Integer, BigInteger> map = new Shamir(new BigInteger(
						sets[0])).split(new BigInteger(sets[1]),
						Integer.parseInt(sets[2]), Integer.parseInt(sets[3]));
				for (int key : map.keySet()) 
					System.out.println(key + ", " + map.get(key)); 
			} else if (in.matches("solve")) 
				solve(args, false); 
			else if (in.matches("fsolve")) 
				solve(args, true); 
		} 
		scanner.close(); 
	}
	/** 
     * @author Rahul 
     * @param number 
     *            The BigInteger secret number 
     * @return the string answer to the question 
     * 
     * Currently all to lower case because that seems to be what the problem says it will be
     */
    public static String decode(String number) { 
        String string = ""; 
        for (int a = 0; a < number.length(); a += 2) 
            string += Character.toLowerCase(Character.toChars(87 + Integer.parseInt(number.substring( 
                    a, a + 2))/* % 26 + 11/*Enable this if you need to*/ )[0]); 

        return string; 
    } 
  
    /** 
     * @author Rahul 
     * @param string 
     *            A string text 
     * @return the secret number 
     */
    public static String encode(String string) { 
        String number = ""; 
        for (int a = 0; a < string.length(); a++) 
            number += (Character.getNumericValue(string.charAt(a)) + 1); 
        return number; 
    } 
}
