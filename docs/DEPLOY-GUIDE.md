# Put CareerConnect Live on the Internet: Beginner Guide

At the end you will have a public link like **https://careerconnect-xxxx.onrender.com**
that you can put on your resume. Everything here is **free**.

```
Your PC (Eclipse)  ──push──▶  GitHub (stores code)  ──auto-deploy──▶  Render (runs website)
                                                                         │
                                                         Neon (free PostgreSQL database, Part 4)
```

The hosting files are already added to your project:

| File | What it does |
|------|--------------|
| `Dockerfile` | A recipe that tells Render how to build and start your app |
| `.dockerignore` | Files Render doesn't need (like `target/`) |
| `src/main/resources/application-prod.properties` | Settings for the live website (port, database) |
| `pom.xml` | Now also has the **PostgreSQL** driver for the free online database |
| `.gitignore` | Files Git should not upload (also skips `settings-old/`) |

Your local setup in Eclipse with SQL Server still works exactly the same.

---

## Part 1: Install Git (one time, 5 minutes)

Git is the tool that uploads your code to GitHub.

1. Go to **https://git-scm.com/download/win** and download **Git for Windows**.
2. Run the installer and click **Next** on every screen (the defaults are fine).
3. Check it works: press the **Windows key**, type `cmd`, open **Command Prompt**, and type:
   ```
   git --version
   ```
   You should see something like `git version 2.x`.
4. Tell Git your name and email (use the email of your GitHub account):
   ```
   git config --global user.name "Mustak Ahmed"
   git config --global user.email "you@example.com"
   ```

## Part 2: Put your code on GitHub

### 2.1 Create an empty repository

1. Go to **https://github.com** and sign in (or sign up, it's free).
2. Click the **+** at the top right, then **New repository**.
3. Fill it in:
   - Repository name: `careerconnect-job-portal`
   - Description: `Job portal built with Java, Spring Boot, Spring Security, JPA and SQL Server`
   - Select **Public** (recruiters must be able to see it)
   - **Don't** tick "Add a README" (you already have one)
4. Click **Create repository**. Keep this page open, because you need the link it shows
   (it looks like `https://github.com/YOUR-NAME/careerconnect-job-portal.git`).

### 2.2 Upload your code

1. Open File Explorer and go to `C:\Users\Dell\Downloads\jobportal\jobportal`
   (the folder that contains `pom.xml`).
2. Click the address bar at the top, type `cmd`, and press **Enter**.
   A Command Prompt opens **inside your project folder**.
3. Type these commands one by one, pressing Enter after each:

   ```
   git init
   git add .
   git commit -m "CareerConnect job portal: first version"
   git branch -M main
   git remote add origin https://github.com/YOUR-NAME/careerconnect-job-portal.git
   git push -u origin main
   ```

   | Command | Meaning |
   |---------|---------|
   | `git init` | Start tracking this folder with Git |
   | `git add .` | Select all files (except the ones in `.gitignore`) |
   | `git commit -m "..."` | Save a snapshot with a message |
   | `git branch -M main` | Name the main branch "main" |
   | `git remote add origin ...` | Tell Git where your GitHub repository is (**use your own link**) |
   | `git push -u origin main` | Upload |

4. The first time you push, a window asks you to **sign in to GitHub**. Click **Sign in with your browser** and approve it.
5. Refresh the GitHub page. You should see your files and README. ✅

> **Later, after any change:** run these 3 commands in the same folder and Render updates the website automatically:
> ```
> git add .
> git commit -m "describe your change"
> git push
> ```

## Part 3: Put the website live on Render

1. Go to **https://render.com** and click **Get Started**. Choose **Sign up with GitHub** (easiest).
2. In the Render dashboard click **+ New**, then **Web Service**.
3. Under *Connect a repository*, click **GitHub**, allow Render to see your repositories,
   then select **careerconnect-job-portal** and click **Connect**.
4. Fill in the form:
   - **Name:** `careerconnect` (this becomes part of your link)
   - **Region:** Singapore (closest to India)
   - **Branch:** `main`
   - **Language / Runtime:** **Docker** (Render picks this automatically because of the `Dockerfile`)
   - **Instance Type:** **Free**
5. Click **Deploy Web Service**.
6. Wait. The first build takes **5 to 10 minutes** because it downloads Java libraries.
   Watch the log. When you see `Your service is live 🎉`, it's done.
