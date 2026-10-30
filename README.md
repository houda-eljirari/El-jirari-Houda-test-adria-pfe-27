# Plateforme bancaire micro-services — Test technique ADRIA / ENSET PFE 27

Plateforme bancaire basée sur des micro-services Spring : consultation de comptes, enregistrement de transactions, événements Kafka, assistant conversationnel agentique (MCP) et détection de fraude en Python.

**Auteur :** El Jirari Houda

## Architecture

| Service | Port | Rôle |
|---|---|---|
| config-service | 8888 | Configuration centralisée (`bank.transaction.max-amount=1000`) |
| discovery-service | 8761 | Eureka (découverte des services) |
| gateway-service | 8080 | Spring Cloud Gateway |
| account-service | 8081 | Gestion des comptes (REST, GraphQL, MCP) |
| transaction-service | 8082 | Transactions (REST, GraphQL, MCP, OpenFeign, Resilience4J, Kafka producer) |
| event-consumer-service | 8083 | Consumer Kafka (journalisation) |
| chatbot-service | 8084 | Agent IA (Spring AI + clients MCP) |
| Kafka | 9092 | Topic `bank.transactions.created` |

Les services métier suivent la séparation en couches `domain/`, `repository/`, `service/`, `web/rest/`, `web/graphql/` et `mcp/`. Les contrôleurs REST, GraphQL et MCP appellent la même couche `service`.

## Technologies

Java 17, Spring Boot 3.4.5, Spring Cloud 2024.0.1 (Config, Eureka, Gateway, OpenFeign), Resilience4J, Spring GraphQL, Spring AI 1.0.0 (serveurs et client MCP), Apache Kafka, H2, Maven multi-modules, Python 3 (pandas, scikit-learn), Docker.

## Règles métier (transaction-service)

- Montant strictement positif et inférieur ou égal à `bank.transaction.max-amount` (1000).
- Compte existant et actif.
- Devise de la transaction identique à celle du compte.
- Solde consulté avant la transaction, puis mis à jour après l'enregistrement.
- Virement d'un compte à un autre disponible dans account-service.
- Après la persistance, un événement `TransactionCreated` est publié dans `bank.transactions.created` (clé = `accountId`).

## Prérequis

- JDK 17, Maven 3.9+
- Docker Desktop (pour Kafka)
- Python 3.10+ (partie fraude)
- Une clé API Groq (ou compatible OpenAI) pour le chatbot

## Lancement

1. Démarrer Kafka :
   ```bash
   docker compose -f infra/docker-compose-kafka.yml up -d
   ```
2. Compiler :
   ```bash
   mvn clean compile
   ```
3. Lancer les services dans cet ordre (IntelliJ ou `mvn spring-boot:run -pl <module>`) :
    1. config-service
    2. discovery-service
    3. account-service
    4. transaction-service
    5. gateway-service
    6. event-consumer-service
    7. chatbot-service (avec la variable d'environnement `GROQ_API_KEY`)

> La clé API n'est jamais versionnée : elle est fournie par variable d'environnement.

## Tests rapides

Le fichier [`docs/requests.http`](docs/requests.http) contient les requêtes de test (client HTTP d'IntelliJ).

```bash
# Créer un compte
curl -X POST http://localhost:8081/api/accounts -H "Content-Type: application/json" \
  -d '{"customerReference":"CUST-001","currency":"EUR","balance":500}'

# Créer une transaction (remplacer <ACCOUNT_ID>)
curl -X POST http://localhost:8082/api/transactions -H "Content-Type: application/json" \
  -d '{"accountId":"<ACCOUNT_ID>","amount":100,"currency":"EUR","merchantCategory":"FOOD","country":"MA"}'

# Chatbot
curl -X POST http://localhost:8084/chat -H "Content-Type: application/json" \
  -d '{"message":"Liste tous les comptes bancaires"}'
```

| Connecteur | account-service | transaction-service |
|---|---|---|
| REST | `http://localhost:8081/api/accounts` | `http://localhost:8082/api/transactions` |
| GraphQL | `http://localhost:8081/graphiql` | `http://localhost:8082/graphiql` |
| MCP (SSE) | `http://localhost:8081/sse` | `http://localhost:8082/sse` |

Via la gateway : `http://localhost:8080/api/accounts` et `http://localhost:8080/api/transactions`.

## Tolérance aux pannes

La communication transaction-service vers account-service utilise OpenFeign, protégée par un CircuitBreaker et un Retry (Resilience4J). Si account-service est indisponible, l'API renvoie une erreur 503 explicite. L'état du circuit est visible sur `http://localhost:8082/actuator/health`.

## Partie H : détection de fraude

Dossier `fraud-detection/`.

```bash
cd fraud-detection
python -m venv .venv
.venv\Scripts\activate        # Linux/Mac : source .venv/bin/activate
pip install -r requirements.txt
python fraud_detection.py
```

Le script charge `bank_transactions.csv`, affiche la proportion de fraudes et les valeurs manquantes, fait une séparation stratifiée 80/20, exclut les identifiants, puis applique un pipeline (imputation, One-Hot, normalisation) ajusté uniquement sur le train et une régression logistique pondérée. Le seuil est choisi par validation croisée sur le train ; le test n'est utilisé qu'une fois.

Résultats sur le jeu de test (classe fraude) : précision 0,208, rappel 0,196, F1 0,202. Détail dans `fraud-detection/resultats.txt`, `confusion_matrix.png` et `precision_recall.png`.

## État d'avancement

| Partie | Statut |
|---|---|
| A. Préparation | Fait |
| B. Conception (diagrammes) | À compléter dans le rapport |
| C. Account, Transaction, OpenFeign, Resilience4J | Fait |
| D. Gateway, Eureka, Config | Fait |
| E. Chatbot agentique | Fait (test via client REST) |
| F. Kafka (producer et consumer) | Fait |
| G. Client web ou mobile | Non traité (optionnel) |
| H. Fraude Python | Fait |
| I. DevOps (Docker, CI) | Partiel (Kafka via Docker Compose) |

Keycloak (authentification) n'est pas mis en place dans cette version.

## Structure du dépôt

```
├── pom.xml                      Projet Maven parent
├── config-service/
├── discovery-service/
├── gateway-service/
├── account-service/
├── transaction-service/
├── event-consumer-service/
├── chatbot-service/
├── fraud-detection/             Script Python et résultats
├── infra/                       docker-compose-kafka.yml
└── docs/                        requests.http et captures (screenshots/)
```
