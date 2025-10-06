
const fileInput = document.getElementById('imageUpdate');

fileInput.addEventListener('change', () => { //UPDATE PROFILE IMAGE
    const file = fileInput.files[0];
    if (!file || !file.type.startsWith('image/')) {
        genModal("Message", "Please select a valid image file.", "info");
        return;
    }

    const formData = new FormData();
    formData.append('imageUpdate', file);
    formData.append('userID', id);
    formData.append('accountNumber', accountNumber); 

    fetch('/profileImage', {
        method: 'POST',
        body: formData
    })
    .then(res => res.text())
    .then(data => {
        if (data == "TRUE") {
            genModal("Message", "Image uploaded successfully!", "success");
        }
        else{genModal("Message", data, "info");}
        document.getElementById('generalModal').addEventListener('hidden.bs.modal', function () {location.reload();}); 
    })
    .catch(err => {genModal("Error", err, "danger");});
});

const updateProfile = document.getElementById('updateProfile');

keyEnter("firstName", "updateProfile");
keyEnter("lastName", "updateProfile");

updateProfile.addEventListener('click', () => { //UPDATE PROFILE FIRST AND LAST NAME

    const firstName = document.getElementById('firstName');
    const lastName = document.getElementById('lastName');
    const id = getCookie('id');

    if (isEmpty(firstName) || isEmpty(lastName)) {
        genModal("Message", "First and Last name must not be empty!", "info");
        return;
    }

const userUpdate = {
        userID: id,
        firstName: firstName.value,
        lastName: lastName.value
    };

fetch("/profileUpdate", {
        method: "POST",
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(userUpdate)
    })
    .then(res => res.text())
    .then(data => {

      if (data == "TRUE") {
        setCookie("firstName", firstName.value, 20);
        setCookie("lastName", lastName.value, 20);
        genModal("Message", "Profile update successful!", "success");
        document.getElementById('generalModal').addEventListener('hidden.bs.modal', function () {location.reload();}); 
      } else {genModal("Message", data, "info");}

    })
    .catch(err => {genModal("Error", err, "danger");});

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
        genModal("Message", "All input must not be empty!", "info");
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
        headers: {"Content-Type": "application/json"  },
        body: JSON.stringify(changePassword)
    })
    .then(res => res.text())
    .then(data => {

      if (data == "TRUE") {
        genModal("Message", "PASSWORD HAS BEEN CHANGED!", "success");
      } else {genModal("Message", data, "info");}

    })
    .catch(err => {genModal("Error", err, "danger");});

});

function keyEnter(ID, ID2) {
    document.getElementById(ID).addEventListener("keypress", function(event) {
    if (event.key === "Enter") {event.preventDefault();document.getElementById(ID2).click();}});  
}

function isEmpty(data){if (data.value.trim() === "") {return true;}}