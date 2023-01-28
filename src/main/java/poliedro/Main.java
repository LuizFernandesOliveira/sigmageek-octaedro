package poliedro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
  public static void main(String[] args) {
    var generatorTetrahedrons = new Generator(Polyhedron.OCTAHEDRON.points);
    System.out.println(generatorTetrahedrons.generate());
  }
}

class Generator {
  private final List<Point> points;
  private int tetrahedronsQuantity;
  private final Set<String> alreadyProcessed = new HashSet<>();
  private final Set<Tetrahedron> tetrahedronsProcessed = new HashSet<>();
  private final Set<Double> tetrahedronsValuesProcessed = new HashSet<>();

  public Generator(List<Point> points) {
    this.points = points;
  }

  public int generate() {
    for (int i = 0; i < points.size(); i++) {
      for (int j = 0; j < points.size(); j++) {
        for (int k = 0; k < points.size(); k++) {
          for (int l = 0; l < points.size(); l++) {
            if (alreadyProcessed.contains("_" + points.get(i).toString() + "__" + points.get(j).toString() + "__" + points.get(k).toString() + "__" + points.get(l).toString() + "_")) {
              continue;
            } else {
              addAlreadyProcessed("_" + points.get(i).toString() + "_", "_" + points.get(j).toString() + "_", "_" + points.get(k).toString() + "_", "_" + points.get(l).toString() + "_");
            }

            var p1 = points.get(i);
            var p2 = points.get(j);
            var p3 = points.get(k);
            var p4 = points.get(l);
            if (p1.equals(p2) || p1.equals(p3) || p1.equals(p4) || p2.equals(p3) || p2.equals(p4) || p3.equals(p4)) {
              continue;
            }
//            if (p1.name.equals("A") && p2.name.equals("B") && p3.name.equals("D") && p4.name.equals("ABC")) {
//              System.out.println("debug");
//              // 112.7
//            }
//            if (p1.name.equals("A") && p2.name.equals("B") && p3.name.equals("D") && p4.name.equals("ABE")) {
//              System.out.println("debug");
//              // 116.5203310673984
//            }
            if (!Generator.isCoplanarPoints(points.get(i), points.get(j), points.get(k), points.get(l))) {
              var tetrahedron = Tetrahedron.of(points.get(i), points.get(j), points.get(k), points.get(l));
              tetrahedron.processSideAreas();
              if (tetrahedronsValuesProcessed.contains(tetrahedron.sumAreas.doubleValue())) {
                continue;
              }
              System.out.println(points.get(i) + " " + points.get(j) + " " + points.get(k) + " " + points.get(l) + "  " + tetrahedron.sumAreas);
              tetrahedronsProcessed.add(tetrahedron);
              tetrahedronsValuesProcessed.add(tetrahedron.sumAreas.doubleValue());
              tetrahedronsQuantity++;
            }
          }
        }
      }
    }
    System.out.println("tetrahedronsProcessed: " + tetrahedronsValuesProcessed.size());
    return tetrahedronsQuantity;
  }

  public void addAlreadyProcessed(String i, String j, String k, String l) {
    alreadyProcessed.add(i + "" + j + "" + k + "" + l);
    alreadyProcessed.add(i + "" + j + "" + l + "" + k);
    alreadyProcessed.add(i + "" + k + "" + j + "" + l);
    alreadyProcessed.add(i + "" + k + "" + l + "" + j);
    alreadyProcessed.add(i + "" + l + "" + j + "" + k);
    alreadyProcessed.add(i + "" + l + "" + k + "" + j);
    alreadyProcessed.add(j + "" + i + "" + k + "" + l);
    alreadyProcessed.add(j + "" + i + "" + l + "" + k);
    alreadyProcessed.add(j + "" + k + "" + i + "" + l);
    alreadyProcessed.add(j + "" + k + "" + l + "" + i);
    alreadyProcessed.add(j + "" + l + "" + i + "" + k);
    alreadyProcessed.add(j + "" + l + "" + k + "" + i);
    alreadyProcessed.add(k + "" + i + "" + j + "" + l);
    alreadyProcessed.add(k + "" + i + "" + l + "" + j);
    alreadyProcessed.add(k + "" + j + "" + i + "" + l);
    alreadyProcessed.add(k + "" + j + "" + l + "" + i);
    alreadyProcessed.add(k + "" + l + "" + i + "" + j);
    alreadyProcessed.add(k + "" + l + "" + j + "" + i);
    alreadyProcessed.add(l + "" + i + "" + j + "" + k);
    alreadyProcessed.add(l + "" + i + "" + k + "" + j);
    alreadyProcessed.add(l + "" + j + "" + i + "" + k);
    alreadyProcessed.add(l + "" + j + "" + k + "" + i);
    alreadyProcessed.add(l + "" + k + "" + i + "" + j);
    alreadyProcessed.add(l + "" + k + "" + j + "" + i);
  }

