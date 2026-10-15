import 'package:flutter/material.dart';

/// Gares - propriétaire : Front D (Bouba) - Sprint 1
/// À coder : Liste et formulaire des gares (ville, nom, quartier, adresse, position).
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceGaresPage extends StatelessWidget {
  const AgenceGaresPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Gares")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste et formulaire des gares (ville, nom, quartier, adresse, position).", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}