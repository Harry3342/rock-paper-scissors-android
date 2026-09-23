/**
 * Rock Paper Scissors Arena - Web Engine & Analytics
 */

// Move Definitions
const MOVES = {
  rock: { id: 'rock', name: 'Rock', emoji: '✊', beats: 'scissors', color: '#8b5cf6' },
  paper: { id: 'paper', name: 'Paper', emoji: '✋', beats: 'rock', color: '#06b6d4' },
  scissors: { id: 'scissors', name: 'Scissors', emoji: '✌️', beats: 'paper', color: '#f43f5e' }
};

// Sound Synthesizer via Web Audio API (zero external assets needed)
class SoundFx {
  constructor() {
    this.ctx = null;
    this.muted = localStorage.getItem('rps_sound_muted') === 'true';
  }

  init() {
    if (!this.ctx) {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) this.ctx = new AudioCtx();
    }
    if (this.ctx && this.ctx.state === 'suspended') {
      this.ctx.resume();
    }
  }

  toggleMute() {
    this.muted = !this.muted;
    localStorage.setItem('rps_sound_muted', this.muted);
    return this.muted;
  }

  playTone(freq, type = 'sine', duration = 0.1, gain = 0.15) {
    if (this.muted) return;
    this.init();
    if (!this.ctx) return;

    try {
      const osc = this.ctx.createOscillator();
      const gainNode = this.ctx.createGain();
      osc.type = type;
      osc.frequency.setValueAtTime(freq, this.ctx.currentTime);
      gainNode.gain.setValueAtTime(gain, this.ctx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.0001, this.ctx.currentTime + duration);

      osc.connect(gainNode);
      gainNode.connect(this.ctx.destination);

      osc.start();
      osc.stop(this.ctx.currentTime + duration);
    } catch (e) {
      // Audio autoplay policy fallback
    }
  }

  click() {
    this.playTone(400, 'sine', 0.05, 0.1);
  }

  suspense(step) {
    const freqs = [350, 440, 520, 660];
    this.playTone(freqs[step] || 440, 'triangle', 0.12, 0.18);
  }

  win() {
    if (this.muted) return;
    this.init();
    const chords = [523.25, 659.25, 783.99, 1046.50]; // C E G C
    chords.forEach((freq, i) => {
      setTimeout(() => this.playTone(freq, 'triangle', 0.25, 0.2), i * 80);
    });
  }

  loss() {
    if (this.muted) return;
    this.init();
    const notes = [300, 260, 220];
    notes.forEach((freq, i) => {
      setTimeout(() => this.playTone(freq, 'sawtooth', 0.22, 0.15), i * 90);
    });
  }

  tie() {
    if (this.muted) return;
    this.init();
    this.playTone(440, 'sine', 0.15, 0.15);
    setTimeout(() => this.playTone(440, 'sine', 0.2, 0.15), 120);
  }
}

const sfx = new SoundFx();

// Game State Class
class GameState {
  constructor() {
    this.history = [];
    this.isAnimating = false;
    this.activeTab = 0; // 0: donut, 1: trajectory, 2: freq, 3: matrix
    this.filter = 'all'; // all, win, loss, tie
    this.loadState();
  }

  loadState() {
    const saved = localStorage.getItem('rps_web_history');
    if (saved) {
      try {
        this.history = JSON.parse(saved);
      } catch (e) {
        this.populateDemoRounds();
      }
    }
    if (!this.history || this.history.length === 0) {
      this.populateDemoRounds();
    }
  }

  saveState() {
    try {
      localStorage.setItem('rps_web_history', JSON.stringify(this.history));
    } catch (e) {}
  }

  populateDemoRounds() {
    this.history = [
      { round: 1, player: 'rock', computer: 'scissors', result: 'win' },
      { round: 2, player: 'paper', computer: 'rock', result: 'win' },
      { round: 3, player: 'scissors', computer: 'rock', result: 'loss' },
      { round: 4, player: 'rock', computer: 'rock', result: 'tie' },
      { round: 5, player: 'paper', computer: 'scissors', result: 'loss' },
      { round: 6, player: 'rock', computer: 'scissors', result: 'win' },
      { round: 7, player: 'paper', computer: 'rock', result: 'win' }
    ];
    this.saveState();
  }

