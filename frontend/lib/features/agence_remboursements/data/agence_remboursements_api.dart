import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceRemboursementsApi {
  final ApiClient client;
  AgenceRemboursementsApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/agence/remboursements')).data);

  /// statut : TRAITE ou REFUSE.
  Future<void> changerStatut(int id, String statut) async {
    await client.dio.patch('/api/agence/remboursements/$id', data: {'statut': statut});
  }
}
