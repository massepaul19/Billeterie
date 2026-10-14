package cm.billetterie.util;

import java.util.UUID;

public final class CodeBilletGenerator {
    private CodeBilletGenerator() {}

    public static String nouveauCode() {
        // TODO : vérifier l'unicité en base (colonne code_unique UNIQUE) et réessayer en cas de collision
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
