function show(id) {
    let sections = document.querySelectorAll(".section");
    sections.forEach(s => s.classList.remove("active"));
    document.getElementById(id).classList.add("active");
}
const startDate = new Date("2026-01-18");
    const today = new Date();
    const diff = Math.floor((today - startDate) / (1000 * 60 * 60 * 24));

    document.getElementById("days").innerText =
        diff + " days of us ❤️";