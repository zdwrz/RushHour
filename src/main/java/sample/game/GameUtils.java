package sample.game;

import sample.Car;
import sample.Constants;

public class GameUtils {

    public static Car.Location calculatePosition(Car car) {
        int xLocation = (int) (car.getX() /  Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        int yLocation = (int) (car.getY() / Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        return new Car.Location(xLocation, yLocation);
    }

    public static boolean twoCarOverLapping(Car car1, Car car2) {
        double x = car1.getX();
        double y = car1.getY();
        double x1 = car2.getX();
        double y1 = car2.getY();
        double width = car1.isVertical()?100:(car1.getLength() * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        double width1 = car2.isVertical()?100:(car2.getLength() * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        double height = car1.isVertical()?(car1.getLength() * Constants.UNIT_SIZE_HORIZONTAL_WIDTH) : 100;
        double height1 = car2.isVertical()?(car2.getLength() * Constants.UNIT_SIZE_HORIZONTAL_WIDTH) : 100;
        return x < x1 + width1 && x + width > x1 && y < y1 + height1 && y + height > y1;

    }

    public static boolean twoCarOverLapping(double x, double y, double x1, double y1, boolean car1Vertical, boolean car2Vertical, int car1Length, int car2Length) {
        double width = car1Vertical?100:(car1Length * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        double width1 = car2Vertical?100:(car2Length * Constants.UNIT_SIZE_HORIZONTAL_WIDTH);
        double height = car1Vertical?(car1Length * Constants.UNIT_SIZE_HORIZONTAL_WIDTH) : 100;
        double height1 = car2Vertical?(car2Length * Constants.UNIT_SIZE_HORIZONTAL_WIDTH) : 100;
        return x < x1 + width1 && x + width > x1 && y < y1 + height1 && y + height > y1;
    }
}
