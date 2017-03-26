import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

import java.util.Optional;


/**
 * Created by Arcombe on 2017-03-20.
 */
public class BoardView {


    private Database db;
    private int ROWS;
    private int COLUMNS;


    private double discSize;
    private double xMargin;
    private double yMargin;
    double xSpace;
    double ySpace;

    // Antalet lagda discs
    private  int nbrPlacedDiscs;

    // Spelarnas namn, gameID och om det finns en vinnare
    private String player1Name;
    private String player2Name;
    private int gameID;
    private boolean gameHasWinner;

    // Lagda discs
    private Disc[][] placedDiscs;
    // Senast lagd disc
    private Disc lastPlaced;
    // Spelare Reds tur
    private boolean playerRed;

    // Pane root
    private StackPane root;
    // Pane discs
    private Pane discRoot;
    // Pane disc Tips
    private Pane discTips;
    // Pane för funktionalitet
    private StackPane columns;

    // Huvudmenyn
    private GameMenu gameMenu;

    /**
     *  Skapar ett objekt med inparametrarna.
     */
    public BoardView(GameMenu gameMenu, Database db){
        this.gameMenu = gameMenu;
        this.db = db;
        root = new StackPane();
    }

    /**
     *  Skapar ett nytt spel med spelarnamnen, antal kolumner och rader.
     *  @return rootPane
     */
    public Parent createContent(String player1Name, String player2Name, int column, int row) {

        // Sätter spelarnas namn.
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        // Sparar ett nytt spel i DB
        newGameDB();

        // Sätter antal kollumner och rader
        COLUMNS = column;
        ROWS = row;

        // Sätter värden för ett nytt spel med spelare Red som börjar
        gameHasWinner = false;
        playerRed = true;
        placedDiscs = new Disc[COLUMNS][ROWS];
        discRoot = new Pane();
        discTips = new Pane();
        columns = new StackPane();
        nbrPlacedDiscs = 0;

        // Sätter upp spelplanen
        setup();

        // Ifall höjden på fönstret ändras uppdateras spelplannen
        root.heightProperty().addListener( e -> {
            setup();
        });

        // ifall längden på fönstret ändras uppdateras spelplanen
        root.widthProperty().addListener( e -> {
            setup();
        });

        return root;
    }

    /**
     *  Räknar ut variablerna med hjälp av fönstrets storlek och sedan bygger spelplanen
     */
    private void setup(){

        // Tar bort tidigare version av layouten
        root.getChildren().clear();

        // Sätter värdena som används för att bygga fönstret
        discSize = Math.min(root.getWidth(), root.getHeight()) / ((Math.max(COLUMNS, ROWS) <= 5 ? 6 : Math.max(COLUMNS, ROWS)) * 1.6);

        xMargin = discSize / COLUMNS;
        yMargin = discSize / ROWS;
        xSpace = (root.getWidth() - (COLUMNS - 1) * (discSize + xMargin) - discSize) / 2;
        ySpace = (root.getHeight() - (ROWS - 1) * (discSize + yMargin) - discSize) / 2;

        // Uppdaterar laggda discs och tipskollumnerna
        updateDiscs();
        makeColumns();

        if(gameHasWinner || nbrPlacedDiscs == COLUMNS * ROWS) root.getChildren().addAll(discRoot, makePlayField(), endOfGame());
        else root.getChildren().addAll(discRoot, discTips, makePlayField(), columns);

    }

