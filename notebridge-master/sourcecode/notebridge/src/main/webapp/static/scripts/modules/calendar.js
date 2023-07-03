// Initializes the calendar
export function initializeCalendar() {
    const calendarBody = document.getElementById("calendar-body");
    const monthYearElement = document.getElementById("month-year");
    let currentDateElement;
    let selectedDate = new Date();

    // Generates the calendar for the specified year and month
    function generateCalendar(year, month) {
        calendarBody.innerHTML = "";

        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        let date = 1;
        for (let i = 0; i < 6; i++) {
            const row = document.createElement("tr");

            for (let j = 0; j < 7; j++) {
                if (i === 0 && j < firstDay) {
                    const cell = document.createElement("td");
                    const cellText = document.createTextNode("");
                    cell.appendChild(cellText);
                    row.appendChild(cell);
                } else if (date > daysInMonth) {
                    break;
                } else {
                    const cell = document.createElement("td");
                    const cellText = document.createTextNode(date);

                    if (
                        date === new Date().getDate() &&
                        year === new Date().getFullYear() &&
                        month === new Date().getMonth()
                    ) {
                        cell.classList.add("current-day");
                    }

                    cell.appendChild(cellText);
                    row.appendChild(cell);
                    date++;
                }
            }

            calendarBody.appendChild(row);

            if (date > daysInMonth) {
                break;
            }
        }
    }

    // Gets the name of the month based on its index
    function getMonthName(month) {
        const monthNames = [
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"
        ];
        return monthNames[month];
    }

    // Updates the calendar with the specified year and month
    function updateCalendar(year, month) {
        generateCalendar(year, month);
        const cells = calendarBody.getElementsByTagName("td");

        for (const element of cells) {
            element.addEventListener("click", function() {
                if (currentDateElement) {
                    currentDateElement.classList.remove("selected-day");
                }

                this.classList.add("selected-day");
                currentDateElement = this;
                selectedDate = new Date(year, month, this.innerHTML);
            });
        }
    }

    // Updates the displayed month and year
    function updateMonthYear(year, month) {
        monthYearElement.textContent = getMonthName(month) + " " + year;
    }

    const date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth();

    updateCalendar(year, month);
    updateMonthYear(year, month);

    const prevMonthElement = document.querySelector(".prev-month");
    prevMonthElement.addEventListener("click", function() {
        if (month === 0) {
            year--;
            month = 11;
        } else {
            month--;
        }

        updateCalendar(year, month);
        updateMonthYear(year, month);
    });

    const nextMonthElement = document.querySelector(".next-month");
    nextMonthElement.addEventListener("click", function() {
        if (month === 11) {
            year++;
            month = 0;
        } else {
            month++;
        }

        updateCalendar(year, month);
        updateMonthYear(year, month);
    });
}

// Returns the month number based on the month name
export function getMonthNumber(monthName) {
    const monthNames = [
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
    ];

    const monthIndex = monthNames.findIndex(name => name.toLowerCase() === monthName.toLowerCase());
    const monthNumber = String(monthIndex + 1).padStart(2, '0');

    return monthNumber;
}

// Marks calendar days as unavailable or available based on the timeslots
export function markCalendarDays(allTimeslots, freeTimeslots, selector) {
    allTimeslots.forEach((timeslot) => {
        markDayAsUnAvailable(timeslot.date, selector);
    });
    freeTimeslots.forEach((timeslot) => {
        markDayAsAvailable(timeslot.date, selector);
    });
}

// Marks a day on the calendar as unavailable
function markDayAsUnAvailable(dateString, selector) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth();

    const monthYearElement = document.getElementById("month-year");
    const [calendarMonth, calendarYear] = monthYearElement.textContent.split(" ");

    if (year === parseInt(calendarYear) && month === getMonthNumber(calendarMonth) - 1) {
        const cells = document.querySelectorAll(`${selector} #calendar-body td`);

        for (const cell of cells) {
            const cellDate = parseInt(cell.textContent);

            if (cellDate === date.getDate()) {
                cell.classList.add("unavailable-day");
            }
        }
    }
}

// Marks a day on the calendar as available
function markDayAsAvailable(dateString, selector) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth();

    const monthYearElement = document.getElementById("month-year");
    const [calendarMonth, calendarYear] = monthYearElement.textContent.split(" ");

    if (year === parseInt(calendarYear) && month === getMonthNumber(calendarMonth) - 1) {
        const cells = document.querySelectorAll(`${selector} #calendar-body td`);

        for (const cell of cells) {
            const cellDate = parseInt(cell.textContent);

            if (cellDate === date.getDate()) {
                cell.classList.add("available-day");
            }
        }
    }
}
