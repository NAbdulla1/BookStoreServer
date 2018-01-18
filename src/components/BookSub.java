package components;

import java.io.Serializable;

public class BookSub implements Serializable {

    private String bookSubTitle;
    private String bookSubAuthor;
    private String bookSubPublisher;
    private String bookSubCategory;

    public BookSub() {
        this(null, null, null, null);
    }

    public BookSub(String bookSubTitle, String bookSubAuthor, String bookSubPublisher, String bookSubCategory) {
        this.bookSubTitle = bookSubTitle;
        this.bookSubAuthor = bookSubAuthor;
        this.bookSubPublisher = bookSubPublisher;
        this.bookSubCategory = bookSubCategory;
    }

    public String getBookSubTitle() {
        return bookSubTitle;
    }

    public void setBookSubTitle(String bookSubTitle) {
        this.bookSubTitle = bookSubTitle;
    }

    public String getBookSubAuthor() {
        return bookSubAuthor;
    }

    public void setBookSubAuthor(String bookSubAuthor) {
        this.bookSubAuthor = bookSubAuthor;
    }

    public String getBookSubPublisher() {
        return bookSubPublisher;
    }

    public void setBookSubPublisher(String bookSubPublisher) {
        this.bookSubPublisher = bookSubPublisher;
    }

    public String getBookSubCategory() {
        return bookSubCategory;
    }

    public void setBookSubCategory(String bookSubCategory) {
        this.bookSubCategory = bookSubCategory;
    }
}
