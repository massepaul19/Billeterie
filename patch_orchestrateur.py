import pathlib, re, shutil

f = pathlib.Path("mobile/app/src/main/kotlin/com/myeyes/agent/noyau/Orchestrateur.kt")
t = f.read_text()

# Bloc 1 : fin de lecture des non lus (direMorceaux après l'appel LireNonLus.lire)
motif1 = re.compile(
    r'(LireNonLus\.lire\(svc, paquet\)(?:(?!direMorceaux).)*?)'
    r'direMorceaux\(v, morceaux, 0, \+\+lectureId\) \{ occupe = false \}',
    re.DOTALL,
)
nouveau1 = r'''val idLecture = ++lectureId
                    val propose = res is Resultat.Dit && !t.startsWith("Tu n'as aucun")
                    direMorceaux(v, morceaux, 0, idLecture) {
                        if (propose && idLecture == lectureId) { proposerReponse(v, paquet) } else { occupe = false }
                    }'''

# Bloc 2 : choisirAppli + nouvelles fonctions
ancien2 = r'''    private fun choisirAppli(v: Voix, c: Commande.EnvoyerMessage) {
        avecAppli(v, c.paquet) { p -> envoyer(v, c, p) }
    }'''
nouveau2 = r'''    /** Ponctuation des messages dictés : signes dits à voix haute, et « ? » sur les questions courtes évidentes. */
    private fun ponctuer(texte: String): String {
        var t = texte.trim()
        t = t.replace(Regex("\\s*point d.interrogation", RegexOption.IGNORE_CASE), "?")
        t = t.replace(Regex("\\s*point d.exclamation", RegexOption.IGNORE_CASE), "!")
        t = t.replace(Regex("\\s*virgule", RegexOption.IGNORE_CASE), ",")
        val fin = t.lastOrNull()
        if (fin != null && fin !in ".?!…") {
            val court = t.split(Regex("\\s+")).size <= 10
            val bas = t.lowercase()
            val question = Regex("^(est-ce qu|qu.est-ce qu)").containsMatchIn(bas) ||
                Regex("^(comment|pourquoi|combien|où|quand|qui|quels?|quelles?)(?=\\s)").containsMatchIn(bas)
            if (court && question) t += "?"
        }
        return t
    }

    /** Après les non lus : propose de répondre (contact, puis texte), puis passe par l'envoi avec confirmation. */
    private fun proposerReponse(v: Voix, paquet: String) {
        v.parler("Veux-tu répondre à quelqu'un ?") {
            v.ecouter { rep ->
                Journal.noter("REPONSE : $rep")
                if (Regles.confirmation(rep) != true) {
                    occupe = false
                    v.parler("D'accord.")
                } else {
                    v.parler("À qui ?") {
                        v.ecouter { qui ->
                            val contact = qui?.trim()
                            if (contact.isNullOrBlank()) {
                                occupe = false
                                v.parler("Je n'ai pas compris. J'annule.")
                            } else {
                                v.parler("Quel message ?") {
                                    v.ecouter { msg ->
                                        val texte = msg?.trim()
                                        if (texte.isNullOrBlank()) {
                                            occupe = false
                                            v.parler("Je n'ai pas compris. J'annule.")
                                        } else {
                                            envoyer(v, Commande.EnvoyerMessage(contact, ponctuer(texte), paquet), paquet)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun choisirAppli(v: Voix, c: Commande.EnvoyerMessage) {
        avecAppli(v, c.paquet) { p -> envoyer(v, c.copy(texte = ponctuer(c.texte)), p) }
    }'''

n1 = len(motif1.findall(t))
n2 = t.count(ancien2)
print(f"Bloc 1 (fin de lireNonLus) : {n1} correspondance(s)")
print(f"Bloc 2 (choisirAppli)      : {n2} correspondance(s)")
if n1 != 1 or n2 != 1:
    print("\n--- Contexte pour diagnostic ---")
    for i, l in enumerate(t.splitlines(), 1):
        if "LireNonLus.lire(" in l or "fun choisirAppli(" in l or "direMorceaux(" in l:
            print(f"{i}: {l}")
    raise SystemExit("ARRÊT : rien n'a été modifié")

shutil.copy(f, str(f) + ".bak2")
t = motif1.sub(lambda m: m.group(1) + nouveau1, t, count=1)
t = t.replace(ancien2, nouveau2)
f.write_text(t)
print("OK : Orchestrateur.kt modifié, sauvegarde .bak2 créée")
