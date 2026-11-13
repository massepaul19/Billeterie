enteteSite('billet');
const params = new URLSearchParams(location.search);

function recents() {
  const l = JSON.parse(localStorage.getItem('mesBillets') || '[]');
  $('#recents').innerHTML = l.length ? 'Vos billets : ' + l.map(c => `<a href="?code=${encodeURIComponent(c)}" class="badge">${esc(c)}</a>`).join(' ') : '';
}

async function afficher(code) {
  const zone = $('#billet');
  zone.innerHTML = '<div class="attente">Chargement…</div>';
  try {
    const b = await Api.get('/api/billets/' + encodeURIComponent(code.trim().toUpperCase()));
    const valide = b.statut === 'VALIDE';
    const v = b.voyage;
    const liste = JSON.parse(localStorage.getItem('mesBillets') || '[]');
    if (!liste.includes(b.code)) { liste.unshift(b.code); localStorage.setItem('mesBillets', JSON.stringify(liste.slice(0, 20))); recents(); }
    zone.innerHTML = `<div class="panneau centre">
      <div id="qr"></div>
      <div class="code" style="margin-top:8px">${esc(b.code)}</div>
      <div style="margin:8px 0"><span class="badge ${valide ? '' : 'ko'}">${esc(b.statut)}</span></div>
      <div style="text-align:left;max-width:420px;margin:auto">
        ${b.client ? `<div class="ligne"><span>Passager</span><span>${esc((b.client.prenom || '') + ' ' + (b.client.nom || ''))}</span></div>` : ''}
        ${v ? `<div class="ligne"><span>Trajet</span><span>${esc(v.depart.ville)} → ${esc(v.arrivee.ville)}</span></div>
        <div class="ligne"><span>Départ</span><span>${esc(v.depart.gare)}</span></div>
        <div class="ligne"><span>Date</span><span>${esc(dateHeure(v.dateHeureDepart))}</span></div>
        <div class="ligne"><span>Agence</span><span>${esc(v.agence && v.agence.nom)}</span></div>` : ''}
      </div>
      <div style="display:flex;gap:10px;justify-content:center;flex-wrap:wrap;margin-top:14px">
        ${valide ? `<a class="btn" href="/api/billets/${encodeURIComponent(b.code)}/pdf" target="_blank" rel="noopener">Télécharger le PDF</a>
        <button class="btn danger" id="annuler">Annuler le billet</button>` : ''}
      </div><div id="infoAnnul"></div></div>`;
    if (window.QRCode) new QRCode($('#qr'), { text: b.code, width: 200, height: 200 });
    const bt = $('#annuler');
    if (bt) bt.addEventListener('click', async () => {
      if (!confirm('Annuler ce billet ? Possible jusqu\'à 2 h avant le départ ; 5 % du prix sont retenus.')) return;
      try {
        const r = await Api.post('/api/billets/' + encodeURIComponent(b.code) + '/annulation');
        toast('Billet annulé' + (r && r.remboursement ? ' · remboursement : ' + fcfa(r.remboursement.montant) : ''));
        afficher(b.code);
      } catch (e) { $('#infoAnnul').innerHTML = `<div class="msg ko">${esc(e.message)}</div>`; }
    });
  } catch (e) { zone.innerHTML = `<p class="erreur">${esc(e.message)}</p>`; }
}

$('#saisie').addEventListener('submit', () => { const c = $('#code').value.trim(); if (c) afficher(c); });
recents();
if (params.get('code')) { $('#code').value = params.get('code'); afficher(params.get('code')); }
