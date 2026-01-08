package live.nerotv.jsonexplorer.frame;

import javax.swing.*;
import java.awt.*;

public class ButtonTabComponent extends JPanel {

    public ButtonTabComponent(final JTabbedPane pane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        setOpaque(false);
        JLabel label = new JLabel() {
            public String getText() {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new JButton("x");
        button.setFocusable(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                pane.remove(i);
            }
        });

        add(button);
    }
}