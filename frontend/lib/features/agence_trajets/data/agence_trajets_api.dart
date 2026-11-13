import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceTrajetsApi {
  final ApiClient client;
  AgenceTrajetsApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/agence/trajets')).data);
  Future<List<Map<String, dynamic>>> gares() async =>
      versListe((await client.dio.get('/api/agence/gares')).data);
  Future<void> creer(Map<String, dynamic> b) async {
    await client.dio.post('/api/agence/trajets', data: b);
  }

  Future<void> modifier(int id, Map<String, dynamic> b) async {
    await client.dio.put('/api/agence/trajets/$id', data: b);
  }

  Future<void> supprimer(int id) async {
    await client.dio.delete('/api/agence/trajets/$id');
  }
}
