class ApiConfig {
  /// Lancer avec : flutter run --dart-define=API_URL=http://10.0.2.2:6012 (émulateur Android).
  /// Production : https://billets.servicesztf.com
  static const String baseUrl =
      String.fromEnvironment('API_URL', defaultValue: 'http://localhost:6012');
}
