import 'package:flutter/material.dart';
import '../../../core/network/api_client.dart';
import '../../../core/ui/crud.dart';
import '../data/carte_api.dart';

/// Agences actives. Version liste ; une vraie carte (flutter_map) pourra la remplacer plus tard
/// grâce aux champs latitude / longitude déjà fournis par l'API.
class CartePage extends StatelessWidget {
  const CartePage({super.key});

  @override
  Widget build(BuildContext context) {
    final api = CarteApi(ApiClient());
    return EcranListe<Map<String, dynamic>>(
      titre: 'Nos agences',
      charger: api.agences,
      vide: 'Aucune agence active.',
      item: (c, a, rech) => Card(
        child: ListTile(
          leading: a['logo'] == null
              ? const CircleAvatar(child: Icon(Icons.business))
              : CircleAvatar(backgroundImage: NetworkImage(a['logo'] as String)),
          title: Text('${a['nom']}'),
          subtitle: Text('${a['ville'] ?? ''}${a['slogan'] == null ? '' : '\n${a['slogan']}'}'),
          isThreeLine: a['slogan'] != null,
        ),
      ),
    );
  }
}
