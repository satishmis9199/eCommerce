/**
 * ═══════════════════════════════════════════════════════════════
 *  MyStore Platform — Super Admin Console
 *  script.js  |  UI Controller
 *
 *  Responsibilities:
 *   - Background canvas particle system
 *   - Form validation (email + password)
 *   - Loading state management
 *   - Toast notification system
 *   - Show/hide password
 *   - Keyboard accessibility (Enter key, focus management)
 *   - Auth flow orchestration (calls api.js)
 *   - Copyright year injection
 * ═══════════════════════════════════════════════════════════════
 */

'use strict';

/* ─────────────────────────────────────────────
   SECTION 1 — UTILITY HELPERS
───────────────────────────────────────────── */

/**
 * Queries a single DOM element. Throws if not found (catches typos early).
 * @param {string} selector
 * @param {Document|Element} [context=document]
 * @returns {Element}
 */
function qs(selector, context = document) {
  const el = context.querySelector(selector);
  if (!el) throw new Error(`[MyStore] Element not found: "${selector}"`);
  return el;
}

/**
 * Waits for the specified number of milliseconds.
 * @param {number} ms
 * @returns {Promise<void>}
 */
function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/* ─────────────────────────────────────────────
   SECTION 2 — VALIDATION
───────────────────────────────────────────── */

/**
 * Rules are pure functions: they take a value and return an error string
 * (or null/undefined when valid). This makes them easily testable in isolation.
 */
const Validators = {
  /**
   * Validates an email address.
   * @param {string} value
   * @returns {string|null}
   */
  email(value) {
    if (!value.trim()) return 'Email address is required.';

    // RFC 5322-ish regex — practical for UI validation
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i;
    if (!pattern.test(value.trim())) {
      return 'Please enter a valid email address (e.g. admin@company.com).';
    }

    return null;
  },

  /**
   * Validates the password.
   * @param {string} value
   * @returns {string|null}
   */
  password(value) {
    if (!value) return 'Password is required.';
    if (value.length < 6) return 'Password must be at least 6 characters.';
    return null;
  },
};

/* ─────────────────────────────────────────────
   SECTION 3 — FIELD STATE MANAGER
───────────────────────────────────────────── */

/**
 * Manages the visual state of a form field (neutral, error, valid).
 * Keeps DOM manipulation in one place.
 */
class FieldController {
  /**
   * @param {string} fieldId   — e.g. 'fieldEmail'
   * @param {string} errorId   — e.g. 'emailError'
   */
  constructor(fieldId, errorId) {
    this.fieldEl = document.getElementById(fieldId);
    this.errorEl = document.getElementById(errorId);

    if (!this.fieldEl || !this.errorEl) {
      console.warn(`[FieldController] Elements missing for "${fieldId}"`);
    }
  }

  /** Clears all state classes and error text. */
  reset() {
    this.fieldEl?.classList.remove('is-error', 'is-valid');
    if (this.errorEl) this.errorEl.textContent = '';
  }

  /**
   * Marks the field as invalid and displays an error message.
   * @param {string} message
   */
  setError(message) {
    this.fieldEl?.classList.remove('is-valid');
    this.fieldEl?.classList.add('is-error');
    if (this.errorEl) this.errorEl.textContent = message;
  }

  /** Marks the field as valid. */
  setValid() {
    this.fieldEl?.classList.remove('is-error');
    this.fieldEl?.classList.add('is-valid');
    if (this.errorEl) this.errorEl.textContent = '';
  }

  /**
   * Validates the field inline using the provided rule function.
   * @param {string} value
   * @param {function(string): string|null} rule
   * @returns {boolean} — true when valid
   */
  validate(value, rule) {
    const error = rule(value);
    if (error) {
      this.setError(error);
      return false;
    }
    this.setValid();
    return true;
  }
}

/* ─────────────────────────────────────────────
   SECTION 4 — TOAST NOTIFICATION SYSTEM
───────────────────────────────────────────── */

/**
 * @typedef {'error'|'success'|'warning'|'info'} ToastType
 */

/** Maps toast type → Font Awesome icon class */
const TOAST_ICONS = {
  error:   'fa-solid fa-circle-xmark',
  success: 'fa-solid fa-circle-check',
  warning: 'fa-solid fa-triangle-exclamation',
  info:    'fa-solid fa-circle-info',
};

/** Maps toast type → accessible heading */
const TOAST_TITLES = {
  error:   'Authentication Failed',
  success: 'Success',
  warning: 'Warning',
  info:    'Information',
};

