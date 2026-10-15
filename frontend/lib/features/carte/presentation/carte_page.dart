import 'package:flutter/material.dart';

/// Carte des agences - propriétaire : Front D (Bouba) - Sprint 1
/// À coder : Carte des agences actives, marqueurs cliquables (option).
/// Voir TODO.md de ce dossier et docs/API.md.
class CartePage extends StatelessWidget {
  const CartePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Carte des agences")),
      body: const Center(
        child: Padding(
          padding: EdgeInsets.all(24),
          child: Text("À coder : Carte des agences actives, marqueurs cliquables (option).", textAlign: TextAlign.center),
        ),
      ),
    );
  }
}