  public static boolean isCoplanarPoints(Point p1, Point p2, Point p3, Point p4) {
    return ((p1.z == p2.z || p1.z == (-1)*p2.z)
            && (p2.z == p3.z || p2.z == (-1)*p3.z)
            && (p3.z == p4.z || p3.z == (-1)*p4.z))
        || ((p1.x == p2.x || p1.x == (-1)*p2.x)
            && (p2.x == p3.x || p2.x == (-1)*p3.x)
            && (p3.x == p4.x || p3.x == (-1)*p4.x))
        || ((p1.y == p2.y || p1.y == (-1)*p2.y)
            && (p2.y == p3.y || p2.y == (-1)*p3.y)
            && (p3.y == p4.y || p3.y == (-1)*p4.y))
        || (Matrix3x3.of(p1, p2, p3).determinantIsZero()
            && Matrix3x3.of(p1, p2, p4).determinantIsZero()
            && Matrix3x3.of(p1, p3, p4).determinantIsZero()
            && Matrix3x3.of(p2, p3, p4).determinantIsZero())
        || (Matrix3x3.midpoint(p1, p2, p3).equals(p4));
  }
}


class Matrix3x3 {
  private final Point p1;
  private final Point p2;
  private final Point p3;

  public Matrix3x3(Point p1, Point p2, Point p3) {
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

  public static Matrix3x3 of(Point p1, Point p2, Point p3) {
    return new Matrix3x3(p1, p2, p3);
  }

  public boolean determinantIsZero() {
    return (p1.x * p2.y * p3.z) + (p1.y * p2.z * p3.x) + (p1.z * p2.x * p3.y)
        - ((p1.z * p2.y * p3.x) + (p1.y * p2.x * p3.z) + (p1.x * p2.z * p3.y)) == 0;
  }

  public static Point midpoint(Point p1, Point p2, Point p3) {
    return Point.of((p1.x + p2.x + p3.x) / 3, (p1.y + p2.y + p3.y) / 3, (p1.z + p2.z + p3.z) / 3, p1.name + p2.name + p3.name);
  }

  public double getArea() {
    var vectorP1P2 = vectorP1P2();
    var vectorP1P3 = vectorP1P3();

    var j = ((vectorP1P3.x * vectorP1P2.z) - (vectorP1P3.z * vectorP1P2.x));
    var i = ((vectorP1P2.y * vectorP1P3.z) - (vectorP1P2.z * vectorP1P3.y));
    var k = ((vectorP1P2.x * vectorP1P3.y) - (vectorP1P2.y * vectorP1P3.x));

    return Math.sqrt(Math.pow(i, 2) + Math.pow(j, 2) + Math.pow(k, 2)) / 2;
  }

  private Point vectorP1P2() {
    return Point.of(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z, "VETOR" + p1.name + p2.name);
  }

  private Point vectorP1P3() {
    return Point.of(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z, "VETOR" + p1.name + p3.name);
  }
}

class Tetrahedron {
  private final Point p1;
  private final Point p2;
  private final Point p3;
  private final Point p4;

  public final List<Double> sideAreas = new ArrayList<>();
  public BigDecimal sumAreas = BigDecimal.ZERO;

  public Tetrahedron(Point p1, Point p2, Point p3, Point p4) {
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
    this.p4 = p4;
  }

  public static Tetrahedron of(Point p1, Point p2, Point p3, Point p4) {
    return new Tetrahedron(p1, p2, p3, p4);
  }

