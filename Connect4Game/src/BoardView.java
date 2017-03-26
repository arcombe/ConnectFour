import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.Optional;


/**
 * Created by Arcombe on 2017-03-20.
 */
public class BoardView {

    private Database db;

    private double TILE_SIZE;
    private int ROWS;
    private int COLUMNS;

    private double xMargin;
    private double yMargin;
    double xSpace;
    double ySpace;
    private  int placedDiscs;

    private String player1Name;
    private String player2Name;
    private int gameID;

    private boolean gameHasWinner;

    private Disc[][] discGrid;
    private Disc lastPlaced;
    private boolean player1;


    private StackPane root;
    private Pane discRoot;
    private Pane dicsTips;
    private StackPane columns;

    private GameMenu gameMenu;

    public BoardView(GameMenu gameMenu, Database db){
        this.gameMenu = gameMenu;
        this.db = db;
        root = new StackPane();
    }

    public Parent createContent(String player1Name, String player2Name, int column, int row) {

        // Sätter spelarnas namn.
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        // Sparar ett nytt spel i DB
        newGameDB();

        // Sätter antal kollumner och rader
        COLUMNS = column;
        ROWS = row;

        // Sätter värden för nytt spel
        gameHasWinner = false;
        player1 = true;
        discGrid = new Disc[COLUMNS][ROWS];
        discRoot = new Pane();
        dicsTips = new Pane();
        placedDiscs = 0;

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

    // Uppdaterar innehållet i fönstret ifall.
    private void setup(){
        // Tar bort tidigare version av fönstret
        root.getChildren().clear();

        // Sätter värdena som används för att bygga fönstret
        TILE_SIZE = Math.min(root.getWidth(), root.getHeight()) / ((Math.max(COLUMNS, ROWS) <= 5 ? 6 : Math.max(COLUMNS, ROWS)) * 1.5);

        xMargin = TILE_SIZE / COLUMNS;
        yMargin = TILE_SIZE / ROWS;
        xSpace = (root.getWidth() - (COLUMNS - 1) * (TILE_SIZE + xMargin) - TILE_SIZE) / 2;
        ySpace = (root.getHeight() - (ROWS - 1) * (TILE_SIZE + yMargin) - TILE_SIZE) / 2;

        // Uppdaterar laggda discs och tipkollumnerna
        updateDiscs();
        makeColumns();

        if(gameHasWinner || placedDiscs == COLUMNS * ROWS) root.getChildren().addAll(discRoot, makePlayField(), endOfGame());
        else root.getChildren().addAll(discRoot, dicsTips, makePlayField(), columns);

    }

    // Skapar spelplanen
    private Shape makePlayField(){

        // Skapar en bakgrund;
        Shape shape = new Rectangle(root.getWidth(), root.getHeight());

        // Skparar rutnätet i planen genom att ta bort cirklar från bakgrunden.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {

                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);

                circle.setTranslateX(j * (TILE_SIZE + xMargin * (j == 0 ? 1 : 1)) + xSpace);
                circle.setTranslateY(i * (TILE_SIZE + yMargin * (i == 0 ? 1 : 1)) + ySpace);

                shape = shape.subtract(shape, circle);
            }
        }

        // Gör bakgrunder blå
        shape.setFill(Color.rgb(44, 151, 222));
        return shape;
    }

    //  För att ge feedback till spelaren var han kan lägga och aggerar på händelser
    private void makeColumns(){

        columns = new StackPane();
        double startPoint = (COLUMNS - 1.0)/ 2;

        // Skappar alla kolumner och bestämmer vad som ska göras vid händelse.
        for (int i = 0; i < COLUMNS; i++) {
            Rectangle rect = new Rectangle(TILE_SIZE, ROWS * (TILE_SIZE + yMargin));

            rect.setTranslateX((i - startPoint) * (TILE_SIZE + xMargin * (i == 0 ? 1 : 1)));
            rect.setFill(Color.TRANSPARENT);

            final int column = i;

            // Om musen är över kolumnen så gör den synlig och visa var han kan lägga en disc.
            rect.setOnMouseEntered(e -> {
                rect.setFill(Color.rgb(200, 200, 200));
                rect.setOpacity(0.4);
                findTips(column);
            });

            // Vid klick och spelet inte är över försöker man lägga en disc i denna kolumn.
            rect.setOnMouseClicked(e -> {
                if (!gameFinished()) {
                    dicsTips.getChildren().clear();
                    placeDisc(new Disc(player1, TILE_SIZE), column);
                }
            });

            // Om musen går ifrån kolumnen så görs den osynlig och tipset tas bort.
            rect.setOnMouseExited(e -> {
                rect.setFill(Color.TRANSPARENT);
                rect.setOpacity(1);
                dicsTips.getChildren().clear();
            });
            columns.getChildren().add(rect);
        }

        //
        GameMenu.OptionLabel exit = new GameMenu.OptionLabel("Exit", root.getWidth() / 8);
        exit.setTranslateY(root.getHeight() / 2 - TILE_SIZE);
        exit.setOnMouseClicked(e -> gameMenu.mainMenu());
        columns.getChildren().add(exit);
    }

    // Visar vems tur det är samt var de kan lägga.
    public void findTips(int column){

        for (int j = ROWS - 1; j >= 0; j--){
            if(!getDisc(column,j).isPresent()){
                Disc disc = new Disc(player1, TILE_SIZE);
                disc.setOpacity(0.8);
                disc.setTranslateX(column * (TILE_SIZE + xMargin) + xSpace);
                disc.setTranslateY(j * (TILE_SIZE + yMargin) + ySpace);
                dicsTips.getChildren().add(disc);
                break;
            }
        }
    }

    /*  Försöker placera en disc i en kolumn, hoppar ur ifall kolumnen är full och avslutar spelet ifall
        någon har vunnit eller spelplanen är full;
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

        // Uppdaterar värden och lägger in disc i fönstret
        player1 = !player1;
        placedDiscs++;
        lastPlaced = disc;
        discGrid[column][row] = disc;
        discRoot.getChildren().add(disc);
        boolean gameOver = gameFinished(column, row);

        // Lägger in move i DB
        addMoveDB(row, column);

        // Animationen för en fallande disc
        disc.setTranslateX(column * (TILE_SIZE + xMargin) + xSpace);
        TranslateTransition animition = new TranslateTransition(javafx.util.Duration.seconds(0.5), disc);
        animition.setToY(row * (TILE_SIZE + yMargin) + ySpace);
        animition.setOnFinished( e -> {
            if(gameOver){
                setup();
            }else{
                if (columns.getChildren().get(column).getOpacity() == 0.4) findTips(column);
            }
        });
        animition.play();

    }

    // Uppdaterar positionen hos alla discs ifall storleken på fönstret ändras.
    private void updateDiscs(){
        discRoot.getChildren().clear();
        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS; j++){
                if(getDisc(i, j).isPresent()){
                    Disc discPlaced = getDisc(i, j).orElse(new Disc(player1, TILE_SIZE));
                    Disc newDisc = new Disc(discPlaced.getRed(), TILE_SIZE);
                    newDisc.setTranslateX(i * (TILE_SIZE + xMargin) + xSpace);
                    newDisc.setTranslateY(j * (TILE_SIZE + yMargin) + ySpace);
                    discRoot.getChildren().add(newDisc);
                }
            }
        }
    }

    // Kollar om spelet är slut men en ny disc har inte lagts.
    private boolean gameFinished(){
        return gameHasWinner || placedDiscs == COLUMNS * ROWS;
    }

    // För att se om spelet är slut när en ny disc har lagts.
    private boolean gameFinished(int column, int row){
        if(gameWon(column, row)){
            gameHasWinner = true;
            updateStatsDB();
            if (lastPlaced.getRed())
                updateGame(player1Name);
            else
                updateGame(player2Name);
            return true;
        }else if (placedDiscs == COLUMNS * ROWS){
            updateStatsDB();
            updateGame("Tied");
            return true;
        } else
            return false;
    }

    // Kollar om det finns 4 i rad runt en given disc.
    private boolean gameWon(int column, int row){
        int chain = 0;
        Disc discPlaced = getDisc(column, row).orElse(new Disc(player1, TILE_SIZE));
        boolean player = discPlaced.getRed();

        // Kollar den vertikala delen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row).orElse(new Disc(!player, TILE_SIZE));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den horisontala delen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column, row + i).orElse(new Disc(!player, TILE_SIZE));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den ena diagonalen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row + i).orElse(new Disc(!player, TILE_SIZE));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Kollar den andra diagonalen
        for (int i = - 3; i <= 3; i++){
            Disc d = getDisc(column + i, row - i).orElse(new Disc(!player, TILE_SIZE));
            if( d.getRed() == player) chain++;
            else chain = 0;
            if(chain == 4) {
                return true;
            }
        }

        // Finns ingen vinnare
        return false;
    }

    // Spelet är slut och om det finns en vinnare så skrivs det ut.
    private VBox endOfGame(){

        VBox vbox = new VBox();
        vbox.alignmentProperty().bind(root.alignmentProperty());
        vbox.setSpacing(root.getHeight() * 0.75);
        HBox hbox = new HBox();
        hbox.alignmentProperty().bind(root.alignmentProperty());
        hbox.setSpacing(TILE_SIZE);

        Label result;
        if (!gameHasWinner) result = new Label("Tied Game");
        else result = new Label("Winner: " + (lastPlaced.getRed() ? player1Name: player2Name));

        result.setFont(new Font("Alegreya Sans SC", root.getWidth() / 12));
        result.setTextFill(Color.WHITE);

        GameMenu.OptionLabel rematch = new GameMenu.OptionLabel("Rematch", root.getWidth()/8);
        rematch.setOnMouseClicked(e -> createContent(player1Name, player2Name, COLUMNS, ROWS));
        GameMenu.OptionLabel menu = new GameMenu.OptionLabel("Menu", root.getWidth()/8);
        menu.setOnMouseClicked(e -> gameMenu.mainMenu());

        hbox.getChildren().addAll(rematch, menu);

        vbox.getChildren().addAll(result, hbox);

        return vbox;
    }

    private void newGameDB(){
        db.newGame(player1Name, player2Name);
        GameHistory game = db.getLatestGame();
        gameID = game.gameID;
    }

    private void updateGame(String result){
        db.updateGameResult(result, gameID);
    }

    private void addMoveDB(int row, int column){
        db.addMove(gameID, placedDiscs, ROWS - row , column + 1);
    }

    private void updateStatsDB(){
        // För att se om det är guests som spelar och då ska data inte sparas
        boolean isGuest1 = player1Name.equals("Red");
        boolean isGuest2 = player2Name.equals("Yellow");

        /*  Kollar om spelare redan finns, ifall inte skrivs den in i databasen
            som ny spelare. Gäster skrivs inte in
         */
        if(!isGuest1 && db.getPlayer(player1Name) == null) db.newPlayer(player1Name);
        if(!isGuest2 && db.getPlayer(player2Name) == null) db.newPlayer(player2Name);

        /*  Med spelets resultat uppdateras databasen med spelarnas namn, inget görs om spelaren är
            gäst
        */
        if(!gameHasWinner){
            if(!isGuest1) db.updatePlayerStats(player1Name, "gamesTied");
            if(!isGuest2) db.updatePlayerStats(player2Name, "gamesTied");
        } else {
            if (lastPlaced.getRed()) {
                if (!isGuest1) db.updatePlayerStats(player1Name, "gamesWon");
                if (!isGuest2) db.updatePlayerStats(player2Name, "gamesLost");
            } else {
                if (!isGuest1) db.updatePlayerStats(player1Name, "gamesLost");
                if (!isGuest2) db.updatePlayerStats(player2Name, "gamesWon");
            }
        }
    }

    // Skickar tillbaka en disc om det finns annars skickar den tillbaka en optional.
    private Optional<Disc> getDisc(int column, int row){
        if ( column < 0 || column >= COLUMNS || row < 0 || row >= ROWS) return Optional.empty();
        return Optional.ofNullable(discGrid[column][row]);
    }

}
