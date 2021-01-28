package app.iggy.panicbutton2;

public class Contacts {
    private String contactName;
    private String phoneNumber;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "contactName='" + contactName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
