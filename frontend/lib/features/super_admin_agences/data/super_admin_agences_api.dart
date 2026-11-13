import 'package:dio/dio.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/json.dart';

class SuperAdminAgencesApi {
  final ApiClient client;
  SuperAdminAgencesApi(this.client);

  Future<List<Map<String, dynamic>>> lister() async =>
      versListe((await client.dio.get('/api/admin/agences')).data);
  Future<List<Map<String, dynamic>>> villes() async =>
      versListe((await client.dio.get('/api/villes')).data);
  Future<void> creer(Map<String, dynamic> b) async {
    await client.dio.post('/api/admin/agences', data: b);
  }

  Future<void> modifier(int id, Map<String, dynamic> b) async {
    await client.dio.put('/api/admin/agences/$id', data: b);
  }

  Future<void> supprimer(int id) async {
    await client.dio.delete('/api/admin/agences/$id');
  }

  Future<void> activer(int id, bool actif) async {
    await client.dio.patch('/api/admin/agences/$id/activation', data: {'actif': actif});
  }

  Future<void> creerCompte(int id, Map<String, dynamic> b) async {
    await client.dio.post('/api/admin/agences/$id/compte', data: b);
  }

  Future<void> envoyerLogo(int id, List<int> octets, String nom) async {
    await client.dio.post('/api/admin/agences/$id/logo',
        data: FormData.fromMap({'fichier': MultipartFile.fromBytes(octets, filename: nom)}));
  }
}
