import 'package:flutter/material.dart';

/// Billet - propriétaire : Front D (Bouba) - Sprint 2
/// À coder : Affichage du billet, ouverture du PDF et annulation.
/// Voir TODO.md de ce dossier et docs/API.md.
class BilletPage extends StatelessWidget {
  const BilletPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Billet")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Affichage du billet, ouverture du PDF et annulation.", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}