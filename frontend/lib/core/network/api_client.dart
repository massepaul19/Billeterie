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
    dio.interceptors.add(InterceptorsWrapper(onRequest: (options, handler) async {
      final token = await _stockage.read(key: 'jwt');
      if (token != null) options.headers['Authorization'] = 'Bearer $token';
      handler.next(options);
    }));
  }

  Future<void> sauverToken(String token) => _stockage.write(key: 'jwt', value: token);
  Future<void> effacerToken() => _stockage.delete(key: 'jwt');

  // TODO (Front C) : transformer les erreurs { code, message } de l'API en exceptions
  //                  lisibles (voir docs/API.md, section Erreurs).
}
