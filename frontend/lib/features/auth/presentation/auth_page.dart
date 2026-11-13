import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../../../core/network/api_client.dart';
import '../data/auth_api.dart';
import '../../agence_gares/presentation/agence_gares_page.dart';
import '../../agence_parametres/presentation/agence_parametres_page.dart';
import '../../agence_remboursements/presentation/agence_remboursements_page.dart';
import '../../agence_reservations/presentation/agence_reservations_page.dart';
import '../../agence_trajets/presentation/agence_trajets_page.dart';
import '../../agence_vehicules/presentation/agence_vehicules_page.dart';
import '../../agence_verification/presentation/agence_verification_page.dart';
import '../../agence_voyages/presentation/agence_voyages_page.dart';
import '../../commissions/presentation/commissions_page.dart';
import '../../super_admin_agences/presentation/super_admin_agences_page.dart';

class AuthPage extends StatefulWidget {
  const AuthPage({super.key});

  @override
  State<AuthPage> createState() => _AuthPageState();
}

class _AuthPageState extends State<AuthPage> {
  final _formKey = GlobalKey<FormState>();
  final _telephone = TextEditingController();
  final _motDePasse = TextEditingController();

  final _client = ApiClient();

  bool _chargement = false;
  bool _masquerMotDePasse = true;
  String? _erreur;

  @override
  void dispose() {
    _telephone.dispose();
    _motDePasse.dispose();
    super.dispose();
  }

  String _messageErreur(dynamic error) {
    if (error is DioException) {
      final data = error.response?.data;

      if (data is Map) {
        return data['message']?.toString() ??
            data['code']?.toString() ??
            'Une erreur est survenue.';
      }

      if (error.response == null) {
        return 'Impossible de contacter le serveur. Vérifiez votre connexion.';
      }

      return 'Erreur serveur (${error.response?.statusCode}).';
    }

    return error.toString().replaceFirst('Exception: ', '');
  }

