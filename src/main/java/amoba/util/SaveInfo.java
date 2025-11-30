package amoba.util;

public record SaveInfo(
        boolean exists,
        String lastModified,
        String playerName
) {}
