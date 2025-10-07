package src;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class RestaurantReservationSystem {

   private static final String url ="jdbc:mysql://localhost:3306/restaurant_db";
   private static final String username="root";
   private static final String password="dipanwita@33";


    public static void main(String[] args) throws ClassNotFoundException,SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            //Connection Established
            Connection connection = DriverManager.getConnection(url, username, password);
            while (true) {

                System.out.println();
                System.out.println("~~~~ WELCOME TO DAILY RESTAURANT RESERVATION TRACKER ~~~~");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a table");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Table Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Update Reservation Status");
                System.out.println("6. Delete Reservation");
                System.out.println("7. Search Reservation by Guest Name");
                System.out.println("8. Search Reservation by Date");
                System.out.println("9. Show All Reservations for Today");
                System.out.println("10. Show Available Tables");
                System.out.println("11. Check Reservation Status By Guest Name");
                System.out.println("12. Add Seating Preference");
                System.out.println("13. Add Special Requests");
                System.out.println("14. Cancel All Reservations");
                System.out.println("15. Exit");
                System.out.print ("Choose an option : ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        reserveTable(connection, sc);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getTableNumber(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        updateReservationStatus(connection, sc);
                        break;
                    case 6:
                        deleteReservation(connection, sc);
                        break;
                    case 7:
                        searchReservationByGuest(connection, sc);
                        break;
                    case 8:
                        searchReservationByDate(connection, sc);
                        break;
                    case 9:
                        showReservationToday(connection, sc);
                        break;
                    case 10:
                        availableTables(connection, sc);
                        break;
                    case 11:
                        checkReservationStatusByGuestName(connection, sc);
                        break;
                    case 12:
                        updateSeatingPreference(connection, sc);
                        break;
                    case 13:
                        updateSpecialRequest(connection, sc);
                        break;
                    case 14:
                        cancelAllReservations(connection);
                        break;
                    case 15:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.Try Again after sometime!!");

                }

            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void reserveTable(Connection connection,Scanner sc){
     try{
         System.out.print("Enter Guest Name : ");
         String guestName=sc.next();
         sc.nextLine();
         System.out.print("Enter Table Number : ");
         int tableNumber=sc.nextInt();
         System.out.print("Enter Contact Number : ");
         String contactNumber=sc.next();

         String sql = "INSERT INTO reservations (guest_name, table_number, contact_number) " +
                 "VALUES ('" + guestName + "', " + tableNumber + ", '" + contactNumber + "')";

         try(Statement statement =connection.createStatement()){
           int affectedRows = statement.executeUpdate(sql);

           if(affectedRows > 0){
               System.out.println("Reservation Successful!");
           }
           else {
               System.out.println("Reservation failed");
           }
         }
     }catch (SQLException e){
         e.printStackTrace();
     }
    }

    private static void viewReservation(Connection connection) throws SQLException {
         String sql = "SELECT reservation_id, guest_name, table_number, contact_number, reservation_date, status, seating_pref, special_requests FROM reservations";

         try(Statement statement = connection.createStatement()){
             ResultSet resultSet = statement.executeQuery(sql);

             System.out.println(" CURRENT RESERVATIONS : ");
             System.out.println("-----------------+--------------------+------------------+-------------------+---------------------------------------------------------------------------------------------");
             System.out.println("| Reservation ID | Guest              |  Table Number    |  Contact Number   |   Reservation Date & Time  |    Status      |   Seating Preference  |   Special Requests  |");
             System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------");


             while(resultSet.next()){
                 int reservationId =  resultSet.getInt("reservation_id");
                 String guestName = resultSet.getString("guest_name");
                 int tableNumber = resultSet.getInt("table_number");
                 String contactNumber = resultSet.getString("contact_number");
                 String reservationDate = resultSet.getTimestamp("reservation_date").toString();
                 String status = resultSet.getString("status");
                 String seating_pref = resultSet.getString("seating_pref");
                 String special_requests = resultSet.getString("special_requests");



                 //format and display
                 System.out.printf("| %-14d |   %-15s  |  %-12d  | %18s  | %19s      | %-14s   |  %-14s     |  %-14s     |\n", reservationId,guestName,tableNumber,contactNumber,reservationDate,status,seating_pref,special_requests);

             }


             System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

         }
    }

    private static void getTableNumber(Connection connection, Scanner sc){
        try{
            System.out.println("Enter Reservation ID : ");
            int reservationId = sc.nextInt();
            System.out.println("Enter Guest Name : ");
            String guestName = sc.next();

            String sql = "SELECT table_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(sql);

                if(resultSet.next()){
                    int tableNumber = resultSet.getInt("table_number");
                    System.out.println("Table Number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + tableNumber);
                }
                else{
                    System.out.println("Reservation not found for the given ID and Guest Name.");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

     private static void updateReservation(Connection connection,Scanner sc){
        try{
            System.out.println("Enter Reservation ID to Update : ");
            int reservationId =sc.nextInt();
            sc.nextLine();

            if(!reservationExists(connection,reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.println("Enter New Guest Name : ");
            String newGuestName = sc.nextLine();
            System.out.print("Enter New Table Number : ");
            int newTableNumber = sc.nextInt();
            System.out.print("Enter New Contact Number : ");
            String newContactNumber = sc.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "table_number = " + newTableNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try(Statement statement =connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation updated successfully!");
                }else{
                    System.out.println("Reservation update failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
     }

    private static void updateReservationStatus(Connection connection, Scanner sc) {
        System.out.print("Enter Reservation ID : ");
        int reservationId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new status (Confirmed / Pending / Cancelled): ");
        String newStatus = sc.nextLine();

        String sql = "UPDATE reservations SET status = '" + newStatus + "' WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int rowsUpdated = statement.executeUpdate(sql);

            if (rowsUpdated > 0) {
                System.out.println("Reservation status updated to: " + newStatus);
            } else {
                System.out.println("No reservation found with ID: " + reservationId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
        }
    }


    private static void deleteReservation(Connection connection,Scanner sc){
        try{
            System.out.print("Enter Reservation ID to delete : ");
            int reservationId=sc.nextInt();

            if(!reservationExists(connection, reservationId)){
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " +reservationId;

            try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

                if(affectedRows > 0){
                    System.out.println("Reservation deleted successfully!!");
                }else{
                    System.out.println("Reservation deletion failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
     }

    private static void searchReservationByGuest(Connection connection, Scanner sc) {
        System.out.print("Enter Guest Name : ");
        String guestName = sc.nextLine();
        sc.nextLine();

        String sql = "SELECT * FROM reservations " +
                "WHERE guest_name LIKE '%" + guestName + "%' " +
                "ORDER BY reservation_date DESC LIMIT 1";


        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            boolean found = false;
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String name = resultSet.getString("guest_name");
                int tableNumber = resultSet.getInt("table_number");
                String contact = resultSet.getString("contact_number");
                String date = resultSet.getString("reservation_date");
                String seating = resultSet.getString("seating_pref");
                String special = resultSet.getString("special_requests");
                String status = resultSet.getString("status");

                System.out.println("Reservation ID : " + reservationId);
                System.out.println("Guest Name     : " + name);
                System.out.println("Table Number   : " + tableNumber);
                System.out.println("Contact Number : " + contact);
                System.out.println("Date & Time    : " + date);
                System.out.println("Seating Pref   : " + (seating == null ? "-" : seating));
                System.out.println("Special Request  : " + (special == null ? "-" : special));
                System.out.println("Status         : " + (status == null ? "Confirmed" : status));
                System.out.println("--------------------------------------------------");
                found = true;
            }

            if (!found) {
                System.out.println("No reservations found for guest: " + guestName);
            }

        } catch (SQLException e) {
            System.err.println("Error searching by guest name: " + e.getMessage());
        }
    }




    private static void searchReservationByDate(Connection connection,Scanner sc) {
        System.out.print("Enter Reservation Date (YYYY-MM-DD) : ");
        String dateInput = sc.nextLine();



        String sql = "SELECT * FROM reservations " +
                "WHERE DATE(reservation_date) = '" + dateInput + "' " +
                "ORDER BY reservation_date";


        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            boolean found = false;
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String name = resultSet.getString("guest_name");
                int tableNumber = resultSet.getInt("table_number");
                String contact = resultSet.getString("contact_number");
                String date = resultSet.getString("reservation_date");
                String seating = resultSet.getString("seating_pref");
                String special = resultSet.getString("special_requests");
                String status = resultSet.getString("status");

                System.out.println("Reservation ID : " + reservationId);
                System.out.println("Guest Name     : " + name);
                System.out.println("Table Number   : " + tableNumber);
                System.out.println("Contact Number : " + contact);
                System.out.println("Date & Time    : " + date);
                System.out.println("Seating Pref   : " + (seating == null ? "-" : seating));
                System.out.println("Special Notes  : " + (special == null ? "-" : special));
                System.out.println("Status         : " + (status == null ? "Confirmed" : status));
                System.out.println("--------------------------------------------------");
                found = true;
            }

            if (!found) {
                System.out.println("No reservations found for date: " + dateInput);
            }

        } catch (SQLException e) {
            System.err.println("Error searching by date: " + e.getMessage());
        }

    }


    private static void showReservationToday(Connection connection,Scanner sc) {

        String sql = "SELECT * FROM reservations " +
                "WHERE DATE(reservation_date) = CURDATE() " +
                "ORDER BY reservation_date";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            boolean found = false;
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String name = resultSet.getString("guest_name");
                int tableNumber = resultSet.getInt("table_number");
                String contact = resultSet.getString("contact_number");
                String date = resultSet.getString("reservation_date");
                String seating = resultSet.getString("seating_pref");
                String special = resultSet.getString("special_requests");
                String status = resultSet.getString("status");

                System.out.println("Reservation ID : " + reservationId);
                System.out.println("Guest Name     : " + name);
                System.out.println("Table Number   : " + tableNumber);
                System.out.println("Contact Number : " + contact);
                System.out.println("Date & Time    : " + date);
                System.out.println("Seating Pref   : " + (seating == null ? "-" : seating));
                System.out.println("Special Notes  : " + (special == null ? "-" : special));
                System.out.println("Status         : " + (status == null ? "Confirmed" : status));
                System.out.println("--------------------------------------------------");
                found = true;
            }

            if (!found) {
                System.out.println("No reservations found for today.");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching today's reservations: " + e.getMessage());
        }
    }



    private static void availableTables(Connection connection,Scanner sc){

        final int TOTAL_TABLES = 20;  // Change this as per your restaurant setup

        String sql = "SELECT table_num FROM (" +
                "  SELECT 1 AS table_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 " +
                "  UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 " +
                "  UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 " +
                "  UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 " +
                ") AS all_tables " +
                "WHERE table_num NOT IN ( " +
                "  SELECT table_number FROM reservations WHERE DATE(reservation_date) = CURDATE() " +
                ")";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            boolean found = false;
            System.out.println("Available Tables for Today:");
            while (resultSet.next()) {
                int tableNum = resultSet.getInt("table_num");
                System.out.println("Table " + tableNum + " is available.");
                found = true;
            }

            if (!found) {
                System.out.println("No available tables today. All are reserved.");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching available tables: " + e.getMessage());
        }
    }




    private static void checkReservationStatusByGuestName(Connection connection, Scanner sc) {
        System.out.print("Enter Guest Name : ");
        String guestName = sc.nextLine();

        String sql = "SELECT * FROM reservations WHERE guest_name = '" + guestName + "' ORDER BY reservation_date DESC";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            boolean found = false;
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                int tableNumber = resultSet.getInt("table_number");
                String status = resultSet.getString("status");

                System.out.println("Reservation ID : " + reservationId);
                System.out.println("Guest Name     : " + guestName);
                System.out.println("Table Number   : " + tableNumber);
                System.out.println("Status         : " + (status == null ? "Pending" : status));
                System.out.println("--------------------------------------------------");

                found = true;
            }

            if (!found) {
                System.out.println("No reservations found for guest: " + guestName);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching reservation status: " + e.getMessage());
        }
    }


    private static void updateSeatingPreference(Connection connection, Scanner sc) {
        System.out.print("Enter Reservation ID : ");
        int reservationId = sc.nextInt();
        sc.nextLine(); // clear buffer

        System.out.print("Enter new Seating Preference (e.g., Window, Corner, Near Stage): ");
        String newSeating = sc.nextLine();

        String sql = "UPDATE reservations SET seating_pref = '" + newSeating + "' WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int rowsUpdated = statement.executeUpdate(sql);

            if (rowsUpdated > 0) {
                System.out.println("Seating preference updated successfully to: " + newSeating);
            } else {
                System.out.println("No reservation found with ID: " + reservationId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating seating preference: " + e.getMessage());
        }
    }

    private static void updateSpecialRequest(Connection connection, Scanner sc) {
        System.out.print("Enter Reservation ID : ");
        int reservationId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new Special Request: ");
        String newRequest = sc.nextLine();

        String sql = "UPDATE reservations SET special_requests = '" + newRequest + "' WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int rowsUpdated = statement.executeUpdate(sql);

            if (rowsUpdated > 0) {
                System.out.println("Special request updated successfully to: " + newRequest);
            } else {
                System.out.println("No reservation found with ID: " + reservationId);
            }

        } catch (SQLException e) {
            System.err.println("Error updating special request: " + e.getMessage());
        }
    }



    private static void cancelAllReservations(Connection connection) {
        String sql = "UPDATE reservations SET status = 'Cancelled'";

        try (Statement statement = connection.createStatement()) {
            int rowsUpdated = statement.executeUpdate(sql);

            if (rowsUpdated > 0) {
                System.out.println("All reservations in the system have been cancelled.");
            } else {
                System.out.println("No reservations found in the table.");
            }

        } catch (SQLException e) {
            System.err.println("Error cancelling all reservations: " + e.getMessage());
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId){
        try{
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = "+reservationId;

            try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){

                return resultSet.next();
                }
            }catch (SQLException e){
                e.printStackTrace();
                return false;
            }
        }



   public static void exit() throws InterruptedException{
       System.out.print("Exiting System");
       int i=6;

       while(i!=0) {
           System.out.print(". ");
           Thread.sleep(500);
           i--;
       }
       System.out.println();
       System.out.println(" ++++++++ THANK YOU FOR USING DAILY RESTAURANT RESERVATION TRACKER ++++++++");

    }
}