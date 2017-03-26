import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Created by Arcombe on 2017-03-21.
 */
public class Disc extends Circle {

    // Håller koll på vilken färg det är.
    private final boolean red;

    public Disc(boolean red , double diameter){
        // Sätter diametern och färgen.
        super(diameter / 2, red ? Color.RED : Color.YELLOW);

        // Sätter värdet på om det är röd eller gul disc.
        this.red = red;

        // Centrerar objektets x och y kordinat.
        setCenterX(diameter / 2);
        setCenterY(diameter / 2);
    }

    /**
     *  Metod för att hämta vilken färg discen är.
     *  @return true ifall det är röd färg.
     */
    public boolean getRed(){
        return red;
    }
}

