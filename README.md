# Video Translation Application

A real-time video conferencing prototype with integrated offline speech recognition and automatic translation to Bemba language.

**Developed by:** [Your Name]  
**Institution:** University of Lusaka (UNILUS)  
**Project:** Final Year Project - ICT 431  
**Date:** October 2025

---

## Features

 Real-time video capture and streaming  
 Offline speech recognition (English)  
 Automatic translation to Bemba language  
 UDP-based video transmission  
 Modern, user-friendly interface

---

## System Requirements

### Software Requirements
- **Operating System:** Windows 10/11, macOS, or Linux
- **Java Development Kit (JDK):** Version 24 or higher
- **Apache Maven:** Version 3.6 or higher
- **IDE (Optional):** Apache NetBeans 27, IntelliJ IDEA, or Eclipse

### Hardware Requirements
- **Processor:** Intel Core i5 or equivalent
- **RAM:** Minimum 4 GB (8 GB recommended)
- **Webcam:** Any USB or built-in camera
- **Microphone:** Any USB or built-in microphone
- **Network:** Internet connection (for translation API)

---

## Installation Guide

### Step 1: Install Java JDK 24

**Windows:**
1. Download JDK 24 from: https://www.oracle.com/java/technologies/downloads/
2. Run the installer and follow prompts
3. Verify installation:


---

### Step 2: Install Apache Maven

**Windows:**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - System Properties → Environment Variables
   - Add `C:\Program Files\Apache\maven\bin` to PATH
4. Verify:


---

### Step 3: Install Sphinx4 Libraries

The application uses CMU Sphinx4 for offline speech recognition. Install the JAR files manually:

1. **Download the JAR files:**
   - `sphinx4-core-5prealpha.jar`
   - `sphinx4-data-5prealpha.jar`
   
   From: https://repo1.maven.org/maven2/net/sf/phat/

2. **Install to local Maven repository:**

   Open Command Prompt/Terminal and run:

mvn install:install-file -Dfile="path/to/sphinx4-core-5prealpha.jar" -DgroupId=edu.cmu.sphinx -DartifactId=sphinx4-core -Dversion=5prealpha -Dpackaging=jar

mvn install:install-file -Dfile="path/to/sphinx4-data-5prealpha.jar" -DgroupId=edu.cmu.sphinx -DartifactId=sphinx4-data -Dversion=5prealpha -Dpackaging=jar



Replace `path/to/` with the actual file path.

---

### Step 4: Clone/Download Project

1. Download the project ZIP or clone from repository
2. Extract to a folder, e.g., `C:\VideoTranslationApp`

---

### Step 5: Build the Project

Navigate to the project directory and run:

cd VideoTranslationApp
mvn clean install


This will:
- Download all dependencies
- Compile the source code
- Create the executable JAR file

---

### Step 6: Run the Application

**Option A: Using Maven**

mvn exec:java


**Option B: Using Java directly**

java -jar target/VideoTranslationApp-1.0-SNAPSHOT.jar


**Option C: Using NetBeans**
1. Open NetBeans
2. File → Open Project → Select `VideoTranslationApp`
3. Right-click project → Run

---

## Usage Guide

### 1. Video Capture
- Click **▶ Start Video** to activate your webcam
- Video feed appears in the center panel
- Click **⏹ Stop Video** to stop capture

### 2. Speech Recognition & Translation
- Click **🎤 Start Audio** to begin listening
- Speak clearly in English
- Recognized text appears in the left text area
- Translated Bemba text appears in the right text area
- Click **⏹ Stop Audio** to stop listening

### 3. Video Transmission (Optional)
- Click **📤 Send Video** to transmit video over UDP
- On receiving computer, click **📥 Receive Video**
- Default: localhost (127.0.0.1), port 6000

---

## Troubleshooting

### Issue: "No Video Feed"
**Solution:**
- Check if webcam is connected
- Grant camera permissions to Java application
- Try a different camera index in the code (change `0` to `1` or `2`)

### Issue: Speech recognition returns blank
**Solution:**
- Speak clearly and at normal volume
- Check microphone permissions
- Ensure microphone is not muted
- Test microphone in another app first
- Try simple words: "hello", "test", "one two three"

