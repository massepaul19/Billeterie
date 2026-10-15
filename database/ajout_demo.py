#!/usr/bin/env python3
"""Complète la base de démo : logos et photos, voyage à 5 places, 3 réservations en attente."""
import os
import sqlite3
import struct
import zlib
from datetime import datetime, timedelta

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DB = os.path.join(RACINE, "backend", "data", "billetterie.db")
UP = os.path.join(RACINE, "backend", "uploads")
MAINTENANT = datetime.now().replace(microsecond=0)
COULEURS = [(11, 93, 59), (30, 111, 217), (192, 57, 43), (142, 68, 173), (230, 126, 34),
            (22, 160, 133), (44, 62, 80), (211, 84, 0), (39, 174, 96), (41, 128, 185),
            (176, 58, 110), (127, 140, 141), (52, 73, 94)]


def fmt(d):
    return d.strftime("%Y-%m-%d %H:%M:%S.000")


def png(chemin, rgb, n=128):
    os.makedirs(os.path.dirname(chemin), exist_ok=True)
    lignes = bytearray()
    for y in range(n):
        lignes.append(0)
        for x in range(n):
            centre = 36 <= x < 92 and 36 <= y < 92
            lignes += bytes((255, 255, 255) if centre else rgb)

    def bloc(t, d):
        return struct.pack(">I", len(d)) + t + d + struct.pack(">I", zlib.crc32(t + d) & 0xFFFFFFFF)

    with open(chemin, "wb") as f:
        f.write(b"\x89PNG\r\n\x1a\n" + bloc(b"IHDR", struct.pack(">IIBBBBB", n, n, 8, 2, 0, 0, 0))
                + bloc(b"IDAT", zlib.compress(bytes(lignes))) + bloc(b"IEND", b""))


con = sqlite3.connect(DB)
con.execute("PRAGMA foreign_keys = ON")
cur = con.cursor()
AG = 2  # Équateur Voyages

# 1. véhicule de test à 5 places + un voyage dessus (test de concurrence)
if not cur.execute("SELECT 1 FROM vehicule WHERE agence_id=? AND immatriculation='TEST-5PLACES'", (AG,)).fetchone():
    cur.execute("INSERT INTO vehicule (agence_id, immatriculation, type, nb_places, clim, wifi, image) "
                "VALUES (?, 'TEST-5PLACES', 'SIMPLE', 5, 1, 0, NULL)", (AG,))
    vid = cur.lastrowid
    trajet = cur.execute("SELECT id FROM trajet WHERE agence_id=? ORDER BY id LIMIT 1", (AG,)).fetchone()[0]
    depart = (MAINTENANT + timedelta(days=3)).replace(hour=18, minute=0, second=0)
    cur.execute("INSERT INTO voyage (agence_id, trajet_id, vehicule_id, modele_id, date_heure_depart, prix, "
                "places_restantes, statut) VALUES (?,?,?,NULL,?,4000,5,'PROGRAMME')",
                (AG, trajet, vid, fmt(depart)))
    print("Voyage à 5 places créé : id %d, départ %s" % (cur.lastrowid, fmt(depart)))
else:
    print("Voyage à 5 places : déjà là")
test_vid = cur.execute("SELECT id FROM vehicule WHERE agence_id=? AND immatriculation='TEST-5PLACES'", (AG,)).fetchone()[0]

# 2. client avec 3 réservations en attente (limite anti-abus : 3 par téléphone et par IP)
if not cur.execute("SELECT 1 FROM client WHERE telephone='699999999'").fetchone():
    cur.execute("INSERT INTO client (nom, prenom, telephone, email) VALUES ('Test', 'Anti-abus', '699999999', NULL)")
    cid = cur.lastrowid
    voyages = cur.execute(
        "SELECT id, prix FROM voyage WHERE agence_id=? AND statut='PROGRAMME' AND places_restantes > 3 "
        "AND vehicule_id <> ? AND date_heure_depart > ? ORDER BY date_heure_depart LIMIT 3",
        (AG, test_vid, fmt(MAINTENANT + timedelta(hours=3)))).fetchall()
    for v, prix in voyages:
        cur.execute("INSERT INTO reservation (voyage_id, client_id, montant, commission, statut, expire_a, "
                    "cree_le, ip_client) VALUES (?,?,?,0,'EN_ATTENTE',?,?,'192.168.99.99')",
                    (v, cid, prix, fmt(MAINTENANT + timedelta(days=1)), fmt(MAINTENANT)))
        cur.execute("UPDATE voyage SET places_restantes = places_restantes - 1 WHERE id = ?", (v,))
    print("Client 699999999 : %d réservations en attente" % len(voyages))
else:
    print("Client 699999999 : déjà là")

# 3. logos et photos (carrés de couleur) ; la base ne garde que le chemin relatif
n = m = 0
for (i,) in cur.execute("SELECT id FROM agence WHERE logo IS NULL").fetchall():
    rel = "agences/agence-%d.png" % i
    png(os.path.join(UP, rel), COULEURS[i % len(COULEURS)])
    cur.execute("UPDATE agence SET logo = ? WHERE id = ?", (rel, i))
    n += 1
for (i,) in cur.execute("SELECT id FROM vehicule WHERE image IS NULL").fetchall():
    rel = "vehicules/vehicule-%d.png" % i
    png(os.path.join(UP, rel), COULEURS[(i * 3) % len(COULEURS)])
    cur.execute("UPDATE vehicule SET image = ? WHERE id = ?", (rel, i))
    m += 1
con.commit()
print("Logos créés : %d, photos créées : %d (dans backend/uploads/)" % (n, m))
print("Clés étrangères invalides : %d" % len(con.execute("PRAGMA foreign_key_check").fetchall()))
con.close()
