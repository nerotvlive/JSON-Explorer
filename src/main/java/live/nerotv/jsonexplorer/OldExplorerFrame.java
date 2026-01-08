package live.nerotv.jsonexplorer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class OldExplorerFrame extends JFrame {

    private JTextArea inputField;
    private JButton button;
    private JTextArea outputArea;
    private int lastIndex = -1;

    public void initialise(APIExplorer instance) {
        setTitle("JSON-Explorer");
        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResource("/logo.png"))).getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        } catch (Exception ignore) {}

        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        inputField = new JTextArea();
        GridBagConstraints gbcInputField = new GridBagConstraints();
        gbcInputField.gridx = 0;
        gbcInputField.gridy = 0;
        gbcInputField.gridwidth = 1;
        gbcInputField.fill = GridBagConstraints.HORIZONTAL;
        gbcInputField.weightx = 1.0;
        content.add(inputField, gbcInputField);

        button = new JButton("Make request");
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.gridx = 1;
        gbcButton.gridy = 0;
        button.addActionListener(e -> instance.makeOldRequest(inputField.getText()));
        content.add(button, gbcButton);

        outputArea = new JTextArea("No output yet...");
        outputArea.setEditable(false);
        outputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_F) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    showSearchDialog();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    navigateToNext();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(outputArea);
        GridBagConstraints gbcScrollPane = new GridBagConstraints();
        gbcScrollPane.gridx = 0;
        gbcScrollPane.gridy = 1;
        gbcScrollPane.gridwidth = 2;
        gbcScrollPane.fill = GridBagConstraints.BOTH;
        gbcScrollPane.weightx = 1.0;
        gbcScrollPane.weighty = 1.0;
        content.add(scrollPane, gbcScrollPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(content);

        pack();
    }

    private void showSearchDialog() {
        String searchText = JOptionPane.showInputDialog(this, "Search: ");
        if (searchText != null && !searchText.isEmpty()) {
            searchAndNavigate(searchText);
        }
    }

    private void searchAndNavigate(String searchText) {
        int index;
        if (lastIndex == -1) {
            index = outputArea.getText().indexOf(searchText);
        } else {
            index = outputArea.getText().indexOf(searchText, lastIndex + 1);
        }
        if (index != -1) {
            outputArea.setCaretPosition(index);
            outputArea.setSelectionStart(index);
            outputArea.setSelectionEnd(index + searchText.length());
            lastIndex = index;
        } else {
            lastIndex = -1;
            //JOptionPane.showMessageDialog(this, "No more matches found. Restarting from the beginning.", "Search", JOptionPane.INFORMATION_MESSAGE);
            searchAndNavigate(searchText);
        }
    }

    private void navigateToNext() {
        if (lastIndex != -1) {
            String searchText = outputArea.getSelectedText();
            searchAndNavigate(searchText);
        }
    }

    public JTextArea getInputField() {
        return inputField;
    }

    public JButton getButton() {
        return button;
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }
}
