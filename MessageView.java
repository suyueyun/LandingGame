import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class MessageView extends JPanel implements Observer {

    // status messages for game
    JLabel fuel = new JLabel("fuel");
    JLabel speed = new JLabel("speed");
    JLabel message = new JLabel("(Paused)");
    private GameModel model;

    public MessageView(GameModel model) {
        model.addObserver(this);
        this.model = model;
        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        fuel.setText("fuel: " + Integer.toString((int)model.ship.getFuel()));
        speed.setText("speed: " + String.format("%.2f", model.ship.getSpeed()));
        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        fuel.setText("fuel: " + Integer.toString((int)model.ship.getFuel()));
        speed.setText("speed: " + String.format("%.2f", model.ship.getSpeed()));
        if(model.ship.timer.isRunning()){
            message.setText("");
        }else{
            message.setText("(Paused)");
        }
        //  fuel label turns red if fuel level <+ 10
        if(model.ship.getFuel() <= 10){
            fuel.setForeground(Color.red);
        }else{
            fuel.setForeground(Color.white);
        }

        // speed label turns green if speed is safe to land
        if(model.ship.getSafeLandingSpeed() >= model.ship.getSpeed()){
            speed.setForeground(Color.green);
        }else {
            speed.setForeground(Color.white);
        }

        // check ship status
        if(model.ShipStatus == "CRASH"){
            message.setText("CRASH");
        }else if (model.ShipStatus == "LAND"){
            message.setText("LANDED!");
        }
    }
}