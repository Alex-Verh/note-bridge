import {
    PROFILE_URI,
    TEACHER_GENERAL_URI,
    TEACHER_AVAILABILITY_URI,
    TEACHER_INSTRUMENTS_URI,
    TEACHER_REVIEWS_URI,
    INSTRUMENTS_COUNT_URI,
    TEACHERS_COUNT_URI,
    STUDENTS_COUNT_URI,
    CURRENT_PROFILE_URI,
    STUDENT_LESSONS_URI,
    TEACHER_LESSONS_URI,
    STUDENT_INSTRUMENTS_URI,
    COUNT_TEACHER_REVIEWS_URI,
    INSTRUMENTS_URI,
    LESSONS_URI,
    USER_NOTIFICATIONS_URI,
    COUNT_STUDENT_LESSONS_URI,
    TEACHERS_URI,
    CITIES_COUNT_URI,
    LESSON_URI,
    IS_LESSON_REVIEW_STUDENT_LESSON_URI,
    REVIEW_URI,
    TEACHER_ALL_AVAILABILITY_URI, SCHEDULE_URI, CSRF_TOKEN, USER_MESSAGES_URI
} from "./constants.js";

const stringifyQueryParams = (paramsObj) =>
    Object.entries(paramsObj)
        .reduce(
            (prev, param) => (param[1] ? [...prev, `${param[0]}=${param[1]}`] : prev),
            []
        )
        .join("&");

const customFetch = async (url, options = { method: "GET" }) => {
    const res = await fetch(url, options);
    if (!res.ok) {
        throw new Error(res.status);
    }
    return res;
};

export const getCurrentId = async () => {
    const url = CURRENT_PROFILE_URI;
    try {
        const res = await customFetch(url);
        if (res.status === 404) {
            return 0;
        }
        const currentId = await res.json();
        return currentId;
    } catch (error) {
        return 0;
    }
};

export const countInstruments = async () => {
    const url = INSTRUMENTS_COUNT_URI;
    const res = await customFetch(url);
    const nrOfInstruments = await res.json();
    return nrOfInstruments;
};

export const countTeachers = async () => {
    const url = TEACHERS_COUNT_URI;
    const res = await customFetch(url);
    const nrOfTeachers = await res.json();
    return nrOfTeachers;
};

export const countCities = async () => {
    const url = CITIES_COUNT_URI;
    const res = await customFetch(url);
    const nrOfCountries = await res.json();
    return nrOfCountries;
};

export const countStudents = async () => {
    const url = STUDENTS_COUNT_URI;
    const res = await customFetch(url);
    const nrOfStudents = await res.json();
    return nrOfStudents;
};

export const getProfileData = async (id) => {
    const url = PROFILE_URI + id;
    const res = await customFetch(url);
    const profileData = await res.json();
    return profileData;
};

export const getTeacherData = async (id) => {
    const url = TEACHER_GENERAL_URI + id;
    const res = await customFetch(url);
    const teacherData = await res.json();
    return teacherData;
};

export const getTeacherAvailability = async (id) => {
    const url = TEACHER_AVAILABILITY_URI + id;
    const res = await customFetch(url);
    const teacherAvailability = await res.json();
    return teacherAvailability;
};

export const getTeacherAllAvailability = async (id) => {
    const url = TEACHER_ALL_AVAILABILITY_URI + id;
    const res = await customFetch(url);
    const teacherAvailability = await res.json();
    return teacherAvailability;
};

export const getSchedule = async (id) => {
    const url = SCHEDULE_URI + id;
    const res = await customFetch(url);
    const schedule = await res.json();
    return schedule;
};

export const getMessages = async (id) => {
    const url = USER_MESSAGES_URI + id;
    const res = await customFetch(url);
    const chat = await res.json();
    return chat;
};

export const getTeacherLessons = async (id) => {
    const url = TEACHER_LESSONS_URI + id;
    const res = await customFetch(url);
    const teacherLessons = await res.json();
    return teacherLessons;
};
export const getTeacherInstruments = async (id) => {
    const url = TEACHER_INSTRUMENTS_URI + id;
    const res = await customFetch(url);
    const teacherInstruments = await res.json();
    return teacherInstruments;
};

export const getTeacherReviews = async (id) => {
    const url = TEACHER_REVIEWS_URI + id;
    const res = await customFetch(url);
    const teacherReviews = await res.json();
    return teacherReviews;
};

export const getStudentLessons = async (id) => {
    const url = STUDENT_LESSONS_URI + id;
    const res = await customFetch(url);
    const studentLessons = await res.json();
    return studentLessons;
};

export const getStudentInstruments = async (id) => {
    const url = STUDENT_INSTRUMENTS_URI + id;
    const res = await customFetch(url);
    const studentInstruments = await res.json();
    return studentInstruments;
};

export const countTeacherReviews = async (id) => {
    const url = COUNT_TEACHER_REVIEWS_URI + id;
    const res = await customFetch(url);
    const teacherReviewsNr = await res.json();
    return teacherReviewsNr;
};

export const getAllInstruments = async () => {
    const url = INSTRUMENTS_URI;
    const res = await customFetch(url);
    const instruments = await res.json();
    return instruments;
};

export const getLessons = async (params) => {
    const url = `${LESSONS_URI}?${stringifyQueryParams(params)}`;
    console.log("Fetch request on " + url)
    const res = await customFetch(url);
    const lessons = await res.json();
    return lessons;
};

export const getTeachers = async (params) => {
    const url = `${TEACHERS_URI}?${stringifyQueryParams(params)}`;
    console.log("Fetch request on " + url)
    const res = await customFetch(url);
    const teachers = await res.json();
    return teachers;
};

export const getNotifications = async (id) => {
    const url = USER_NOTIFICATIONS_URI + id;
    const res = await customFetch(url);
    const notifications = await res.json();
    return notifications;
};

export const getLesson = async (id) => {
    const url = LESSON_URI + id;
    const res = await customFetch(url);
    const lesson = await res.json();
    return lesson;
};

export const countStudentLessons = async (id) => {
    const url = COUNT_STUDENT_LESSONS_URI + id;
    const res = await customFetch(url);
    const nrOfLessonsLearned = await res.json();
    return nrOfLessonsLearned;
};

export const getIfLessonHasReview = async (lessonId, studentId) => {
    const url = IS_LESSON_REVIEW_STUDENT_LESSON_URI + lessonId + "/"+ studentId;
    const res = await customFetch(url);
    const ifLessonHasReview = await res.json();
    return ifLessonHasReview;
};

export const deleteLesson = async (id) => {
    try {
        const res = await customFetch(`${LESSON_URI}${id}`, {
            method: "DELETE",
            headers: {
                'X-CSRF-TOKEN': CSRF_TOKEN
            }
        });
        return res.json();
    } catch (e) {
        throw e;
    }
};

export const deleteReview = async (lessonId, studentId) => {
    try {
        const res = await customFetch(`${REVIEW_URI}${lessonId}/${studentId}`, {
            method: "DELETE",
            headers: {
                'X-CSRF-TOKEN': CSRF_TOKEN
            }
        });
        return res.json();
    } catch (e) {
        throw e;
    }
};

export const deleteTimeslot = async (id) => {
    try {
        const res = await customFetch(`${SCHEDULE_URI}${id}`, {
            method: "DELETE",
            headers: {
                'X-CSRF-TOKEN': CSRF_TOKEN
            }
        });
        return res.json();
    } catch (e) {
        throw e;
    }
};
