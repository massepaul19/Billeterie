#!/usr/bin/env python3
"""
Billetterie - insère les données de démonstration dans backend/data/billetterie.db

A placer dans  <projet>/database/gen_demo_data.py  puis, depuis la racine du projet :
    python3 database/gen_demo_data.py            -> insère (ne fait rien si des agences existent déjà)
    python3 database/gen_demo_data.py --reset    -> vide les tables puis réinsère (dates rafraîchies)

Mot de passe de TOUS les comptes de démonstration : demo1234
    super admin : 699000000
    admin agence n°1 à n°13 : 677000001 ... 677000013   (la n°13 est une agence désactivée)
"""
import os
import random
import sqlite3
import sys
from datetime import datetime, timedelta

RACINE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DB = os.path.join(RACINE, "backend", "data", "billetterie.db")
RESET = "--reset" in sys.argv
MDP_HASH = "$2a$10$G8ITb9W9WxYSgU8C4WJ87et4hzJYalsft14NfoWnLyguJU7yAbdBW"  # BCrypt de "demo1234"
TAUX_COMMISSION = 0.007

random.seed(42)
MAINTENANT = datetime.now().replace(microsecond=0)


def fmt(d):
    """Format texte attendu par sqlite-jdbc (date_class=TEXT)."""
    return d.strftime("%Y-%m-%d %H:%M:%S.000")


# ------------------------------------------------------------------ villes
COORD = {
    "Yaoundé": (3.848, 11.502), "Douala": (4.051, 9.768), "Bafoussam": (5.477, 10.418),
    "Bamenda": (5.959, 10.146), "Garoua": (9.301, 13.393), "Maroua": (10.591, 14.316),
    "Ngaoundéré": (7.322, 13.584), "Bertoua": (4.577, 13.684), "Ebolowa": (2.900, 11.150),
    "Kribi": (2.940, 9.910), "Limbé": (4.024, 9.214), "Buea": (4.156, 9.243),
    "Edéa": (3.800, 10.133), "Kumba": (4.634, 9.446), "Dschang": (5.445, 10.053),
}
QUARTIERS = {
    "Yaoundé": ["Mvan", "Biyem-Assi", "Mokolo", "Nsam"], "Douala": ["Bonabéri", "Akwa", "Ndokotti", "Yassa"],
    "Bafoussam": ["Tamdja", "Marché A"], "Bamenda": ["Mile 4", "Commercial Avenue"],
    "Garoua": ["Roumdé Adjia", "Foulbéré"], "Maroua": ["Domayo", "Doualaré"],
    "Ngaoundéré": ["Dang", "Baladji"], "Bertoua": ["Nkolbikon", "Mokolo"],
}
# durée en minutes (trajet dans les deux sens)
DUREE = {
    ("Yaoundé", "Douala"): 300, ("Yaoundé", "Bafoussam"): 300, ("Yaoundé", "Bertoua"): 360,
    ("Yaoundé", "Ebolowa"): 150, ("Yaoundé", "Kribi"): 240, ("Yaoundé", "Ngaoundéré"): 660,
    ("Yaoundé", "Edéa"): 180, ("Yaoundé", "Bamenda"): 480, ("Douala", "Bafoussam"): 240,
    ("Douala", "Limbé"): 90, ("Douala", "Buea"): 120, ("Douala", "Kribi"): 180,
    ("Douala", "Edéa"): 90, ("Douala", "Kumba"): 180, ("Douala", "Bamenda"): 360,
    ("Douala", "Ebolowa"): 330, ("Douala", "Bertoua"): 660, ("Douala", "Dschang"): 270,
    ("Bafoussam", "Bamenda"): 120, ("Bafoussam", "Dschang"): 60, ("Bamenda", "Kumba"): 210,
    ("Buea", "Limbé"): 45, ("Buea", "Kumba"): 120, ("Kribi", "Edéa"): 120, ("Kribi", "Ebolowa"): 150,
    ("Garoua", "Maroua"): 240, ("Garoua", "Ngaoundéré"): 420, ("Maroua", "Ngaoundéré"): 600,
    ("Ngaoundéré", "Bertoua"): 600,
}


