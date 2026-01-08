package live.nerotv.jsonexplorer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import live.nerotv.Main;
import live.nerotv.jsonexplorer.frame.ExplorerFrame;
import live.nerotv.jsonexplorer.utils.JsonUtility;
import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIExplorer {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private ExplorerFrame frame;
    private String apiKey;

    public APIExplorer(String apiKey) {
        this.apiKey = apiKey;
        open(null);
    }

    public void open(Component loc) {
        SwingUtilities.invokeLater(() -> {
            frame = new ExplorerFrame(this);
            frame.setMinimumSize(new Dimension(720,600));
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(loc);
            frame.setVisible(true);
        });
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @SuppressWarnings("all")
    public String formatJson(String input) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(input).getAsJsonObject();
        return gson.toJson(jsonObject);
    }

    public String resolveXAPIKeyRequest(URL url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("x-api-key", apiKey);
            return JsonUtility.getContent(connection.getInputStream());
        } catch (Exception e) {
            Main.getLogger().err(e.getMessage(),true);
            return e.getMessage();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public boolean useKey() {
        return apiKey != null;
    }
}