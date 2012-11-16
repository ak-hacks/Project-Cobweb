/**
 * 
 */
package com.ft.hack.cobweb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.ft.hack.cobweb.dao.CobwebDAO;

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
	
	public static void main(String[] args) {
		DBPopulator dbPopulator = new DBPopulator();
		
		String[] people = {"Larry Page", "Sergey Brin"};
		
		for (String string : people) {
			List<String[]> records = dbPopulator.searchCorporateAPI(string);
			CobwebDAO dao = new CobwebDAO();
			dao.insertRecords(records);
		}
	}
}