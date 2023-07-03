`POST` and `PUT` methods consume a `JSON` object with all the details.
# Booking
`GET /booking` get all bookings

`POST /booking` insert new `Booking` along with `Payment` and `Notification`

`GET /booking/count` get total amount of bookings

`GET /booking/{id}` get booking by booking id

`GET /booking/student/{id}` get booking by student id

`GET /booking/lesson/{id}` get booking by lesson id

`GET /booking/schedule/{id}` get booking by schedule id

`GET /booking/schedule/{id}` get booking by schedule id

`GET /booking/count/{id}` get amount of bookings in the past of given student id

`POST /booking/finished/{id}` sets the booking with the id to finished

`POST /booking/canceled/{id}` set the booking with the id to canceled

`DELETE /booking/{id}` delete booking
# Instrument
`GET /instruments` get all instruments

`GET /instruments/{id}` get instrument by their id

`GET /instruments/count` get count of all the instruments

`GET /instruments/student/{id}` get all instruments learned by student with the given id

`POST /instruments` add a new instrument

`PUT /instruments` updates an instrument

`DELETE /instruments/{id}` deletes an instrument by their id
# Lesson
`GET /lessons` gets all lessons

`GET /lessons/{id}` gets lesson by lesson id

`GET /lessons/count` get number of all lessons

`GET /lessons/student/{id}` get all lessons of the student

`GET /lessons/student/count/{id}` get lesson count of a student

`GET /lessons/teacher/{id}` get all lessons of the teacher

`PUT /lessons` updates a lesson with new lesson object

`POST /lessons` adds a lesson, consumes JSON

`POST /lessons` adds a lesson, consumes type application form
- parameters:
  - lessonPrice
  - lessonInstrument
  - lessonLevel
  - lessonDescription
  - lessonTitle

`DELETE /lessons/{id}` deletes a lesson with the given id 

`GET /lessons/search?lessonOffset=0&lessonInstrumentId=%%&lessonSkillId=%%&lessonRating=%%&lessonLocation=%%&lessonAvailbility=%%&lessonType=%%` search for lesson by filtering the instrument id, skill id, lesson rating and lesson location.
- Query parameters:
  - lessonOffset
  - lessonInstrumentId
  - lessonSkillId
  - lessonRating
  - lessonLocation
  - lessonAvailability
  - lessonType
# Messages
`GET /message/participant/{id}` gets all message where the sending id is `id` or the receiving id is `id`.

`POST /message` adds a new message
# Notification
`POST /notification` adds a new notification

`GET /notification/{id}` gets notification by id

`GET /notification/user/id` gets notification for a user id

`GET /notification/count` gets amount of notification

`GET /notification/confirm/{id}` confirms the notification

`DELETE /notification/{id}` delete notification with the id
# Payment
- payments are added along with the booking

`PUT /payment/{id}` updates payment to being paid with the current timestamp
# Review
`GET /reviews` gets all reviews

`GET /reviews/teacher/{id}` gets all reviews with their teacher id

`GET /reviews/count/{id}` gets the count of the reviews of the teacher with id `id`

`GET /reviews/has-review-from/{lessonId}/studentId}` gets the count of the reviews written by that student given that lesson

`POST /reviews` adds a new review, consumes type application form
- parameters:
  - addMark
  - reviewComment
  - teacherId
  - studentId
  - lessonId

`DELETE /reviews/{lessonId}/{studentId}` deletes a review from a student for the given lesson
# Skill
`GET /skill` gets all skills

`GET /skill/{id}` gets skill by its id
# Teacher Instruments
`GET teacher-instruments` gets all instruments of the teacher

`GET teacher-instruments/{id}` gets all instruments of the teacher by their teacher id

`GET teacher-instruments/instrument/{id}` gets all instruments of the teacher by their instrument id
# TeacherSchedules
- schedule = time slot

`GET /schedule` gets all schedules

`GET /schedule/id` get schedule by their id

`GET /schedule/count` gets total schedule count

`GET /schedule/teacher/{id}` gets schedule by teacher id

`GET /schedule/teacher/free/{id}` get all free schedules by teacher id

`POST /schedule` add schedule for teacher

`DELETE /schedule/{id}` delete schedule with given id
# Teacher
`GET /teachers` gets all teachers

`GET /teachers/{id}` gets teacher by id

`GET /teachers/count` gets count of all teachers

`GET /teachers/search?instrumentId=%%&skillId=%%&rating=%%&online=%%&date=%%`
- online is `true` or `false`
- date is YYYY-MM-DD format
- example: `/teachers/search?instrument-id=3&online=true&date=2023-01-29`

`POST /teachers` adds a new teacher

`PUT /teachers` updates a teacher, consumes type multipart form data
- parameters:
  - editFullname
  - is_online
  - editCountry
  - editCity
  - editExperience
  - editZip
  - instrument

`DELETE /teachers/{id}` deletes teacher by id
# User
`GET /users` gets all users

`GET /users/{id}` get user by their id

`GET /users/count` get count of users (=students)

`GET /users/count/city` get count of all unique cities

`GET users/current` gets current user logged in

`GET /users/profile/{id}` gets profile details of user with the given id

`GET  /users/exists/{email}` returns if a user exists by the given email

`POST /users` creates a user, consumes JSON

`PUT /users` updates user, consumes type multipart form data
- parameters:
  - editFullName
  - is_online
  - editCountry
  - editCity
  - editProfileImage

`PUT /users` updates a user, consumes a json

`PUT /users/description` updates the description of a user

`DELETE /users/{id}` deletes user by their id
# Zip code coordinates
`GET /zipcode-coordinates` gets all zip code coordinates

`GET /zipcode-coordinates/{zipcode}` gets coordinates by zip code