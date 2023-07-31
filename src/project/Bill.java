package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Bill {
    private int billNum;
    private int customerId;
    private String month;
    private double unitsConsumed;
    private double amount;
    private String paymentStatus;


    public Bill(int billNum, int customerId, String month, double unitsConsumed, double amount, String paymentStatus) {
		super();
		this.billNum = billNum;
		this.customerId = customerId;
		this.month = month;
		this.unitsConsumed = unitsConsumed;
		this.amount = amount;
		this.paymentStatus = paymentStatus;
	}

	public int getBillNum() {
		return billNum;
	}

	public void setBillNum(int billNum) {
		this.billNum = billNum;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public double getUnitsConsumed() {
		return unitsConsumed;
	}

	public void setUnitsConsumed(double unitsConsumed) {
		this.unitsConsumed = unitsConsumed;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	// Method to generate a new bill and insert it into the database
	public static void generateBill(Connection connection, Scanner scanner) throws SQLException {
	    System.out.print("Enter customer ID: ");
	    int customerId = scanner.nextInt();

	    System.out.print("Enter month (e.g., Jan, Feb, ...): ");
	    String month = scanner.next();

	    // Check if a bill already exists for the given customer and month
	    if (isBillGeneratedForMonth(connection, customerId, month)) {
	        System.out.println("Bill already generated for customer ID " + customerId + " and month " + month);
	        return;
	    }

	    System.out.print("Enter units consumed: ");
	    double unitsConsumed = scanner.nextDouble();

	    // Calculate bill amount using the formula
	    double billAmount = calculateBillAmount(unitsConsumed);

	    // Insert new bill record into the database
	    String insertBillQuery = "INSERT INTO bill (customer_id, month, units_consumed, amount, payment_status) VALUES (?, ?, ?, ?, ?)";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(insertBillQuery)) {
	        preparedStatement.setInt(1, customerId);
	        preparedStatement.setString(2, month);
	        preparedStatement.setDouble(3, unitsConsumed);
	        preparedStatement.setDouble(4, billAmount);
	        preparedStatement.setString(5, "Unpaid"); // Initially set payment status as "Unpaid"
	        preparedStatement.executeUpdate();
	        System.out.println("Bill generated successfully!");
	    }
	}

	// Method to check if a bill already exists for a given customer and month
	private static boolean isBillGeneratedForMonth(Connection connection, int customerId, String month) throws SQLException {
	    String selectBillQuery = "SELECT * FROM bill WHERE customer_id = ? AND month = ?";
	    try (PreparedStatement preparedStatement = connection.prepareStatement(selectBillQuery)) {
	        preparedStatement.setInt(1, customerId);
	        preparedStatement.setString(2, month);
	        try (ResultSet resultSet = preparedStatement.executeQuery()) {
	            return resultSet.next(); // If a record is found, it means bill is already generated for the given customer and month
	        }
	    }
	}
    // Method to calculate the bill amount based on the formula
    private static double calculateBillAmount(double unitsConsumed) {
        // Implement the formula for bill calculation based on units consumed
        // Formula:
        // The first 100 units are free
        // Next 100 units are charged at Rs. 1.5 per unit
        // Next 300 units are charged at Rs. 2 per unit
        // Remaining units are charged at Rs. 3 per unit

        double billAmount = 0.0;
        if (unitsConsumed > 0) {
            if (unitsConsumed <= 100) {
                billAmount = 0.0; // First 100 units are free
            } else if (unitsConsumed <= 200) {
                billAmount = (unitsConsumed - 100) * 1.5; // Rs. 1.5 per unit for next 100 units
            } else if (unitsConsumed <= 500) {
                billAmount = 100 * 1.5 + (unitsConsumed - 200) * 2; // Rs. 1.5 per unit for first 100 units, Rs. 2 per unit for next 300 units
            } else {
                billAmount = 100 * 1.5 + 300 * 2 + (unitsConsumed - 500) * 3; // Rs. 1.5 per unit for first 100 units, Rs. 2 per unit for next 300 units, Rs. 3 per unit for remaining units
            }
        }

        return billAmount;
    }

    // Method to view bill details
    public static void viewBillDetails(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();

        String selectBillQuery = "SELECT * FROM bill WHERE customer_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectBillQuery)) {
            preparedStatement.setInt(1, customerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("Bill Details:");
                    System.out.println("Bill Number: " + resultSet.getInt("bill_num"));
                    System.out.println("Customer ID: " + resultSet.getInt("customer_id"));
                    System.out.println("Month: " + resultSet.getString("month"));
                    System.out.println("Units Consumed: " + resultSet.getDouble("units_consumed"));
                    System.out.println("Amount: " + resultSet.getDouble("amount"));
                    System.out.println("Payment Status: " + resultSet.getString("payment_status"));
                    System.out.println("-----------------------------------");
                }
            }
        }
    }

    // Method to update payment status
    public static void updatePaymentStatus(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter bill number: ");
        int billNumber = scanner.nextInt();

        System.out.print("Enter new payment status (Paid/Unpaid): ");
        String paymentStatus = scanner.next();

        // Validate the payment status input to either "Paid" or "Unpaid"
        if (!paymentStatus.equalsIgnoreCase("Paid") && !paymentStatus.equalsIgnoreCase("Unpaid")) {
            System.out.println("Invalid payment status. Please enter 'Paid' or 'Unpaid'.");
            return;
        }

        // Update the payment status in the database
        String updatePaymentQuery = "UPDATE bill SET payment_status = ? WHERE bill_num = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updatePaymentQuery)) {
            preparedStatement.setString(1, paymentStatus);
            preparedStatement.setInt(2, billNumber);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Payment status updated successfully!");
            } else {
                System.out.println("Bill with number " + billNumber + " not found.");
            }
        }
    }

}