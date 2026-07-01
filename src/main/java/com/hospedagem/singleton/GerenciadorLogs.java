package com.hospedagem.singleton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GerenciadorLogs {

    private static volatile GerenciadorLogs instancia;

    private final List<String> registros = new ArrayList<>();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private GerenciadorLogs() {
    }

    public static GerenciadorLogs getInstance() {
        if (instancia == null) {
            synchronized (GerenciadorLogs.class) {
                if (instancia == null) {
                    instancia = new GerenciadorLogs();
                }
            }
        }
        return instancia;
    }

    public void registrar(String nivel, String mensagem) {
        String entrada = String.format("[%s] [%s] %s",
                LocalDateTime.now().format(FORMATTER), nivel.toUpperCase(), mensagem);
        synchronized (registros) {
            registros.add(entrada);
        }
        System.out.println(entrada);
    }

    public void info(String mensagem) {
        registrar("INFO", mensagem);
    }

    public void warn(String mensagem) {
        registrar("WARN", mensagem);
    }

    public void erro(String mensagem) {
        registrar("ERRO", mensagem);
    }

    public List<String> obterRegistros() {
        synchronized (registros) {
            return Collections.unmodifiableList(new ArrayList<>(registros));
        }
    }

    public void limpar() {
        synchronized (registros) {
            registros.clear();
        }
    }
}
