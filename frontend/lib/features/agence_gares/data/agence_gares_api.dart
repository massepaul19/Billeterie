import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceGaresApi {
  final ApiClient client;
  AgenceGaresApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/agence/gares')).data);
  Future<List<Map<String, dynamic>>> villes() async =>
      versListe((await client.dio.get('/api/villes')).data);
  Future<void> creer(Map<String, dynamic> b) async {
    await client.dio.post('/api/agence/gares', data: b);
  }

  Future<void> modifier(int id, Map<String, dynamic> b) async {
    await client.dio.put('/api/agence/gares/$id', data: b);
  }

  Future<void> supprimer(int id) async {
    await client.dio.delete('/api/agence/gares/$id');
  }
}
