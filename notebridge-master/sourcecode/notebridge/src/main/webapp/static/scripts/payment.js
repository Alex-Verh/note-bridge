import {updateNav} from "./modules/nav.js";
import {getCurrentId, getLesson, getProfileData, getSchedule} from "./api/queries.js";
import {CSRF_TOKEN, getBookingParams, SKILL_LEVELS} from "./api/constants.js";
import {useMessageModal} from "./modules/modal-generator.js";

document.addEventListener("DOMContentLoaded",  async () => {
    import("./modules/modal.js");

    updateNav();

    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);

    const studentId = params.get('studentId');
    const teacherId = params.get('teacherId');
    const lessonId = params.get('lessonId');
    const scheduleId = params.get('scheduleId');
    const currentId = await getCurrentId();
    //Performing the booking while simulating a payment process
    if (studentId && teacherId && lessonId && scheduleId && currentId === parseInt(studentId)) {
        const lessonDetails = await getLesson(lessonId);
        const scheduleDetails = await getSchedule(scheduleId);
        const teacherDetails = await getProfileData(teacherId);
        const lessonSkillId = lessonDetails.skillId;

        document.querySelector(".teacher-name").innerHTML = `${teacherDetails.full_name}`;
        document.querySelector(".name-level").innerHTML = `${lessonDetails.title} - ${SKILL_LEVELS[lessonSkillId]}`
        document.querySelector(".date").innerHTML = `${scheduleDetails.date}`
        document.querySelector(".timeslot").innerHTML = `${scheduleDetails.startTime} - ${scheduleDetails.endTime}`
        document.querySelector(".amount").innerHTML = `${lessonDetails.price} €`


        document.querySelector(".pay-now").addEventListener("click", () => {
            // Do booking
            const bookingParams = getBookingParams(studentId, teacherId, lessonId, scheduleId);
            fetch('/api/booking', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': CSRF_TOKEN
                },
                body: JSON.stringify(bookingParams)
            }).then(() => {
                useMessageModal("The booking has been processed successfully!")
                setTimeout(function () {
                    window.location.assign('/profile');
                }, 3000);
            })
        })
    } else {
        window.location.assign("/main");
    }
});