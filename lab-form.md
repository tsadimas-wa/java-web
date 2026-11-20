# Lab: Φόρμα φοιτητών με Servlets και JSP στο NetBeans

Οδηγός εργαστηρίου, βήμα‑βήμα. Φτιάχνουμε στο **NetBeans** μια μικρή web εφαρμογή που:

- καταχωρεί φοιτητές (όνομα, επώνυμο, email) με μια **HTML φόρμα**,
- τους κρατάει σε μια απλή **λίστα στη μνήμη** (repository),
- τους εμφανίζει σε μια **σελίδα με πίνακα**.

Όλα τα requests περνούν από **controllers** (Servlets). Οι JSP σελίδες είναι απλές, χωρίς CSS, και μόνο
εμφανίζουν δεδομένα. Ο κώδικας είναι σε έτοιμα blocks για copy‑paste.

> Περισσότερες εξηγήσεις για όσα κάνουμε εδώ θα βρείτε στο [README.md](README.md) (Maven, Servlets), στο
> [Servers.md](Servers.md) (Tomcat) και στο [Forms.md](Forms.md) (φόρμες, JSP, forward/redirect).

## Περιεχόμενα

- [0. Τι θα φτιάξουμε](#0-τι-θα-φτιάξουμε)
- [1. Προετοιμασία: Tomcat στο NetBeans](#1-προετοιμασία-tomcat-στο-netbeans)
- [2. Δημιουργία Maven project: Web Application](#2-δημιουργία-maven-project-web-application)
- [3. Το pom.xml](#3-το-pomxml)
- [4. Καθάρισμα αρχείων που δεν χρειαζόμαστε](#4-καθάρισμα-αρχείων-που-δεν-χρειαζόμαστε)
- [5. Το web.xml](#5-το-webxml)
- [6. Η οντότητα Student](#6-η-οντότητα-student)
- [7. Το StudentRepository](#7-το-studentrepository)
- [8. Η φόρμα καταχώρησης: controller και JSP](#8-η-φόρμα-καταχώρησης-controller-και-jsp)
- [9. Η λίστα φοιτητών: controller και JSP](#9-η-λίστα-φοιτητών-controller-και-jsp)
- [10. Η αρχική σελίδα](#10-η-αρχική-σελίδα)
- [11. Εκτέλεση και δοκιμή](#11-εκτέλεση-και-δοκιμή)
- [12. Πώς δουλεύουν όλα μαζί](#12-πώς-δουλεύουν-όλα-μαζί)
- [Συχνά προβλήματα](#συχνά-προβλήματα)
- [Ασκήσεις](#ασκήσεις)

---

## 0. Τι θα φτιάξουμε

| URL                        | Μέθοδος | Controller                 | Τι κάνει                                         |
|----------------------------|---------|----------------------------|--------------------------------------------------|
| `/studentlab/`             | GET     | (στατική σελίδα)           | αρχική σελίδα με δύο links                       |
| `/studentlab/students/new` | GET     | `StudentFormController`    | εμφανίζει την κενή φόρμα                         |
| `/studentlab/students/new` | POST    | `StudentFormController`    | διαβάζει τη φόρμα και αποθηκεύει τον φοιτητή     |
| `/studentlab/students`     | GET     | `StudentListController`    | εμφανίζει όλους τους φοιτητές                    |

Η δομή του project στο τέλος:

```
studentlab/
├── pom.xml
└── src/main/
    ├── java/gr/uniwa/ice/studentlab/
    │   ├── model/
    │   │   └── Student.java                    η οντότητα (name, surname, email)
    │   ├── repository/
    │   │   └── StudentRepository.java          η λίστα με τους φοιτητές
    │   └── controller/
    │       ├── StudentFormController.java      /students/new  (GET: φόρμα, POST: αποθήκευση)
    │       └── StudentListController.java      /students      (GET: λίστα)
    └── webapp/
        ├── index.html                          αρχική σελίδα
        └── WEB-INF/
            ├── web.xml
            └── views/
                ├── student-form.jsp            η φόρμα
                └── student-list.jsp            ο πίνακας με τους φοιτητές
```

Οι JSP σελίδες μπαίνουν μέσα στο **`WEB-INF/`**. Ό,τι βρίσκεται εκεί **δεν** ανοίγει απευθείας από τον browser, μόνο
μέσω ενός controller. Έτσι κάθε request περνάει υποχρεωτικά από τον controller.

> Αυτή η οργάνωση λέγεται **MVC** (Model–View–Controller):
> **Model** είναι η `Student` (τα δεδομένα), **View** οι JSP (η εμφάνιση) και **Controller** τα Servlets
> (δέχονται το request, αποφασίζουν τι θα γίνει, και διαλέγουν ποια σελίδα θα εμφανιστεί).

---

## 1. Προετοιμασία: Tomcat στο NetBeans

Χρειάζεστε **JDK 21** (ή νεότερο) και **Apache Tomcat 10.1** ή **11**. Ο Tomcat κατεβαίνει ως zip από το
<https://tomcat.apache.org>. Αποσυμπιέστε τον σε έναν φάκελο, π.χ. `~/opt/apache-tomcat-11.0.x`.

> **Προσοχή:** ο Tomcat **9** δεν κάνει. Χρησιμοποιεί τα παλιά packages `javax.servlet.*`, ενώ εμείς γράφουμε
> `jakarta.servlet.*`.

Δηλώστε τον Tomcat στο NetBeans (μία φορά μόνο):

1. **Tools → Servers → Add Server…**
2. Επιλέξτε **Apache Tomcat or TomEE** → **Next**.
3. **Server Location:** ο φάκελος όπου αποσυμπιέσατε τον Tomcat.
4. **Username / Password:** γράψτε π.χ. `admin` / `admin` και αφήστε τσεκαρισμένο το
   *Create user if it does not exist*. Το NetBeans θα τον προσθέσει στο `conf/tomcat-users.xml` του Tomcat.
5. **Finish**.

Ο Tomcat εμφανίζεται πλέον στην καρτέλα **Services → Servers**.

---

## 2. Δημιουργία Maven project: Web Application

1. **File → New Project…**
2. **Categories:** `Java with Maven` · **Projects:** `Web Application` → **Next**.
3. Συμπληρώστε:

   | Πεδίο            | Τιμή                            |
   |------------------|---------------------------------|
   | Project Name     | `studentlab`                    |
   | Project Location | ένας φάκελος της επιλογής σας   |
   | Group Id         | `gr.uniwa.ice`                  |
   | Version          | `1.0-SNAPSHOT` (όπως είναι)     |
   | Package          | `gr.uniwa.ice.studentlab`       |

   → **Next**.
4. **Server:** ο Tomcat του βήματος 1 · **Jakarta EE Version:** `Jakarta EE 10 Web` (ή όποια έκδοση 10 προτείνει) → **Finish**.

Το NetBeans φτιάχνει το project. Στο παράθυρο **Projects** το βλέπετε έτσι:

```
studentlab
├── Web Pages              ← ο φάκελος src/main/webapp
├── Source Packages        ← ο φάκελος src/main/java
├── Dependencies
└── Project Files
    └── pom.xml
```

> Το **Group Id** μαζί με το **Project Name** (που γίνεται το **Artifact Id**) είναι οι «συντεταγμένες» του project στο
> Maven. Δείτε το [Maven.md](Maven.md#coordinates-gav).

---

## 3. Το pom.xml

Ανοίξτε το **Project Files → pom.xml**, σβήστε **όλο** το περιεχόμενο και επικολλήστε:

**Αρχείο:** `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>gr.uniwa.ice</groupId>
  <artifactId>studentlab</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <properties>
    <maven.compiler.release>21</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <!-- Servlet API: provided, γιατί το παρέχει ο Tomcat -->
    <dependency>
      <groupId>jakarta.servlet</groupId>
      <artifactId>jakarta.servlet-api</artifactId>
      <version>6.0.0</version>
      <scope>provided</scope>
    </dependency>

    <!-- JSTL: tags για τις JSP σελίδες (<c:forEach>, <c:out>, <c:if>) -->
    <dependency>
      <groupId>jakarta.servlet.jsp.jstl</groupId>
      <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
      <version>3.0.2</version>
      <exclusions>
        <exclusion>
          <groupId>jakarta.el</groupId>
          <artifactId>jakarta.el-api</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    <dependency>
      <groupId>org.glassfish.web</groupId>
      <artifactId>jakarta.servlet.jsp.jstl</artifactId>
      <version>3.0.1</version>
    </dependency>
  </dependencies>

  <build>
    <finalName>studentlab</finalName>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-war-plugin</artifactId>
        <version>3.4.0</version>
      </plugin>
    </plugins>
  </build>
</project>
```

Αποθηκεύστε (**Ctrl+S**). Κάντε δεξί κλικ στο project → **Build** (ή **Clean and Build**) και περιμένετε το `BUILD SUCCESS`.
Την πρώτη φορά το Maven κατεβάζει τις βιβλιοθήκες, οπότε θέλει λίγο χρόνο.

**Τι δηλώνουμε εδώ:**

- **`jakarta.servlet-api`** με scope **`provided`**: τα Servlets. Τα χρειαζόμαστε για το compile, αλλά δεν μπαίνουν στο `.war`,
  γιατί τα έχει ήδη ο Tomcat.
- **JSTL** (δύο dependencies, το API και η υλοποίηση): τα tags `<c:forEach>`, `<c:out>`, `<c:if>` που θα χρησιμοποιήσουμε
  στις JSP. Ο Tomcat **δεν** τα έχει, οπότε μπαίνουν στο `.war`.
- **`finalName`**: το αρχείο θα λέγεται `studentlab.war`, άρα η εφαρμογή θα ανοίγει στο `/studentlab`.

---

## 4. Καθάρισμα αρχείων που δεν χρειαζόμαστε

Ανάλογα με την έκδοση, το NetBeans φτιάχνει μερικά αρχεία παραδείγματος. Σβήστε (δεξί κλικ → **Delete**) όσα από αυτά υπάρχουν:

- στο **Source Packages**: το package **`gr.uniwa.ice.studentlab.resources`** (κλάσεις `JakartaRestConfiguration`,
  `JakartaEE10Resource`). Είναι παράδειγμα REST service και, με το νέο `pom.xml`, δεν κάνει πια compile.
- στο **Web Pages → WEB-INF**: το **`beans.xml`**.

**Μην** σβήσετε το `Web Pages → META-INF/context.xml`, αν υπάρχει. Εκεί το NetBeans γράφει το context path (`/studentlab`).

Το `index.html` το κρατάμε. Θα αλλάξουμε το περιεχόμενό του στο βήμα 10.

---

## 5. Το web.xml

Αν **δεν** υπάρχει ήδη `Web Pages → WEB-INF → web.xml`, φτιάξτε το:
δεξί κλικ στο project → **New → Other… → Web → Standard Deployment Descriptor (web.xml)** → **Next** → **Finish**.

Σβήστε το περιεχόμενό του και επικολλήστε:

**Αρχείο:** `src/main/webapp/WEB-INF/web.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                             https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

  <!-- Τα δεδομένα των φορμών και οι σελίδες σε UTF-8, για να δουλεύουν τα ελληνικά -->
  <request-character-encoding>UTF-8</request-character-encoding>
  <response-character-encoding>UTF-8</response-character-encoding>

  <welcome-file-list>
    <welcome-file>index.html</welcome-file>
  </welcome-file-list>
</web-app>
```

Δεν δηλώνουμε εδώ τα Servlets. Κάθε Servlet δηλώνει το URL του με το annotation **`@WebServlet`**.

---

## 6. Η οντότητα Student

Δεξί κλικ στο **Source Packages → `gr.uniwa.ice.studentlab`** → **New → Java Package…** → όνομα
`gr.uniwa.ice.studentlab.model` → **Finish**.
Μετά, δεξί κλικ στο νέο package → **New → Java Class…** → όνομα `Student` → **Finish**.
Σβήστε ό,τι έφτιαξε το NetBeans και επικολλήστε:

**Αρχείο:** `src/main/java/gr/uniwa/ice/studentlab/model/Student.java`

```java
package gr.uniwa.ice.studentlab.model;

public class Student {

    private String name;
    private String surname;
    private String email;

    public Student(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }
}
```

Μια απλή κλάση με τρία πεδία, έναν constructor και **getters**. Οι getters είναι απαραίτητοι: όταν σε μια JSP γράφουμε
`${student.name}`, στην πραγματικότητα καλείται η `student.getName()`.

---

## 7. Το StudentRepository

Το **repository** είναι η κλάση που «αποθηκεύει» τα δεδομένα. Εδώ τα κρατάμε απλώς σε μια `List` στη μνήμη. Αργότερα, στη
θέση της θα μπει μια βάση δεδομένων, χωρίς να αλλάξουν οι controllers.

Φτιάξτε το package `gr.uniwa.ice.studentlab.repository` και μέσα του την κλάση `StudentRepository`:

**Αρχείο:** `src/main/java/gr/uniwa/ice/studentlab/repository/StudentRepository.java`

```java
package gr.uniwa.ice.studentlab.repository;

import gr.uniwa.ice.studentlab.model.Student;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    // Μία λίστα για όλη την εφαρμογή (static). Χάνεται όταν σταματήσει ο Tomcat.
    private static final List<Student> students = new ArrayList<>();

    // synchronized: πολλά requests μπορεί να έρθουν ταυτόχρονα,
    // οπότε μόνο ένα τη φορά επιτρέπεται να πειράζει τη λίστα.
    public static synchronized void save(Student student) {
        students.add(student);
    }

    public static synchronized List<Student> findAll() {
        return new ArrayList<>(students);   // αντίγραφο, για να μην αλλάζει η λίστα απ' έξω
    }
}
```

> Επειδή η λίστα είναι **στη μνήμη**, οι φοιτητές χάνονται σε κάθε επανεκκίνηση του Tomcat ή νέο deploy.
> Αυτό είναι αναμενόμενο σε αυτό το lab.

---

## 8. Η φόρμα καταχώρησης: controller και JSP

### 8.1 Ο controller

Φτιάξτε το package `gr.uniwa.ice.studentlab.controller` και μέσα του μια **Java Class** (όχι *Servlet*: ο οδηγός
*New → Servlet* του NetBeans βάζει πολύ έτοιμο κώδικα που δεν χρειαζόμαστε) με όνομα `StudentFormController`:

**Αρχείο:** `src/main/java/gr/uniwa/ice/studentlab/controller/StudentFormController.java`

```java
package gr.uniwa.ice.studentlab.controller;

import gr.uniwa.ice.studentlab.model.Student;
import gr.uniwa.ice.studentlab.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/students/new")
public class StudentFormController extends HttpServlet {

    // GET /students/new → εμφάνιση της κενής φόρμας
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
    }

    // POST /students/new → ο χρήστης πάτησε «Αποθήκευση»
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Διαβάζουμε τα πεδία της φόρμας (τα ονόματα είναι τα name="..." των <input>)
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String email = request.getParameter("email");

        // 2. Απλός έλεγχος: όλα τα πεδία υποχρεωτικά
        if (isBlank(name) || isBlank(surname) || isBlank(email)) {
            request.setAttribute("error", "Συμπληρώστε όλα τα πεδία.");
            request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
            return;
        }

        // 3. Αποθήκευση
        StudentRepository.save(new Student(name.trim(), surname.trim(), email.trim()));

        // 4. Redirect στη λίστα (ο browser κάνει νέο GET στο /students)
        response.sendRedirect(request.getContextPath() + "/students");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
```

Ο ίδιος controller χειρίζεται **δύο** requests στο ίδιο URL:

- **`doGet`**: απλώς κάνει **forward** στη JSP της φόρμας. Το forward γίνεται μέσα στον server: ο browser δεν το καταλαβαίνει
  και το URL μένει `/students/new`.
- **`doPost`**: διαβάζει τα πεδία με **`getParameter`**, τα ελέγχει, αποθηκεύει τον φοιτητή και κάνει **redirect** στη λίστα.
  Αν λείπει κάποιο πεδίο, βάζει ένα μήνυμα λάθους ως **attribute** στο request και ξαναδείχνει τη φόρμα.

### 8.2 Η JSP της φόρμας

Στο **Web Pages → WEB-INF**: δεξί κλικ → **New → Folder…** → όνομα `views` → **Finish**.
Μετά, δεξί κλικ στο `views` → **New → JSP…** → όνομα `student-form` (το `.jsp` το βάζει μόνο του) → **Finish**.
Σβήστε το περιεχόμενο και επικολλήστε:

**Αρχείο:** `src/main/webapp/WEB-INF/views/student-form.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Νέος φοιτητής</title>
</head>
<body>
    <h1>Νέος φοιτητής</h1>

    <c:if test="${not empty error}">
        <p><strong><c:out value="${error}"/></strong></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/students/new">
        <p>
            <label for="name">Όνομα:</label>
            <input type="text" id="name" name="name" value="<c:out value='${param.name}'/>">
        </p>
        <p>
            <label for="surname">Επώνυμο:</label>
            <input type="text" id="surname" name="surname" value="<c:out value='${param.surname}'/>">
        </p>
        <p>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>">
        </p>
        <p>
            <button type="submit">Αποθήκευση</button>
        </p>
    </form>

    <p><a href="${pageContext.request.contextPath}/students">Λίστα φοιτητών</a></p>
</body>
</html>
```

**Τι προσέχουμε:**

- **`method="post"`** και **`action=".../students/new"`**: με το πάτημα του κουμπιού, ο browser στέλνει POST στον
  `StudentFormController`, που θα το χειριστεί η `doPost`.
- Το **`name="..."`** κάθε `<input>` είναι το όνομα που διαβάζει ο controller με `request.getParameter("...")`.
- **`${pageContext.request.contextPath}`** είναι το `/studentlab`. Έτσι τα links δουλεύουν όποιο κι αν είναι το όνομα της εφαρμογής.
- **`${error}`**: το attribute που έβαλε ο controller. Υπάρχει μόνο όταν ο έλεγχος αποτύχει.
- **`${param.name}`**: η τιμή που είχε στείλει ο χρήστης. Αν η φόρμα ξαναεμφανιστεί με λάθος, τα πεδία
  κρατούν ό,τι είχε γράψει.
- **`<c:out>`**: εμφανίζει κείμενο με ασφάλεια (μετατρέπει π.χ. το `<` σε `&lt;`), ώστε ο χρήστης να μην μπορεί να «περάσει»
  δικό του HTML ή JavaScript στη σελίδα.

---

## 9. Η λίστα φοιτητών: controller και JSP

### 9.1 Ο controller

Στο package `gr.uniwa.ice.studentlab.controller`, νέα **Java Class** με όνομα `StudentListController`:

**Αρχείο:** `src/main/java/gr/uniwa/ice/studentlab/controller/StudentListController.java`

```java
package gr.uniwa.ice.studentlab.controller;

import gr.uniwa.ice.studentlab.model.Student;
import gr.uniwa.ice.studentlab.repository.StudentRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/students")
public class StudentListController extends HttpServlet {

    // GET /students → εμφάνιση όλων των φοιτητών
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Παίρνουμε τα δεδομένα από το repository
        List<Student> students = StudentRepository.findAll();

        // 2. Τα «δίνουμε» στη JSP ως attribute του request
        request.setAttribute("students", students);

        // 3. Forward στη JSP, που θα φτιάξει το HTML
        request.getRequestDispatcher("/WEB-INF/views/student-list.jsp").forward(request, response);
    }
}
```

Αυτό είναι το τυπικό σχήμα ενός controller: **παίρνει δεδομένα → τα βάζει στο request → κάνει forward σε μια JSP**.

### 9.2 Η JSP της λίστας

Δεξί κλικ στο `WEB-INF/views` → **New → JSP…** → όνομα `student-list`:

**Αρχείο:** `src/main/webapp/WEB-INF/views/student-list.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Φοιτητές</title>
</head>
<body>
    <h1>Φοιτητές</h1>

    <c:choose>
        <c:when test="${empty students}">
            <p>Δεν υπάρχουν φοιτητές ακόμα.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr>
                    <th>Όνομα</th>
                    <th>Επώνυμο</th>
                    <th>Email</th>
                </tr>
                <c:forEach var="student" items="${students}">
                    <tr>
                        <td><c:out value="${student.name}"/></td>
                        <td><c:out value="${student.surname}"/></td>
                        <td><c:out value="${student.email}"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>

    <p><a href="${pageContext.request.contextPath}/students/new">Νέος φοιτητής</a></p>
</body>
</html>
```

- **`${students}`** είναι το attribute που έβαλε ο `StudentListController`.
- **`<c:forEach>`** γράφει μία γραμμή του πίνακα για κάθε φοιτητή της λίστας. Μέσα στον βρόχο, το `student` είναι ο τρέχων φοιτητής.
- **`${student.name}`** καλεί την `getName()` της `Student`.
- **`<c:choose>` / `<c:when>` / `<c:otherwise>`**: όπως ένα `if … else`. Αν η λίστα είναι άδεια, δείχνει μήνυμα αντί για πίνακα.

---

## 10. Η αρχική σελίδα

Ανοίξτε το **Web Pages → index.html** (αν δεν υπάρχει: δεξί κλικ στο **Web Pages** → **New → HTML…** → `index`),
σβήστε το περιεχόμενο και επικολλήστε:

**Αρχείο:** `src/main/webapp/index.html`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Student Lab</title>
</head>
<body>
    <h1>Student Lab</h1>
    <ul>
        <li><a href="students/new">Νέος φοιτητής</a></li>
        <li><a href="students">Λίστα φοιτητών</a></li>
    </ul>
</body>
</html>
```

Είναι στατική σελίδα, χωρίς δεδομένα, οπότε δεν χρειάζεται controller. Τα links είναι **σχετικά** (χωρίς `/` μπροστά), άρα
από το `/studentlab/` οδηγούν στο `/studentlab/students/new` και στο `/studentlab/students`.

---

## 11. Εκτέλεση και δοκιμή

1. Δεξί κλικ στο project → **Run** (ή **F6**).
   Το NetBeans κάνει build, ξεκινά τον Tomcat (αν δεν τρέχει), κάνει deploy την εφαρμογή και ανοίγει τον browser στο
   <http://localhost:8080/studentlab/>.
2. Πατήστε **Νέος φοιτητής**, συμπληρώστε τα πεδία και πατήστε **Αποθήκευση**. Βλέπετε τη λίστα με τον φοιτητή.
3. Προσθέστε άλλον έναν, με ελληνικά. Πρέπει να εμφανίζονται σωστά.
4. Αφήστε ένα πεδίο κενό και πατήστε **Αποθήκευση**. Βλέπετε το μήνυμα «Συμπληρώστε όλα τα πεδία.», ενώ τα υπόλοιπα
   πεδία κρατούν ό,τι γράψατε.
5. Δοκιμάστε να ανοίξετε απευθείας <http://localhost:8080/studentlab/WEB-INF/views/student-list.jsp>.
   Παίρνετε **404**: οι JSP μέσα στο `WEB-INF` είναι προσβάσιμες μόνο μέσω controller.

Τα μηνύματα του Tomcat (και τυχόν σφάλματα) εμφανίζονται στο παράθυρο **Output**, στην καρτέλα του Tomcat.

> **Αλλαγές ενώ τρέχει:** μετά από αλλαγή σε κώδικα Java ή JSP, πατήστε ξανά **Run**. Το NetBeans κάνει νέο deploy
> (και, επειδή η λίστα είναι στη μνήμη, οι φοιτητές που είχατε βάλει χάνονται).

---

## 12. Πώς δουλεύουν όλα μαζί

Τι γίνεται όταν ο χρήστης καταχωρεί έναν φοιτητή:

```
Browser                         Tomcat
───────                         ──────
GET  /studentlab/students/new ─▶ StudentFormController.doGet
                                   └─ forward ─▶ student-form.jsp ─▶ HTML φόρμα
                              ◀─ 200 OK (η φόρμα)

(ο χρήστης συμπληρώνει και πατά «Αποθήκευση»)

POST /studentlab/students/new ─▶ StudentFormController.doPost
     name=…&surname=…&email=…      ├─ getParameter("name"), …
                                   ├─ StudentRepository.save(...)
                                   └─ sendRedirect("/studentlab/students")
                              ◀─ 302 Found, Location: /studentlab/students

GET  /studentlab/students     ─▶ StudentListController.doGet
                                   ├─ StudentRepository.findAll()
                                   ├─ setAttribute("students", ...)
                                   └─ forward ─▶ student-list.jsp ─▶ HTML πίνακας
                              ◀─ 200 OK (η λίστα)
```

**Γιατί redirect μετά το POST;** Αν ο controller έκανε απευθείας forward στη λίστα, ο browser θα έμενε στο URL του POST. Ένα
**Refresh (F5)** τότε θα ξανάστελνε τη φόρμα και θα αποθήκευε τον ίδιο φοιτητή δεύτερη φορά. Με το redirect, ο browser
καταλήγει σε ένα απλό GET στο `/students`, που μπορεί να γίνει refresh όσες φορές θέλουμε.
Το σχήμα αυτό λέγεται **Post/Redirect/Get**.

**Forward ή redirect;**

|                    | `forward`                                  | `sendRedirect`                                   |
|--------------------|--------------------------------------------|--------------------------------------------------|
| Πού γίνεται        | μέσα στον server                           | ο browser κάνει **νέο** request                  |
| URL στον browser   | δεν αλλάζει                                | αλλάζει                                          |
| Attributes         | περνάνε στη JSP                            | **χάνονται** (νέο request)                       |
| Πότε το χρησιμοποιούμε | για να εμφανίσουμε μια JSP             | μετά από ένα POST που άλλαξε δεδομένα            |

**Parameter ή attribute;**

- **Parameter** (`getParameter`): ό,τι στέλνει ο **browser** (πεδία φόρμας, `?x=...` στο URL). Πάντα `String`.
- **Attribute** (`setAttribute` / `${...}`): ό,τι βάζει ο **controller** στο request για να το διαβάσει η JSP. Οποιοδήποτε αντικείμενο.

Για περισσότερα, δείτε το [Forms.md](Forms.md#14-σύνοψη-parameters-attributes-forward-redirect).

---

## Συχνά προβλήματα

| Πρόβλημα | Αιτία / Λύση |
|----------|--------------|
| Δεν κάνει compile το `JakartaRestConfiguration` / `package jakarta.ws.rs does not exist` | Δεν σβήσατε το package `resources` του NetBeans (βήμα 4). |
| `package jakarta.servlet does not exist` | Δεν αντικαταστήσατε το `pom.xml` (βήμα 3), ή δεν έγινε ακόμα build. Δεξί κλικ στο project → **Clean and Build**. |
| 404 στο `/students` ή στο `/students/new` | Ελέγξτε το URL (πρέπει να έχει το `/studentlab`) και το `@WebServlet(...)` του controller. |
| 404 με μήνυμα για `/WEB-INF/views/....jsp` | Λάθος όνομα ή θέση του αρχείου JSP. Πρέπει να είναι στο `Web Pages/WEB-INF/views/` και το όνομα να ταιριάζει ακριβώς με αυτό του `getRequestDispatcher(...)`. |
| Η σελίδα δείχνει κυριολεκτικά `${student.name}` | Λείπει το `web.xml` ή είναι παλιάς έκδοσης (2.x). Χρησιμοποιήστε αυτό του βήματος 5. |
| `The absolute uri: [jakarta.tags.core] cannot be resolved` | Λείπουν οι dependencies της JSTL από το `pom.xml` (βήμα 3). |
| Τα ελληνικά εμφανίζονται ως `???` ή `ÎÎ±...` | Λείπει η πρώτη γραμμή `<%@ page contentType=... charset=UTF-8 ... %>` στη JSP, ή το `<request-character-encoding>` στο `web.xml`. |
| `PropertyNotFoundException: Property [name] not found on type [...Student]` | Λείπει ο getter (`getName()`) στην `Student`, ή έχει άλλο όνομα. |
| Ο Tomcat δεν ξεκινά: `Address already in use` | Τρέχει ήδη άλλος server στη θύρα 8080. Σταματήστε τον ή αλλάξτε θύρα (δείτε [Servers.md, 3.9](Servers.md#39-αλλαγή-θύρας)). |

---

## Ασκήσεις

1. Προσθέστε στη `Student` ένα τέταρτο πεδίο, **`am`** (αριθμός μητρώου). Χρειάζεται αλλαγή στην κλάση, στον controller,
   στη φόρμα και στον πίνακα.
2. Ελέγξτε στον `StudentFormController` ότι το email περιέχει `@`. Αν όχι, εμφανίστε το μήνυμα «Μη έγκυρο email.».
3. Εμφανίστε πάνω από τον πίνακα της λίστας το πλήθος των φοιτητών (Υπόδειξη: `${students.size()}`).
4. Μην επιτρέπετε δύο φοιτητές με το ίδιο email. Προσθέστε στο `StudentRepository` μια μέθοδο
   `existsByEmail(String email)` και χρησιμοποιήστε τη στον controller.
5. Φτιάξτε έναν τρίτο controller, `/students/search?surname=...`, που εμφανίζει μόνο τους φοιτητές με το επώνυμο που δόθηκε.
   Ξαναχρησιμοποιήστε τη `student-list.jsp`.
