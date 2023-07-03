// Validate client name input
export const validateName = (name, options = {}) => {
    const {min = 3, max = 40, regEx = /^[-a-zA-Z0-9 ]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 3-40 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '-' are allowed ",
        };
    }
    return {isValid: true};
};

// Validate client email input
export const validateEmail = (email, options = {}) => {
    const {
        regEx = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i,
    } = options;
    if (!email) {
        return {isValid: false, detail: "This field is required"};
    }
    if (!regEx.test(email)) {
        return {isValid: false, detail: "Invalid email"};
    }
    return {isValid: true};
};

// Validate client password input
export const validatePassword = (password, options = {}) => {
    const {
        min = 6,
        max = 30,
        regEx = /^((?=\S*?[A-Z])(?=\S*?[a-z])(?=\S*?[0-9])[^\s]{6,})$/,
    } = options;
    if (password.length < min || password.length > max) {
        return {isValid: false, detail: "Length must be between 6-30 characters"};
    }
    if (!regEx.test(password)) {
        return {
            isValid: false,
            detail:
                "Must contain at least 1 uppercase letter, 1 lowercase letter, and 1 number with no spaces",
        };
    }
    return {isValid: true};
};

// Validate client login password input
export const validateLoginPassword = (password, options = {}) => {
    const {
        min = 6,
        max = 30
    } = options;
    if (password.length < min || password.length > max) {
        return {isValid: false, detail: "Length must be between 6-30 characters"};
    }
    return {isValid: true};
};

// Validate topic input
export const validateTopic = (name, options = {}) => {
    const {min = 1, max = 20, regEx = /^[-a-zA-Z0-9 ]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-20 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '-' are allowed ",
        };
    }
    return {isValid: true};
};

// Validate lesson description
export const validateLessonDescription = (name, options = {}) => {
    const {min = 1, max = 120, regEx = /^[-a-zA-Z0-9 .,?!]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-120 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '- , . ? !' are allowed ",
        };
    }
    return {isValid: true};
};

// Validate review mark
export const validateMark = (name, options = {}) => {
    const {min = 1, max = 4, regEx = /^[0-9.]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-4 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only digits and '.' are allowed ",
        };
    }
    if (parseFloat(name) < 1 || parseFloat(name) > 10) {
        return {isValid: false, detail: "Should be between 1 and 10"};
    }
    return {isValid: true};
};

// Validate review mark
export const validateTime = (name, options = {}) => {
    const {max = 5, regEx = /^[0-9:]+$/} = options;
    if (name.length !== max) {
        return {isValid: false, detail: "Format must be hh:mm"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only digits and ':' are allowed ",
        };
    }
    if (parseInt(name.split(":")[0]) < 0 || parseInt(name.split(":")[0]) > 23 || parseInt(name.split(":")[1]) < 0 || parseInt(name.split(":")[1]) > 59 ) {
        return {
            isValid: false,
            detail: "Invalid timestamp.",
        };
    }
    if (name.split(":")[0].length !== 2 || name.split(":")[1].length !== 2) {
        return {
            isValid: false,
            detail: "Invalid timestamp.",
        };
    }
    return {isValid: true};
};

// Validate price
export const validatePrice = (name, options = {}) => {
    const {min = 1, max = 5, regEx = /^[0-9]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-5 digits"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only numbers are allowed ",
        };
    }
    return {isValid: true};
};

// Validate city/country input
export const validateCityCountry = (name, options = {}) => {
    const {min = 1, max = 50, regEx = /^[-a-zA-Z0-9 ]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-50 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '-' are allowed ",
        };
    }
    return {isValid: true};
};

// Validate zipcode input
export const validateZip = (name, options = {}) => {
    const {min = 1, max = 10, regEx = /^[-a-zA-Z0-9 ]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-10 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '-' are allowed ",
        };
    }
    return {isValid: true};
};

// Validate message input
export const validateMessage = (name, options = {}) => {
    const {min = 1, max = 200, regEx = /^[-a-zA-Z0-9 .,?!]+$/} = options;
    if (name.length < min || name.length > max) {
        return {isValid: false, detail: "Length must be between 1-200 characters"};
    }
    if (!regEx.test(name)) {
        return {
            isValid: false,
            detail: "Only letters, numbers and '- , . ? !' are allowed ",
        };
    }
    return {isValid: true};
};

// Show input error
export const showInputError = (input, error) => {
    if (input.nextSibling && input.nextSibling.tagName === 'SPAN') {
        input.nextSibling.remove();
    }
    input.insertAdjacentHTML("afterend", `<span class="input-error"> ${error} </span>`);
};

export const hideInputError = (input) => {
    if (input.nextSibling && input.nextSibling.tagName === 'SPAN') {
        input.nextSibling.remove();
    }
};

// Enable validation on an input
export const inputValidation = (input, validator) => {
    const validateInput = (value = input.value) => {
        const validation = validator(value);
        if (!validation.isValid) {
            showInputError(input, validation.detail);
        } else {
            hideInputError(input);
        }
        return validation.isValid;
    };
    input.addEventListener("change", (e) => {
        validateInput(e.target.value);
    });
    return { validate: validateInput };
};