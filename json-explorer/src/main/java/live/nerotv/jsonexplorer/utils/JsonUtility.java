package live.nerotv.jsonexplorer.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import live.nerotv.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class JsonUtility {

    public static String getContent(InputStream remote) {
        final StringBuilder sb = new StringBuilder();
        try(InputStream stream = new BufferedInputStream(remote)) {
            final ReadableByteChannel rbc = Channels.newChannel(stream);
            final Reader enclosedReader = Channels.newReader(rbc, StandardCharsets.UTF_8.newDecoder(), -1);
            final BufferedReader reader = new BufferedReader(enclosedReader);
            int character;
            while ((character = reader.read()) != -1) sb.append((char)character);
            reader.close();
            enclosedReader.close();
            rbc.close();
        } catch (Exception e) {
            Main.getLogger().err("Couldn't get content of "+remote+": "+e.getMessage());
            return null;
        }
        return sb.toString();
    }

    public static String getFromURL(String urlString) {
        try {
            URL url = new URI(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getFromFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            return gson.fromJson(reader, JsonObject.class).toString();
        } catch (Exception e) {
            return null;
        }
    }
}