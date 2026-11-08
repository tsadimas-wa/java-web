package gr.uniwa.ice.webapp1.repository;

import gr.uniwa.ice.webapp1.model.Student;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Αποθήκευση φοιτητών ΣΤΗ ΜΝΗΜΗ. Τα δεδομένα χάνονται όταν σταματήσει ο server.
 * (Σε επόμενο εργαστήριο θα αντικατασταθεί από βάση δεδομένων.)
 */
public class StudentRepository {

    // Ένα και μοναδικό αντικείμενο για όλη την εφαρμογή (Singleton)
    private static final StudentRepository INSTANCE = new StudentRepository();

    public static StudentRepository getInstance() {
        return INSTANCE;
    }

    // Ο server εξυπηρετεί πολλά requests ταυτόχρονα (σε διαφορετικά threads),
    // γι' αυτό χρησιμοποιούμε thread-safe δομές.
    private final Map<Long, Student> students = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    private StudentRepository() {
    }

    public Student save(Student student) {
        student.setId(nextId.getAndIncrement());
        students.put(student.getId(), student);
        return student;
    }

    public Optional<Student> findById(long id) {
        return Optional.ofNullable(students.get(id));
    }

    public List<Student> findAll() {
        return students.values().stream()
                .sorted(Comparator.comparingLong(Student::getId))
                .toList();
    }
}
