function toggleDropdown() {
    document.getElementById("dropdownBox").classList.toggle("active");
}

document.addEventListener("click", function (e) {
    const box = document.querySelector(".dropdown-checkbox");
    if (box && !box.contains(e.target)) {
        document.getElementById("dropdownBox").classList.remove("active");
    }
});

document.addEventListener("DOMContentLoaded", function () {

    const tableCheckboxes = document.querySelectorAll('.table-checkbox');

    tableCheckboxes.forEach(cb => {
        cb.addEventListener('change', () => toggleHighlight(cb));
    });

    function toggleHighlight(checkbox) {
        const label = checkbox.closest('.checkbox-item');
        if (!label) return;

        if (checkbox.checked) {
            label.classList.add('selected');
        } else {
            label.classList.remove('selected');
        }
    }

    // ✅ Restore highlight after POST
    tableCheckboxes.forEach(cb => toggleHighlight(cb));
});

function validateSelection() {
    const checked = document.querySelectorAll("input[name='selectedTables']:checked");

    if (checked.length === 0) {
        alert("Please select at least one table.");
        return false;
    }

    // ✅ DEBUG what is actually sent
    try {
        const form = document.querySelector("form");
        const fd = new FormData(form);
        console.log("FORM DATA BEING SENT:");
        for (const p of fd.entries()) {
            console.log(p[0], "=", p[1]);
        }
    } catch (e) {
        console.log("FormData debug failed:", e);
    }

    return true;
}

function updateDownloadButtons() {
    const incidentId = document.getElementById("incidentId").value.trim();
    const jsonData = document.getElementById("jsonDataField").value.trim();

    const disable = (incidentId === "" || jsonData.length < 3);

    document.getElementById("csvBtn").disabled = disable;
    document.getElementById("excelBtn").disabled = disable;
}

// Sync incident ID into hidden fields before submit
function copyIncidentId() {
    let id = document.getElementById("incidentId").value.trim();
    document.getElementById("incidentIdHidden1").value = id;
    document.getElementById("incidentIdHidden2").value = id;
}

// Register listeners
document.getElementById("incidentId").addEventListener("input", updateDownloadButtons);

// Initialize state after page loads
updateDownloadButtons();
