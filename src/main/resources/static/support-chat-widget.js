/**
 * Support Chat Widget
 * -----------------------------------------------------------------------
 * Ek self-contained floating chat bubble jo Spring AI backed customer
 * support chatbot (POST /api/chat/support) se baat karta hai.
 *
 * USAGE: Kisi bhi HTML/Thymeleaf page ke </body> se pehle ye line daal do:
 *
 *   <script src="/support-chat-widget.js"></script>
 *
 * Bas itna hi — koi aur setup nahi chahiye. Widget khud apna CSS aur
 * HTML inject kar leta hai, isliye kisi existing page ke saath conflict
 * nahi karega.
 * -----------------------------------------------------------------------
 */
(function () {
    'use strict';

    const API_URL = '/api/chat/support';
    const STORAGE_KEY = 'support_chat_conversation_id';

    // ---------- styles (matches the project's dark indigo theme, with fallbacks) ----------
    const css = `
        #sc-widget * { box-sizing: border-box; }
        #sc-widget {
            --sc-primary: var(--color-primary, #6366F1);
            --sc-primary-hover: var(--color-primary-hover, #4F46E5);
            --sc-bg: var(--color-surface, #0F1629);
            --sc-card: var(--color-card, #111827);
            --sc-border: var(--color-card-border, rgba(99,102,241,0.18));
            --sc-text: var(--color-text-primary, #F8FAFC);
            --sc-text-muted: var(--color-text-secondary, #94A3B8);
            position: fixed;
            right: 20px;
            bottom: 20px;
            z-index: 999999;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }
        #sc-bubble {
            width: 60px; height: 60px; border-radius: 50%;
            background: var(--sc-primary);
            box-shadow: 0 8px 32px rgba(99,102,241,0.45);
            border: none; cursor: pointer;
            display: flex; align-items: center; justify-content: center;
            transition: transform .15s ease, background .15s ease;
        }
        #sc-bubble:hover { background: var(--sc-primary-hover); transform: scale(1.06); }
        #sc-bubble svg { width: 28px; height: 28px; fill: #fff; }
        #sc-panel {
            position: absolute; bottom: 76px; right: 0;
            width: 340px; max-width: calc(100vw - 32px);
            height: 460px; max-height: 70vh;
            background: var(--sc-card);
            border: 1px solid var(--sc-border);
            border-radius: 16px;
            box-shadow: 0 32px 80px rgba(0,0,0,0.55), 0 0 0 1px var(--sc-border);
            display: none;
            flex-direction: column;
            overflow: hidden;
        }
        #sc-panel.sc-open { display: flex; }
        #sc-header {
            background: linear-gradient(135deg, var(--sc-primary), var(--sc-primary-hover));
            color: #fff; padding: 14px 16px;
            display: flex; align-items: center; justify-content: space-between;
        }
        #sc-header-title { font-size: 14.5px; font-weight: 600; }
        #sc-header-sub { font-size: 11.5px; opacity: .85; margin-top: 1px; }
        #sc-close { background: none; border: none; color: #fff; cursor: pointer; font-size: 18px; line-height: 1; opacity: .85; }
        #sc-close:hover { opacity: 1; }
        #sc-messages {
            flex: 1; overflow-y: auto; padding: 14px;
            display: flex; flex-direction: column; gap: 10px;
            background: var(--sc-bg);
        }
        .sc-msg { max-width: 84%; padding: 9px 12px; border-radius: 14px; font-size: 13.5px; line-height: 1.4; white-space: pre-wrap; word-wrap: break-word; }
        .sc-msg-bot { align-self: flex-start; background: var(--sc-card); color: var(--sc-text); border: 1px solid var(--sc-border); border-bottom-left-radius: 4px; }
        .sc-msg-user { align-self: flex-end; background: var(--sc-primary); color: #fff; border-bottom-right-radius: 4px; }
        .sc-msg-error { align-self: flex-start; background: rgba(248,113,113,0.12); color: #F87171; border: 1px solid rgba(248,113,113,0.3); border-bottom-left-radius: 4px; }
        #sc-typing { align-self: flex-start; display: flex; gap: 4px; padding: 10px 12px; }
        #sc-typing span { width: 6px; height: 6px; border-radius: 50%; background: var(--sc-text-muted); animation: sc-bounce 1.2s infinite ease-in-out; }
        #sc-typing span:nth-child(2) { animation-delay: .15s; }
        #sc-typing span:nth-child(3) { animation-delay: .3s; }
        @keyframes sc-bounce { 0%, 60%, 100% { transform: translateY(0); opacity: .5; } 30% { transform: translateY(-4px); opacity: 1; } }
        #sc-input-row { display: flex; gap: 8px; padding: 10px; border-top: 1px solid var(--sc-border); background: var(--sc-card); }
        #sc-input {
            flex: 1; resize: none; border: 1px solid var(--sc-border); border-radius: 10px;
            background: rgba(255,255,255,0.04); color: var(--sc-text);
            padding: 9px 11px; font-size: 13.5px; font-family: inherit; outline: none;
            max-height: 80px;
        }
        #sc-input:focus { border-color: var(--sc-primary); }
        #sc-send {
            width: 38px; height: 38px; border-radius: 10px; border: none; cursor: pointer;
            background: var(--sc-primary); color: #fff; display: flex; align-items: center; justify-content: center;
            flex-shrink: 0; transition: background .15s ease;
        }
        #sc-send:hover { background: var(--sc-primary-hover); }
        #sc-send:disabled { opacity: .5; cursor: default; }
        #sc-send svg { width: 16px; height: 16px; fill: #fff; }
        @media (max-width: 420px) {
            #sc-panel { width: calc(100vw - 32px); }
        }
    `;

    const styleTag = document.createElement('style');
    styleTag.textContent = css;
    document.head.appendChild(styleTag);

    // ---------- markup ----------
    const root = document.createElement('div');
    root.id = 'sc-widget';
    root.innerHTML = `
        <div id="sc-panel">
            <div id="sc-header">
                <div>
                    <div id="sc-header-title">Support Chat</div>
                    <div id="sc-header-sub">We usually reply in a few seconds</div>
                </div>
                <button id="sc-close" aria-label="Close chat">&times;</button>
            </div>
            <div id="sc-messages"></div>
            <div id="sc-input-row">
                <textarea id="sc-input" rows="1" placeholder="Type your message..."></textarea>
                <button id="sc-send" aria-label="Send">
                    <svg viewBox="0 0 24 24"><path d="M2 21l21-9L2 3v7l15 2-15 2z"/></svg>
                </button>
            </div>
        </div>
        <button id="sc-bubble" aria-label="Open support chat">
            <svg viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.03 2 11c0 2.42 1.09 4.61 2.86 6.24-.1 1.09-.51 2.6-1.7 4.36 1.98-.28 3.54-1.09 4.7-1.93A11.6 11.6 0 0012 20c5.52 0 10-4.03 10-9s-4.48-9-10-9z"/></svg>
        </button>
    `;
    document.body.appendChild(root);

    const panel = root.querySelector('#sc-panel');
    const bubble = root.querySelector('#sc-bubble');
    const closeBtn = root.querySelector('#sc-close');
    const messagesEl = root.querySelector('#sc-messages');
    const input = root.querySelector('#sc-input');
    const sendBtn = root.querySelector('#sc-send');

    let conversationId = localStorage.getItem(STORAGE_KEY) || null;
    let sending = false;

    function addMessage(text, type) {
        const div = document.createElement('div');
        div.className = 'sc-msg ' + (type === 'user' ? 'sc-msg-user' : type === 'error' ? 'sc-msg-error' : 'sc-msg-bot');
        div.textContent = text;
        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
    }

    function showTyping() {
        const div = document.createElement('div');
        div.id = 'sc-typing';
        div.innerHTML = '<span></span><span></span><span></span>';
        messagesEl.appendChild(div);
        messagesEl.scrollTop = messagesEl.scrollHeight;
        return div;
    }

    function openPanel() {
        panel.classList.add('sc-open');
        if (messagesEl.children.length === 0) {
            addMessage("Hi! I'm the support assistant. Ask me about product availability, pricing or your order status.", 'bot');
        }
        input.focus();
    }

    bubble.addEventListener('click', () => {
        panel.classList.contains('sc-open') ? (panel.classList.remove('sc-open')) : openPanel();
    });
    closeBtn.addEventListener('click', () => panel.classList.remove('sc-open'));

    async function sendMessage() {
        const text = input.value.trim();
        if (!text || sending) return;

        sending = true;
        sendBtn.disabled = true;
        addMessage(text, 'user');
        input.value = '';
        input.style.height = 'auto';
        const typingEl = showTyping();

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify({ message: text, conversationId: conversationId })
            });

            const data = await response.json();
            typingEl.remove();

            if (!response.ok || !data.success) {
                addMessage(data.message || 'Something went wrong. Please try again.', 'error');
                return;
            }

            conversationId = data.data.conversationId;
            localStorage.setItem(STORAGE_KEY, conversationId);
            addMessage(data.data.reply, 'bot');

        } catch (err) {
            typingEl.remove();
            addMessage('Could not reach support chat. Please check your connection and try again.', 'error');
        } finally {
            sending = false;
            sendBtn.disabled = false;
        }
    }

    sendBtn.addEventListener('click', sendMessage);
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
    input.addEventListener('input', () => {
        input.style.height = 'auto';
        input.style.height = Math.min(input.scrollHeight, 80) + 'px';
    });
})();
