## How to Setup and Use the Source Code

### You only have to do this step ONCE to get the project on your computer

[Download Java IDE Eclipse](https://www.eclipse.org/downloads/)

**Step 1: Download and Open Git Bash**

[Download Git Bash](https://git-scm.com/install/)

Once installation is done open Git Bash

-> Navigate to your Eclipse workspace folder. Type ```cd``` with a space, then drag your workspace folder from your files directly into the terminal and hit enter.

```cd "C:{path of your eclipse}\eclipse-workspace"```

-> Copy and paste this exact command to download our code

```git clone "https://github.com/fnskye/Inventory-Management-System/"```

Then make sure you are logged in on GitHub and let the Authentication Succeeded.

Once Done

**Step 2: Open Eclipse**

Go to File > Import

Select General > Existing Projects into Workspace and click Next.

Click Browse, select the 'AutomatedInventoryManagementSystem' folder that Git just downloaded, and click Finish.

**Step 3: Setup the Database**

Right Click the 'AutomatedInventoryManagementSystem' folder > Properties > Java Build Path > Libraries > Then add your SQLite to the Classpath ```sqlite-jdbc-3.51.3.0.jar```.

Click Apply and Close > Restart your `Eclipse`
