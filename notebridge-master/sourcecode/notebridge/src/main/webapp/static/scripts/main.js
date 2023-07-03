import {countInstruments, countCities, countTeachers, countStudents} from "./api/queries.js";
import {updateNav} from "./modules/nav.js";

document.addEventListener("DOMContentLoaded",  () => {
    import("./modules/modal.js");

    updateNav();

    // FAQ section
    const questions = document.querySelector(".help__questions");
    questions.addEventListener("click", (event) => {
        const question = event.target.closest(".help__question");
        if (question) {
            const answer = question.querySelector(".help__answer");
            const isOpen = answer.classList.contains("none");
            const openQuestions = questions.querySelectorAll(".help__question .help__answer:not(.none)");
            openQuestions.forEach((openQuestion) => {
                openQuestion.classList.add("none");
            });
            answer.classList.toggle("none", !isOpen);
        }
    });

    // Stats section
    async function fetchStatistics() {
        try {
            const instrumentsNr = await countInstruments();
            const studentsNr = await countStudents();
            const teachersNr = await countTeachers();
            const citiesNr = await countCities();

            uploadStatistics(citiesNr, studentsNr, instrumentsNr, teachersNr);
        } catch (error) {
            console.error("Error fetching statistics:", error);
        }
    }

    // Puts stats on the page
    const uploadStatistics = (citiesNr, studentsNr, instrumentsNr, teachersNr) => {
        document.querySelector(".statistics").innerHTML = `
        <div class="statistics__element">
            <p class="statistics__number">${citiesNr}</p>
            <p class="statistics__label">Cities</p>  
        </div>
        <div class="statistics__element">
            <p class="statistics__number">${teachersNr}</p>
            <p class="statistics__label">Teachers</p>  
        </div>
        <div class="statistics__element">
            <p class="statistics__number">${instrumentsNr}</p>
            <p class="statistics__label">Instruments</p>  
        </div>
        <div class="statistics__element">
            <p class="statistics__number">${studentsNr}</p>
            <p class="statistics__label">Students</p>  
        </div>
    `;
    }

    fetchStatistics();
});