
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Arcombe on 2017-03-21.
 */
public class GameMenu extends Application {

    // Databas för att spara spelare och historik
    private Database db;

    // Själva spelet
    private BoardView board;

    // Stage och scene
    private Stage stage;
    private Scene scene;

    // Olika panes
    private StackPane startPane;
    private StackPane newGamePane;
    private GridPane highscorePane;
    private StackPane historyPane;
    private StackPane helpPane;


    /**
     * Körs när man startar applicationen och sätter upp grunden.
     */
    public void start(Stage primaryStage) throws Exception {

        // Skapar ett nytt objekt av Database som hanterar all data med databasen.
        db = new Database();

        // Skapar en connection till själva databasen.
        if (db.openConnection("playerDatabase.db")) {

        } else {
            System.out.println("Kunde inte skapa connection till databasen");
        }

        // Skapar ett objekt av spelet.
        board = new BoardView(this, db);

        // Skapar ny panes som ska användas
        stage = primaryStage;
        startPane = new StackPane();
        newGamePane = new StackPane();
        highscorePane = new GridPane();
        historyPane = new StackPane();
        helpPane = new StackPane();

        // Skapar en ny scene med startmenyn, ger den en titel och sätter min värden.
        scene = new Scene(startPane, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setMinHeight(300);
        stage.setMinWidth(300);

        // Bygger layouten för startsidan
        setupStartPane();

        // Uppdaterar layouten ifall fönstrets storlek ändras.
        startPane.widthProperty().addListener( e -> {
            startPane.getChildren().clear();
            setupStartPane();
        });

        startPane.heightProperty().addListener( e -> {
            startPane.getChildren().clear();
            setupStartPane();
        });

        newGamePane.widthProperty().addListener(e -> {
            newGamePane.getChildren().clear();
            setupNewGame();
        });

        newGamePane.heightProperty().addListener(e -> {
            newGamePane.getChildren().clear();
            setupNewGame();
        });

        highscorePane.widthProperty().addListener( e -> {
            highscorePane.getChildren().clear();
            setupHighscore();
        });

        highscorePane.heightProperty().addListener( e -> {
            highscorePane.getChildren().clear();
            setupHighscore();
        });

        historyPane.widthProperty().addListener( e -> {
            historyPane.getChildren().clear();
            setupHistory();
        });

        historyPane.heightProperty().addListener( e -> {
            historyPane.getChildren().clear();
            setupHistory();
        });

        helpPane.widthProperty().addListener( e -> {
            helpPane.getChildren().clear();
            setupHelp();
        });

        helpPane.heightProperty().addListener( e -> {
            helpPane.getChildren().clear();
            setupHelp();
        });

        // Gör stage vissible
        stage.show();
    }

    /**
     *  Skapar en animation till startsidan
     *  Inparametrar: red avgör färg, xPos vilken position i x-led och dur är duration för animationen
     */
    private void animation(boolean red, double xPos, int dur){

        // Skapar en disc som är sjävla objektet som animeras, sätter dess startposition och lägger till den
        // i startsidan.
        Disc d = new Disc(red, Math.min(startPane.getWidth(),startPane.getHeight()) / 9 );
        d.setTranslateX(xPos);
        d.setTranslateY(- (startPane.getHeight() * 0.6) );
        startPane.getChildren().add(d);

        // Utför själva animationen
        TranslateTransition ani = new TranslateTransition();


        // Sätter duration, vilket objekt som det handlar om, var den ska flyttas samt att den ska loopa.
        ani.setDuration(javafx.util.Duration.seconds(dur));
        ani.setNode(d);
        ani.setToY(startPane.getHeight() * 0.6);
        ani.setCycleCount(100);

        // Startar animationen.
        ani.play();
    }

    /**
     *  Bygger startsidan.
     */
    private void setupStartPane(){

        // Sätter bakgrundens färg.
        startPane.setStyle("-fx-background-color: #2C97DE;");

        // Skapar animationer i bakgrunden bakgrunden.
        animation(true, -startPane.getWidth() / 3, 5);
        animation(false,startPane.getWidth() / 3, 5);
        animation(true, startPane.getWidth() / 5, 3);
        animation(false, -startPane.getWidth() / 5, 3);

        // Beräknar size som ett mått att använda.
        double size = Math.min(startPane.getWidth(), startPane.getHeight()) / 8;

        // En vertikal box som ska innehålla de olika delarna och binder den till startPane.
        VBox layout = new VBox(size / 2);
        layout.alignmentProperty().bind(startPane.alignmentProperty());

        // Skapar Titeln
        GameLabel gameTitle = new GameLabel("Connect Four", size);
        gameTitle.setTextFill(Color.BLACK);

        // Skapar alternativen i startmenyn och vad som ska hända när de klickas på.
        OptionLabel newGame = new OptionLabel("New Game", size);
        newGame.setOnMouseClicked( e -> {
            setupNewGame();
            scene.setRoot(newGamePane);
        });

        OptionLabel highscore = new OptionLabel("Highscore", size);
        highscore.setOnMouseClicked( e -> {
            setupHighscore();
            scene.setRoot(highscorePane);
        });

        OptionLabel history = new OptionLabel("History", size);
        history.setOnMouseClicked( e -> {
            setupHistory();
            scene.setRoot(historyPane);
        });

        OptionLabel help = new OptionLabel("Help", size);
        help.setOnMouseClicked( e -> {
            setupHelp();
            scene.setRoot(helpPane);
        });

        OptionLabel exit = new OptionLabel("Exit", size);
        exit.setOnMouseClicked( e -> System.exit(0));

        // Lägger in altenativen och i VBoxen för att sedan läggas in i startPane
        layout.getChildren().addAll(gameTitle, newGame, highscore, history, help, exit);
        startPane.getChildren().add(layout);
    }

    /**
     *  Bygger New Game sidan.
     */
    private void setupNewGame(){

        // Sätter bakgrundensfärgen.
        newGamePane.setStyle("-fx-background-color: #2C97DE;");

        // Beräknar size som mått.
        double size = Math.min(newGamePane.getWidth(), newGamePane.getHeight()) / 8;

        // En vertikal box som ska innehålla delarna.
        VBox layout = new VBox(size);
        layout.alignmentProperty().bind(newGamePane.alignmentProperty());

        // Skapar en horisontell box för delarna att ange spelare red.
        HBox player1Part = new HBox(10);
        player1Part.alignmentProperty().bind(layout.alignmentProperty());
        GameLabel player1 = new GameLabel("Player Red: ", size / 3);
        TextField player1TextField = new TextField();
        // Promptext gör att man kan start spelet och spela som gäst.
        player1TextField.setPromptText("Red");
        player1Part.getChildren().addAll(player1, player1TextField);

        // Skapar en horisontell box för delarna att ange spelare yellow.
        HBox player2Part = new HBox( 10);
        player2Part.alignmentProperty().bind(layout.alignmentProperty());
        GameLabel player2 = new GameLabel("Player Yellow: ", size / 3);
        TextField player2TextField = new TextField();
        // Promptext gör att man kan start spelet och spela som gäst.
        player2TextField.setPromptText("Yellow");
        player2Part.getChildren().addAll(player2, player2TextField);

        // Skapar en horisontell box för delarna att välja antal kolumner och rader.
        HBox columnsAndRows = new HBox();
        columnsAndRows.setSpacing(10);
        columnsAndRows.alignmentProperty().bind(newGamePane.alignmentProperty());
        GameLabel columns = new GameLabel("Columns", size / 3);
        ComboBox comboColumn = new ComboBox();
        comboColumn.setValue(7);
        comboColumn.getItems().addAll(4,5,6,7,8,9,10);
        GameLabel rows = new GameLabel("Rows", size / 3);
        ComboBox comboRow = new ComboBox();
        comboRow.setValue(6);
        comboRow.getItems().addAll(4,5,6,7,8,9,10);
        columnsAndRows.getChildren().addAll(columns, comboColumn, rows, comboRow);

        // Skapar en horisontell box för att hålla back och start.
        HBox startBackBox = new HBox(size);
        startBackBox.alignmentProperty().bind(layout.alignmentProperty());
        // För att starta spelet, fyller man inte i textfielden så spelar man som gäster.
        OptionLabel start = new OptionLabel("Start Game!", size);
        start.setOnMouseClicked( e -> {
            String player1Name = player1TextField.getText();
            if(player1Name.equals("")) player1Name = player1TextField.getPromptText();
            String player2Name = player2TextField.getText();
            if(player2Name.equals("")) player2Name = player2TextField.getPromptText();
            int column = Integer.parseInt(comboColumn.getValue().toString());
            int row = Integer.parseInt(comboRow.getValue().toString());
            scene.setRoot(board.createContent(player1Name, player2Name, column, row));
        });
        // För att kunna komma tillbaka till startmenyn.
        OptionLabel back = new OptionLabel("Back", size);
        back.setOnMouseClicked( e -> scene.setRoot(startPane));
        startBackBox.getChildren().addAll(back, start);

        // Lägger in delarna i layout och sedan i newGamePane.
        layout.getChildren().addAll(player1Part, player2Part, columnsAndRows, startBackBox);
        newGamePane.getChildren().add(layout);
    }

    /**
     *  Bygger highscoresidan.
     */
    private void setupHighscore(){

        // Sätter bakgrundsfärgen.
        highscorePane.setStyle("-fx-background-color: #2C97DE;");

        // Sätter en size att utgå från.
        double size = Math.min(highscorePane.getWidth(), highscorePane.getHeight()) / 10;

        // Förnyar.
        highscorePane.getChildren().clear();
        highscorePane.setPadding(new Insets(size));
        highscorePane.setHgap(size);
        highscorePane.setVgap(size / 6);

        //Sätter titlarna för varje rad.
        GameLabel name = new GameLabel("Name", size / 3);
        GameLabel gamesWon = new GameLabel("Won", size / 3);
        GameLabel gamesLost = new GameLabel("Lost", size / 3);
        GameLabel gamesTied = new GameLabel("Tied",size / 3);
        GridPane.setConstraints(name, 0,0);
        GridPane.setConstraints(gamesWon, 1, 0);
        GridPane.setConstraints(gamesLost, 2, 0);
        GridPane.setConstraints(gamesTied, 3, 0);
        highscorePane.getChildren().addAll(name, gamesWon, gamesLost, gamesTied);

        // För Skapar lines för varje person som ligger top 10.
        GameLabel[][] lines = new GameLabel[10][4];
        int count = 0;
        for (Player p : db.getTopTenPlayer()){
            lines[count][0] = new GameLabel(p.name, size / 3);
            lines[count][1] = new GameLabel(Integer.toString(p.gamesWon), size/ 3);
            lines[count][2] = new GameLabel(Integer.toString(p.gamesLost), size / 3);
            lines[count][3] = new GameLabel(Integer.toString(p.gamesTied), size / 3);
            GridPane.setConstraints(lines[count][0], 0, count + 1);
            GridPane.setConstraints(lines[count][1], 1, count + 1);
            GridPane.setConstraints(lines[count][2], 2, count + 1);
            GridPane.setConstraints(lines[count][3], 3, count + 1);
            highscorePane.getChildren().addAll(lines[count][0],lines[count][1],lines[count][2],lines[count][3]);
            count++;
        }

        // För att kunna gå tillbaka till startmenu.
        OptionLabel back = new OptionLabel("Back", size);
        back.setOnMouseClicked(e -> scene.setRoot(startPane));
        GridPane.setConstraints(back, 0, 15);
        highscorePane.getChildren().add(back);
    }

    /**
     *  Bygger historysidan.
     */
    private void setupHistory(){

        // Sätter bakgrundsfärgen.
        historyPane.setStyle("-fx-background-color: #2C97DE;");

        // Hämtar de 10 bästa spelarna eller så många som finns.
        List<GameHistory> games = db.getGameHistory();

        // Beräknar size som mått.
        double size = Math.min(historyPane.getWidth(), historyPane.getHeight()) / 16;

        // Skapar VBox som ska innehålla delarna.
        VBox layout = new VBox(size);
        layout.alignmentProperty().bind(historyPane.alignmentProperty());

        // Skapar Hbox som ska innehålla första delen.
        HBox first = new HBox(size);
        first.alignmentProperty().bind(layout.alignmentProperty());
        GameLabel chooseGame = new GameLabel("Choose Game: ", size);

        // Skapar en combobox, hämtar alla tidigare spel som finns och lägger in dem som alternativ.
        ComboBox gamesOptions = new ComboBox();
        gamesOptions.setPrefWidth(historyPane.getWidth() / 2);
        gamesOptions.setMaxWidth(Control.USE_PREF_SIZE);
        for (GameHistory game : games){
            gamesOptions.getItems().add("Game " + game.gameID + " " + game.playerNameRed + " vs " + game.playerNameYellow);
        }

        first.getChildren().addAll(chooseGame, gamesOptions);

        // Skapar en listview som ska presentera de dragen som gjordes i spelet.
        ListView<String> showGame = new ListView<String>();
        showGame.setPrefHeight(historyPane.getHeight() / 2);
        showGame.setPrefWidth(historyPane.getWidth() * 0.7);
        showGame.setMaxHeight(Control.USE_PREF_SIZE);
        showGame.setMaxWidth(Control.USE_PREF_SIZE);

        // Skapar en horisontell box som ska innehålla back och show för att visa ett spel och kunna
        // gå tillbaka till startmenu.
        HBox third = new HBox(size);
        third.alignmentProperty().bind(layout.alignmentProperty());
        OptionLabel back = new OptionLabel("Back", size * 3);
        back.setOnMouseClicked( e -> scene.setRoot(startPane));

        // Vid show hämtas det valda spelet från comboboxen och hämtar detta spelet från databasen
        // för att sen presentera det i listviewn
        OptionLabel show = new OptionLabel("Show", size * 3);
        show.setOnMouseClicked( e -> {
            // Hämtar det valda spelet.
            Object obj = gamesOptions.getValue();
            // Är inget spel valt händer inget, annars så hämtar man alla drag som gjordes i spelet och lägger
            // in dem i listview.
            if (obj != null) {
                String line = obj.toString();
                String[] gameID = line.split(" ");
                GameHistory game = games.get(games.size()-Integer.parseInt(gameID[1]));
                List<Move> moves = db.getMoves(Integer.parseInt(gameID[1]));
                boolean red = true;
                showGame.getItems().clear();
                for (Move move : moves){
                    if (red)
                        showGame.getItems().add("Move " + move.moveID + " " + game.playerNameRed +
                                " placed disc on column " + move.column + ", row " + move.row);
                    else
                        showGame.getItems().add("Move " + move.moveID + " " + game.playerNameYellow +
                                " placed disc on column " + move.column + ", row " + move.row);
                    red = !red;
                }
                // När alla drag har lagts till läggs även hur spelet slutades till.
                if (game.winner == null)
                    showGame.getItems().add("The Game was not finished");
                else if (game.winner.equals("Tied"))
                    showGame.getItems().add("The Game was a tie");
                else
                    showGame.getItems().add("Winner: " + game.winner);
            }
        });

        // Lägger till allt i historyPane
        third.getChildren().addAll(back, show);
        layout.getChildren().addAll(first, showGame, third);
        historyPane.getChildren().addAll(layout);
    }

    /**
     *  Bygger helpsidan.
     */
    private void setupHelp(){

        // Sätter bakgrundsfärgen
        helpPane.setStyle("-fx-background-color: #2C97DE;");

        // Sätter en size att utgåfrån.
        double size = Math.min(helpPane.getWidth(), helpPane.getHeight()) / 32;

        // Sätter en max width för texten
        double maxWidth = helpPane.getWidth() - size * 5;

        // Skapar en vertikal box för att hålla texterna
        VBox layout = new VBox();
        layout.setSpacing(size);
        layout.alignmentProperty().bind(helpPane.alignmentProperty());

        // Skpar alla textet
        GameText about = new GameText("Connect Four is a two-player connection game in which the " +
                "players take turns dropping colored discs from the top into a seven-column, six-row vertically " +
                "suspended grid. The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four" +
                " of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.", size, maxWidth);

        GameText start = new GameText("Start game by clicking on new game in startmenu and name the players then " +
                "click on Start Game. You also have the choice of changing the number of columns and rows. If no name is " +
                "entered on a player that player is a guest and the result won't be registered in highscores.",
                size, maxWidth);

        GameText highscore = new GameText("Top ten players can be view by clicking on " +
                "Highscore in startmenu.", size, maxWidth);

        GameText history = new GameText("History from previous games can be viewed by clicking on history in " +
                "startmenu then choosing a game and click on show.", size, maxWidth);

        GameText haveFun = new GameText("Have fun playing Connect Four and may the " +
                "best player win!", size, maxWidth);

        // För att kunna gå tillbaka till startmenu
        OptionLabel back = new OptionLabel("Back", size * 3);
        back.setOnMouseClicked(e -> scene.setRoot(startPane));

        // Lägger in allt i layouten för att sedan lägga i helpPane
        layout.getChildren().addAll(about, start, highscore, history, haveFun, back);
        helpPane.getChildren().add(layout);
    }

    /**
     *  Sätter scene till startPane.
     */
    public void mainMenu(){
        scene.setRoot(startPane);
    }

    /**
     *  En text class så alla ser lika ut.
     */
    private class GameText extends Text{

        public GameText(String text, double size, double maxWidth){
            super(text);
            setFont(new Font("Alegreya Sans SC", size));
            setFill(Color.WHITE);
            setWrappingWidth(maxWidth);
        }
    }

    /**
     *  En label class som används när programmet ska använda vanliga labels.
     */
    public class GameLabel extends Label{

        public GameLabel(String text, double size){
            super(text);
            setFont(new Font("Alegreya Sans SC", size));
            setTextFill(Color.WHITE);
            autosize();
        }
    }

    /**
     *  En label class som används när programmet ska använda sig av labels med funktionalitet.
     */
    public static class OptionLabel extends Label{

        public OptionLabel(String text, double size){
            super(text);

            setFont(new Font("Alegreya Sans SC", size / 3));
            setTextFill(Color.WHITE);

            setOnMouseEntered( e -> {
                setTextFill(Color.GOLD);
            });

            setOnMouseExited( e -> {
                setTextFill(Color.WHITE);
            });

            autosize();

        }
    }
}