  getStats() {
    let wins = 0;
    let losses = 0;
    let ties = 0;
    let playerMoves = { rock: 0, paper: 0, scissors: 0 };
    let computerMoves = { rock: 0, paper: 0, scissors: 0 };
    let currentStreak = 0;
    let bestStreak = 0;

    for (const r of this.history) {
      playerMoves[r.player] = (playerMoves[r.player] || 0) + 1;
      computerMoves[r.computer] = (computerMoves[r.computer] || 0) + 1;

      if (r.result === 'win') {
        wins++;
        if (currentStreak >= 0) currentStreak++;
        else currentStreak = 1;
        if (currentStreak > bestStreak) bestStreak = currentStreak;
      } else if (r.result === 'loss') {
        losses++;
        if (currentStreak <= 0) currentStreak--;
        else currentStreak = -1;
      } else {
        ties++;
      }
    }

    const total = this.history.length;
    const winRate = total > 0 ? (wins / total) * 100 : 0;
    const lossRate = total > 0 ? (losses / total) * 100 : 0;
    const tieRate = total > 0 ? (ties / total) * 100 : 0;
    const netScore = wins - losses;

    return {
      total,
      wins,
      losses,
      ties,
      winRate,
      lossRate,
      tieRate,
      currentStreak,
      bestStreak,
      netScore,
      playerMoves,
      computerMoves
    };
  }

  addRound(playerMove, computerMove) {
    let result = 'tie';
    if (playerMove !== computerMove) {
      result = MOVES[playerMove].beats === computerMove ? 'win' : 'loss';
    }

    const roundNum = this.history.length + 1;
    const record = {
      round: roundNum,
      player: playerMove,
      computer: computerMove,
      result: result
    };

    this.history.push(record);
    this.saveState();
    return record;
  }

  simulateRounds(count = 5) {
    const moveKeys = Object.keys(MOVES);
    let lastRecord = null;
    for (let i = 0; i < count; i++) {
      const p = moveKeys[Math.floor(Math.random() * moveKeys.length)];
      const c = moveKeys[Math.floor(Math.random() * moveKeys.length)];
      lastRecord = this.addRound(p, c);
    }
    return lastRecord;
  }

  reset() {
    this.history = [];
    this.saveState();
  }
}

const game = new GameState();

// DOM Elements
const userChoiceEl = document.getElementById('user-choice');
const computerChoiceEl = document.getElementById('computer-choice');
const userSlotNameEl = document.getElementById('user-slot-name');
const computerSlotNameEl = document.getElementById('computer-slot-name');
const vsBadgeEl = document.getElementById('vs-badge');
const resultBannerEl = document.getElementById('result-banner');
const resultTitleEl = document.getElementById('result-title');
const resultDescEl = document.getElementById('result-desc');

const kpiWinRateEl = document.getElementById('kpi-win-rate');
const kpiWinRecordEl = document.getElementById('kpi-win-record');
const kpiStreakEl = document.getElementById('kpi-streak');
const kpiStreakSubEl = document.getElementById('kpi-streak-sub');
const kpiBestStreakEl = document.getElementById('kpi-best-streak');
const kpiNetScoreEl = document.getElementById('kpi-net-score');
const kpiTotalRoundsEl = document.getElementById('kpi-total-rounds');

const historyListEl = document.getElementById('history-list');
const historyCountEl = document.getElementById('history-count');
const muteBtn = document.getElementById('mute-btn');
const themeToggleBtn = document.getElementById('theme-toggle-btn');
const simulateBtn = document.getElementById('simulate-btn');
const resetBtn = document.getElementById('reset-btn');

const moveButtons = document.querySelectorAll('.move-btn');
const vizTabButtons = document.querySelectorAll('.viz-tab-btn');
const historyFilterChips = document.querySelectorAll('.chip-btn');

// Theme Management
function initTheme() {
  const savedTheme = localStorage.getItem('rps_web_theme') || 'dark';
  document.documentElement.setAttribute('data-theme', savedTheme);
  updateThemeIcon(savedTheme);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.setAttribute('data-theme', next);
  localStorage.setItem('rps_web_theme', next);
  updateThemeIcon(next);
  renderVisualizations();
}

