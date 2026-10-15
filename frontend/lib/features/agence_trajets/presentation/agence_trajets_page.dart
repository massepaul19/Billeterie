import 'package:flutter/material.dart';

/// Trajets - propriétaire : Front C (Abdias) - Sprint 1
/// À coder : Liste et formulaire des trajets (gare de départ, gare d'arrivée, durée).
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceTrajetsPage extends StatelessWidget {
  const AgenceTrajetsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Trajets")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Liste et formulaire des trajets (gare de départ, gare d'arrivée, durée).", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}