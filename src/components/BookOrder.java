package components;

import java.io.Serializable;

/**
 * Created by nayon on 13-Feb-18.
 */
public class BookOrder implements Serializable {

    private Customer orderedCustomer;
    private int orderedCopy;
    private Book orderedBook;

    public BookOrder(Customer orderedCustomer, int orderedCopy, Book orderedBook) {
        this.orderedCustomer = orderedCustomer;
        this.orderedCopy = orderedCopy;
        this.orderedBook = orderedBook;
    }

    public Customer getOrderedCustomer() {
        return orderedCustomer;
    }

    public void setOrderedCustomer(Customer orderedCustomer) {
        this.orderedCustomer = orderedCustomer;
    }

    public int getOrderedCopy() {
        return orderedCopy;
    }

    public void setOrderedCopy(int orderedCopy) {
        this.orderedCopy = orderedCopy;
    }

    public Book getOrderedBook() {
        return orderedBook;
    }

    public void setOrderedBook(Book orderedBook) {
        this.orderedBook = orderedBook;
    }
}
