enteteSite('voyages');
const idVoyage = new URLSearchParams(location.search).get('voyage');
const CLE = 'resa';
let resa = null, minuteur = null, voyage = null;

const montrer = (el, oui) => { el.style.display = oui ? '' : 'none'; };
const erreur = (el, m) => { el.innerHTML = m ? `<div class="msg ko">${esc(m)}</div>` : ''; };

function resume(v) {
  return `<div class="trajet" style="font-weight:700;font-size:1.1rem">${esc(v.depart.ville)} → ${esc(v.arrivee.ville)}</div>
  <div class="ligne"><span>Agence</span><span>${esc(v.agence.nom)}</span></div>
  <div class="ligne"><span>Départ</span><span>${esc(v.depart.gare)}</span></div>
  <div class="ligne"><span>Arrivée</span><span>${esc(v.arrivee.gare)}</span></div>
  <div class="ligne"><span>Date</span><span>${esc(dateHeure(v.dateHeureDepart))}</span></div>
  <div class="ligne"><span>Prix</span><b>${fcfa(v.prix)}</b></div>`;
}

function lancerChrono() {
  clearInterval(minuteur);
  const tic = () => {
    const reste = Math.max(0, parseDate(resa.expireA) - Date.now());
    const s = Math.floor(reste / 1000);
    $('#chrono').textContent = String(Math.floor(s / 60)).padStart(2, '0') + ':' + String(s % 60).padStart(2, '0');
    if (reste === 0) {
      clearInterval(minuteur);
      $('#chrono').classList.add('fin'); $('#chrono').textContent = 'Expirée';
      $('#btnPayer').disabled = true; montrer($('#retour'), true);
      sessionStorage.removeItem(CLE);
    }
  };
  tic(); minuteur = setInterval(tic, 1000);
}

function afficherPaiement() {
  montrer($('#etapeContact'), false);
  montrer($('#etapePaiement'), true);
  $('#montant').textContent = fcfa(resa.montant);
  lancerChrono();
}

$('#etapeContact').addEventListener('submit', async e => {
  e.preventDefault();
  const tel = $('#tel').value.replace(/\D/g, '');
  const d = { nom: $('#nom').value.trim(), prenom: $('#prenom').value.trim(), telephone: $('#tel').value.trim(), email: $('#email').value.trim() };
  if (!d.nom || !d.prenom || tel.length < 9 || !d.email.includes('@')) { erreur($('#erreurContact'), 'Vérifiez vos informations (nom, prénom, téléphone, e-mail).'); return; }
  erreur($('#erreurContact'), ''); $('#btnReserver').disabled = true;
  try {
    const r = await Api.post('/api/reservations', { voyageId: Number(idVoyage), ...d });
    resa = { id: r.id, montant: r.montant, expireA: r.expireA, voyageId: idVoyage, tel: d.telephone };
    sessionStorage.setItem(CLE, JSON.stringify(resa));
    $('#telPaiement').value = d.telephone;
    afficherPaiement();
  } catch (err) { erreur($('#erreurContact'), err.message); $('#btnReserver').disabled = false; }
});

$('#etapePaiement').addEventListener('submit', async e => {
  e.preventDefault();
  if (($('#telPaiement').value.replace(/\D/g, '')).length < 9) { erreur($('#erreurPaiement'), 'Numéro Mobile Money invalide.'); return; }
  erreur($('#erreurPaiement'), ''); $('#btnPayer').disabled = true;
  try {
    const r = await Api.post('/api/paiements', { reservationId: resa.id, fournisseur: 'SIMULE', operateur: $('#operateur').value, telephonePaiement: $('#telPaiement').value.trim() });
    const code = r.codeBillet || r.code || r.billetCode;
    if (r.statut === 'REUSSI' && code) {
      clearInterval(minuteur); sessionStorage.removeItem(CLE);
      const liste = JSON.parse(localStorage.getItem('mesBillets') || '[]');
      if (!liste.includes(code)) { liste.unshift(code); localStorage.setItem('mesBillets', JSON.stringify(liste.slice(0, 20))); }
      location.href = '/billet.html?code=' + encodeURIComponent(code);
    } else {
      sessionStorage.removeItem(CLE);
      erreur($('#erreurPaiement'), 'Le paiement a échoué. Votre réservation est annulée : recommencez.');
      montrer($('#retour'), true);
    }
  } catch (err) {
    erreur($('#erreurPaiement'), err.message); // ex. RESERVATION_EXPIREE
    if (err.code === 'RESERVATION_EXPIREE') { montrer($('#retour'), true); sessionStorage.removeItem(CLE); }
    else $('#btnPayer').disabled = false;
  }
});

(async function init() {
  if (!idVoyage) { $('#voyage').innerHTML = '<p class="erreur">Aucun voyage sélectionné. <a href="/">Voir les voyages</a></p>'; return; }
  try {
    voyage = await Api.get('/api/voyages/' + encodeURIComponent(idVoyage));
    $('#voyage').innerHTML = resume(voyage);
  } catch (e) { $('#voyage').innerHTML = `<p class="erreur">${esc(e.message)} <a href="/">Retour</a></p>`; return; }
  // reprise d'une réservation en cours (rechargement de la page)
  try {
    const sauve = JSON.parse(sessionStorage.getItem(CLE) || 'null');
    if (sauve && sauve.voyageId === idVoyage && parseDate(sauve.expireA) > Date.now()) {
      resa = sauve; $('#telPaiement').value = sauve.tel || ''; afficherPaiement(); return;
    }
  } catch (e) { /* ignore */ }
  montrer($('#etapeContact'), true);
})();
