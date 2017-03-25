
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

import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * Created by Arcombe on 2017-03-21.
 */
public class GameMenu extends Application {

    private Database db;

    private BoardView board;

    private Stage stage;
    private Scene scene;

    private StackPane startPane;
    private StackPane multiplayerPane;
    private GridPane highscorePane;
    private StackPane historyPane;
    private StackPane helpPane;

    private Label gameTitle;


    public void start(Stage primaryStage) throws Exception {

        db = new Database();

        if (db.openConnection("playerDatabase.db")) {

        } else {
            System.out.println("Kunde inte skapa connection till databasen");
        }

        /*db.newGame("Alexander", "Combo");
        db.updateGameResult("Alexander", 1);

        for (GameHistory game : db.getGameHistory()){
            if (game.winner == null)
                System.out.println("Spelet mellan " + game.playerNameRed + " och " + game.playerNameYellow
                + " spelades inte färdigt. GameID: " + game.gameID);
            else
                System.out.println(game.winner + " vann GameID: " + game.gameID);
        }

        List<GameHistory> games = db.getGameHistory();
        GameHistory game = games.get(1);
        boolean red = true;
        for (Move move : db.getMoves(game.gameID)){
            if(red)
                System.out.println(game.playerNameRed + " placed disc on column " + move.column + " and row " + move.row);
            else
                System.out.println(game.playerNameYellow + " placed disc on column " + move.column + " and row " + move.row);
            red = !red;
        } */

        board = new BoardView(this, db);

        stage = primaryStage;
        startPane = new StackPane();
        multiplayerPane = new StackPane();
        highscorePane = new GridPane();
        historyPane = new StackPane();
        helpPane = new StackPane();

        scene = new Scene(startPane, 500, 500);

        setupStartPane();
        setupMultiplayerPane();
        setupHighscore();
        setupHistory();
        setupHelp();
        stage.setScene(scene);
        stage.setTitle("Connect Four");
        stage.setMinHeight(300);
        stage.setMinWidth(300);

        // Uppdaterar layouten ifall fönstrets storlek ändras.
        startPane.widthProperty().addListener( e -> {
            startPane.getChildren().clear();
            setupStartPane();
        });

        startPane.heightProperty().addListener( e -> {
            startPane.getChildren().clear();
            setupStartPane();
        });

        multiplayerPane.widthProperty().addListener( e -> {
            multiplayerPane.getChildren().clear();
            setupMultiplayerPane();
        });

        multiplayerPane.heightProperty().addListener( e -> {
            multiplayerPane.getChildren().clear();
            setupMultiplayerPane();
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



        stage.show();
    }

    // Skapar animation
    private void animation(boolean red, double xPos, int dur){

        Random rd = new Random();
        TranslateTransition ani = new TranslateTransition();
        Disc d = new Disc(red, Math.min(startPane.getWidth(),startPane.getHeight()) / 9 );
        d.setTranslateX(xPos);
        d.setTranslateY(- (startPane.getHeight() * 0.6) );

        startPane.getChildren().add(d);

        // Sätter duration och att den ska loop.
        ani.setDuration(javafx.util.Duration.seconds(dur));
        ani.setNode(d);
        ani.setToY(startPane.getHeight() * 0.6);
        ani.setCycleCount(100);

        // Startar animationen
        ani.play();
    }

    // Sätter upp och uppdaterar Startsidan.
    private void setupStartPane(){

        // Sätter bakgrunden.
        startPane.setStyle("-fx-background-color: #2C97DE;");

        // Skapar animation i bakgrunden.
        animation(true, -startPane.getWidth() / 3, 5);
        animation(false,startPane.getWidth() / 3, 5);
        animation(true, startPane.getWidth() / 5, 3);
        animation(false, -startPane.getWidth() / 5, 3);

        // Beräknar size som ett mått.
        double size = Math.min(startPane.getWidth(), startPane.getHeight()) / 8;

        // En vertikal box som ska innehålla delarna.
        VBox layout = new VBox(30);
        layout.alignmentProperty().bind(startPane.alignmentProperty());

        // Skapar Titeln
        gameTitle = new GameLabel("Connect Four", size);
        gameTitle.setTextFill(Color.BLACK);

        // Skapar alternativen i startmenyn och vad som ska hända när de klickas
        OptionLabel newGame = new OptionLabel("New Game", size);
        newGame.setOnMouseClicked( e -> {
            scene.setRoot(multiplayerPane);
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
            scene.setRoot(helpPane);
        });

        OptionLabel exit = new OptionLabel("Exit", size);
        exit.setOnMouseClicked( e -> System.exit(0));

        layout.getChildren().addAll(gameTitle, newGame, highscore, history, help, exit);
        startPane.getChildren().add(layout);
    }

    // Sätter upp och uppdaterar New Game sidan.
    private void setupMultiplayerPane(){

        // Sätter bakgrunden
        multiplayerPane.setStyle("-fx-background-color: #2C97DE;");

        // Beräknar size som mått.
        double size = Math.min(multiplayerPane.getWidth(), multiplayerPane.getHeight()) / 8;

        // En vertikal box som ska innehålla delarna.
        VBox layout = new VBox(size);
        layout.alignmentProperty().bind(multiplayerPane.alignmentProperty());

        // Skapar en horisontell box för att hålla label och textfield
        HBox player1Part = new HBox(10);
        player1Part.alignmentProperty().bind(layout.alignmentProperty());
        GameLabel player1 = new GameLabel("Player Red: ", size / 3);
        TextField player1TextField = new TextField();
        // Promptext gör att man kan start spelet och spela som gäst
        player1TextField.setPromptText("Red");
        player1Part.getChildren().addAll(player1, player1TextField);

        // Skapar en horisontell box för att hålla label och textfield
        HBox player2Part = new HBox( 10);
        player2Part.alignmentProperty().bind(layout.alignmentProperty());
        GameLabel player2 = new GameLabel("Player Yellow: ", size / 3);
        TextField player2TextField = new TextField();
        // Promptext gör att man kan start spelet och spela som gäst
        player2TextField.setPromptText("Yellow");
        player2Part.getChildren().addAll(player2, player2TextField);

        HBox columnsAndRows = new HBox();
        columnsAndRows.setSpacing(10);
        columnsAndRows.alignmentProperty().bind(multiplayerPane.alignmentProperty());
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
        // För att starta spelet, fyller man inte i textfielden så spelar man som Guests.
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
        // För att komma tillbaka till startmenyn.
        OptionLabel back = new OptionLabel("Back", size);
        back.setOnMouseClicked( e -> scene.setRoot(startPane));

        startBackBox.getChildren().addAll(back, start);
        layout.getChildren().addAll(player1Part, player2Part, columnsAndRows, startBackBox);

        multiplayerPane.getChildren().add(layout);
    }

    private void setupHighscore(){

        // Sätter en size att utgåfrån.
        double size = Math.min(highscorePane.getWidth(), highscorePane.getHeight()) / 9;

        // Bakgrunden
        highscorePane.setStyle("-fx-background-color: #2C97DE;");

        // Förnyar
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

        //
        OptionLabel back = new OptionLabel("Back", size);
        back.setTextFill(Color.BLACK);
        back.setOnMouseClicked(e -> scene.setRoot(startPane));
        GridPane.setConstraints(back, 0, 11);
        highscorePane.getChildren().add(back);
    }

    private void setupHistory(){

        historyPane.setStyle("-fx-background-color: #2C97DE;");

        List<GameHistory> games = db.getGameHistory();

        // Beräknar size som mått.
        double size = Math.min(historyPane.getWidth(), historyPane.getHeight()) / 16;

        VBox layout = new VBox();
        layout.alignmentProperty().bind(historyPane.alignmentProperty());
        layout.setSpacing(size);

        HBox first = new HBox();
        first.alignmentProperty().bind(layout.alignmentProperty());
        first.setSpacing(size );
        GameLabel chooseGame = new GameLabel("Choose Game: ", size);

        ComboBox gamesOptions = new ComboBox();
        gamesOptions.setPrefWidth(historyPane.getWidth() / 2);
        gamesOptions.setMaxWidth(Control.USE_PREF_SIZE);
        for (GameHistory game : games){
            gamesOptions.getItems().add("Game " + game.gameID + " " + game.playerNameRed + " vs " + game.playerNameYellow);
        }

        first.getChildren().addAll(chooseGame, gamesOptions);

        ListView<String> showGame = new ListView<String>();
        showGame.setPrefHeight(historyPane.getHeight() / 2);
        showGame.setPrefWidth(historyPane.getWidth() * 0.7);
        showGame.setMaxHeight(Control.USE_PREF_SIZE);
        showGame.setMaxWidth(Control.USE_PREF_SIZE);

        HBox third = new HBox();
        third.setSpacing(size);
        third.alignmentProperty().bind(layout.alignmentProperty());
        OptionLabel back = new OptionLabel("Back", size * 3);
        back.setOnMouseClicked( e -> scene.setRoot(startPane));

        OptionLabel show = new OptionLabel("Show", size * 3);
        show.setOnMouseClicked( e -> {
            Object obj = gamesOptions.getValue();
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
                if (game.winner == null)
                    showGame.getItems().add("The Game was not finished");
                else if (game.winner.equals("Tied"))
                    showGame.getItems().add("The Game was a tie");
                else
                    showGame.getItems().add("Winner: " + game.winner);
            }
        });

        third.getChildren().addAll(back, show);

        layout.getChildren().addAll(first, showGame, third);

        historyPane.getChildren().addAll(layout);
    }

    private void setupHelp(){

        // Sätter en size att utgåfrån.
        double size = Math.min(helpPane.getWidth(), helpPane.getHeight()) / 32;
        double maxWidth = helpPane.getWidth() - size * 5;

        helpPane.setStyle("-fx-background-color: #2C97DE;");

        VBox vbox = new VBox();
        vbox.setSpacing(size);
        vbox.alignmentProperty().bind(helpPane.alignmentProperty());


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

        OptionLabel back = new OptionLabel("Back", size * 3);
        back.setOnMouseClicked(e -> scene.setRoot(startPane));

        vbox.getChildren().addAll(about, start, highscore, history, haveFun, back);

        helpPane.getChildren().add(vbox);
    }

    public void mainMenu(){
        scene.setRoot(startPane);
    }

    private static class GameText extends Text{

        public GameText(String text, double size, double maxWidth){
            super(text);
            setFont(new Font("Alegreya Sans SC", size));
            setFill(Color.WHITE);
            setWrappingWidth(maxWidth);
        }
    }

    public static class GameLabel extends Label{

        public GameLabel(String text, double size){
            super(text);
            setFont(new Font("Alegreya Sans SC", size));
            setTextFill(Color.WHITE);
            autosize();

        }

    }

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
