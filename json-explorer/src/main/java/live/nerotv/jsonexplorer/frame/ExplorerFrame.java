package live.nerotv.jsonexplorer.frame;

import jnafilechooser.api.JnaFileChooser;
import live.nerotv.Main;
import live.nerotv.jsonexplorer.APIExplorer;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class ExplorerFrame extends JFrame {

    private JMenuBar toolbar;
    private JTabbedPane tabbedPane;
    private final APIExplorer instance;
    private JPanel home;

    public ExplorerFrame(APIExplorer instance) {
        this.instance = instance;

        getContentPane().setBackground(Color.black);
        setLayout(new BorderLayout());

        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception ignore) {}

        initToolbar();
        initNewTab();
        initContent();

        setTitleColors(Color.black, Color.white);
        super.setTitle("JSON Explorer");
    }

    private void initToolbar() {
        if(toolbar==null) {
            toolbar = new JMenuBar();

            JButton backButton = new JButton("<");
            backButton.addActionListener((e)->{
                Component activeTab = tabbedPane.getSelectedComponent();
                if (activeTab instanceof JsonPanel) {
                    JsonPanel tab = (JsonPanel)activeTab;
                    if(!tab.getSource().equals(tab.getPreviousSource())) {
                        tab.loadText(tab.getPreviousSource());
                    }
                }
            });
            backButton.setBorderPainted(false);
            toolbar.add(backButton);

            JButton forwardButton = new JButton(">");
            forwardButton.addActionListener((e)->{
                Component activeTab = tabbedPane.getSelectedComponent();
                if (activeTab instanceof JsonPanel) {
                    JsonPanel tab = (JsonPanel)activeTab;
                    if(!tab.getSource().equals(tab.getNextSource())) {
                        tab.loadText(tab.getNextSource());
                    }
                }
            });
            forwardButton.setBorderPainted(false);
            toolbar.add(forwardButton);

            toolbar.add(getFileMenu());
            toolbar.add(getWindowMenu());

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            if(this.isUndecorated()) {
                add(toolbar, BorderLayout.NORTH);
            } else {
                setJMenuBar(toolbar);
            }
        }
    }

    private JMenu getFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem openFile = new JMenuItem("Open File...");
        openFile.addActionListener((e) -> openFileChooser());
        fileMenu.add(openFile);

        JMenuItem loadUrl = new JMenuItem("Load URL...");
        loadUrl.addActionListener((e) -> openUrlInput());
        fileMenu.add(loadUrl);

        JMenuItem setXKey = new JMenuItem("Set x-api-key...");
        setXKey.addActionListener((e) -> setXKey());
        fileMenu.add(setXKey);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        return fileMenu;
    }

    public void setXKey() {
        String input = JOptionPane.showInputDialog(this,"Please input the API key","Input key...",JOptionPane.QUESTION_MESSAGE);
        if(input != null && !input.isBlank()) {
            instance.setAPIKey(input);
        } else {
            instance.setAPIKey(null);
        }
        if(instance.useKey()) {
            setTitle("API key set");
        } else {
            super.setTitle("JSON Explorer");
        }
    }

    private JMenu getWindowMenu() {
        JMenu fileMenu = new JMenu("Window");

        JMenuItem newWindow = new JMenuItem("New window");
        newWindow.addActionListener((e) -> instance.open());
        fileMenu.add(newWindow);

        JMenuItem exitItem = new JMenuItem("Close window");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        return fileMenu;
    }

    private void initContent() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.black);
        tabbedPane.addTab("Home",home);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void initNewTab() {
        home = new JPanel(new BorderLayout());
        home.setBackground(Color.decode("#1f1f1f"));

        JPanel homeContent = new JPanel(new GridBagLayout());
        homeContent.setOpaque(false);

        JPanel centerCard = new JPanel(new BorderLayout(20, 0));
        centerCard.setOpaque(false);

        try {
            JLabel imageLabel = new JLabel(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png")))));
            centerCard.add(imageLabel, BorderLayout.WEST);
        } catch (Exception ignore) {}

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        buttonPanel.setOpaque(false);

        JButton openFile = new JButton("View new JSON file...");
        openFile.addActionListener((e)-> openFileChooser());

        JButton openUrl = new JButton("View new JSON url...");
        openUrl.addActionListener((e)-> openUrlInput());

        JButton newWindow = new JButton("New window");
        newWindow.addActionListener((e)-> instance.open());

        JButton setApiKey = new JButton("Set x-api-key...");
        setApiKey.addActionListener((e)-> setXKey());

        JButton exit = new JButton("Exit JSON Explorer");
        exit.addActionListener((e)-> System.exit(0));

        JSeparator separator = new JSeparator();
        JButton sourceCode = new JButton("View source code");
        sourceCode.addActionListener((e)->{
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/nerotvlive/json-explorer"));
            } catch (Exception ex) {
                Main.getLogger().err("Couldn't open link: "+ex.getMessage(), true);
            }
        });

        buttonPanel.add(openFile);
        buttonPanel.add(openUrl);
        buttonPanel.add(newWindow);
        buttonPanel.add(setApiKey);
        buttonPanel.add(separator);
        buttonPanel.add(sourceCode);
        buttonPanel.add(exit);

        centerCard.add(buttonPanel, BorderLayout.CENTER);
        homeContent.add(centerCard);
        homeContent.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.darkGray));

        home.add(homeContent, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(null);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(4,7,4,7));
        JLabel branding = new JLabel("JSON Explorer 2.17.2 by nerotvlive");
        branding.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://nerotv.live"));
                } catch (Exception ex) {
                    Main.getLogger().err("Couldn't open link: "+ex.getMessage(), true);
                }
            }
        });
        bottomPanel.add(branding,BorderLayout.EAST);
        home.add(bottomPanel, BorderLayout.SOUTH);

        JTextField pathInput = new JTextField();
        pathInput.setBackground(Color.decode("#181818"));
        pathInput.setBorder(BorderFactory.createEmptyBorder(3,3,4,3));
        pathInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==10) {
                    String input = pathInput.getText();
                    if(input.toLowerCase().startsWith("http://")||input.toLowerCase().startsWith("https://")||input.toLowerCase().startsWith("file://")) {
                        loadUrl(input);
                    } else {
                        openFile(new File(input));
                    }
                    pathInput.setText("");
                }
            }
        });
        home.add(pathInput, BorderLayout.NORTH);
    }

    public void openUrlInput() {
        String input = JOptionPane.showInputDialog(this,"Please input JSON element url","Input URL...",JOptionPane.QUESTION_MESSAGE);
        if(input != null && !input.isBlank()) {
            try {
                URL validate = URI.create(input).toURL();
                loadUrl(validate.toString());
            } catch (Exception e) {
                Main.getLogger().err("The given url is not valid: "+e.getMessage(),true);
            }
        }
    }

    public void loadUrl(String url) {
        try {
            JsonPanel jsonPanel = new JsonPanel(this);
            jsonPanel.getTextArea().setEditable(false);
            jsonPanel.getTextArea().setCaretColor(null);
            tabbedPane.addTab("Resolving url...", jsonPanel);

            int index = tabbedPane.getTabCount() - 1;
            if(jsonPanel.loadText(url)) {
                tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
                tabbedPane.setSelectedIndex(index);
            } else {
                tabbedPane.remove(index);
            }
        } catch (Exception e) {
            Main.getLogger().err("Couldn't load json url "+url+": "+e.getMessage(),true);
        }
    }

    public void openFileChooser() {
        JnaFileChooser fc = new JnaFileChooser();
        fc.addFilter("JSON file","json");
        fc.setMode(JnaFileChooser.Mode.Files);
        fc.setMultiSelectionEnabled(false);
        if(fc.showOpenDialog(this)) {
            openFile(fc.getSelectedFile());
        }
    }

    public void openFile(File file) {
        try {
            JsonPanel jsonPanel = new JsonPanel(this);
            jsonPanel.getTextArea().setEditable(false);
            jsonPanel.getTextArea().setCaretColor(null);
            tabbedPane.addTab("Resolving file...", jsonPanel);

            int index = tabbedPane.getTabCount() - 1;
            if(jsonPanel.loadText(file.getAbsolutePath())) {
                tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
                tabbedPane.setSelectedIndex(index);
            } else {
                tabbedPane.remove(index);
            }
        } catch (Exception e) {
            Main.getLogger().err("Couldn't open file "+file.getAbsolutePath()+": "+e.getMessage(),true);
        }
    }

    @Override
    public void setTitle(String title) {
        super.setTitle("JSON Explorer (" + title + ")");
    }

    public void setTitlebar(String title, Color background, Color foreground) {
        setTitle(title);
        setTitleColors(background,foreground);
    }

    public void setTitleColors(Color background, Color foreground) {
        setTitleBackground(background);
        setTitleForeground(foreground);
    }

    public void setTitleBackground(Color color) {
        getRootPane().putClientProperty("JRootPane.titleBarBackground", color);

        toolbar.setBackground(color);
        for(Component c : toolbar.getComponents()) {
            c.setBackground(color);
            if(c instanceof JMenu) {
                ((JMenu)c).getPopupMenu().setBackground(color);
                for(Component mi : ((JMenu)c).getMenuComponents()) {
                    mi.setBackground(color);
                }
            }
        }

        setBackground(color);
        getContentPane().setBackground(color);
        getRootPane().setBackground(color);
    }

    public void setTitleForeground(Color color) {
        getRootPane().putClientProperty("JRootPane.titleBarForeground", color);

        toolbar.setForeground(color);
        for(Component c : toolbar.getComponents()) {
            c.setForeground(color);
            if(c instanceof JMenu) {
                ((JMenu)c).getPopupMenu().setForeground(color);
                for(Component mi : ((JMenu)c).getMenuComponents()) {
                    mi.setForeground(color);
                }
            }
        }

        setForeground(color);
        getContentPane().setForeground(color);
        getRootPane().setForeground(color);
    }

    public JMenuBar getToolbar() {
        return toolbar;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void renameTabForPanel(JPanel panel, String newName) {
        int index = tabbedPane.indexOfComponent(panel);
        if (index != -1) {
            tabbedPane.setTitleAt(index, newName);
            Component tabComp = tabbedPane.getTabComponentAt(index);

            if (tabComp != null) {
                tabComp.revalidate();
                tabComp.repaint();
            }

            tabbedPane.revalidate();
            tabbedPane.repaint();
        }
    }

    public APIExplorer getInstance() {
        return instance;
    }
}