package live.nerotv.jsonexplorer;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import org.zyneonstudios.apex.bootstrapper.ApexBootstrapper;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static String workingPath = getDefaultPath();

    public static void main(String[] args) {
        initLookAndFeel();
        String url = "https://zyneonstudios.github.io/apex-metadata/json-explorer/bootstrapper-metadata.json";
        File file = new File(workingPath+"/json-explorer.json");
        ApexBootstrapper bootstrapper = new ApexBootstrapper(url,workingPath,file,args,true,true);
        bootstrapper.showFrame();
        bootstrapper.update();
        bootstrapper.hideFrame();
        bootstrapper.launch();
    }

    private static String getDefaultPath() {
        String appData;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            appData = System.getenv("LOCALAPPDATA");
        } else if (os.contains("mac")) {
            appData = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            appData = System.getProperty("user.home") + "/.local/share";
        }
        Path folderPath = Paths.get(appData, "Zyneon/JSON Explorer");
        try {
            Files.createDirectories(folderPath);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return (folderPath + "/").replace("\\", "/");
    }

    private static void initLookAndFeel() {
        try {
            FlatDarkLaf.setup();
            if(System.getProperty("os.name").toLowerCase().contains("mac")) {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            }
        } catch (Exception ignore) {}
    }
}