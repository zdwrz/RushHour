package sample.gameBuilder;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.Car;
import sample.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuilderController {

    @FXML
    Car car;
    @FXML
    Car car_v;
    @FXML
    Car truck;
    @FXML
    Car truck_v;
    @FXML
    Car mainCar;

    @FXML
    GridPane fieldPane;

    @FXML
    AnchorPane legendPane;

    double prevX;
    double prevY;
    List<Car> cars = new ArrayList<>();

    @FXML
    protected void initialize() {
        cars.add(car);
        cars.add(car_v);
        cars.add(truck);
        cars.add(truck_v);
        cars.add(mainCar);
        cars.forEach(car->{
            car.setOnMouseDragged(this::carDragged);
            car.setOnMouseReleased(this::carReleased);
            car.setOnMousePressed(this::carClicked);
        });
        fieldPane.setOnMouseClicked(e->{
            System.out.println(e.getSceneX() + " , " + e.getSceneY());
        });

//        for (int i = 0; i < 6; i++) {
//            for(int j = 0 ; j < 6 ;j++){
//                ImageView cell = new ImageView();
//                cell.setFitWidth(60);
//                cell.setFitHeight(60);
//                cell.setImage(new Image("/resources/img/grass.png"));
//                fieldPane.add(cell,i,j);
//            }
//        }

    }
    public void carClicked(MouseEvent event) {
        prevY = prevX = 0;
    }

    public void carReleased(MouseEvent event) {
        Car car = (Car) event.getTarget();
        Bounds boundsInScene = car.localToScene(car.getBoundsInLocal());
        double carX = boundsInScene.getMinX();
        double carY = boundsInScene.getMinY();
        System.out.println("Car location is " + carX +", "+ carY);
        if(car.getName() != null) {
            Car car2 = new Car();
            car2.setName(car.getName());
            car.setName(null);
            car2.setImage(car.getImage());
            car2.setLayoutX(car.getLayoutX());
            car2.setLayoutY(car.getLayoutY());
            car2.setFitWidth(car.getFitWidth());
            car2.setFitHeight(car.getFitHeight());
            car2.setPreserveRatio(true);
            car2.setMain(car.isMain());
            car2.setLength(car.getLength());
            car2.setOnMouseDragged(this::carDragged);
            car2.setOnMousePressed(this::carClicked);
            car2.setOnMouseReleased(this::carReleased);
            car2.setVisible(true);
            car2.setVertical(car.isVertical());
            legendPane.getChildren().add(car2);
        }

        int x,y;
        for (int i = 1; ; i++) {
            if (Constants.FIELD_START_POINT[0]  + i * Constants.CELL_WIDTH > carX) {
                x = i - 1;
                break;
            }
        }
        for (int i = 1; ; i++) {
            if (Constants.FIELD_START_POINT[1]  + i * Constants.CELL_WIDTH > carY) {
                y = i - 1;
                break;
            }
        }
        Car.Location location = new Car.Location(x,y);
        car.setPosition(location);
    }

    public void carDragged(MouseEvent event) {
        Car car = (Car) event.getTarget();
        if(prevX != 0) {
            double movedX = event.getX() - prevX;
            double movedY = event.getY() - prevY;
            car.setY(car.getY() + movedY);
            car.setX(car.getX() + movedX);
        }
        prevX = event.getX();
        prevY = event.getY();
    }

    public void reset(MouseEvent event) throws IOException {
        Stage pStage = (Stage) fieldPane.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("/builder.fxml"));
        pStage.setScene(new Scene(root));
        pStage.setResizable(false);
        pStage.show();
    }

    /**
     * Car{name='null', position=Location{x=3, y=2}, length=3, vertical=false, main=false}
     * 3|2|3|false|false|null
     * @param event
     */
    public void createGame(MouseEvent event) {
        File data = new File(Constants.GAME_DATA_LOCATION);
        if (!data.exists()) {
            try {
                data.getParentFile().mkdirs();
                data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileOutputStream fos = new FileOutputStream(data, true);){
            legendPane.getChildren().stream()
                    .filter(e-> e instanceof Car)
                    .map(e->(Car)e)
                    .filter(e->e.getName() == null)
                    .map(e->e.getPosition().getX()
                            + Constants.DELIMITER+e.getPosition().getY()
                            +Constants.DELIMITER+e.getLength()
                            +Constants.DELIMITER + e.isVertical()
                            +Constants.DELIMITER + e.isMain()
                            +Constants.DELIMITER + e.getName()+Constants.CAR_END_MARK
                    )
                    .forEach(e->{
                        try {
                            fos.write(e.getBytes());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    });
            fos.write(System.getProperty("line.separator").getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("New Game Added");
        alert.setHeaderText(null);
        alert.setContentText("Successfully added a new game.");
        alert.showAndWait();
        try {
            reset(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
