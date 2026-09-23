# UI/UX Design System & Portfolio Specification: Rock Paper Scissors Arena

## Project Overview
- **Project Name:** Rock Paper Scissors Arena (Data-Driven Mobile Game)
- **Role:** Product Designer & Mobile UI/UX Engineer
- **Platform:** Android 14+ (Jetpack Compose, Material Design 3)
- **Aesthetic:** Dark Obsidian & Electric Indigo Neo-Sport

---

## 1. Design Philosophy & Craft Goals
- **Purpose-Driven Data Visualization:** Move beyond a simple playground game by elevating gameplay insights with real-time statistics, cumulative score trajectories, head-to-head matrix grids, and streak counters.
- **Adaptive Contrast Architecture:** Full high-contrast support for Light Mode, Dark Mode, and Dynamic System adaptation.
- **Tactile Feedback & Rhythm:** Micro-interactions and countdown animations ("Rock... Paper... Scissors... Shoot!") to build suspense and reward engagement.

---

## 2. Color System & Token Specification

| Token Name | Light Mode Hex | Dark Mode Hex | Usage |
| :--- | :--- | :--- | :--- |
| **Primary (Brand Indigo)** | `#4F46E5` | `#818CF8` | Core buttons, primary indicators, active states |
| **Secondary (Brand Violet)** | `#7C3AED` | `#A78BFA` | Computer opponent avatar, secondary highlights |
| **Win Accent (Emerald)** | `#10B981` | `#10B981` | Victory banners, positive streaks, win rate charts |
| **Loss Accent (Coral Red)** | `#EF4444` | `#EF4444` | Defeat banners, negative score trajectory, losses |
| **Tie Accent (Amber)** | `#F59E0B` | `#F59E0B` | Draw banners, neutral streaks, tie distribution |
| **Move: Rock (Purple)** | `#8B5CF6` | `#8B5CF6` | Rock move button & matrix rows |
| **Move: Paper (Cyan)** | `#06B6D4` | `#06B6D4` | Paper move button & matrix rows |
| **Move: Scissors (Rose)** | `#F43F5E` | `#F43F5E` | Scissors move button & matrix rows |
| **Background Canvas** | `#F8FAFC` | `#0B0F19` | Main screen backdrop |
| **Surface Card** | `#FFFFFF` | `#151C2C` | Elevated cards and containers |
| **Surface Elevated** | `#F1F5F9` | `#1E293B` | Embedded chart areas and inner card sections |

---

## 3. Typography Hierarchy

| Style Level | Font Weight | Tracking / Size | Purpose |
| :--- | :--- | :--- | :--- |
| **Title Large** | Black (900) | 22sp | Main score percentages, hero labels |
| **Title Medium** | Bold (700) | 18sp | Card titles, modal headers, outcome states |
| **Body Medium** | SemiBold (600) | 14sp | Move labels, fighter designations |
| **Label Small** | Bold (700) | 10sp / 1.0sp Tracking | KPI card labels, status badges, column headers |

---

## 4. UI/UX Components Architecture

1. **Battle Arena Component**:
   - Split opponent display (User vs. CPU) with pulsing countdown indicators.
   - Dynamic outcome card with animated scale-in transitions.
   - Interactive 3-column move selection grid with 48dp+ accessible touch targets.
   - Rapid simulation tooling (`Simulate +5`) and one-tap stats reset.

2. **Data Visualization Suite**:
   - **Win Ratio Donut Chart:** Animated Compose Canvas arc rendering with segmented breakdown and central win rate KPI.
   - **Trajectory Line Chart:** Smooth bezier spline mapping cumulative score (+1 Win, -1 Loss) over match history with zero-baseline reference.
   - **Move Frequency Bar Chart:** Side-by-side comparative distribution comparing user move bias against computer choices.
   - **3×3 Matchup Matrix:** Heatmap grid calculating outcomes across all 9 game permutations.

3. **Match History Feed**:
   - Quick filter chips: All, Wins, Losses, Ties.
   - Individual match cards showing round numbers, move icons, and colored status pills.

---

## 5. Portfolio Presentation Assets
The following high-resolution image assets are generated for download and inclusion in your Behance, Dribbble, Figma, or personal portfolio:
- **Hero Showcase Banner:** `/assets/portfolio/rps_hero_showcase.jpg`
- **Design System Spec Sheet:** `/assets/portfolio/rps_design_system.jpg`
- **Adaptive App Icon:** `/assets/portfolio/rps_app_icon.jpg`
