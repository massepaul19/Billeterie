import '../../../core/network/api_client.dart';

class PaiementResultat {
  final bool reussi;
  final String? codeBillet;
  PaiementResultat(this.reussi, this.codeBillet);
}

class PaiementApi {
  final ApiClient client;
  PaiementApi(this.client);

  /// Paiement simulé. Vérifiez les noms de champs de la réponse avec un curl
  /// (lecture tolérante : codeBillet / code / billetCode).
  Future<PaiementResultat> payer({
    required int reservationId,
    required String operateur,
    required String telephonePaiement,
  }) async {
    final r = await client.dio.post('/api/paiements', data: {
      'reservationId': reservationId,
      'fournisseur': 'SIMULE',
      'operateur': operateur,
      'telephonePaiement': telephonePaiement,
    });
    final m = Map<String, dynamic>.from(r.data);
    final code = (m['codeBillet'] ?? m['code'] ?? m['billetCode'])?.toString();
    return PaiementResultat(m['statut'] == 'REUSSI' && code != null, code);
  }
}
