import {createSelectAvailabilityModal, useConfirmModal, useMessageModal} from "../modules/modal-generator.js";

export const BASE_API_URL = window.location.protocol + "//" + location.host + "/api/";

export const PROFILE_URI = BASE_API_URL + "users/profile/";
export const CURRENT_PROFILE_URI = BASE_API_URL + "users/current/";


export const TEACHER_GENERAL_URI = BASE_API_URL + "teachers/"
export const TEACHER_AVAILABILITY_URI = BASE_API_URL + "schedule/teacher/free/"
export const TEACHER_ALL_AVAILABILITY_URI = BASE_API_URL + "schedule/teacher/"
export const TEACHER_INSTRUMENTS_URI = BASE_API_URL + "teacher-instruments/"
export const TEACHER_REVIEWS_URI = BASE_API_URL + "reviews/teacher/"
export const TEACHER_LESSONS_URI = BASE_API_URL + "lessons/teacher/"
export const COUNT_TEACHER_REVIEWS_URI = BASE_API_URL + "reviews/count/"
export const IS_LESSON_REVIEW_STUDENT_LESSON_URI = BASE_API_URL + "reviews/has-review-from/"

export const STUDENT_LESSONS_URI = BASE_API_URL + "lessons/student/"
export const STUDENT_INSTRUMENTS_URI = BASE_API_URL + "instruments/student/"
export const COUNT_STUDENT_LESSONS_URI = BASE_API_URL + "lessons/student/count/"
export const LESSON_URI = BASE_API_URL + "lessons/"
export const REVIEW_URI = BASE_API_URL + "reviews/"
export const SCHEDULE_URI = BASE_API_URL + "schedule/"

export const INSTRUMENTS_COUNT_URI = BASE_API_URL + "instruments/count/"
export const CITIES_COUNT_URI = BASE_API_URL + "users/count/city/"
export const TEACHERS_COUNT_URI = BASE_API_URL + "teachers/count/"
export const STUDENTS_COUNT_URI = BASE_API_URL + "users/count/"

export const INSTRUMENTS_URI = BASE_API_URL + "instruments/"
export const LESSONS_URI = BASE_API_URL + "lessons/search"
export const TEACHERS_URI = BASE_API_URL + "teachers/search"
export const USER_NOTIFICATIONS_URI = BASE_API_URL + "notification/user/"
export const USER_MESSAGES_URI = BASE_API_URL + "message/participant/"

function getCsrfToken() {
    let cookies = document.cookie.split(';');
    for (const element of cookies) {
        let cookie = element.trim();
        if (cookie.startsWith('X-CSRF-TOKEN=')) {
            let csrfToken = cookie.substring('X-CSRF-TOKEN='.length);
            return csrfToken;
        }
    }
    return null;
}

export const CSRF_TOKEN = getCsrfToken();


export const SKILL_LEVELS = {
    '1' : 'Beginner',
    '2' : 'Intermediate',
    '3' : 'Advanced'
}

// Returns booking parameters object
export const getBookingParams = (senderUser, receiverUser, lessonId, scheduleId) => {
    return {
        "senderId" : senderUser,
        "receiverId" : receiverUser,
        "lessonId" : lessonId,
        "scheduleId" : scheduleId
    }
}

// Returns schedule parameters object
export const getScheduleParams = (teacherId, date, startTime, endTime) => {
    return {
        "teacherId" : teacherId,
        "date" : date,
        "startTime" : startTime,
        "endTime" : endTime
    }
}

// Returns message parameters object
export const getMessageParams = (senderId, receiverId, messageText) => {
    return {
        "senderId" : senderId,
        "receiverId" : receiverId,
        "messageText" : messageText,
    }
}

// Returns notification parameters object
export const getNotificationParams = (senderUser, receiverUser, message, isConfirmed, bookingId) => {
    return {
        "senderId" : senderUser,
        "receiverId" : receiverUser,
        "text" : message,
        "isConfirmed" : isConfirmed,
        "bookingId" : bookingId
    }
}

// Displays star ratings based on average rating
export function displayStars(avgRating) {
    return avgRating <= 1 ?
        `<img src='../static/images/icons/star-empty.svg' alt='Star' />`.repeat(5) :
        avgRating <= 2 ?
            `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(1) + `<img src='../static/images/icons/star-empty.svg' alt='Star' />`.repeat(4) :
            avgRating <= 4 ?
                `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(2) + `<img src='../static/images/icons/star-empty.svg' alt='Star' />`.repeat(3) :
                avgRating <= 6 ?
                    `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(3) + `<img src='../static/images/icons/star-empty.svg' alt='Star' />`.repeat(2) :
                    avgRating <= 8 ?
                        `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(4) + `<img src='../static/images/icons/star-empty.svg' alt='Star' />` :
                        avgRating <= 10 ?
                            `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(5) :
                            `<img src='../static/images/icons/star-fill.svg' alt='Star' />`.repeat(5)
}

// Adds event listener for booking lessons
export function addEventToBookLessons(currentId) {
    document.addEventListener("click", (e) => {
        const clickedLesson = e.target.closest(".lesson-card");
        if (clickedLesson) {
            if (currentId) {
                const clickedLessonId = parseInt(clickedLesson.getAttribute("data-lesson-id"));
                const clickedLessonTeacherId = parseInt(clickedLesson.getAttribute("data-teacher-id"));
                useConfirmModal("Do you want to book this lesson?", async () => {
                    // Display availability of the teacher
                    // Add check if profile modal opened
                    document.querySelector(`.modal-${clickedLessonTeacherId}`) && document.querySelector(`.modal-${clickedLessonTeacherId}`).remove();
                    await createSelectAvailabilityModal(clickedLessonTeacherId);
                    const clickedTeacherAvailability = document.querySelector(`.view-availability-${clickedLessonTeacherId}`);
                    clickedTeacherAvailability.querySelector(".book-lesson").addEventListener("click", async () => {
                        const timeslotsDiv = clickedTeacherAvailability.querySelector(".view-availability__timeslot");
                        const selectedTimeslot = timeslotsDiv.querySelector("input[type='radio']:checked");
                        // scheduleId, senderId, receiverId, lessonId
                        if (selectedTimeslot && selectedTimeslot.parentElement.getAttribute("data-timeslot-id")) {
                            if (document.querySelector(".calendar .selected-day").classList.contains("available-day")) {
                                const scheduleId = parseInt(selectedTimeslot.parentElement.getAttribute("data-timeslot-id"));
                                // Redirect to payment with the following parameters: studentId, teacherId, lessonId, scheduleId
                                window.location.assign(`/payment?studentId=${currentId}&teacherId=${clickedLessonTeacherId}&lessonId=${clickedLessonId}&scheduleId=${scheduleId}`)
                            } else {
                                useMessageModal("This day is already booked by someone else.")
                            }
                        } else {
                            useMessageModal("This teacher does not have any available timeslots. Contact them via chat.")
                        }
                    })
                })
            } else if (!currentId) {
                useMessageModal("You cannot book a lesson without authorization. Join our community!")
            }
        }
    })
}