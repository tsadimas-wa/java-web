# Εκτέλεση της εφαρμογής: Jetty και Tomcat

Πού και πώς τρέχει η web εφαρμογή μας: με το **Jetty** μέσα από το Maven (`mvn jetty:run`), ή σε έναν
εγκατεστημένο **Apache Tomcat**, όπου κάνουμε deploy το `.war`.

> **Προαπαιτούμενα:** Έχετε φτιάξει το project `webapp1` και κάνει build με `mvn package`
> ([README.md](README.md), Βήματα 1–7).

## Περιεχόμενα

- [1. Εκτέλεση με Jetty](#1-εκτέλεση-με-jetty)
  - [1.1 Αλλαγή θύρας (port)](#11-αλλαγή-θύρας-port)
- [2. Jetty ή Tomcat;](#2-jetty-ή-tomcat)
- [3. Εκτέλεση σε Apache Tomcat](#3-εκτέλεση-σε-apache-tomcat)
  - [3.1 Ποια έκδοση Tomcat](#31-ποια-έκδοση-tomcat)
  - [3.2 Εγκατάσταση](#32-εγκατάσταση)
  - [3.3 Εκκίνηση και τερματισμός](#33-εκκίνηση-και-τερματισμός)
  - [3.4 Deploy της εφαρμογής](#34-deploy-της-εφαρμογής)
  - [3.5 Context path](#35-context-path)
  - [3.6 Ο κύκλος ανάπτυξης: αλλαγή → redeploy](#36-ο-κύκλος-ανάπτυξης-αλλαγή--redeploy)
  - [3.7 Deploy από τον browser: Tomcat Manager](#37-deploy-από-τον-browser-tomcat-manager)
  - [3.8 Logs](#38-logs)
  - [3.9 Αλλαγή θύρας](#39-αλλαγή-θύρας)
  - [3.10 Tomcat μέσα από το IDE](#310-tomcat-μέσα-από-το-ide)

## 1. Εκτέλεση με Jetty

Ο πιο γρήγορος τρόπος για ανάπτυξη — δεν χρειάζεται να εγκαταστήσετε server:

```bash
mvn jetty:run
```

Όταν δείτε `Started Server`, ανοίξτε στον browser:

- <http://localhost:8080/webapp1/> → η σελίδα `index.jsp` («Hello World!»)
- <http://localhost:8080/webapp1/hello> → το servlet («Γεια σου, κόσμε!»)
- <http://localhost:8080/webapp1/hello?name=Μαρία> → «Γεια σου, Μαρία!»

Τερματισμός με **Ctrl+C**.

**Αλλαγές ενώ τρέχει:** αλλαγές σε `index.jsp` (και γενικά σε JSP, HTML, CSS) φαίνονται με απλό refresh του browser, μέσα σε λίγα
δευτερόλεπτα. Αλλαγές σε **κώδικα Java** χρειάζονται Ctrl+C και ξανά `mvn jetty:run` (ή αυτόματο reload, δείτε το tip στο «Jetty ή Tomcat;»).


### 1.1 Αλλαγή θύρας (port)

Αν δεν ορίσετε κάτι, ο Jetty ακούει στη θύρα **8080**. Αν η θύρα είναι πιασμένη ή θέλετε άλλη, έχετε δύο τρόπους:

**α) Μόνο για μία εκτέλεση**, από τη γραμμή εντολών (δεν αλλάζει το `pom.xml`):

```bash
mvn jetty:run -Djetty.http.port=9090
```

**β) Μόνιμα**, στο `pom.xml`, προσθέτοντας `<httpConnector>` στο `<configuration>` του Jetty plugin:

```xml
<configuration>
  <webApp>
    <contextPath>/webapp1</contextPath>
  </webApp>
  <httpConnector>
    <port>9090</port>
  </httpConnector>
</configuration>
```

Και στις δύο περιπτώσεις η εφαρμογή ανοίγει στο <http://localhost:9090/webapp1/>.


## 2. Jetty ή Tomcat;

### Τι κοινό έχουν

Ο **Eclipse Jetty** και ο **Apache Tomcat** είναι και οι δύο **servlet containers**: web servers που
υλοποιούν τις προδιαγραφές Jakarta **Servlet**, **JSP**, **Expression Language** και **WebSocket**. Και οι δύο είναι
open source και δωρεάν. Ο Jetty αναπτύσσεται από το Eclipse Foundation και ο Tomcat από το Apache Software Foundation.

Επειδή υλοποιούν **τις ίδιες προδιαγραφές**, **το ίδιο `.war` τρέχει και στους δύο χωρίς καμία αλλαγή**.
Ο κώδικάς μας χρησιμοποιεί μόνο `jakarta.servlet.*` και δεν «ξέρει» σε ποιον server τρέχει.
Ακόμα και τα JSP μεταγλωττίζονται με τον ίδιο μηχανισμό: ο Jetty χρησιμοποιεί τον **Jasper**, τον JSP compiler του Tomcat.

> Κανένας από τους δύο **δεν** είναι πλήρης *Jakarta EE application server* (όπως WildFly, GlassFish, Payara, Open Liberty).
> Δεν περιλαμβάνουν, π.χ., JPA ή CDI. Αν τα χρειαστούμε, τα προσθέτουμε ως dependencies στο `pom.xml`.

### Σε τι διαφέρουν (όπως τους χρησιμοποιούμε εδώ)

|                              | **Jetty** μέσω `mvn jetty:run`                                        | **Tomcat** (εγκατεστημένος)                                    |
|------------------------------|------------------------------------------------------------------------|----------------------------------------------------------------|
| Εγκατάσταση                  | καμία: το Maven κατεβάζει το plugin                                    | download και αποσυμπίεση, ρύθμιση `JAVA_HOME`                  |
| Εκκίνηση                     | `mvn jetty:run` μέσα στον φάκελο του project                            | `catalina.sh run` / `startup.sh`                               |
| Από πού τρέχει η εφαρμογή    | **απευθείας** από τα `src/main/webapp/` και `target/classes/`. **Δεν χρειάζεται `.war`** | από το **`.war`** στο `webapps/`              |
| Αλλαγή σε JSP / CSS / HTML   | φαίνεται με απλό refresh (σε λίγα δευτερόλεπτα)                       | νέο `mvn package` και αντιγραφή του `.war`                     |
| Αλλαγή σε κώδικα Java        | Ctrl+C και ξανά `mvn jetty:run` (ή αυτόματα, βλ. παρακάτω)             | νέο `mvn package` και αντιγραφή του `.war` (αυτόματο redeploy) |
| Πόσες εφαρμογές              | μία: το project που τρέχουμε                                          | **πολλές** ταυτόχρονα, μία ανά `.war`                          |
| Ρυθμίσεις                    | στο `pom.xml` (`<configuration>` του plugin)                           | αρχεία στο `conf/` (`server.xml`, `tomcat-users.xml`, …)       |
| Διαχείριση από browser       | όχι                                                                    | ναι, Tomcat Manager (ενότητα 3.7)                                      |
| Logs                         | στο τερματικό                                                          | στο τερματικό (`catalina.sh run`) ή στον φάκελο `logs/`        |

### Ποιον να χρησιμοποιήσω;

- **Κατά την ανάπτυξη:** `mvn jetty:run`. Ξεκινά με μία εντολή, δεν θέλει εγκατάσταση, και οι αλλαγές σε JSP/CSS φαίνονται αμέσως.
- **Για να δείτε πώς γίνεται πραγματικό deploy:** Tomcat. Είναι ο πιο διαδεδομένος standalone servlet container,
  και η διαδικασία `.war` → server είναι αυτή που θα συναντήσετε σε πραγματικούς servers.
- Και στην αγορά χρησιμοποιούνται και οι δύο. Συχνά μάλιστα **ενσωματωμένοι** μέσα σε μια εφαρμογή αντί για ξεχωριστό
  πρόγραμμα. Για παράδειγμα, το Spring Boot έχει by default ενσωματωμένο Tomcat και προαιρετικά Jetty.

> **Tip: αυτόματο reload στο Jetty.** Προσθέστε `<scan>1</scan>` στο `<configuration>` του Jetty plugin:
> ```xml
> <configuration>
>   <scan>1</scan>
>   <webApp>
>     <contextPath>/webapp1</contextPath>
>   </webApp>
> </configuration>
> ```
> Αφήστε το `mvn jetty:run` να τρέχει και, μετά από κάθε αλλαγή σε Java, τρέξτε σε **δεύτερο τερματικό** `mvn compile`.
> Το Jetty βλέπει τα νέα `.class` και ξαναφορτώνει μόνο του την εφαρμογή μέσα σε λίγα δευτερόλεπτα.
> Προσοχή: με το reload χάνονται τα δεδομένα που κρατάει η εφαρμογή στη μνήμη.


## 3. Εκτέλεση σε Apache Tomcat

Ας δούμε τώρα πώς γίνεται deploy σε έναν **ξεχωριστό** servlet container, τον **Apache Tomcat**. Ο Tomcat είναι ένα αυτόνομο
πρόγραμμα: το εγκαθιστούμε μία φορά και μπορεί να «φιλοξενεί» πολλές web εφαρμογές ταυτόχρονα.

### 3.1 Ποια έκδοση Tomcat

| Tomcat   | Servlet API        | Packages     | Ελάχιστη Java |
|----------|--------------------|--------------|---------------|
| 9.0      | 4.0                | `javax.*`    | 8             |
| 10.1     | 6.0                | `jakarta.*`  | 11            |
| **11.0** | 6.1                | `jakarta.*`  | 17            |

Ο κώδικάς μας χρησιμοποιεί `jakarta.servlet` (Servlet 6.0), οπότε χρειάζεστε **Tomcat 10.1 ή 11**
(προτείνεται ο 11). Ο Tomcat **9 δεν θα δουλέψει**.

### 3.2 Εγκατάσταση

1. Από το <https://tomcat.apache.org/download-11.cgi>, ενότητα **Binary Distributions → Core**, κατεβάστε:
   - Linux/macOS: το **`tar.gz`**
   - Windows: το **`zip`** (64-bit Windows zip). Ο Windows Service Installer **δεν** συνιστάται για ανάπτυξη.
2. Αποσυμπιέστε το σε έναν φάκελο **χωρίς κενά και ελληνικά** στη διαδρομή, π.χ.:
   ```bash
   mkdir -p ~/opt && cd ~/opt
   tar xzf ~/Downloads/apache-tomcat-11.0.*.tar.gz
   ```
   Στα Windows αποσυμπιέστε το zip π.χ. στο `C:\tools\`.
3. Ο φάκελος που προκύπτει (π.χ. `~/opt/apache-tomcat-11.0.27`) λέγεται **`CATALINA_HOME`**:
   ```
   apache-tomcat-11.0.x/
   ├── bin/       ← scripts εκκίνησης/τερματισμού (startup, shutdown, catalina)
   ├── conf/      ← ρυθμίσεις (server.xml, tomcat-users.xml, …)
   ├── lib/       ← βιβλιοθήκες του Tomcat (εδώ βρίσκεται και το Servlet API)
   ├── logs/      ← αρχεία καταγραφής
   ├── webapps/   ← ΕΔΩ μπαίνουν οι εφαρμογές (.war)
   ├── work/      ← προσωρινά αρχεία (π.χ. τα JSP μεταγλωττισμένα σε Java)
   └── temp/
   ```
4. Ο Tomcat χρειάζεται να βρει τη Java: πρέπει να είναι ορισμένο το **`JAVA_HOME`** (ή το `JRE_HOME`). Αν δεν το έχετε ήδη
   ορίσει, δείτε τη [ρύθμιση στο Maven.md](Maven.md#μεταβλητές-περιβάλλοντος-path-και-java_home).
   Για ευκολία ορίστε και το **`CATALINA_HOME`** (τον φάκελο του Tomcat), ώστε οι εντολές του οδηγού να δουλεύουν όπως είναι.
   **Προσαρμόστε την έκδοση** στο όνομα του φακέλου που προέκυψε από την αποσυμπίεση:

   Linux/macOS (στο τέλος του `~/.bashrc` ή του `~/.zshrc`):
   ```bash
   export CATALINA_HOME=~/opt/apache-tomcat-11.0.27
   ```
   και μετά `source ~/.bashrc` (ή `source ~/.zshrc`), ή ανοίξτε νέο τερματικό.

   Windows (PowerShell, μόνιμα για τον χρήστη):
   ```powershell
   setx CATALINA_HOME "C:\tools\apache-tomcat-11.0.27"
   ```
   και ανοίξτε **νέο** τερματικό (το `setx` δεν επηρεάζει το τρέχον).

   > Αν έχετε αποσυμπιέσει **zip** σε Linux/macOS, δώστε δικαίωμα εκτέλεσης στα scripts:
   > `chmod +x $CATALINA_HOME/bin/*.sh`

### 3.3 Εκκίνηση και τερματισμός

Υπάρχουν δύο τρόποι εκκίνησης:

| Εντολή (Linux/macOS)             | Εντολή (Windows)                 | Τι κάνει                                                                     |
|----------------------------------|----------------------------------|------------------------------------------------------------------------------|
| `$CATALINA_HOME/bin/catalina.sh run` | `& "$env:CATALINA_HOME\bin\catalina.bat" run` | Τρέχει **στο προσκήνιο**: τα logs φαίνονται στο τερματικό, σταματάει με **Ctrl+C**. **Προτείνεται για ανάπτυξη.** |
| `$CATALINA_HOME/bin/startup.sh`  | `& "$env:CATALINA_HOME\bin\startup.bat"`  | Τρέχει **στο παρασκήνιο**. Τα logs πάνε στο `logs/catalina.out`.              |
| `$CATALINA_HOME/bin/shutdown.sh` | `& "$env:CATALINA_HOME\bin\shutdown.bat"` | Σταματάει τον Tomcat που ξεκίνησε με `startup`.                              |

> Η στήλη των Windows είναι για **PowerShell** (`$env:ΜΕΤΑΒΛΗΤΗ`, και `&` για να τρέξει μια διαδρομή μέσα σε εισαγωγικά).
> Στο παλιό *Command Prompt* (cmd) η αντίστοιχη σύνταξη είναι `%CATALINA_HOME%\bin\catalina.bat run`.

Μετά την εκκίνηση, ανοίξτε <http://localhost:8080>. Πρέπει να δείτε τη σελίδα καλωσορίσματος του Tomcat.

### 3.4 Deploy της εφαρμογής

Ο Tomcat έχει **auto-deploy**: παρακολουθεί τον φάκελο `webapps/` και, όταν βρει ένα νέο `.war`,
το αποσυμπιέζει και ξεκινάει την εφαρμογή **χωρίς επανεκκίνηση**. Αν ο Tomcat δεν τρέχει τη στιγμή της αντιγραφής,
η εφαρμογή γίνεται deploy στην επόμενη εκκίνησή του.

Αφήστε τον Tomcat να τρέχει ([ενότητα 3.3](#33-εκκίνηση-και-τερματισμός)) σε ένα τερματικό. Σε ένα **δεύτερο τερματικό**, στον φάκελο του project
(εκεί όπου είναι το `pom.xml`):

```bash
cd ~/java-web/webapp1                        # ο φάκελος του project σας
mvn clean package
cp target/webapp1.war $CATALINA_HOME/webapps/
```

Windows (PowerShell):
```powershell
mvn clean package
Copy-Item target\webapp1.war $env:CATALINA_HOME\webapps\
```

Στο log θα εμφανιστεί κάτι σαν:

```
INFO [...] HostConfig.deployWAR Deploying web application archive [.../webapps/webapp1.war]
INFO [...] HostConfig.deployWAR Deployment of web application archive [.../webapps/webapp1.war] has finished in [416] ms
```

Ανοίξτε:

- <http://localhost:8080/webapp1/> → `index.jsp`
- <http://localhost:8080/webapp1/hello?name=Μαρία> → το servlet

### 3.5 Context path

Το πρώτο κομμάτι του URL (το **context path**) προκύπτει από το **όνομα του αρχείου `.war`**:

| Αρχείο στο `webapps/` | URL                                  |
|-----------------------|--------------------------------------|
| `webapp1.war`         | `http://localhost:8080/webapp1/`     |
| `shop.war`            | `http://localhost:8080/shop/`        |
| `ROOT.war`            | `http://localhost:8080/` (ρίζα)      |

Γι' αυτό στο `pom.xml` ορίσαμε `<finalName>webapp1</finalName>`: χωρίς αυτό το αρχείο θα λεγόταν
`webapp1-1.0-SNAPSHOT.war` και το URL θα ήταν `/webapp1-1.0-SNAPSHOT/`.

> Για να τρέχει η εφαρμογή στη ρίζα, αντιγράψτε τη ως `ROOT.war`. Πρώτα όμως διαγράψτε
> τον υπάρχοντα φάκελο `webapps/ROOT` (τη σελίδα καλωσορίσματος του Tomcat).

> **Και στο Jetty;** Εκεί το context path **δεν** προκύπτει από όνομα αρχείου, γιατί το `mvn jetty:run` δεν φτιάχνει `.war`.
> Το ορίσαμε ρητά στο `pom.xml`, με `<contextPath>/webapp1</contextPath>` στη ρύθμιση του Jetty plugin ([README, Βήμα 3](README.md#βήμα-3-εκσυγχρονισμός-του-pomxml)),
> ώστε τα URLs να είναι ίδια και στους δύο servers. Χωρίς αυτή τη ρύθμιση, το Jetty θα έτρεχε την εφαρμογή στη ρίζα (`/`).

### 3.6 Ο κύκλος ανάπτυξης: αλλαγή → redeploy

Κάθε φορά που αλλάζετε κώδικα:

```bash
mvn clean package && cp target/webapp1.war $CATALINA_HOME/webapps/
```

Ο Tomcat καταλαβαίνει ότι το `.war` άλλαξε και κάνει **αυτόματα redeploy** μέσα σε λίγα δευτερόλεπτα.
Τον Tomcat **δεν** χρειάζεται να τον επανεκκινήσετε.

**Undeploy** (αφαίρεση της εφαρμογής): διαγράψτε το `webapps/webapp1.war`. Ο Tomcat θα σβήσει μόνος του και τον φάκελο `webapps/webapp1/`.

> Αν κάτι «κολλήσει», σταματήστε τον Tomcat, διαγράψτε τα `webapps/webapp1.war`, `webapps/webapp1/`
> και `work/Catalina/localhost/webapp1/`, και ξεκινήστε τον ξανά.

### 3.7 Deploy από τον browser: Tomcat Manager

Ο Tomcat έρχεται με δύο web εφαρμογές διαχείρισης, τον **Manager** και τον **Host Manager**. Ο Manager έχει επιπλέον
και μια σελίδα κατάστασης (*Server Status*):

| UI                | URL                                     | Τι κάνει                                                                 | Απαιτούμενος ρόλος |
|-------------------|-----------------------------------------|--------------------------------------------------------------------------|--------------------|
| **Manager App**   | <http://localhost:8080/manager/html>    | λίστα εφαρμογών, start/stop/reload/undeploy, **ανέβασμα `.war`**         | `manager-gui`      |
| **Host Manager**  | <http://localhost:8080/host-manager/html> | διαχείριση virtual hosts (δεν θα τη χρειαστούμε)                       | `admin-gui`        |
| **Server Status** | <http://localhost:8080/manager/status>  | μνήμη, threads, ενεργά requests                                           | `manager-gui` (ή `manager-status`) |

Τα ίδια links υπάρχουν και ως κουμπιά στη σελίδα καλωσορίσματος (<http://localhost:8080>).

Για λόγους ασφαλείας **δεν υπάρχει κανένας χρήστης by default**. Αν πατήσετε *Manager App* χωρίς να
έχετε ορίσει χρήστη, ο browser θα ζητήσει username/password και ό,τι κι αν δώσετε θα αποτύχει.

#### α) Ορισμός χρήστη στο `conf/tomcat-users.xml`

1. **Σταματήστε** τον Tomcat (Ctrl+C ή `shutdown.sh`/`shutdown.bat`).
2. Ανοίξτε με έναν text editor το αρχείο `$CATALINA_HOME/conf/tomcat-users.xml`
   (Windows: `$env:CATALINA_HOME\conf\tomcat-users.xml`). Από τερματικό, π.χ. `code $CATALINA_HOME/conf/tomcat-users.xml`
   (VS Code) ή `nano $CATALINA_HOME/conf/tomcat-users.xml`.
   Στο **Codespaces** δείτε και την ενότητα [Χρήστης για τον Tomcat Manager στο Codespaces](README.md#χρήστης-για-τον-tomcat-manager-στο-codespaces).
3. Το αρχείο περιέχει ήδη παραδείγματα χρηστών, αλλά είναι **μέσα σε σχόλια** `<!-- ... -->`, άρα δεν ισχύουν.
   Προσθέστε **μόνο** αυτές τις τρεις γραμμές, **ακριβώς πριν** το κλείσιμο `</tomcat-users>` (στο τέλος του αρχείου),
   έξω από οποιοδήποτε σχόλιο:

   ```xml
   <role rolename="manager-gui"/>
   <role rolename="admin-gui"/>
   <user username="admin" password="Ενας-Δικος-Σας-Κωδικος" roles="manager-gui,admin-gui"/>
   ```

   Έτσι θα μοιάζει το αρχείο μετά την αλλαγή. **Μην** αντιγράψετε την ετικέτα `<tomcat-users ...>`: υπάρχει ήδη στο αρχείο,
   και αν μπει δεύτερη φορά, το XML χαλάει.

   ```xml
   <tomcat-users xmlns="http://tomcat.apache.org/xml"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
                 version="1.0">

     <!-- ... τα σχόλια/παραδείγματα που ήδη υπάρχουν, τα αφήνουμε ως έχουν ... -->

     <role rolename="manager-gui"/>
     <role rolename="admin-gui"/>
     <user username="admin" password="Ενας-Δικος-Σας-Κωδικος" roles="manager-gui,admin-gui"/>

   </tomcat-users>
   ```

   - `username` / `password`: ό,τι θέλετε. Αυτά θα δίνετε στον browser.
   - `roles`: τι επιτρέπεται στον χρήστη, χωρισμένοι με κόμμα **χωρίς κενά**. Το `manager-gui` αρκεί
     για το Manager App. Το `admin-gui` χρειάζεται μόνο για το Host Manager.
   - Αν ο κωδικός περιέχει χαρακτήρες του XML (`&`, `<`, `"`), γράψτε τους ως `&amp;`, `&lt;`, `&quot;`.
     Πιο απλά: αποφύγετε τους.
4. Αποθηκεύστε και **ξεκινήστε ξανά** τον Tomcat. Το αρχείο διαβάζεται κατά την εκκίνηση.
   Αν το XML έχει λάθος, ο Tomcat γράφει σφάλμα στο log (`logs/catalina.<ημερομηνία>.log`) και η σύνδεση δεν θα δουλεύει.

#### β) Σύνδεση

1. Ανοίξτε <http://localhost:8080/manager/html>.
2. Ο browser εμφανίζει παράθυρο σύνδεσης (*HTTP Basic Authentication*). Δώστε το `username` και το `password` που ορίσατε στο (α).
3. Θα δείτε τον πίνακα *Applications* με όλες τις εφαρμογές (`/`, `/docs`, `/manager`, `/webapp1`, …)
   και κουμπιά **Start / Stop / Reload / Undeploy** για την καθεμία.

#### γ) Deploy ενός `.war` από το UI

1. Στην ενότητα **Deploy → WAR file to deploy**, πατήστε *Choose File / Browse* και επιλέξτε το `target/webapp1.war`.
2. Πατήστε **Deploy**.
3. Η εφαρμογή εμφανίζεται στη λίστα *Applications*. Πατήστε το path της (`/webapp1`) για να την ανοίξετε.

Μετά από αλλαγές στον κώδικα: `mvn clean package`, μετά **Undeploy** της παλιάς έκδοσης και **Deploy** της νέας.
Εναλλακτικά, αντιγράψτε το νέο `.war` στο `webapps/` όπως στην [ενότητα 3.6](#36-ο-κύκλος-ανάπτυξης-αλλαγή--redeploy).

#### Προβλήματα σύνδεσης

| Τι βλέπετε | Αιτία / Λύση |
|------------|--------------|
| Ο browser ξαναζητάει συνεχώς κωδικό (401) | Λάθος username/password, ή ο χρήστης είναι μέσα σε `<!-- -->`, ή δεν κάνατε επανεκκίνηση τον Tomcat. |
| Σελίδα **403 Access Denied** μετά τη σύνδεση | Ο χρήστης υπάρχει αλλά **δεν έχει τον ρόλο** `manager-gui` (π.χ. ορθογραφικό λάθος στο `roles`). |
| 403 όταν ανοίγετε τον Manager από **άλλον υπολογιστή** | Από προεπιλογή ο Manager δέχεται συνδέσεις **μόνο από localhost**. Είναι ρύθμιση ασφαλείας στο `webapps/manager/META-INF/context.xml` (`RemoteCIDRValve`) και για τις ασκήσεις **δεν** χρειάζεται να αλλάξει. |
| Θέλετε να συνδεθείτε ως άλλος χρήστης | Ο browser θυμάται τα στοιχεία της Basic Authentication. Κλείστε όλα τα παράθυρα του browser ή χρησιμοποιήστε ιδιωτικό παράθυρο (incognito). |

> ⚠ **Ασφάλεια:** Ο κωδικός αποθηκεύεται **σε απλό κείμενο** στο `tomcat-users.xml`. Για τον
> υπολογιστή σας και τις ασκήσεις αυτό είναι αποδεκτό. Μην χρησιμοποιήσετε όμως κωδικό που χρησιμοποιείτε
> και αλλού. Σε πραγματικό server, ο Manager είτε απενεργοποιείται είτε προστατεύεται πολύ πιο αυστηρά.
> Μην αφαιρέσετε ποτέ τον περιορισμό του localhost σε μηχάνημα προσβάσιμο από το internet.

#### Deploy από τη γραμμή εντολών (`manager-script`)

Υπάρχει και ο ρόλος `manager-script`. Με αυτόν μπορείτε να κάνετε deploy και από τη γραμμή εντολών,
μέσω του *text interface* του Manager. Ο ρόλος αυτός μπαίνει **σε διαφορετικό χρήστη** από τον `manager-gui`
(η τεκμηρίωση του Tomcat το συνιστά για λόγους ασφαλείας):

```xml
<role rolename="manager-script"/>
<user username="deployer" password="ΚΩΔΙΚΟΣ" roles="manager-script"/>
```

```bash
curl -u deployer:ΚΩΔΙΚΟΣ -T target/webapp1.war \
  "http://localhost:8080/manager/text/deploy?path=/webapp1&update=true"
```

### 3.8 Logs

Όλα τα logs βρίσκονται στο `$CATALINA_HOME/logs/`:

| Αρχείο                                | Περιεχόμενο                                                        |
|---------------------------------------|--------------------------------------------------------------------|
| `catalina.out`                        | ό,τι γράφεται στην κονσόλα (μόνο με `startup.sh`), μαζί με `System.out.println` |
| `catalina.<ημερομηνία>.log`           | μηνύματα του ίδιου του Tomcat (εκκίνηση, deploy)                    |
| `localhost.<ημερομηνία>.log`          | σφάλματα/exceptions των εφαρμογών                                   |
| `localhost_access_log.<ημερομηνία>.txt` | κάθε HTTP request που δέχτηκε ο server                           |

Παρακολούθηση σε πραγματικό χρόνο (Ctrl+C για έξοδο):

```bash
tail -f $CATALINA_HOME/logs/catalina.out                          # Linux/macOS
```

```powershell
Get-Content -Wait "$env:CATALINA_HOME\logs\catalina.$(Get-Date -Format yyyy-MM-dd).log"   # Windows (PowerShell)
```

> Στα Windows ο `startup.bat` ανοίγει ξεχωριστό παράθυρο με τα μηνύματα και **δεν** γράφει `catalina.out`, γι' αυτό
> παρακολουθούμε το `catalina.<ημερομηνία>.log`.

### 3.9 Αλλαγή θύρας

Αν η θύρα 8080 είναι πιασμένη (π.χ. από ένα `mvn jetty:run` που τρέχει ακόμα), αλλάξτε τη θύρα
στο `conf/server.xml`:

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8443" />
```

π.χ. σε `port="8081"`, και κάντε επανεκκίνηση τον Tomcat.

### 3.10 Tomcat μέσα από το IDE

Όλα τα IDE μπορούν να ξεκινούν τον Tomcat και να κάνουν deploy με ένα κλικ. Σε όλα δηλώνετε
τον φάκελο `CATALINA_HOME`:

- **IntelliJ IDEA Ultimate**: *Run → Edit Configurations → + → Tomcat Server → Local* και στην καρτέλα
  *Deployment* επιλέξτε `webapp1:war exploded`. Στο IntelliJ Community υπάρχει το plugin *Smart Tomcat*.
- **Eclipse (for Enterprise Java and Web Developers)**: καρτέλα *Servers → new server → Apache → Tomcat v11.0*,
  και μετά δεξί κλικ στο project → *Run As → Run on Server*.
- **NetBeans**: *Tools → Servers → Add Server → Apache Tomcat or TomEE*.
- **VS Code**: extension *Community Server Connectors*.

Χρησιμοποιείτε IDE; Μάθετε πρώτα τη χειροκίνητη διαδικασία (`mvn package` → `cp` στο `webapps/`).
Είναι αυτό που κάνει το IDE στο παρασκήνιο, και αυτό που θα κάνετε σε έναν πραγματικό server.

---

Πίσω στο [README.md](README.md) · Συνέχεια: [Forms.md](Forms.md)