function updateThemeIcon(theme) {
  if (themeToggleBtn) {
    themeToggleBtn.textContent = theme === 'dark' ? '🌙' : '☀️';
    themeToggleBtn.setAttribute('title', `Current: ${theme.toUpperCase()} mode. Click to toggle.`);
  }
}

// Sound Button Management
function updateSoundButton() {
  if (muteBtn) {
    muteBtn.textContent = sfx.muted ? '🔇' : '🔊';
    muteBtn.setAttribute('title', sfx.muted ? 'Sound Muted. Click to Unmute.' : 'Sound Active. Click to Mute.');
  }
}

// Update KPI Stats UI
function updateKpis() {
  const stats = game.getStats();

  kpiWinRateEl.textContent = `${Math.round(stats.winRate)}%`;
  kpiWinRecordEl.textContent = `${stats.wins}W - ${stats.losses}L - ${stats.ties}T`;

  if (stats.currentStreak > 0) {
    kpiStreakEl.textContent = `+${stats.currentStreak}`;
    kpiStreakEl.style.color = 'var(--color-win)';
    kpiStreakSubEl.textContent = '🔥 On Fire';
  } else if (stats.currentStreak < 0) {
    kpiStreakEl.textContent = `${stats.currentStreak}`;
    kpiStreakEl.style.color = 'var(--color-loss)';
    kpiStreakSubEl.textContent = '❄️ Slump';
  } else {
    kpiStreakEl.textContent = '0';
    kpiStreakEl.style.color = 'var(--text-primary)';
    kpiStreakSubEl.textContent = 'Neutral';
  }

  kpiBestStreakEl.textContent = `${stats.bestStreak}`;

  kpiNetScoreEl.textContent = stats.netScore > 0 ? `+${stats.netScore}` : `${stats.netScore}`;
  kpiNetScoreEl.style.color = stats.netScore >= 0 ? 'var(--color-win)' : 'var(--color-loss)';
  kpiTotalRoundsEl.textContent = `${stats.total} Rounds`;
}

// Render Result Display
function displayResult(record) {
  const player = MOVES[record.player];
  const computer = MOVES[record.computer];

  userChoiceEl.textContent = player.emoji;
  userSlotNameEl.textContent = player.name;

  computerChoiceEl.textContent = computer.emoji;
  computerSlotNameEl.textContent = computer.name;

  userChoiceEl.classList.remove('highlight-win', 'highlight-loss');
  computerChoiceEl.classList.remove('highlight-win', 'highlight-loss');

  resultBannerEl.className = `result-banner ${record.result}`;

  if (record.result === 'win') {
    resultTitleEl.textContent = 'YOU WIN!';
    resultDescEl.textContent = `${player.name} beats ${computer.name}`;
    userChoiceEl.classList.add('highlight-win');
    computerChoiceEl.classList.add('highlight-loss');
    sfx.win();
  } else if (record.result === 'loss') {
    resultTitleEl.textContent = 'YOU LOSE!';
    resultDescEl.textContent = `${computer.name} beats ${player.name}`;
    computerChoiceEl.classList.add('highlight-win');
    userChoiceEl.classList.add('highlight-loss');
    sfx.loss();
  } else {
    resultTitleEl.textContent = "IT'S A TIE!";
    resultDescEl.textContent = `Both chose ${player.name}`;
    sfx.tie();
  }
}

// Play Move with Suspense Countdown
function playMove(moveKey) {
  if (game.isAnimating) return;
  game.isAnimating = true;

  // Disable buttons while running
  moveButtons.forEach(b => b.disabled = true);
  if (simulateBtn) simulateBtn.disabled = true;

  const player = MOVES[moveKey];
  userChoiceEl.textContent = player.emoji;
  userSlotNameEl.textContent = player.name;
  computerChoiceEl.textContent = '❓';
  computerSlotNameEl.textContent = 'Deciding...';

  userChoiceEl.classList.remove('highlight-win', 'highlight-loss');
  computerChoiceEl.classList.remove('highlight-win', 'highlight-loss');

  resultBannerEl.className = 'result-banner';
  resultTitleEl.textContent = 'Choosing move...';
  resultDescEl.textContent = 'Analyzing battle strategies';

  vsBadgeEl.classList.add('suspense');

  const steps = ['Rock...', 'Paper...', 'Scissors...', 'Shoot!'];
  let currentStep = 0;

  const stepInterval = setInterval(() => {
    if (currentStep < steps.length) {
      vsBadgeEl.textContent = steps[currentStep];
      sfx.suspense(currentStep);
      currentStep++;
    } else {
      clearInterval(stepInterval);
      vsBadgeEl.classList.remove('suspense');
      vsBadgeEl.textContent = 'VS';

      // Pick computer move
      const moveKeys = Object.keys(MOVES);
      const compKey = moveKeys[Math.floor(Math.random() * moveKeys.length)];

      const record = game.addRound(moveKey, compKey);

      displayResult(record);
      updateKpis();
      renderVisualizations();
      renderHistory();

      game.isAnimating = false;
      moveButtons.forEach(b => b.disabled = false);
      if (simulateBtn) simulateBtn.disabled = false;
    }
  }, 190);
}

