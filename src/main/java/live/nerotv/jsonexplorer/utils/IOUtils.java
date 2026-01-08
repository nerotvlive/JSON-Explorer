package live.nerotv.jsonexplorer.utils;

import live.nerotv.Main;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    public static String getContent(URL url) {
        try {
            return getContent(catchForbidden(url));
        } catch (Exception e) {
            Main.getLogger().err("Couldn't get content of "+url+": "+e.getMessage());
            return null;
        }
    }

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

    public static InputStream catchForbidden(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        return connection.getInputStream();
    }
}