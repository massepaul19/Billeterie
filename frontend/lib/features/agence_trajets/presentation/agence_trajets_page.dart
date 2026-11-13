import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';
import '../../../core/ui/crud.dart';
import '../data/agence_trajets_api.dart';

class AgenceTrajetsPage extends StatelessWidget {
  const AgenceTrajetsPage({super.key});

  Future<void> _form(BuildContext c, AgenceTrajetsApi api, VoidCallback rech,
      [Map<String, dynamic>? t]) async {
    List<Map<String, dynamic>> gares;
    try {
      gares = await api.gares();
    } catch (_) {
      if (c.mounted) toast(c, 'Impossible de charger les gares.');
      return;
    }
    if (!c.mounted) return;
    if (gares.length < 2) {
      toast(c, 'Créez d\'abord au moins deux gares.');
      return;
    }
    final opt = {for (final g in gares) g['id'].toString(): '${g['nom']} (${g['villeNom']})'};
    final r = await ouvrirFormulaire(c, t == null ? 'Nouveau trajet' : 'Modifier le trajet', [
      Champ('gareDepartId', 'Gare de départ', initial: '${t?['gareDepartId'] ?? ''}', options: opt),
      Champ('gareArriveeId', 'Gare d\'arrivée', initial: '${t?['gareArriveeId'] ?? ''}', options: opt),
      Champ('dureeEstimeeMin', 'Durée estimée (minutes)',
          initial: '${t?['dureeEstimeeMin'] ?? ''}', clavier: TextInputType.number),
    ]);
    if (r == null || !c.mounted) return;
    final body = {
      'gareDepartId': ent(r['gareDepartId']),
      'gareArriveeId': ent(r['gareArriveeId']),
      'dureeEstimeeMin': ent(r['dureeEstimeeMin']),
    };
    await executer(c, () => t == null ? api.creer(body) : api.modifier(t['id'] as int, body),
        apres: rech, succes: 'Trajet enregistré.');
  }

  @override
  Widget build(BuildContext context) {
    final api = AgenceTrajetsApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Trajets',
      charger: api.lister,
      vide: 'Aucun trajet. Ajoutez-en un avec le bouton +.',
      ajouter: (c, rech) => _form(c, api, rech),
      item: (c, t, rech) {
        final min = (t['dureeEstimeeMin'] as num?)?.toInt() ?? 0;
        return Card(
          child: ListTile(
            leading: const Icon(Icons.route),
            title: Text('${t['gareDepartNom']} → ${t['gareArriveeNom']}'),
            subtitle: Text('${min ~/ 60} h ${min % 60} min'),
            onTap: () => _form(c, api, rech, t),
            trailing: IconButton(
              icon: const Icon(Icons.delete_outline),
              onPressed: () async {
                if (await confirmer(c, 'Supprimer ce trajet ?') && c.mounted) {
                  await executer(c, () => api.supprimer(t['id'] as int),
                      apres: rech, succes: 'Trajet supprimé.');
                }
              },
            ),
          ),
        );
      },
    );
  }
}
