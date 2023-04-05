package salestaxapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SalesTaxCalculator {
    private static final BigDecimal BASIC_TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal IMPORT_TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal ROUNDING_FACTOR = new BigDecimal("0.05");
    private static final Pattern INPUT_PATTERN = Pattern.compile("(\\d+)\\s+([^\\d]+)\\s+at\\s+(\\d+\\.\\d{2})");


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, BigDecimal> itemPrices = new HashMap<>();
        Map<String, Integer> itemQuantities = new HashMap<>();
        BigDecimal totalSalesTax = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        List<String> itemList = new ArrayList<>();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                break;
            }

            Matcher matcher = INPUT_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid input format: " + line);
            }

            int quantity = Integer.parseInt(matcher.group(1));
            String itemName = matcher.group(2);
            BigDecimal price = new BigDecimal(matcher.group(3));

            BigDecimal salesTax = calculateSalesTax(itemName, price);
            BigDecimal totalPrice = price.add(salesTax).multiply(new BigDecimal(quantity));

            totalSalesTax = totalSalesTax.add(salesTax);
            totalCost = totalCost.add(totalPrice);

            itemPrices.merge(itemName, totalPrice, BigDecimal::add);
            itemQuantities.merge(itemName, quantity, Integer::sum);

            if (!itemList.contains(itemName)) {
                itemList.add(itemName);
            }
        }

        for (int i = 0; i < itemList.size(); i++) {
            String itemName = itemList.get(i);
            BigDecimal itemPrice = itemPrices.get(itemName);
            System.out.println(quantityString(itemQuantities, itemName) + itemName + ": " + formatPrice(itemPrice));
        }

        System.out.println("Sales Taxes: " + formatPrice(totalSalesTax));
        System.out.println("Total: " + formatPrice(totalCost));
    }

    private static String formatPrice(BigDecimal itemPrice) {
        return String.format("%.2f", itemPrice);
    }

    private static String quantityString(Map<String, Integer> itemQuantities, String itemName) {
        int quantity = itemQuantities.getOrDefault(itemName, 0);
        return quantity > 1 ? quantity + " " : "1 ";
    }

    private static BigDecimal calculateItemPrice(String itemName, int itemQuantity) {
        BigDecimal itemPrice = BigDecimal.ZERO;
        if (itemQuantity > 0) {
            BigDecimal price = getItemPrice(itemName);
            BigDecimal salesTax = calculateSalesTax(itemName, price);
            itemPrice = price.add(salesTax).multiply(new BigDecimal(itemQuantity));
        }
        return itemPrice;
    }

    private static BigDecimal getItemPrice(String itemName) {
        // TODO: implement method to look up item price in a database or service
        // For this example, just return a default price of $1.00
        return new BigDecimal("1.00");
    }

    private static BigDecimal calculateSalesTax(String itemName, BigDecimal price) {
        BigDecimal salesTax = BigDecimal.ZERO;
        if (!isTaxExempt(itemName)) {
            salesTax = salesTax.add(price.multiply(BASIC_TAX_RATE));
        }
        if (isImported(itemName)) {
            salesTax = salesTax.add(price.multiply(IMPORT_TAX_RATE));
        }
        return roundUp(salesTax);
    }

    private static BigDecimal roundUp(BigDecimal salesTax) {
        BigDecimal divided = salesTax.divide(ROUNDING_FACTOR, 0, RoundingMode.UP);
        return divided.multiply(ROUNDING_FACTOR);
    }

    private static boolean isImported(String itemName) {
        return itemName.contains("imported");
    }

    private static boolean isTaxExempt(String itemName) {
        return itemName.contains("book") || itemName.contains("chocolate") || itemName.contains("pills");
    }
}
