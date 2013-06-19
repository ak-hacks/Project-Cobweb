/**
 * 
 */
package com.ft.hack.cobweb.service;

import com.ft.hack.cobweb.dao.CobwebDAO;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anurag.kapur
 *
 */
public class DBPopulator {

	private static final Logger LOGGER = Logger.getLogger(DBPopulator.class);
	
	public List<String[]> searchCorporateAPI(String query) {
		BufferedReader in = null;
		List<String[]> records = new ArrayList<String[]>();
		
		try{
			URL url = new URL("http://api.opencorporates.com/v0.2/officers/search?q=" + URLEncoder.encode(query, "UTF-8") + "&order=score");
			URLConnection connection = url.openConnection();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer responseBuffer = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			
			Object obj = JSONValue.parse(responseBuffer.toString());
			JSONObject results = (JSONObject)obj;
			JSONObject officersObject = (JSONObject)results.get("results");
			JSONArray officers = (JSONArray)officersObject.get("officers");

			for (Object object : officers) {
				JSONObject officerWrapper = (JSONObject)object;
				JSONObject officer = (JSONObject)officerWrapper.get("officer");
				JSONObject company = (JSONObject)officer.get("company");
				String companyName = (String)company.get("name");

				LOGGER.debug(companyName);
				
				String[] record = {query, "person", companyName, "company"};
				records.add(record);
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return records;
	}

    public List<String[]> constructMockData() {
        List<String[]> records = new ArrayList<String[]>();

        String[] record1 = {"Larry Page", "person", "Google", "company"};
        String[] record2 = {"Sergey Brin", "person", "Google", "company"};
        String[] record3 = {"John Ridding", "person", "Financial Times", "company"};
        String[] record4 = {"Christina Scott", "person", "Financial Times", "company"};
        String[] record5 = {"Lionel Barber", "person", "Financial Times", "company"};
        String[] record6 = {"Larry Page", "person", "Youtube", "company"};
        String[] record7 = {"Nikesh Arora", "person", "T-Mobile", "company"};
        String[] record8 = {"John Legere", "person", "T-Mobile", "company"};
        String[] record9 = {"Nikesh Arora", "person", "Bharti Airtel", "company"};
        String[] record10 = {"Sunil Bharti Mittal", "person", "Bharti Airtel", "company"};
        String[] record11 = {"Rajan Bharti Mittal", "person", "Bharti Airtel", "company"};
        String[] record12 = {"Nikesh Arora", "person", "Google", "company"};
        String[] record13 = {"John Ridding", "person", "Pearson", "company"};
        String[] record14 = {"John Fallon", "person", "Pearson", "company"};

        records.add(record1);
        records.add(record2);
        records.add(record3);
        records.add(record4);
        records.add(record5);
        records.add(record6);
        records.add(record7);
        records.add(record8);
        records.add(record9);
        records.add(record10);
        records.add(record11);
        records.add(record12);
        records.add(record13);
        records.add(record14);

        return records;
    }
	
	public static void main(String[] args) {
		DBPopulator dbPopulator = new DBPopulator();
		
		String[] people = {"Larry Page", "Sergey Brin","Marjorie Scardino","Tim Cook","John Ridding"};
		
		for (String string : people) {
			List<String[]> records = dbPopulator.searchCorporateAPI(string);
			CobwebDAO dao = new CobwebDAO();
			dao.insertRecords(records);
		}
	}
}