import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class GoEuroTest{

	public static void main(String[] args) {
		
		if(args.length == 0) {
			
			System.out.println("Please provide a city name.");
			
			System.exit(1);
		}
		
		String cityName = args[0];
		
		String apiCall = "http://api.goeuro.com/api/v2/position/suggest/en/" + cityName;
		
		String apiResponseBody = httpGet(apiCall);
		
		JSONArray nodes = new JSONArray();
		
		try {
			
			nodes = new JSONArray(apiResponseBody);
			
		} catch (JSONException e) {
			
			System.out.println("Unable to parse response from API " + apiCall + " into JSON: " + apiResponseBody);
			e.printStackTrace();
			System.exit(1);
		}
		
		PrintWriter writer = null;
		
		try {
		
			writer = new PrintWriter(cityName + ".csv", "UTF-8");
		
		} catch (Exception e) {
			
			System.out.println("Unable to open CSV file for writing: " + cityName + ".csv");
			e.printStackTrace();
			System.exit(1);
		}
		
		for(int index = 0; index < nodes.length(); index++) {
			
			String line = "";
			
			try {
				
				JSONObject node = (JSONObject) nodes.get(index);
				
				line += node.get("_id") + ",";
				line += node.get("name") + ",";
				line += node.get("type") + ",";
				
				JSONObject geoPosition= node.getJSONObject("geo_position");
				
				line += geoPosition.get("latitude") + ",";
				line += geoPosition.get("longitude");
				
				writer.println(line);
				
			} catch (JSONException e) {
				
				System.out.println("Unable to parse JSON 'location' object, skipping");
				e.printStackTrace();
			}
		}
		
		writer.close();
	}

	private static String httpGet(String url) {

		URL urlObj;
		
		String responseBody = "";

		try {

			urlObj = new URL(url);

			URLConnection connection = urlObj.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String responseLine;

			while ((responseLine = in.readLine()) != null) {

				responseBody += responseLine;
			}

			in.close();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return responseBody;
	}
}
