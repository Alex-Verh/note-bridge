class Question {
    constructor(text, answer) {
        this.text = text;
        this.answer = answer;
    }
}
// Questions - answer constants

const q114 = new Question("What security measures are in place to protect my account?", "Our website has security measures against CSRF, XSS and SQL injections")
const q113 = new Question("Can I change my name?", "To change your name, you should visit the Account Settings page")
const q112 = new Question("How can I change my email address?", "To change your email address, you should visit the Account Settings page")
const q111 = new Question("How do I change my password?", "To change your password, you should visit the Account Settings page")
const q12 = new Question("How do I close my account?", "To delete your account, you should visit the Account Settings page")
const q11 = new Question("How can I update my account details?", [q111, q112, q113, q114])
const q1 = new Question("Account management question",[q11,q12])

const q25 = new Question("Can I request a refund if the lesson was different from what I was expecting?", "As a student, you need to arrange a refund with the teacher. In case you do not agree with the decision made by the teacher, you can contact us by email [EMAIL] or phone [Phone]")
const q24 = new Question("How can I check the experience of the teacher?", "Go the the profile of the teacher, then you can see his/her experience")
const q23 = new Question("How can I leave feedback to a teacher?", "After your lesson with them, you can go to their profile to leave a review and add a rating")
const q22 = new Question("How can I book a lesson?", "There are multiple ways how a student can book a lesson.If you are interested in a lesson near your location, you can visit the map page and apply the filters, otherwise,  you can search on the lessons page.Another way would be to visit the teacher's profile and book the desired lesson")
const q21 = new Question("What to do when I cannot attend a booked lesson?", "If you cannot attend a booked lesson, you should notify the teacher, in order to reschedule the lesson")
const q2 = new Question("Student related question", [q21,q22,q23,q24,q25])

const q36 = new Question("How do I add an instrument?", "Go to your account page and click on ‘Edit Profile’. Here you can click on ‘edit instruments’ on the left hand side to add or delete instruments")
const q35 = new Question("How do I change my availability?", "As a teacher, you have access to the availability page, where you can insert or change a new time slot")
const q34 = new Question("How do I create a lesson?", "Go to your account page and click on ‘Edit Profile’. Now you can click the ‘+’ button on an empty lesson to create a new lesson")
const q33 = new Question("How can I add my location to the map?", "If you are not visible on the map, ensure that you have entered a (valid) address in your account details. If you did this and the issue persists for more than 24h, you should contact customer support")
const q32 = new Question("How can I edit or cancel a lesson?", "Go to your account page and click on ‘Edit Profile’. Now you can see the buttons ‘Edit’ and ‘Delete’ underneath each lesson")
const q312 = new Question("How do I add or update my experience?", "Go to your account page and click on ‘Edit Profile’. On the left side, you can edit your experience")
const q311 = new Question("How can I upload a video?", "Go to your account page and click on ‘Edit Profile’. In the bottom left of your screen, you should see a field to add/delete a video to your profile")
const q31 = new Question("How can I show my expertise as a teacher on my profile?", [q311, q312])
const q3 = new Question("Teacher related question", [q31,q32,q33,q34,q35,q36])

const q43 = new Question("What if my question isn’t answered?", "You can call with customer support, or send us an mail")
const q42 = new Question("How can I provide feedback or suggest improvements for the course or platform?", "Your feedback is always welcomed, you can send your feedback to  email@gmail.com, or call us at +31 XXXXXXXX")
const q41 = new Question("How can I reach out to customer support if I have any issues or inquiries?", "Send us an email email@gmail.com or call us at +31 6 96 360 420")
const q4 = new Question("Customer support question", [q41, q42, q43])

const chat = document.querySelector(".support-chat")
const chat_bubble = document.querySelector(".support-bubble")
const chat_block = document.querySelector(".support-chat__block")

chat_bubble.addEventListener('click', (event) => {
        chat_bubble.classList.add("none")
        chat.classList.remove("none")
})

// Selecting right answer to clicked question
let questions = [q1, q2, q3, q4];
chat_block.addEventListener('click', (event) => {
    let options = document.querySelectorAll(".support-chat__to-select")
    if (event.target.classList.contains("support-chat__to-select")){
        options.forEach(e => {
            e.classList.remove("support-chat__to-select")
        })
        event.target.classList.add("support-chat__selected")
        let selectedQuestion;
        questions.forEach(e => {if(e.text === event.target.innerText) {selectedQuestion = e}})
        // Append to chat our response
        let selected = document.createElement('div')
        selected.textContent = selectedQuestion.text;
        selected.classList.add("chat__messages-my");
        chat_block.appendChild(selected)
        // Append the answers
        if (Array.isArray(selectedQuestion.answer)) {
            questions = selectedQuestion.answer;
            selectedQuestion.answer.forEach(question => {
                let questionElement = document.createElement('div')
                questionElement.textContent = question.text;
                questionElement.classList.add("support-chat__option", "support-chat__to-select");
                chat_block.appendChild(questionElement)
            })
        } else {
            let answer = document.createElement('div')
            answer.textContent = selectedQuestion.answer;
            answer.classList.add("chat__messages-their");
            let again = document.createElement('div')
            again.textContent = "Do you want to ask another question?";
            again.classList.add("support-chat__option", "support-chat__again");
            chat_block.appendChild(answer)
            chat_block.append(again)
        }
        chat_block.scrollTo({top: chat_block.scrollHeight, left: chat_block.scrollHeight, behavior: 'smooth'})
    }

    if (event.target.classList.contains("support-chat__again")) {
        event.target.classList.remove("support-chat__again")
        questions = [q1, q2, q3, q4];
        questions.forEach(question => {
            let questionElement = document.createElement('div')
            questionElement.textContent = question.text;
            questionElement.classList.add("support-chat__option", "support-chat__to-select");
            chat_block.appendChild(questionElement)
        })
        chat_block.scrollTo({top: chat_block.scrollHeight, left: chat_block.scrollHeight, behavior: 'smooth'})
    }
})

document.querySelector(".support-bubble__open").addEventListener('click', event => {
    chat_bubble.classList.remove("none")
    console.log("jdaskl")
})