### Issue: Translation fails
**Solution:**
- Check internet connection
- LibreTranslate API may have rate limits
- Wait a few seconds and try again

### Issue: Build fails
**Solution:**
- Verify JDK 24 is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Delete `target/` folder and rebuild
- Check that Sphinx4 JARs are installed in local Maven repository

---

## Project Structure


**Option C: Using NetBeans**
1. Open NetBeans
2. File → Open Project → Select `VideoTranslationApp`
3. Right-click project → Run

---

## Usage Guide

### 1. Video Capture
- Click **▶ Start Video** to activate your webcam
- Video feed appears in the center panel
- Click **⏹ Stop Video** to stop capture

### 2. Speech Recognition & Translation
- Click **🎤 Start Audio** to begin listening
- Speak clearly in English
- Recognized text appears in the left text area
- Translated Bemba text appears in the right text area
- Click **⏹ Stop Audio** to stop listening

### 3. Video Transmission (Optional)
- Click **📤 Send Video** to transmit video over UDP
- On receiving computer, click **📥 Receive Video**
- Default: localhost (127.0.0.1), port 6000

---

## Troubleshooting

### Issue: "No Video Feed"
**Solution:**
- Check if webcam is connected
- Grant camera permissions to Java application
- Try a different camera index in the code (change `0` to `1` or `2`)

### Issue: Speech recognition returns blank
**Solution:**
- Speak clearly and at normal volume
- Check microphone permissions
- Ensure microphone is not muted
- Test microphone in another app first
- Try simple words: "hello", "test", "one two three"

### Issue: Translation fails
**Solution:**
- Check internet connection
- LibreTranslate API may have rate limits
- Wait a few seconds and try again

### Issue: Build fails
**Solution:**
- Verify JDK 24 is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Delete `target/` folder and rebuild
- Check that Sphinx4 JARs are installed in local Maven repository

---

## Project Structure

VideoTranslationApp/
├── pom.xml # Maven configuration
├── src/
│ └── main/
│ ├── java/
│ │ └── com/unilus/videotranslator/videotranslationapp/
│ │ ├── VideoTranslationApp.java # Main entry point
│ │ ├── ui/
│ │ │ └── MainWindow.java # Main UI window
│ │ ├── video/
│ │ │ ├── VideoCaptureManager.java
│ │ │ └── ImageUtils.java
│ │ ├── audio/
│ │ │ └── OfflineSpeechRecognizer.java
│ │ ├── translation/
│ │ │ └── TranslationEngine.java
│ │ └── network/
│ │ ├── UDPTransmitter.java
│ │ └── UDPReceiver.java
│ └── resources/ # Resources (if any)
└── target/ # Compiled output


---

## Known Limitations

1. **Speech Recognition Accuracy:** Sphinx4 has moderate accuracy (WER ~30-50%). Works best with clear, dictionary words.
2. **Translation API:** Uses LibreTranslate free tier with rate limits.
3. **Video Transmission:** Currently limited to local network (UDP).
4. **Language Support:** Speech recognition is English-only. Translation supports English → Bemba.

---

## Future Enhancements

- Train custom Bemba speech recognition model
- Implement Moses SMT for offline translation
- Add real-time subtitle overlay on video
- Support multiple target languages
- Improve video compression for better network performance
- Add authentication and encryption for secure transmission

---

## Dependencies

- **OpenCV 4.9.0** - Video capture and processing
- **CMU Sphinx4 5prealpha** - Offline speech recognition
- **Apache HttpClient 4.5.14** - HTTP requests for translation
- **Jackson 2.15.2** - JSON parsing
- **MySQL Connector 8.1.0** - Database connectivity (optional)

---

## API Credits

- **LibreTranslate:** https://libretranslate.com (Free translation API)
- **CMU Sphinx:** https://cmusphinx.github.io (Open-source speech recognition)

---

## License

This project is developed as academic coursework for the University of Lusaka.

---

## Contact

For questions or support:  
**Email:** [your-email@example.com]  
**Institution:** University of Lusaka  
**Department:** Information and Communication Technology

---

## Acknowledgments

Special thanks to:
- University of Lusaka Faculty of ICT
- CMU Sphinx project contributors
- LibreTranslate open-source community
- OpenCV development team

---

**Last Updated:** October 21, 2025
