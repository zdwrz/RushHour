package sample.game;

import sample.Car;
import sample.Constants;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {
    Map<Car.Location, Car> locationMap = new HashMap<>();
    List<Car> cars = new ArrayList<>();
    Car mainCar;
    List<List<Car>> games;
    public GameEngine() {
        try {
            games = Files.lines(new File(Constants.GAME_DATA_LOCATION).toPath()).map(l -> {
                return Arrays.asList(l.split(Constants.CAR_END_MARK)).stream().map(c -> {
//                    System.out.println(c);
                    String[] carData = c.split("\\"+Constants.DELIMITER);
//                    System.out.println(Arrays.toString(carData));
                    Car car = new Car();
                    car.setPosition(new Car.Location(Integer.parseInt(carData[0]), Integer.parseInt(carData[1])));
                    car.setLength(Integer.parseInt(carData[2]));
                    car.setVertical(new Boolean(carData[3]));
                    car.setMain(new Boolean(carData[4]));
                    return car;
                }).collect(Collectors.toList());
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<Car.Location, Car> getLocationMap() {
        return locationMap;
    }

    public void setLocationMap(Map<Car.Location, Car> locationMap) {
        this.locationMap = locationMap;
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car getMainCar() {
        return mainCar;
    }

    public void setMainCar(Car mainCar) {
        this.mainCar = mainCar;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public List<List<Car>> getGames() {
        return games;
    }

    public void setGames(List<List<Car>> games) {
        this.games = games;
    }
//    public static void main(String[] args) {
//        String d = "4|0|2|true|false|null";
//        System.out.println(Arrays.toString(d.split("\\|")));
//    }
}