    /**
     *  Skapar spelplannen.
     *  @return Shape som är spelplanen
     */
    private Shape makePlayField(){

        // Skapar en bakgrund;
        Shape playField = new Rectangle(root.getWidth(), root.getHeight());

        // Skparar rutnätet i planen genom att ta bort cirklar från bakgrunden.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {

                Circle circle = new Circle(discSize / 2);
                circle.setCenterX(discSize / 2);
                circle.setCenterY(discSize / 2);

                circle.setTranslateX(j * (discSize + xMargin * (j == 0 ? 1 : 1)) + xSpace);
                circle.setTranslateY(i * (discSize + yMargin * (i == 0 ? 1 : 1)) + ySpace);

                playField = playField.subtract(playField, circle);
            }
        }

        // Gör bakgrunder blå
        playField.setFill(Color.rgb(44, 151, 222));
        return playField;
    }

    /**
     *  Skapar kolumner med funktionalitet som lyssnar på händelser för att ge feedback på vad användaren gör.
     */
    private void makeColumns(){

        // Tar bort tidigare version.
        columns.getChildren().clear();

        // Räknar ut startpoint.
        double startPoint = (COLUMNS - 1.0)/ 2;

        // Skappar alla kolumner och bestämmer vad som ska göras vid händelser.
        for (int i = 0; i < COLUMNS; i++) {

            Rectangle rect = new Rectangle(discSize, ROWS * (discSize + yMargin));

            // Sätter position i x-led och gör den osnynlig till att börja med.
            rect.setTranslateX((i - startPoint) * (discSize + xMargin * (i == 0 ? 1 : 1)));
            rect.setFill(Color.TRANSPARENT);

            // För att ha en final int i lamda-funktionen.
            final int column = i;

            // Om musen är över kolumnen så ska den vara synlig och visa om man kan lägga en disc.
            rect.setOnMouseEntered(e -> {
                rect.setFill(Color.rgb(200, 200, 200));
                rect.setOpacity(0.4);
                findTips(column);
            });

            // Vid klick och spelet inte är över försöker man lägga en disc i denna kolumn.
            rect.setOnMouseClicked(e -> {
                if (!gameFinished()) {
                    discTips.getChildren().clear();
                    placeDisc(new Disc(playerRed, discSize), column);
                }
            });

            // Om musen går ifrån kolumnen så görs den osynlig och tipset tas bort.
            rect.setOnMouseExited(e -> {
                rect.setFill(Color.TRANSPARENT);
                rect.setOpacity(1);
                discTips.getChildren().clear();
            });

            // Lägger till den nya kolumnen.
            columns.getChildren().add(rect);
        }

        // Skapar exit för möjligheten att gå tillbaka till mainmenu.
        GameMenu.OptionLabel exit = new GameMenu.OptionLabel("Exit", root.getWidth() / 8);
        exit.setTranslateY(root.getHeight() / 2 - discSize);
        exit.setOnMouseClicked(e -> gameMenu.mainMenu());

        columns.getChildren().add(exit);
    }

    /**
     *  Skapar kolumner med funktionalitet som lyssnar på händelser för att ge feedback på vad användaren gör.
     */
    public void findTips(int column){

        // Börjar nerifrån och kollar om platsen är upptagen på denna raden. Är den inte det så läggs en tillfällig disc till
        // i discTips som visar var man kan lägga.
        for (int j = ROWS - 1; j >= 0; j--){
            if(!getDisc(column,j).isPresent()){
                Disc disc = new Disc(playerRed, discSize);
                disc.setOpacity(0.8);
                disc.setTranslateX(column * (discSize + xMargin) + xSpace);
                disc.setTranslateY(j * (discSize + yMargin) + ySpace);
                discTips.getChildren().add(disc);
                break;
            }
        }
    }

    /**
     *  Försöker placera en disc i kolumnen column. Finns det en plats ledig placeras den och en animation startas
     *  annars hoppar den tillbaka. Ifall spelet är slut efter animationen kallas setup.
     */
    private void placeDisc(Disc disc, int column){

        // Hittar en ledig plats.
        int row = ROWS - 1;
        do {
            if(!getDisc(column, row).isPresent()) break;
            row--;
        } while (row >= 0);

        // Kolumnen är full och vi kan inte placera något.
        if (row < 0) return;

        // Uppdaterar värden och disc läggs till.
        playerRed = !playerRed;
        nbrPlacedDiscs++;
        lastPlaced = disc;
        placedDiscs[column][row] = disc;
        discRoot.getChildren().add(disc);
        boolean gameOver = gameFinished(column, row);

        // Lägger in move i DB
        addMoveDB(row, column);

        // Animationen för en fallande disc
        disc.setTranslateX(column * (discSize + xMargin) + xSpace);
        TranslateTransition animition = new TranslateTransition(javafx.util.Duration.seconds(0.5), disc);
        animition.setToY(row * (discSize + yMargin) + ySpace);
        animition.setOnFinished( e -> {
            if(gameOver){
                // Spelet är slut och vinnaren ska presenteras.
                setup();
            }else{
                // Om musen är kvar över kolumnen hittas visas tips för den andra spelaren.
                if (columns.getChildren().get(column).getOpacity() == 0.4) findTips(column);
            }
        });
        animition.play();

    }

    /**
     *  Uppdaterar alla lagda discs med nya värden.
     */
    private void updateDiscs(){
        discRoot.getChildren().clear();
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++){
                if(getDisc(i, j).isPresent()){
                    Disc discPlaced = getDisc(i, j).orElse(new Disc(playerRed, discSize));
                    Disc newDisc = new Disc(discPlaced.getRed(), discSize);
                    newDisc.setTranslateX(i * (discSize + xMargin) + xSpace);
                    newDisc.setTranslateY(j * (discSize + yMargin) + ySpace);
                    discRoot.getChildren().add(newDisc);
                }
            }
        }
    }


    /**
     *  Kollar om spelet är slut utan att en disc har lagts.
     *  @return true ifall spelet är slut.
     */
    private boolean gameFinished(){
        return gameHasWinner || nbrPlacedDiscs == COLUMNS * ROWS;
    }

    /**
     *  Kollar om spelet är slut när en ny disc på positionen column och row. Uppdaterar databasen ifall spelet
     *  är slut.
     *  @return true ifall spelet är slut.
     */
    private boolean gameFinished(int column, int row){

        if(gameWon(column, row)){
            gameHasWinner = true;
            updateStatsDB();
            return true;
        }else if (nbrPlacedDiscs == COLUMNS * ROWS){
            updateStatsDB();
            return true;
        } else
            return false;
    }

    /**
     *  Kollar om det finns en vinanre runt en disc som ligger på position column och row.
     *  @return true ifall det finns en vinnande rad.
     */
    private boolean gameWon(int column, int row){
        int chain = 0;
        Disc discPlaced = getDisc(column, row).orElse(new Disc(playerRed, discSize));
        boolean player = discPlaced.getRed();

        // För varje kombination kollas om det finns 4 i rad.

        // Kollar den vertikala delen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row).orElse(new Disc(!player, discSize));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den horisontala delen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column, row + i).orElse(new Disc(!player, discSize));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den ena diagonalen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row + i).orElse(new Disc(!player, discSize));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den andra diagonalen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row - i).orElse(new Disc(!player, discSize));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Finns ingen vinnare
        return false;
    }

    /**
     *  Spelet är slut och resultatet presenteras.
     *  @return VBox med vinnaren och alternativ att starta om eller gå tillbaka till startmenu.
     */
    private VBox endOfGame(){

        // Skapar en VBox för att hålla innehållet
        VBox layout = new VBox();
        layout.alignmentProperty().bind(root.alignmentProperty());
        layout.setSpacing(root.getHeight() * 0.72);

        // Skapar en Hbox för att hålla rematch och menu.
        HBox options = new HBox();
        options.alignmentProperty().bind(root.alignmentProperty());
        options.setSpacing(discSize);

        // Skapar en label som presenterar vinnaren eller att det blev lika.
        GameMenu.GameLabel result;
        if (!gameHasWinner) result = new GameMenu.GameLabel("Tied Game", root.getWidth() / 12);
        else result = new GameMenu.GameLabel("Winner: " + (lastPlaced.getRed() ? player1Name: player2Name), root.getWidth() / 12);

        // Skapar rematch och menu för att kunna ta sig vidare.
        GameMenu.OptionLabel rematch = new GameMenu.OptionLabel("Rematch", root.getWidth()/8);
        rematch.setOnMouseClicked(e -> createContent(player1Name, player2Name, COLUMNS, ROWS));
        GameMenu.OptionLabel menu = new GameMenu.OptionLabel("Menu", root.getWidth()/8);
        menu.setOnMouseClicked(e -> gameMenu.mainMenu());

        options.getChildren().addAll(rematch, menu);
        layout.getChildren().addAll(result, options);
        return layout;
    }

    /**
     *  Startar ett nytt spel.
     */
    private void newGameDB(){
        db.newGame(player1Name, player2Name);
        GameHistory game = db.getLatestGame();
        gameID = game.gameID;
    }

    /**
     *  Lägger in ett nytt move i databasen.
     */
    private void addMoveDB(int row, int column){
        db.addMove(gameID, nbrPlacedDiscs, ROWS - row , column + 1);
    }

    /**
     *  Uppdaterar spelets resultat och spelarnas statestik i databasen. Spelar man som gäst så uppdateras ingen
     *  statestik.
     */
    private void updateStatsDB(){
        // För att se om det är guests som spelar och då ska data inte sparas
        boolean isGuest1 = player1Name.equals("Red");
        boolean isGuest2 = player2Name.equals("Yellow");

        // Kollar om spelare redan finns, ifall inte skrivs den in i databasen
        // som ny spelare. Gäster skrivs inte in.
        if(!isGuest1 && db.getPlayer(player1Name) == null) db.newPlayer(player1Name);
        if(!isGuest2 && db.getPlayer(player2Name) == null) db.newPlayer(player2Name);

        // Spelets resultat uppdateras och ifall spelaren inte är en gäst så uppdateras dess statestik.
        if(!gameHasWinner){
            db.updateGameResult("Tied", gameID);
            if(!isGuest1) db.updatePlayerStats(player1Name, "gamesTied");
            if(!isGuest2) db.updatePlayerStats(player2Name, "gamesTied");
        } else {
            if (lastPlaced.getRed()) {
                db.updateGameResult(player1Name, gameID);
                if (!isGuest1) db.updatePlayerStats(player1Name, "gamesWon");
                if (!isGuest2) db.updatePlayerStats(player2Name, "gamesLost");
            } else {
                db.updateGameResult(player2Name, gameID);
                if (!isGuest1) db.updatePlayerStats(player1Name, "gamesLost");
                if (!isGuest2) db.updatePlayerStats(player2Name, "gamesWon");
            }
        }
    }

    /**
     *  Hämtar en disc från placedDiscs.
     *  @return Disc på plats column, row. Finns det ingen eller det är utanför intervallet skickas en Optional
     *  tillbaka.
     */
    private Optional<Disc> getDisc(int column, int row){
        if ( column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) return Optional.empty();
        return Optional.ofNullable(placedDiscs[column][row]);
    }

}
