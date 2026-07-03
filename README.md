# Sistema de Hospedagem

## 👥 Membros do Grupo
*   **Ana Luiza Cavalcante Oliveira**
*   **André Fortini de Mello**
*   **Lucas Maia Marques Pinheiro**

---

## 📺 Video pela visão do admin
[▶️ Assistir demonstração do projeto](./docs/0701(1).mp4)

---

## ⚙️ Tecnologias Utilizadas

**Backend**
*   **Java 17**
*   **Spring Boot 3.5.0** (Spring Web, Spring Data JPA, Spring Validation)
*   **Lombok**
*   **Maven** — gerenciador de build/dependências
*   **H2 Database** — banco em memória (perfil padrão)
*   **MySQL** — banco relacional (perfil `mysql`, opcional)
*   **JUnit 5 + Mockito** — testes

**Frontend**
*   **HTML5, CSS3 e JavaScript puro (vanilla)** — servidos como recursos estáticos pelo próprio Spring, sem build separado

---

## 🚀 Como rodar o projeto

### Pré-requisitos
*   **Java 17 (JDK)** instalado
*   **Maven** instalado (o projeto não inclui `mvnw`)
*   MySQL só é necessário caso queira usar o perfil `mysql` — por padrão roda com H2 em memória

### Passos

Clone o repositório e entre na pasta:
```bash
git clone <url-do-repositorio>
cd trabalho-pratico-trabalho_pratico_pm-main
```

Rodar a aplicação (perfil padrão, banco H2 em memória — não precisa configurar nada):
```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. O frontend fica acessível direto nessa URL (ex: `http://localhost:8080/index.html` ou `http://localhost:8080/login.html`).

Rodar os testes:
```bash
mvn test
```

Gerar o `.jar` e executar:
```bash
mvn clean package
java -jar target/sistema-hospedagem-1.0.0.jar
```

**Usando MySQL em vez de H2** (opcional — requer um MySQL rodando localmente):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
Por padrão espera um MySQL em `localhost:3306`, banco `hospedagem`, usuário `root`, senha `root`. É possível sobrescrever via variáveis de ambiente `DB_URL`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`.

---

🎯 *Projeto desenvolvido para fins acadêmicos e de avaliação.*
