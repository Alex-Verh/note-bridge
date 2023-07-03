import { getAllInstruments } from "../api/queries.js";

// Add event listener for opening/closing filter dropdowns
document.querySelectorAll(".filters__field").forEach(e => {
    const header = e.querySelector(".filters__option-header")
    const arrow = e.querySelector(".filters__arrow")
    const dropdown = e.querySelector(".filters__dropdown")

    header.addEventListener("click", () => {
        const isOpen = dropdown.classList.contains("filters__dropdown__open");

        // Close all other open dropdowns and remove arrow rotation
        document.querySelectorAll(".filters__dropdown").forEach(dropdown => {
            dropdown.classList.remove("filters__dropdown__open");
        });

        document.querySelectorAll(".filters__arrow").forEach(arrow => {
            arrow.classList.remove("rotate");
        });

        // Open the clicked dropdown and rotate the arrow
        if (!isOpen) {
            dropdown.classList.add("filters__dropdown__open");
            arrow.classList.add("rotate");
        }
    });
});

// Reset the search input, checkboxes, and selected days
function handleReset(searchInput, checkboxes, selectedDays) {
    checkboxes.forEach((checkbox) => {
        checkbox.checked = false;
    });
    selectedDays.forEach((day) => {
        day.classList.remove('selected-day');
    });
    searchInput.value = '';
}

// Load instruments and add event listener for reset filters
export async function loadInstruments() {
    const instrumentsList = document.querySelector("#instruments-list");
    const instruments = await getAllInstruments();

    // Insert instrument options into the instruments list
    instruments.forEach((instrument) => {
        instrumentsList.insertAdjacentHTML("beforeend", `
            <input id="${instrument.id}" type='radio' name='instrument' />
            <label class="filters__radio filters__option filters__line" for='${instrument.id}'> ${instrument.name} </label>
        `)
    })

    // Add event listener for reset filters
    await document.querySelector(".filters__reset-filters").addEventListener("click", () => {
        handleReset(document.querySelector('#search-location'), document.querySelectorAll('input[type="radio"]'), document.querySelectorAll('.selected-day'))
    })
}