/**
 * Shows a self-dismissing toast notification.
 *
 * @param {object} opts
 * @param {ToastType} opts.type
 * @param {string}    opts.title    — Override default title
 * @param {string}    opts.message  — Detail text
 * @param {number}    [opts.duration=5000] — Auto-dismiss delay in ms
 */
function showToast({ type = 'info', title, message, duration = 5000 }) {
  const container = document.getElementById('toastContainer');
  if (!container) return;

  const toastEl = document.createElement('div');
  toastEl.className = `toast toast--${type}`;
  toastEl.setAttribute('role', 'alert');
  toastEl.setAttribute('aria-live', 'assertive');

  toastEl.innerHTML = `
    <div class="toast__icon-wrap" aria-hidden="true">
      <i class="${TOAST_ICONS[type] || TOAST_ICONS.info}"></i>
    </div>
    <div class="toast__body">
      <div class="toast__title">${escapeHtml(title || TOAST_TITLES[type])}</div>
      ${message ? `<div class="toast__message">${escapeHtml(message)}</div>` : ''}
    </div>
    <button class="toast__close" aria-label="Dismiss notification">
      <i class="fa-solid fa-xmark" aria-hidden="true"></i>
    </button>
  `;

  // Dismiss on close button click
  const closeBtn = toastEl.querySelector('.toast__close');
  closeBtn?.addEventListener('click', () => dismissToast(toastEl));

  container.appendChild(toastEl);

  // Auto-dismiss
  if (duration > 0) {
    setTimeout(() => dismissToast(toastEl), duration);
  }
}

/**
 * Animates a toast out and removes it from the DOM.
 * @param {HTMLElement} toastEl
 */
function dismissToast(toastEl) {
  if (!toastEl || !toastEl.parentNode) return;
  toastEl.classList.add('is-hiding');
  toastEl.addEventListener('animationend', () => toastEl.remove(), { once: true });
}

/**
 * Escapes HTML special characters to prevent XSS in toast content.
 * @param {string} str
 * @returns {string}
 */
function escapeHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/* ─────────────────────────────────────────────
   SECTION 5 — BUTTON LOADING STATE
───────────────────────────────────────────── */

/**
 * Manages the submit button's loading / idle states.
 */
const ButtonState = {
  _btn:     null,
  _label:   null,
  _spinner: null,

  init() {
    this._btn     = document.getElementById('loginBtn');
    this._label   = document.getElementById('btnLabel');
    this._spinner = document.getElementById('btnSpinner');
  },

  /** Enters loading state: disables button, shows spinner. */
  setLoading() {
    if (!this._btn) return;
    this._btn.disabled = true;
    this._btn.setAttribute('aria-busy', 'true');
    this._label.hidden   = true;
    this._spinner.hidden = false;
  },

  /** Restores idle state. */
  setIdle() {
    if (!this._btn) return;
    this._btn.disabled = false;
    this._btn.removeAttribute('aria-busy');
    this._label.hidden   = false;
    this._spinner.hidden = true;
  },
};

/* ─────────────────────────────────────────────
   SECTION 6 — ANIMATED BACKGROUND (Canvas)
───────────────────────────────────────────── */

/**
 * Renders a dark space-like particle field on a <canvas> element.
 * Uses requestAnimationFrame for smooth, efficient animation.
 */
