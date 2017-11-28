import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

// the edit toolbar
public class ToolBarView extends JPanel implements Observer {

    JButton undo = new JButton("Undo");
    JButton redo = new JButton("Redo");

    GameModel model;

    public ToolBarView(GameModel model) {
        model.addObserver(this);
        this.model = model;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // prevent buttons from stealing focus
        undo.setFocusable(false);
        redo.setFocusable(false);

        add(undo);
        add(redo);

        undo.setEnabled(false);
        redo.setEnabled(false);

        // add listener to change the model
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.undo();
            }
        });
        redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.redo();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        undo.setEnabled(this.model.canUndo());
        redo.setEnabled(this.model.canRedo());
    }
}
