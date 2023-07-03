import {
    getLesson,
    getProfileData, getTeacherAllAvailability, getTeacherAvailability,
    getTeacherData,
    getTeacherInstruments,
    getTeacherLessons,
} from "../api/queries.js";
import {getMonthNumber, initializeCalendar, markCalendarDays} from "../modules/calendar.js";
import {CSRF_TOKEN, displayStars, SKILL_LEVELS} from "../api/constants.js";
import {
    inputValidation,
    validateMark,
    validateMessage
} from "./validation.js";

// Function to create the teacher modal HTML
export const createTeacherModal = async (profileId) => {
    const generalDetails = await getProfileData(profileId);
    const teacherDetails = await getTeacherData(profileId);
    const teacherLessons = await getTeacherLessons(profileId)
    const teacherInstruments = await getTeacherInstruments(profileId)
    const fullName = generalDetails.full_name.trim();
    const teacherModalHTML = `
        <div class="overlay modal-${profileId}" >
          <div class="view-profile modal" id="view-profile">
            <div class="modal__close"><img src="../../static/images/icons/modal-close.svg" alt="Close"/></div>
            <div class="profile">
              <div class="container">
                <div class="row">
                  <div class="col-0 d-none col-md-4 d-md-block">
                    <div class="profile__image profile-image"><img src="${generalDetails.pfp_path}" alt="Profile"/></div>
                    <div class="profile__info">
                      <p><span class="highlighted-first-color">Experience: </span>${teacherDetails.experience}</p>
                      <p><span class="highlighted-first-color">Type: </span>${generalDetails.online ? "Online" : "Offline"}</p>
                      <p><span class="highlighted-first-color">Instruments: </span></p>
                    </div>
                    <div class="profile__instruments">
                         ${teacherInstruments.map((instrument) => `<div class="profile__instrument">${instrument.name}</div>`).join('')}
                    </div>
                    <div class="profile__buttons">
                      <div class="button"><a href="/profile/${profileId}">Visit Profile</a></div>
                    </div>
                  </div>
                  <div class="col-12 col-md-8">
                    <div class="profile__title"><span class="profile__name">${fullName.split(' ')[0]} <span class="highlighted-first-color">${fullName.split(' ').slice(1).join(' ')}</span></span></div>
                    <div class="profile__status"><span class="profile__type highlighted-first-color">Teacher</span></div>
                    <div class="profile__rating"><span class="profile__mark">${teacherDetails.avgRating}</span>${displayStars(teacherDetails.avgRating)}</div>
                    <div class="button d-block d-md-none" style="margin:0"><a href="/profile/${profileId}">Visit Profile</a></div>
                    <div class="profile__description">${generalDetails.description}</div>
                    <div class="row lessons-list">
                   ${teacherLessons.map((lesson) => `
                      <div class="col-12 col-sm-4 d-flex justify-content-center">
                        <div class="lesson-card" data-lesson-id="${lesson.id}" data-teacher-id="${lesson.teacherId}">
                          <div class="lesson-card__title">${lesson.title.trim().split(' ')[0]} <span class="highlighted-first-color">${lesson.title.trim().split(' ').slice(1).join(' ')}</span></div>
                          <div class="lesson-card__description">${lesson.description}</div>  
                          <div class="lesson-card__level">${SKILL_LEVELS[lesson.skillId]}</div>
                          <div class="lesson-card__price">${lesson.price} &euro;</div>
                        </div>
                      </div>
                    `).join('')}
                   </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>`
    return teacherModalHTML;
}

