package salgaderia.service;

import salgaderia.dao.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupService {

    private static final String PASTA_BACKUPS = "backups";
    private static final DateTimeFormatter FORMATO_TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    public Path realizarBackup() throws IOException {
        Path origem = Paths.get(Database.DB_FILE_NAME);

        if (!Files.exists(origem)) {
            throw new IOException("Arquivo do banco de dados não encontrado: " + origem.toAbsolutePath());
        }

        Path pastaBackups = Paths.get(PASTA_BACKUPS);
        Files.createDirectories(pastaBackups);

        String nomeBase = origem.getFileName().toString();
        int pontoExtensao = nomeBase.lastIndexOf('.');
        String nomeSemExtensao = pontoExtensao > 0 ? nomeBase.substring(0, pontoExtensao) : nomeBase;
        String extensao = pontoExtensao > 0 ? nomeBase.substring(pontoExtensao) : "";

        String timestamp = LocalDateTime.now().format(FORMATO_TIMESTAMP);
        String nomeBackup = nomeSemExtensao + "_" + timestamp + extensao;

        Path destino = pastaBackups.resolve(nomeBackup);
        Files.copy(origem, destino, StandardCopyOption.COPY_ATTRIBUTES);

        return destino;
    }
}

