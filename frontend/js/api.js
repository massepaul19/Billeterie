/* Client API partagé : jeton, erreurs {code,message}, aides d'affichage. Même origine que le backend. */
class ApiError extends Error {
  constructor(statut, code, message) { super(message); this.statut = statut; this.code = code; }
}

const Api = {
  base: '',
  session() {
    return { token: localStorage.getItem('jwt'), role: localStorage.getItem('role'), agenceId: localStorage.getItem('agenceId') };
  },
  ouvrirSession(r) {
    localStorage.setItem('jwt', r.token);
    localStorage.setItem('role', r.role);
    if (r.agenceId != null) localStorage.setItem('agenceId', r.agenceId); else localStorage.removeItem('agenceId');
  },
  deconnexion() { ['jwt', 'role', 'agenceId'].forEach(k => localStorage.removeItem(k)); },

  async req(methode, chemin, corps) {
    const entetes = {};
    const jeton = localStorage.getItem('jwt');
    if (jeton) entetes.Authorization = 'Bearer ' + jeton;
    let body;
    if (corps instanceof FormData) body = corps;
    else if (corps !== undefined) { entetes['Content-Type'] = 'application/json'; body = JSON.stringify(corps); }
    let res;
    try { res = await fetch(this.base + chemin, { method: methode, headers: entetes, body }); }
    catch (e) { throw new ApiError(0, 'RESEAU', 'Impossible de contacter le serveur. Vérifiez votre connexion.'); }
    if (res.status === 401 && jeton && /^\/api\/(agence|admin)\//.test(chemin)) {
      this.deconnexion();
      location.href = '/connexion.html?expire=1';
      throw new ApiError(401, 'NON_AUTHENTIFIE', 'Session expirée.');
    }
    if (res.status === 204) return null;
    const json = (res.headers.get('content-type') || '').includes('json') ? await res.json().catch(() => null) : null;
    if (!res.ok) throw new ApiError(res.status, json && json.code, (json && json.message) || 'Erreur (' + res.status + ').');
    return json;
  },
  get(c) { return this.req('GET', c); },
  post(c, b) { return this.req('POST', c, b === undefined ? {} : b); },
  put(c, b) { return this.req('PUT', c, b); },
  patch(c, b) { return this.req('PATCH', c, b); },
  del(c) { return this.req('DELETE', c); },
  qs(obj) {
    const p = new URLSearchParams();
    Object.entries(obj).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '' && v !== false) p.set(k, v); });
    const s = p.toString();
    return s ? '?' + s : '';
  },
};

/* ---------- aides d'affichage ---------- */
const $ = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => [...r.querySelectorAll(s)];
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const fcfa = n => new Intl.NumberFormat('fr-FR').format(n).replace(/ | /g, ' ') + ' FCFA';
/* Le serveur envoie l'heure locale du Cameroun (UTC+1, sans décalage dans la chaîne). */
const parseDate = iso => new Date(/[zZ]|[+-]\d\d:?\d\d$/.test(iso) ? iso : iso + '+01:00');
const dateHeure = iso => new Intl.DateTimeFormat('fr-FR', { timeZone: 'Africa/Douala', weekday: 'short', day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' }).format(parseDate(iso));
const duree = min => min == null ? '' : Math.floor(min / 60) + ' h ' + String(min % 60).padStart(2, '0');

function toast(msg) {
  let t = $('#toast');
  if (!t) { t = document.createElement('div'); t.id = 'toast'; document.body.appendChild(t); }
  t.textContent = msg; t.classList.add('on');
  clearTimeout(toast._t); toast._t = setTimeout(() => t.classList.remove('on'), 3500);
}

function enteteSite(actif) {
  const s = Api.session();
  const lien = (href, txt, cle) => `<a href="${href}" class="${actif === cle ? 'actif' : ''}">${txt}</a>`;
  const espace = s.token ? (s.role === 'SUPER_ADMIN' ? '/admin/' : '/agence/') : '/connexion.html';
  document.body.insertAdjacentHTML('afterbegin',
    `<header class="entete"><div class="in"><a class="logo" href="/">🚌 Billetterie</a><nav>
      ${lien('/', 'Voyages', 'voyages')}${lien('/#agences', 'Agences', 'agences')}${lien('/billet.html', 'Mon billet', 'billet')}
      ${lien(espace, s.token ? 'Mon espace' : 'Connexion', 'espace')}</nav></div></header>`);
}

function debounce(f, ms) { let t; return (...a) => { clearTimeout(t); t = setTimeout(() => f(...a), ms); }; }
