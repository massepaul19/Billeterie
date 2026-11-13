import 'package:dio/dio.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class AgenceVehiculesApi {
  final ApiClient client;
  AgenceVehiculesApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/agence/vehicules')).data);
  Future<void> creer(Map<String, dynamic> b) async {
    await client.dio.post('/api/agence/vehicules', data: b);
  }

  Future<void> modifier(int id, Map<String, dynamic> b) async {
    await client.dio.put('/api/agence/vehicules/$id', data: b);
  }

  Future<void> supprimer(int id) async {
    await client.dio.delete('/api/agence/vehicules/$id');
  }

  Future<void> envoyerImage(int id, List<int> octets, String nom) async {
    await client.dio.post('/api/agence/vehicules/$id/image',
        data: FormData.fromMap({'fichier': MultipartFile.fromBytes(octets, filename: nom)}));
  }
}
