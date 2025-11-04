External IP : 34.29.96.80

git access token: [REDACTED]


# Google Cloud E2 Hello World Deployment - Game Plan



## Project Goal
Deploy a simple "Hello World" webpage to a Google Cloud E2 instance and set up version control with GitHub.

---

## Phase 1: Local Setup

### Step 1: Create Project Directory
```bash
mkdir hello-world-gcp
cd hello-world-gcp
```

### Step 2: Initialize Git Repository
```bash
git init
```

### Step 3: Create Hello World HTML File
Create a file named `index.html`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello World - Workout Logger</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f0f0f0;
        }
        .container {
            text-align: center;
            background-color: white;
            padding: 50px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #333;
        }
        p {
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Hello World!</h1>
        <p>Workout Logger - Coming Soon</p>
        <p>Deployed on Google Cloud E2</p>
    </div>
</body>
</html>
```

### Step 4: Create README.md
Create a file named `README.md`:

```markdown
# Hello World - Google Cloud E2 Deployment

This is a simple "Hello World" webpage deployed on Google Cloud E2 instance.

## Project Info
- **Purpose**: Initial deployment test for Workout Logger application
- **Cloud Provider**: Google Cloud Platform (E2 instance)
- **OS**: Ubuntu Linux
- **Web Server**: Nginx

## Deployment
This page is hosted on Google Cloud E2 instance running Ubuntu with Nginx as the web server.

## Future Plans
This will eventually become the Workout/Exercise Logger web application.
```

### Step 5: Create .gitignore
Create a file named `.gitignore`:

```
# OS files
.DS_Store
Thumbs.db

# IDE
.vscode/
.idea/

# Temporary files
*.tmp
*.log
```

### Step 6: Initial Git Commit
```bash
git add .
git commit -m "Initial commit: Hello World page"
```

---

## Phase 2: GitHub Setup

### Step 7: Create GitHub Repository
1. Go to https://github.com
2. Click the "+" icon in the top right corner
3. Select "New repository"
4. Name it `hello-world-gcp` or `workout-logger-setup`
5. Keep it public
6. Do NOT initialize with README (you already have one)
7. Click "Create repository"

### Step 8: Link Local Repository to GitHub
Replace `YOUR_USERNAME` with your actual GitHub username:

```bash
git remote add origin https://github.com/YOUR_USERNAME/hello-world-gcp.git
git branch -M main
git push -u origin main
```

---

## Phase 3: Google Cloud E2 Instance Setup

### Step 9: Create Google Cloud Account
1. Go to https://cloud.google.com
2. Sign up or log in
3. You might get $300 free credit for new users

### Step 10: Create a New Project
1. Go to Google Cloud Console: https://console.cloud.google.com
2. Click on the project dropdown at the top
3. Select "New Project"
4. Enter a Project Name (e.g., `CSCI4830-Project-YourLastName` or `workout-logger-project`)
5. Click "Create"
6. Select the newly created project from the dropdown

### Step 11: Enable Compute Engine API
1. Navigate to "Compute Engine" in the left menu (or search for it)
2. Click "Enable" if prompted (API activation may take a few minutes)
3. You will be redirected to "VM Instances" page

### Step 12: Create Firewall Rule for Port 8000
**Important: Do this BEFORE creating your instance**

1. In the Search Bar at the top, type "Firewall"
2. Select "VPC network" > "Firewall"
3. Click "Create Firewall Rule"
4. Configure the rule:
   - **Name**: `web8000` (or similar)
   - **Targets**: All instances in the network
   - **Source IPv4 ranges**: `0.0.0.0/0`
   - **Protocols and ports**: 
     - Select "Specified protocols and ports"
     - Check "TCP"
     - Enter `8000` in the port field
5. Click "Create"

### Step 13: Create E2 Instance
1. Navigate to "Compute Engine" > "VM instances"
2. Click "Create Instance"
3. Configure the instance:
   - **Name**: `csci4830-server-yourLastName` (e.g., `csci4830-server-smith`)
   - **Region**: Choose one close to you (e.g., `us-central1`)
   - **Zone**: Keep default
   - **Machine configuration**: 
     - Click "General Purpose"
     - Series: Select "E2" (low cost, day-to-day computing)
     - Machine type: `e2-medium` or `e2-small`
   - **Boot disk**: 
     - In the Left Menu, click "OS and Storage"
     - Click "Change"
     - Operating System: Ubuntu
     - Version: Ubuntu 22.04 LTS (x86/64) - **Do NOT select Minimal**
     - Boot disk type: Standard persistent disk
     - Size: 30 GB
     - Click "Select"
   - **Networking**: 
     - In the Left Menu, click "Networking"
     - ✅ Check "Allow HTTP traffic"
     - ✅ Check "Allow HTTPS traffic"
4. Scroll down and click "Create"
5. Wait for the instance to start (green checkmark)

### Step 13: Note Your External IP
- Once the instance is running, you'll see an "External IP" address
- Write this down - you'll need it to access your webpage

---

## Phase 4: Server Configuration

### Step 14: Connect to Your Instance via SSH
In the Google Cloud Console:
1. Go to "Compute Engine" > "VM instances"
2. Find your instance
3. Click the "SSH" button (this opens a browser-based terminal)

### Step 15: Update the Server
Run these commands in the SSH terminal:

```bash
sudo apt update
sudo apt upgrade -y
```

### Step 16: Install Nginx Web Server
```bash
sudo apt install nginx -y
```

### Step 17: Start and Enable Nginx
```bash
sudo systemctl start nginx
sudo systemctl enable nginx
```

### Step 18: Check Nginx Status
```bash
sudo systemctl status nginx
```
- You should see "active (running)" in green
- Press `q` to exit

### Step 19: Test Basic Access
- Open a browser and go to your External IP address
- You should see the default Nginx welcome page
- This confirms the web server is working!

---

## Phase 5: Deploy Hello World Page

### Step 20: Install Git on Server
```bash
sudo apt install git -y
```

### Step 21: Clone Your Repository
Replace `YOUR_USERNAME` with your GitHub username:

```bash
cd /tmp
git clone https://github.com/YOUR_USERNAME/hello-world-gcp.git
```

### Step 22: Copy HTML File to Nginx Directory
```bash
sudo cp /tmp/hello-world-gcp/index.html /var/www/html/
```

### Step 23: Set Proper Permissions
```bash
sudo chown www-data:www-data /var/www/html/index.html
sudo chmod 644 /var/www/html/index.html
```

### Step 24: Restart Nginx
```bash
sudo systemctl restart nginx
```

---

## Phase 6: Verify and Test

### Step 25: Test Your Hello World Page
- Open a browser
- Go to `http://YOUR_EXTERNAL_IP`
- You should see your "Hello World - Workout Logger Coming Soon" page!

### Step 26: Create Deployment Documentation
Create a new file locally called `DEPLOYMENT.md`:

```markdown
# Deployment Information

## Server Details
- **Cloud Provider**: Google Cloud Platform
- **Instance Type**: E2-micro
- **Operating System**: Ubuntu 22.04 LTS
- **Web Server**: Nginx
- **External IP**: [YOUR_EXTERNAL_IP_HERE]

## Live URL
http://[YOUR_EXTERNAL_IP_HERE]

## GitHub Repository
https://github.com/[YOUR_USERNAME]/hello-world-gcp

## Deployment Steps
1. Created Google Cloud E2 instance
2. Installed and configured Nginx
3. Cloned GitHub repository to server
4. Deployed index.html to /var/www/html/

## Future Updates
To update the webpage:
1. Make changes locally
2. Commit and push to GitHub
3. SSH into server
4. Pull latest changes from GitHub
5. Copy updated files to /var/www/html/
```

### Step 27: Commit Deployment Documentation
```bash
git add DEPLOYMENT.md
git commit -m "Add deployment documentation with server details"
git push origin main
```

---

## Phase 7: Submission

### What to Submit:
1. **URL to your live webpage**: `http://YOUR_EXTERNAL_IP`
2. **GitHub repository URL**: `https://github.com/YOUR_USERNAME/hello-world-gcp`
3. **Screenshots** (if required):
   - Screenshot of your Hello World page in browser
   - Screenshot of your GitHub repository
   - Screenshot of Google Cloud E2 instance running

---

## Troubleshooting

### If webpage doesn't load:
```bash
# Check if Nginx is running
sudo systemctl status nginx

# Check if firewall allows HTTP
sudo ufw status

# Check Nginx error logs
sudo tail -f /var/log/nginx/error.log

# Restart Nginx
sudo systemctl restart nginx
```

### If you need to update the page:
```bash
# SSH into your server
# Then pull latest changes
cd /tmp
rm -rf hello-world-gcp
git clone https://github.com/YOUR_USERNAME/hello-world-gcp.git
sudo cp /tmp/hello-world-gcp/index.html /var/www/html/
sudo systemctl restart nginx
```

---

## Git Commit Best Practices

Make meaningful commits as you work:

```bash
git add index.html
git commit -m "Create initial Hello World HTML page"

git add README.md
git commit -m "Add project README with deployment info"

git add DEPLOYMENT.md
git commit -m "Document deployment process and server details"
```

---

## Quick Reference Commands

### Git Commands
- `git status` - Check what files have changed
- `git add <file>` - Stage a file for commit
- `git commit -m "message"` - Commit staged changes
- `git push origin main` - Push commits to GitHub
- `git log` - View commit history

### Server Commands
- `sudo systemctl status nginx` - Check Nginx status
- `sudo systemctl restart nginx` - Restart Nginx
- `sudo systemctl stop nginx` - Stop Nginx
- `sudo systemctl start nginx` - Start Nginx

---

## Cost Management

### To avoid charges:
- The e2-micro instance is very cheap (around $7-8/month)
- Google gives $300 free credit for new users
- When done with assignment, you can STOP the instance (don't delete, just stop)
- Stopped instances don't incur compute charges (only storage, which is minimal)

### To stop your instance:
1. Go to Compute Engine > VM instances
2. Select your instance
3. Click "Stop" at the top
4. Start it again when needed

---

## Next Steps (Future Assignments)

After this assignment, you'll probably:
1. Set up Spring Boot backend on the server
2. Set up React frontend
3. Configure them to work together
4. Set up SQLite database
5. Build the full Workout Logger application

But for now, just get this Hello World deployed successfully!

---

Good luck! 🚀