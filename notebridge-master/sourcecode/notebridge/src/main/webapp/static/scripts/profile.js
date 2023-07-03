import {
    getProfileData,
    getTeacherLessons,
    getTeacherData,
    getTeacherInstruments,
    getTeacherReviews,
    getStudentLessons,
    countTeacherReviews,
    getStudentInstruments,
    countStudentLessons,
    getCurrentId,
    getAllInstruments,
    deleteLesson,
    getIfLessonHasReview,
    deleteReview,
    getTeacherAllAvailability,
    getTeacherAvailability, deleteTimeslot
} from "./api/queries.js";
import {updateNav} from "./modules/nav.js";
import {
    SKILL_LEVELS,
    displayStars,
    getScheduleParams,
    CSRF_TOKEN,
    addEventToBookLessons
} from "./api/constants.js";
import {
    createAddLessonModal, createEditAvailability,
    createEditProfile,
    generateAddReview,
    useConfirmModal,
    useMessageModal
} from "./modules/modal-generator.js";
import {
    inputValidation,
    validateCityCountry, validateLessonDescription,
    validateMessage,
    validateName, validatePrice, validateTime,
    validateTopic, validateZip
} from "./modules/validation.js";
import {getMonthNumber, markCalendarDays} from "./modules/calendar.js";
import {sendMessage} from "./modules/chat.js";

