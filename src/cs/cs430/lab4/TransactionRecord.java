package cs.cs430.lab4;

/**
 * A data structure that's used to keep the records read from the XML file
 */

class TransactionRecord {
    private int memberID;
    private String ISBN;
    private String checkoutDate;
    private String checkinDate;

    TransactionRecord(final int memberID, final String ISBN, final String checkoutDate, final String checkinDate) {
        this.memberID = memberID;
        this.ISBN = ISBN;
        this.checkoutDate = checkoutDate;
        this.checkinDate = checkinDate;
    }

    int getMemberID() {
        return memberID;
    }
    String getISBN() {
        return ISBN;
    }
    String getCheckoutDate() {
        return checkoutDate;
    }
    String getCheckinDate() {
        return checkinDate;
    }
}
