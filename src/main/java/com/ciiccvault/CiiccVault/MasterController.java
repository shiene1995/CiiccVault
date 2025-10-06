package com.ciiccvault.CiiccVault;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.ciiccvault.CiiccVault.UserController.*;

@RestController
public class MasterController {
    static int numPages = 0;

    // DTO (Data Transfer Object)
    public static class Master {
        private String userID;
        private String accountNumber;
        private String amountDeposit;
        private String status;
        private String pageClick;

        private String dateFrom;
        private String dateTo;

        private String searchAccount;

        public String getUserID() {return userID;}
        public String getAccountNumber() {return accountNumber;}
        public String getAmountDeposit() {return amountDeposit;}
        public String getStatus() {return status;}
        public String getPageClick() {return pageClick;}
        public String getDateFrom() {return dateFrom;}
        public String getDateTo() {return dateTo;}
        public String getSearchAccount() {return searchAccount;}
    }

    public static class EditAccountSave {
        private String inputFname;
        private String inputLname;
        private String userID;


        public String getUserID() {return userID;}
        public String getInputFname() {return inputFname;}
        public String getInputLname() {return inputLname;}
    }

    @PostMapping("/depositMoney")
    public String depositMoney(@RequestBody Master master) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.selectSQL(master.getAccountNumber(), "account_number", "tb_account", ""); //search receiver

        if(!rs.next()) { return "The transaction was unsuccessful as the account number provided does not exist."; }

        String receiverID = rs.getString("id");

        BigDecimal receiverBalance = new BigDecimal(rs.getString("balance_money"));
        BigDecimal amountDeposit = new BigDecimal(master.getAmountDeposit());
        BigDecimal result = receiverBalance.add(amountDeposit);

        mySQLConnection.updateSQL(new String[]{result.toString()},new String[] {"balance_money"},"tb_account", receiverID); //add the amount to receiver

        String ref_number = generateReferenceNumber(9);
        String dateTransaction = String.valueOf(LocalDate.now(ZoneId.of("Asia/Manila")));

