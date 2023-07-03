import {updateNav} from "./modules/nav.js";
import {inputValidation, validateEmail, validateMessage, validateName, validateTopic} from "./modules/validation.js";
document.addEventListener("DOMContentLoaded",() => {
    import("./modules/modal.js");

    updateNav();
    try {
        const contactForm = document.querySelector('#contact-form');

        inputValidation(contactForm.contactFullName, validateName)
        inputValidation(contactForm.contactEmail, validateEmail)
        inputValidation(contactForm.contactTopic, validateTopic)
        inputValidation(contactForm.contactMessage, validateMessage)
        contactForm.addEventListener("submit", (e) => {
            e.preventDefault();
            if (
                validateName(contactForm.contactFullName.value).isValid &&
                validateEmail(contactForm.contactEmail.value).isValid &&
                validateTopic(contactForm.contactTopic.value).isValid &&
                validateMessage(contactForm.contactMessage.value).isValid
            ) {
                contactForm.submit();
                contactForm.reset();
            }
        });
    } catch (e) {
        console.log(e)
    }
});