// Function to create the edit availability modal
export const createEditAvailability = async () => {
    await document.querySelector("body").insertAdjacentHTML("afterbegin", `
    <div class="overlay none">
        <div id="edit-availability" class="edit-availability modal">
            <div class="modal__close">
                <img src="../static/images/icons/modal-close.svg" alt="Close">
            </div>
            <div class="modal__title">
                <span class="highlighted-first-color">Teacher</span>
                Availability
            </div>
            <div class="container">
                <div class="row">
                    <div class="col-12 col-sm-7">
                        <div class="view-availability__title">Select day</div>
                        <div class="view-availability__calendar">
                            <div class="calendar">
                                <div class="month-navigation">
                                    <span class="prev-month">&lt;</span>
                                    <span id="month-year"></span>
                                    <span class="next-month">&gt;</span>
                                </div>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Sun</th>
                                            <th>Mon</th>
                                            <th>Tue</th>
                                            <th>Wed</th>
                                            <th>Thu</th>
                                            <th>Fri</th>
                                            <th>Sat</th>
                                        </tr>
                                    </thead>
                                    <tbody id="calendar-body"></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-5">
                        <div class="view-availability__title">Current timeslots</div>
                        <div class="view-availability__timeslot"></div>
                        <div class="view-availability__add-timeslot">
                            <div class="input-field">
                                <label class="input-label" for="startTime">Start time</label>
                                <input class="input" type="text" name="startTime" id="startTime">
                            </div>
                            <div class="input-field">
                                <label class="input-label" for="finishTime">Finish time</label>
                                <input class="input" type="text" name="finishTime" id="finishTime">
                            </div>
                        </div>
                        <div class="button create-schedule">Add timeslot</div>
                    </div>
                </div>
            </div>
        </div>
    </div>`);
    await initializeCalendar();
}
// Function to create the edit profile modal
export const createEditProfile = (isTeacher) => {
    document.querySelector("body").insertAdjacentHTML("afterbegin", `
    <div class="overlay none">
      <div class="edit-profile modal" id="edit-profile">
        <div class="modal__close"><img src="../static/images/icons/modal-close.svg" alt="Close"/></div>
        <div class="modal__title"><span class="highlighted-first-color">Account</span> Settings</div>
        <div class="container">
          <form id="editProfileForm">
            <div class="row">
              <div class="col-7 col-md-5">
                <div class="input-field">
                  <label class="input-label" for="editFullName">Full Name</label>
                  <input class="input" id="editFullName" type="text" name="editFullName" value=""/>
                </div>`
        + (isTeacher ? `<div class="input-field">
                  <label class="input-label" for="editExperience">Experience</label>
                  <input class="input" id="editExperience" type="text" name="editExperience" value=""/>
                </div>` : ``) +
        `<div class="input-field">
                  <label class="input-label" for="editType">Choose type</label>
                  <div class="choice">
                    <input id="option-1" type="radio" name="is_online" value="1" checked=""/>
                    <input id="option-2" type="radio" name="is_online" value="0"/>
                    <label class="choice__option choice__option-1" for="option-1"><span>Online</span></label>
                    <label class="choice__option choice__option-2" for="option-2"><span>Offline</span></label>
                  </div>
                </div>` + (isTeacher ? `` : `<div class="button become-a-teacher" style="margin-top: 15px">Become a teacher</div>`) +
        `</div>
              <div class="col-5 col-md-3">
                <div class="input-field">
                  <label class="input-label" for="editCountry">Country</label>
                  <input class="input" id="editCountry" type="text" name="editCountry" value=""/>
                </div>
                <div class="input-field">
                  <label class="input-label" for="editCity">City</label>
                  <input class="input" id="editCity" type="text" name="editCity" value=""/>
                </div>`
        + (isTeacher ? `
                <div class="input-field">
                  <label class="input-label" for="editZip">Zip Code</label>
                  <input class="input" id="editZip" type="text" name="editZip" value=""/>
                </div>` : ``) +
        `</div>
        <div class="col-12 col-md-4">
            <div class="profile__image profile-image">
                <img src="" alt="Profile"/>
            </div>
            <label class="button" for="editProfileImage" id="editProfileImageLabel">Upload image</label>
            <input accept=".jpeg, .jpg, .png"  type="file" id="editProfileImage" name="editProfileImage" class="button" style="display: none;"/>
        </div>
            `+ (isTeacher ? `
              <div class="col-12">
                <div class="input-field">
                  <label class="input-label" for="editInstruments">Instruments</label>
                  <div class="profile__instruments">
                    <img class="add-instrument" src="../static/images/icons/add.svg" alt="Add"/>
                    <fieldset class="profile__checkbox none">      
                    </fieldset>
                  </div>
                </div>
            </div>` : ``) +
              `
              <input class="modal__accept" type="submit" value=""/>
            </div>
          </form>
        </div>
        <div class="modal__footer">
          <div class="modal__line"></div>
          <div class="modal__buttons">
            <div class="modal__button delete-account">Delete account</div>
            <div class="modal__button reset-password">Reset password</div>
          </div>
        </div>
      </div>
    </div>
  `);
}
// Function to display a message modal
export const useMessageModal = (message) => {
    const modalOverlay = document.createElement("div");
    modalOverlay.classList.add("overlay");

    const modal = document.createElement("div");
    modal.classList.add("modal", "modal__small");
    modal.insertAdjacentHTML(
        "afterbegin",
        `
      <div class="modal__title">${message}</div>
    `
    );
    modal.style.width = "400px";
    modal.style.paddingBottom = "20px";
    const closeModal = () => {
        modalOverlay.remove();
        document.body.style.overflow = "auto";
    };
    const closeBtn = document.createElement("div");
    closeBtn.classList.add("modal__close");
    closeBtn.innerHTML = `<img src="../static/images/icons/modal-close.svg" alt="Close" />`;
    closeBtn.addEventListener("click", closeModal);
    modal.prepend(closeBtn);

    const confirmBtn = document.createElement("button");
    confirmBtn.innerHTML = "OK";
    confirmBtn.classList.add("button");
    confirmBtn.style.marginTop = "5px";
    confirmBtn.addEventListener("click", closeModal);
    modal.append(confirmBtn);

    modalOverlay.append(modal);
    document.body.append(modalOverlay);
    document.body.style.overflow = "hidden";
};

