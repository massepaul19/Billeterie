import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/ui/crud.dart';
import '../data/agence_vehicules_api.dart';

class AgenceVehiculesPage extends StatelessWidget {
  const AgenceVehiculesPage({super.key});

  Future<void> _form(BuildContext c, AgenceVehiculesApi api, VoidCallback rech,
      [Map<String, dynamic>? v]) async {
    final oui = {'true': 'Oui', 'false': 'Non'};
    final r = await ouvrirFormulaire(c, v == null ? 'Nouveau véhicule' : 'Modifier le véhicule', [
      Champ('immatriculation', 'Immatriculation', initial: v?['immatriculation'] ?? ''),
      Champ('type', 'Type', initial: v?['type'] ?? '', options: {'SIMPLE': 'Simple', 'VIP': 'VIP'}),
      Champ('nbPlaces', 'Nombre de places',
          initial: v?['nbPlaces']?.toString() ?? '', clavier: TextInputType.number),
      Champ('clim', 'Climatisation', initial: '${v?['clim'] ?? false}', options: oui),
      Champ('wifi', 'Wifi', initial: '${v?['wifi'] ?? false}', options: oui),
    ]);
    if (r == null || !c.mounted) return;
    final body = {
      'immatriculation': r['immatriculation'],
      'type': r['type'],
      'nbPlaces': int.tryParse(r['nbPlaces'] ?? ''),
      'clim': r['clim'] == 'true',
      'wifi': r['wifi'] == 'true',
    };
    await executer(c, () => v == null ? api.creer(body) : api.modifier(v['id'] as int, body),
        apres: rech, succes: 'Véhicule enregistré.');
  }

  Future<void> _photo(BuildContext c, AgenceVehiculesApi api, VoidCallback rech, int id) async {
    final f = await FilePicker.platform.pickFiles(type: FileType.image, withData: true);
    final fichier = f?.files.single;
    if (fichier == null || fichier.bytes == null || !c.mounted) return;
    await executer(c, () => api.envoyerImage(id, fichier.bytes!, fichier.name),
        apres: rech, succes: 'Photo enregistrée.');
  }

  @override
  Widget build(BuildContext context) {
    final api = AgenceVehiculesApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Véhicules',
      charger: api.lister,
      vide: 'Aucun véhicule. Ajoutez-en un avec le bouton +.',
      ajouter: (c, rech) => _form(c, api, rech),
      item: (c, v, rech) => Card(
        child: ListTile(
          leading: v['image'] == null
              ? const CircleAvatar(child: Icon(Icons.directions_bus))
              : CircleAvatar(backgroundImage: NetworkImage(v['image'] as String)),
          title: Text('${v['immatriculation']} · ${v['type']}'),
          subtitle: Text('${v['nbPlaces']} places'
              '${v['clim'] == true ? ' · clim' : ''}${v['wifi'] == true ? ' · wifi' : ''}'),
          onTap: () => _form(c, api, rech, v),
          trailing: Row(mainAxisSize: MainAxisSize.min, children: [
            IconButton(
                tooltip: 'Photo',
                icon: const Icon(Icons.photo_camera_outlined),
                onPressed: () => _photo(c, api, rech, v['id'] as int)),
            IconButton(
              icon: const Icon(Icons.delete_outline),
              onPressed: () async {
                if (await confirmer(c, 'Supprimer ${v['immatriculation']} ?') && c.mounted) {
                  await executer(c, () => api.supprimer(v['id'] as int),
                      apres: rech, succes: 'Véhicule supprimé.');
                }
              },
            ),
          ]),
        ),
      ),
    );
  }
}
