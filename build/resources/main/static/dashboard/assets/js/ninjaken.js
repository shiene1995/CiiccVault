function genModal(title, message, type){

    var generalModal = new bootstrap.Modal(document.getElementById('generalModal'));
    var headerModal = document.getElementById('headerModal');
    var iconModal = document.getElementById('iconModal');
    var messageModal = document.getElementById('messageModalID');

    switch (type) {
    case "success":
        headerModal.classList.add("text-bg-success", "text-white");
        iconModal.innerHTML = '<i class="fas fa-check-square text-success" style="font-size: 30px;"></i>';
        break;
    case "warning":
        headerModal.classList.add("text-bg-warning", "text-white");
        iconModal.innerHTML = '<i class="fas fa-exclamation-circle text-warning" style="font-size: 30px;"></i>';
        break;
    case "danger":
        headerModal.classList.add("text-bg-danger", "text-white");
        iconModal.innerHTML = '<i class="fas fa-exclamation-triangle text-danger" style="font-size: 30px;"></i>';
        break;
    case "info":
        headerModal.classList.add("text-bg-info", "text-white");
        iconModal.innerHTML = '<i class="fas fa-info-circle text-info" style="font-size: 30px;"></i>';
        break;
    }
    
    headerModal.innerText = title;
    messageModal.innerText = message;
    generalModal.show();
}