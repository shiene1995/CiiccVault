const id = getCookie('id');
const firstName = getCookie('firstName');
const lastName = getCookie('lastName');
const accountNumber = getCookie('accountNumber');
const balance = getCookie('balance');
const imageLink = getCookie('imageLink');
const status_login = getCookie('status_login');
const username = getCookie('username');

const cookie_time = 20;

if (status_login === "true") {

    setCookie("id", id, cookie_time);
    setCookie("firstName", firstName, cookie_time);
    setCookie("lastName", lastName, cookie_time);
    setCookie("accountNumber", accountNumber, cookie_time);
    setCookie("balance", balance, cookie_time);
    setCookie("imageLink", imageLink, cookie_time);
    setCookie("status_login", status_login, cookie_time);
    setCookie("username", username, cookie_time);

    if (window.location.pathname === "/dashboard/login.html") {
        window.location.href='index.html';
    }
}
else
{
    if (window.location.pathname != "/dashboard/login.html") {
        window.location.href='login.html';
    }
}

function getCookie(name) {
    const decodedCookies = decodeURIComponent(document.cookie);
    const cookiesArray = decodedCookies.split(';');
    for (let cookie of cookiesArray) {
        cookie = cookie.trim(); // Remove whitespace
        if (cookie.startsWith(name + '=')) {
            return cookie.substring(name.length + 1);
        }
    }
    return null;
}

function setCookie(name, value, minutes) {
    let expires = "";
    if (minutes) {
        const date = new Date();
        date.setTime(date.getTime() + (minutes * 60 * 1000)); // minutes to milliseconds
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + encodeURIComponent(value || "") + expires + "; path=/";
}

function delCookie() { //LOGOUT
    const cookie_time2 = -9999;

    setCookie("id", id, cookie_time2);
    setCookie("firstName", firstName, cookie_time2);
    setCookie("lastName", lastName, cookie_time2);
    setCookie("accountNumber", accountNumber, cookie_time2);
    setCookie("balance", balance, cookie_time2);
    setCookie("imageLink", imageLink, cookie_time2);
    setCookie("status_login", status_login, cookie_time2);
    setCookie("username", username, cookie_time2);

    window.location.href='login.html';
}