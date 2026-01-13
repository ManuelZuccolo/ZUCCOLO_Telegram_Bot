# DnD Telegram Bot

## Descrizione del progetto
Questo progetto implementa un bot per **Telegram** dedicato al gioco **Dungeons & Dragons 5e**.  
Il bot consente di cercare informazioni sui mostri, mostrare immagini, registrare statistiche di utilizzo e visualizzare i mostri più cercati o statistiche generali.

Caratteristiche principali:
- Recupero dati dei mostri tramite **D&D 5e API**.
- Salvataggio delle ricerche e dei dati dei mostri in un **database SQLite** locale.
- Comandi interattivi per visualizzare mostri, statistiche generali e top mostri.
- Invio immagini dei mostri direttamente su Telegram.

---

## API Utilizzata

Il bot utilizza la seguente API per i dati dei mostri:

- **D&D 5e API**  
  Endpoint principale per i mostri: `https://www.dnd5eapi.co/api/2014/monsters/`  
  Documentazione: [https://www.dnd5eapi.co/docs/#get-/api/2014/monsters](https://www.dnd5eapi.co/docs/#get-/api/2014/monsters)

Il bot mappa i nomi dei mostri in **index** compatibili con l’API (`nameToIndex`) e scarica le informazioni JSON, comprese le immagini.

---

## Setup del progetto

### Prerequisiti
- **Java 17+**
- **Maven** o altro sistema di gestione delle dipendenze
- **Telegram Bot Token** (ottenibile tramite [BotFather](https://t.me/BotFather))

### Installazione

1. Clona il repository:
```bash
git clone <REPO_URL>
cd <NOME_CARTELLA>
```
2. Configura le dipendenze Maven (o IntelliJ/IDE di riferimento). Nel pom.xml includi:
```
<dependencies>
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots</artifactId>
        <version>6.7.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.0</version>
    </dependency>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.42.0.0</version>
    </dependency>
</dependencies>
```
3. Crea il file di configurazione config.properties nella root del progetto:
```
bot.username=NomeDelTuoBot
bot.token=TOKEN_DEL_TUO_BOT
```
4. Avvia il bot:
```
mvn compile exec:java -Dexec.mainClass="org.example.Main"
```
---
## Comandi disponibili
```
/monster <nome>	  -->  Recupera le informazioni del mostro, mostra anche l'immagine e salva la richiesta nel database.
/topmonsters	  -->  Mostra i 5 mostri più cercati dagli utenti.
/stats	          -->  Mostra statistiche generali: numero di mostri salvati e richieste totali.
```
### Esempio di utilizzo
```
/monster goblin
```
### Output bot:

Immagine del goblin

Informazioni leggibili sul mostro (HP, AC, abilità, azioni, ecc.)

---
## Schema Database
### users
| Colonna    | Tipo                | Descrizione             |
| ---------- | ------------------- | ----------------------- |
| chat_id    | INTEGER PRIMARY KEY | ID dell'utente Telegram |
| username   | TEXT                | Username Telegram       |
| first_name | TEXT                | Nome utente             |
| last_name  | TEXT                | Cognome utente          |
| first_seen | DATETIME            | Data prima interazione  |
| last_seen  | DATETIME            | Data ultima interazione |

### monsters
| Colonna       | Tipo             | Descrizione                           |
| ------------- | ---------------- | ------------------------------------- |
| monster_index | TEXT PRIMARY KEY | Identificativo del mostro (API/index) |
| name          | TEXT             | Nome del mostro                       |
| json_data     | TEXT             | JSON completo del mostro              |
| last_update   | DATETIME         | Data ultimo aggiornamento             |

### monster_request
| Colonna       | Tipo                              | Descrizione                  |
| ------------- | --------------------------------- | ---------------------------- |
| id            | INTEGER PRIMARY KEY AUTOINCREMENT | ID della richiesta           |
| chat_id       | INTEGER                           | ID utente (FK su `users`)    |
| monster_index | TEXT                              | ID mostro (FK su `monsters`) |
| timestamp     | DATETIME                          | Data richiesta               |

### Relazioni
```
monster_requests.chat_id        -->   users.chat_id

monster_requests.monster_index  -->   monsters.monster_index
```
---
## Query e statistiche
### Mostri più cercati
```
SELECT m.name, COUNT(r.id) AS searches
FROM monster_requests r
JOIN monsters m ON r.monster_index = m.monster_index
GROUP BY r.monster_index
ORDER BY searches DESC
LIMIT 5;
```
### Statistiche generali
```
SELECT COUNT(*) FROM monsters; -- Numero totale mostri nel DB
SELECT COUNT(*) FROM monster_requests; -- Numero totale richieste

```
Il bot utilizza queste query per popolare /topmonsters e /stats.

---
## Esempi Conversazioni
```
/monster young red dragon
Output: immagine + info dettagliate
```
```
/topmonsters
Output: lista 5 mostri più cercati
```
```
/stats
Output: statistiche generali del bot
```

---
## Note
-Il bot memorizza localmente i mostri nel database per evitare richieste ripetute all’API.

-Le immagini vengono recuperate dal campo image del JSON del mostro. Se l’immagine non è disponibile, viene inviato solo il testo.

---
## Configurazione del Bot
Per far funzionare il bot, è necessario inserire token e username nel file config.properties presente nella cartella principale del progetto.

### Il file deve avere questa struttura:
```
bot.username=IlTuoUsernameBot
bot.token=IlTuoTokenBot
```
Il bot legge automaticamente questi valori all’avvio e li usa per autenticarsi con le API di Telegram. Nessuna modifica al codice sorgente è richiesta.