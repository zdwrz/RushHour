package sample;

import javafx.scene.image.ImageView;

public class Car extends ImageView{
    private String name;
    private Location position;
    private int length;
    private boolean vertical = false;
    private boolean main = false;
    public Car(){}
    public Car(int x, int y, boolean vertical, int length) {
        super();
        this.position = new Location(x,y);
        this.length = length;
        this.vertical = vertical;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public static class Location{
        int x;
        int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "Location{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Car{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", length=" + length +
                ", vertical=" + vertical +
                ", main=" + main +
                '}';
    }
}

