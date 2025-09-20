$(document).ready(function(){

    $("#name_show").text(firstName + " " + lastName);
    $("#name_show2").text(firstName + " " + lastName);
    $("#account_number").text(accountNumber);
    $("#balanceID").text(balance);

    $("#firstName").val(firstName);
    $("#lastName").val(lastName);
    $("#showUsername").text(username);

    if (imageLink.length != 0) {
        $("#imgProfile").attr("src", "assets/img/avatars/"+imageLink+"?v=1.5");
        $("#imgProfile2").attr("src", "assets/img/avatars/"+imageLink+"?v=1.4");
        $("#imgProfile3").attr("src", "assets/img/avatars/"+imageLink+"?v=1.3");
    }
    
});

const sendMoney = document.getElementById('sendButton');

keyEnter("accNum", "sendButton");
keyEnter("amountSent", "sendButton");

sendMoney.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const accNum = document.getElementById('accNum');
    const amountSent = document.getElementById('amountSent');
    const id = getCookie('id');

    if (isEmpty(accNum) || isEmpty(amountSent)) {
        alert("All input must not be empty!");
        return;
    }

const sendMoney = {
        userID: id,
        accNum: accNum.value,
        amountSent: amountSent.value
    };

fetch("/sendMoney", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(sendMoney)
    })
    .then(res => res.text())
    .then(data => {

      if (data == "TRUE") {
        const newBalance = getCookie("balance") - amountSent.value;
        setCookie("balance", newBalance, 20);
        alert("Send Money successful!");
        window.location.assign("index.html");
      } else {alert(data);}

    })
    .catch(err => {
        alert(err);
    });

});


function keyEnter(ID) {
    document.getElementById(ID).addEventListener("keypress", function(event) {
    if (event.key === "Enter") {event.preventDefault();document.getElementById("login").click();}});  
}

function isEmpty(data){if (data.value.trim() === "") {return true;}}

function setCookie(name, value, minutes) {
    let expires = "";
    if (minutes) {
        const date = new Date();
        date.setTime(date.getTime() + (minutes * 60 * 1000)); // minutes to milliseconds
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + encodeURIComponent(value || "") + expires + "; path=/";
}