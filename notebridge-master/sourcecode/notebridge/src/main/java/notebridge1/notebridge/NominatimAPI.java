package notebridge1.notebridge;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is responsible for fetching information from the Nominatim API.
 */
public class NominatimAPI {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/search?";
    private static final String LIMIT = "&limit=1";

    /**
     * Retrieves the coordinates of the indicated query. If no
     *
     * @param query the query to be searched
     * @return json array containing the result of the search (coordinates of the given query)
     * @throws IOException if exception whilst communicating with the API
     */
    public static JSONArray search(String query) throws IOException {
        String apiUrl = BASE_URL + "q=" + query + "&format=json" + LIMIT;
        URL url = new URL(apiUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        conn.disconnect();

        return new JSONArray(response.toString());
    }
}
