package test;

import org.junit.jupiter.api.Test;
import salestaxapp.SalesTaxCalculator;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SalesTaxCalculatorTest {
    @Test
    public void testCalculateSalesTax() throws Exception {
        SalesTaxCalculator calculator = new SalesTaxCalculator();
        Method method = SalesTaxCalculator.class.getDeclaredMethod("calculateSalesTax", String.class, BigDecimal.class);
        method.setAccessible(true);

        // Test case 1: Basic tax rate, non-imported item
        String itemName1 = "book";
        BigDecimal price1 = new BigDecimal("12.49");
        BigDecimal expectedTax1 = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal result1 = (BigDecimal) method.invoke(calculator, itemName1, price1);
        assertEquals(expectedTax1, result1);

        // Test case 2: Basic tax rate, imported item
        String itemName2 = "imported box of chocolates";
        BigDecimal price2 = new BigDecimal("10.00");
        BigDecimal expectedTax2 = new BigDecimal("0.50");
        BigDecimal result2 = (BigDecimal) method.invoke(calculator, itemName2, price2);
        assertEquals(expectedTax2, result2);

        // Test case 3: Imported and basic tax rate, non-exempt item
        String itemName3 = "imported bottle of perfume";
        BigDecimal price3 = new BigDecimal("47.50");
        BigDecimal expectedTax3 = new BigDecimal("7.15");
        BigDecimal result3 = (BigDecimal) method.invoke(calculator, itemName3, price3);
        assertEquals(expectedTax3, result3);
    }
}
