import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by Arcombe on 2017-03-21.
 */
public class Disc extends Circle {

    private final boolean red;

    public Disc(boolean red , double diameter){
        super(diameter / 2, red ? Color.RED : Color.YELLOW);
        this.red = red;

        setCenterX(diameter / 2);
        setCenterY(diameter / 2);
    }

    public boolean getRed(){
        return red;
    }
}

