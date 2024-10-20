# Table Tennis Hub - Score Tracker, Match Manager & Performance Analyzer

[![Get it on Google Play](https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg)](https://play.google.com/store/apps/developer?id=Abrebo+Studio)

Take your table tennis experience to the next level with Table Tennis Hub, your ultimate companion for tracking match scores, managing tournaments, and analyzing your performance. Whether you are a casual player or a competitive pro, this app is designed to make it easy for you to organize and track every aspect of your table tennis matches.

## Key Features:

🏓 **Real-Time Score Tracking:**  
Track the scores of your table tennis matches live. Easily input points for both home and away players, and the app will keep a detailed record of each set and game. Perfect for casual games with friends or competitive matches.

🗂️ **Manage Multiple Matches & Tournaments:**  
Create and manage multiple matches or even organize entire tournaments with ease. Whether you're playing a single match or organizing a tournament, the app allows you to add match details, player names, scores, and more, all in one place.

📊 **Detailed Match Statistics & Analytics:**  
Get deeper insights into your performance. Table Tennis Hub provides detailed statistics, including scores, win/loss ratios, and more. Analyze your performance over time and identify areas for improvement. Perfect for players looking to elevate their game.

📝 **Match Confirmation System:**  
Players can confirm match results within the app, ensuring accurate and verified outcomes. No more disputes over match results – both players can review and confirm scores.

🔔 **Match Notifications & Updates:**  
Stay informed with notifications on match updates, confirmations, and tournament progress. Never miss an important update with instant alerts right on your device.

📅 **History & Records:**  
View your entire match history in one place. Table Tennis Hub keeps track of all your previous games, making it easy to go back and see how you've improved over time. See detailed records of who you played, when, and the outcome of each match.

🔄 **Easy Match Updates:**  
Update match details anytime! Even if a match is still ongoing, you can modify scores and player information in real time.

## Why Use Table Tennis Hub?

🏅 **For Players of All Levels:**  
Whether you're just starting out or you're a seasoned table tennis pro, Table Tennis Hub caters to players of all levels. The app is easy to use for beginners but packed with features that even advanced players will appreciate.

👥 **Perfect for Tournaments:**  
If you're organizing a table tennis tournament, Table Tennis Hub makes it easier than ever to keep track of all the matches, scores, and results. It's like having your own tournament assistant right in your pocket!

📱 **Intuitive Interface:**  
Designed with user experience in mind, Table Tennis Hub offers a simple and clean interface, making it easy to input data and navigate through the app.

🌍 **Play Anytime, Anywhere:**  
Whether you're playing in a casual game at home or in a professional tournament, Table Tennis Hub is your go-to tool for keeping track of scores and results.

## Additional Features:

🎯 **Set Custom Match Rules:**  
Customize your matches by setting different rules for games and sets. Whether you’re playing to 11 points or 21, you can configure the app to match your game settings.

📈 **Visual Performance Graphs:**  
View performance graphs that visualize your progress over time. Track your wins, losses, and match history with simple, easy-to-read graphs.

👤 **User Profiles:**  
Create a personalized profile where you can store your match history, statistics, and more. Show off your achievements and track your progress as you improve your table tennis skills.

🛠️ **Support & Feedback:**  
Our dedicated support team is always here to help! If you encounter any issues or have suggestions on how we can improve, feel free to reach out through the app's support section.

## How It Works:

1. **Create an Account:** Sign up using your email address or Google account.
2. **Add a Match:** Input the names of the players, match date, and starting details.
3. **Track Scores:** Use the easy interface to input points for each player as you play.
4. **Confirm Results:** After the match ends, both players can confirm the result, making the outcome official.
5. **Analyze Performance:** Head to the analytics section to review your match history and performance over time.

## Privacy & Data Security:

Your data is important to us! We are committed to protecting your privacy. You can request to delete your account and all associated data at any time through the app’s settings. Rest assured that your personal information is secure and will not be shared with any third parties.

## 🛠 Technologies Used

- **Kotlin:** Core language used for app development.
- **Android Jetpack:** Architecture components including ViewModel, LiveData, Room, and Navigation for seamless app structure.
- **MVVM Architecture:** Ensures clean separation between the UI and business logic.
- **Hilt:** Efficient dependency injection framework used to manage app components.
- **Firebase:** 
  - **Firebase Realtime Database:** For real-time data storage and synchronization across users.
  - **Firebase Authentication:** For secure user authentication via email, Google, and other providers.
  - **Firebase Crashlytics:** To monitor and report app crashes, improving app stability.
- **Google Mobile Ads SDK:** Ad integration for monetizing the app with banner and interstitial ads.
- **Material Design:** Implemented across the app to ensure a modern, user-friendly interface.


### 📂 Detailed Structure

- **data:**
  - **datasource:** Classes for handling data from API or local database.
  - **model:** Data models representing the objects like leagues and teams.
  - **repo:** Repository layer abstracts data sources and interacts with ViewModel.
  
- **di (Dependency Injection):**
  - Dependency injection with Hilt for managing application-wide components.

- **ui:**
  - **adapter:** RecyclerView adapters for displaying teams and league lists.
  - **fragment:** Fragments containing the app’s UI components and navigation logic.
  - **viewmodel:** ViewModel for handling business logic and providing data to UI.