// Match History Rendering
function renderHistory() {
  const filtered = game.history.filter(item => {
    if (game.filter === 'all') return true;
    return item.result === game.filter;
  }).slice().reverse();

  historyCountEl.textContent = `${filtered.length} Rounds`;
  historyListEl.innerHTML = '';

  if (filtered.length === 0) {
    historyListEl.innerHTML = `
      <div class="empty-state">
        <p>No matches recorded under ${game.filter.toUpperCase()}.</p>
      </div>`;
    return;
  }

  filtered.slice(0, 25).forEach(record => {
    const row = document.createElement('div');
    row.className = 'history-row';

    const p = MOVES[record.player];
    const c = MOVES[record.computer];

    row.innerHTML = `
      <div class="history-round-num">#${record.round}</div>
      <div class="history-moves">
        <span>${p.emoji} ${p.name}</span>
        <span style="color: var(--text-muted); font-size: 0.8rem; margin: 0 4px;">vs</span>
        <span style="color: var(--text-secondary);">${c.emoji} ${c.name}</span>
      </div>
      <div class="history-pill ${record.result}">
        ${record.result.toUpperCase()}
      </div>
    `;
    historyListEl.appendChild(row);
  });
}

// ----------------- DATA VISUALIZATIONS ----------------- //

function renderVisualizations() {
  switch (game.activeTab) {
    case 0:
      renderDonutChart();
      break;
    case 1:
      renderTrajectoryChart();
      break;
    case 2:
      renderFrequencyBars();
      break;
    case 3:
      renderMatchupMatrix();
      break;
  }
}

