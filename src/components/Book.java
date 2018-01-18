/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.io.Serializable;

public class Book implements Serializable {

    private int bookID;
    private String bookTitle;
    private String authorName;
    private int publisherID;
    private String bookCategory;
    private int stock;
    private double price;
    private String description;

    public Book() {
        this(null, null, -1, null, 0, 0, null);
        bookID = -1;
    }

    public Book(String bookTitle, String authorName, int publisherID, String bookCategory, int stock, double price, String description) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.publisherID = publisherID;
        this.bookCategory = bookCategory;
        this.stock = stock;
        this.price = price;
        this.description = description;
        bookID = -1;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(int publisherID) {
        this.publisherID = publisherID;
    }

    public String getBookCategory() {
        return bookCategory;
    }

    public void setBookCategory(String bookCategory) {
        this.bookCategory = bookCategory;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "Title: %s\n"
                + "Author: %s\n"
                + "Seller: %d\n"
                + "Category: %s", bookTitle, authorName, publisherID, bookCategory);
    }
}
