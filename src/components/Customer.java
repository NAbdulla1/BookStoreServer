package components;

/**
 * Created by nayon on 16-Jan-18.
 */

public class Customer extends User {
    private String address;
    private int securityQuestionID;
    private String securityQuestionAnswer;

    private Customer(String name, String email, String password, UserType userType) {
        super(name, email, password, userType);
    }

    public Customer(String name, String email, String password, UserType userType, String address,
                    int securityQuestionID, String securityQuestionAnswer) {
        this(name, email, password, userType);
        this.address = address;
        this.securityQuestionID = securityQuestionID;
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public String getAddress() {
        return address;
    }

    public int getSecurityQuestionID() {
        return securityQuestionID;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }
}
