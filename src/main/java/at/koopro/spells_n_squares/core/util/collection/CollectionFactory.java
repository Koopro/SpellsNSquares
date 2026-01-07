package at.koopro.spells_n_squares.core.util.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Factory utility for creating collections with consistent patterns.
 * Reduces boilerplate and provides a centralized way to create collections.
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Instead of: new ArrayList<>()
 * List<String> list = CollectionFactory.createList();
 * 
 * // Instead of: new ArrayList<>(10)
 * List<String> list = CollectionFactory.createList(10);
 * 
 * // Instead of: new HashMap<>()
 * Map<String, Integer> map = CollectionFactory.createMap();
 * 
 * // Pre-populated list
 * List<String> list = CollectionFactory.createListOf("a", "b", "c");
 * }</pre>
 */
public final class CollectionFactory {
    private CollectionFactory() {
        // Utility class - prevent instantiation
    }
    
    // ========== List Factory Methods ==========
    
    /**
     * Creates a new ArrayList.
     * 
     * @param <T> The element type
     * @return A new ArrayList
     */
    public static <T> List<T> createList() {
        return new ArrayList<>();
    }
    
    /**
     * Creates a new ArrayList with the specified initial capacity.
     * 
     * @param <T> The element type
     * @param initialCapacity The initial capacity
     * @return A new ArrayList with the specified capacity
     */
    public static <T> List<T> createList(int initialCapacity) {
        return new ArrayList<>(initialCapacity);
    }
    
    /**
     * Creates a new ArrayList pre-populated with the given elements.
     * 
     * @param <T> The element type
     * @param elements The elements to add
     * @return A new ArrayList containing the elements
     */
    @SafeVarargs
    public static <T> List<T> createListOf(T... elements) {
        if (elements == null || elements.length == 0) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>(elements.length);
        for (T element : elements) {
            if (element != null) {
                list.add(element);
            }
        }
        return list;
    }
    
    /**
     * Creates a new ArrayList from a collection.
     * 
     * @param <T> The element type
     * @param collection The collection to copy
     * @return A new ArrayList containing the collection elements
     */
    public static <T> List<T> createListFrom(java.util.Collection<T> collection) {
        if (collection == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(collection);
    }
    
    // ========== Map Factory Methods ==========
    
    /**
     * Creates a new HashMap.
     * 
     * @param <K> The key type
     * @param <V> The value type
     * @return A new HashMap
     */
    public static <K, V> Map<K, V> createMap() {
        return new HashMap<>();
    }
    
    /**
     * Creates a new HashMap with the specified initial capacity.
     * 
     * @param <K> The key type
     * @param <V> The value type
     * @param initialCapacity The initial capacity
     * @return A new HashMap with the specified capacity
     */
    public static <K, V> Map<K, V> createMap(int initialCapacity) {
        return new HashMap<>(initialCapacity);
    }
    
    /**
     * Creates a new LinkedHashMap.
     * 
     * @param <K> The key type
     * @param <V> The value type
     * @return A new LinkedHashMap
     */
    public static <K, V> Map<K, V> createLinkedMap() {
        return new LinkedHashMap<>();
    }
    
    /**
     * Creates a new LinkedHashMap with the specified initial capacity.
     * 
     * @param <K> The key type
     * @param <V> The value type
     * @param initialCapacity The initial capacity
     * @return A new LinkedHashMap with the specified capacity
     */
    public static <K, V> Map<K, V> createLinkedMap(int initialCapacity) {
        return new LinkedHashMap<>(initialCapacity);
    }
    
    // ========== Set Factory Methods ==========
    
    /**
     * Creates a new HashSet.
     * 
     * @param <T> The element type
     * @return A new HashSet
     */
    public static <T> Set<T> createSet() {
        return new HashSet<>();
    }
    
    /**
     * Creates a new HashSet with the specified initial capacity.
     * 
     * @param <T> The element type
     * @param initialCapacity The initial capacity
     * @return A new HashSet with the specified capacity
     */
    public static <T> Set<T> createSet(int initialCapacity) {
        return new HashSet<>(initialCapacity);
    }
    
    /**
     * Creates a new HashSet pre-populated with the given elements.
     * 
     * @param <T> The element type
     * @param elements The elements to add
     * @return A new HashSet containing the elements
     */
    @SafeVarargs
    public static <T> Set<T> createSetOf(T... elements) {
        if (elements == null || elements.length == 0) {
            return new HashSet<>();
        }
        Set<T> set = new HashSet<>(elements.length);
        for (T element : elements) {
            if (element != null) {
                set.add(element);
            }
        }
        return set;
    }
    
    /**
     * Creates a new HashSet from a collection.
     * 
     * @param <T> The element type
     * @param collection The collection to copy
     * @return A new HashSet containing the collection elements
     */
    public static <T> Set<T> createSetFrom(java.util.Collection<T> collection) {
        if (collection == null) {
            return new HashSet<>();
        }
        return new HashSet<>(collection);
    }
    
    /**
     * Creates a new LinkedHashSet.
     * 
     * @param <T> The element type
     * @return A new LinkedHashSet
     */
    public static <T> Set<T> createLinkedSet() {
        return new LinkedHashSet<>();
    }
    
    /**
     * Creates a new LinkedHashSet with the specified initial capacity.
     * 
     * @param <T> The element type
     * @param initialCapacity The initial capacity
     * @return A new LinkedHashSet with the specified capacity
     */
    public static <T> Set<T> createLinkedSet(int initialCapacity) {
        return new LinkedHashSet<>(initialCapacity);
    }
}