  Future<void> _connexion() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() {
      _chargement = true;
      _erreur = null;
    });

    try {
      final resultat = await AuthApi(_client).connexion(
        telephone: _telephone.text.trim(),
        motDePasse: _motDePasse.text,
      );

      if (!mounted) return;

      final role = resultat['role'] as String;
      final agenceId = resultat['agenceId'] as int?;

      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (_) => EspaceAdminPage(
            client: _client,
            role: role,
            agenceId: agenceId,
          ),
        ),
      );
    } catch (e) {
      if (!mounted) return;

      setState(() {
        _erreur = _messageErreur(e);
      });
    } finally {
      if (mounted) {
        setState(() => _chargement = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final couleurs = Theme.of(context).colorScheme;

    return Scaffold(
      backgroundColor: const Color(0xFFF4F7F5),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(24),
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 440),
            child: Card(
              elevation: 3,
              color: Colors.white,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(22),
              ),
              child: Padding(
                padding: const EdgeInsets.all(32),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      CircleAvatar(
                        radius: 35,
                        backgroundColor: couleurs.primaryContainer,
                        child: Icon(
                          Icons.directions_bus_rounded,
                          size: 38,
                          color: couleurs.primary,
                        ),
                      ),
                      const SizedBox(height: 22),
                      const Text(
                        'Billetterie',
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          fontSize: 29,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        'Connectez-vous à votre espace de gestion',
                        textAlign: TextAlign.center,
                        style: TextStyle(color: Colors.grey.shade600),
                      ),
                      const SizedBox(height: 32),
                      const Text(
                        'Téléphone',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _telephone,
                        keyboardType: TextInputType.phone,
                        decoration: InputDecoration(
                          hintText: 'Ex : 699000000',
                          prefixIcon: const Icon(Icons.phone_outlined),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        validator: (value) {
                          if (value == null || value.trim().isEmpty) {
                            return 'Entrez votre numéro de téléphone.';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 20),
                      const Text(
                        'Mot de passe',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(height: 8),
                      TextFormField(
                        controller: _motDePasse,
                        obscureText: _masquerMotDePasse,
                        decoration: InputDecoration(
                          hintText: 'Votre mot de passe',
                          prefixIcon: const Icon(Icons.lock_outline),
                          suffixIcon: IconButton(
                            onPressed: () {
                              setState(() {
                                _masquerMotDePasse = !_masquerMotDePasse;
                              });
                            },
                            icon: Icon(
                              _masquerMotDePasse
                                  ? Icons.visibility_outlined
                                  : Icons.visibility_off_outlined,
                            ),
                          ),
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return 'Entrez votre mot de passe.';
                          }
                          return null;
                        },
                        onFieldSubmitted: (_) => _connexion(),
                      ),
                      if (_erreur != null) ...[
                        const SizedBox(height: 18),
                        Container(
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: Colors.red.shade50,
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: Text(
                            _erreur!,
                            style: TextStyle(color: Colors.red.shade800),
                          ),
                        ),
                      ],
                      const SizedBox(height: 28),
                      SizedBox(
                        height: 52,
                        child: FilledButton(
                          onPressed: _chargement ? null : _connexion,
                          style: FilledButton.styleFrom(
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                          child: _chargement
                              ? const SizedBox(
                                  height: 23,
                                  width: 23,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2,
                                    color: Colors.white,
                                  ),
                                )
                              : const Text(
                                  'Se connecter',
                                  style: TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

/// Espace d'accueil après authentification.
/// Les modules seront intégrés progressivement selon le rôle.
class EspaceAdminPage extends StatelessWidget {
  final ApiClient client;
  final String role;
  final int? agenceId;

  const EspaceAdminPage({
    super.key,
    required this.client,
    required this.role,
    this.agenceId,
  });

  @override
  Widget build(BuildContext context) {
    final superAdmin = role == 'SUPER_ADMIN';

    return Scaffold(
      appBar: AppBar(
        title: const Text('Billetterie'),
        actions: [
          IconButton(
            tooltip: 'Déconnexion',
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await client.deconnexion();

              if (!context.mounted) return;

              Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(
                  builder: (_) => const AuthPage(),
                ),
                (route) => false,
              );
            },
          ),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text(
            superAdmin ? 'Espace Super Administrateur' : 'Espace Administrateur Agence',
            style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
          ),
          if (!superAdmin && agenceId != null) Text('Agence N° $agenceId'),
          const SizedBox(height: 12),
          for (final e in (superAdmin ? _menuSuperAdmin : _menuAgence).entries)
            Card(
              child: ListTile(
                leading: Icon(e.value.$1),
                title: Text(e.key),
                trailing: const Icon(Icons.chevron_right),
                onTap: () => Navigator.push(
                    context, MaterialPageRoute(builder: (_) => e.value.$2)),
              ),
            ),
        ],
      ),
    );
  }

  static const _menuAgence = <String, (IconData, Widget)>{
    'Voyages': (Icons.directions_bus, AgenceVoyagesPage()),
    'Réservations': (Icons.confirmation_number, AgenceReservationsPage()),
    'Vérification des billets': (Icons.qr_code_scanner, AgenceVerificationPage()),
    'Remboursements': (Icons.payments_outlined, AgenceRemboursementsPage()),
    'Trajets': (Icons.route, AgenceTrajetsPage()),
    'Gares': (Icons.location_city, AgenceGaresPage()),
    'Véhicules': (Icons.airport_shuttle, AgenceVehiculesPage()),
    'Paramètres de rappel': (Icons.notifications_outlined, AgenceParametresPage()),
  };

  static const _menuSuperAdmin = <String, (IconData, Widget)>{
    'Agences': (Icons.business, SuperAdminAgencesPage()),
    'Commissions': (Icons.account_balance_wallet_outlined, CommissionsPage()),
  };
}
