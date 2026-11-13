import 'package:dio/dio.dart';

/// Message lisible à partir d'une erreur de l'API ({ code, message }).
String messageErreur(Object error) {
  if (error is DioException) {
    final data = error.response?.data;
    if (data is Map && data['message'] != null) return data['message'].toString();
    if (error.response == null) {
      return 'Impossible de contacter le serveur. Vérifiez votre connexion.';
    }
    return 'Erreur serveur (${error.response?.statusCode}).';
  }
  return error.toString().replaceFirst('Exception: ', '');
}

/// Code d'erreur de l'API (ex. PLACES_INDISPONIBLES), ou null.
String? codeErreur(Object error) {
  if (error is DioException && error.response?.data is Map) {
    return (error.response!.data as Map)['code']?.toString();
  }
  return null;
}
