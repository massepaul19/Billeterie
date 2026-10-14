import 'package:intl/intl.dart';

String formatFcfa(int montant) =>
    '${NumberFormat.decimalPattern('fr').format(montant)} FCFA';

String formatDateHeure(DateTime date) =>
    DateFormat('EEE d MMM, HH:mm', 'fr').format(date);
// Penser à appeler initializeDateFormatting('fr') au démarrage.
