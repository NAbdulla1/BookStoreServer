package components;

public class Publisher extends User {

    private String phoneNumber;
    private int securityQuestionID;
    private String securityQuestionAnswer;

    public Publisher(String name, String email, String password, UserType userType) {
        super(name, email, password, userType);
    }

    public Publisher(String name, String email, String password, String phoneNumber, int securityQuestionID, String securityQuestionAnswer) {
        super(name, email, password, UserType.PUBLISHER);
        this.phoneNumber = phoneNumber;
        this.securityQuestionID = securityQuestionID;
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

    public int getSecurityQuestionID() {
        return securityQuestionID;
    }

    public void setSecurityQuestionID(int securityQuestionID) {
        this.securityQuestionID = securityQuestionID;
    }

}
