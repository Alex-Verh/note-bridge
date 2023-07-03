import { getMessages, getProfileData } from "../api/queries.js";
import { CSRF_TOKEN, getMessageParams } from "../api/constants.js";
import { validateMessage } from "./validation.js";

// Updates the chat interface with messages and contacts
export const updateChat = async (currentId) => {
    const fetchMessages = await getMessages(currentId);
    const chatPartnersAndTheirChats = getChatPartners(currentId, fetchMessages);
    const chatContactsContainer = document.querySelector(".chat__contacts");
    const chatMessagesContainer = document.querySelector(".chat__messages");

    // Add chat partners to the contacts list
    for (const chatPartner of chatPartnersAndTheirChats[0]) {
        const chatPartnerData = await getProfileData(chatPartner);
        chatContactsContainer.insertAdjacentHTML("beforeend", `
        <input class="chat__contacts-radio" id="contact-${chatPartner}" type="radio" name="chatContact"/>
        <label class="chat__contacts-preview" data-contact-id="${chatPartner}" for="contact-${chatPartner}">
          <div class="profile-image chat__contacts-icon"><img src="${chatPartnerData.pfp_path}" alt="Profile"/></div>
          <div class="chat__contacts-text">
            <div class="chat__contacts-text-name">${chatPartnerData.full_name}</div>
            <div class="chat__contacts-text-message">${chatPartnerData.is_teacher ? "Teacher" : "Student"}</div>
          </div>
        </label>
    `);}

    if (chatContactsContainer.childElementCount > 2) {
        // Add event listener for selecting a chat contact
        chatContactsContainer.addEventListener("click", async (e) => {
            const clickedContact = e.target.closest(".chat__contacts-preview");
            if (clickedContact) {
                const clickedContactId = clickedContact.getAttribute("data-contact-id");
                const contactData = await getProfileData(clickedContactId);

                // Retrieve the messages for the selected chat partner
                const messages = chatPartnersAndTheirChats[1][clickedContactId];

                // Generate the HTML for the chat messages
                const chatMessagesHTML = messages.map((message) => {
                    const messageClass = message.sender === currentId ? "chat__messages-my" : "chat__messages-their";
                    const timestampClass = message.sender === currentId ? "chat__messages-my-time" : "chat__messages-their-time";
                    return `<div class="${messageClass}">
                  <span class="chat__messages-text">${message.text}</span>
                  <span class="${timestampClass}">${formatTimestamp(message.timestamp)}</span>
                </div>`;
                }).join("");

                chatMessagesContainer.innerHTML = `
                <div class="chat__messages-header" data-current-chat-id="${contactData.id}">
                    <div class="profile-image chat__messages-icon"><img src="${contactData.pfp_path}" alt="Profile"/></div>
                    <div class="chat__messages-name"><span>${contactData.full_name}</span></div>
                    <div class="chat__line"></div>
                </div>
                <div class="chat__messages-block">
                  ${chatMessagesHTML}
                </div>
                <input class="chat__messages-input" type="text"/>
                <img src="../static/images/icons/send.svg" class="send-message" alt="Send"/>`;
            }
        });

        // Add event listener for sending a message
        document.querySelector('.chat__messages').addEventListener('click', function (e) {
            const sendBtn = e.target.closest('.chat__messages .send-message');
            if (sendBtn) {
                const openedChatId = document.querySelector('.chat__messages .chat__messages-header').getAttribute("data-current-chat-id");
                const messageInput = document.querySelector('.chat__messages-input');
                if (validateMessage(messageInput.value).isValid) {
                    sendMessage(currentId, openedChatId, messageInput.value)
                }
            }
        });

    } else {
        // Show message when there are no contacts
        chatContactsContainer.insertAdjacentHTML("beforeend", `
            <label class="chat__contacts-preview"
              <div class="chat__contacts-text">
                <div class="chat__contacts-text-name">You have no contacts :(</div>
                <div class="chat__contacts-text-message">Find some friend on our map!</div>
              </div>
            </label>
        `)
    }
}

// Retrieves chat partners and their corresponding messages
function getChatPartners(currentId, allMessages) {
    let chatPartners = new Set();
    let messages = {};

    for (const message of allMessages) {
        let sender = message.senderId;
        let receiver = message.receiverId;

        if (sender === currentId || receiver === currentId) {
            if (sender !== currentId) {
                chatPartners.add(sender);
            }
            if (receiver !== currentId) {
                chatPartners.add(receiver);
            }

            let partnerId = sender === currentId ? receiver : sender;
            if (!messages[partnerId]) {
                messages[partnerId] = [];
            }
            messages[partnerId].push({
                text: message.messageText,
                sender: sender,
                timestamp: message.timestamp
            });
        }
    }

    for (let partnerId in messages) {
        messages[partnerId].sort((a, b) => a.timestamp - b.timestamp);
    }

    return [Array.from(chatPartners), messages];
}

// Formats the timestamp to HH:mm format
function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    const hours = date.getHours();
    const minutes = date.getMinutes();
    const formattedTime = `${padZero(hours)}:${padZero(minutes)}`;
    return formattedTime;
}

// Pads a number with leading zeros
function padZero(number) {
    return number.toString().padStart(2, '0');
}

// Sends a message
export function sendMessage(senderId, receiverId, messageText) {
    const messageParams = getMessageParams(senderId, receiverId, messageText)
    fetch('/api/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': CSRF_TOKEN
        },
        body: JSON.stringify(messageParams)
    }).then(() => {
        setTimeout(function() {
            window.location.assign(window.location.href);
        }, 1000);
    })
}
