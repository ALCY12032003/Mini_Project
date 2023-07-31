package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class ElectricityBillCalculator {
    public static void main(String[] args) {
        try {
            // Establish the connection to the MySQL database
            String url = "jdbc:mysql://127.0.0.1:3306/EB_bill";
            String username = "root";
            String password = "120624";
            Connection connection = DriverManager.getConnection(url, username, password);

            Scanner scanner = new Scanner(System.in);

            int choice;
            do {
                // Display menu options
                System.out.println("Welcome To POWERBILLPRO");
                System.out.println("・‥…━━━━━━━☆☆━━━━━━━…‥・");
                System.out.println("Choose A Menu:");
                System.out.println("1. Add a new customer");     // Encapsulation: Accessing methods of Customer class
                System.out.println("2. Generate bill");         // Abstraction: Accessing method of Bill class
                System.out.println("3. View customer details"); // Abstraction: Accessing method of Customer class
                System.out.println("4. View bill details");     // Abstraction: Accessing method of Bill class
                System.out.println("5. Update payment status"); // Abstraction: Accessing method of Bill class
                System.out.println("6. Exit");
                System.out.println("・‥…━━━━━━━☆☆━━━━━━━…‥・");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        // Add a new customer
                        Customer newCustomer = Customer.addNewCustomer(scanner);  // Encapsulation: Creating Customer object
                        newCustomer.insertCustomer(connection);                   // Encapsulation: Accessing method of Customer class
                        break;
                    case 2:
                        // Generate bill
                        Bill.generateBill(connection, scanner);                   // Abstraction: Accessing method of Bill class
                        break;
                    case 3:
                        // View customer details
                        Customer.viewCustomerDetails(connection, scanner);        // Abstraction: Accessing method of Customer class
                        break;
                    case 4:
                        // View bill details
                        Bill.viewBillDetails(connection, scanner);               // Abstraction: Accessing method of Bill class
                        break;
                    case 5:
                        // Update payment status
                        Bill.updatePaymentStatus(connection, scanner);           // Abstraction: Accessing method of Bill class
                        break;
                    case 6:
                        System.out.println("Exiting...Thank You");
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } while (choice != 6);

            scanner.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
