# Automated Sales and Inventory Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)
![Git](https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white)
![Argon2](https://img.shields.io/badge/Security-Argon2-green?style=for-the-badge)

A complete, standalone Java GUI application designed to help small local suppliers to modernize their business operations. This system integrates real-time inventory tracking, dynamic sales processing, secure user authentication, and offline support platform.

Developed in partial fulfillment of the course **ITE012 - Computer Programming 2** at the **Technological Institute of the Philippines (TIP) - Quezon City**.

---

## Key Features
* **Real-time Inventory Tracking:** Monitors incoming stock and outgoing inventory levels with dynamic product availability.
* **Commercial Transaction Engine:** Generates text-based billing invoices/receipts and processes customer payments.
* **Global Settings Module:** Real-time configuration for global currency symbols (₱, $, €) and dynamic unit localization (pcs, kg, L, boxes).
* **Enterprise-Grade Security:** Utilizes native **Argon2** cryptographic hashing for robust user password protection.
* **System Event Logging:** Integrates Apache **Log4j2** for comprehensive error tracking and operational history.
* **Standalone Deployment:** Packaged as a native `.exe` for direct installation on Windows environments without requiring local IDE setups.

---

## How to Setup and Use the Source Code

### Initial Computer Setup (Do this once)
1. **Download a Java IDE:** We recommend [Eclipse IDE](https://www.eclipse.org/downloads/).
2. **Download Git Bash:** Get the terminal environment from [Git-SCM](https://git-scm.com/install/).

### Step 1: Clone the Repository
Open your Git Bash terminal. Navigate to your Eclipse workspace folder by typing `cd ` (with a space), dragging your workspace folder into the terminal, and pressing Enter:
```bash
cd "C:\path\to\your\eclipse-workspace"
```
Next, download the source code by cloning the repository:
```bash
git clone "https://github.com/fnskye/Inventory-Management-System/" AutomatedInventoryManagementSystem
```
*(If prompted, log in to GitHub to authenticate the connection).*

### Step 2: Import into Eclipse
1. Open Eclipse.
2. Go to `File` > `Import`.
3. Select `General` > `Existing Projects into Workspace` and click **Next**.
4. Click **Browse**, select the `AutomatedInventoryManagementSystem` folder you just cloned, and click **Finish**.

### Step 3: Setup the Libraries
To ensure the database, security, and logging work properly, you must link the provided external `.jar` libraries.
1. Right-click the `AutomatedInventoryManagementSystem` folder in the Package Explorer > `Properties`.
2. Navigate to `Java Build Path` > `Libraries` tab.
3. Select **Classpath** > click `Add External JARs...`.
4. Navigate to the `AutomatedInventoryManagementSystem/lib` folder and select the following:
   * `argon2-jvm-2.11.jar`
   * `argon2-jvm-nolibs-2.11.jar`
   * `jna-5.18.1.jar`
   * `log4j-api-2.25.4.jar`
   * `log4j-core-2.25.4.jar`
   * `sqlite-jdbc-3.51.3.0.jar`
5. Click **Apply and Close**, then restart **Eclipse.**

---

## How to Run the Application

**For Running from Source (Developers)**
1. Open Eclipse and expand the project tree.
2. Navigate to the `Main` package.
3. Right-click `Main.java` > `Run As` > `Java Application`.

**For Standalone Executable (Users)**
If you do not want to run the code via an IDE, simply navigate to the **Releases** section on the right side of this GitHub repository. Download the latest `AutomatedInventory_v1.4.1.exe` and place it in the same folder as the `database.db` file to launch it instantly.

---

## User Manual & Default Access

For launching the application for the first time, the SQLite database will automatically initialize. Use the following default administrative credentials to log in:

* **Username:** `admin`
* **Password:** `admin`

### Core Modules:
* **Inventory Menu:** Add, update, or remove stock. Set dynamic units and monitor thresholds.
* **Order Menu:** Add items to the cart, automatically calculate line totals based on the global currency, and generate receipts.
* **Sales Report:** View aggregated data on past transactions.
* **System Settings:** Only accessible by the `admin` account. Change the system-wide currency formatting instantly.

---

## Acknowledgements & Third-Party Licensing

This software was made possible by the incredible work of the open-source community. The following third-party libraries and tools are integrated into this project:

* **[Apache Log4j 2](https://logging.apache.org/log4j/2.x/)**
  * **Use:** Core system event and error logging.
  * **License:** [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
  * *Copyright © The Apache Software Foundation.*

* **[Argon2-JVM](https://github.com/phxql/argon2-jvm)**
  * **Use:** Cryptographic password hashing and security.
  * **License:** [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
  * *Copyright © phxql and contributors.*

* **[SQLite-JDBC](https://github.com/xerial/sqlite-jdbc)**
  * **Use:** Local database management and SQL drivers.
  * **License:** [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
  * *Copyright © Taro L. Saito and contributors.*

* **[JNA (Java Native Access)](https://github.com/java-native-access/jna)**
  * **Use:** Required dependency for Argon2 native bindings.
  * **License:** [Apache License 2.0 / LGPL](https://github.com/java-native-access/jna/blob/master/LICENSE)
  * *Copyright © Timothy Wall and contributors.*

* **[Launch4j](http://launch4j.sourceforge.net/)**
  * **Use:** Executable (.exe) wrapper generation.
  * **License:** [BSD / MIT](http://launch4j.sourceforge.net/docs.html#License)
  * *Copyright © Grzegorz Kowal.*
    
---

## Development Team
* **Timmalog, John Joshua L.** - Lead Developer / System Security & Architecture
* **Frasco, Joshua Gabriel Q.** - Sales Report Module Developer
* **Julongbayan, Dale Andre A.** - Inventory & Order Module Developer
* **Santos, Alexis Andrie C.** - Main Menu & UI Dashboard Designer
* **Suyo, Hadrian Kendrick S.** - Billing Invoice & Receipt Engine Developer
