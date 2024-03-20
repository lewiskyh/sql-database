package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TokeniserTest {
    private Tokeniser tokeniser;

    @BeforeEach
    public void setUp() {
        this.tokeniser = new Tokeniser("SELECT * FROM table12;");
    }

    //Test preprocessQuery with a simple query
    /**

    @Test
    public void testGetTokenAtCurrentIndex() {
        tokeniser.preprocessQuery();
        assertEquals("SELECT", tokeniser.getTokenByIndex(0));
        tokeniser.incrementTokenIndex();
        assertEquals("*", tokeniser.getTokenByIndex(1));
        tokeniser.incrementTokenIndex();
        assertEquals("FROM", tokeniser.getTokenByIndex(2));
        tokeniser.incrementTokenIndex();
        assertEquals("table12", tokeniser.getTokenByIndex(3));
    }

    // Test preprocessQuery with many whitespaces

    @Test
    public void testPreprocessQuery() {
        this.tokeniser = new Tokeniser("SELECT *        FROM         table12              WHERE name != 'Sion';");
        tokeniser.preprocessQuery();
        assertEquals("SELECT", tokeniser.getTokenByIndex(0));
        assertEquals("*", tokeniser.getTokenByIndex(1));
        assertEquals("FROM", tokeniser.getTokenByIndex(2));
        assertEquals("table12", tokeniser.getTokenByIndex(3));
        assertEquals("WHERE", tokeniser.getTokenByIndex(4));
        assertEquals("name", tokeniser.getTokenByIndex(5));
        assertEquals("!=", tokeniser.getTokenByIndex(6));
        assertEquals("'Sion'", tokeniser.getTokenByIndex(7));
        assertEquals(";", tokeniser.getTokenByIndex(8));
    }

    @Test
    public void testPreprocessQueryComplex() {
        this.tokeniser = new Tokeniser("SELECT name, age FROM students WHERE age == 18;");
        this.tokeniser.preprocessQuery();
        assertEquals("SELECT", tokeniser.getTokenByIndex(0));
        assertEquals("name", tokeniser.getTokenByIndex(1));
        assertEquals(",", tokeniser.getTokenByIndex(2));
        assertEquals("age", tokeniser.getTokenByIndex(3));
        assertEquals("FROM", tokeniser.getTokenByIndex(4));
        assertEquals("students", tokeniser.getTokenByIndex(5));
        assertEquals("WHERE", tokeniser.getTokenByIndex(6));
        assertEquals("age", tokeniser.getTokenByIndex(7));
        assertEquals("==", tokeniser.getTokenByIndex(8));
        assertEquals("18", tokeniser.getTokenByIndex(9));
    }

    @Test
    public void testPreprocessQueryComplex2() {
        this.tokeniser = new Tokeniser("INSERT INTO orders (id, item_name, quantity, price, shipped??) VALUES (999, 'iphone', 2, 99.99, FALSE);");
        this.tokeniser.preprocessQuery();
        assertEquals("INSERT", tokeniser.getTokenByIndex(0));
        assertEquals("INTO", tokeniser.getTokenByIndex(1));
        assertEquals("orders", tokeniser.getTokenByIndex(2));
        assertEquals("(", tokeniser.getTokenByIndex(3));
        assertEquals("id", tokeniser.getTokenByIndex(4));
        assertEquals(",", tokeniser.getTokenByIndex(5));
        assertEquals("item_name", tokeniser.getTokenByIndex(6));
        assertEquals(",", tokeniser.getTokenByIndex(7));
        assertEquals("quantity", tokeniser.getTokenByIndex(8));
        assertEquals(",", tokeniser.getTokenByIndex(9));
        assertEquals("price", tokeniser.getTokenByIndex(10));
        assertEquals(",", tokeniser.getTokenByIndex(11));
        assertEquals("shipped??", tokeniser.getTokenByIndex(12));
        assertEquals(")", tokeniser.getTokenByIndex(13));
        assertEquals("VALUES", tokeniser.getTokenByIndex(14));
        assertEquals("(", tokeniser.getTokenByIndex(15));
        assertEquals("999", tokeniser.getTokenByIndex(16));
        assertEquals(",", tokeniser.getTokenByIndex(17));
        assertEquals("'iphone'", tokeniser.getTokenByIndex(18));
        assertEquals(",", tokeniser.getTokenByIndex(19));
        assertEquals("2", tokeniser.getTokenByIndex(20));
        assertEquals(",", tokeniser.getTokenByIndex(21));
        assertEquals("99.99", tokeniser.getTokenByIndex(22));
        assertEquals(",", tokeniser.getTokenByIndex(23));
        assertEquals("FALSE", tokeniser.getTokenByIndex(24));
        assertEquals(")", tokeniser.getTokenByIndex(25));
    }*/

}
