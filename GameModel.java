import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.Random;

public class GameModel extends Observable {

    // Undo manager
    private UndoManager undoManager;

    public GameModel(int fps, int width, int height, int peaks) {
        undoManager = new UndoManager();

        ShipStatus = "IDLE";


        pad = new Rectangle(330, 100, 40, 10);

        ship = new Ship(60, width / 2, 50);

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

        // anonymous class to monitor ship updates
        ship.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setChangedAndNotify();
            }
        });
        Random ranomNum = new Random();

        // initialize terrain x and y points
        double w = 720 / 19;
        int[] ys = new int[22];
        int[] xs = new int[22];
        for (int i = 0; i < 19; i++) {
            xs[i] = (int)(w * i);
            ys[i] = 100 + ranomNum.nextInt(100);
        }
        xs[19] = 700;
        ys[19] = 100 + ranomNum.nextInt(100);
        xs[20] = 700;
        ys[20] = 200;
        xs[21] = 0;
        ys[21] = 200;
        terrain = new Polygon(xs,ys,22);
    }

    public void restart(){
        ship.setFuel(50);
        ship.reset(new Point2d(350,50));
        ShipStatus = "IDLE";
        setChangedAndNotify();
    }

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    Rectangle2D.Double worldBounds;

    // Ship
    // - - - - - - - - - - -

    public Ship ship;

    // Ship Status
    public String ShipStatus;

    // Lauching Pad Location
    // - - - - - - - - - - -
    public Rectangle pad;

    public void setPad(Rectangle nPad){
        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit(){
            // capture variables for closure
            final Rectangle oldPad = pad;
            final Rectangle newPad = nPad;
            public void redo() throws CannotRedoException {
                super.redo();
                pad = newPad;
                setChangedAndNotify();
            }
            public void undo() throws CannotUndoException {
                super.undo();
                pad = oldPad;
                setChangedAndNotify();
            }
        };
        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        // finally, set the value and notify views
        pad = nPad;
        setChanged();
        notifyObservers();
    }

    // Terrain
    // - - - - - - - - - - -
    public Polygon terrain;

    public void setTerrain(Polygon nterrain){
        // create undoable edit
        UndoableEdit undoableEdit = new AbstractUndoableEdit(){
            // capture variables for closure
            final Polygon oldterrain = terrain;
            final Polygon newterrain = nterrain;
            public void redo() throws CannotRedoException {
                super.redo();
                terrain = newterrain;
                setChangedAndNotify();
            }
            public void undo() throws CannotUndoException {
                super.undo();
                terrain = oldterrain;
                setChangedAndNotify();
            }
        };
        // Add this undoable edit to the undo manager
        undoManager.addEdit(undoableEdit);

        // finally, set the value and notify views
        terrain = nterrain;
        setChanged();
        notifyObservers();
    }

    // Observerable
    // - - - - - - - - - - -

    // check the status of ship
    private void checkShip(){
        // check ship status
        if(ship.timer.isRunning()){
            // check hitting the world bound or the terrain
            if(terrain.intersects(ship.getShape()) || !getWorldBounds().contains(ship.getShape())){
                ShipStatus = "CRASH";
                ship.setPaused(true);
            }
            // check landing the pad
            if(pad.intersects(ship.getShape())){
                if(ship.getSafeLandingSpeed() >= ship.getSpeed()){
                    ShipStatus = "LAND";
                }else{
                    ShipStatus = "CRASH";
                }
                ship.setPaused(true);
            }
        }
    }

    // helper function to do both
    public void setChangedAndNotify() {
        checkShip();
        setChanged();
        notifyObservers();
    }

    // undo and redo methods
    // - - - - - - - - - - - - - -

    public void undo() {
        if (canUndo())
            undoManager.undo();
    }

    public void redo() {
        if (canRedo())
            undoManager.redo();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canRedo();
    }

}




