package at.koopro.spells_n_squares.features.education;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Manages O.W.L. and N.E.W.T. exam systems.
 * Handles exam mechanics, grading, and certificate generation.
 */
public final class ExamSystem {
    private ExamSystem() {
    }
    
    // Registry of exam results (UUID -> ExamResults)
    private static final Map<UUID, ExamResults> examResults = new HashMap<>();
    
    /**
     * Exam types.
     */
    public enum ExamType {
        OWL("Ordinary Wizarding Level", "O.W.L.", 5), // 5 grades
        NEWT("Nastily Exhausting Wizarding Test", "N.E.W.T.", 5); // 5 grades
        
        private final String fullName;
        private final String abbreviation;
        private final int maxGrade;
        
        ExamType(String fullName, String abbreviation, int maxGrade) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
            this.maxGrade = maxGrade;
        }
        
        public String getFullName() { return fullName; }
        public String getAbbreviation() { return abbreviation; }
        public int getMaxGrade() { return maxGrade; }
    }
    
    /**
     * Exam grades.
     */
    public enum Grade {
        OUTSTANDING("O", "Outstanding", 5),
        EXCEEDS_EXPECTATIONS("E", "Exceeds Expectations", 4),
        ACCEPTABLE("A", "Acceptable", 3),
        POOR("P", "Poor", 2),
        DREADFUL("D", "Dreadful", 1),
        TROLL("T", "Troll", 0);
        
        private final String abbreviation;
        private final String displayName;
        private final int numericValue;
        
        Grade(String abbreviation, String displayName, int numericValue) {
            this.abbreviation = abbreviation;
            this.displayName = displayName;
            this.numericValue = numericValue;
        }
        
        public String getAbbreviation() { return abbreviation; }
        public String getDisplayName() { return displayName; }
        public int getNumericValue() { return numericValue; }
        
        public static Grade fromNumeric(int value) {
            for (Grade grade : values()) {
                if (grade.numericValue == value) {
                    return grade;
                }
            }
            return TROLL;
        }
    }
    
    /**
     * Exam subject.
     */
    public enum Subject {
        TRANSFIGURATION("Transfiguration"),
        CHARMS("Charms"),
        POTIONS("Potions"),
        HERBOLOGY("Herbology"),
        DEFENSE_AGAINST_DARK_ARTS("Defense Against the Dark Arts"),
        ASTRONOMY("Astronomy"),
        HISTORY_OF_MAGIC("History of Magic"),
        DIVINATION("Divination"),
        CARE_OF_MAGICAL_CREATURES("Care of Magical Creatures"),
        ANCIENT_RUNES("Ancient Runes"),
        ARITHMANCY("Arithmancy");
        
        private final String displayName;
        
        Subject(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    /**
     * Represents exam results for a player.
     */
    public record ExamResults(
        UUID playerId,
        Map<Subject, Grade> owlGrades,
        Map<Subject, Grade> newtGrades,
        long owlExamDate,
        long newtExamDate
    ) {}
    
    /**
     * Takes an O.W.L. exam.
     */
    public static ExamResults takeOWLExam(ServerPlayer player, Map<Subject, Grade> grades) {
        UUID playerId = player.getUUID();
        
        ExamResults results = examResults.getOrDefault(playerId, new ExamResults(
            playerId,
            new HashMap<>(),
            new HashMap<>(),
            0,
            0
        ));
        
        ExamResults newResults = new ExamResults(
            playerId,
            grades,
            results.newtGrades(),
            System.currentTimeMillis(),
            results.newtExamDate()
        );
        
        examResults.put(playerId, newResults);
        return newResults;
    }
    
    /**
     * Takes a N.E.W.T. exam.
     * Requires O.W.L. grade of at least Acceptable in the subject.
     */
    public static ExamResults takeNEWTExam(ServerPlayer player, Map<Subject, Grade> grades) {
        UUID playerId = player.getUUID();
        
        ExamResults results = examResults.getOrDefault(playerId, new ExamResults(
            playerId,
            new HashMap<>(),
            new HashMap<>(),
            0,
            0
        ));
        
        // Check prerequisites
        for (Subject subject : grades.keySet()) {
            Grade owlGrade = results.owlGrades().get(subject);
            if (owlGrade == null || owlGrade.getNumericValue() < 3) { // Less than Acceptable
                return null; // Prerequisite not met
            }
        }
        
        ExamResults newResults = new ExamResults(
            playerId,
            results.owlGrades(),
            grades,
            results.owlExamDate(),
            System.currentTimeMillis()
        );
        
        examResults.put(playerId, newResults);
        return newResults;
    }
    
    /**
     * Gets exam results for a player.
     */
    public static ExamResults getExamResults(ServerPlayer player) {
        return examResults.get(player.getUUID());
    }
    
    /**
     * Creates a certificate item for exam results.
     */
    public static ItemStack createCertificate(ServerPlayer player, ExamType examType, Subject subject) {
        ExamResults results = getExamResults(player);
        if (results == null) {
            return ItemStack.EMPTY;
        }
        
        Grade grade = examType == ExamType.OWL 
            ? results.owlGrades().get(subject)
            : results.newtGrades().get(subject);
        
        if (grade == null) {
            return ItemStack.EMPTY;
        }
        
        // TODO: Create certificate item with exam data
        // For now, return empty stack
        return ItemStack.EMPTY;
    }
    
    /**
     * Checks if a player has the required grade for a career path.
     */
    public static boolean hasRequiredGrade(ServerPlayer player, Subject subject, Grade minGrade, ExamType examType) {
        ExamResults results = getExamResults(player);
        if (results == null) {
            return false;
        }
        
        Grade playerGrade = examType == ExamType.OWL
            ? results.owlGrades().get(subject)
            : results.newtGrades().get(subject);
        
        return playerGrade != null && playerGrade.getNumericValue() >= minGrade.getNumericValue();
    }
}
















