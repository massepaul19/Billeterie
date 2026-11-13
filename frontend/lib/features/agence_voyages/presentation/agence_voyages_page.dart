import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_erreur.dart';
import '../../../core/network/json.dart';
import '../../../core/ui/crud.dart';
import '../../../core/utils/format.dart';
import '../data/agence_voyages_api.dart';

class AgenceVoyagesPage extends StatelessWidget {
  const AgenceVoyagesPage({super.key});

  Future<void> _creer(BuildContext c, AgenceVoyagesApi api, VoidCallback rech) async {
    List<Map<String, dynamic>> trajets, vehicules;
    try {
      trajets = await api.trajets();
      vehicules = await api.vehicules();
    } catch (e) {
      if (c.mounted) toast(c, messageErreur(e));
      return;
    }
    if (!c.mounted) return;
    if (trajets.isEmpty || vehicules.isEmpty) {
      toast(c, 'Créez d\'abord un trajet et un véhicule.');
      return;
    }
    final r = await ouvrirFormulaire(c, 'Nouveau voyage', [
      Champ('trajetId', 'Trajet', options: {
        for (final t in trajets) t['id'].toString(): '${t['gareDepartNom']} → ${t['gareArriveeNom']}'
      }),
      Champ('vehiculeId', 'Véhicule', options: {
        for (final v in vehicules)
          v['id'].toString(): '${v['immatriculation']} (${v['type']}, ${v['nbPlaces']} pl.)'
      }),
      const Champ('dateHeureDepart', 'Départ (date et heure)', dateHeure: true),
      const Champ('prix', 'Prix (FCFA)', clavier: TextInputType.number),
    ]);
    if (r == null || !c.mounted) return;
    await executer(
        c,
        () => api.creer({
              'trajetId': ent(r['trajetId']),
              'vehiculeId': ent(r['vehiculeId']),
              'dateHeureDepart': r['dateHeureDepart'],
              'prix': ent(r['prix']),
            }),
        apres: rech,
        succes: 'Voyage créé.');
  }

  Future<void> _prix(BuildContext c, AgenceVoyagesApi api, VoidCallback rech, Map<String, dynamic> v) async {
    final r = await ouvrirFormulaire(c, 'Nouveau prix', [
      Champ('prix', 'Prix (FCFA)', initial: '${v['prix']}', clavier: TextInputType.number),
    ]);
    final prix = ent(r?['prix']);
    if (prix == null || !c.mounted) return;
    await executer(c, () => api.changerPrix(v['id'] as int, prix),
        apres: rech, succes: 'Prix modifié (les réservations existantes gardent leur prix).');
  }

  Future<void> _annuler(BuildContext c, AgenceVoyagesApi api, VoidCallback rech, Map<String, dynamic> v) async {
    if (!await confirmer(
            c, 'Annuler ce voyage ? Les billets payés seront annulés et remboursés à 100 %.') ||
        !c.mounted) {
      return;
    }
    try {
      final res = await api.annuler(v['id'] as int);
      if (!c.mounted) return;
      toast(c, 'Voyage annulé. ${res['remboursementsCrees']} remboursement(s) : '
          '${formatFcfa((res['montantTotal'] as num).toInt())}');
      rech();
    } catch (e) {
      if (c.mounted) toast(c, messageErreur(e));
    }
  }

  @override
  Widget build(BuildContext context) {
    final api = AgenceVoyagesApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Voyages',
      charger: api.lister,
      vide: 'Aucun voyage. Ajoutez-en un avec le bouton +.',
      ajouter: (c, rech) => _creer(c, api, rech),
      item: (c, v, rech) {
        final actif = v['statut'] == 'PROGRAMME' || v['statut'] == 'COMPLET';
        return Card(
          child: ListTile(
            leading: const Icon(Icons.directions_bus),
            title: Text('${v['gareDepart']} → ${v['gareArrivee']}'),
            subtitle: Text('${formatDateHeure(DateTime.parse(v['dateHeureDepart'] as String))}\n'
                '${v['immatriculation']} · ${v['placesRestantes']}/${v['nbPlaces']} places · ${v['statut']}'),
            isThreeLine: true,
            trailing: Row(mainAxisSize: MainAxisSize.min, children: [
              Text(formatFcfa((v['prix'] as num).toInt()),
                  style: const TextStyle(fontWeight: FontWeight.bold)),
              if (actif)
                PopupMenuButton<String>(
                  onSelected: (a) => a == 'prix' ? _prix(c, api, rech, v) : _annuler(c, api, rech, v),
                  itemBuilder: (_) => const [
                    PopupMenuItem(value: 'prix', child: Text('Modifier le prix')),
                    PopupMenuItem(value: 'annuler', child: Text('Annuler le voyage')),
                  ],
                ),
            ]),
          ),
        );
      },
    );
  }
}