function initBackgroundCanvas() {
  const canvas = document.getElementById('bgCanvas');
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  let animId;
  let particles = [];

  /** Particle parameters */
  const CONFIG = {
    COUNT: 120,
    MIN_RADIUS: 0.5,
    MAX_RADIUS: 2.2,
    MIN_SPEED: 0.08,
    MAX_SPEED: 0.25,
    COLORS: [
      'rgba(99, 102, 241, {a})',   // indigo
      'rgba(129, 140, 248, {a})',  // indigo-light
      'rgba(16, 185, 129, {a})',   // emerald
      'rgba(248, 250, 252, {a})',  // white
    ],
    /** Max connection distance */
    CONNECT_DIST: 130,
    CONNECT_OPACITY: 0.06,
  };

  function resize() {
    canvas.width  = window.innerWidth;
    canvas.height = window.innerHeight;
  }

  function randomBetween(min, max) {
    return min + Math.random() * (max - min);
  }

  function createParticle() {
    const colorTemplate = CONFIG.COLORS[Math.floor(Math.random() * CONFIG.COLORS.length)];
    const alpha = randomBetween(0.3, 0.85);
    return {
      x:      Math.random() * canvas.width,
      y:      Math.random() * canvas.height,
      r:      randomBetween(CONFIG.MIN_RADIUS, CONFIG.MAX_RADIUS),
      vx:     randomBetween(-1, 1) * CONFIG.MAX_SPEED,
      vy:     randomBetween(-1, 1) * CONFIG.MAX_SPEED * 0.6,
      color:  colorTemplate.replace('{a}', alpha.toFixed(2)),
      pulse:  Math.random() * Math.PI * 2, // phase offset for twinkle
    };
  }

  function initParticles() {
    particles = Array.from({ length: CONFIG.COUNT }, createParticle);
  }

  function drawParticle(p, time) {
    // Twinkle: oscillate radius slightly
    const twinkle = 1 + 0.3 * Math.sin(time * 0.001 + p.pulse);
    ctx.beginPath();
    ctx.arc(p.x, p.y, p.r * twinkle, 0, Math.PI * 2);
    ctx.fillStyle = p.color;
    ctx.fill();
  }

  function connectParticles() {
    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x;
        const dy = particles[i].y - particles[j].y;
        const dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < CONFIG.CONNECT_DIST) {
          const opacity = CONFIG.CONNECT_OPACITY * (1 - dist / CONFIG.CONNECT_DIST);
          ctx.beginPath();
          ctx.moveTo(particles[i].x, particles[i].y);
          ctx.lineTo(particles[j].x, particles[j].y);
          ctx.strokeStyle = `rgba(99, 102, 241, ${opacity.toFixed(3)})`;
          ctx.lineWidth = 0.5;
          ctx.stroke();
        }
      }
    }
  }

  function update(p) {
    p.x += p.vx;
    p.y += p.vy;

    // Wrap around edges
    if (p.x < -10) p.x = canvas.width + 10;
    if (p.x > canvas.width + 10) p.x = -10;
    if (p.y < -10) p.y = canvas.height + 10;
    if (p.y > canvas.height + 10) p.y = -10;
  }

  function animate(time) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Subtle radial gradient overlay
    const gradient = ctx.createRadialGradient(
      canvas.width * 0.25,
      canvas.height * 0.4,
      0,
      canvas.width * 0.25,
      canvas.height * 0.4,
      canvas.width * 0.7
    );
    gradient.addColorStop(0, 'rgba(63, 66, 150, 0.08)');
    gradient.addColorStop(1, 'transparent');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    connectParticles();
    particles.forEach((p) => {
      update(p);
      drawParticle(p, time);
    });

    animId = requestAnimationFrame(animate);
  }

  // Handle window resize
  const resizeObserver = new ResizeObserver(() => {
    resize();
    initParticles();
  });
  resizeObserver.observe(document.body);

  resize();
  initParticles();
  animId = requestAnimationFrame(animate);

  // Pause when tab is hidden (save CPU)
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
      cancelAnimationFrame(animId);
    } else {
      animId = requestAnimationFrame(animate);
    }
  });
}

/* ─────────────────────────────────────────────
   SECTION 7 — SHOW / HIDE PASSWORD
───────────────────────────────────────────── */

function initPasswordToggle() {
  const toggleBtn  = document.getElementById('togglePassword');
  const passwordEl = document.getElementById('password');

  if (!toggleBtn || !passwordEl) return;

  let isVisible = false;

  toggleBtn.addEventListener('click', () => {
    isVisible = !isVisible;

    passwordEl.type = isVisible ? 'text' : 'password';

    const icon = toggleBtn.querySelector('i');
    if (icon) {
      icon.classList.toggle('fa-eye',      !isVisible);
      icon.classList.toggle('fa-eye-slash', isVisible);
    }

    toggleBtn.setAttribute('aria-label', isVisible ? 'Hide password' : 'Show password');

    // Return focus to password field for accessibility
    passwordEl.focus();
  });
}

/* ─────────────────────────────────────────────
   SECTION 8 — INLINE VALIDATION (on blur)
───────────────────────────────────────────── */

function initInlineValidation(emailField, passwordField) {
  const emailInput    = document.getElementById('email');
  const passwordInput = document.getElementById('password');

  emailInput?.addEventListener('blur', () => {
    const val = emailInput.value;
    if (val.trim()) emailField.validate(val, Validators.email);
  });

  emailInput?.addEventListener('input', () => {
    if (emailField.fieldEl?.classList.contains('is-error')) {
      const val = emailInput.value;
      if (!Validators.email(val)) emailField.setValid();
    }
  });

  passwordInput?.addEventListener('blur', () => {
    const val = passwordInput.value;
    if (val.trim()) passwordField.validate(val, Validators.password);
  });

  passwordInput?.addEventListener('input', () => {
    if (passwordField.fieldEl?.classList.contains('is-error')) {
      const val = passwordInput.value;
      if (!Validators.password(val)) passwordField.setValid();
    }
  });
}

/* ─────────────────────────────────────────────
   SECTION 9 — LOGIN FORM CONTROLLER
───────────────────────────────────────────── */

