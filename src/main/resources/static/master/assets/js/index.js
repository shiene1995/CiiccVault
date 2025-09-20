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