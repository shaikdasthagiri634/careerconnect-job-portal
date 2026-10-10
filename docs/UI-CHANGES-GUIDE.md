# New Look for CareerConnect: Beginner Guide

This guide explains every change made to make the website look more attractive.
You don't need to write any code. The files are already copied into your project.
Just follow **Part A** to see the result, then read **Part B** to understand what changed.

---

## Part A: See the new design (5 steps)

1. **Open Eclipse** and your `jobportal` project.
2. **Refresh the project** so Eclipse sees the new files:
   right-click the project → **Refresh** (or press **F5**).
3. **Stop the app** if it is already running: click the red square ■ in the *Console* tab.
4. **Start the app again**: open `JobPortalApplication.java` → right-click → **Run As → Java Application**.
5. Open **http://localhost:8080** in Chrome and press **Ctrl + F5** (this clears the old cached style).

You should see a big photo at the top with a dark blue gradient over it, floating "glass" cards,
colourful icons, and cards that lift up when you move the mouse over them.

> The photos load from the internet, so your computer must be online.
> To use your own pictures, see **Part C**.

---

## Part B: What changed, file by file

Only **5 files** changed and **2 new files** were added. No Java code was changed,
so your database, login and API work exactly the same.

All files are inside `src/main/resources/`.

| # | File | New or changed | What it does |
|---|------|----------------|--------------|
| 1 | `static/css/style.css` | Changed | All colours, gradients ("shades"), background photos and animations |
| 2 | `static/js/main.js` | **New** | Small effects: fade-in on scroll, numbers counting up, back-to-top button |
| 3 | `templates/index.html` | Changed | The home page: new sections added |
| 4 | `templates/fragments/layout.html` | Changed | Loads `main.js` and adds the back-to-top button on every page |
| 5 | `templates/auth/login.html` | Changed | Small class name so the title is white on the photo |
| 6 | `templates/auth/register.html` | Changed | Same as login |
| 7 | `static/images/` | **New folder** | Put your own background pictures here (optional) |

### 1. `style.css`: the "paint" of the website

CSS decides how things **look** (colour, size, shadow). HTML decides **what** is on the page.

**a) Colour and gradient variables.** At the top of the file:

```css
--cc-grad-main: linear-gradient(135deg, #0f9d8a 0%, #2563eb 100%);
```

- `linear-gradient` blends one colour into another: a "shade".
- `135deg` is the direction (top-left to bottom-right).
- `#0f9d8a` is teal and `#2563eb` is blue.
- `--cc-grad-main` is a **variable**: you write the gradient once and use it everywhere with `var(--cc-grad-main)`.

👉 **Try it:** change `#2563eb` to `#e11d48` (pink), save, restart the app, then press Ctrl + F5. Every button changes colour.

**b) Background photos.**

```css
.hero {
    background: var(--cc-photo-overlay), var(--cc-img-hero), var(--cc-navy);
    background-size: cover;
}
```

The background has 3 **layers**, stacked like transparent sheets:
1. `--cc-photo-overlay`: a see-through dark gradient, so white text is easy to read
2. `--cc-img-hero`: the photo
3. `--cc-navy`: a plain dark colour shown if the photo can't load

`background-size: cover` stretches the photo to fill the whole area.

**c) Glass effect** (the floating cards and the top menu):

```css
background: rgba(255,255,255,.12);   /* white at 12% → see-through */
backdrop-filter: blur(14px);          /* blurs whatever is behind it */
```

**d) Hover effects.** When the mouse is over a job card it moves up and gets a shadow:

```css
.job-card:hover { transform: translateY(-4px); box-shadow: ...; }
```

**e) Animations.** `@keyframes float` moves the glass cards slowly up and down.
`.reveal` hides items at first; `.reveal.visible` shows them with a fade.

### 2. `main.js`: small effects (new file)

JavaScript runs in the browser and makes the page **move**. It does 3 things:

1. **Fade in on scroll.** It finds every element with `class="reveal"` and adds `visible` when you scroll to it.
2. **Count-up numbers.** The 3 statistics count from 0 up to the real number from your database.
3. **Back-to-top button.** A round arrow appears at the bottom right after you scroll down.

### 3. `index.html`: the home page

New sections, from top to bottom:

| Section | What you see | Real data? |
|---------|--------------|-----------|
| Hero | Photo + gradient, green "X jobs open right now" badge, search box, popular tags | ✅ Job count and search are real |
| Floating cards | 3 glass cards (decoration only, hidden on phones) | ❌ Example only |
| Stats | Open positions / Companies / Candidates with colourful icons | ✅ From database |
| Tech strip | Java, SQL Server, Cloud... | Decoration |
| **Browse by category** | 4 coloured tiles. Clicking one searches jobs | ✅ Runs a real search |
| Latest jobs | Same as before, now with hover lift | ✅ From database |
| How it works | 3 steps with gradient circles | Text |
| **Why CareerConnect** | Photo + 3 features | Text |
| Hiring band | Purple-blue gradient with photo | Text |

**How a Thymeleaf link works** (used by the category tiles):

```html
<a th:href="@{/jobs(keyword='Java')}">
```

Thymeleaf turns this into `/jobs?keyword=Java`, which your existing `HomeController` already understands.
That's why no Java code had to change.

### 4. `layout.html`: shared parts of every page

Two lines were added at the bottom (inside `scripts`):

```html
<button id="backToTop" ...>        <!-- the round arrow button -->
<script th:src="@{/js/main.js}"></script>   <!-- loads the effects -->
```

Because every page includes this fragment, every page gets the effects.

**Why it's allowed:** your `SecurityConfig.java` already lets everyone open `/js/**`, `/css/**` and `/images/**`.

### 5 & 6. `login.html` and `register.html`

Only one class, `auth-intro`, was added so the "Welcome back" title turns white on top of the photo.
The form itself did not change.

---

## Part C: Use your own background pictures

1. Pick 4 landscape pictures (about **1920 × 1080**, under **500 KB** each).
2. Rename them exactly:
   - `hero-bg.jpg`: home page top
   - `page-bg.jpg`: header of the Jobs, Dashboard and About pages
   - `auth-bg.jpg`: Login and Sign up pages
   - `cta-bg.jpg`: "Why CareerConnect" photo and the "Hiring?" band
3. Copy them into `src/main/resources/static/images/`.
4. In Eclipse: **Refresh (F5)**, restart the app, then press **Ctrl + F5** in the browser.

If a picture is missing, the site automatically uses the online photo, so nothing breaks.

**Change the shade on top of the photo:** in `style.css` find `--cc-photo-overlay`.
The last number in each `rgba(...)` is the **darkness**: `.92` is very dark, `.5` is lighter.

---

## Part D: Undo everything (if you need to)

The original files are saved in `docs/backup-original-ui/`.
Copy them back over the new ones in `src/main/resources/` and restart the app.

---

## Words you'll hear in interviews

| Word | Meaning |
|------|---------|
| **Responsive** | The page adapts to phone and desktop (Bootstrap's `col-md-4`, `col-lg-6` classes) |
| **CSS variables** | `--name: value;` reused with `var(--name)` |
| **Gradient** | A smooth blend of colours (`linear-gradient`) |
| **Glassmorphism** | See-through, blurred "glass" cards (`backdrop-filter: blur`) |
| **Thymeleaf fragment** | A reusable piece of HTML (navbar, footer) shared by all pages |
| **Static resources** | Files Spring Boot serves as-is from `static/` (CSS, JS, images) |
| **Accessibility** | Animations switch off automatically for users who turned off motion (`prefers-reduced-motion`) |
