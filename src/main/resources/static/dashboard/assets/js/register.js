
document.getElementById("register").addEventListener("click", Register);

keyEnter("FirstName");
keyEnter("LastName");
keyEnter("Username");
keyEnter("Password1");
keyEnter("Password2");

function Register() {

    const FirstName = document.getElementById("FirstName");
    const LastName = document.getElementById("LastName");
    const Username = document.getElementById("Username");
    const Password1 = document.getElementById("Password1");
    const Password2 = document.getElementById("Password2");

    if (isEmpty(FirstName) || isEmpty(LastName) || isEmpty(Username) || isEmpty(Password1) || isEmpty(Password2)) {
        genModal("Message", "All fields must be filled!", "info");
        return;
    }

    if (Password1.value !== Password2.value) {
        genModal("Message", "Password mismatch!", "info");
        return;
    }

    const user = {
        firstName: FirstName.value,
        lastName: LastName.value,
        usernameRegister: Username.value,
        password1: Password1.value,
        password2: Password2.value
    };

    fetch("/register", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(user)
    })
    .then(res => res.text())
    .then(data => {
        if (data == "TRUE") {
            genModal("Message", "Account has been created!", "success");
            window.location.assign("login.html");}
        else{genModal("Message", data, "info");Password1.value = Password2.value = "";}
    })
    .catch(err => {genModal("Error", err, "danger");});
}

function keyEnter(ID) {
    document.getElementById(ID).addEventListener("keypress", function(event) {
    if (event.key === "Enter") {event.preventDefault();document.getElementById("register").click();}});  
}

function isEmpty(data){if (data.value.trim() === "") {return true;}}
    