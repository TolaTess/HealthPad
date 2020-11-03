# HealthPad
HealthPad is an Andriod messaging application that connects users with health professionals in realtime. This app was built using Java Programming language, Firebase authentication, realtime database, file storage and Picasso image library. The MVP architecture model was used to keep the code simple, easy to understand and maintain.

* Create a health messenger mobile app
* Login and Signup for users with Firebase
* Sign up as a Doctor feature
* Fetch and show users posts
* Search for a doctors
* Request and accept consultations
* Account Setting / Profile feature
* Messenger feature

Profile setting | Search for a Doctor | Consultation via chat
--- | --- | ---
![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/useracced.gif "") |![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/searchdoced.gif "") | ![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/consultationed.gif "")

## Architecture
The architecture used for this application is the MVP(Model, View and Presenter). Using MVP ensures that the code is simple, easy to understand, and able to be changed quickly, making the application efficient.

#### Model Layer:
![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/modellayer.png "Model directory")
![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/helper.png "Helper directory")

The Model layer is responsible for handling data within the application. It houses all the principal reusable codes to fetch data from the database (Firebase realtime database) and serves it to the Presenter when needed. (Some future refinement is needed with decouple these dependencies further).

#### View Layer:
![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/activity.png "Activity directory")
![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/fragment.png "Fragment directory")

The View layer primary responsibility is to render the UI components as directed by the user. This layer includes Activity and Fragment and their respective Presenters. 

#### Presenter Layer:
The Presenter classes house the java functionality for the application. It receives the user interactions from its View and passes back the business feature requested by the View. It also communicates to the Model layer to get data relevant to the exchange. 

## Feature implementation:

#### Login and Signup for users with Firebase
AuthActivity class instantiates FirebasePresenter and IntentPresenter (Injected by the dependencyRegistry and Dependencyinjection classes).  AuthActivity will redirect the User to MainActivity class if logged in but if not the AuthActivity will render the UI component for user to log in or register.

![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/dependencies.png "Dependency directory")

LoginActivity renders UI component for the user to enter email and password for authentication with Firebase. Successful authentication will direct the user to the MainActivity class, and failure will present a Toast message to the user to “try again”.

#### Sign up as a Doctor feature
SettingActivity checks if the user is a doctor or not, if not, then it displays a “Enable as Doctor” message to prompt the user to set up as a Doctor. If the user interacts with this button, then the user is redirected to the DoctorRegisterActivity class. The user can fill in all the necessary details, and when submitted, this will create a user within the Doctor’s table in the database. 

#### Fetch and show users posts
PostsFragment class instantiates PostsPresenter which renders individual posts using Firebase Adapter and PostsViewHolder class to help bind posts data to the RecyclerView.

#### Request and accept consultations
DoctorProfileActivity class instantiates DoctorProfilePresenter and doctor variables needed for UI components. The user can interact with the request consultation button, which will create a request for the Doctor in the database.

RequestsFragment class instantiates RequestsPresenter and renders these requests using Firebase Adapter and RequestViewHolder class to help bind request data to the RecyclerView. The RequestFragment is only viewable to the doctor users.  

#### Messenger feature
ChatFragment class instantiates ChatPresenter and renders each consultation chats using Firebase Adapter and ChatViewHolder class to help bind chat data to the RecyclerView.

ChatAcitivity class will instantiate ChatActivityPresenter class when a chat is opened and renders users messages using a custom Adapter (MessageAdapter) to generate different ViewHolder depending on the message type. 

![alt text](https://github.com/TolaTess/HealthPad/blob/master/assets/messageadapter.png "Message Adapter Code Snippet")

### Firebase
I used Firebase for this project because it is an application development platform that provides tools to build, improve and grow an efficient application. I used their email and password authentication, realtime database and file storage for images. Firebase also offers push messaging which I will implement as I develop this application further. 

### Improvement
I will continue to improve the codes by implementing further decoupling. I will be introducing another layer between the View and the Presenter to further separate dependencies.

I will be adding payment and push messaging features in future iterations. 
