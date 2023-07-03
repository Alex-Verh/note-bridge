import {updateNav} from "./modules/nav.js";
import {getCurrentId, getLessons, getProfileData, getTeacherData} from "./api/queries.js";
import {addEventToBookLessons, displayStars, SKILL_LEVELS} from "./api/constants.js";
import {
    createTeacherModal,
} from "./modules/modal-generator.js";
import {getMonthNumber, initializeCalendar} from "./modules/calendar.js";
import {loadInstruments} from "./modules/filter.js";


document.addEventListener("DOMContentLoaded",  async () => {
    import("./modules/modal.js");
    import("./modules/calendar.js");
    import("./modules/filter.js");

    document.querySelector("body").insertAdjacentHTML("afterbegin", `<div id="loader" class="overlay"><img src="../static/images/loader.gif" alt="Loader" /></div>`);
    const loader = document.querySelector("#loader");
    document.body.style.overflow = "hidden";


    await updateNav();
    await loadInstruments();
    await initializeCalendar();
    const currentId = parseInt(await getCurrentId());
    addEventToBookLessons(currentId);

    // Loads lessons on the page
    const loadLessons = (requestBody, lessonDash, loadMoreBtn) => {
        return new Promise(async (resolve) => {
            try {
                const data = await getLessons(requestBody);

                if (!data.length) {
                    lessonDash.innerHTML = `            
                      <div class="lessons__no-lessons"><span class="lessons__no-lessons_message">
                          <p class="lessons__no-lessons_title">No more<span class="highlighted"> lessons.</span> Try something<span class="highlighted"> different.</span></p>
                          <p class="lessons__no-lessons_text">Adjust the<span class="highlighted-first-color"> filters.</span> or choose<span class="highlighted-first-color"> online</span> learning instead!</p></span>
                          <img src="../static/images/lessons-illustration.svg" alt="Illustration"/>
                      </div>`;
                    loader.classList.add("none");
                    document.body.style.overflow = "scroll";
                    resolve();
                    return;
                }

                await Promise.all(data.map(async (teacher) => {
                    if (!lessonDash.querySelector("#teacher-" + teacher[0].teacherId)) {
                        lessonDash.insertAdjacentHTML("beforeend", await getTeacherHTML(teacher));
                    }
                    await Promise.all(teacher.map(async (lessonOfTeacher) => {
                        lessonDash.querySelector("#teacher-" + teacher[0].teacherId).firstElementChild.insertAdjacentHTML("beforeend", await getLessonHTML(lessonOfTeacher));
                    }));
                }));

                loader.classList.add("none");
                document.body.style.overflow = "scroll";

                let counter = 0;
                data.forEach((object) => {
                    counter += object.length;
                })
                if (counter >= 10) {
                    lessonDash.insertAdjacentElement("beforeend", loadMoreBtn);
                }

                resolve();
            } catch (error) {
                console.error(error);
                resolve();
            }
        });
    };

    lessonsLoadingAndSearch();

    // Returns teachers html
    const getTeacherHTML = async (teacher) => {
        const lessonTeacher = await getTeacherData(teacher[0].teacherId);
        const lessonTeacherUser = await getProfileData(teacher[0].teacherId);
        const fullName = lessonTeacherUser.full_name.trim();
        const teacherRowHTML = `
        <div class="lessons-row" id="teacher-${teacher[0].teacherId}">
          <div class="row">
            <div class="col-sm-12 col-md-6 col-lg-3">
              <div class="lessons-row__profile-info" data-profile-id="${teacher[0].teacherId}">
                <div class="lessons-row__credentials">
                    <span class="lessons-row__name">${fullName.split(' ')[0]} </span>
                    <span class="lessons-row__name highlighted">${fullName.split(' ').slice(1).join(' ')}</span>
                    <span class="lessons-row__stars">${displayStars(lessonTeacher.avgRating)}</span>
                    <span class="lessons-row__location"><img src="../static/images/icons/location.svg" alt="Location"/><span>${lessonTeacherUser.city}</span></span></div>
                <div class="lessons-row__profile-image profile-image"><img src="${lessonTeacherUser.pfp_path}" alt="Profile"/></div>
              </div>
            </div>            
          </div>
        </div>`
        return teacherRowHTML;
    }

    // Returns lessons html
    const getLessonHTML = async (lesson) => {

        const lessonTitle = lesson.title.trim();
        const lessonSkill = lesson.skillId;
        const lessonHTML = `
            
                <div class="col-sm-12 col-md-6 col-lg-3 d-flex justify-content-center align-items-center">
                  <div class="lesson-card" data-lesson-id="${lesson.id}" data-teacher-id="${lesson.teacherId}">
                    <div class="lesson-card__title">${lessonTitle.split(' ')[0]} <span class="highlighted">${lessonTitle.split(' ').slice(1).join(' ')}</span></div>
                    <div class="lesson-card__description">${lesson.description}</div>
                    <div class="lesson-card__level">${SKILL_LEVELS[lessonSkill]}</div>
                    <div class="lesson-card__price">${lesson.price} &euro;</div>
                  </div>
                </div>

        `;
        return lessonHTML;
    };

    //show profile modal of the teacher on click
    document.querySelector(".lessons").firstElementChild.addEventListener("click", async (e) => {
        const clickedProfile = e.target.closest(".lessons-row__profile-info");
        if (clickedProfile) {
            const profileModal = await createTeacherModal(clickedProfile.getAttribute('data-profile-id'));
            document.querySelector("body").insertAdjacentHTML("afterbegin", profileModal);
            document.querySelector(".modal__close").addEventListener("click", (e) => {
                document.querySelector(".modal__close").parentElement.parentElement.classList.add("none");
            })
        }
    })


    //searching teacher with filters
    function lessonsLoadingAndSearch() {
        const lessonRequest = {
            lessonOffset: 0,
            lessonInstrumentId: "%%",
            lessonSkillId: "%%",
            lessonRating: "-1",
            lessonLocation: "%%",
            lessonAvailability: "%%",
            lessonType: "%%",
        };

        const lessonDash = document.querySelector(".lessons").firstElementChild;
        const searchInput = document.getElementById("search-location");
        const loadMoreBtn = document.querySelector("#load-more");

        //Initial load
        loadLessons(lessonRequest, lessonDash, loadMoreBtn);
        loadMoreBtn.remove();


        document.querySelector(".filters__reset-filters").addEventListener("click", () => {
            lessonDash.replaceChildren(loadMoreBtn);
            lessonRequest.lessonOffset = 0;
            lessonRequest.lessonInstrumentId = "%%";
            lessonRequest.lessonSkillId = "%%";
            lessonRequest.lessonRating = "-1";
            lessonRequest.lessonLocation = "%%";
            lessonRequest.lessonAvailability = "%%";
            lessonRequest.lessonType = "%%";
            loadLessons(lessonRequest, lessonDash, loadMoreBtn);
            loader.classList.remove("none");
            document.body.style.overflow = "hidden";
            loadMoreBtn.remove();
        })

        let timeoutId; // Variable to store the timeout ID
        // Adjusting lessonsLocation when in search bar
        searchInput.addEventListener("input", (event) => {
            event.preventDefault();
            // Clear the previous timeout if exists
            clearTimeout(timeoutId);
            // Set a new timeout of 2 seconds
            timeoutId = setTimeout(() => {
                if (searchInput.value) {
                    lessonDash.replaceChildren(loadMoreBtn);
                    lessonRequest.lessonOffset = 0;
                    lessonRequest.lessonLocation = searchInput.value;
                    loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                    loader.classList.remove("none");
                    document.body.style.overflow = "hidden";
                    loadMoreBtn.remove();
                }
            }, 2000); // 2000 milliseconds = 2 seconds
        });


        // Adjusting lessonsRating when selected in filters
        document.querySelector("#instruments-list").addEventListener("click", (event) => {
            const selectedInstrument = event.target.closest(".filters__option")
            if (selectedInstrument) {
                lessonDash.replaceChildren(loadMoreBtn);
                lessonRequest.lessonOffset = 0;
                lessonRequest.lessonInstrumentId = selectedInstrument.getAttribute("for");
                loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                loader.classList.remove("none");
                document.body.style.overflow = "hidden";
                loadMoreBtn.remove();
            }
        });

        // Adjusting skill level when selected in filters
        document.querySelector("#level-list").addEventListener("click", (event) => {
            const selectedSkill = event.target.closest(".filters__option")
            if (selectedSkill) {
                lessonDash.replaceChildren(loadMoreBtn);
                lessonRequest.lessonOffset = 0;
                lessonRequest.lessonSkillId = parseInt(selectedSkill.getAttribute("for").charAt(0));
                loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                loader.classList.remove("none");
                document.body.style.overflow = "hidden";
                loadMoreBtn.remove();
            }
        });

        // Adjusting lessons type when selected in filters
        document.querySelector("#type-list").addEventListener("click", (event) => {
            const selectedType = event.target.closest(".filters__option")
            if (selectedType) {
                lessonDash.replaceChildren(loadMoreBtn);
                lessonRequest.lessonOffset = 0;
                lessonRequest.lessonType = selectedType.getAttribute("for");
                loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                loader.classList.remove("none");
                document.body.style.overflow = "hidden";
                loadMoreBtn.remove();
            }
        });

        // Adjusting teachers rating when selected in filters
        document.querySelector("#rating-list").addEventListener("click", (event) => {
            const selectedRating = event.target.closest(".filters__option")
            if (selectedRating) {
                lessonDash.replaceChildren(loadMoreBtn);
                lessonRequest.lessonOffset = 0;
                lessonRequest.lessonRating = parseInt(selectedRating.getAttribute("for").charAt(0));
                loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                loader.classList.remove("none");
                document.body.style.overflow = "hidden";
                loadMoreBtn.remove();
            }
        });

        // Adjusting teachers availability when selected in filters
        document.querySelector("#calendar-schedule").addEventListener("click", (event) => {
            const selectedDay = event.target.closest(".selected-day")
            if (selectedDay) {
                lessonDash.replaceChildren(loadMoreBtn);
                lessonRequest.lessonOffset = 0;
                const year = document.querySelector("#month-year").innerHTML.split(" ")[1];
                const month = getMonthNumber(document.querySelector("#month-year").innerHTML.split(" ")[0]);
                const day = selectedDay.innerHTML.padStart(2, '0');
                lessonRequest.lessonAvailability = `${year}-${month}-${day}`;
                loadLessons(lessonRequest, lessonDash, loadMoreBtn);
                loader.classList.remove("none");
                document.body.style.overflow = "hidden";
                loadMoreBtn.remove();
            }
        });

        // Load more button
        loadMoreBtn.addEventListener("click", () => {
            lessonRequest.lessonOffset += 10;
            loadLessons(lessonRequest, lessonDash, loadMoreBtn);
            loader.classList.remove("none");
            document.body.style.overflow = "hidden";
            loadMoreBtn.remove();
        })

    }

});