document.addEventListener("DOMContentLoaded",  () => {
    updateNav();

    //Fetching all information from profile
    const currentUrl = window.location.href;
    const profileContainer = document.querySelector(".profile");
    const profileLessons = document.querySelector(".profile-lessons").firstElementChild.firstElementChild;
    let profileData;
    let teacherData;
    async function fetchProfile() {
        const idNumber = currentUrl.split('/').pop();
        try {
            const currentId = await getCurrentId();
            profileData = await getProfileData(idNumber);
            await updateProfile(profileData, currentId);

            try {
                if (profileData.is_teacher) {
                    teacherData = await getTeacherData(profileData.id);
                    await makeTeacher(teacherData, profileData.is_current_profile);
                    if (teacherData) {
                        const teacherLessons = await getTeacherLessons(teacherData.id);
                        addLessonsTeacher(teacherLessons, profileData.is_current_profile, teacherData.id, currentId);
                        const teacherInstruments = await getTeacherInstruments(teacherData.id);
                        removeInstruments();
                        addInstruments(teacherInstruments, true,  profileData.is_current_profile);
                        const teacherReviews = await getTeacherReviews(teacherData.id);
                        addReviews(teacherReviews)
                        const countReviews = await countTeacherReviews(teacherData.id);
                        addReviewsNr(countReviews);
                        if (profileData.is_current_profile) {
                            addLessonEvent();
                            await addEditProfileTeacher(profileData, teacherData, teacherInstruments);
                            await addEditSchedule(teacherData);
                        }
                    }
                } else {
                    const studentInstruments = await getStudentInstruments(profileData.id);
                    removeInstruments();
                    addInstruments(studentInstruments, false, profileData.is_current_profile);
                    const countLearnedLessons = await countStudentLessons(profileData.id);
                    addCountLessons(countLearnedLessons);
                    const studentLessons = await getStudentLessons(profileData.id);
                    addStudentLessons(studentLessons, profileData.is_current_profile, currentId);
                    if (profileData.is_current_profile) {
                        addEditProfileStudent(profileData);
                    }
                }
            } catch (e) {
                console.log(e);
            }
        } catch (e) {
            console.log(e);
        }
    }

    //Update profile information
    function updateProfile(userData, currentId) {

        //Update full-name
        const fullName = userData.full_name.trim();
        const city = userData.city.trim();
        const isCurrent = userData.is_current_profile;
        const description = userData.description.trim();
        const profileType = userData.online;
        const profileImage = userData.pfp_path;
        if (isCurrent === true) {
            profileContainer.querySelector(".profile__buttons").insertAdjacentHTML("afterbegin", `
                <div class="button" data-modal-class="edit-profile">Edit Profile<img src="../static/images/icons/edit-profile.svg" alt=""/></div>
                <div class="button logout">Log Out</div>
            `);
            document.querySelector(".profile__buttons .logout").addEventListener("click", () => {
                useConfirmModal("Do you want to logout?", () => {
                    fetch("/logout", {
                        method: 'GET',
                        headers: {
                            'X-CSRF-TOKEN': CSRF_TOKEN
                        }
                    }).then(() => {
                        window.location.assign("/main");
                    })
                })
            })
        } else if (isCurrent === false && currentId) {
            profileContainer.querySelector(".profile__buttons").insertAdjacentHTML("afterbegin", `
                <div class="button send-msg-profile">Send message</div>
            `);

            document.querySelector(".send-msg-profile").addEventListener("click", () => {
                sendMessage(currentId, userData.id, "Good day!")
            })
        }

        if (profileType === true) {
            profileContainer.querySelector(".profile__info").insertAdjacentHTML("afterbegin", `
            <p><span class="highlighted">Type:</span> Online</p>
        `);
        } else {
            profileContainer.querySelector(".profile__info").insertAdjacentHTML("afterbegin", `
            <p><span class="highlighted">Type:</span> Offline</p>
        `);
        }

        profileContainer.querySelector(".profile__image").firstElementChild.setAttribute("src", profileImage);

        profileContainer.querySelector(".profile__name").innerHTML = `
        ${fullName.split(' ')[0]}
        <span class="highlighted">${fullName.split(' ').slice(1).join(' ')}</span>
        `;
        const profileDescription = profileContainer.querySelector(".profile__description");
        if ((description === "UNKNOWN" && isCurrent === false) || description !== "UNKNOWN") {
            profileDescription.innerHTML = description;
        }
        if (isCurrent) {
            profileDescription.innerHTML += '<img class="description-edit make-description-edit" src="../static/images/icons/edit-profile.svg" alt="Edit">'
            profileContainer.querySelector(".make-description-edit").addEventListener("click", () => {
                profileDescription.innerHTML = `<textarea type="text" name="profileDescription" class="input textarea edit-description-input"></textarea><img class="description-edit accept-description-edit" src="../static/images/icons/add.svg" alt="Accept"/>`;
                profileDescription.querySelector(".edit-description-input").value = description
                inputValidation(profileDescription.querySelector(".edit-description-input"), validateMessage);
                profileContainer.querySelector(".accept-description-edit").addEventListener("click", () => {
                    const profileDescriptionInputData = document.querySelector(".edit-description-input").value;
                    if (validateMessage(profileDescriptionInputData).isValid) {
                        fetch('/api/users/description', {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json',
                                'X-CSRF-TOKEN': CSRF_TOKEN
                            },
                            body: JSON.stringify({ profileDescriptionInputData })
                        }).then(() => {
                            window.location.assign('/profile');
                        })
                    }
                })
            })
        }

        profileContainer.querySelector(".profile__location").innerHTML = `
        <img src="../static/images/icons/location.svg" alt="Location">
        ${city}`;

    }
    //If a teacher profile update information on profile to teacher
    async function makeTeacher(teacherData, isCurrent) {
        const profileStatus = document.querySelector(".profile__status");
        profileStatus.innerHTML = "Teacher";
        if (isCurrent) {
            document.querySelector(".profile__buttons").insertAdjacentHTML("afterbegin", `
            <div class="button" data-modal-class="edit-availability">
                Availability
                <img src="../static/images/icons/calendar.svg" alt="">
            </div>
        `);
        }
        if (isCurrent) {
            createAddLessonModal();
        }

        document.querySelector(".profile__info").insertAdjacentHTML("afterbegin", `
            <p><span class="highlighted">Experience:</span> ${teacherData.experience}</p>
        `);

        const addTeacherVideo = isCurrent ? `
        <div class="col-sm-6 col-md-3 d-flex justify-content-center" style="position:relative">
            <video class="profile-lessons__video" loop muted autoplay controls>
                <source src="${teacherData.videoPath}" type="video/mp4"/>
                Your browser does not support the video tag.
            </video>
            <div class="lesson-card__edit-bar">
                <input type="file" accept="video/mp4" class="none" id="profileEditVideo" name="profileEditVideo">
                <span class="lesson-card__video" id="uploadVideo"  style="color:#F5F5F5; cursor: pointer;">Upload video</span>
            </div>
        </div> 
        ` : `
        <div class="col-sm-6 col-md-3 d-flex justify-content-center">
          <video class="profile-lessons__video" loop muted autoplay controls>
            <source src="${teacherData.videoPath}" type="video/mp4"/>
            Your browser does not support the video tag.
          </video>
        </div>`
        document.querySelector(".profile-lessons").firstElementChild.firstElementChild.insertAdjacentHTML("afterbegin", addTeacherVideo);

        if (isCurrent) {
            const profileVideoInput = document.getElementById('profileEditVideo');
            const profileVideoLabel = document.getElementById('uploadVideo');

            profileVideoLabel.addEventListener('click', function() {
                profileVideoInput.click();
            });
            profileVideoInput.addEventListener('change', () => {
                const file = profileVideoInput.files[0];
                if (file) {
                    const allowedFormats = ['video/mp4'];
                    if (allowedFormats.includes(file.type)) {
                        const formData = new FormData();
                        formData.append('editProfileVideo', file);
                        fetch('/upload/video', {
                            method: 'PUT',
                            headers : {
                                'X-CSRF-TOKEN': CSRF_TOKEN
                            },
                            body: formData
                        }).then(() => {
                            window.location.assign('/profile');
                        })
                    } else {
                        useMessageModal('Invalid file format. Please upload an image in MP4 format.');
                    }
                }
            });
        }

        profileStatus.insertAdjacentHTML("afterend", `
            <div class="profile__rating">
                <span class="profile__mark">${teacherData.avgRating}</span>
                ${displayStars(teacherData.avgRating)}
                <span data-modal-class="view-review" class="view-rating">View reviews</span>
            </div>`
        );
    }

    //Adding an edit availability modal if current profile and teacher
    async function addEditSchedule(teacherData) {
        await createEditAvailability();
        const calendar = document.querySelector("#edit-availability .view-availability__calendar")
        const freeTimeslots = await getTeacherAvailability(teacherData.id);
        const allTimeslots = await getTeacherAllAvailability(teacherData.id);

        markCalendarDays(allTimeslots, freeTimeslots, "#edit-availability");

        calendar.querySelector(".next-month" ).addEventListener("click", () => {
            markCalendarDays(allTimeslots, freeTimeslots, "#edit-availability");
        })
        calendar.querySelector(".prev-month" ).addEventListener("click", () => {
            markCalendarDays(allTimeslots, freeTimeslots, "#edit-availability");
        })

        let availableDays = {};
        let availableTimeslotsId = {};

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

        const timeslotContainer = document.querySelector(".view-availability__timeslot");
        document.querySelector("#edit-availability").addEventListener("click", (e) => {
            const dayClicked = e.target.closest(".unavailable-day");
            const removeDay = e.target.closest(".view-availability__remove");
            const addTimeslot = e.target.closest(".view-availability__add-timeslot");
            if (dayClicked) {
                const year = document.querySelector("#month-year").innerHTML.split(" ")[1];
                const month = getMonthNumber(document.querySelector("#month-year").innerHTML.split(" ")[0]);
                const day = dayClicked.innerHTML.padStart(2, '0');
                const dayChosen =`${year}-${month}-${day}`;
                if (dayChosen in availableDays) {
                    timeslotContainer.innerHTML = "";
                    for (const timeslotObj of availableTimeslotsId[dayChosen]) {
                        const timeslotId = timeslotObj.id;
                        const timeslot = timeslotObj.timeslot;
                        timeslotContainer.innerHTML += `<label>${timeslot}<div class="view-availability__remove" data-timeslot-id="${timeslotId}">Remove</div></label>`;
                    }
                }
            } else if (removeDay) {
                if (document.querySelector("#edit-availability .selected-day").classList.contains("available-day")) {
                    deleteTimeslot(removeDay.getAttribute("data-timeslot-id"));
                    useMessageModal("The time slot has been successfully removed!")
                    setTimeout(function() {
                        window.location.assign('/profile');
                    }, 2000);
                } else {
                    useMessageModal("This timeslot is booked by a student!")
                }
            } else if (!addTimeslot) {
                timeslotContainer.innerHTML = "";
            }
        })


        //Creating new schedule timeslots
        const startTimeInput = document.querySelector("#startTime");
        const finishTimeInput = document.querySelector("#finishTime");
        inputValidation(startTimeInput, validateTime);
        inputValidation(finishTimeInput, validateTime)
        await document.querySelector("#edit-availability .create-schedule").addEventListener("click", () => {
            const selectedDay = calendar.querySelector(".selected-day");
            if (selectedDay && selectedDay.innerHTML !== '') {
                const year = document.querySelector("#month-year").innerHTML.split(" ")[1];
                const month = getMonthNumber(document.querySelector("#month-year").innerHTML.split(" ")[0]);
                const day = selectedDay.innerHTML.padStart(2, '0');
                const dateSelected =`${year}-${month}-${day}`;
                if (
                    validateTime(startTimeInput.value).isValid &&
                    validateTime(finishTimeInput.value).isValid
                ) {
                    const scheduleParams = getScheduleParams(parseInt(teacherData.id), dateSelected, startTimeInput.value, finishTimeInput.value);
                    console.log(scheduleParams);
                    fetch('/api/schedule', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-CSRF-TOKEN': CSRF_TOKEN
                        },
                        body: JSON.stringify(scheduleParams)
                    }).then(() => {
                        useMessageModal("The schedule has been successfully added!")
                        setTimeout(function() {
                            window.location.assign('/profile');
                        }, 2000);
                    })
                } else {
                    useMessageModal("Invalid timeslot indicated!")
                }

            } else {
                useMessageModal("Select a day before adding a timeslot!")
            }
        })
    }
    //Adding an edit profile for a student
    function addEditProfileStudent(profileData) {
        createEditProfile(false)
        updateEditProfile(profileData);


        let editProfileForm = document.querySelector("#editProfileForm");
        inputValidation(editProfileForm.editFullName, validateName);
        inputValidation(editProfileForm.editCountry, validateCityCountry);
        inputValidation(editProfileForm.editCity, validateCityCountry);
        editProfileForm.addEventListener("submit", (e) => {
            e.preventDefault();
            if (
                validateName(editProfileForm.editFullName.value).isValid &&
                validateCityCountry(editProfileForm.editCountry.value).isValid &&
                validateCityCountry(editProfileForm.editCity.value).isValid
            ) {
                const formData = new FormData(editProfileForm);
                fetch("/api/users", {
                    method: 'PUT',
                    body: formData,
                    headers : {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    window.location.assign('/profile');
                })

            }
        });
        document.querySelector("#edit-profile .become-a-teacher").addEventListener("click", () => {
            useConfirmModal("Do you want to become a teacher?", () => {
                fetch("/become-teacher", {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    window.location.assign("/profile");
                })
            })
        })

    }
    //Adding an edit profile for a teacher
    async function addEditProfileTeacher(profileData, teacherData, instruments) {
        createEditProfile(true)
        updateEditProfile(profileData);
        document.querySelector("#editExperience").value = teacherData.experience;
        document.querySelector("#editZip").value = teacherData.zipcode;
        const allInstruments = await getAllInstruments();
        allInstruments.forEach((instrument) => {
            document.querySelector(".profile__checkbox").innerHTML +=
                `<div>
                      <input type="checkbox" id="instrument-${instrument.id}" name="instrument" value="${instrument.id}">
                      <label class="instrument" for="instrument-${instrument.id}">${instrument.name}</label>
                </div>`
        });
        instruments.forEach((teacherInstrument) => {
            document.querySelector(`#instrument-${teacherInstrument.id}`).checked = true;
            const teacherInstrumentHTML = `<div class="profile__instrument instrument-${teacherInstrument.id}" data-instrument-id="${teacherInstrument.id}">${teacherInstrument.name}<img src="../static/images/icons/remove.svg" alt="Remove"/></div>`
            document.querySelector("#edit-profile .profile__instruments").insertAdjacentHTML('afterbegin', teacherInstrumentHTML);
            document.querySelector(`#instrument-${teacherInstrument.id}`).nextElementSibling.classList.add("none");
        })
        document.querySelector(".profile__checkbox").addEventListener("click", (e) => {
            const checkedInstrument = e.target.closest(".instrument");
            if (checkedInstrument) {
                const checkedInstrumentId = parseInt(checkedInstrument.getAttribute("for").split("-")[1]);
                const checkedInstrumentName = checkedInstrument.innerHTML;
                if (checkedInstrument.previousSibling.checked === true) {
                    checkedInstrument.previousSibling.checked = false;
                    document.querySelector(".profile__checkbox").classList.add("none");
                    document.querySelector(`.instrument-${checkedInstrumentId}`).remove()
                } else {
                    checkedInstrument.previousSibling.checked = true;
                    document.querySelector(`#instrument-${checkedInstrumentId}`).nextElementSibling.classList.add("none");
                    document.querySelector(".profile__checkbox").classList.add("none");
                    document.querySelector(".profile__instruments").insertAdjacentHTML('afterbegin', `
                    <div class="profile__instrument instrument-${checkedInstrumentId}" data-instrument-id="${checkedInstrumentId}">${checkedInstrumentName}<img src="../static/images/icons/remove.svg" alt="Remove"/></div>
                    `)
                }
            }
        })
        document.querySelector("#edit-profile .profile__instruments").addEventListener("click", (e) => {
            const removingInstrument = e.target.closest(".profile__instrument");
            const displayInstruments = e.target.closest(".add-instrument");
            if (removingInstrument) {
                const removingInstrumentId = removingInstrument.getAttribute("data-instrument-id");
                document.querySelector(`.instrument-${removingInstrumentId}`).remove();
                document.querySelector(`#instrument-${removingInstrumentId}`).checked = false;
                document.querySelector(`#instrument-${removingInstrumentId}`).nextElementSibling.classList.remove("none");
                removingInstrument.remove();
            }
            if (displayInstruments) {
                document.querySelector(".profile__checkbox").classList.remove("none");
            }
        })

        let editProfileForm = document.querySelector("#editProfileForm");
        inputValidation(editProfileForm.editFullName, validateName);
        inputValidation(editProfileForm.editCountry, validateCityCountry);
        inputValidation(editProfileForm.editCity, validateCityCountry);
        inputValidation(editProfileForm.editZip, validateZip);
        inputValidation(editProfileForm.editExperience, validateTopic);
        editProfileForm.addEventListener("submit", (e) => {
            e.preventDefault();
            if (
                validateName(editProfileForm.editFullName.value).isValid &&
                validateCityCountry(editProfileForm.editCountry.value).isValid &&
                validateCityCountry(editProfileForm.editCity.value).isValid &&
                validateTopic(editProfileForm.editExperience.value).isValid &&
                validateZip(editProfileForm.editZip.value).isValid
            ) {
                const formData = new FormData(editProfileForm);
                fetch("/api/teachers", {
                    method: 'PUT',
                    body: formData,
                    headers : {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    window.location.assign('/profile');
                })

            }
        });
    }
    //Adding edit profile for both type of accounts
    function updateEditProfile(profileData) {
        document.querySelector("#editFullName").value = profileData.full_name;
        if (profileData.online) {
            document.querySelector("#option-1").checked = true;
        } else {
            document.querySelector("#option-2").checked = true;
        }
        document.querySelector("#editCountry").value = profileData.country;
        document.querySelector("#editCity").value = profileData.city;
        document.querySelector("#edit-profile .profile__image img").src = profileData.pfp_path;

        const profileImageInput = document.getElementById('editProfileImage');
        const profileImageLabel = document.getElementById('editProfileImageLabel');

        profileImageLabel.addEventListener('click', function() {
            profileImageInput.click();
        });
        profileImageInput.addEventListener('change', () => {
            const file = profileImageInput.files[0];
            if (file) {
                const allowedFormats = ['image/jpeg', 'image/jpg', 'image/png'];
                if (allowedFormats.includes(file.type)) {
                    const formData = new FormData();
                    formData.append('editProfileImage', file);
                    fetch('/upload/image', {
                        method: 'PUT',
                        body: formData,
                        headers : {
                            'X-CSRF-TOKEN': CSRF_TOKEN
                        }
                    }).then(() => {
                        window.location.assign('/profile');
                    })
                } else {
                    useMessageModal('Invalid file format. Please upload an image in JPEG, JPG, or PNG format.');
                }
            }
        });

        document.querySelector("#edit-profile .delete-account").addEventListener("click", () => {
            useConfirmModal("Do you want to delete your account?", () => {
                fetch("/delete-profile", {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    window.location.assign("/main");
                })
            })
        })
        document.querySelector("#edit-profile .reset-password").addEventListener("click", () => {
            useConfirmModal("Do you want to reset your password?", () => {
                window.location.assign("/reset-password");
            })
        })
    }
    //Adding adding instruments
    function addInstruments(instruments, isTeacher, currentUser) {
        instruments.forEach((instrument) => {
            profileContainer.querySelector(".profile__instruments").innerHTML += `<div class="profile__instrument">${instrument.name}</div>`;
            if (isTeacher && currentUser) {
                document.querySelector("#add-lesson .profile__instruments").innerHTML += `
                <label class="profile__instrument" for="lessonInstr-${instrument.id}">${instrument.name}</label>
                <input type="radio" id="lessonInstr-${instrument.id}" name="lessonInstrument" value="${instrument.id}">`;
            }
        })
    }
    //Adding lesson event listeners
    function addLessonEvent() {
        if (document.querySelector("#add-lesson .profile__instruments").firstElementChild)  {
            document.querySelector("#add-lesson .profile__instruments").firstElementChild.classList.add("selected-instrument");
            document.querySelector("#add-lesson .profile__instruments").firstElementChild.nextElementSibling.checked = true;
            document.querySelector("#add-lesson .profile__instruments").addEventListener("click", (e) => {
                const selectedInstrument = e.target.closest("#add-lesson .profile__instrument");
                if (selectedInstrument) {
                    document.querySelectorAll("#add-lesson .profile__instrument").forEach((instrument) => {
                        instrument.classList.remove("selected-instrument");
                    })
                    selectedInstrument.classList.add("selected-instrument");
                }
            });
        }
        const addLessonForm = document.querySelector("#addLessonForm");
        inputValidation(addLessonForm.lessonTitle, validateTopic);
        inputValidation(addLessonForm.lessonPrice, validatePrice);
        inputValidation(addLessonForm.lessonDescription, validateLessonDescription);
        addLessonForm.addEventListener("submit", (e) => {
            e.preventDefault();
            if (
                validateTopic(addLessonForm.lessonTitle.value).isValid &&
                validatePrice(addLessonForm.lessonPrice.value).isValid &&
                validateLessonDescription(addLessonForm.lessonDescription.value, true).isValid
            ) {
                if (!document.querySelector("#add-lesson .profile__instruments").firstElementChild) {
                    document.querySelector("#add-lesson .profile__instruments").innerHTML = "<span style='color:#FF8D80'>Add some instruments to your profile to be able to create lessons!</span>"
                } else {
                    addLessonForm.submit();
                }
            }
        });
    }
    //Adding lessons counter for stats for students
    function addCountLessons(countLearnedLessons) {
        document.querySelector(".profile__info").insertAdjacentHTML("afterbegin", `
            <p><span class="highlighted">Lessons:</span> ${countLearnedLessons} learned</p>
        `);
    }
    //Adding reviews counter for stats for teacher
    function addReviewsNr(number) {
        document.querySelector(".profile__info").insertAdjacentHTML("afterbegin", `
            <p><span class="highlighted">Reviews:</span> ${number + (number === 1 ? " student" : " students")}</p>
        `);
    }
    //Removing instruments
    function removeInstruments() {
        profileContainer.querySelector(".profile__instruments").innerHTML = "";
    }
    //Adding reviews to teacher
    function addReviews(reviews) {
        const reviewModalContainer = document.querySelector(".view-review__container");
        if (reviews.length > 0) {
            reviews.forEach((review) => {
                const reviewerName = review.studentName.trim();
                reviewModalContainer.insertAdjacentHTML("beforeend", `
                <div class="row">
                  <div class="col-md-2 d-md-flex align-items-center justify-content-end d-none">
                    <div class="view-review__student">
                      <span>${reviewerName.split(' ')[0]}</span>
                      <span class="highlighted-first-color">${reviewerName.split(' ').slice(1).join(' ')}</span>
                      <span class="view-review__location">
                        <img src="../static/images/icons/location.svg" alt="Location" />
                        ${review.studentCity}
                      </span>
                    </div>
                  </div>
                  <div class="col-md-2 col-4">
                    <div class="input-field view-review__mark">
                      <label class="input-label" for="#">Mark</label>
                      <input id="view-mark" class="input" type="text" name="view-mark" value="${review.rating}" readonly="" />
                    </div>
                  </div>
                  <div class="col-8">
                    <div class="input-field view-review__comment">
                      <label class="input-label" for="r#">Review Comment:</label>
                      <textarea id="viewReviewComment" class="textarea input" name="view-review-comment" readonly="">${review.comment}</textarea>
                    </div>
                  </div>
                </div>      
            `)
            })
        } else {
            reviewModalContainer.innerHTML += `<div class="view-review__no-reviews">This teacher <span class="highlighted">does not</span> have any reviews yet!</div>`
        }
    }
    //Adding lesson of a teacher
    function addLessonsTeacher(lessons, isCurrentUser, teacherId, currentId) {
        lessons.forEach((lesson) => {
            const ifCurrentUser = isCurrentUser ?`
            <div class="lesson-card__edit-bar">
                <span class="lesson-card__delete" data-lesson-id="${lesson.id}">Delete <img src="../static/images/icons/delete-card.svg" alt=""/></span>
            </div>` : '';
            const lessonSkill = lesson.skillId;
            const lessonTitle = lesson.title.trim();
            profileLessons.insertAdjacentHTML("beforeend", `
            <div class="col-sm-6 col-md-3 d-flex justify-content-center">
              <div class="lesson-card" data-teacher-id="${teacherId}" data-lesson-id="${lesson.id}">
                <div class="lesson-card__title">${lessonTitle.split(' ')[0]} <span class="highlighted">${lessonTitle.split(' ').slice(1).join(' ')}</span></div>
                <div class="lesson-card__description">${lesson.description}</div>
                <div class="lesson-card__level">${SKILL_LEVELS[lessonSkill]}</div>
                <div class="lesson-card__price">${lesson.price} &euro;</div>`
                + ifCurrentUser +
              `</div>
            </div>`
            )
        })
        document.querySelectorAll(".lesson-card__delete") && document.querySelectorAll(".lesson-card__delete").forEach((delLesson) => {
            delLesson.addEventListener("click",  () => {
                useConfirmModal("Do you want to delete your lesson?", async () => {
                    await deleteLesson(delLesson.getAttribute("data-lesson-id")).then(() => {
                        window.location.assign('/profile');
                    });
                })
            })
        })
        const countLessons = Object.keys(lessons).length;
        if (countLessons < 3 && isCurrentUser) {
            while (profileLessons.childElementCount < 4) {
                profileLessons.insertAdjacentHTML("beforeend", `
                <div class="col-sm-6 col-md-3 d-flex justify-content-center">
                    <div class="profile-lessons__add-lesson" data-modal-class="add-lesson">
                        <img src='../static/images/add-lesson.png' alt='Add Lesson'/>
                    </div>
                </div>
                `)
            }
        }
        // Booking
        if (!isCurrentUser) {
            addEventToBookLessons(currentId);
        }
    }
    //Adding lessons learned by a student
    function addStudentLessons(lessons, isCurrent, currentId) {
        lessons.forEach(async (lesson) => {
            const isCurrentProfileHasReviewOnLesson = await getIfLessonHasReview(lesson.id, currentId);
            const ifCurrent = isCurrent ? (parseInt(isCurrentProfileHasReviewOnLesson) > 0 ? `<span class="lesson-card__view-rating delete-review" data-lesson-id="${lesson.id}">Delete review</span></div></div>` : `<span class="lesson-card__view-rating add-review" data-lesson-id="${lesson.id}">Add review</span></div></div>` ) : `<div class="lesson-card__price">${lesson.price} &euro;</div></div></div>`;
                const lessonSkill = lesson.skillId;
            const lessonTitle = lesson.title.trim();
            profileLessons.insertAdjacentHTML("beforeend", `
            <div class="col-sm-6 col-md-3 d-flex justify-content-center">
              <div class="lesson-card" data-teacher-id="${lesson.teacherId}">
                <div class="lesson-card__title">${lessonTitle.split(' ')[0]} <span class="highlighted">${lessonTitle.split(' ').slice(1).join(' ')}</span></div>
                <div class="lesson-card__description">${lesson.description}</div>
                <div class="lesson-card__level">${SKILL_LEVELS[lessonSkill]}</div>`
                + ifCurrent)
        });
        if (isCurrent) {
            profileLessons.addEventListener("click", async (e) => {
                const clickedDeleteReview = e.target.closest(".delete-review");
                const clickedAddReview = e.target.closest(".add-review");
                if (clickedDeleteReview) {
                    await deleteReview(clickedDeleteReview.getAttribute("data-lesson-id"), currentId);
                    window.location.assign('/profile');

                } else if (clickedAddReview) {
                    await generateAddReview(clickedAddReview.getAttribute("data-lesson-id"), currentId);
                }
            })
        }
        profileLessons.addEventListener("click", (e) => {
            const clickedLessonTitle = e.target.closest(".lesson-card__title");
            const clickedLessonDescription = e.target.closest(".lesson-card__description");
            if (clickedLessonTitle || clickedLessonDescription) {
                let teacher = e.target.parentElement.getAttribute("data-teacher-id")
                teacher && window.location.assign(`/profile/${teacher}`)
            }
        })

        if (lessons.length === 0 && isCurrent) {
            profileLessons.insertAdjacentHTML("beforeend", `
            <div class="profile__no-lessons">There are <span class="highlighted-first-color">no lessons </span>taken yet! Try and find some in your <span class="highlighted-first-color">region</span> or <span class="highlighted-first-color">search</span> some specific lesson you want to take.</div>
            `);
        }
    }

    fetchProfile().then(() => {
        import("./modules/modal.js");
    });
});


const currentURL = window.location.href;
if (currentURL.includes("error")) {
    useMessageModal("Can't add more than 3 lessons")
}