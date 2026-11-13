import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';
import '../../../core/ui/crud.dart';
import '../data/agence_gares_api.dart';

class AgenceGaresPage extends StatelessWidget {
  const AgenceGaresPage({super.key});

  Future<void> _form(BuildContext c, AgenceGaresApi api, VoidCallback rech,
      [Map<String, dynamic>? g]) async {
    List<Map<String, dynamic>> villes;
    try {
      villes = await api.villes();
    } catch (_) {
      if (c.mounted) toast(c, 'Impossible de charger les villes.');
      return;
    }
    if (!c.mounted) return;
    final r = await ouvrirFormulaire(c, g == null ? 'Nouvelle gare' : 'Modifier la gare', [
      Champ('villeId', 'Ville',
          initial: g?['villeId']?.toString() ?? '',
          options: {for (final v in villes) v['id'].toString(): v['nom'].toString()}),
      Champ('nom', 'Nom', initial: g?['nom'] ?? ''),
      Champ('quartier', 'Quartier', initial: g?['quartier'] ?? '', requis: false),
      Champ('adresse', 'Adresse', initial: g?['adresse'] ?? '', requis: false),
      Champ('latitude', 'Latitude', initial: g?['latitude']?.toString() ?? '', requis: false,
          clavier: const TextInputType.numberWithOptions(decimal: true, signed: true)),
      Champ('longitude', 'Longitude', initial: g?['longitude']?.toString() ?? '', requis: false,
          clavier: const TextInputType.numberWithOptions(decimal: true, signed: true)),
    ]);
    if (r == null || !c.mounted) return;
    final body = {
      'villeId': ent(r['villeId']),
      'nom': r['nom'],
      'quartier': videNull(r['quartier']),
      'adresse': videNull(r['adresse']),
      'latitude': dbl(r['latitude']),
      'longitude': dbl(r['longitude']),
    };
    await executer(c, () => g == null ? api.creer(body) : api.modifier(g['id'] as int, body),
        apres: rech, succes: 'Gare enregistrée.');
  }

  @override
  Widget build(BuildContext context) {
    final api = AgenceGaresApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Gares',
      charger: api.lister,
      vide: 'Aucune gare. Ajoutez-en une avec le bouton +.',
      ajouter: (c, rech) => _form(c, api, rech),
      item: (c, g, rech) => Card(
        child: ListTile(
          leading: const Icon(Icons.location_city),
          title: Text('${g['nom']}'),
          subtitle: Text([g['villeNom'], g['quartier'], g['adresse']]
              .where((e) => e != null && '$e'.isNotEmpty)
              .join(' · ')),
          onTap: () => _form(c, api, rech, g),
          trailing: IconButton(
            icon: const Icon(Icons.delete_outline),
            onPressed: () async {
              if (await confirmer(c, 'Supprimer la gare « ${g['nom']} » ?') && c.mounted) {
                await executer(c, () => api.supprimer(g['id'] as int),
                    apres: rech, succes: 'Gare supprimée.');
              }
            },
          ),
        ),
      ),
    );
  }
}
