
document.getElementById("login").addEventListener("click", loginButton);

keyEnter("username");
keyEnter("password");

function loginButton() {

    const username = document.getElementById("username");
    const password = document.getElementById("password");

    if (isEmpty(username) || isEmpty(password)) {
        genModal("Message", "Username and password must not be empty!", "info");
        return;
    }

    const user = {
        username: username.value,
        password: password.value
    };

    fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(user)
    })
    .then(res => res.text())
    .then(data => {
        var arr = data.split(',');
        const cookie_time = 20;

        if (arr[6] == "true") {
            setCookie("id", arr[0], cookie_time);
            setCookie("firstName", arr[1], cookie_time);
            setCookie("lastName", arr[2], cookie_time);
            setCookie("accountNumber", arr[3], cookie_time);
            setCookie("balance", arr[4], cookie_time);
            setCookie("imageLink", arr[5], cookie_time);
            setCookie("status_login", arr[6], cookie_time);
            setCookie("username", arr[7], cookie_time);
            window.location.assign("index.html");
        }
        else if (arr[6] == "master") {
            setCookie("id", arr[0], cookie_time);
            setCookie("firstName", arr[1], cookie_time);
            setCookie("lastName", arr[2], cookie_time);
            setCookie("accountNumber", arr[3], cookie_time);
            setCookie("balance", arr[4], cookie_time);
            setCookie("imageLink", arr[5], cookie_time);
            setCookie("status_login", arr[6], cookie_time);
            setCookie("username", arr[7], cookie_time);
            window.location.assign("../master/master.html");
        }
        else if (data == "LOCKED") {
            genModal("Message", "Your Account is Locked. Please contact us for more info.", "warning");password.value = "";
        }
        else{genModal("Message", data, "info");password.value = "";}
    })
    .catch(err => {
        genModal("Error", err, "danger");
    });
}

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