
const transactions = {
        userID: id
    };

fetch("/transactions", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(transactions)
    })
    .then(res => res.text())
    .then(data => {
        
        document.getElementById("transactionCard").innerHTML = data;
    })
    .catch(err => {
        alert(err);
    });


function pageClick(data) {

    var active_page = document.getElementById("active_page").text;

    if(data == "next"){data = parseInt(active_page) + 1;}
    if(data == "prev"){data = parseInt(active_page) - 1;}

    const transactions = {
        userID: id,
        pageClick: data
    };

    fetch("/transactions", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(transactions)
    })
    .then(res => res.text())
    .then(data => {
        
        document.getElementById("transactionCard").innerHTML = data;
    })
    .catch(err => {
        alert(err);
    });

}

const searchButton = document.getElementById("searchButton");

keyEnter("dateFrom", "searchButton");
keyEnter("dateTo", "searchButton");

searchButton.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const dateFrom = document.getElementById('dateFrom');
    const dateTo = document.getElementById('dateTo');
    const id = getCookie('id');

    if (isEmpty(dateFrom) || isEmpty(dateTo)) {
        alert("First and Last name must not be empty!");
        return;
    }

const transactions = {
        userID: id,
        dateFrom: dateFrom.value,
        dateTo: dateTo.value
    };

fetch("/transactions", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(transactions)
    })
    .then(res => res.text())
    .then(data => {

        document.getElementById("transactionCard").innerHTML = data;
    })
    .catch(err => {
        alert(err);
    });

});

function keyEnter(ID, ID2) {
    document.getElementById(ID).addEventListener("keypress", function(event) {
    if (event.key === "Enter") {event.preventDefault();document.getElementById(ID2).click();}});  
}

function isEmpty(data){if (data.value.trim() === "") {return true;}}