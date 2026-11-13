enteteSite('voyages');

const etat = { lat: null, lon: null, num: 0 };
const champs = { depart: $('#depart'), destination: $('#destination'), date: $('#date'), type: $('#type'), q: $('#q'), clim: $('#clim'), wifi: $('#wifi') };

function carte(v) {
  const veh = v.vehicule || {};
  const photo = veh.image ? `<img class="photo" src="${esc(veh.image)}" alt="" loading="lazy">` : '';
  const logo = v.agence && v.agence.logo ? `<img src="${esc(v.agence.logo)}" alt="" loading="lazy">` : '';
  return `<article class="voyage">${photo}<div class="corps">
    <div class="ag">${logo}<span>${esc(v.agence && v.agence.nom)}</span></div>
    <div class="trajet">${esc(v.depart.ville)} → ${esc(v.arrivee.ville)}</div>
    <div style="color:var(--gris);font-size:.88rem">${esc(v.depart.gare)}${v.depart.quartier ? ' (' + esc(v.depart.quartier) + ')' : ''}</div>
    <div>${esc(dateHeure(v.dateHeureDepart))}${v.dureeEstimeeMin ? ' · ' + duree(v.dureeEstimeeMin) : ''}</div>
    <div><span class="badge ${veh.type === 'VIP' ? 'vip' : ''}">${esc(veh.type || '')}</span>${veh.clim ? '<span class="badge">Clim</span>' : ''}${veh.wifi ? '<span class="badge">Wifi</span>' : ''}</div>
    <div class="pied"><span class="prix">${fcfa(v.prix)}</span><a class="btn" href="/reservation.html?voyage=${encodeURIComponent(v.id)}">Réserver</a></div>
  </div></article>`;
}

async function chercher() {
  const num = ++etat.num;
  $('#resume').textContent = 'Recherche…';
  const d = champs.date.value;
  const url = '/api/voyages' + Api.qs({
    depart: champs.depart.value.trim(), destination: champs.destination.value.trim(),
    dateDebut: d, dateFin: d, type: champs.type.value, q: champs.q.value.trim(),
    clim: champs.clim.checked, wifi: champs.wifi.checked,
    latitude: etat.lat, longitude: etat.lon, taille: 50,
  });
  try {
    const r = await Api.get(url);
    if (num !== etat.num) return; // réponse périmée
    $('#resume').textContent = r.total + ' voyage' + (r.total > 1 ? 's' : '') + (etat.lat ? ' · triés par distance' : '');
    $('#resultats').innerHTML = r.contenu.length ? r.contenu.map(carte).join('') : '<p class="vide" style="grid-column:1/-1">Aucun voyage ne correspond à votre recherche.</p>';
  } catch (e) {
    if (num !== etat.num) return;
    $('#resume').textContent = '';
    $('#resultats').innerHTML = `<p class="erreur" style="grid-column:1/-1">${esc(e.message)} <button class="btn clair" onclick="chercher()">Réessayer</button></p>`;
  }
}

const chercherDiffere = debounce(chercher, 300);
Object.values(champs).forEach(c => c.addEventListener(c.type === 'text' || c.type === 'search' ? 'input' : 'change', chercherDiffere));

$('#pos').addEventListener('click', () => {
  if (!navigator.geolocation) { $('#posEtat').textContent = 'Géolocalisation indisponible.'; return; }
  $('#posEtat').textContent = 'Localisation…';
  navigator.geolocation.getCurrentPosition(p => {
    etat.lat = p.coords.latitude; etat.lon = p.coords.longitude;
    $('#posEtat').textContent = 'Position utilisée ✓';
    chercher();
  }, () => { $('#posEtat').textContent = 'Position refusée ou indisponible.'; }, { timeout: 10000 });
});

async function init() {
  chercher();
  Api.get('/api/villes').then(l => { $('#villes').innerHTML = l.map(v => `<option value="${esc(v.nom)}">`).join(''); }).catch(() => {});
  try {
    const ags = await Api.get('/api/agences');
    $('#listeAgences').innerHTML = ags.length ? ags.map(a => `<div class="agence" data-nom="${esc(a.nom)}">
      ${a.logo ? `<img src="${esc(a.logo)}" alt="" loading="lazy">` : '<div class="sans">🚌</div>'}
      <b>${esc(a.nom)}</b><small>${esc(a.ville || '')}</small>${a.slogan ? `<div><small><i>${esc(a.slogan)}</i></small></div>` : ''}</div>`).join('')
      : '<p class="vide">Aucune agence pour le moment.</p>';
    $$('.agence').forEach(el => el.addEventListener('click', () => {
      champs.q.value = el.dataset.nom; chercher(); scrollTo({ top: 0, behavior: 'smooth' });
    }));
  } catch (e) { $('#listeAgences').innerHTML = `<p class="erreur">${esc(e.message)}</p>`; }
}
init();
