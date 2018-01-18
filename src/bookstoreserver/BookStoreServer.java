package bookstoreserver;

import components.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookStoreServer {

    private static final String SERVER_IP_ADDRESS = ServerInfo.SERVER_IP_ADDRESS;
    private static final int SERVER_PORT = ServerInfo.SERVER_PORT;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT, 50, InetAddress.getByName(SERVER_IP_ADDRESS));
            if (serverSocket == null) {
                System.err.println("Can't Start server");
                return;
            }
            int cnt = 1;
            while (true) {
                System.out.println("waiting for connection...");

                Socket clientConnection = serverSocket.accept();

                System.out.println("connection " + cnt);
                cnt++;

                ObjectInputStream ois = new ObjectInputStream(clientConnection.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(clientConnection.getOutputStream());
                oos.flush();

                boolean fine = true;
                String exceptionMsg = "";

                try {
                    Commands command = (Commands) ois.readObject();

                    System.out.println(command);

                    oos.writeObject(Boolean.TRUE);
                    oos.flush();

                    if (null != command) {
                        switch (command) {
                            case VALIDATE_LOGIN:
                                if (validateLogin(ois)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("Can't validate user login!");
                                    oos.flush();
                                }
                                break;
                            case CUSTOMER_REGISTER:
                                if (validateCustomerReg(ois)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("User Already Exists.");
                                    oos.flush();
                                }
                                break;
                            case PUBLISHER_REGISTER:
                                if (validatePublisherReg(ois)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("User Already Exists.");
                                    oos.flush();
                                }
                                break;
                            case USER_REGISTER:
                                if (validateReg(ois)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("User Already Exists.");
                                    oos.flush();
                                }
                                break;

                            case ADD_BOOK:
                                Boolean[] ex = {Boolean.FALSE};
                                if (addBook(ois, ex)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject(
                                            (ex[0] ? "Book already exists\n" : "") + "Can't add book."
                                    );
                                    oos.flush();
                                }
                                break;

                            case GET_USER:
                                User user = getUser(ois);
                                oos.writeObject(Boolean.TRUE);
                                oos.flush();
                                oos.writeObject(user);
                                oos.flush();
                                break;
                            case LOAD_SECURITY_QUESTIONS:
                                Pair<ArrayList<Integer>, ArrayList<String>> list
                                        = getSecurityQuestions();
                                oos.writeObject(Boolean.TRUE);
                                oos.flush();
                                oos.writeObject(list);
                                oos.flush();
                                break;
                            case UPDATE_PASSWORD:
                                boolean f = updatePassword(ois);
                                if (f) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("Update password failed.");
                                }
                            case GET_ALL_AUTHOR_CATEGORY:
                                Pair<ArrayList<String>, ArrayList<String>> list2
                                        = getAuthorCategoryList();
                                oos.writeObject(Boolean.TRUE);
                                oos.flush();
                                oos.writeObject(list2);
                                oos.flush();
                                break;
                            case GET_BOOKS_LIST:
                                ArrayList<Book> booklist = getBookList(ois);
                                if (booklist != null) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                    oos.writeObject(booklist);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("error in book query");
                                    oos.flush();
                                }
                                break;
                            case DELETE_BOOK:
                                if (deleteBook(ois)) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("Can't delete book.");
                                    oos.flush();
                                }
                                break;
                            case GET_ALL_TITLE_PUBLISHER:
                                Pair<ArrayList<String>, ArrayList<String>> list3
                                        = getTitlePublisherList();
                                oos.writeObject(Boolean.TRUE);
                                oos.flush();
                                oos.writeObject(list3);
                                oos.flush();
                                break;
                            case GET_BOOKS_LIST_2:
                                ArrayList<Book> booklist2 = getBookList2(ois);
                                if (booklist2 != null) {
                                    oos.writeObject(Boolean.TRUE);
                                    oos.flush();
                                    oos.writeObject(booklist2);
                                    oos.flush();
                                } else {
                                    oos.writeObject(Boolean.FALSE);
                                    oos.flush();
                                    oos.writeObject("error in book query");
                                    oos.flush();
                                }
                                break;
                            default:
                                System.err.println("Unknown Command.");
                                break;
                        }
                    }
                } catch (IOException ex) {
                    exceptionMsg += ex.toString();
                    fine = false;
                    ex.printStackTrace();
                } catch (ClassNotFoundException | SQLException ex) {
                    exceptionMsg += ex.toString();
                    ex.printStackTrace();
                    fine = false;
                }

                System.err.println("Fine: " + fine);

                if (!fine) {
                    oos.writeObject(Boolean.FALSE);
                    oos.flush();
                    oos.writeObject(exceptionMsg);
                    oos.flush();
                }
                ois.close();
                oos.close();
            }
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean validateLogin(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        User user = (User) ois.readObject();

        String email = user.getUserEmail();
        String pass = user.getPassword();
        UserType userType = user.getUserType();
        return DataBaseHandler.findUser(email, pass, userType);
    }

    private static boolean validateReg(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        User user = (User) ois.readObject();

        String name = user.getUserName();
        String email = user.getUserEmail();
        String pass = user.getPassword();
        UserType userType = user.getUserType();

        return !DataBaseHandler.isExits(email, userType) && DataBaseHandler.addUser(userType, name, email, pass) >= 0;
    }

    private static boolean validateCustomerReg(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        Customer user = (Customer) ois.readObject();

        return !DataBaseHandler.isExits(user.getUserEmail(), user.getUserType())
                && DataBaseHandler.addCustomer(user) >= 0;
    }

    private static boolean validatePublisherReg(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        Publisher user = (Publisher) ois.readObject();

        return !DataBaseHandler.isExits(user.getUserEmail(), user.getUserType())
                && DataBaseHandler.addPublisher(user) >= 0;
    }

    private static User getUser(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        User user = (User) ois.readObject();

        String email = user.getUserEmail();
        UserType type = user.getUserType();

        return DataBaseHandler.getUserDetails(type, email);
    }

    private static boolean addBook(ObjectInputStream reader, Boolean[] exists) throws IOException, SQLException, ClassNotFoundException {
        Book book = (Book) reader.readObject();

        if (DataBaseHandler.isBookExists(book.getBookTitle(), book.getAuthorName(), book.getPublisherID(), book.getBookCategory())) {
            exists[0] = Boolean.TRUE;
            return false;
        }
        return DataBaseHandler.addBook(book.getBookTitle(), book.getAuthorName(), book.getPublisherID(),
                book.getBookCategory(), book.getStock(), book.getPrice(), book.getDescription());
    }

    private static Pair<ArrayList<Integer>, ArrayList<String>> getSecurityQuestions() throws SQLException, ClassNotFoundException {
        return DataBaseHandler.getAllSecurityQuestionsList();
    }

    private static boolean updatePassword(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
        return DataBaseHandler.updatePassword((User) ois.readObject());
    }

    private static Pair<ArrayList<String>, ArrayList<String>> getAuthorCategoryList() throws SQLException, ClassNotFoundException {
        return DataBaseHandler.getAllAuthorCategoryList();
    }

    private static ArrayList<Book> getBookList(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
        Book book = (Book) ois.readObject();
        return DataBaseHandler.getBookList(book.getBookTitle(), book.getPublisherID(), book.getAuthorName(), book.getBookCategory());
    }

    private static boolean deleteBook(ObjectInputStream ois) throws SQLException, ClassNotFoundException, IOException {
        Book b = (Book) ois.readObject();
        return DataBaseHandler.deleteBook(b);
    }

    private static Pair<ArrayList<String>, ArrayList<String>> getTitlePublisherList() throws SQLException, ClassNotFoundException {
        return DataBaseHandler.getAllTitlePublisherList();
    }

    private static ArrayList<Book> getBookList2(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
        BookSub bookSub = (BookSub) ois.readObject();
        String title = bookSub.getBookSubTitle().length() == 0 ? null : bookSub.getBookSubTitle();
        int seller = -1;
        if (bookSub.getBookSubPublisher().length() != 0) {
            seller = DataBaseHandler.getPublisherID(bookSub.getBookSubPublisher());
        }
        String author = bookSub.getBookSubAuthor().length() == 0 ? null : bookSub.getBookSubAuthor();
        String category = bookSub.getBookSubCategory().length() == 0 ? null : bookSub.getBookSubCategory();

        return DataBaseHandler.getBookList(title, seller, author, category);
    }
}
