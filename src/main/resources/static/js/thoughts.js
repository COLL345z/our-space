

let thoughts = [];

function addThought() {
    let person = document.getElementById("person").value;
    let message = document.getElementById("message").value;

    if (message.trim() === "") return;

    let time = new Date().toLocaleString();

    thoughts.unshift({
        person,
        message,
        time
    });

    document.getElementById("message").value = "";

    renderThoughts();
}

function renderThoughts() {
    let container = document.getElementById("thoughtList");
    container.innerHTML = "";

    thoughts.forEach(t => {

        let color = t.person === "rehema" ? "#4aa3df" : "#0b3d2e";

        container.innerHTML += `
            <div class="card" style="border-left:5px solid ${color}">
                <b style="color:${color}">
                    ${t.person === "rehema" ? "Rehema 💙" : "Collins 💚"}
                </b>
                <p>${t.message}</p>
                <small>${t.time}</small>
            </div>
        `;
    });
}