7. Click the link at the top (like `https://careerconnect-xxxx.onrender.com`). Your website is live! 🎉
8. Log in with `candidate@gmail.com` / `candidate123` to test it.

**Good to know about the free plan:**
- If nobody visits for 15 minutes, the site goes to "sleep". The next visitor waits about 30 to 60 seconds while it wakes up. That's normal for free hosting.
- Right now the live site uses a **temporary database**: the demo data is created again on every restart, and new sign-ups disappear. That's fine for a demo. Do Part 4 if you want data to be saved permanently.

## Part 4 (optional, recommended): Free permanent database with Neon

Render's free database is deleted after 30 days, so use **Neon**, which has a free PostgreSQL database that doesn't expire.

1. Go to **https://neon.tech**, click **Sign up** and choose **Continue with GitHub**.
2. Create a project:
   - Project name: `careerconnect`
   - Region: **AWS Asia Pacific (Singapore)**
   - Click **Create**
3. On the dashboard click **Connect**. You'll see a connection string like:
   ```
   postgresql://neondb_owner:AbC123xyz@ep-cool-name-123456.ap-southeast-1.aws.neon.tech/neondb?sslmode=require
   ```
   Split it into 3 parts:

   | Part | In the example |
   |------|----------------|
   | user | `neondb_owner` (between `//` and `:`) |
   | password | `AbC123xyz` (between `:` and `@`) |
   | host + database | `ep-cool-name-123456.ap-southeast-1.aws.neon.tech/neondb` |

4. In **Render**, open your `careerconnect` service, click **Environment** in the left menu,
   then **Add Environment Variable** three times:

   | Key | Value |
   |-----|-------|
   | `DB_URL` | `jdbc:postgresql://` + host + database + `?sslmode=require`<br>e.g. `jdbc:postgresql://ep-cool-name-123456.ap-southeast-1.aws.neon.tech/neondb?sslmode=require` |
   | `DB_USERNAME` | the user, e.g. `neondb_owner` |
   | `DB_PASSWORD` | the password |

   ⚠️ Note that `DB_URL` starts with **`jdbc:postgresql://`**. Java needs that `jdbc:` in front, and the user and password go in their own variables, not in the URL.
5. Click **Save, rebuild, and deploy**. On start the app creates the tables and demo data in Neon automatically.
6. Test it: sign up a new account, then in Render click **Manual Deploy → Restart service**. Your account is still there. ✅

**Why this is safe:** the password lives only in Render's settings, not in your code on GitHub.

## Part 5: Show it off

1. **README:** open `README.md` and add this near the top (use your own links):
   ```markdown
   **🔗 Live demo:** https://careerconnect-xxxx.onrender.com
   (free hosting: the first load can take ~1 minute while the server wakes up)
   ```
   Then run `git add .`, `git commit -m "Add live demo link"` and `git push`.
2. **Resume:** next to the project title, add
   `Live: careerconnect-xxxx.onrender.com | Code: github.com/YOUR-NAME/careerconnect-job-portal`
3. **Resume bullet** you can now add:
   > Containerized the application with Docker and deployed it to Render with a cloud PostgreSQL database (Neon), using Spring profiles and environment variables to keep credentials out of source code.

---

## If something goes wrong

| Problem | Fix |
|---------|-----|
| `git` is not recognized | Close Command Prompt and open it again after installing Git. If it still fails, reinstall Git. |
| `remote origin already exists` | Run `git remote set-url origin https://github.com/YOUR-NAME/careerconnect-job-portal.git` |
| Push asks for a password and fails | GitHub no longer accepts passwords here. Use the **browser sign-in** window. |
| Render build fails | Open the **Logs** tab, copy the red lines and send them to me. |
| Site shows "Application error" | Usually a typo in `DB_URL`. It must start with `jdbc:postgresql://` and end with `?sslmode=require`. |
| Site is slow the first time | It was asleep (free plan). Wait about 1 minute and refresh. |

## New words

| Word | Meaning |
|------|---------|
| **Git / GitHub** | Git saves versions of your code; GitHub stores them online |
| **Commit / Push** | Commit = save a snapshot on your PC; Push = upload it to GitHub |
| **Docker / Dockerfile** | A recipe that packs your app and Java together so it runs the same on any server |
| **Deploy** | Put your app on a server so anyone can open it |
| **Spring profile** | A set of settings for one situation (`prod` = live website, `h2` = quick test) |
| **Environment variable** | A setting given to the app by the server (like `DB_PASSWORD`), kept outside the code |