def duree(a, b):
    return DUREE.get((a, b)) or DUREE.get((b, a))


# ------------------------------------------------------------------ agences
# (nom, slogan, ville, villes desservies, code région, actif)
AGENCES = [
    ("Savanna Express", "Le Grand Nord, en confort", "Garoua", ["Garoua", "Maroua", "Ngaoundéré", "Bertoua", "Yaoundé"], "NO", 1),
    ("Équateur Voyages", "Voyagez l'esprit tranquille", "Yaoundé", ["Yaoundé", "Douala", "Bafoussam", "Ebolowa", "Kribi"], "CE", 1),
    ("Littoral Coaches", "Le littoral à portée de main", "Douala", ["Douala", "Yaoundé", "Limbé", "Buea", "Kribi", "Edéa"], "LT", 1),
    ("Kondengui Transport", "Ponctuel, sûr, abordable", "Yaoundé", ["Yaoundé", "Douala", "Bertoua", "Ebolowa"], "CE", 1),
    ("Mont Cameroun Lines", "Du pied du mont à toutes les villes", "Buea", ["Buea", "Douala", "Limbé", "Kumba", "Bamenda"], "SW", 1),
    ("Sanaga Express", "Cap sur la Sanaga", "Edéa", ["Edéa", "Douala", "Yaoundé", "Kribi"], "LT", 1),
    ("Waza Voyages", "Le Nord vous attend", "Maroua", ["Maroua", "Garoua", "Ngaoundéré"], "EN", 1),
    ("Noun Transport", "Les Grassfields en première classe", "Bafoussam", ["Bafoussam", "Douala", "Yaoundé", "Dschang", "Bamenda"], "OU", 1),
    ("Grassfields Coaches", "Confort sur les hauts plateaux", "Bamenda", ["Bamenda", "Bafoussam", "Douala", "Yaoundé", "Kumba"], "NW", 1),
    ("Atlantique Bus", "La côte en toute sérénité", "Kribi", ["Kribi", "Douala", "Yaoundé", "Edéa"], "SU", 1),
    ("Adamaoua Express", "Au rythme du plateau", "Ngaoundéré", ["Ngaoundéré", "Garoua", "Bertoua", "Yaoundé"], "AD", 1),
    ("Forêt Sud Voyages", "Le Sud à votre rythme", "Ebolowa", ["Ebolowa", "Yaoundé", "Douala", "Kribi"], "SU", 1),
    ("Ancienne Route Transport", "Agence désactivée (test RG16)", "Yaoundé", ["Yaoundé", "Douala"], "CE", 0),
]

PRENOMS = ["Jean", "Marie", "Paul", "Grâce", "Pierre", "Estelle", "Samuel", "Aïcha", "Armand", "Brenda",
           "Cyrille", "Dorothée", "Éric", "Fatima", "Gilbert", "Hortense", "Ibrahim", "Joëlle", "Kevin", "Linda"]
NOMS = ["Mballa", "Nkomo", "Fotso", "Tchoumi", "Abega", "Ngono", "Kamga", "Essomba", "Mvondo", "Ndzana",
        "Tagne", "Owona", "Bello", "Atangana", "Njoya", "Ekani", "Mbarga", "Sali", "Ngassa", "Tchatchou"]


def arrondi500(x):
    return max(1000, int(round(x / 500.0)) * 500)


