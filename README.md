# Rock Paper Scissors Arena (Web & Mobile Edition)

A high-performance, data-driven **Rock Paper Scissors** game equipped with real-time analytics, animated donut charts, cumulative score trajectory lines, move frequency distributions, and a 3×3 head-to-head matchup matrix.

Built with pure modern web technologies (HTML5 Canvas, CSS3 Custom Properties, ES6+ JavaScript, Web Audio API), it requires **zero build tools, zero dependencies, and zero compile steps**. It runs seamlessly on any browser and can be deployed in seconds to **Vercel**, **Netlify**, **Cloudflare Pages**, or **GitHub Pages**.

---

## ✨ Features

- **Classic Battle Engine:** Rock crushes Scissors, Scissors cuts Paper, Paper covers Rock.
- **Rhythmic Suspense Countdown:** "Rock... Paper... Scissors... Shoot!" audio-visual suspense loop.
- **Real-Time Data Visualizations:**
  - **Win / Loss / Tie Donut Ratio Chart** (HTML5 Canvas with percentage breakdown)
  - **Cumulative Score Trajectory Line Chart** (Bezier spline tracking score progression over rounds)
  - **Player vs. Computer Move Distribution Bars**
  - **3×3 Head-to-Head Permutation Matrix** (Heatmap of all 9 outcome combinations)
- **Sound Effects:** 100% synthesized Web Audio API sounds (chimes, clicks, countdown beeps, win fanfare). Toggleable with one tap.
- **Dark & Light Mode:** High-contrast Obsidian Dark and Clean Light themes with `localStorage` persistence.
- **Fast Batch Simulation:** `Simulate +5` button for rapid statistical analysis.
- **Filterable Match History:** Filter rounds by All, Wins, Losses, and Ties.
- **Keyboard Shortcuts:** Press `R` for Rock, `P` for Paper, `S` for Scissors, or `Space` for a random quick-move.

---

## 🚀 Instant Deployment Guide

### Option 1: Deploy to Vercel (Recommended)

1. Push this repository to **GitHub**, **GitLab**, or **Bitbucket**.
2. Go to [Vercel Dashboard](https://vercel.com/new).
3. Import this repository.
4. The included `vercel.json` will automatically configure routing.
   - **Framework Preset:** Other / None
   - **Root Directory:** `./`
   - **Output / Publish Directory:** `public` (or leave default with `vercel.json`)
5. Click **Deploy**. Your app will be live with a worldwide CDN in under 30 seconds!

Or deploy via Vercel CLI from your terminal:
```bash
npx vercel
```

---

### Option 2: Deploy to Netlify

1. Push this repository to GitHub.
2. In [Netlify Dashboard](https://app.netlify.com), select **Add new site > Import an existing project**.
3. Choose your repository. Netlify will auto-detect the included `netlify.toml`:
   - **Publish directory:** `public`
4. Click **Deploy Site**.

---

### Option 3: Deploy to GitHub Pages

1. In your GitHub repository, navigate to **Settings > Pages**.
2. Under **Build and deployment > Source**, select **Deploy from a branch**.
3. Select your branch (e.g. `main`) and folder `/public` (or `/root` if using root setup).
4. Save and your game will be live at `https://<username>.github.io/<repo-name>/`.

---

### Option 4: Local Development

Because this web edition uses vanilla web standards, you can run it locally with any static server:

```bash
# Python
python3 -m http.server 8000 --directory public

# Node.js (npx serve)
npx serve public

# PHP
php -S localhost:8000 -t public
```
Then open `http://localhost:8000` in your browser.

---

## 🎨 Design System & Portfolio Assets

Downloadable case study graphics (16:9 3D Hero Banner, Design System specification sheet, and Adaptive App Icon) are available at:
- Web link: `/portfolio_assets.html`
- Asset directory: `public/assets/portfolio/`
