package live.nerotv.jsonexplorer;

import com.google.gson.*;
import live.nerotv.Main;
import live.nerotv.jsonexplorer.frame.ExplorerFrame;
import live.nerotv.jsonexplorer.utils.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class APIExplorer {

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private OldExplorerFrame old_frame;
    private ExplorerFrame frame;
    private String apiKey;

    public APIExplorer(String apiKey, boolean oldFrame) {
        this.apiKey = apiKey;
        if(oldFrame) {
            openOld();
        } else {
            open();
        }
    }

    public void open() {
        SwingUtilities.invokeLater(() -> {
            frame = new ExplorerFrame(this);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void openOld() {
        SwingUtilities.invokeLater(() -> {
            if(old_frame != null) {
                old_frame.dispose();
                old_frame = null;
            }
            old_frame = new OldExplorerFrame();
            old_frame.initialise(this);
            old_frame.setSize(1000, 700);
            old_frame.setLocationRelativeTo(null);
            old_frame.setVisible(true);
        });
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @SuppressWarnings("all")
    public static String formatJson(String input) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(input).getAsJsonObject();
        return gson.toJson(jsonObject);
    }

    private URI encode(String input) {
        try {
            URI uri = URI.create(input);

            String query = uri.getQuery();
            String path = uri.getPath();
            String scheme = uri.getScheme();
            String host = uri.getHost();
            int port = uri.getPort();
            return new URI(scheme, null, host, port, path, query, null);
        } catch (Exception e) {
            return null;
        }
    }

    public void makeOldRequest(String urlString) {
        old_frame.getButton().setText("Making request...");
        old_frame.getButton().setEnabled(false);
        try {
            old_frame.getOutputArea().setText("Resolving...");
            if(urlString.startsWith("http")) {
                URI uri = encode(urlString);
                URL url = uri.toURL();
                String urlString_ = urlString.toLowerCase().replace("https://", "").replace("http://", "");
                if (urlString_.startsWith("api.curseforge.com")) {
                    old_frame.getOutputArea().setText(formatJson(resolveCurseforgeRequest(url)));
                } else {
                    old_frame.getOutputArea().setText(formatJson(resolveRequest(url)));
                }
            } else {
                String urlString_ = urlString.replace("file:///", "").replace("file://", "");
                old_frame.getOutputArea().setText(resolveLocalRequest(urlString_));
            }
            old_frame.getButton().setText("Make request");
            old_frame.getButton().setEnabled(true);
        } catch (Exception e) {
            Main.getLogger().err("Bad request: "+e.getMessage());
            old_frame.getOutputArea().setText("Please input a valid URL to make a request.");
            old_frame.getButton().setText("Make request");
            old_frame.getButton().setEnabled(true);
        }
    }

    private String resolveRequest(URL url) {
        Main.getLogger().log("Resolving request "+url+"...");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            return IOUtils.getContent(connection.getInputStream());
        } catch (Exception e) {
            String error = "Couldn't resolve request: "+e.getMessage();
            Main.getLogger().err(error);
            return error;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private String resolveLocalRequest(String path) {
        try {
            File file = new File(URLDecoder.decode(path,StandardCharsets.UTF_8));
            Reader reader = new FileReader(file.getAbsolutePath());
            JsonElement json = JsonParser.parseReader(reader);
            return formatJson(json.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveCurseforgeRequest(URL url) {
        Main.getLogger().log("Resolving CurseForge request "+url+"...");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("x-api-key", apiKey);
            return IOUtils.getContent(connection.getInputStream());
        } catch (Exception e) {
            String error = "Couldn't resolve CurseForge request: "+e.getMessage();
            Main.getLogger().err(error);
            return error;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
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
            return IOUtils.getContent(connection.getInputStream());
        } catch (Exception e) {
            Main.getLogger().err(e.getMessage(),true);
            return e.getMessage();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public static Gson getGson() {
        return gson;
    }

    public boolean useKey() {
        return apiKey != null;
    }
}