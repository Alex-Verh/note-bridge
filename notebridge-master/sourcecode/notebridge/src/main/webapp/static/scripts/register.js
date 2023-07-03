import {
    hideInputError,
    inputValidation, showInputError,
    validateEmail,
    validateLoginPassword,
    validateName,
    validatePassword
} from "./modules/validation.js";
import { useMessageModal} from "./modules/modal-generator.js";
import {updateNav} from "./modules/nav.js";
import {BASE_API_URL} from "./api/constants.js";

document.addEventListener("DOMContentLoaded",  () => {
    import("./modules/modal.js");

    updateNav();

    document.querySelector(".non-authenticated").classList.remove("none");

    // Set validators on actual forms
    try {
        const registerForm = document.querySelector("#registerForm");
        const loginForm = document.querySelector("#loginForm");
        const resetForm = document.querySelector("#resetForm");
        const forgotForm = document.querySelector("#forgotForm");
        // Applying validation on register
        if (registerForm) {
            inputValidation(registerForm.registerFullName, validateName);
            inputValidation(registerForm.registerEmail, validateEmail);
            inputValidation(registerForm.registerPassword, validatePassword);

            registerForm.registerRepeatPassword.addEventListener("change", (e) => {
                const target = e.target;
                const isValid = target.value === registerForm.registerPassword.value;
                if (!isValid) {
                    showInputError(target, "Passwords must match");
                } else {
                    hideInputError(target);
                }
            });
            registerForm.addEventListener("submit", (e) => {
                e.preventDefault();
                if (
                    validateName(registerForm.registerFullName.value).isValid &&
                    validateEmail(registerForm.registerEmail.value).isValid &&
                    validatePassword(registerForm.registerPassword.value, true).isValid &&
                    registerForm.registerRepeatPassword.value === registerForm.registerPassword.value
                ) {
                    fetch(`${BASE_API_URL}users/exists/${registerForm.registerEmail.value}`)
                        .then(response => response.text())
                        .then(result => JSON.parse(result))
                        .then(obj => {if(obj.exists) {
                            useMessageModal("There is already an account with this email.");
                        } else {
                            useMessageModal("A verification email has been sent to your email address.");
                            registerForm.submit();
                        }})
                }
            });
            // Applying validation on login
        } else if (loginForm) {
            const currentURL = window.location.href;
            if (currentURL.includes("token")) {
                useMessageModal("Email successfully verified. Your account is now active.")
            } else if (currentURL.includes("error")) {
                useMessageModal("Incorrect email or password.")
            }
            inputValidation(loginForm.loginEmail, validateEmail);
            inputValidation(loginForm.loginPassword, validateLoginPassword);

            loginForm.addEventListener("submit", (e) => {
                e.preventDefault();
                if (
                    validateEmail(loginForm.loginEmail.value).isValid &&
                    validatePassword(loginForm.loginPassword.value, false).isValid
                ) {
                    loginForm.submit();
                }
            });
            // Applying validation on reset password
        } else if (resetForm){
            inputValidation(resetForm.resetPassword, validatePassword);

            resetForm.resetRepeatPassword.addEventListener("change", (e) => {
                const target = e.target;
                const isValid = target.value === resetForm.resetPassword.value;
                if (!isValid) {
                    showInputError(target, "Passwords must match");
                } else {
                    hideInputError(target);
                }
            });
            resetForm.addEventListener("submit", (e) => {
                e.preventDefault();
                if (
                    validatePassword(resetForm.resetPassword.value, true).isValid &&
                    resetForm.resetRepeatPassword.value === resetForm.resetPassword.value
                ) {
                    useMessageModal("Password successfully reset.")
                    setTimeout(() => resetForm.submit(), 1500);
                }
            });
            // Applying validation on forgot password
        } else if(forgotForm) {
            inputValidation(forgotForm.forgotEmail, validateEmail);

            forgotForm.addEventListener("submit", (e) => {
                e.preventDefault();
                if (validateEmail(forgotForm.forgotEmail.value).isValid) {
                    // Checking if email exists
                    fetch(`${BASE_API_URL}users/exists/${forgotForm.forgotEmail.value}`)
                        .then(response => response.text())
                        .then(result => JSON.parse(result))
                        .then(obj => {if(obj.exists) {
                            forgotForm.submit();
                            useMessageModal("A unique link for reseting your password has been sent to your email.");
                        } else {
                            useMessageModal("There is no account with such email.");
                        }})
                }
            });
        }
    } catch (e) {
        console.log(e)
    }
});