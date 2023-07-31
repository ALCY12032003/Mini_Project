package project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Customer {
    private int customerNum;
    private String name;
    private long phone;
    private String address;
    private long aadharNumber;

    // Constructor and getter/setter methods
    public int getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(int customerNum) {
        this.customerNum = customerNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(long aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

 // Method to insert a new customer record into the database
    public void insertCustomer(Connection connection) throws SQLException {
        // Check if a customer with the same Aadhar number already exists
        if (isCustomerExists(connection, aadharNumber)) {
            System.out.println("Customer with Aadhar number " + aadharNumber + " already exists.");
            return;
        }

        String insertCustomerQuery = "INSERT INTO customer (name, phone, address, aadhar_number) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertCustomerQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, phone);
            preparedStatement.setString(3, address);
            preparedStatement.setLong(4, aadharNumber);
            preparedStatement.executeUpdate();
            System.out.println("New customer added successfully!");
            
        }
    }

    // Method to check if a customer with the same Aadhar number already exists in the database
    private boolean isCustomerExists(Connection connection, long aadharNumber) throws SQLException {
        String selectCustomerQuery = "SELECT * FROM customer WHERE aadhar_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectCustomerQuery)) {
            preparedStatement.setLong(1, aadharNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); 
            }
        }
    }

    // Method to create a new customer object from user input
    public static Customer addNewCustomer(Scanner scanner) {
        Customer newCustomer = new Customer();

        System.out.print("Enter customer name: ");
        scanner.nextLine();
        newCustomer.setName(scanner.nextLine());

        System.out.print("Enter phone number: ");
        newCustomer.setPhone(Long.parseLong(scanner.nextLine()));

        System.out.print("Enter address: ");
        newCustomer.setAddress(scanner.nextLine());

        System.out.print("Enter Aadhar number: ");
        newCustomer.setAadharNumber(Long.parseLong(scanner.nextLine()));

        return newCustomer;
    }

    // Method to view customer details
    public static void viewCustomerDetails(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();

        String selectCustomerQuery = "SELECT * FROM customer WHERE customer_num = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectCustomerQuery)) {
            preparedStatement.setInt(1, customerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Customer Details:");
                    System.out.println("Customer ID: " + resultSet.getInt("customer_num"));
                    System.out.println("Name: " + resultSet.getString("name"));
                    System.out.println("Phone: " + resultSet.getLong("phone"));
                    System.out.println("Address: " + resultSet.getString("address"));
                    System.out.println("Aadhar Number: " + resultSet.getLong("aadhar_number"));
                } else {
                    System.out.println("Customer with ID " + customerId + " not found.");
                }
            }
        }
    }

}