/**
 * Orchestrates the complete login flow.
 * Reads form values → validates → calls API → handles result.
 */
function initLoginForm() {
  const form          = document.getElementById('loginForm');
  const emailInput    = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const rememberInput = document.getElementById('rememberMe');

  if (!form) return;

  // Field controllers
  const emailField    = new FieldController('fieldEmail',    'emailError');
  const passwordField = new FieldController('fieldPassword', 'passwordError');

  // Wire up inline validation
  initInlineValidation(emailField, passwordField);

  // Guard: prevent double-submission
  let isSubmitting = false;

  /**
   * Validates both fields and returns whether the form is valid.
   * @returns {boolean}
   */
  function validateForm() {
    const emailOk    = emailField.validate(emailInput.value,    Validators.email);
    const passwordOk = passwordField.validate(passwordInput.value, Validators.password);

    // Focus first invalid field for accessibility
    if (!emailOk) {
      emailInput.focus();
    } else if (!passwordOk) {
      passwordInput.focus();
    }

    return emailOk && passwordOk;
  }

  /**
   * Normalises an ApiError into a user-friendly toast.
   * @param {import('./api.js').ApiError} err
   */
  function handleApiError(err) {
    const { status, message } = err;
    let title   = 'Sign In Failed';
    let msg     = message || 'An unexpected error occurred. Please try again.';

    if (status === 0) {
      title = 'Network Error';
      msg   = 'Unable to reach the server. Please check your internet connection.';
    } else if (status === 408) {
      title = 'Request Timed Out';
      msg   = 'The server took too long to respond. Please try again.';
    } else if (status === 401 || status === 403) {
      title = 'Access Denied';
      msg   = message || 'Invalid credentials. Please check your email and password.';
    } else if (status === 429) {
      title = 'Too Many Attempts';
      msg   = 'Your account has been temporarily locked. Please wait a few minutes.';
    } else if (status >= 500) {
      title = 'Server Error';
      msg   = 'Our servers are experiencing an issue. Please try again shortly.';
    }

    showToast({ type: 'error', title, message: msg });
  }

  /**
   * The main submit handler.
   * @param {Event} e
   */
  async function handleSubmit(e) {
    e.preventDefault();

    if (isSubmitting) return; // Prevent double-click
    if (!validateForm()) return;

    isSubmitting = true;
    ButtonState.setLoading();

    try {
      const credentials = {
        email:    emailInput.value.trim().toLowerCase(),
        password: passwordInput.value,
        remember: rememberInput?.checked || false,
      };

      // Small minimum delay so the loading state is always perceptible
      const [result] = await Promise.all([
        window.MyStoreAPI.loginSuperAdmin(credentials),
        sleep(800),
      ]);

      // Success path
      showToast({
        type:     'success',
        title:    'Authentication Successful',
        message:  `Welcome back, ${credentials.email}. Redirecting to dashboard…`,
        duration: 2500,
      });

      // Redirect after a short pause (lets the toast be seen)
      await sleep(1200);
     window.location.href=result.redirectUrl;

    } catch (err) {
      console.error('[MyStore] Login error:', err);
      handleApiError(err);
      isSubmitting = false;
      ButtonState.setIdle();

      // Re-focus email field so the user can correct credentials
      emailInput.focus();
    }
  }

  form.addEventListener('submit', handleSubmit);

  // Keyboard Enter support — submit from any field
  form.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSubmit(e);
    }
  });
}

/* ─────────────────────────────────────────────
   SECTION 10 — MISC INITIALISERS
───────────────────────────────────────────── */

/** Injects the current year into the copyright notice. */
function initCopyrightYear() {
  const el = document.getElementById('copyrightYear');
  if (el) el.textContent = new Date().getFullYear();
}

/**
 * If the user is already authenticated, redirect straight to the dashboard.
 * Avoids showing the login screen unnecessarily.
 */

/** Applies a CSS class to the <body> after JS loads (enables JS-only styles). */
function markJsReady() {
  document.body.classList.add('js-ready');
}

/* ─────────────────────────────────────────────
   SECTION 11 — BOOT
───────────────────────────────────────────── */

/**
 * Application entry point.
 * Called once the DOM is fully parsed.
 */
function boot() {
  markJsReady();
//  checkExistingSession();
  initCopyrightYear();
  ButtonState.init();
  initBackgroundCanvas();
  initPasswordToggle();
  initLoginForm();

  console.info(
    '%c MyStore Platform — Super Admin Console ',
    'background: #6366F1; color: #fff; font-weight: 600; padding: 4px 8px; border-radius: 4px;'
  );
}

// Boot once the DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', boot);
} else {
  boot();
}
