package test.java.org.vamonos;

import main.java.org.vamonos.Search;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

class SearchTest{
    private final Search TEST = new Search();

    @Test
    public void testQueryMaxPrice_T1() {
        Assertions.assertEquals(this.TEST.queryMaxPrice(new Scanner("0")),0);
    }

    @Test
    public void testQueryMaxPrice_T2() {
        Assertions.assertEquals(this.TEST.queryMaxPrice(new Scanner("*")),99999);
    }

    @Test
    public void testQueryMaxPrice_T3() {
        Assertions.assertEquals(this.TEST.queryMaxPrice(new Scanner("100000")),99999);
    }

    @Test
    public void testQueryMaxPrice_T4() {
        Assertions.assertEquals(this.TEST.queryMaxPrice(new Scanner("-1")),0);
    }
}