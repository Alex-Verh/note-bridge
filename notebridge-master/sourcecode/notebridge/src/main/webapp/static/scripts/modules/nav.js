import {getCurrentId, getProfileData} from "../api/queries.js";
import {updateNotifications} from "./notifications.js";
import {updateChat} from "./chat.js";

export async function updateNav () {
    //user authenticated
    const currentId = await getCurrentId();
    if (currentId === 0) {
        document.querySelectorAll(".non-authenticated").forEach((elem) => { elem.classList.remove("none"); });
    //user unauthenticated
    } else {
        const profileData = await getProfileData(currentId);
        document.querySelectorAll(".authenticated").forEach((elem) => { elem.classList.remove("none"); });
        document.querySelector("#profileLink").setAttribute("href", "/profile/" + currentId)
        document.querySelector("#profileLink").firstElementChild.setAttribute("src", profileData.pfp_path)
        await updateNotifications(currentId);
        await updateChat(currentId);
    }
}

