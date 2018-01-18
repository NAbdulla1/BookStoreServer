/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstoreserver;

import components.Book;
import components.Customer;
import components.Pair;
import components.Publisher;
import components.User;
import components.UserType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author nayon
 */
public class DataBaseHandler {

    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookStore";
    private static final String USER_NAME = "javaApp";
    private static final String PASSWORD = "books";
    private static boolean driverLoaded = false;

    protected static boolean loadDriver() throws ClassNotFoundException {
        if (!driverLoaded) {
            driverLoaded = true;
            System.out.println("Trying to load driver...");
            Class.forName(DRIVER_NAME);
            System.out.println("Driver Loaded successfully...");
        }
        return true;
    }

    private static Connection connectToDB() throws SQLException, ClassNotFoundException {
        loadDriver();
        Connection connction = null;
        connction = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
        return connction;
    }

    public static void connectionTest() throws SQLException, ClassNotFoundException {
        Connection c = connectToDB();
        System.out.println(c.toString());
    }

    public static boolean findUser(String email, String pass, UserType type) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return false;
        }

        Statement statement = connection.createStatement();
        String query = "SELECT * FROM %s WHERE Email = \'" + email + "\' AND Password = \'" + pass + "\';";
        if (UserType.CUSTOMER == type) {
            query = String.format(query, "buyerTable");
        } else if (UserType.PUBLISHER == type) {
            query = String.format(query, "sellerTable");
        }
        ResultSet resultSet = statement.executeQuery(query);
        boolean f = false;
        while (!f && resultSet.next()) {
            f = f || (resultSet.getString("Email").equals(email) && resultSet.getString("Password").equals(pass));
        }
        statement.close();
        connection.close();
        return f;
    }

    public static boolean isExits(String email, UserType type) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return false;
        }

        Statement statement = connection.createStatement();
        String query = "SELECT Email FROM %s WHERE Email=\'" + email + "\';";
        if (UserType.CUSTOMER == type) {
            query = String.format(query, "buyerTable");
        } else {
            query = String.format(query, "sellerTable");
        }
        ResultSet resultSet = statement.executeQuery(query);
        boolean f = false;
        while (resultSet.next()) {
            f = f || (resultSet.getString("Email").equals(email));
        }
        statement.close();
        connection.close();
        return f;
    }

    public static int addUser(UserType type, String name, String email, String pass) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return -1;
        }

        Statement statement = connection.createStatement();
        String query = "INSERT INTO %s(Name, Email, Password) VALUES(\'%s\', \'%s\', \'%s\');";
        query = String.format(query, ((UserType.CUSTOMER == type) ? "buyerTable" : "sellerTable"), name, email, pass);
        int executeUpdate = statement.executeUpdate(query);
        connection.close();
        statement.close();
        return executeUpdate;
    }

    public static int addCustomer(Customer customer) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return -1;
        }

        String name = customer.getUserName();
        String email = customer.getUserEmail();
        String pass = customer.getPassword();
        String address = customer.getAddress();
        int seqID = customer.getSecurityQuestionID();
        String answer = customer.getSecurityQuestionAnswer();
        UserType userType = customer.getUserType();

        Statement statement = connection.createStatement();
        String query = "INSERT INTO buyerTable(Name, Email, Password, Address, SecurityQuestionID, SecurityAnswer)"
                + "     VALUES(\'%s\', \'%s\', \'%s\', \'%s\', %d, \'%s\');";
        query = String.format(query, name, email, pass, address, seqID, answer);
        int executeUpdate = statement.executeUpdate(query);
        connection.close();
        statement.close();
        return executeUpdate;
    }

    public static int addPublisher(Publisher publisher) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return -1;
        }

        String name = publisher.getUserName();
        String email = publisher.getUserEmail();
        String pass = publisher.getPassword();
        String phone = publisher.getPhoneNumber();
        int seqID = publisher.getSecurityQuestionID();
        String answer = publisher.getSecurityQuestionAnswer();

        Statement statement = connection.createStatement();
        String query = "INSERT INTO sellerTable(Name, Email, Password, PhoneNumber, SecurityQuestionID, SecurityAnswer)"
                + "     VALUES(\'%s\', \'%s\', \'%s\', \'%s\', %d, \'%s\');";
        query = String.format(query, name, email, pass, phone, seqID, answer);
        int executeUpdate = statement.executeUpdate(query);
        connection.close();
        statement.close();
        return executeUpdate;
    }

    public static User getUserDetails(UserType type, String email) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            System.out.println("connection failed!");
            return null;
        }
        Statement statement = connection.createStatement();
        String query = String.format("SELECT * FROM %s WHERE Email=\'" + email + "\';",
                (UserType.CUSTOMER == type) ? "buyerTable" : "sellerTable");

        ResultSet resultSet = statement.executeQuery(query);
        User user = null;
        if (resultSet.next()) {
            if (type == UserType.CUSTOMER) {
                user = new Customer(
                        resultSet.getString("Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Password"),
                        UserType.CUSTOMER,
                        resultSet.getString("Address"),
                        resultSet.getInt("SecurityQuestionID"),
                        resultSet.getString("SecurityAnswer")
                );
                user.setUserID(resultSet.getInt("ID"));
            } else if (type == UserType.PUBLISHER) {
                user = new Publisher(
                        resultSet.getString("Name"),
                        resultSet.getString("Email"),
                        resultSet.getString("Password"),
                        resultSet.getString("PhoneNumber"),
                        resultSet.getInt("SecurityQuestionID"),
                        resultSet.getString("SecurityAnswer")
                );
                user.setUserID(resultSet.getInt("ID"));
            }
        }
        connection.close();
        statement.close();
        return user;
    }

    public static boolean isBookExists(String title, String authorName, int sellerID, String category) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return false;
        }
        int authorID = getAuthorID(connection, authorName);
        int categoryID = getCategoryID(connection, category);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Title, AuthorID, SellerID, CategoryID from bookTable "
                + "WHERE " + String.format("Title = \'%s\' AND AuthorID = %d "
                        + "AND SellerID = %d AND CategoryID = %d;", title, authorID, sellerID, categoryID));
        boolean f = false;
        while (resultSet.next()) {
            f = f || (resultSet.getString("Title").equals(title) && resultSet.getInt("AuthorID") == authorID
                    && resultSet.getInt("SellerID") == sellerID && resultSet.getInt("CategoryID") == categoryID);
        }
        statement.close();
        connection.close();
        return f;
    }

    public static boolean addBook(String title, String authorName, int sellerID, String category, int stock, double price, String descr) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return false;
        }
        int authorID = getAuthorID(connection, authorName);
        int categoryID = getCategoryID(connection, category);

        Statement statement = connection.createStatement();
        int rowCount = statement.executeUpdate("INSERT INTO bookTable(Title, AuthorID, SellerID, CategoryID, StockCount, Description, Price) "
                + String.format("VALUES(\'%s\', %d, %d, %d, %d, \'%s\', %.2f);",
                        title, authorID, sellerID, categoryID, stock, descr, price));
        connection.close();
        return rowCount > 0;
    }

    public static int getAuthorID(Connection connection, String name) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ID FROM AuthorTable "
                + "WHERE UPPER(Name)=\'" + name.toUpperCase() + "\';");
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        if (id != -1) {
            return id;
        }
        statement.executeUpdate("INSERT INTO AuthorTable(Name)"
                + " VALUES(\'" + name + "\');");
        resultSet = statement.executeQuery("SELECT ID FROM AuthorTable "
                + "WHERE UPPER(Name)=\'" + name.toUpperCase() + "\';");
        id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        return id;
    }

    public static int getCategoryID(Connection connection, String categoty) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ID FROM bookCategory "
                + "WHERE UPPER(Category)=\'" + categoty.toUpperCase() + "\';");
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        if (id != -1) {
            return id;
        }
        statement.executeUpdate("INSERT INTO bookCategory(Category)"
                + " VALUES(\'" + categoty + "\');");
        resultSet = statement.executeQuery("SELECT ID FROM bookCategory "
                + "WHERE UPPER(Category)=\'" + categoty.toUpperCase() + "\';");
        id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        return id;
    }

    public static int getPublisherID(String publisher) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return -1;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ID FROM sellerTable "
                + "WHERE UPPER(Name)=\'" + publisher.toUpperCase() + "\';");
        int id = -1;
        if (resultSet.next()) {
            id = resultSet.getInt("ID");
        }
        return id;
    }

    public static Pair<ArrayList<Integer>, ArrayList<String>> getAllSecurityQuestionsList() throws SQLException, ClassNotFoundException {
        Pair<ArrayList<Integer>, ArrayList<String>> list = new Pair<>(new ArrayList<Integer>(), new ArrayList<String>());
        Connection connection = connectToDB();
        if (connection == null) {
            return null;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM securityquestionstable;");
        while (resultSet.next()) {
            list.getFirst().add(resultSet.getInt("ID"));
            list.getSecond().add(resultSet.getString("Question"));
        }
        statement.close();
        connection.close();
        return list;
    }

    public static boolean updatePassword(User user) throws SQLException, ClassNotFoundException {
        Connection c = connectToDB();
        if (c == null) {
            return false;
        }
        Statement statement = c.createStatement();
        int rowCount = statement.executeUpdate(
                String.format("UPDATE %s SET Password=\'%s\' WHERE Email=\'%s\';",
                        user.getUserType() == UserType.CUSTOMER ? "buyerTable" : "sellerTable",
                        user.getPassword(),
                        user.getUserEmail()));
        statement.close();
        return rowCount == 1;
    }

    static Pair<ArrayList<String>, ArrayList<String>> getAllAuthorCategoryList() throws SQLException, ClassNotFoundException {
        Pair<ArrayList<String>, ArrayList<String>> list = new Pair<>(new ArrayList<String>(), new ArrayList<String>());
        Connection connection = connectToDB();
        if (connection == null) {
            return null;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Name FROM authorTable;");
        while (resultSet.next()) {
            list.getFirst().add(resultSet.getString("Name"));
        }
        statement.close();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT Category FROM bookCategory;");
        while (resultSet.next()) {
            list.getSecond().add(resultSet.getString("Category"));
        }
        statement.close();
        connection.close();
        return list;
    }

    static ArrayList<Book> getBookList(String title, int sellerID, String authorName, String category) throws SQLException, ClassNotFoundException {
        Connection connection = connectToDB();
        if (connection == null) {
            return null;
        }
        if (authorName == null && category == null && sellerID == -1 && title == null) {
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable;");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category == null && sellerID != -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE SellerID=" + sellerID + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName != null && category == null && sellerID == -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE AuthorID="
                    + getAuthorID(connection, authorName) + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category != null && sellerID == -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE CategoryID="
                    + getCategoryID(connection, category) + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category == null && sellerID == -1 && title != null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE Title LIKE \'%%" + title + "%%\';");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName != null && category != null && sellerID == -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + "AuthorID=" + getAuthorID(connection, authorName)
                    + " AND CategoryID=" + getCategoryID(connection, category) + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category != null && sellerID != -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + "SellerID=" + sellerID + " AND CategoryID=" + getCategoryID(connection, category) + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category == null && sellerID != -1 && title != null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + "SellerID=" + sellerID + " AND Title LIKE \'%%" + title + "%%\';");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName != null && category != null && sellerID != -1 && title == null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + "AuthorID=" + getAuthorID(connection, authorName)
                    + " AND CategoryID=" + getCategoryID(connection, category)
                    + " AND SellerID=" + sellerID + ";");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName == null && category != null && sellerID != -1 && title != null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + " AND CategoryID=" + getCategoryID(connection, category)
                    + " AND SellerID=" + sellerID
                    + " AND Title LIKE '%%" + title + "%%\';");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        } else if (authorName != null && category != null && sellerID != -1 && title != null) {//return all books of a seller
            Statement statement = connection.createStatement();
            //System.err.println(sellerID);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM bookTable WHERE "
                    + "AuthorID=" + getAuthorID(connection, authorName)
                    + " AND CategoryID=" + getCategoryID(connection, category)
                    + " AND SellerID=" + sellerID
                    + " AND Title LIKE \'%%" + title + "%%\';");
            ArrayList<Book> list = new ArrayList<Book>();
            while (resultSet.next()) {
                Book b = new Book(
                        resultSet.getString("Title"),
                        getAuthorName(connection, resultSet.getInt("AuthorID")),
                        sellerID,
                        getCategory(connection, resultSet.getInt("CategoryID")),
                        resultSet.getInt("StockCount"),
                        resultSet.getDouble("Price"),
                        resultSet.getString("Description")
                );
                //System.err.println(b);
                b.setBookID(resultSet.getInt("ID"));
                list.add(b);
            }
            return list;
        }
        return null;
    }

    static String getAuthorName(Connection c, int id) throws SQLException {
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Name FROM AuthorTable WHERE ID=" + id + ";");
        if (resultSet.next()) {
            return resultSet.getString("Name");
        }
        return null;
    }

    static String getCategory(Connection c, int id) throws SQLException {
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Category FROM BookCategory WHERE ID=" + id + ";");
        if (resultSet.next()) {
            return resultSet.getString("Category");
        }
        return null;
    }

    static boolean deleteBook(Book b) throws SQLException, ClassNotFoundException {
        Connection c = connectToDB();
        if (c == null) {
            return false;
        }
        int authorID = getAuthorID(c, b.getAuthorName());
        int categoryID = getCategoryID(c, b.getBookCategory());
        System.out.println("book id: " + b.getBookID());
        int r = c.createStatement().executeUpdate(
                String.format("DELETE FROM bookTable WHERE Title=\'%s\' AND "
                        + "AuthorID=%d AND SellerID=%d AND CategoryID=%d;",
                        b.getBookTitle(),
                        authorID,
                        b.getPublisherID(),
                        categoryID)
        );
        c.close();
        return r > 0;
    }

    static Pair<ArrayList<String>, ArrayList<String>> getAllTitlePublisherList() throws SQLException, ClassNotFoundException {
        Pair<ArrayList<String>, ArrayList<String>> list = new Pair<>(new ArrayList<String>(), new ArrayList<String>());
        Connection connection = connectToDB();
        if (connection == null) {
            return null;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT Title FROM bookTable;");
        while (resultSet.next()) {
            list.getFirst().add(resultSet.getString("Title"));
        }
        statement.close();
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT Name FROM sellerTable;");
        while (resultSet.next()) {
            list.getSecond().add(resultSet.getString("Name"));
        }
        statement.close();
        connection.close();
        return list;
    }
    /*
    static Pair<ArrayList<Integer>, ArrayList<String>> getAllPublishers() throws SQLException, ClassNotFoundException {
        Pair<ArrayList<Integer>, ArrayList<String>> list = new Pair<>(new ArrayList<Integer>(), new ArrayList<String>());
        Connection connection = connectToDB();
        if (connection == null) {
            return null;
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ID, Name FROM sellerTable;");
        while (resultSet.next()) {
            list.getFirst().add(resultSet.getInt("ID"));
            list.getSecond().add(resultSet.getString("Name"));
        }
        statement.close();
        connection.close();
        return list;
    }*/
}