def main():
    os.makedirs(os.path.dirname(DB), exist_ok=True)
    con = sqlite3.connect(DB)
    con.execute("PRAGMA foreign_keys = ON")
    con.execute("PRAGMA journal_mode = WAL")
    for f in ("schema.sql", "seed.sql"):
        with open(os.path.join(RACINE, "database", f), encoding="utf-8") as fh:
            con.executescript(fh.read())

    if RESET:
        for t in ["rappel", "remboursement", "paiement", "billet", "reservation", "client", "voyage",
                  "modele_voyage", "trajet", "vehicule", "gare", "compte_admin", "releve_commission", "agence"]:
            con.execute("DELETE FROM %s" % t)
            con.execute("DELETE FROM sqlite_sequence WHERE name = ?", (t,))
        con.commit()
    elif con.execute("SELECT COUNT(*) FROM agence").fetchone()[0] > 0:
        print("Des agences existent déjà : rien inséré (utiliser --reset pour repartir de zéro).")
        return

    villes = {n: i for i, n in con.execute("SELECT id, nom FROM ville")}
    cur = con.cursor()

    # ---- super administrateur
    cur.execute("INSERT INTO compte_admin (role, agence_id, telephone, email, mot_de_passe_hash, actif) "
                "VALUES ('SUPER_ADMIN', NULL, '699000000', 'superadmin@billetterie.test', ?, 1)", (MDP_HASH,))

    agences = []  # dicts
    for i, (nom, slogan, ville, desservies, region, actif) in enumerate(AGENCES, start=1):
        lat, lon = COORD[ville]
        cur.execute(
            "INSERT INTO agence (nom, logo, slogan, ville_id, latitude, longitude, numero_mobile_money, "
            "devise, delais_rappel_heures, actif) VALUES (?,?,?,?,?,?,?, 'XAF', '24,2', ?)",
            (nom, None, slogan, villes[ville], lat + random.uniform(-0.01, 0.01),
             lon + random.uniform(-0.01, 0.01), "6%d%07d" % (random.choice([5, 7, 9]), random.randint(0, 9999999)), actif))
        aid = cur.lastrowid
        cur.execute("INSERT INTO compte_admin (role, agence_id, telephone, email, mot_de_passe_hash, actif) "
                    "VALUES ('ADMIN_AGENCE', ?, ?, ?, ?, 1)",
                    (aid, "677%06d" % i, "admin%d@billetterie.test" % i, MDP_HASH))
        court = nom.split()[0]

        # gares : une par ville desservie
        gares = {}
        for v in desservies:
            glat, glon = COORD[v]
            q = random.choice(QUARTIERS.get(v, ["Centre-ville"]))
            cur.execute("INSERT INTO gare (agence_id, ville_id, nom, quartier, adresse, latitude, longitude) "
                        "VALUES (?,?,?,?,?,?,?)",
                        (aid, villes[v], "Gare %s %s" % (court, v), q, "%s, %s" % (q, v),
                         glat + random.uniform(-0.015, 0.015), glon + random.uniform(-0.015, 0.015)))
            gares[v] = cur.lastrowid

        # véhicules : 2 simples + 1 VIP
        vehicules = []
        for type_, places, clim, wifi in [("SIMPLE", 70, random.choice([0, 1]), 0), ("SIMPLE", 50, 1, 0), ("VIP", 30, 1, 1)]:
            immat = "%s %d%s" % (region, random.randint(100, 999), "".join(random.choices("ABCDEFGHJKLMNPRSTUVXZ", k=2)))
            cur.execute("INSERT INTO vehicule (agence_id, immatriculation, type, nb_places, clim, wifi, image) "
                        "VALUES (?,?,?,?,?,?,NULL)", (aid, immat, type_, places, clim, wifi))
            vehicules.append(dict(id=cur.lastrowid, type=type_, places=places))

        # trajets : toutes les paires desservies (deux sens) dont la durée est connue
        trajets = []
        for a in desservies:
            for b in desservies:
                d = duree(a, b)
                if a != b and d:
                    cur.execute("INSERT INTO trajet (agence_id, gare_depart_id, gare_arrivee_id, duree_estimee_min) "
                                "VALUES (?,?,?,?)", (aid, gares[a], gares[b], d))
                    trajets.append(dict(id=cur.lastrowid, duree=d))
        facteur = random.uniform(0.9, 1.15)
        agences.append(dict(id=aid, nom=nom, actif=actif, vehicules=vehicules, trajets=trajets, facteur=facteur))

        # modèles récurrents (2 premiers trajets)
        for t in trajets[:2]:
            v = vehicules[0]
            cur.execute("INSERT INTO modele_voyage (agence_id, trajet_id, vehicule_id, prix, jours_semaine, heure, "
                        "date_debut, date_fin, actif) VALUES (?,?,?,?,?,?,?,?,1)",
                        (aid, t["id"], v["id"], arrondi500(t["duree"] * 15 * facteur), "LUN,MER,VEN", "07:00",
                         fmt(MAINTENANT.replace(hour=0, minute=0, second=0)),
                         fmt(MAINTENANT.replace(hour=0, minute=0, second=0) + timedelta(days=60))))

    # ---- voyages : de J-2 à J+20
    heures = [(5, 30), (7, 0), (9, 0), (13, 0), (15, 30), (20, 30)]
    voyages = []  # (id, agence_id, prix, depart, statut, places_restantes)
    for ag in agences:
        for t in ag["trajets"]:
            creneaux = random.sample(heures, 1 if not ag["actif"] else random.choice([1, 2]))
            for jour in range(-2, 21):
                for (h, m) in creneaux:
                    if random.random() < 0.08:
                        continue  # pas de départ ce jour-là
                    depart = (MAINTENANT + timedelta(days=jour)).replace(hour=h, minute=m, second=0)
                    veh = random.choice(ag["vehicules"])
                    prix = t["duree"] * 15 * ag["facteur"] * (1.4 if veh["type"] == "VIP" else 1.0)
                    prix = arrondi500(prix)
                    if depart < MAINTENANT:
                        statut, places = "PARTI", random.randint(0, veh["places"] // 2)
                    else:
                        r = random.random()
                        if r < 0.03:
                            statut, places = "ANNULE", veh["places"]
                        elif r < 0.10:
                            statut, places = "COMPLET", 0
                        else:
                            statut, places = "PROGRAMME", veh["places"] - random.randint(0, int(veh["places"] * 0.8))
                    cur.execute("INSERT INTO voyage (agence_id, trajet_id, vehicule_id, modele_id, date_heure_depart, "
                                "prix, places_restantes, statut) VALUES (?,?,?,NULL,?,?,?,?)",
                                (ag["id"], t["id"], veh["id"], fmt(depart), prix, places, statut))
                    voyages.append(dict(id=cur.lastrowid, agence=ag["id"], prix=prix, depart=depart,
                                        statut=statut, places=places))

    # ---- réservations d'exemple
    def nouveau_client():
        p, n = random.choice(PRENOMS), random.choice(NOMS)
        email = "%s.%s%d@mail.test" % (p.lower().replace("é", "e").replace("ï", "i").replace("ë", "e"), n.lower(), random.randint(1, 99)) if random.random() < 0.7 else None
        cur.execute("INSERT INTO client (nom, prenom, telephone, email) VALUES (?,?,?,?)",
                    (n, p, "6%d%07d" % (random.choice([5, 6, 7, 8, 9]), random.randint(0, 9999999)), email))
        return cur.lastrowid

    def code():
        while True:
            c = "".join(random.choices("0123456789ABCDEF", k=16))
            if not cur.execute("SELECT 1 FROM billet WHERE code_unique = ?", (c,)).fetchone():
                return c

    actives = {a["id"] for a in agences if a["actif"]}
    futurs = [v for v in voyages if v["depart"] > MAINTENANT + timedelta(hours=3) and v["statut"] == "PROGRAMME"
              and v["places"] > 2 and v["agence"] in actives]
    passes = [v for v in voyages if v["depart"] < MAINTENANT and v["agence"] in actives]
    commission_mois = {}  # agence_id -> (total, commission)

    def reserver(v, statut, billet_statut=None):
        montant = v["prix"]
        commission = int(round(montant * TAUX_COMMISSION)) if statut in ("PAYEE", "ANNULEE") else 0
        cree = MAINTENANT - timedelta(hours=random.randint(1, 60), minutes=random.randint(0, 59))
        if statut == "EN_ATTENTE":
            cree = MAINTENANT - timedelta(minutes=2)
        cid = nouveau_client()
        cur.execute("INSERT INTO reservation (voyage_id, client_id, montant, commission, statut, expire_a, cree_le, ip_client) "
                    "VALUES (?,?,?,?,?,?,?,?)",
                    (v["id"], cid, montant, commission, statut, fmt(cree + timedelta(minutes=5)), fmt(cree),
                     "192.168.%d.%d" % (random.randint(0, 5), random.randint(2, 250))))
        rid = cur.lastrowid
        if statut in ("PAYEE", "ANNULEE"):
            cur.execute("INSERT INTO paiement (reservation_id, fournisseur, reference, montant, statut, cree_le) "
                        "VALUES (?, 'SIMULE', ?, ?, 'REUSSI', ?)",
                        (rid, "SIM-%08x" % random.getrandbits(32), montant, fmt(cree + timedelta(minutes=1))))
            cur.execute("INSERT INTO billet (reservation_id, code_unique, statut, valide_jusqu_a, reutilise) "
                        "VALUES (?,?,?,?,0)", (rid, code(), billet_statut, fmt(v["depart"])))
            bid = cur.lastrowid
            if statut == "PAYEE":
                t, c = commission_mois.get(v["agence"], (0, 0))
                commission_mois[v["agence"]] = (t + montant, c + commission)
            if billet_statut == "VALIDE":
                for delta in (24, 2):
                    envoyer = v["depart"] - timedelta(hours=delta)
                    cur.execute("INSERT INTO rappel (billet_id, envoyer_a, canal, envoye) VALUES (?,?, 'EMAIL', ?)",
                                (bid, fmt(envoyer), 1 if envoyer < MAINTENANT else 0))
        return rid

    def prendre_place(v):
        v["places"] -= 1
        cur.execute("UPDATE voyage SET places_restantes = ?, statut = ? WHERE id = ?",
                    (v["places"], "COMPLET" if v["places"] == 0 else "PROGRAMME", v["id"]))

    for v in random.sample(futurs, min(60, len(futurs))):
        r = random.random()
        if r < 0.65:
            reserver(v, "PAYEE", "VALIDE"); prendre_place(v)
        elif r < 0.75:
            reserver(v, "EN_ATTENTE"); prendre_place(v)
        elif r < 0.85:
            reserver(v, "EXPIREE")
        else:  # annulée par le client : billet ANNULE + remboursement (montant moins 5 %)
            rid = reserver(v, "ANNULEE", "ANNULE")
            cur.execute("INSERT INTO remboursement (reservation_id, montant, motif, statut, cree_le) "
                        "VALUES (?,?, 'Annulation client', ?, ?)",
                        (rid, int(v["prix"] * 0.95), random.choice(["DEMANDE", "TRAITE"]), fmt(MAINTENANT)))
    for v in random.sample(passes, min(20, len(passes))):
        reserver(v, "PAYEE", random.choice(["UTILISE", "UTILISE", "UTILISE", "EXPIRE"]))

    # ---- relevés de commission : mois précédent (fictif, déjà facturé) et mois courant (calculé)
    mois_courant = MAINTENANT.strftime("%Y-%m")
    mois_prec = (MAINTENANT.replace(day=1) - timedelta(days=1)).strftime("%Y-%m")
    for ag in agences:
        if not ag["actif"]:
            continue
        total = random.randint(600, 4000) * 500
        cur.execute("INSERT INTO releve_commission (agence_id, mois, total_ventes, commission, statut_facturation) "
                    "VALUES (?,?,?,?, 'FACTURE')", (ag["id"], mois_prec, total, int(round(total * TAUX_COMMISSION))))
        t, c = commission_mois.get(ag["id"], (0, 0))
        cur.execute("INSERT INTO releve_commission (agence_id, mois, total_ventes, commission, statut_facturation) "
                    "VALUES (?,?,?,?, 'A_FACTURER')", (ag["id"], mois_courant, t, c))

    con.commit()
    problemes = con.execute("PRAGMA foreign_key_check").fetchall()
    print("Base : %s" % DB)
    for t in ["agence", "compte_admin", "gare", "vehicule", "trajet", "modele_voyage", "voyage", "client",
              "reservation", "paiement", "billet", "remboursement", "rappel", "releve_commission"]:
        print("  %-18s %d" % (t, con.execute("SELECT COUNT(*) FROM %s" % t).fetchone()[0]))
    print("Clés étrangères invalides : %d" % len(problemes))
    print()
    print("Comptes (mot de passe : demo1234)")
    print("  super admin     : 699000000")
    print("  admin agence 1  : 677000001  ...  agence 13 (désactivée) : 677000013")
    con.close()


if __name__ == "__main__":
    main()
