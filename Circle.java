package stepa.project;


public class Circle {

    public double radius;
    public Point point;


    public Circle(Point p1, Point p2){

        this.point = p1;
        this.radius = p1.getLength(p2);
    }

    @Override
    public String toString() {
        return "    Radius: " + String.format("%1f", radius) + ",\n    Centre: " + point;
    }
}