// Tab 0: Donut Chart
function renderDonutChart() {
  const panel = document.getElementById('viz-content-panel');
  const stats = game.getStats();

  if (stats.total === 0) {
    panel.innerHTML = `<div class="empty-state"><p>Play a round to view Win / Loss distribution</p></div>`;
    return;
  }

  panel.innerHTML = `
    <div class="donut-layout">
      <div class="chart-canvas-wrapper">
        <canvas id="donut-canvas" width="340" height="340"></canvas>
        <div class="donut-center-label">
          <div class="donut-percent">${Math.round(stats.winRate)}%</div>
          <div class="donut-sub">Win Rate</div>
        </div>
      </div>
      <div class="chart-legend">
        <div class="legend-item">
          <div class="legend-dot-label">
            <div class="legend-dot" style="background: var(--color-win)"></div>
            <span>Wins</span>
          </div>
          <div class="legend-val" style="color: var(--color-win)">${stats.wins} (${Math.round(stats.winRate)}%)</div>
        </div>
        <div class="legend-item">
          <div class="legend-dot-label">
            <div class="legend-dot" style="background: var(--color-loss)"></div>
            <span>Losses</span>
          </div>
          <div class="legend-val" style="color: var(--color-loss)">${stats.losses} (${Math.round(stats.lossRate)}%)</div>
        </div>
        <div class="legend-item">
          <div class="legend-dot-label">
            <div class="legend-dot" style="background: var(--color-tie)"></div>
            <span>Ties</span>
          </div>
          <div class="legend-val" style="color: var(--color-tie)">${stats.ties} (${Math.round(stats.tieRate)}%)</div>
        </div>
      </div>
    </div>
  `;

  const canvas = document.getElementById('donut-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  const size = 340;
  const centerX = size / 2;
  const centerY = size / 2;
  const radius = 130;
  const lineWidth = 40;

  ctx.clearRect(0, 0, size, size);

  // Background track
  ctx.beginPath();
  ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
  ctx.strokeStyle = 'rgba(150, 150, 150, 0.12)';
  ctx.lineWidth = lineWidth;
  ctx.stroke();

  // Compute angles
  let startAngle = -Math.PI / 2;
  const total = stats.total;

  const winAngle = (stats.wins / total) * Math.PI * 2;
  const lossAngle = (stats.losses / total) * Math.PI * 2;
  const tieAngle = (stats.ties / total) * Math.PI * 2;

  // Segment colors
  const isLight = document.documentElement.getAttribute('data-theme') === 'light';
  const winColor = isLight ? '#059669' : '#10b981';
  const lossColor = isLight ? '#dc2626' : '#ef4444';
  const tieColor = isLight ? '#d97706' : '#f59e0b';

  function drawArcSegment(angle, color) {
    if (angle <= 0) return;
    ctx.beginPath();
    ctx.arc(centerX, centerY, radius, startAngle, startAngle + angle);
    ctx.strokeStyle = color;
    ctx.lineWidth = lineWidth;
    ctx.stroke();
    startAngle += angle;
  }

  drawArcSegment(winAngle, winColor);
  drawArcSegment(lossAngle, lossColor);
  drawArcSegment(tieAngle, tieColor);
}

// Tab 1: Trajectory Line Chart
function renderTrajectoryChart() {
  const panel = document.getElementById('viz-content-panel');
  if (game.history.length === 0) {
    panel.innerHTML = `<div class="empty-state"><p>Play rounds to view the score trajectory over time</p></div>`;
    return;
  }

  const scores = [0];
  let running = 0;
  game.history.forEach(r => {
    if (r.result === 'win') running += 1;
    else if (r.result === 'loss') running -= 1;
    scores.push(running);
  });

  const lastScore = scores[scores.length - 1];
  const maxScore = Math.max(...scores, 1);
  const minScore = Math.min(...scores, 0);
  const scoreRange = Math.max(maxScore - minScore, 1);

  panel.innerHTML = `
    <div class="line-chart-container">
      <div class="chart-top-stat">
        <span>Cumulative Score Trajectory (+1 Win, -1 Loss)</span>
        <strong style="color: ${lastScore >= 0 ? 'var(--color-win)' : 'var(--color-loss)'}; font-size: 1.1rem;">
          ${lastScore >= 0 ? '+' + lastScore : lastScore}
        </strong>
      </div>
      <div class="line-canvas-box">
        <canvas id="line-canvas"></canvas>
      </div>
      <div class="chart-x-labels">
        <span>Round 0</span>
        <span>Round ${game.history.length}</span>
      </div>
    </div>
  `;

  const canvas = document.getElementById('line-canvas');
  if (!canvas) return;

  const rect = canvas.parentElement.getBoundingClientRect();
  const dpr = window.devicePixelRatio || 1;
  canvas.width = rect.width * dpr;
  canvas.height = rect.height * dpr;

  const ctx = canvas.getContext('2d');
  ctx.scale(dpr, dpr);

  const w = rect.width;
  const h = rect.height;
  const padY = 20;
  const padX = 14;
  const usableH = h - padY * 2;
  const usableW = w - padX * 2;

  // Zero-baseline dashed line
  const zeroRatio = (maxScore - 0) / scoreRange;
  const zeroY = padY + zeroRatio * usableH;

  ctx.strokeStyle = 'rgba(150, 150, 150, 0.3)';
  ctx.setLineDash([4, 4]);
  ctx.beginPath();
  ctx.moveTo(padX, zeroY);
  ctx.lineTo(w - padX, zeroY);
  ctx.stroke();
  ctx.setLineDash([]);

  // Plot Points
  const points = scores.map((sc, i) => {
    const x = padX + (i / (scores.length - 1)) * usableW;
    const y = padY + ((maxScore - sc) / scoreRange) * usableH;
    return { x, y };
  });

  const lineColor = lastScore >= 0 ? '#10b981' : '#ef4444';

  // Area Fill
  const grad = ctx.createLinearGradient(0, 0, 0, h);
  grad.addColorStop(0, lastScore >= 0 ? 'rgba(16, 185, 129, 0.35)' : 'rgba(239, 68, 68, 0.35)');
  grad.addColorStop(1, 'rgba(0, 0, 0, 0)');

  ctx.beginPath();
  ctx.moveTo(points[0].x, zeroY);
  points.forEach(p => ctx.lineTo(p.x, p.y));
  ctx.lineTo(points[points.length - 1].x, zeroY);
  ctx.closePath();
  ctx.fillStyle = grad;
  ctx.fill();

  // Line Stroke
  ctx.beginPath();
  ctx.moveTo(points[0].x, points[0].y);
  points.forEach(p => ctx.lineTo(p.x, p.y));
  ctx.strokeStyle = lineColor;
  ctx.lineWidth = 2.5;
  ctx.stroke();

  // Highlight Current Point
  const lastPoint = points[points.length - 1];
  ctx.beginPath();
  ctx.arc(lastPoint.x, lastPoint.y, 5, 0, Math.PI * 2);
  ctx.fillStyle = lineColor;
  ctx.fill();
  ctx.beginPath();
  ctx.arc(lastPoint.x, lastPoint.y, 2.5, 0, Math.PI * 2);
  ctx.fillStyle = '#ffffff';
  ctx.fill();
}

// Tab 2: Move Frequency Bars
function renderFrequencyBars() {
  const panel = document.getElementById('viz-content-panel');
  const stats = game.getStats();

  if (stats.total === 0) {
    panel.innerHTML = `<div class="empty-state"><p>Play rounds to view move preferences</p></div>`;
    return;
  }

  const moves = [
    { key: 'rock', name: 'Rock ✊', color: 'var(--color-rock)' },
    { key: 'paper', name: 'Paper ✋', color: 'var(--color-paper)' },
    { key: 'scissors', name: 'Scissors ✌️', color: 'var(--color-scissors)' }
  ];

  let rowsHtml = '';
  moves.forEach(m => {
    const userCount = stats.playerMoves[m.key] || 0;
    const compCount = stats.computerMoves[m.key] || 0;
    const userPct = Math.round((userCount / stats.total) * 100);
    const compPct = Math.round((compCount / stats.total) * 100);

    rowsHtml += `
      <div class="freq-item">
        <div class="freq-item-header">
          <span>${m.name}</span>
          <span class="freq-sub">YOU: ${userCount} (${userPct}%) | CPU: ${compCount} (${compPct}%)</span>
        </div>
        <div class="dual-bar-container">
          <div class="bar-track" title="Player Move Share: ${userPct}%">
            <div class="bar-fill" style="width: ${Math.max(userPct, 2)}%; background: ${m.color}"></div>
          </div>
          <div class="bar-track" title="Computer Move Share: ${compPct}%">
            <div class="bar-fill" style="width: ${Math.max(compPct, 2)}%; background: ${m.color}; opacity: 0.45;"></div>
          </div>
        </div>
      </div>
    `;
  });

  panel.innerHTML = `
    <div style="display: flex; justify-content: flex-end; gap: 16px; margin-bottom: 8px; font-size: 0.75rem;">
      <span style="display: inline-flex; align-items: center; gap: 4px;">
        <span style="width: 8px; height: 8px; border-radius: 50%; background: var(--primary);"></span> Player
      </span>
      <span style="display: inline-flex; align-items: center; gap: 4px;">
        <span style="width: 8px; height: 8px; border-radius: 50%; background: var(--secondary); opacity: 0.5;"></span> Computer
      </span>
    </div>
    <div class="freq-list">
      ${rowsHtml}
    </div>
  `;
}

// Tab 3: Matchup Matrix (3x3)
function renderMatchupMatrix() {
  const panel = document.getElementById('viz-content-panel');
  if (game.history.length === 0) {
    panel.innerHTML = `<div class="empty-state"><p>Play rounds to populate the Matchup Matrix</p></div>`;
    return;
  }

  const moveKeys = ['rock', 'paper', 'scissors'];
  const matrix = {};

  moveKeys.forEach(p => {
    moveKeys.forEach(c => {
      matrix[`${p}_${c}`] = 0;
    });
  });

  game.history.forEach(r => {
    const key = `${r.player}_${r.computer}`;
    matrix[key] = (matrix[key] || 0) + 1;
  });

  let tableHtml = `
    <div class="matrix-wrap">
      <table class="matrix-table">
        <thead>
          <tr>
            <th class="matrix-th">YOU \\ CPU</th>
            <th class="matrix-th">✊ Rock</th>
            <th class="matrix-th">✋ Paper</th>
            <th class="matrix-th">✌️ Scis</th>
          </tr>
        </thead>
        <tbody>
  `;

  moveKeys.forEach(p => {
    const pMove = MOVES[p];
    tableHtml += `<tr><td class="matrix-row-header">${pMove.emoji} ${pMove.name}</td>`;

    moveKeys.forEach(c => {
      const count = matrix[`${p}_${c}`] || 0;
      let outcome = 'tie';
      let code = 'T';
      if (p !== c) {
        if (pMove.beats === c) {
          outcome = 'win';
          code = 'W';
        } else {
          outcome = 'loss';
          code = 'L';
        }
      }

      tableHtml += `
        <td>
          <div class="matrix-cell ${outcome}" title="${pMove.name} vs ${MOVES[c].name}: ${outcome.toUpperCase()} (${count} times)">
            <div class="matrix-count">${count}</div>
            <div class="matrix-sub">${code}</div>
          </div>
        </td>
      `;
    });
    tableHtml += `</tr>`;
  });

  tableHtml += `</tbody></table></div>`;
  panel.innerHTML = tableHtml;
}

// Event Listeners
function initEventListeners() {
  // Move selection
  moveButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
      sfx.click();
      const move = btn.getAttribute('data-move');
      playMove(move);
    });
  });

  // Keyboard controls
  window.addEventListener('keydown', (e) => {
    if (game.isAnimating) return;
    const key = e.key.toLowerCase();
    if (key === 'r') playMove('rock');
    if (key === 'p') playMove('paper');
    if (key === 's') playMove('scissors');
    if (key === ' ' || key === 'enter') {
      e.preventDefault();
      const moveKeys = Object.keys(MOVES);
      playMove(moveKeys[Math.floor(Math.random() * moveKeys.length)]);
    }
  });

  // Visualization Tabs
  vizTabButtons.forEach(tab => {
    tab.addEventListener('click', () => {
      sfx.click();
      vizTabButtons.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      game.activeTab = parseInt(tab.getAttribute('data-tab'), 10);
      renderVisualizations();
    });
  });

  // History Filter Chips
  historyFilterChips.forEach(chip => {
    chip.addEventListener('click', () => {
      sfx.click();
      historyFilterChips.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      game.filter = chip.getAttribute('data-filter');
      renderHistory();
    });
  });

  // Simulate +5 Rounds
  if (simulateBtn) {
    simulateBtn.addEventListener('click', () => {
      sfx.click();
      const last = game.simulateRounds(5);
      if (last) displayResult(last);
      updateKpis();
      renderVisualizations();
      renderHistory();
    });
  }

  // Reset Data
  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      if (confirm('Reset all match stats and battle history?')) {
        sfx.click();
        game.reset();
        userChoiceEl.textContent = '—';
        userSlotNameEl.textContent = 'Ready';
        computerChoiceEl.textContent = '—';
        computerSlotNameEl.textContent = 'Ready';
        userChoiceEl.classList.remove('highlight-win', 'highlight-loss');
        computerChoiceEl.classList.remove('highlight-win', 'highlight-loss');
        resultBannerEl.className = 'result-banner';
        resultTitleEl.textContent = 'Choose your move!';
        resultDescEl.textContent = 'Rock, Paper, or Scissors';
        updateKpis();
        renderVisualizations();
        renderHistory();
      }
    });
  }

  // Theme Toggle
  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', () => {
      sfx.click();
      toggleTheme();
    });
  }

  // Mute Sound Toggle
  if (muteBtn) {
    muteBtn.addEventListener('click', () => {
      sfx.toggleMute();
      updateSoundButton();
    });
  }

  // Resize listener for responsive line chart redrawing
  window.addEventListener('resize', () => {
    if (game.activeTab === 1) renderTrajectoryChart();
  });
}

// Initial Launch
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  updateSoundButton();
  initEventListeners();
  updateKpis();
  renderVisualizations();
  renderHistory();

  // If previous round exists, show last result
  if (game.history.length > 0) {
    displayResult(game.history[game.history.length - 1]);
  }
});
