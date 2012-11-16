/**
 * 
 */
package com.ft.hack.cobweb.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.ft.hack.cobweb.domain.SearchResult;

/**
 * @author anurag.kapur
 * 
 */
public class FTContentSearchService {

	private static final Logger LOGGER = Logger.getLogger(FTContentSearchService.class);
	
	public List<SearchResult> search(String query1, String query2) {
		FTContentSearchService searchService = new FTContentSearchService();
		String queryString = "{\"queryString\": \""+ query1 + " " + query2 +"\",\"resultContext\" : {\"maxResults\" : \"5\",\"aspects\" : [\"title\",\"location\"]}}";
		String response = searchService.excutePost(
				"http://api.ft.com/content/search/v1?apiKey=718b18d599d32fdfaa7f6619747d403e", queryString);
		Object obj = JSONValue.parse(response);
		List<String> belongs = new ArrayList<String>();
		belongs.add(query1);
		belongs.add(query2);
		
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		
		if(obj.getClass().equals(JSONObject.class)) {
			try {
				JSONObject jsonResponse = (JSONObject)obj;
				JSONArray jsonResults = (JSONArray)jsonResponse.get("results");
				JSONObject jsonResultObject = (JSONObject)jsonResults.get(0);
				JSONArray results = (JSONArray)jsonResultObject.get("results");
				
				if (null != results && results.size() > 0) {
					for (Object object : results) {
						SearchResult searchResult = new SearchResult();
						
						JSONObject result = (JSONObject)object;
						JSONObject titleTemp = (JSONObject)result.get("title");
						String title = (String)titleTemp.get("title");
						JSONObject location = (JSONObject)result.get("location");
						String url = (String)location.get("uri");
						
						searchResult.setTitle(title);
						searchResult.setUrl(url);
						searchResult.setBelongs(belongs);
						
						searchResults.add(searchResult);
					}
				}

			} catch(Exception e) {
				e.printStackTrace();
				LOGGER.error(e);
			}
		}
		return searchResults;
	}
	
	private String excutePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e) {

			e.printStackTrace();
			return null;

		} finally {

			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FTContentSearchService searchService = new FTContentSearchService();
		searchService.search("Google", "Larry Page");
		//System.out.println(response);
	}

}
