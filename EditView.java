import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {

    GameModel model;
    boolean selectPad;
    boolean selectTerrain;
    boolean firstDrag;
    int TerrainIndex;

    public EditView(GameModel model) {
        this.model = model;
        selectPad = false;
        selectTerrain = false;
        firstDrag = true;
        TerrainIndex = -1;

        // Prevent steal key focus
        setFocusable(false);

        // want the background to be black
        setBackground(Color.BLACK);
        model.addObserver(this);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // check if double click, then move the landing pad
                if(e.getClickCount() == 2){
                    if(model.getWorldBounds().contains(e.getX()+20,e.getY()-5)) {
                        model.setPad(new Rectangle(e.getX() - 20,e.getY() - 5,40,10));
                        model.setChangedAndNotify();
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int []xpoints = model.terrain.xpoints;
                int []ypoints = model.terrain.ypoints;
                TerrainIndex = -1;
                selectTerrain = false;
                // check which terrain gets chosen
                for(int i = 0;i < 20; i++){
                   if(Math.pow(xpoints[i] - mouseX,2) + Math.pow(ypoints[i] - mouseY,2) < 225){
                       selectTerrain = true;
                       TerrainIndex = i;
                       break;
                   }
                }
                if(model.pad.contains(mouseX,mouseY)){
                    selectPad = true;
                }else{
                    selectPad = false;
                }
                if(selectPad){
                    selectTerrain = false;
                    TerrainIndex = -1;
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                selectPad = false;
                firstDrag = true;
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle2D boundary = model.getWorldBounds();
                if (selectPad) {
                    if(boundary.contains(e.getX()-20,e.getY()-5,40,10)) {
                        if(!firstDrag) {
                            model.pad.setLocation(e.getX() - 20, e.getY() - 5);
                        }else{
                            firstDrag = false;
                            model.setPad(new Rectangle(e.getX() - 20, e.getY() - 5, 40, 10));
                        }
                    }
                }else{
                    if(selectTerrain){
                        if(TerrainIndex >=0 && boundary.contains(e.getX(),e.getY())) {
                            if(!firstDrag) {
                                model.terrain.ypoints[TerrainIndex] = e.getY();
                            }else{
                                firstDrag = false;
                                int[] newypoints = new int[22];
                                System.arraycopy(model.terrain.ypoints,0,newypoints,0,22);
                                newypoints[TerrainIndex] = e.getY();
                                model.setTerrain(new Polygon(model.terrain.xpoints,newypoints,22));
                            }
                        }
                    }
                }
                model.setChangedAndNotify();
            }
        });

    }
    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    // paint all the display component
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.lightGray);
        g.fillRect(0,0,700,200);
        g.setColor(Color.darkGray);
        g.fillPolygon(model.terrain);
        g.setColor(Color.GRAY);
        for(int i = 0; i < 20; i++){
            g.drawArc(model.terrain.xpoints[i]-15,model.terrain.ypoints[i]-15,30,30,360,360);
        }
        g.setColor(Color.red);
        g.fillRect(model.pad.x,model.pad.y,model.pad.width,model.pad.height);
    }

}
