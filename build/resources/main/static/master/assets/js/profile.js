
const fileInput = document.getElementById('imageUpdate');

fileInput.addEventListener('change', () => { //UPDATE PROFILE IMAGE
    const file = fileInput.files[0];
    if (!file || !file.type.startsWith('image/')) {
        alert('Please select a valid image file.');
        return;
    }

    // Optional: Provide immediate user feedback (e.g., disable input, show loading spinner)
    // fileInput.disabled = true;
    // showLoadingIndicator(); // You'd need to implement this function

    const formData = new FormData();
    formData.append('imageUpdate', file);
    formData.append('userID', id); // Make sure 'id' is defined in your scope
    formData.append('accountNumber', accountNumber); // Make sure 'id' is defined in your scope

    fetch('/profileImage', {
        method: 'POST',
        body: formData
    })
    .then(res => {
        if (!res.ok) { // Check if the HTTP status code indicates success (200-299)
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.text(); // Assuming your PHP always returns text (HTML message + <img>)
    })
    .then(data => {
        // This block runs ONLY AFTER the server has responded successfully
        alert(data); // Server returns HTML: message + <img>. This alert will now show after upload.
        console.log("Image uploaded and server responded. Refreshing page...");
        window.location.assign("profile.html");
    })
    .catch(error => {
        // This block runs if there's a network error or a non-OK HTTP status
        alert('Upload failed. Please try again. : ' + error); // More user-friendly message
        console.error('Error during image upload:', error);
    })
    .finally(() => {
        // Optional: Re-enable input or hide loading spinner, regardless of success or failure
        // fileInput.disabled = false;
        // hideLoadingIndicator(); // You'd need to implement this function
    });
});

const updateProfile = document.getElementById('updateProfile');

keyEnter("firstName", "updateProfile");
keyEnter("lastName", "updateProfile");

updateProfile.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const firstName = document.getElementById('firstName');
    const lastName = document.getElementById('lastName');
    const id = getCookie('id');

    if (isEmpty(firstName) || isEmpty(lastName)) {
        alert("First and Last name must not be empty!");
        return;
    }

const userUpdate = {
        userID: id,
        firstName: firstName.value,
        lastName: lastName.value
    };

fetch("/profileUpdate", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(userUpdate)
    })
    .then(res => res.text())
    .then(data => {

      if (data == "TRUE") {
        setCookie("firstName", firstName.value, 20);
        setCookie("lastName", lastName.value, 20);
        alert("Profile update successful!");
        window.location.assign("profile.html");
      } else {alert(data);}

    })
    .catch(err => {
        alert(err);
    });

});

const updatePasswordButton = document.getElementById('updatePasswordButton');

keyEnter("oldPass", "updatePasswordButton");
keyEnter("newPass1", "updatePasswordButton");
keyEnter("newPass2", "updatePasswordButton");

updatePasswordButton.addEventListener('click', () => { //CHANGE PASSWORD

const oldPass = document.getElementById('oldPass');
const newPass1 = document.getElementById('newPass1');
const newPass2 = document.getElementById('newPass2');
const id = getCookie('id');

    if (isEmpty(oldPass) || isEmpty(newPass1) || isEmpty(newPass2)) {
        alert("All input must not be empty!");
        return;
    }

const changePassword = {
        userID: id,
        oldPass: oldPass.value,
        newPass1: newPass1.value,
        newPass2: newPass2.value
    };

fetch("/changePassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"  
        },
        body: JSON.stringify(changePassword)
    })
    .then(res => res.text())
    .then(data => {

      if (data == "TRUE") {
        alert("PASSWORD HAS BEEN CHANGED!");
      } else {alert(data);}

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