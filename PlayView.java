import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.AffineTransform;

// the actual game view
public class PlayView extends JPanel implements Observer {

    private GameModel model;

    public PlayView(GameModel model) {
        this.model = model;
        model.addObserver(this);
        // needs to be focusable for keylistener
        setFocusable(true);
        // want the background to be black
        setBackground(Color.BLACK);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                //System.out.println(model.ShipStatus); // debug for losing focus
                if(model.ShipStatus == "CRASH" || model.ShipStatus == "LAND"){
                    // restart the game
                    if (e.getKeyChar() == ' ') {
                        model.restart();
                        model.ship.setPaused(false);
                    }

                }else {
                    if (e.getKeyChar() == ' ') {
                        if (model.ship.timer.isRunning()) {
                            model.ship.setPaused(true);
                        } else {
                            model.ship.setPaused(false);
                        }
                    }
                    if (model.ship.timer.isRunning()) {
                        //control the direction of ship
                        char check = e.getKeyChar();
                        if (check == 'a' || check == 'A') {
                            model.ship.thrustLeft();
                        } else if (check == 'w' || check == 'W') {
                            model.ship.thrustUp();
                        } else if (check== 'd' || check == 'D') {
                            model.ship.thrustRight();
                        } else if (check == 's' || check == 'S') {
                            model.ship.thrustDown();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        this.requestFocus();
        repaint();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // scaled by 3 using Transformation
        AffineTransform M = g2.getTransform();
        g2.translate(this.getWidth() / 2,this.getHeight() / 2);
        g2.scale(3,3);
        g2.translate(- model.ship.getPosition().x,- model.ship.getPosition().y);

        // copy EditView's objects to PlayView
        //draw background
        g2.setColor(Color.lightGray);
        g2.fillRect(0,0,700,200);
        // draw terrain
        g2.setColor(Color.darkGray);
        g2.fillPolygon(model.terrain);
        // draw pad
        g2.setColor(Color.red);
        g2.fillRect(model.pad.x,model.pad.y,model.pad.width,model.pad.height);
        // draw the ship
        g2.setColor(Color.blue);
        g2.fill(model.ship.getShape());
        g2.setTransform(M);
    }
}
