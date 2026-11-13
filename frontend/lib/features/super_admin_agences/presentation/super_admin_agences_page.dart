import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';
import '../../../core/ui/crud.dart';
import '../data/super_admin_agences_api.dart';

class SuperAdminAgencesPage extends StatelessWidget {
  const SuperAdminAgencesPage({super.key});

  Future<void> _form(BuildContext c, SuperAdminAgencesApi api, VoidCallback rech,
      [Map<String, dynamic>? a]) async {
    List<Map<String, dynamic>> villes;
    try {
      villes = await api.villes();
    } catch (_) {
      if (c.mounted) toast(c, 'Impossible de charger les villes.');
      return;
    }
    if (!c.mounted) return;
    const dec = TextInputType.numberWithOptions(decimal: true, signed: true);
    final r = await ouvrirFormulaire(c, a == null ? 'Nouvelle agence' : 'Modifier l\'agence', [
      Champ('nom', 'Nom', initial: a?['nom'] ?? ''),
      Champ('slogan', 'Slogan', initial: a?['slogan'] ?? '', requis: false),
      Champ('villeId', 'Ville',
          initial: '${a?['villeId'] ?? ''}',
          options: {for (final v in villes) v['id'].toString(): v['nom'].toString()}),
      Champ('latitude', 'Latitude', initial: '${a?['latitude'] ?? ''}', requis: false, clavier: dec),
      Champ('longitude', 'Longitude', initial: '${a?['longitude'] ?? ''}', requis: false, clavier: dec),
      Champ('numeroMobileMoney', 'N° Mobile Money',
          initial: a?['numeroMobileMoney'] ?? '', requis: false, clavier: TextInputType.phone),
    ]);
    if (r == null || !c.mounted) return;
    final body = {
      'nom': r['nom'],
      'slogan': videNull(r['slogan']),
      'villeId': ent(r['villeId']),
      'latitude': dbl(r['latitude']),
      'longitude': dbl(r['longitude']),
      'numeroMobileMoney': videNull(r['numeroMobileMoney']),
    };
    await executer(c, () => a == null ? api.creer(body) : api.modifier(a['id'] as int, body),
        apres: rech, succes: 'Agence enregistrée.');
  }

  Future<void> _compte(BuildContext c, SuperAdminAgencesApi api, VoidCallback rech, Map<String, dynamic> a) async {
    final r = await ouvrirFormulaire(c, 'Compte administrateur de « ${a['nom']} »', [
      const Champ('telephone', 'Téléphone', clavier: TextInputType.phone),
      const Champ('email', 'E-mail', requis: false, clavier: TextInputType.emailAddress),
      const Champ('motDePasseInitial', 'Mot de passe initial (6 caractères min.)', secret: true),
    ]);
    if (r == null || !c.mounted) return;
    await executer(
        c,
        () => api.creerCompte(a['id'] as int, {
              'telephone': r['telephone'],
              'email': videNull(r['email']),
              'motDePasseInitial': r['motDePasseInitial'],
            }),
        apres: rech,
        succes: 'Compte créé.');
  }

  Future<void> _logo(BuildContext c, SuperAdminAgencesApi api, VoidCallback rech, int id) async {
    final f = await FilePicker.platform.pickFiles(type: FileType.image, withData: true);
    final fichier = f?.files.single;
    if (fichier == null || fichier.bytes == null || !c.mounted) return;
    await executer(c, () => api.envoyerLogo(id, fichier.bytes!, fichier.name),
        apres: rech, succes: 'Logo enregistré.');
  }

  @override
  Widget build(BuildContext context) {
    final api = SuperAdminAgencesApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Agences',
      charger: api.lister,
      vide: 'Aucune agence.',
      ajouter: (c, rech) => _form(c, api, rech),
      item: (c, a, rech) => Card(
        child: ListTile(
          leading: a['logo'] == null
              ? const CircleAvatar(child: Icon(Icons.business))
              : CircleAvatar(backgroundImage: NetworkImage(a['logo'] as String)),
          title: Text('${a['nom']}'),
          subtitle: Text('${a['villeNom'] ?? ''} · compte : ${a['compteTelephone'] ?? 'aucun'}'),
          onTap: () => _form(c, api, rech, a),
          trailing: Row(mainAxisSize: MainAxisSize.min, children: [
            Switch(
              value: a['actif'] == true,
              onChanged: (v) => executer(c, () => api.activer(a['id'] as int, v), apres: rech),
            ),
            PopupMenuButton<String>(
              onSelected: (s) async {
                if (s == 'compte') {
                  await _compte(c, api, rech, a);
                } else if (s == 'logo') {
                  await _logo(c, api, rech, a['id'] as int);
                } else if (await confirmer(c, 'Supprimer « ${a['nom']} » ? (refusé si elle a des voyages)') &&
                    c.mounted) {
                  await executer(c, () => api.supprimer(a['id'] as int),
                      apres: rech, succes: 'Agence supprimée.');
                }
              },
              itemBuilder: (_) => const [
                PopupMenuItem(value: 'compte', child: Text('Créer un compte admin')),
                PopupMenuItem(value: 'logo', child: Text('Changer le logo')),
                PopupMenuItem(value: 'suppr', child: Text('Supprimer')),
              ],
            ),
          ]),
        ),
      ),
    );
  }
}
