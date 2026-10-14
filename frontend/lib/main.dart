import 'package:flutter/material.dart';
import 'core/theme/app_theme.dart';
import 'features/auth/presentation/auth_page.dart';
import 'features/super_admin_agences/presentation/super_admin_agences_page.dart';
import 'features/agence_gares/presentation/agence_gares_page.dart';
import 'features/agence_vehicules/presentation/agence_vehicules_page.dart';
import 'features/agence_trajets/presentation/agence_trajets_page.dart';
import 'features/agence_voyages/presentation/agence_voyages_page.dart';
import 'features/agence_reservations/presentation/agence_reservations_page.dart';
import 'features/agence_verification/presentation/agence_verification_page.dart';
import 'features/commissions/presentation/commissions_page.dart';
import 'features/recherche/presentation/recherche_page.dart';
import 'features/carte/presentation/carte_page.dart';
import 'features/reservation/presentation/reservation_page.dart';
import 'features/paiement/presentation/paiement_page.dart';
import 'features/billet/presentation/billet_page.dart';

void main() => runApp(const BilletterieApp());

class BilletterieApp extends StatelessWidget {
  const BilletterieApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Billetterie',
      theme: AppTheme.clair(),
      home: const AccueilPage(),
    );
  }
}

/// Accueil de développement : un lien vers chaque page à construire.
/// TODO (Paulo, intégration) : remplacer par la vraie navigation.
class AccueilPage extends StatelessWidget {
  const AccueilPage({super.key});

  @override
  Widget build(BuildContext context) {
    final entrees = <String, Widget>{
      'Connexion (Front C)': const AuthPage(),
      'Agences (super admin) (Front C)': const SuperAdminAgencesPage(),
      'Gares (Front C)': const AgenceGaresPage(),
      'Véhicules (Front C)': const AgenceVehiculesPage(),
      'Trajets (Front C)': const AgenceTrajetsPage(),
      'Voyages (Front C)': const AgenceVoyagesPage(),
      'Réservations (agence) (Front C)': const AgenceReservationsPage(),
      'Vérification des billets (Front C)': const AgenceVerificationPage(),
      'Commissions (super admin) (Front C)': const CommissionsPage(),
      'Recherche de voyages (Front D)': const RecherchePage(),
      'Carte des agences (Front D)': const CartePage(),
      'Réservation (Front D)': const ReservationPage(),
      'Paiement (Front D)': const PaiementPage(),
      'Billet (Front D)': const BilletPage(),
    };
    return Scaffold(
      appBar: AppBar(title: const Text('Billetterie - accueil de développement')),
      body: ListView(
        children: entrees.entries
            .map((e) => ListTile(
                  title: Text(e.key),
                  onTap: () => Navigator.push(
                      context, MaterialPageRoute(builder: (_) => e.value)),
                ))
            .toList(),
      ),
    );
  }
}
