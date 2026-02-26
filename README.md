# 7th Semester Projects

A comprehensive collection of semester 7 projects developed by a collaborative team. This repository contains practical implementations of web and desktop applications showcasing various programming concepts and techniques.

## 📁 Project Structure

### 1. **Java - Swing Notepad Application**
Located in the `java/` folder, this is a feature-rich desktop notepad application built with Java Swing. The original project is split across multiple source files under `java/SwingNotepad/src/com/notepad/`.

**Features:**
- Create, edit, and save text files
- Find and Replace functionality
- Custom font selection and styling
- Clean and intuitive user interface

**Tech Stack:**
- Java
- Swing GUI Framework

### 1a. Compact two-file alternative
A compact, two-file variant has been added for easier sharing and quick compilation:
- `CompactNotepadApp.java` (entry + GUI)
- `CompactNotepadDialogs.java` (font picker + find/replace)

Compile and run the compact version from the `java/SwingNotepad` folder:

```bash
javac -d out_compact src/com/notepad/CompactNotepadDialogs.java src/com/notepad/CompactNotepadApp.java
java -cp out_compact com.notepad.CompactNotepadApp
```

This produces a single runnable compact notepad while preserving the original, more modular project files.

### 2. **Web - MCQS Quiz System**
Located in the `web/` folder, this is an interactive online multiple-choice question (MCQS) quiz system.

**Features:**
- Multiple choice questions with instant feedback
- Score calculation and result display
- Responsive web design
- User-friendly interface

**Tech Stack:**
- HTML5
- CSS3
- JavaScript

## 👥 Contributors

This project was developed by a team of three developers:

| Name | Role |
|------|------|
| **Waqas** | Developer |
| **Zakaria** | Developer |
| **Romanullah** | Developer |

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher (for Java projects)
- Modern web browser (for web projects)

### Running the Java Notepad

1. Navigate to the `java/SwingNotepad` directory
2. Compile the Java files:
   ```bash
   javac -d out src/com/notepad/*.java
   ```
3. Run the application:
   ```bash
   java -cp out com.notepad.Main
   ```

### Running the Web Quiz System

1. Navigate to the `web/` directory
2. Open `index.html` in your web browser
3. Start taking the quiz!

## 📝 License

This project is part of the academic curriculum for the 7th semester.

## 📧 Contact

For any questions or inquiries about this project, feel free to reach out to any of the contributors.

---

**Last Updated:** February 26, 2026
