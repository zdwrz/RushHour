package sample;

import com.sun.javafx.scene.control.skin.LabeledText;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.game.GameEngine;
import sample.game.GameUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Controller {

    double prevX;
    double prevY;

    GameEngine game;

    int step = 0;

    @FXML
    AnchorPane mainField;

    @FXML
    Label stepLabel;

    @FXML
    protected void initialize() throws IOException {
        mainField.setPrefSize(Constants.WINDOWS_SIZE_WIDTH,Constants.WINDOWS_SIZE_HEIGHT);
//        drawTheGrid();
        game = new GameEngine();
        step = 0;
        stepLabel.setText(step + Constants.STEP);
        popSelectGame();

    }

    private void popSelectGame() throws IOException {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Rush Hour");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose your game");
        dialog.setGraphic(null);
        Parent dialogPane = FXMLLoader.load(getClass().getResource("/select_game.fxml"));
        GridPane btnGrid = (GridPane)dialogPane.lookup("#buttonGridPane");
        Button randomBtn = (Button)dialogPane.lookup("#randomBtn");
        randomBtn.setOnMouseClicked(e->{
            dialog.setResult((new Random().nextInt(game.getGames().size())));
            dialog.close();
        });
        for (int i = 0; i < game.getGames().size();i++) {
            Button btn = new Button();
            btn.setText("Jam " + i);
            btn.setUserData(i);
            btn.setOnMouseClicked(e->{
                if( e.getTarget() instanceof Button) {
                    dialog.setResult((Integer) ((Button) (e.getTarget())).getUserData());
                }else{
                    dialog.setResult((Integer) ((Button)(((LabeledText) (e.getTarget())).getParent())).getUserData());
                }
                dialog.close();
            });
            btnGrid.add(btn,i % 5, i / 5);
        }
        dialog.getDialogPane().setContent(dialogPane);
        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(this::prepareGame);
    }

    private void prepareGame(Integer i) {
        prepareGame(game.getGames().get(i));
    }

    private void prepareGame(List<Car> cars) {
        cars.forEach(this::addOneCar);
    }

    private void drawTheGrid() {
        for (int i = 1; i <= 6; i++) {
            Line lineX = new Line(100 * i ,0, 100 * i,600);
            Line lineY = new Line(0, 100 * i, 600, 100 * i);
            mainField.getChildren().add(lineX);
            mainField.getChildren().add(lineY);
        }
    }

    private void addOneCar(Car car) {
        String imgSrc = getImg(car.getLength(),car.isVertical());
        car.setPreserveRatio(false);

        if(car.isVertical()){
            car.setImage(car.isMain()? new Image("img/car_main.png"):new Image(imgSrc));
            car.setFitWidth(Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
            car.setFitHeight(Constants.UNIT_SIZE_HORIZONTAL_WIDTH * car.getLength());
        }else {
            car.setImage(car.isMain()? new Image("img/car_main.png"):new Image(imgSrc));
            car.setFitHeight(Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
            car.setFitWidth(Constants.UNIT_SIZE_HORIZONTAL_WIDTH * car.getLength());
        }
        car.setX(car.getPosition().x * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        car.setY(car.getPosition().y * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);

        car.setOnMouseDragged(this::carDragged);
        car.setOnMousePressed(this::carClicked);
        car.setOnMouseReleased(this::carReleased);
        mainField.getChildren().add(car);
        game.getCars().add(car);
        if (car.isMain()) {
            game.setMainCar(car);
        }
    }
    private void addOneCar(int x, int y, boolean vertical, int length) {
        Car car = new Car(x,y,vertical,length);
        addOneCar(car);
    }

    private String getImg(int length, boolean vertical) {
        Random r = new Random();
        int truckNo = 1 + r.nextInt(Constants.TOTAL_TRUCK_NO);
        int carNo = 1 + r.nextInt(Constants.TOTAL_CAR_NO);
        if(length == 2){
            return vertical?"img/car"+carNo+"_vertical.png":"img/car"+carNo+".png";
        }else{
            return vertical? "img/truck"+truckNo+"_vertical.png" :"img/truck"+truckNo+".png";
        }
    }

    private void winGamePop(Car car, double distance) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(Constants.ANIMATION_DUARATION_CAR_MOVE),car);

        if (car.isVertical()) {
            translateTransition.setByY(distance);

        }else{
            translateTransition.setByX(distance);
        }
        translateTransition.setOnFinished(e->{
            if (car.isVertical()) {
                car.setY(car.getY() + car.getTranslateY());

            }else{
                car.setX(car.getX() + car.getTranslateX());
            }

            car.setTranslateX(0);
            car.setTranslateY(0);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Win");
            alert.setHeaderText(null);
            alert.setContentText("You Win The Game!!! Used " + step + " steps.");
            alert.setOnCloseRequest(r->{this.restartBtn(null);});
            alert.show();
        });
        translateTransition.play();
    }

    public void carDragged(MouseEvent event) {
        Car car = (Car) event.getTarget();
        if(prevX != 0) {
            double movedX = event.getX() - prevX;
            double movedY = event.getY() - prevY;
            if(car.isVertical()){
                double newY = car.getY() + movedY;
                car.setY(checkNewY(car, newY));
//                System.out.println("Y is " + (car.getY() + movedY) );
            }else{
                double newX = car.getX() + movedX;
                car.setX(checkNewX(car, newX));
//                System.out.println("X is " + (car.getX() + movedX) );
            }
            car.setPosition(GameUtils.calculatePosition(car));

//            System.out.println("Location is :" + car.getPosition());
        }
        prevX = event.getX();
        prevY = event.getY();
    }

    private double checkBoundry(int carLength, double newCord) {
        if(newCord <= 0.0) return 0.0;
        if(carLength == 3){
            if(newCord >= 300.0){
                return 300.0;
            }else{
                return newCord;
            }
        }else{
            if(newCord >= 400.0){
                return 400.0;
            }else{
                return newCord;
            }
        }
    }
    private double checkNewX(Car car, double newX) {
        for (Car c : game.getCars()) {
            if (c != car) {
                if(GameUtils.twoCarOverLapping(newX,car.getY(), c.getX(),c.getY(),car.isVertical(),c.isVertical(), car.getLength(),c.getLength())){
                   // System.out.println("FInd overLapping!!! " + c.getPosition());
                    return car.getX();
                };
            }
        }
        return checkBoundry(car.getLength(), newX);
    }

    private double checkNewY(Car car, double newY) {
        for (Car c : game.getCars()) {
            if (c != car) {
                if(GameUtils.twoCarOverLapping(car.getX(),newY, c.getX(),c.getY(),car.isVertical(),c.isVertical(), car.getLength(),c.getLength())){
                  //  System.out.println("FInd overLapping!!! " + c.getPosition());
                    return car.getY();
                };
            }
        }
        return checkBoundry(car.getLength(), newY);
    }

    public void carClicked(MouseEvent event) {
        prevY = prevX = 0;
        Car car = (Car) event.getTarget();
    }


    public void carReleased(MouseEvent event) {
        Car car = (Car) event.getTarget();
        playMoveCar(car,calculateMove(car));
        car.setPosition(GameUtils.calculatePosition(car));
        stepLabel.setText(++step + Constants.STEP);
        checkWinning();
    }

    private void checkWinning() {
        Car mainCar = game.getMainCar();
        double range = mainCar.getX();
        for (double newX = range; newX <= Constants.WINDOWS_SIZE_WIDTH; newX++) {
            for (Car c : game.getCars()) {
                if (c != mainCar) {
                    if(GameUtils.twoCarOverLapping(newX, mainCar.getY(), c.getX(),c.getY(),mainCar.isVertical(),c.isVertical(), mainCar.getLength(),c.getLength())){
                        System.out.println("FInd overLapping!!! " + c.getPosition());
                        return;
                    };
                }
            }
        }
        System.out.println("WIN!!!");
        winGamePop(mainCar, Constants.WINDOWS_SIZE_WIDTH - mainCar.getPosition().x *Constants.UNIT_SIZE_HORIZONTAL_WIDTH - 200) ;
    }

    private double calculateMove(Car car) {
        double x = car.getX();
        double y = car.getY();
        if (car.isVertical()) {
            double upper = car.getPosition().y * Constants.UNIT_SIZE_HORIZONTAL_WIDTH;
            double lower = (car.getPosition().y + 1) * Constants.UNIT_SIZE_HORIZONTAL_WIDTH;
            if(y - upper < lower - y){
                return upper - y;
            }else return lower - y;
        }else{
            double upper = car.getPosition().x * Constants.UNIT_SIZE_HORIZONTAL_WIDTH;
            double lower = (car.getPosition().x + 1) * Constants.UNIT_SIZE_HORIZONTAL_WIDTH;
            if(x - upper < lower - x){
                return upper - x;
            }else return lower - x;
        }
    }

    private void playMoveCar(Car car, double distance) {
        if (car.isVertical()) {
            car.setY(car.getY() + distance);

        }else{
            car.setX(car.getX() + distance);
        }
    }

    public void restartBtn(MouseEvent event) {
        Stage pStage = (Stage) mainField.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pStage.setScene(new Scene(root));
        pStage.setResizable(false);
        pStage.show();
    }
}
