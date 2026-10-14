-- Schéma SQLite - Billetterie multi-agences (v0.1)
-- Dates : texte ISO-8601 heure locale Africa/Douala. Montants : entiers en FCFA.
-- Booléens : 0/1. Les contraintes CHECK reprennent les enums Java.

CREATE TABLE IF NOT EXISTS ville (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nom TEXT NOT NULL UNIQUE,
  pays TEXT NOT NULL DEFAULT 'Cameroun'
);

CREATE TABLE IF NOT EXISTS agence (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nom TEXT NOT NULL,
  logo TEXT,
  slogan TEXT,
  ville_id INTEGER NOT NULL REFERENCES ville(id),
  latitude REAL,
  longitude REAL,
  numero_mobile_money TEXT,
  devise TEXT NOT NULL DEFAULT 'XAF',
  delais_rappel_heures TEXT NOT NULL DEFAULT '24,2',
  actif INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS compte_admin (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  role TEXT NOT NULL CHECK (role IN ('SUPER_ADMIN','ADMIN_AGENCE')),
  agence_id INTEGER REFERENCES agence(id) ON DELETE CASCADE,
  telephone TEXT NOT NULL UNIQUE,
  email TEXT,
  mot_de_passe_hash TEXT NOT NULL,
  actif INTEGER NOT NULL DEFAULT 1,
  CHECK ((role = 'SUPER_ADMIN' AND agence_id IS NULL) OR (role = 'ADMIN_AGENCE' AND agence_id IS NOT NULL))
);

CREATE TABLE IF NOT EXISTS gare (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id) ON DELETE CASCADE,
  ville_id INTEGER NOT NULL REFERENCES ville(id),
  nom TEXT NOT NULL,
  quartier TEXT,
  adresse TEXT,
  latitude REAL,
  longitude REAL
);

CREATE TABLE IF NOT EXISTS vehicule (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id) ON DELETE CASCADE,
  immatriculation TEXT NOT NULL,
  type TEXT NOT NULL CHECK (type IN ('SIMPLE','VIP')),
  nb_places INTEGER NOT NULL CHECK (nb_places > 0),
  clim INTEGER NOT NULL DEFAULT 0,
  wifi INTEGER NOT NULL DEFAULT 0,
  image TEXT,
  UNIQUE (agence_id, immatriculation)
);

CREATE TABLE IF NOT EXISTS trajet (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id) ON DELETE CASCADE,
  gare_depart_id INTEGER NOT NULL REFERENCES gare(id),
  gare_arrivee_id INTEGER NOT NULL REFERENCES gare(id),
  duree_estimee_min INTEGER,
  CHECK (gare_depart_id <> gare_arrivee_id)
);

CREATE TABLE IF NOT EXISTS modele_voyage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id) ON DELETE CASCADE,
  trajet_id INTEGER NOT NULL REFERENCES trajet(id),
  vehicule_id INTEGER NOT NULL REFERENCES vehicule(id),
  prix INTEGER NOT NULL CHECK (prix >= 0),
  jours_semaine TEXT NOT NULL,          -- ex. 'LUN,MER,VEN'
  heure TEXT NOT NULL,                  -- ex. '07:30'
  date_debut TEXT NOT NULL,
  date_fin TEXT NOT NULL,
  actif INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS voyage (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id) ON DELETE CASCADE,
  trajet_id INTEGER NOT NULL REFERENCES trajet(id),
  vehicule_id INTEGER NOT NULL REFERENCES vehicule(id),
  modele_id INTEGER REFERENCES modele_voyage(id),
  date_heure_depart TEXT NOT NULL,
  prix INTEGER NOT NULL CHECK (prix >= 0),
  places_restantes INTEGER NOT NULL CHECK (places_restantes >= 0),
  statut TEXT NOT NULL DEFAULT 'PROGRAMME' CHECK (statut IN ('PROGRAMME','COMPLET','PARTI','ANNULE'))
);

CREATE TABLE IF NOT EXISTS client (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nom TEXT NOT NULL,
  prenom TEXT NOT NULL,
  telephone TEXT NOT NULL,
  email TEXT
);

CREATE TABLE IF NOT EXISTS reservation (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  voyage_id INTEGER NOT NULL REFERENCES voyage(id),
  client_id INTEGER NOT NULL REFERENCES client(id),
  montant INTEGER NOT NULL,
  commission INTEGER NOT NULL DEFAULT 0,
  statut TEXT NOT NULL DEFAULT 'EN_ATTENTE' CHECK (statut IN ('EN_ATTENTE','PAYEE','EXPIREE','ANNULEE')),
  expire_a TEXT,
  cree_le TEXT NOT NULL,
  ip_client TEXT
);

CREATE TABLE IF NOT EXISTS billet (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  reservation_id INTEGER NOT NULL UNIQUE REFERENCES reservation(id),
  code_unique TEXT NOT NULL UNIQUE,
  statut TEXT NOT NULL DEFAULT 'VALIDE' CHECK (statut IN ('VALIDE','UTILISE','EXPIRE','ANNULE')),
  valide_jusqu_a TEXT,
  reutilise INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS paiement (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  reservation_id INTEGER NOT NULL REFERENCES reservation(id),
  fournisseur TEXT NOT NULL CHECK (fournisseur IN ('SIMULE','CAMPAY')),
  reference TEXT,
  montant INTEGER NOT NULL,
  statut TEXT NOT NULL CHECK (statut IN ('EN_ATTENTE','REUSSI','ECHEC')),
  cree_le TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS remboursement (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  reservation_id INTEGER NOT NULL REFERENCES reservation(id),
  montant INTEGER NOT NULL,
  motif TEXT,
  statut TEXT NOT NULL DEFAULT 'DEMANDE' CHECK (statut IN ('DEMANDE','TRAITE','REFUSE')),
  cree_le TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS rappel (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  billet_id INTEGER NOT NULL REFERENCES billet(id) ON DELETE CASCADE,
  envoyer_a TEXT NOT NULL,
  canal TEXT NOT NULL DEFAULT 'EMAIL' CHECK (canal IN ('EMAIL')),
  envoye INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS releve_commission (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  agence_id INTEGER NOT NULL REFERENCES agence(id),
  mois TEXT NOT NULL,                   -- 'YYYY-MM'
  total_ventes INTEGER NOT NULL DEFAULT 0,
  commission INTEGER NOT NULL DEFAULT 0,
  statut_facturation TEXT NOT NULL DEFAULT 'A_FACTURER' CHECK (statut_facturation IN ('A_FACTURER','FACTURE','PAYE')),
  UNIQUE (agence_id, mois)
);

CREATE TABLE IF NOT EXISTS synonyme_recherche (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  mot TEXT NOT NULL UNIQUE,
  critere TEXT NOT NULL                 -- CLIM, WIFI, VIP
);

CREATE INDEX IF NOT EXISTS idx_voyage_recherche ON voyage (trajet_id, date_heure_depart, statut);
CREATE INDEX IF NOT EXISTS idx_voyage_agence ON voyage (agence_id, date_heure_depart);
CREATE INDEX IF NOT EXISTS idx_gare_ville ON gare (ville_id, latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_agence_ville ON agence (ville_id, actif);
CREATE INDEX IF NOT EXISTS idx_reservation_voyage ON reservation (voyage_id, statut);
CREATE INDEX IF NOT EXISTS idx_reservation_expiration ON reservation (statut, expire_a);
CREATE INDEX IF NOT EXISTS idx_rappel_envoi ON rappel (envoye, envoyer_a);