        String[] data2 = {receiverID, ref_number, "Deposit Money", "999999999", dateTransaction, master.getAmountDeposit()};
        mySQLConnection.insertSQL(data2, new String[] {"userID","reference_number","transaction_type","sent_from_to_account", "sent_date", "sent_amount"},"tb_transactions"); //add transaction history to receiver

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        return "TRUE";
    }

    @PostMapping("/allBalance")
    public String allBalance() throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.manualSQL("SELECT SUM(balance_money) FROM `tb_account`");
        rs.next();

        DecimalFormat df = new DecimalFormat("0.00");

        String data = df.format(rs.getFloat(1));
        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        return data;
    }

    @PostMapping("/listAccounts")
    public String listAccounts(@RequestBody Master master) throws SQLException {

        int numPages = 100; //PAGE LIMIT
        int pageClick = 1;
        if(master.getPageClick() != null){pageClick = Integer.parseInt( master.getPageClick());}
        if (MasterController.numPages >= 0){ if(pageClick > MasterController.numPages){pageClick = MasterController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        MySQL mySQLConnection = new MySQL("db_ciicc");
        ResultSet rs;

        if(master.getSearchAccount().isEmpty())
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_account LIMIT "+start+", "+numPages);
        }
        else if (!master.getSearchAccount().isEmpty() && pageClick > 1)
        {
            rs = mySQLConnection.searchSQL(master.getSearchAccount(), new String[] {"Fname","Lname","Username", "account_number", "balance_money", "date_account_created", "status"},"tb_account", "LIMIT "+start+", "+numPages);
        }
        else
        {   // SEARCH
            rs = mySQLConnection.searchSQL(master.getSearchAccount(), new String[] {"Fname","Lname","Username", "account_number", "balance_money", "date_account_created", "status"},"tb_account", "LIMIT "+numPages);
        }


        StringBuilder data = new StringBuilder();

        while (rs.next()){
            if (!rs.getString("status").equals("master")) {
                data.append(accountData(rs.getString("id"), rs.getString("status"), rs.getString("img_link"), rs.getString("Fname") + " " + rs.getString("Lname")
                        , rs.getString("date_account_created"), rs.getString("balance_money"), rs.getString("account_number")));
            }
        }
        //! I NEED FIX THE PAGINATION
        //data.append(pagesAccountList(master.getUserID(), numPages, master, pageClick));

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        return data.toString();
    }

    @PostMapping("/status")
    public String status(@RequestBody Master master) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        int rs1 = mySQLConnection.updateSQL(new String[]{master.getStatus()},new String[] {"status"},"tb_account",master.getUserID());
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        return "TRUE";
    }

    @PostMapping("/transaction")
    public String transaction(@RequestBody Master master) throws SQLException {


        int numPages = 5; //PAGE LIMIT
        int pageClick = 1;
        if(master.getPageClick() != null){pageClick = Integer.parseInt( master.getPageClick());}
        if (MasterController.numPages >= 0){ if(pageClick > MasterController.numPages){pageClick = MasterController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        MySQL mySQLConnection = new MySQL("db_ciicc");
        ResultSet rs;

        if (master.getDateFrom() != null && master.getDateTo() !=null)
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_transactions WHERE userID = '"+ master.getUserID() +"' AND sent_date >= '" + master.getDateFrom() + "' AND sent_date <= '" + master.getDateTo() + "' ORDER BY id DESC LIMIT " + start + ", " + numPages);
        }
        else {
            rs = mySQLConnection.selectSQL(master.getUserID(), "userID", "tb_transactions", "ORDER BY id DESC LIMIT " + start + ", " + numPages);
        }

        StringBuilder data = new StringBuilder();

        while (rs.next()){

            DecimalFormat df = new DecimalFormat("0.00");

            if (rs.getString("transaction_type").equals("Send Money"))
            {
                data.append(sendMoneyHTML(rs.getString("transaction_type"), rs.getString("sent_from_to_account"), rs.getString("sent_date"), df.format(rs.getFloat("sent_amount")), rs.getString("reference_number")));}

            if (rs.getString("transaction_type").equals("Received Money"))
            {
                data.append(receivedMoneyHTML(rs.getString("transaction_type"), rs.getString("sent_from_to_account"), rs.getString("sent_date"), df.format(rs.getFloat("sent_amount")), rs.getString("reference_number")));}

            if (rs.getString("transaction_type").equals("Deposit Money"))
            {
                data.append(depositMoneyHTML(rs.getString("transaction_type"), rs.getString("sent_date"), df.format(rs.getFloat("sent_amount")), rs.getString("reference_number")));}
        }

        data.append(pages(master.getUserID(), numPages, master));

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        return data.toString();
    }

    @PostMapping("/editAccount")
    public String editAccount(@RequestBody Master master) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.selectSQL(master.getUserID(),"id","tb_account","");
        rs.next();
        String data = rs.getString("Fname")+","+rs.getString("Lname")+","+rs.getString("img_link");
        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        return data;
    }

    @PostMapping("/editAccountSave")
    public String editAccountSave(@RequestBody EditAccountSave editAccountSave) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        if (editAccountSave.getInputFname().isEmpty() || editAccountSave.getInputLname().isEmpty())
        {return "All input must not be empty!";}

        int rs1 = mySQLConnection.updateSQL(new String[]{editAccountSave.getInputFname(), editAccountSave.getInputLname()},new String[] {"Fname", "Lname"},"tb_account",editAccountSave.getUserID());
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        if (rs1 > 0) {
            return "TRUE";
        }
        else
        {
            return "FALSE";
        }
    }

    @PostMapping("/changePassToDefault")
    public String changePassToDefault(@RequestBody Master master) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        int rs1 = mySQLConnection.updateSQL(new String[]{"E4KD3Euf48vqBhtF/GSor6phRJHQCWtUtcKE3JZgaTc=", "EHeoo1sUCbeQoAanbbyvBw=="},new String[] {"PasswordHash", "SaltHash"},"tb_account","1");
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        if (rs1 > 0) {
            return "PASSWORD CHANGED TO : 123456";
        }
        else
        {
            return "FAILED TO CHANGE PASSWORD";
        }
    }

    @PostMapping("/delAccount")
    public String delAccount(@RequestBody Master master) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        int rs1 = mySQLConnection.deleteSQL(master.getUserID(), "tb_account");

        if (rs1 > 0) {
            return "Account has been deleted Permanently!";
        }
        else
        {
            return "FAILED TO DELETE ACCOUNT!";
        }
    }

    public static String accountData(String userID, String status, String imgLink, String name, String dateCreated, String availableBalance, String accountNumber){

        DecimalFormat df = new DecimalFormat("0.00");

        LocalDate date = LocalDate.parse(dateCreated);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        dateCreated = date.format(formatter);

        String statusLabel;

        if (status.equals("true"))
        {status = "checked"; statusLabel = "Enabled";}
        else{status=""; statusLabel = "Disabled";}

        availableBalance = df.format(Float.parseFloat(availableBalance));

        if (imgLink.isEmpty())
        {
            imgLink = "default.jpg";
        }

        String data = String.format("""
                <tr>
                    <td class="text-start">
                        <div class="form-check form-switch">
                            <input onclick="status(%s, this.checked)" class="form-check-input" type="checkbox" id="formCheck-1" %s>
                            <label class="form-check-label" for="formCheck-1">%s</label>
                        </div>
                    </td>
                    <td class="text-start">
                        <img class="rounded-circle me-2" width="30" height="30" src="../dashboard/assets/img/avatars/%s">%s
                    </td>
                    <td class="text-center">%s</td>
                    <td class="text-center">%s</td>
                    <td class="text-end">%s</td>
                    <td class="text-center">
                        <div class="btn-group" role="group">
                            <button onclick="transaction(%s)" class="btn btn-primary" type="button" style="font-weight: bold;" data-bs-target="#transactionsModal" data-bs-toggle="modal">Transactions</button>
                            <button onclick="editAccount(%s)" class="btn btn-success" type="button" style="color: rgb(255,255,255);font-weight: bold;" data-bs-target="#editModal" data-bs-toggle="modal">Edit</button>
                            <button onclick="delAccount(%s)" class="btn btn-danger" type="button" style="font-weight: bold;" data-bs-target="#deleteModal" data-bs-toggle="modal">Delete</button>
                        </div>
                    </td>
                </tr>
                """, userID, status, statusLabel, imgLink, name, accountNumber, dateCreated, availableBalance, userID, userID, userID);


        return data;
    }

    public static String pages(String activeUserID, int numPages, Master master) throws SQLException {

        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs;

        if (master.getDateFrom() != null && master.getDateTo() !=null)
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_transactions WHERE userID = '"+ activeUserID +"' AND sent_date >= '" + master.getDateFrom() + "' AND sent_date <= '" + master.getDateTo() + "' ORDER BY id DESC");
        }
        else {
            rs = mySQLConnection.selectSQL(activeUserID, "userID", "tb_transactions", "ORDER BY id DESC");
        }


        int numResult = 0;

        while (rs.next()){numResult++;}

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        String data = "";

        data += "<div class=\"row\">\n" +
                "                                    <div class=\"col-md-6 align-self-center\">\n" +
                "                                        <p id=\"dataTable_info\" class=\"dataTables_info\" role=\"status\" aria-live=\"polite\">"+ numResult +" Results</p>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-md-6\">\n" +
                "                                        <nav class=\"d-lg-flex justify-content-lg-end dataTables_paginate paging_simple_numbers\">\n" +
                "                                            <ul class=\"pagination\">\n";

        int pageClick = 1;

        if(master.getPageClick() != null){pageClick = Integer.parseInt( master.getPageClick());}
        if (MasterController.numPages >= 0){ if(pageClick > MasterController.numPages){pageClick = MasterController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        int roundUpPages = MasterController.numPages = (int) Math.ceil((double) numResult / numPages);

        int x_end = roundUpPages;

        if (pageClick <= 0 || pageClick == 1) //FIRST PAGE
        {
            start = 1;
            if (roundUpPages >=3)
            {
                x_end = 3;
            }
        }
        else
        {
            start = pageClick-1; //MID PAGE
            x_end = pageClick+1;
        }

        if (pageClick >= roundUpPages) //LAST PAGE
        {
            if (roundUpPages >= 3 )
            {
                start = roundUpPages-2;
            }
            else
            {
                start = 1;
            }
            x_end = roundUpPages;
        }

        data += "<li class=\"page-item\"><a onclick=\"pageClick('prev')\" class=\"page-link\" aria-label=\"Previous\" href=\"#\"><span aria-hidden=\"true\">«</span></a></li>\n";

        if (roundUpPages > 0) {
            for (int x = start; x <= x_end; x++) {

                if (pageClick == x) {
                    data += "<li class=\"page-item active\"><a id=\"active_page\" onclick=\"pageClick(" + x + ")\" class=\"page-link\" href=\"#\">" + x + "</a></li>\n";
                } else {
                    data += "<li class=\"page-item\"><a onclick=\"pageClick(" + x + ")\" class=\"page-link\" href=\"#\">" + x + "</a></li>\n";
                }
                if(x == roundUpPages){break;}
            }
        }
        data +=  "<li class=\"page-item\"><a onclick=\"pageClick('next')\" class=\"page-link\" aria-label=\"Next\" href=\"#\"><span aria-hidden=\"true\">»</span></a></li></ul></nav></div></div>";

        return data;
    }

    public static String pagesAccountList(String activeUserID, int numPages, Master master, int pageClick) throws SQLException {

        if(master.getPageClick() != null){pageClick = Integer.parseInt( master.getPageClick());}
        if (MasterController.numPages >= 0){ if(pageClick > MasterController.numPages){pageClick = MasterController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs;

        if(master.getSearchAccount().isEmpty())
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_account");
        }
        else if (!master.getSearchAccount().isEmpty() && pageClick > 0)
        {
            rs = mySQLConnection.searchSQL(master.getSearchAccount(), new String[] {"Fname","Lname","Username", "account_number", "balance_money", "date_account_created", "status"},"tb_account", "LIMIT "+start+", "+numPages);
        }
        else
        {   // SEARCH
            rs = mySQLConnection.searchSQL(master.getSearchAccount(), new String[] {"Fname","Lname","Username", "account_number", "balance_money", "date_account_created", "status"},"tb_account", "LIMIT "+numPages);
        }

        //int pageClick = 1;



        int numResult = 0;

        while (rs.next()){
            if (!rs.getString("status").equals("master")) {
                numResult++;
            }
        }

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        String data = "";

        data += "<div class=\"row\">\n" +
                "                                    <div class=\"col-md-6 align-self-center\">\n" +
                "                                        <p id=\"dataTable_info\" class=\"dataTables_info\" role=\"status\" aria-live=\"polite\">"+ numResult +" Results</p>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-md-6\">\n" +
                "                                        <nav class=\"d-lg-flex justify-content-lg-end dataTables_paginate paging_simple_numbers\">\n" +
                "                                            <ul class=\"pagination\">\n";

        //int pageClick = 1;

        int roundUpPages = MasterController.numPages = (int) Math.ceil((double) numResult / numPages);

        int x_end = roundUpPages;

        if (pageClick <= 0 || pageClick == 1) //FIRST PAGE
        {
            start = 1;
            if (roundUpPages >=3)
            {
                x_end = 3;
            }
        }
        else
        {
            start = pageClick-1; //MID PAGE
            x_end = pageClick+1;
        }

        if (pageClick >= roundUpPages) //LAST PAGE
        {
            if (roundUpPages >= 3 )
            {
                start = roundUpPages-2;
            }
            else
            {
                start = 1;
            }
            x_end = roundUpPages;
        }

        data += "<li class=\"page-item\"><a onclick=\"pageClick2('prev')\" class=\"page-link\" aria-label=\"Previous\" href=\"#\"><span aria-hidden=\"true\">«</span></a></li>\n";

        if (roundUpPages > 0) {
            for (int x = start; x <= x_end; x++) {

                if (pageClick == x) {
                    data += "<li class=\"page-item active\"><a id=\"active_page2\" onclick=\"pageClick2(" + x + ")\" class=\"page-link\" href=\"#\">" + x + "</a></li>\n";
                } else {
                    data += "<li class=\"page-item\"><a onclick=\"pageClick2(" + x + ")\" class=\"page-link\" href=\"#\">" + x + "</a></li>\n";
                }
                if(x == roundUpPages){break;}
            }
        }
        data +=  "<li class=\"page-item\"><a onclick=\"pageClick2('next')\" class=\"page-link\" aria-label=\"Next\" href=\"#\"><span aria-hidden=\"true\">»</span></a></li></ul></nav></div></div>";

        return data;
    }
}
