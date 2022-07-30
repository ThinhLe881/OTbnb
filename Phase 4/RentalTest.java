package test.java.org.vamonos;

import main.java.org.vamonos.Rental;
import main.java.org.vamonos.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RentalTest {
    Rental rental;
    ArrayList<Unit> units = new ArrayList<>();
    ArrayList<Unit> sessionUnits = new ArrayList<>();

    String sep = System.getProperty("line.separator");

    @BeforeAll
    void setUp() {
        this.rental = new Rental();

        this.units.add(new Unit("User001", "Toronto", 30000, 2, "F", "A0000001"));
        this.units.add(new Unit("User002", "Quebec", 60050, 4, "F", "A0000002"));
        this.units.add(new Unit("User005", "Toronto", 20000, 1, "F", "A0000003"));
        this.units.add(new Unit("User005", "Calgary", 20050, 1, "T", "A0000004"));
        this.units.add(new Unit("User001", "Oshawa", 45050, 3, "F", "A0000005"));
        this.units.add(new Unit("User001", "Tobermory", 9999, 1, "F", "A0000006"));
        this.units.add(new Unit("User002", "Tofino", 25000, 2, "F", "A0000007"));
        this.units.add(new Unit("User005", "Toronto", 35000, 3, "F", "A0000008"));

        this.sessionUnits.add(new Unit("User001", "Oshawa", 15000, 1, "F", "A0000009"));
    }

    @Test
    public void testQueryUnit_T1() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000009\nA0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(),
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental is not available for rental yet!" + this.sep +
                "Please enter the rental ID..." + this.sep
        );
    }

    @Test
    public void testQueryUnit_T2() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000000\nA0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(),
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental does not exist!" + this.sep +
                "Please enter the rental ID..." + this.sep
        );
    }

    @Test
    public void testQueryUnit_T3() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000001\nA0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(),
                "Please enter the rental ID..." + this.sep +
                "Sorry, you own this rental!" + this.sep +
                "Please enter the rental ID..." + this.sep
        );
    }

    @Test
    public void testQueryUnit_T4() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000004\nA0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(),
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental is not available!" + this.sep +
                "Please enter the rental ID..." + this.sep
        );
    }

    @Test
    public void testQueryUnit_T5() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(), "Please enter the rental ID..." + this.sep);
    }

    @Test
    public void testQueryUnit_T6() {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Assertions.assertEquals(
                this.rental.queryUnit(
                        new Scanner("A0000009\nA0000000\nA0000001\nA0000004\nA0000002"),
                        this.units,
                        this.sessionUnits,
                        "User001").getID(),
                "A0000002");

        Assertions.assertEquals(output.toString(),
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental is not available for rental yet!" + this.sep +
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental does not exist!" + this.sep +
                "Please enter the rental ID..." + this.sep +
                "Sorry, you own this rental!" + this.sep +
                "Please enter the rental ID..." + this.sep +
                "Sorry, that rental is not available!" + this.sep +
                "Please enter the rental ID..." + this.sep
        );
    }
}