import 'package:flutter/material.dart';

/// Véhicules - propriétaire : Front D (Bouba) - Sprint 1
/// À coder : Liste et formulaire des véhicules, avec envoi de la photo.
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceVehiculesPage extends StatelessWidget {
  const AgenceVehiculesPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Véhicules")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste et formulaire des véhicules, avec envoi de la photo.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}