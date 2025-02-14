# MediaLab Task Manager

## 📌 Project Overview
This project is a **Task Management System** developed for the course *"Τεχνολογία Πολυμέσων"* at the National Technical University of Athens (NTUA), School of Electrical and Computer Engineering.

The application allows users to manage tasks by creating, editing, and deleting them. Additionally, it provides features such as category and priority management, reminders, and task search functionalities.

---

## 💻 Technologies Used
- **Programming Language:** Java (JavaFX for GUI)
- **IDE:** Visual Studio Code (VSCode)
- **Build Tool:** Maven
- **Data Storage:** JSON Files

---

## 🚀 How to Run the Project
1. **Clone the Repository:**  
   ```bash
   git clone https://github.com/Sskarm/taskmananger.git
   cd taskmanager/medialab
   ```
2. **Open with VSCode:** Open the project folder with Visual Studio Code.
3. **Run the Application:**  
   Execute the application using the `App.java` file located at:
   ```
   src/main/java/gr/medialab/App.java
   ```
   You can run it using the **Debug Console** of VSCode.

---

## 📂 Project Structure
- **`src/main/java/gr/medialab`**: Main application source code
  - **`App.java`**: Main class to launch the application
  - **`controllers/`**: Contains controllers for GUI management
  - **`models/`**: Data models for Task, Category, Priority, and Reminder
  - **`managers/`**: Handles JSON file operations
- **`src/main/resources/`**: Application resources (e.g., icons, stylesheets)
- **`medialab/`**: JSON files folder for persistent storage:
  - `tasks.json`
  - `categories.json`
  - `priorities.json`
  - `reminders.json`

---

## 📝 Features
- **Task Management:** Add, edit, delete, and search tasks.
- **Category Management:** Manage task categories.
- **Priority Management:** Add or remove priority levels (with a default priority level).
- **Reminders:** Set multiple types of reminders for tasks.
- **Persistent Storage:** Saves data in JSON files for future sessions.
- **Graphical Interface:** Interactive GUI with task statistics (total, completed, delayed, and upcoming tasks).

---

## 📝 Known Issues & Limitations
- The **search functionality** is partially implemented and may not return all expected results.
- **Reminders** are automatically deleted when a task is marked as completed.

---

## 🛡️ Design Principles
- **Object-Oriented Programming (OOP):** The project follows OOP principles.
- **Documentation:** Public methods are documented using Javadoc.

---

## 🤝 Contributors
- **Spyros Skarmoutsos** *(Developer)*

---

## 📝 References
- [JavaFX Official Documentation](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm)
- [Java I/O Overview](https://docs.oracle.com/javase/tutorial/essential/io/)
- [Maven Documentation](https://maven.apache.org/guides/)

---

### 📌 License
This project is licensed under the [MIT License](LICENSE). Feel free to use and modify it.

---
**Thank you for using MediaLab Task Manager! 😊**