  public void processSideAreas() {
    sideAreas.add(Matrix3x3.of(p1, p2, p3).getArea());
    sideAreas.add(Matrix3x3.of(p1, p3, p2).getArea());
    sideAreas.add(Matrix3x3.of(p2, p1, p3).getArea());
    sideAreas.add(Matrix3x3.of(p2, p3, p1).getArea());
    sideAreas.add(Matrix3x3.of(p3, p1, p2).getArea());
    sideAreas.add(Matrix3x3.of(p3, p2, p1).getArea());

    sideAreas.add(Matrix3x3.of(p1, p2, p4).getArea());
    sideAreas.add(Matrix3x3.of(p1, p4, p2).getArea());
    sideAreas.add(Matrix3x3.of(p2, p1, p4).getArea());
    sideAreas.add(Matrix3x3.of(p2, p4, p1).getArea());
    sideAreas.add(Matrix3x3.of(p4, p1, p2).getArea());
    sideAreas.add(Matrix3x3.of(p4, p2, p1).getArea());

    sideAreas.add(Matrix3x3.of(p1, p3, p4).getArea());
    sideAreas.add(Matrix3x3.of(p1, p4, p3).getArea());
    sideAreas.add(Matrix3x3.of(p3, p1, p4).getArea());
    sideAreas.add(Matrix3x3.of(p3, p4, p1).getArea());
    sideAreas.add(Matrix3x3.of(p4, p1, p3).getArea());
    sideAreas.add(Matrix3x3.of(p4, p3, p1).getArea());

    sideAreas.add(Matrix3x3.of(p2, p3, p4).getArea());
    sideAreas.add(Matrix3x3.of(p2, p4, p3).getArea());
    sideAreas.add(Matrix3x3.of(p3, p2, p4).getArea());
    sideAreas.add(Matrix3x3.of(p3, p4, p2).getArea());
    sideAreas.add(Matrix3x3.of(p4, p2, p3).getArea());
    sideAreas.add(Matrix3x3.of(p4, p3, p2).getArea());

    this.sumAllAreas();
  }

  public void sumAllAreas() {
    for (var area : sideAreas) {
      sumAreas = sumAreas.add(BigDecimal.valueOf(area));
    }
    sumAreas = sumAreas.setScale(0, RoundingMode.HALF_EVEN);
  }

  @Override
  public boolean equals(Object obj) {
    return this.sumAreas == ((Tetrahedron) obj).sumAreas;
  }
}

enum Polyhedron {
  OCTAHEDRON(
      List.of(
          Octahedron.A, // Point A
          Octahedron.B, // Point B
          Octahedron.C, // Point C
          Octahedron.D, // Point D
          Octahedron.E, // Point E
          Octahedron.F, // Point F
          Matrix3x3.midpoint(Octahedron.A, Octahedron.B, Octahedron.C), // Midpoint ABC
          Matrix3x3.midpoint(Octahedron.A, Octahedron.B, Octahedron.E), // Midpoint ABE
          Matrix3x3.midpoint(Octahedron.A, Octahedron.C, Octahedron.F), // Midpoint ACF
          Matrix3x3.midpoint(Octahedron.A, Octahedron.E, Octahedron.F), // Midpoint AEF
          Matrix3x3.midpoint(Octahedron.B, Octahedron.C, Octahedron.D), // Midpoint BCD
          Matrix3x3.midpoint(Octahedron.B, Octahedron.D, Octahedron.E), // Midpoint BDE
          Matrix3x3.midpoint(Octahedron.C, Octahedron.D, Octahedron.F), // Midpoint CDF
          Matrix3x3.midpoint(Octahedron.D, Octahedron.E, Octahedron.F) // Midpoint DEF
      )
  );

  Polyhedron(List<Point> points) {
    this.points = points;
  }

  public final List<Point> points;
}

class Octahedron {
  public static Point A = Point.of(2, -2, 0, "A");
  public static Point B = Point.of(0, 0, 2*Math.sqrt(2), "B");
  public static Point C = Point.of(2, 2, 0, "C");
  public static Point D = Point.of(-2, 2, 0, "D");
  public static Point E = Point.of(-2, -2, 0, "E");
  public static Point F = Point.of(0, 0, -2*Math.sqrt(2), "F");
}

class Point {
  public double x;
  public double y;
  public double z;
  public String name;

  private Point(double x, double y, double z, String name) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.name = name;
  }

  public static Point of(double x, double y, double z, String name) {
    return new Point(x, y, z, name);
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    return this.x == ((Point) obj).x && this.y == ((Point) obj).y && this.z == ((Point) obj).z;
  }
}
