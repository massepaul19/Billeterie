import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceVoyagesApi {
  final ApiClient client;
  AgenceVoyagesApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/agence/voyages')).data);
  Future<List<Map<String, dynamic>>> trajets() async =>
      versListe((await client.dio.get('/api/agence/trajets')).data);
  Future<List<Map<String, dynamic>>> vehicules() async =>
      versListe((await client.dio.get('/api/agence/vehicules')).data);
  Future<void> creer(Map<String, dynamic> b) async {
    await client.dio.post('/api/agence/voyages', data: b);
  }

  Future<void> changerPrix(int id, int prix) async {
    await client.dio.patch('/api/agence/voyages/$id/prix', data: {'prix': prix});
  }

  /// Retourne { remboursementsCrees, montantTotal }.
  Future<Map<String, dynamic>> annuler(int id) async =>
      Map<String, dynamic>.from((await client.dio.post('/api/agence/voyages/$id/annulation')).data);
}
