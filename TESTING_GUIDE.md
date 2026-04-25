# Cinescope Testing Guide (Beginner Friendly)

Welcome to the world of testing! This guide will help you understand how to run, interpret, and maintain tests in the Cinescope project.

---

## 1. Types of Tests in this Project

We have two main types of tests:

### A. Unit Tests (Local JVM)
*   **Location:** `app/src/test`
*   **What they do:** Test small pieces of logic (like ViewModels) very fast.
*   **Requirement:** Nothing! They run directly on your computer.

### B. Instrumented Tests (UI Tests)
*   **Location:** `app/src/androidTest`
*   **What they do:** Test how the app looks and feels (like clicking buttons).
*   **Requirement:** An **Android Emulator** or a **Physical Device** connected via USB.

---

## 2. Prerequisites

1.  **Android Studio:** Ensure you have the latest version.
2.  **Emulator/Device:** Required only for tests in the `(androidTest)` folder.
3.  **Gradle Sync:** If you've just added new libraries, always click the "Elephant" icon (Sync Project with Gradle Files).

---

## 3. How to Run Tests

### Method 1: The "Right-Click" (Easiest)
1.  Open the Project view in Android Studio.
2.  Navigate to the folder or specific file you want to test (e.g., `PulseViewModelTest.kt`).
3.  **Right-click** the file name or folder.
4.  Select **Run 'Tests in...'** (with the green play icon).

### Method 2: The "Play Button" (Faster)
1.  Open a test file.
2.  Look for the **Green Play Icons** next to the class name or individual test functions (usually on the left side of the code).
3.  Click it and select "Run".

### Method 3: Command Line (For Professionals/Automation)
Open the terminal at the bottom of Android Studio and type:
*   To run Unit Tests: `./gradlew test`
*   To run UI Tests: `./gradlew connectedAndroidTest`

---

## 4. Will it be done automatically?

*   **Locally:** No. You have to trigger them using the steps above.
*   **In Production:** Usually, teams set up "CI/CD" (Continuous Integration). This is a system (like GitHub Actions) that automatically runs all these tests every time you upload your code. If a test fails, it prevents the code from being merged.

---

## 5. Understanding Results (The Life Cycle)

### Pre-Test (Preparation)
Android Studio will build your app first. If there are syntax errors in your code, the tests won't even start.

### During the Test
*   **Unit Tests:** A small window opens at the bottom showing a progress bar.
*   **UI Tests:** You will see the app automatically open on your phone/emulator and "magically" click buttons by itself. **Do not touch the screen while this is happening!**

### Post-Test (Understanding Pass/Fail)

| Result | Icon | Meaning | What to do |
| :--- | :--- | :--- | :--- |
| **PASS** | ✅ (Green) | Everything works as expected. | Celebrate! Your changes didn't break anything. |
| **FAIL** | ❌ (Red) | Something went wrong. | Click on the failed test in the list. Read the "Assertion Error". It will say something like: `Expected: Pulse Screen but was: Error Screen`. |

---

## 6. Pro-Tips for Beginners
1.  **Test Often:** Run your tests every time you finish a small feature.
2.  **Read the Errors:** Don't be afraid of the red text. It usually tells you exactly which line failed and why.
3.  **Keep it Clean:** If you change a feature (e.g., rename "Pulse" to "Home"), remember to update your tests too, or they will fail!
