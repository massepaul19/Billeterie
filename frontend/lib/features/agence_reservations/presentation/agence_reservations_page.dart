import 'package:flutter/material.dart';

/// Réservations (agence) - propriétaire : Front C (Abdias) - Sprint 2
/// À coder : Liste filtrable des réservations de l'agence (client, voyage, montant, état).
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceReservationsPage extends StatelessWidget {
  const AgenceReservationsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Réservations (agence)")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste filtrable des réservations de l'agence (client, voyage, montant, état).", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}