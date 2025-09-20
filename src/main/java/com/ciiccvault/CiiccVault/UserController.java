package com.ciiccvault.CiiccVault;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.awt.desktop.UserSessionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class UserController {

    String activeUserID = "";
    static int numPages = 0;

    // DTO (Data Transfer Object)
    public static class User {
        private String username;
        private String password;

        private String firstName;
        private String lastName;
        private String usernameRegister;
        private String password1;
        private String password2;

        private String userID;

        // Getters and setters
        public String getUserID() {
            return userID;
        }


        public String getUsername() {
            return username;
        }
        public void setUsername(String data) {
            this.username = data;
        }

        public String getPassword() {return password;}
        public void setPassword(String data) {this.password = data;}

        public String getFirstName() {return firstName;}
        public void setFirstName(String data) {this.firstName = data;}

        public String getLastName() {return lastName;}
        public void setLastName(String data) {this.lastName = data;}

        public String getUsernameRegister() {return usernameRegister;}
        public void setUsernameRegister(String data) {this.usernameRegister = data;}

        public String getPassword1() {return password1;}
        public void setPassword1(String data) {this.password1 = data;}

        public String getPassword2() {return password2;}
        public void setPassword2(String data) {this.password2 = data;}

    }

    public static class UserUpdate {
        private String firstName;
        private String lastName;
        private String userID;

        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String data) {
            this.firstName = data;
        }

        public String getLastName() {
            return lastName;
        }
        public void setLastName(String data) {
            this.lastName = data;
        }

        public String getUserID() {
            return userID;
        }
        public void setUserID(String data) {
            this.userID = data;
        }
    }

    public static class ChangePassword {
        private String userID;
        private String oldPass;
        private String newPass1;
        private String newPass2;

        public String getUserID() {
            return userID;
        }
        public String getOldPass() {
            return oldPass;
        }
        public String getNewPass1() {
            return newPass1;
        }
        public String getNewPass2() {
            return newPass2;
        }
    }

    public static class SendMoney {
        private String userID;
        private String accNum;
        private String amountSent;

        public String getUserID() {
            return userID;
        }
        public String getAccNum() {
            return accNum;
        }
        public String getAmountSent() {
            return amountSent;
        }
    }

    public static class Transactions {
        private String userID;
        private String pageClick;

        private String dateFrom;
        private String dateTo;

        public String getUserID() {
            return userID;
        }
        public String getPageClick() {
            return pageClick;
        }
        public String getDateFrom() {
            return dateFrom;
        }
        public String getDateTo() {
            return dateTo;
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) throws SQLException {

        String data;

        if (user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Username and password must not be empty!";
        }

        MySQL mySQLConnection = new MySQL("db_ciicc");

        boolean usernameFilter = InputFilterUtils.isValidUsername(user.getUsername());
        //boolean passwordFilter = InputFilterUtils.isStrongPassword(new String(passwordChars)); //I should include this below in the future.

        if (usernameFilter) {

            try {
                ResultSet rs = mySQLConnection.selectSQL(user.getUsername(), "username", "tb_account", "");
                if (rs.next()) {

                    if (!rs.getString("status").equals("true") && !rs.getString("status").equals("master"))
                    {return "Your Account is Locked. Please contact us for mode info.";}

                    boolean VP = PasswordHashers.verifyPassword(user.getPassword(), rs.getString("PasswordHash"), rs.getString("SaltHash"));
                    boolean VU = PasswordHashers.verifyUsername(user.getUsername(),rs.getString("username")); //to case-sensitive the username
                    //example: Admin & admin are different

                    if (VP && VU) {
                        DecimalFormat df = new DecimalFormat("0.00");
                        DecimalFormat df2 = new DecimalFormat("00000000000");

                        this.activeUserID = rs.getString("id");

                        data  = rs.getInt("id") + ",";
                        data += rs.getString("Fname") + ",";
                        data += rs.getString("Lname") + ",";
                        data += df2.format(rs.getInt("account_number")) + ",";
                        data += df.format(rs.getFloat("balance_money")) + ",";
                        data += rs.getString("img_link") + ",";
                        data += rs.getString("status") + ",";
                        data += rs.getString("username");

                        rs.close(); //Closed the ResultSet (For Select Query only)
                        mySQLConnection.close(); //Closing MySQL Connection and stmt


                        return data;

                    } else { return "INVALID USERNAME OR PASSWORD!"; }

                } else {return "INVALID USERNAME OR PASSWORD!";}
            } catch (SQLException ex) {throw new RuntimeException(ex);}

        } else {return "INVALID USERNAME OR PASSWORD!";}
    }

    @PostMapping("/register")
    public String createUser(@RequestBody User user) throws SQLException {

        if (user.getFirstName() == null || user.getFirstName().isEmpty() ||
                user.getLastName() == null || user.getLastName().isEmpty() ||
                user.getUsernameRegister() == null || user.getUsernameRegister().isEmpty() ||
                user.getPassword1() == null || user.getPassword1().isEmpty() ||
                user.getPassword2() == null || user.getPassword2().isEmpty())
        {return "All fields must be filled!";}

        if (!user.getPassword1().equals(user.getPassword2())) {return "Password mismatch!";}

        if (InputFilterUtils.isValidUsername(user.getUsernameRegister())) {

            MySQL mySQLConnection = new MySQL("db_ciicc");
            ResultSet rs = mySQLConnection.selectSQL(user.getUsernameRegister(), "username", "tb_account", "");

            if (rs.next()) {
                rs.close(); //Closed the ResultSet (For Select Query only)
                mySQLConnection.close(); //Closing MySQL Connection and stmt
                return "INVALID USERNAME. PLEASE CHOOSE ANOTHER!";
            }

            //getting next id
            rs = mySQLConnection.manualSQL("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'db_ciicc' AND TABLE_NAME = 'tb_account';");
            rs.next();

            String accNumber = LocalDate.now(ZoneId.of("Asia/Manila")).format(DateTimeFormatter.ofPattern("ddMMyyyy")) + rs.getString(1); //account number
            String dateCreated = String.valueOf(LocalDate.now(ZoneId.of("Asia/Manila")));

            char[] passwordChars = user.getPassword1().toCharArray();
            byte[] salt = PasswordHashers.generateSalt();

            int rs1 = mySQLConnection.insertSQL(new String[]{user.getFirstName(), user.getLastName(), user.getUsernameRegister(), PasswordHashers.hashPassword(passwordChars, salt), Base64.getEncoder().encodeToString(salt), accNumber, dateCreated, "true", "0", ""}, new String[]{"Fname", "Lname", "Username", "PasswordHash", "SaltHash", "account_number", "date_account_created", "status", "balance_money", "img_link"}, "tb_account");
            rs.close(); //Closed the ResultSet (For Select Query only)
            mySQLConnection.close(); //Closing MySQL Connection and stmt

            if (rs1 > 0) {

                return "TRUE";
            } else {return "INVALID USERNAME. PLEASE CHOOSE ANOTHER!";}
        } else {return "INVALID USERNAME. PLEASE CHOOSE ANOTHER!";}
    }

    @PostMapping("/profileImage")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("imageUpdate") MultipartFile file,
                                                     @RequestParam("accountNumber") String accountNumber,
                                                     @RequestParam("userID") String userID) {

        if (file.isEmpty()) {// Return an error if no file was uploaded
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);}

        if (accountNumber == null || accountNumber.isEmpty()) {// Return an error if the image ID is missing
            return new ResponseEntity<>("Image ID is missing.", HttpStatus.BAD_REQUEST);}

        try {
            //path - from Main Folder CiiccVault/src////////
            Path uploadPath = Paths.get("src/main/resources/static/dashboard/assets/img/avatars/");

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {Files.createDirectories(uploadPath);}

            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));}

            String newFileName = accountNumber + fileExtension; //filename

            Path targetPath = uploadPath.resolve(newFileName); //path with filename

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            MySQL mySQLConnection = new MySQL("db_ciicc");
            mySQLConnection.updateSQL(new String[]{newFileName},new String[] {"img_link"},"tb_account",userID);
            mySQLConnection.close(); //Closing MySQL Connection and stmt

            return new ResponseEntity<>("Image uploaded successfully!", HttpStatus.OK);

        } catch (IOException e) {return new ResponseEntity<>("Failed to upload image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);} catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/profileUpdate")
    public String profileUpdate(@RequestBody UserUpdate userUpdate) throws SQLException {

        MySQL mySQLConnection = new MySQL("db_ciicc");
        int rs = mySQLConnection.updateSQL(new String[]{userUpdate.getFirstName(), userUpdate.getLastName()},new String[] {"Fname","Lname"},"tb_account",userUpdate.userID);
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        if (rs > 0) {
            return "TRUE";
        }
        else
        {
            return "Failed to update!";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody ChangePassword changePassword) throws SQLException {

        if (!changePassword.getNewPass1().equals(changePassword.getNewPass2())) {return "Password mismatch!";}

        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.selectSQL(changePassword.getUserID(), "id", "tb_account", "");
        rs.next();

        boolean VP = PasswordHashers.verifyPassword(changePassword.getOldPass(), rs.getString("PasswordHash"), rs.getString("SaltHash"));

        if (!VP) { return "INVALID OLD PASSWORD!";}

        char[] passwordChars = changePassword.getNewPass1().toCharArray();
        byte[] salt = PasswordHashers.generateSalt();

        int rs1 = mySQLConnection.updateSQL(new String[]{PasswordHashers.hashPassword(passwordChars, salt), Base64.getEncoder().encodeToString(salt)},new String[] {"PasswordHash","SaltHash"},"tb_account",changePassword.getUserID());
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        if (rs1 > 0) {return "TRUE";} else { return "THERE IS AN ERROR! PLEASE REPORT IT!";}
    }

    @PostMapping("/sendMoney")
    public String sendMoney(@RequestBody SendMoney sendMoney) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.selectSQL(sendMoney.getUserID(), "id", "tb_account", "");rs.next(); //check balance

        BigDecimal balance = new BigDecimal(rs.getString("balance_money"));
        BigDecimal amountSent = new BigDecimal(sendMoney.getAmountSent());
        BigDecimal result = balance.subtract(amountSent);

        String senderAccountNumber = rs.getString("account_number");

        if (result.compareTo(BigDecimal.ZERO) < 0) {return "Insufficient balance. Please deposit money to the nearest bank of CiiccVault.";}

        rs = mySQLConnection.selectSQL(sendMoney.getAccNum(), "account_number", "tb_account", ""); //search receiver

        if(!rs.next()) { return "The transaction was unsuccessful as the account number provided does not exist."; }

        String receiverID = rs.getString("id");

        BigDecimal receiverBalance = new BigDecimal(rs.getString("balance_money"));
        BigDecimal result2 = receiverBalance.add(amountSent);

        mySQLConnection.updateSQL(new String[]{result.toString()},new String[] {"balance_money"},"tb_account", sendMoney.getUserID()); //deduct the amount from sender
        mySQLConnection.updateSQL(new String[]{result2.toString()},new String[] {"balance_money"},"tb_account", receiverID); //add the amount to receiver

        String ref_number = generateReferenceNumber(9);
        String dateTransaction = String.valueOf(LocalDate.now(ZoneId.of("Asia/Manila")));

        String[] data1 = {sendMoney.getUserID(),ref_number,"Send Money", sendMoney.getAccNum(), dateTransaction, sendMoney.getAmountSent()};
        mySQLConnection.insertSQL(data1, new String[] {"userID","reference_number","transaction_type","sent_from_to_account", "sent_date", "sent_amount"},"tb_transactions"); //add transaction history to sender

        String[] data2 = {receiverID, ref_number, "Received Money", senderAccountNumber, dateTransaction, sendMoney.getAmountSent()};
        mySQLConnection.insertSQL(data2, new String[] {"userID","reference_number","transaction_type","sent_from_to_account", "sent_date", "sent_amount"},"tb_transactions"); //add transaction history to receiver
        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt

        return "TRUE";
    }

    @PostMapping("/transactions")
    public String transactions(@RequestBody Transactions transactions) throws SQLException {

        int numPages = 5; //PAGE LIMIT
        int pageClick = 1;
        if(transactions.getPageClick() != null){pageClick = Integer.parseInt( transactions.getPageClick());}
        if (UserController.numPages >= 0){ if(pageClick > UserController.numPages){pageClick = UserController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        MySQL mySQLConnection = new MySQL("db_ciicc");
        ResultSet rs;

        if (transactions.getDateFrom() != null && transactions.getDateTo() !=null)
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_transactions WHERE userID = '"+ transactions.getUserID() +"' AND sent_date >= '" + transactions.getDateFrom() + "' AND sent_date <= '" + transactions.getDateTo() + "' ORDER BY id DESC LIMIT " + start + ", " + numPages);
        }
        else {
             rs = mySQLConnection.selectSQL(transactions.getUserID(), "userID", "tb_transactions", "ORDER BY id DESC LIMIT " + start + ", " + numPages);
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

        data.append(pages(transactions.getUserID(), numPages, transactions));

        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        return data.toString();
    }

    @PostMapping("/refresh")
    public String refresh(@RequestBody User user) throws SQLException {
        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs = mySQLConnection.manualSQL("SELECT SUM(balance_money) FROM tb_account WHERE id="+user.getUserID());
        rs.next();
        String data = rs.getString(1);
        rs.close();
        mySQLConnection.close(); //Closing MySQL Connection and stmt
        return data;
    }

    @GetMapping("/") // Redirection
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/index.html");
    }

    @GetMapping("/dashboard/") // Redirection
    public void redirect2(HttpServletResponse response) throws IOException {
        response.sendRedirect("/dashboard/login.html");
    }

    public static String generateReferenceNumber(int length) {
        Random random = new Random();
        StringBuilder referenceNumber = new StringBuilder();
        // Generate a random number of the specified length
        for (int i = 0; i < length; i++) {
            referenceNumber.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return referenceNumber.toString();
    }

    public static String sendMoneyHTML(String type, String accNum, String dateSent, String amountSent, String refNum){
        LocalDate date = LocalDate.parse(dateSent);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        dateSent = date.format(formatter);
        return "<div class=\"row\" style=\"margin: 15px 0;\">\n" +
                "                                    <div class=\"col-3 col-sm-2 col-md-2 col-lg-2 col-xl-2 col-xxl-2 align-self-center\">\n" +
                "                                        <div class=\"text-center\"><i class=\"fas fa-paper-plane\" style=\"font-size: 24px;color: var(--bs-primary);\"></i></div>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5\">\n" +
                "                                        <h6 style=\"color: var(--bs-gray-900);margin: 0;font-weight: bold;\">"+ type +"</h6>\n" +
                "                                        <p style=\"font-size: 12px;\">Reference "+ refNum +"<br>To "+ accNum +"<br>"+ dateSent +"</p>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5 align-self-center\">\n" +
                "                                        <h6 class=\"text-end\" style=\"color: var(--bs-gray-900);font-weight: bold;\">\n" +
                "                                            <svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"-64 0 512 512\" width=\"1em\" height=\"1em\" fill=\"currentColor\">\n" +
                "                                                <path d=\"M176 32C244.4 32 303.7 71.01 332.8 128H352C369.7 128 384 142.3 384 160C384 177.7 369.7 192 352 192H351.3C351.8 197.3 352 202.6 352 208C352 213.4 351.8 218.7 351.3 224H352C369.7 224 384 238.3 384 256C384 273.7 369.7 288 352 288H332.8C303.7 344.1 244.4 384 176 384H96V448C96 465.7 81.67 480 64 480C46.33 480 32 465.7 32 448V288C14.33 288 0 273.7 0 256C0 238.3 14.33 224 32 224V192C14.33 192 0 177.7 0 160C0 142.3 14.33 128 32 128V64C32 46.33 46.33 32 64 32H176zM254.4 128C234.2 108.2 206.5 96 176 96H96V128H254.4zM96 192V224H286.9C287.6 218.8 288 213.4 288 208C288 202.6 287.6 197.2 286.9 192H96zM254.4 288H96V320H176C206.5 320 234.2 307.8 254.4 288z\"></path>\n" +
                "                                            </svg>&nbsp;"+ amountSent +"</h6>\n" +
                "                                    </div>\n" +
                "                                </div>";
    }

    public static String receivedMoneyHTML(String type, String accNum, String dateSent, String amountSent, String refNum) {
        LocalDate date = LocalDate.parse(dateSent);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        dateSent = date.format(formatter);

        return "<div class=\"row\" style=\"margin: 15px 0;\">\n" +
                "                                    <div class=\"col-3 col-sm-2 col-md-2 col-lg-2 col-xl-2 col-xxl-2 align-self-center\">\n" +
                "                                        <div class=\"text-center\"><i class=\"fas fa-clinic-medical\" style=\"font-size: 24px;color: var(--bs-success);\"></i></div>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5\">\n" +
                "                                        <h6 style=\"color: var(--bs-gray-900);margin: 0;font-weight: bold;\">"+ type +"</h6>\n" +
                "                                        <p style=\"font-size: 12px;\">Reference "+ refNum +"<br>From "+ accNum +"<br>"+ dateSent +"</p>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5 align-self-center\">\n" +
                "                                        <h6 class=\"text-end\" style=\"color: var(--bs-gray-900);font-weight: bold;\"><svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"-64 0 512 512\" width=\"1em\" height=\"1em\" fill=\"currentColor\">\n" +
                "                                                <!--! Font Awesome Free 6.1.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free (Icons: CC BY 4.0, Fonts: SIL OFL 1.1, Code: MIT License) Copyright 2022 Fonticons, Inc. -->\n" +
                "                                                <path d=\"M176 32C244.4 32 303.7 71.01 332.8 128H352C369.7 128 384 142.3 384 160C384 177.7 369.7 192 352 192H351.3C351.8 197.3 352 202.6 352 208C352 213.4 351.8 218.7 351.3 224H352C369.7 224 384 238.3 384 256C384 273.7 369.7 288 352 288H332.8C303.7 344.1 244.4 384 176 384H96V448C96 465.7 81.67 480 64 480C46.33 480 32 465.7 32 448V288C14.33 288 0 273.7 0 256C0 238.3 14.33 224 32 224V192C14.33 192 0 177.7 0 160C0 142.3 14.33 128 32 128V64C32 46.33 46.33 32 64 32H176zM254.4 128C234.2 108.2 206.5 96 176 96H96V128H254.4zM96 192V224H286.9C287.6 218.8 288 213.4 288 208C288 202.6 287.6 197.2 286.9 192H96zM254.4 288H96V320H176C206.5 320 234.2 307.8 254.4 288z\"></path>\n" +
                "                                            </svg>&nbsp;"+ amountSent +"</h6>\n" +
                "                                    </div>\n" +
                "                                </div>";
    }

    public static String depositMoneyHTML(String type, String dateSent, String amountSent, String refNum) {

        LocalDate date = LocalDate.parse(dateSent);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        dateSent = date.format(formatter);

        return "<div class=\"row\" style=\"margin: 15px 0;\">\n" +
                "                                    <div class=\"col-3 col-sm-2 col-md-2 col-lg-2 col-xl-2 col-xxl-2 align-self-center\">\n" +
                "                                        <div class=\"text-center\"><i class=\"fas fa-donate\" style=\"font-size: 24px;color: var(--bs-warning);\"></i></div>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5\">\n" +
                "                                        <h6 style=\"color: var(--bs-gray-900);margin: 0;font-weight: bold;\">"+ type +"<br></h6>\n" +
                "                                        <p style=\"font-size: 12px;\">Reference "+ refNum +"<br>From CIICC Vault-QC<br>"+ dateSent +"</p>\n" +
                "                                    </div>\n" +
                "                                    <div class=\"col-4 col-sm-5 col-md-5 col-lg-5 col-xl-5 col-xxl-5 align-self-center\">\n" +
                "                                        <h6 class=\"text-end\" style=\"color: var(--bs-gray-900);font-weight: bold;\"><svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"-64 0 512 512\" width=\"1em\" height=\"1em\" fill=\"currentColor\" style=\"font-size: 20px;\">\n" +
                "                                                <!--! Font Awesome Free 6.1.1 by @fontawesome - https://fontawesome.com License - https://fontawesome.com/license/free (Icons: CC BY 4.0, Fonts: SIL OFL 1.1, Code: MIT License) Copyright 2022 Fonticons, Inc. -->\n" +
                "                                                <path d=\"M176 32C244.4 32 303.7 71.01 332.8 128H352C369.7 128 384 142.3 384 160C384 177.7 369.7 192 352 192H351.3C351.8 197.3 352 202.6 352 208C352 213.4 351.8 218.7 351.3 224H352C369.7 224 384 238.3 384 256C384 273.7 369.7 288 352 288H332.8C303.7 344.1 244.4 384 176 384H96V448C96 465.7 81.67 480 64 480C46.33 480 32 465.7 32 448V288C14.33 288 0 273.7 0 256C0 238.3 14.33 224 32 224V192C14.33 192 0 177.7 0 160C0 142.3 14.33 128 32 128V64C32 46.33 46.33 32 64 32H176zM254.4 128C234.2 108.2 206.5 96 176 96H96V128H254.4zM96 192V224H286.9C287.6 218.8 288 213.4 288 208C288 202.6 287.6 197.2 286.9 192H96zM254.4 288H96V320H176C206.5 320 234.2 307.8 254.4 288z\"></path>\n" +
                "                                            </svg>&nbsp;"+ amountSent +"</h6>\n" +
                "                                    </div>\n" +
                "                                </div>";
    }

    public static String pages(String activeUserID, int numPages, Transactions transactions) throws SQLException {

        MySQL mySQLConnection = new MySQL("db_ciicc");

        ResultSet rs;

        if (transactions.getDateFrom() != null && transactions.getDateTo() !=null)
        {
            rs = mySQLConnection.manualSQL("SELECT * FROM tb_transactions WHERE userID = '"+ transactions.getUserID() +"' AND sent_date >= '" + transactions.getDateFrom() + "' AND sent_date <= '" + transactions.getDateTo() + "' ORDER BY id DESC");
        }
        else {
            rs = mySQLConnection.selectSQL(transactions.getUserID(), "userID", "tb_transactions", "ORDER BY id DESC");
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

        if(transactions.getPageClick() != null){pageClick = Integer.parseInt( transactions.getPageClick());}
        if (UserController.numPages >= 0){ if(pageClick > UserController.numPages){pageClick = UserController.numPages;}}
        int start = (pageClick - 1) * numPages;
        if(start <=0){start=0; pageClick = 1;}

        int roundUpPages = UserController.numPages = (int) Math.ceil((double) numResult / numPages);

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
}
