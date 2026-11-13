import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../config/api_config.dart';

class ApiClient {
  final Dio dio;
  final FlutterSecureStorage _stockage = const FlutterSecureStorage();

  ApiClient()
      : dio = Dio(BaseOptions(
          baseUrl: ApiConfig.baseUrl,
          connectTimeout: const Duration(seconds: 10),
          receiveTimeout: const Duration(seconds: 20),
        )) {
    dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final token = await _stockage.read(key: 'jwt');

          if (token != null && token.isNotEmpty) {
            options.headers['Authorization'] = 'Bearer $token';
          }

          handler.next(options);
        },
      ),
    );
  }

  Future<void> sauverToken(String token) =>
      _stockage.write(key: 'jwt', value: token);

  Future<void> sauverSession({
    required String token,
    required String role,
    int? agenceId,
  }) async {
    await _stockage.write(key: 'jwt', value: token);
    await _stockage.write(key: 'role', value: role);

    if (agenceId != null) {
      await _stockage.write(
        key: 'agenceId',
        value: agenceId.toString(),
      );
    } else {
      await _stockage.delete(key: 'agenceId');
    }
  }

  Future<String?> lireToken() => _stockage.read(key: 'jwt');

  Future<String?> lireRole() => _stockage.read(key: 'role');

  Future<int?> lireAgenceId() async {
    final valeur = await _stockage.read(key: 'agenceId');
    return valeur == null ? null : int.tryParse(valeur);
  }

  Future<void> effacerToken() async {
    await _stockage.delete(key: 'jwt');
    await _stockage.delete(key: 'role');
    await _stockage.delete(key: 'agenceId');
  }

  Future<void> deconnexion() => effacerToken();
}
