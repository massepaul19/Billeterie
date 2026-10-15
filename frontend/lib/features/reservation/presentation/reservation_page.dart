import 'package:flutter/material.dart';

/// Réservation - propriétaire : Front D (Bouba) - Sprint 2
/// À coder : Formulaire de contact et décompte de 5 minutes pendant lequel la place est retenue.
/// Voir TODO.md de ce dossier et docs/API.md.
class ReservationPage extends StatelessWidget {
  const ReservationPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Réservation")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Formulaire de contact et décompte de 5 minutes pendant lequel la place est retenue.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}