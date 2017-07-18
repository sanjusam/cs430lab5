package cs.cs430.lab4;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;

public class Lab4 {

    /**
     * Method which check is the member exists or not.
     */

    private boolean memberExist(final int memberID) {
        final String checkMemeberExist = "select 1 from member where memberID = " + memberID;
        ResultSet resultSet =  executeSelect(checkMemeberExist);
        return checkIRecordFound(resultSet);
    }

    /**
     * Method that finds the checkout record.
     */
    private boolean getBookCheckoutRecord(final int memberID, final String ISBN) {

        final String sql = "select * from borrowedBy where"
                + " memberID = " + memberID
                + " and ISBN = '"
                + ISBN + "' and  checkinDate is null";
        ResultSet resultSet =  executeSelect(sql);
        return checkIRecordFound(resultSet);
    }

    /**
     * Method that finds the checkout record.
     */
    private boolean checkIRecordFound(final ResultSet resultSet) {
        boolean found = false;
        try {
            if (resultSet.next()) {
                found = true;
            }
        } catch (final Exception exception) {
            found = false;
        }
        return found;
    }

    /**
     * Method that checks the book back in.
     */
    private void checkInBook(final TransactionRecord transactionRecord) {
        final String updateStatement = "update borrowedBy set checkinDate = STR_TO_DATE('"
                + transactionRecord.getCheckinDate() +"','%m/%d/%Y')"
                + " where memberID = " + transactionRecord.getMemberID()
                + " and ISBN = '"
                + transactionRecord.getISBN()
                + "' and  checkinDate is null";
        int resultSet =  updateDatabase(updateStatement);
        if(resultSet == -1 ) {
            logMessage("Failed to check-in book  " + transactionRecord.getMemberID() + "  " + transactionRecord.getISBN());
        }else {
            logMessage("CHECK-IN SUCCESSFUL  : SQL  : " + updateStatement);
        }
    }

    /**
     * Method that checks if a checkout record is present,
     *      if yes, call the method to check the book in
     *      if not, print a message.
     */
    private void processBookCheckIn(final TransactionRecord transactionRecord) {
        final boolean checkoutRecordFound = getBookCheckoutRecord(transactionRecord.getMemberID(), transactionRecord.getISBN());
        if(checkoutRecordFound) {
            checkInBook(transactionRecord);
        } else {
            logMessage("ABORTING CHECK-IN AS CHECKOUT RECORD NOT PRESENT : " + transactionRecord.getMemberID() + " " + transactionRecord.getISBN() + " " + transactionRecord.getCheckinDate());
        }
    }

    /**
     * Method that checks the book exists in the library.
     * if not, print a message.
     */
    private boolean bookExists(final String ISBN) {
        final String checkBook =  "select 1 from book where ISBN = '" + ISBN +"'";
        final ResultSet resultSet = executeSelect(checkBook);
        return checkIRecordFound(resultSet);
    }

    /**
     * Method that checks-out a book by inserting an entry into the database
     */
    private void checkOutBook(final TransactionRecord transactionRecord) {
        final String checkoutBook  = "insert into borrowedBy (memberID, ISBN, checkoutDate) values ("
                + transactionRecord.getMemberID() + ", '"
                + transactionRecord.getISBN()  + "', "
                + "STR_TO_DATE('" + transactionRecord.getCheckoutDate() + "','%m/%d/%Y'))";

        int resultSet =  updateDatabase(checkoutBook);
        if(resultSet < 0 ) {
            logMessage("Failed to checkout the book " + transactionRecord.getMemberID() + "  " + transactionRecord.getISBN());
        } else {
            logMessage("CHECK-OUT SUCCESSFUL : SQL  : " + checkoutBook);
        }
    }

    /**
     * Method which calls multiple methods to checkout a book.
     * Validate is book is present,
     *      if yes, check-out
     *      if not, prints a message.
     */
    private void processBookCheckOut(final TransactionRecord transactionRecord) {
        if(bookExists(transactionRecord.getISBN())) {
            checkOutBook(transactionRecord);
        } else {
            logMessage("ABORTING CHECK-OUT AS BOOK NOT PRESENT IN LIBRARY : " + transactionRecord.getISBN());
        }
    }


    /**
     * Method which calls processes to checkin or checkout data bases on the type of record it gets.
     * Only proceed if the member exists;
     */
    private void processData(final Queue<TransactionRecord> transactionRecordQueue) {
        for (TransactionRecord transactionRecord : transactionRecordQueue) {
            if(!memberExist(transactionRecord.getMemberID())) {
                logMessage("ABORTING TRANSACTION AS MEMBER DOES NOT EXIST : " + transactionRecord.getMemberID());
                continue;
            }
            if (isCheckoutRecord(transactionRecord)) {
                processBookCheckOut(transactionRecord);
            } else {
                processBookCheckIn(transactionRecord);
            }
        }
    }


    /**
     * A method that executes the select statements on the DB and returns the result set
     */
    private ResultSet executeSelect(final String sqlStatement) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        ResultSet resultSet = null;
        try {
            resultSet = databaseAccess.executeQuery(sqlStatement);
        } catch (final SQLException sqlE) {
            logMessage("Error while executing statement : " + sqlStatement);
            sqlE.printStackTrace();
        }
        return resultSet;
    }

    /**
     * A method that executes the update statements on the DB and returns the results
     */
    private int updateDatabase(final String sqlStatement) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance();
        int ret  = -1;
        try {
            ret = databaseAccess.updateDatabase(sqlStatement);
        } catch (SQLException e) {
            logMessage("Error while executing statement : " + sqlStatement);
            e.printStackTrace();
            return -1 ;
        }
        return ret;
    }

    /**
     * A utility method that checks if a given record is a checkout or checkin record.
    */
    private boolean isCheckoutRecord(final TransactionRecord transactionRecord) {
        final String notApplicable = "N/A";
        return transactionRecord.getCheckinDate().equals(notApplicable);
    }

    /**
     * A simple utility method that just prints to the console.
     */
    private static void logMessage(final String message) {
        System.out.println(message);
    }


    /**
     * Start
     */
    public static void main(String args[]){

        Lab4.logMessage("Starting to processing Libdata.xml");
        final ParseXMLData parseXMLData =  new ParseXMLData();
        final Queue<TransactionRecord> transactionRecords = parseXMLData.readXML("Libdata.xml");

        final Lab4 libraryDataProcessing = new Lab4();
        libraryDataProcessing.processData(transactionRecords);
        Lab4.logMessage("Done Processing Libdata.xml");
    }
}
