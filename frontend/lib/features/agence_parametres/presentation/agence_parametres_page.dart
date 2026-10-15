import 'package:flutter/material.dart';

/// Paramètres de rappel - propriétaire : Front C (Abdias) - Sprint 2
/// À coder : Choix des délais de rappel par email (24 h et 2 h par défaut).
/// Voir TODO.md de ce dossier et docs/API.md.
class AgenceParametresPage extends StatelessWidget {
  const AgenceParametresPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Paramètres de rappel")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Choix des délais de rappel par email (24 h et 2 h par défaut).", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}