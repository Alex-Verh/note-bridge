import {getNotifications, getProfileData} from "../api/queries.js";
import {useMessageModal} from "./modal-generator.js";
import {CSRF_TOKEN, getNotificationParams} from "../api/constants.js";

// Function to update notifications
export const updateNotifications = async (currentId) => {
    const notifications = await getNotifications(currentId);
    await notifications.forEach(async (notification) => {
        const sender = await getProfileData(notification.senderId);
        document.querySelector(".notifications__container").insertAdjacentHTML("beforeend", `
        <div class="notification">
          <div class="profile-image notification__profile"><img src="${sender.pfp_path}" alt="${sender.full_name}"/></div>
          <div class="notification__text">${notification.text}</div>
          <div class="notification__commands" data-booking-id="${notification.bookingId}" data-notification-id="${notification.id}" data-sender-id="${notification.senderId}">
            ${(notification.confirmed ? `` : 
                '<div class="notification__command notification__command_confirm">Confirm</div>' +
                '<div class="notification__command notification__command_reject">Deny</div>')}
            </div>
          <div class="notification__time">${displayTimePeriod(notification.date)}</div>
        </div>
        `)
    })

    await document.querySelector(".notifications__container").addEventListener("click", async (e) => {
        const clickedCommand = e.target.closest(".notification__command");
        if (clickedCommand) {
            const notificationId = parseInt(clickedCommand.parentElement.getAttribute("data-notification-id"));
            const bookingId = parseInt(clickedCommand.parentElement.getAttribute("data-booking-id"));
            const studentId = parseInt(clickedCommand.parentElement.getAttribute("data-sender-id"));
            const teacher = await getProfileData(currentId);
            const teacherName = teacher.full_name.trim();
            if (clickedCommand.classList.contains("notification__command_confirm")) {
                //Send to student that lesson has taken place
                const notificationParams = getNotificationParams(currentId, studentId, `Your teacher, ${teacherName}, has confirmed that lesson has taken place. 
                Report on Contact page if fraud was committed in your opinion.`, true, bookingId);


                fetch('/api/booking/finished/'+bookingId, {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    useMessageModal("Lesson marked as finished!");
                })

                fetch('/api/notification', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    },
                    body: JSON.stringify(notificationParams)
                }).then(() => {
                    console.log("Student notified!")
                })

                fetch('/api/notification/confirm/'+notificationId, {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    console.log("Notification converted in confirmed!")
                    setTimeout(function() {
                        window.location.assign('/profile');
                    }, 3000);
                })

            }
            if (clickedCommand.classList.contains("notification__command_reject")) {
                const notificationParams1 = getNotificationParams(currentId, studentId, `Your teacher, ${teacherName}, has rejected that lesson has taken place. 
                Report on Contact page if fraud was committed in your opinion.`, true, bookingId);

                fetch('/api/booking/canceled/'+bookingId, {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    useMessageModal("Lesson marked as canceled!")
                })

                fetch('/api/notification', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    },
                    body: JSON.stringify(notificationParams1)
                }).then(() => {
                    console.log("Student notified!")
                })

                const notificationParams2 = getNotificationParams(currentId, studentId, `Your money will be returner soon!`, true, bookingId);
                fetch('/api/notification', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    },
                    body: JSON.stringify(notificationParams2)
                }).then(() => {
                    console.log("Student will get money back!")
                })

                fetch('/api/notification/confirm/'+notificationId, {
                    method: 'GET',
                    headers: {
                        'X-CSRF-TOKEN': CSRF_TOKEN
                    }
                }).then(() => {
                    console.log("Notification converted in confirmed!")
                    setTimeout(function() {
                        window.location.assign('/profile');
                    }, 3000);
                })
            }
        }
    })
}
// Function to display the time period of a notification
function displayTimePeriod(otherDate) {
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    const currentDateObj = new Date(today);
    const otherDateObj = new Date(otherDate);

    if (currentDateObj.toDateString() === otherDateObj.toDateString()) {
        return "Today";
    } else if (currentDateObj.toDateString() === yesterday.toDateString()) {
        return "Yesterday";
    } else if (
        currentDateObj.getFullYear() === otherDateObj.getFullYear() &&
        currentDateObj.getMonth() === otherDateObj.getMonth() &&
        currentDateObj.getDate() - otherDateObj.getDate() <= 7
    ) {
        return "This week";
    } else if (
        currentDateObj.getFullYear() === otherDateObj.getFullYear() &&
        currentDateObj.getMonth() === otherDateObj.getMonth()
    ) {
        return "This month";
    } else {
        return "Long ago";
    }
}