// Function to display a confirmation modal
export const useConfirmModal = (message, onConfirm) => {
    const modalOverlay = document.createElement("div");
    modalOverlay.classList.add("overlay");

    const modal = document.createElement("div");
    modal.classList.add("modal", "modal__small");
    modal.insertAdjacentHTML(
        "afterbegin",
        `
      <div class="modal__title">${message}</div>
    `
    );
    modal.style.width = "400px";
    modal.style.paddingBottom = "20px";
    const closeModal = () => {
        modalOverlay.remove();
        document.body.style.overflow = "auto";
    };
    const closeBtn = document.createElement("div");
    closeBtn.classList.add("modal__close");
    closeBtn.innerHTML = `<img src="../static/images/icons/modal-close.svg" alt="Close" />`;
    closeBtn.addEventListener("click", closeModal);
    modal.prepend(closeBtn);

    const confirmBtn = document.createElement("button");
    confirmBtn.innerHTML = "YES";
    confirmBtn.classList.add("button");
    confirmBtn.style.margin = "5px";
    confirmBtn.style.display = "inline-block";
    confirmBtn.addEventListener("click", () => {
        onConfirm();
        closeModal();
    });
    modal.append(confirmBtn);

    const rejectBtn = document.createElement("button");
    rejectBtn.innerHTML = "NO";
    rejectBtn.classList.add("button");
    rejectBtn.style.margin = "5px";
    rejectBtn.style.display = "inline-block";
    rejectBtn.addEventListener("click", closeModal);
    modal.append(rejectBtn);

    modalOverlay.append(modal);
    document.body.append(modalOverlay);
    document.body.style.overflow = "hidden";
};
// Function to create the add lesson modal
export function createAddLessonModal() {
    document.querySelector("body").insertAdjacentHTML("afterbegin", `<div class="overlay none">
        <div class="add-lesson modal" id="add-lesson">
            <div class="modal__close"><img src="../static/images/icons/modal-close.svg" alt="Close"/></div>
            <div class="modal__title">Add<span class="highlighted-first-color"> Lesson</span></div>
            <div class="container">
                <form id="addLessonForm" method="POST" action="/api/lessons">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-field">
                                <label class="input-label" for="lessonTitle">Title</label>
                                <input class="input" id="lessonTitle" type="text" name="lessonTitle"/>
                            </div>
                            <div class="input-field">
                                <label class="input-label" for="lessonPrice">Price: (&euro;)</label>
                                <input class="input" id="lessonPrice" type="text" name="lessonPrice"/>
                            </div>
                            <div class="input-field">
                                <label class="input-label" for="lessonDescription">Description:</label>
                                <textarea class="input textarea" id="lessonDescription"
                                          name="lessonDescription"></textarea>
                            </div>
                        </div>
                        <input type="hidden" name="X-CSRF-TOKEN" value="${CSRF_TOKEN}">
                        <div class="col-md-6">
                            <div class="input-field">
                                <label class="input-label">Choose difficulty:</label>
                                <div class="choice">
                                    <input id="level-1" type="radio" name="lessonLevel" value="1"
                                           checked="checked"/>
                                    <input id="level-2" type="radio" name="lessonLevel" value="2"/>
                                    <input id="level-3" type="radio" name="lessonLevel" value="3"/>
                                    <label class="choice__option choice__option-1"
                                           for="level-1"><span>Beginner</span></label>
                                    <label class="choice__option choice__option-2"
                                           for="level-2"><span>Intermediate</span></label>
                                    <label class="choice__option choice__option-3"
                                           for="level-3"><span>Advanced</span></label>
                                </div>
                            </div>
                            <div class="input-field">
                                <label class="input-label">Choose instrument:</label>
                                <div class="profile__instruments"></div>
                            </div>
                        </div>
                    </div>
                    <input class="modal__accept" type="submit" value=""/>
                </form>
            </div>
        </div>
    </div>`);
}
// Function to create a modal for selecting availability
export async function createSelectAvailabilityModal(teacherId) {
    await document.querySelector("body").insertAdjacentHTML("afterbegin",
        `<div class="overlay">
          <div class="view-availability modal view-availability-${teacherId}" id="view-availability">
            <div class="modal__close"><img src="../static/images/icons/modal-close.svg" alt="Close"/></div>
            <div class="modal__title"><span class="highlighted-first-color">Teacher</span> Availability</div>
            <div class="container">
              <div class="row">
                <div class="col-12 col-sm-7">
                  <div class="view-availability__title">Select day</div>
                  <div class="view-availability__calendar">
                    <div class="calendar">
                        <div class="month-navigation">
                            <span class="prev-month">&lt;</span>
                            <span id="month-year"></span>
                            <span class="next-month">&gt;</span>
                        </div>
                        <table>
                            <thead>
                                <tr>
                                    <th>Sun</th>
                                    <th>Mon</th>
                                    <th>Tue</th>
                                    <th>Wed</th>
                                    <th>Thu</th>
                                    <th>Fri</th>
                                    <th>Sat</th>
                                </tr>
                            </thead>
                            <tbody id="calendar-body"></tbody>
                        </table>
                    </div>
                </div>
                </div>
                <div class="col-12 col-sm-5">
                  <div class="view-availability__title">Select timeslot</div>
                  <div class="view-availability__timeslot"></div>
                  <div class="button book-lesson">Book lesson</div>
                </div>
              </div>
            </div>
          </div>
        </div>`
    )
    document.querySelector(`.view-availability-${teacherId} .modal__close`).addEventListener("click", () => {
        document.querySelector(`.view-availability-${teacherId}`).parentElement.remove();
    })
    // Initialize calendar and fetch teacher availability
    await initializeCalendar();
    await fetchTeacherAvailability(teacherId);
}
// Function to generate a modal for adding a review
export async function generateAddReview(lessonId, currenId) {
    const lessonDetails = await getLesson(lessonId);
    const lessonTitle = lessonDetails.title.trim();
    const teacherDetails = await getProfileData(lessonDetails.teacherId);
    const teacherFullName = teacherDetails.full_name.trim();
    await document.querySelector("body").insertAdjacentHTML("afterbegin",
        `<div class="overlay">
                <div class="modal add-review add-review-${lessonId}" id="add-review">
                <div class="modal__close"><img src="../static/images/icons/modal-close.svg" alt="Close"/></div>
                <div class="modal__title">Add<span class="highlighted-first-color"> Review</span></div>
                <div class="container">
                    <form action="/api/reviews" method="POST" id="add-review-form">
                        <div class="row">
                            <div class="col-4">
                                <div class="add-review__teacher"><span>${teacherFullName.split(' ')[0]} </span><span class="highlighted-first-color">${teacherFullName.split(' ').slice(1).join(' ')}</span><span class="add-review__location"><img src="../static/images/icons/location.svg" alt="Location"/>${teacherDetails.city}</span></div>
                                <div class="input-field add-review__mark">
                                    <label class="input-label" for="addMark">Mark</label>
                                    <input class="input" id="addMark" type="text" name="addMark" value="0.0"/>
                                </div>
                            </div>
                            
                            <div class="col-8">
                                <div class="add-review__lesson"><span class="add-review__title">${lessonTitle.split(' ')[0]}<span class="highlighted-first-color"> ${lessonTitle.split(' ').slice(1).join(' ')}</span></span><span class="add-review__description">${lessonDetails.description}</span></div>
                                <div class="input-field add-review__comment">
                                    <label class="input-label" for="reviewComment">Review Comment:</label>
                                    <textarea class="input textarea" id="reviewComment" name="reviewComment"></textarea>
                                </div>
                                <input type="hidden" name="teacherId" value="${lessonDetails.teacherId}">
                                <input type="hidden" name="lessonId" value="${lessonId}">
                                <input type="hidden" name="studentId" value="${currenId}">
                                <input type="hidden" name="X-CSRF-TOKEN" value="${CSRF_TOKEN}">
                            </div>
                        </div>
                        <input class="modal__accept" type="submit" value=""/>
                    </form>
                </div>
            </div>
        </div>`)

    document.querySelector(`.add-review-${lessonId} .modal__close`).addEventListener("click", () => {
        document.querySelector(`.add-review-${lessonId}`).parentElement.remove();
    })
    // Form submission and validation logic
    const addReviewForm = document.querySelector("#add-review-form");
    inputValidation(addReviewForm.addMark, validateMark);
    inputValidation(addReviewForm.reviewComment, validateMessage);

    addReviewForm.addEventListener("submit", (e) => {
        e.preventDefault();
        if (
            validateMark(addReviewForm.addMark.value).isValid &&
            validateMessage(addReviewForm.reviewComment.value).isValid
        ) {
            addReviewForm.submit();
        }
    });
}
// Function to fetch teacher availability and update the calendar
async function fetchTeacherAvailability(teacherId) {
    const freeTimeslots = await getTeacherAvailability(teacherId);
    const allTimeslots = await getTeacherAllAvailability(teacherId);
    const calendar = document.querySelector("#view-availability .view-availability__calendar")

    let availableDays = {};
    let availableTimeslotsId = {};
    let freeTimes = new Set();
    allTimeslots.forEach((timeslot) => {
        const date = timeslot.date.substr(0, 10); // Extract the date portion from the full date-time string

        if (!(date in availableDays)) {
            availableDays[date] = [timeslot.startTime + "-" + timeslot.endTime];
            availableTimeslotsId[date] = [{ id: timeslot.id, timeslot: timeslot.startTime + "-" + timeslot.endTime }];
        } else {
            availableDays[date].push(timeslot.startTime + "-" + timeslot.endTime);
            availableTimeslotsId[date].push({ id: timeslot.id, timeslot: timeslot.startTime + "-" + timeslot.endTime });
        }
    });
    freeTimeslots.forEach((freeTimeslot) => {
        freeTimes.add(parseInt(freeTimeslot.id));
    })
    markCalendarDays(allTimeslots, freeTimeslots, "#view-availability");
    calendar.querySelector(".next-month" ).addEventListener("click", () => {
        markCalendarDays(allTimeslots, freeTimeslots, "#view-availability");
    })
    calendar.querySelector(".prev-month" ).addEventListener("click", () => {
        markCalendarDays(allTimeslots, freeTimeslots, "#view-availability");
    })

    const timeslotContainer = document.querySelector(".view-availability__timeslot");
    calendar.addEventListener("click", (e) => {
        const dayClicked = e.target.closest(".unavailable-day");
        if (dayClicked) {
            const year = document.querySelector("#month-year").innerHTML.split(" ")[1];
            const month = getMonthNumber(document.querySelector("#month-year").innerHTML.split(" ")[0]);
            const day = dayClicked.innerHTML.padStart(2, '0');
            const availableDayChosen = `${year}-${month}-${day}`;
            if (availableDayChosen in availableDays) {
                timeslotContainer.innerHTML = "";
                for (const timeslotObj of availableTimeslotsId[availableDayChosen]) {
                    const timeslotId = timeslotObj.id;
                    const timeslot = timeslotObj.timeslot;
                    if (freeTimes.has(timeslotId)) {
                        timeslotContainer.innerHTML += `
                        <label class="view-availability__timeslots" data-timeslot-id="${timeslotId}">${timeslot}<input type="radio" name="timeslot" value="${timeslot}" /></label>
                    `;
                    } else {
                        timeslotContainer.innerHTML += `
                        <label class="view-availability__timeslots" data-timeslot-id="${timeslotId}">${timeslot}<input type="radio" name="timeslot" value="${timeslot}" disabled/></label>
                    `;
                    }
                }
            }
        } else {
            timeslotContainer.innerHTML = "";
        }
    })
}



