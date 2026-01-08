package live.nerotv.jsonexplorer.frame;

import live.nerotv.Main;
import live.nerotv.jsonexplorer.APIExplorer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;
import org.zyneonstudios.apex.utilities.json.GsonUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class JsonPanel extends JPanel {

    private final RSyntaxTextArea textArea;
    private final RTextScrollPane scrollPane;
    private final JTextField pathInput;
    private final JPanel searchControls;
    private final JPanel replacePanel;
    private final JTextField searchInput;
    private final JTextField replaceInput;
    private final ExplorerFrame parent;
    private String source = null;

    ArrayList<String> history = new ArrayList<>();
    int currentIndex = -1;

    public JsonPanel(ExplorerFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        JPanel additionalControls = new JPanel(new BorderLayout());

        pathInput = new JTextField();
        pathInput.setBackground(Color.decode("#181818"));
        pathInput.setBorder(BorderFactory.createEmptyBorder(3,3,4,3));
        pathInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pathInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String input = pathInput.getText();
                    if(loadText(input)) {
                        while (history.size() > currentIndex + 1) {
                            history.removeLast();
                        }
                        history.add(input);
                        currentIndex++;
                    }
                } else if (pathInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeSearch();
                }
            }
        });

        searchControls = new JPanel(new BorderLayout());
        searchControls.setBackground(Color.decode("#181818"));
        searchControls.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));

        JPanel searchReplacePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        searchReplacePanel.setBackground(null);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(null);
        searchPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        searchInput = new JTextField();
        searchInput.setBackground(Color.decode("#313131"));
        searchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (searchInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String query = searchInput.getText();
                    if (query.isEmpty()) return;

                    SearchContext context = new SearchContext();
                    context.setSearchFor(query);
                    context.setSearchForward(true);
                    context.setMatchCase(false);

                    SearchResult result = SearchEngine.find(textArea, context);

                    if (!result.wasFound()) {
                        textArea.setCaretPosition(0);
                        SearchEngine.find(textArea, context);
                    }
                } else if (searchInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeSearch();
                }
            }
        });
        searchPanel.add(searchInput, BorderLayout.CENTER);

        replacePanel = new JPanel(new BorderLayout(5, 0));
        replacePanel.setBackground(null);
        replacePanel.add(new JLabel("Replace with: "), BorderLayout.WEST);
        replaceInput = new JTextField();
        replaceInput.setBackground(Color.decode("#313131"));
        replaceInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (replaceInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    textArea.setText(getText().replace(searchInput.getText(), replaceInput.getText()));
                } else if (replaceInput.hasFocus() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeSearch();
                }
            }
        });
        replacePanel.add(replaceInput, BorderLayout.CENTER);

        textArea = new RSyntaxTextArea(20, 60);
        JButton closeSearch = new JButton("Close");
        closeSearch.setBackground(Color.decode("#1f1f1f"));
        closeSearch.addActionListener((_)-> closeSearch());

        searchReplacePanel.add(searchPanel);
        searchReplacePanel.add(replacePanel);
        searchControls.add(searchReplacePanel, BorderLayout.CENTER);
        searchControls.add(closeSearch, BorderLayout.EAST);

        searchControls.setVisible(false);
        additionalControls.add(pathInput, BorderLayout.NORTH);
        add(searchControls, BorderLayout.SOUTH);

        add(additionalControls, BorderLayout.NORTH);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        textArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        textArea.setCodeFoldingEnabled(true);
        textArea.setAntiAliasingEnabled(true);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (textArea.hasFocus() && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    closeSearch();
                }
            }
        });

        try {
            Theme theme = Theme.load(Main.class.getResourceAsStream(
                    "/themes/dark.xml"));
            theme.apply(textArea);
        } catch (IOException ignore) {}

        scrollPane = new RTextScrollPane(textArea);
        scrollPane.getGutter().setBackground(textArea.getBackground());
        scrollPane.getGutter().setLineNumberColor(java.awt.Color.GRAY);

        add(scrollPane, BorderLayout.CENTER);

        initSearch();
    }

    private void closeSearch() {
        SearchContext context = new SearchContext();
        context.setSearchFor("");
        context.setMarkAll(false);
        SearchEngine.markAll(textArea, context);
        textArea.requestFocus();
        searchControls.setVisible(false);
    }

    private void initSearch() {
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textArea.hasFocus()||pathInput.hasFocus()||searchInput.hasFocus()||replaceInput.hasFocus()) {
                    replacePanel.setVisible(false);
                    searchControls.setVisible(!searchControls.isVisible());
                    if(searchControls.isVisible()) {
                        searchInput.requestFocus();
                    } else {
                        closeSearch();
                    }
                }
            }
        };
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control F");
        textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "doSearch");
        textArea.getActionMap().put("doSearch", searchAction);


        Action replaceAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((textArea.hasFocus()||pathInput.hasFocus())&&textArea.isEditable()&&textArea.isEnabled()) {
                    replacePanel.setVisible(true);
                    searchControls.setVisible(!searchControls.isVisible());
                    if(searchControls.isVisible()) {
                        searchInput.requestFocus();
                    } else {
                        closeSearch();
                    }
                }
            }
        };
        KeyStroke keyStrokeR = KeyStroke.getKeyStroke("control R");
        textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStrokeR, "doReplace");
        textArea.getActionMap().put("doReplace", replaceAction);
    }

    public RSyntaxTextArea getTextArea() {
        return textArea;
    }

    public RTextScrollPane getScrollPane() {
        return scrollPane;
    }

    public String getText() {
        return textArea.getText();
    }

    public boolean loadText(String textUrlOrPath) {
        String text = "{\"error\":\"Invalid file source\"}";
        String name = "(!) Invalid";
        try {
            if(textUrlOrPath == null||textUrlOrPath.isEmpty()) {
                throw new IllegalArgumentException("Input text url or path is null or empty");
            } else {
                if(textUrlOrPath.startsWith("file://")||textUrlOrPath.startsWith("http://")||textUrlOrPath.startsWith("https://")) {
                    URL source = URI.create(textUrlOrPath).toURL();
                    String url = source.toString();
                    if(parent.getInstance().useKey()) {
                        text = parent.getInstance().resolveXAPIKeyRequest(source);
                    } else {
                        text = GsonUtility.getFromURL(url);
                    }
                    if(url.contains("/")) {
                        String[] u = url.split("/");
                        name = u[u.length-1];
                    } else {
                        name = url.toLowerCase().replace("http://","0").replace("https://","0").replace("file://","0");
                    }
                } else {
                    File file = new File(textUrlOrPath);
                    if(file.exists() && file.isFile()) {
                        text = GsonUtility.getFromFile(file);
                        name = file.getName();
                    } else {
                        throw new IllegalArgumentException("File does not exist: " + textUrlOrPath);
                    }
                }
            }

            if(history.isEmpty()) {
                while (history.size() > currentIndex + 1) {
                    history.removeLast();
                }
                history.add(textUrlOrPath);
                currentIndex++;
            }

            textArea.setText(APIExplorer.formatJson(text));
            textArea.requestFocus();
            pathInput.setText(textUrlOrPath);
            parent.renameTabForPanel(this,name);
            source = textUrlOrPath;
            return true;
        } catch (Exception e) {
            Main.getLogger().err("Failed to parse JSON text: " + e.getMessage(),true);
            return false;
        }
    }

    public JTextField getPathInputField() {
        return pathInput;
    }

    public String getPreviousSource() {
        if (currentIndex > 0) {
            currentIndex--;
            return history.get(currentIndex);
        }
        return history.getFirst();
    }

    public String getNextSource() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return history.get(currentIndex);
        }
        return history.get(currentIndex);
    }

    public String getSource() {
        return source;
    }

    public ArrayList<String> getHistory() {
        return history;
    }
}