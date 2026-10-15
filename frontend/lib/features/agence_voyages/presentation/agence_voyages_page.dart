import 'package:flutter/material.dart';

/// Voyages - propriétaire : Front C (Abdias) - Sprint 1
/// À coder : Liste des voyages, création (trajet, véhicule, date, prix), changement de prix, annulation.
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceVoyagesPage extends StatelessWidget {
  const AgenceVoyagesPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Voyages")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste des voyages, création (trajet, véhicule, date, prix), changement de prix, annulation.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}