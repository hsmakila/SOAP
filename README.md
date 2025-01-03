Collecting workspace information

# SOAP Android Application

SOAP is an Android application designed to receive and display emails from an Outlook account. The app periodically checks for new emails and notifies the user about specific emails based on predefined criteria.

## Features

- Periodically checks for new emails from an Outlook account.
- Displays new and flagged emails in a RecyclerView.
- Allows users to mark emails as done or new.
- Notifies users about specific emails.

## Project Structure

```
.gitignore
.gradle/
app/
    .gitignore
    app.iml
    build/
    libs/
    proguard-rules.pro
    src/
        main/
            java/com/example/akila/soap/
                AlarmReceiver.java
                MailDataBaseContract.java
                MailDataBaseHelper.java
                Mails.java
                MailsAdapter.java
                MainActivity.java
            res/
                drawable/
                layout/
                mipmap-anydpi-v26/
                values/
                AndroidManifest.xml
        androidTest/
            java/com/example/akila/soap/
                ExampleInstrumentedTest.java
        test/
            java/com/example/akila/soap/
                ExampleUnitTest.java
build.gradle
gradle/
gradle.properties
gradlew
gradlew.bat
settings.gradle
```

## Getting Started

### Prerequisites

- Android Studio
- Java Development Kit (JDK)
- Gradle

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/soap.git
    ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.

### Running the App

1. Connect an Android device or start an emulator.
2. Click on the "Run" button in Android Studio.

## Configuration

### Email Credentials

Update the email credentials in the 

strings.xml

 file located at 

strings.xml

:
```xml
<string name="mail_user_name">your_email@outlook.com</string>
<string name="mail_password">your_password</string>
```

### ProGuard

ProGuard rules are defined in the 

proguard-rules.pro

 file located at 

proguard-rules.pro

.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Android Support Libraries](https://developer.android.com/topic/libraries/support-library)
- [JavaMail API](https://javaee.github.io/javamail/)

## Contributing

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.
