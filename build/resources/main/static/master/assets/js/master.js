const updateProfile = document.getElementById('depositButton');

keyEnter("accountNumber", "depositButton");
keyEnter("amountDeposit", "depositButton");

updateProfile.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const accountNumber = document.getElementById('accountNumber');
    const amountDeposit = document.getElementById('amountDeposit');
    const id = getCookie('id');

    if (isEmpty(accountNumber) || isEmpty(amountDeposit)) {
        genModal("Message", "All input must not be empty!", "info");
        return;
    }

    const depositMoney = {
        userID: id,
        accountNumber: accountNumber.value,
        amountDeposit: amountDeposit.value
    };

    fetch("/depositMoney", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(depositMoney)
    })
    .then(res => res.text())
    .then(data => {

      if(data == "TRUE")
      {
        genModal("Message", "Transaction Success!", "success");
        document.getElementById('generalModal').addEventListener('hidden.bs.modal', function () {location.reload();}); 
      } else {genModal("Message", data, "info");}

    })
    .catch(err => {genModal("Error", err, "danger");});

});

    //========================================================== ALL ACCOUNT BALANCE

    fetch("/allBalance", {method: "POST"})
    .then(res => res.text())
    .then(data => {
        setCookie("balanceID", data, 20);
        document.getElementById("balanceID").innerText = data;
    })
    .catch(err => {genModal("Error", err, "danger");});

    //========================================================== LIST OF ACCOUNTS WITH SEARCH

searchAccount("");

function searchAccount(data) {
    const listAccounts = {
        searchAccount: data
    };

    fetch("/listAccounts", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(listAccounts)
    })
    .then(res => res.text())
    .then(data => {
        document.getElementById("tableBody").innerHTML = data;
    })
    .catch(err => {genModal("Error", err, "danger");});
}
    

function status(userID, value) {
    const status = {
        userID: userID,
        status: value
    };

    fetch("/status", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(status)
    })
    .then(res => res.text())
    .then(data => {})
    .catch(err => {genModal("Error", err, "danger");});
}

let userIdTransaction = 0;

function transaction(userID) {
    userIdTransaction = userID;
    const transaction = {
        userID: userID
    };

    fetch("/transaction", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(transaction)
    })
    .then(res => res.text())
    .then(data => {document.getElementById("transactionBody").innerHTML = data;})
    .catch(err => {genModal("Error", err, "danger");});
}

let userIdEditAccount = 0;

function editAccount(userID) {
userIdEditAccount = userID;
    const editAccount = {
        userID: userID
    };

    fetch("/editAccount", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(editAccount)
    })
    .then(res => res.text())
    .then(data => {
        var arr = data.split(',');
        document.getElementById("inputFname").value = arr[0];
        document.getElementById("inputLname").value = arr[1];
        if (arr[2]!="") {document.getElementById("imgAccount").src = "../dashboard/assets/img/avatars/" + arr[2]; }
        else{document.getElementById("imgAccount").src = "../dashboard/assets/img/avatars/default.jpg";}
    })
    .catch(err => {genModal("Error", err, "danger");});
}

function editAccountSave() {
    
    const inputFname = document.getElementById("inputFname").value;
    const inputLname = document.getElementById("inputLname").value;

    const editAccountData = {
        userID: userIdEditAccount,
        inputFname: inputFname,
        inputLname: inputLname
    };

    fetch("/editAccountSave", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(editAccountData)
    })
    .then(res => res.text())
    .then(data => {

        if (data != "TRUE") {genModal("Message", data, "success");}
        else{window.location.assign("master.html");}

    })
    .catch(err => {genModal("Error", err, "danger");});

}

function changePassToDefault() {
    const changePassToDefault = {
        userID: userIdEditAccount
    };

    fetch("/changePassToDefault", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(changePassToDefault)
    })
    .then(res => res.text())
    .then(data => {
        genModal("Message", data, "info");
    })
    .catch(err => {genModal("Error", err, "danger");});
}

let delAccountID = 0;

function delAccount(userID) {
    delAccountID = userID;
}

function delAccountConfirm() {
    const delAccount = {
        userID: delAccountID
    };

    fetch("/delAccount", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(delAccount)
    })
    .then(res => res.text())
    .then(data => {
        genModal("Message", data, "success");
        document.getElementById('generalModal').addEventListener('hidden.bs.modal', function () {location.reload();}); 
    })
    .catch(err => {genModal("Error", err, "danger");});
}

function keyEnter(ID, ID2) {
    document.getElementById(ID).addEventListener("keypress", function(event) {
    if (event.key === "Enter") {event.preventDefault();document.getElementById(ID2).click();}});  
}

function isEmpty(data){if (data.value.trim() === "") {return true;}}

//================================================================== PAGE CLICK FOR TRANSACTIONS

function pageClick(data) {

    var active_page = document.getElementById("active_page").text;

    if(data == "next"){data = parseInt(active_page) + 1;}
    if(data == "prev"){data = parseInt(active_page) - 1;}

    const transactions = {
        userID: userIdTransaction,
        pageClick: data
    };

    fetch("/transaction", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(transactions)
    })
    .then(res => res.text())
    .then(data => {
        
        document.getElementById("transactionBody").innerHTML = data;
    })
    .catch(err => {genModal("Error", err, "danger");});
}

//================================================================== PAGE CLICK FOR LIST OF ACCOUNTS

function pageClick2(data) {

    var active_page = document.getElementById("active_page2").text;
    const search = document.getElementById("searchInput").value

    if(data == "next"){data = parseInt(active_page) + 1;}
    if(data == "prev"){data = parseInt(active_page) - 1;}

    const listAccounts = {
        searchAccount: search,
        pageClick: data
    };

    fetch("/listAccounts", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(listAccounts)
    })
    .then(res => res.text())
    .then(data => {
        
        document.getElementById("tableBody").innerHTML = data;
    })
    .catch(err => {genModal("Error", err, "danger");});
}

//==================================================================

const searchButton = document.getElementById("searchButton");

keyEnter("dateFrom", "searchButton");
keyEnter("dateTo", "searchButton");

searchButton.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const dateFrom = document.getElementById('dateFrom');
    const dateTo = document.getElementById('dateTo');

    if (isEmpty(dateFrom) || isEmpty(dateTo)) {
        genModal("Message", "First and Last name must not be empty!", "info");
        return;
    }

const transactions = {
        userID: userIdTransaction,
        dateFrom: dateFrom.value,
        dateTo: dateTo.value
    };

fetch("/transaction", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(transactions)
    })
    .then(res => res.text())
    .then(data => {
        document.getElementById("transactionBody").innerHTML = data;
    })
    .catch(err => {genModal("Error", err, "danger");});
});