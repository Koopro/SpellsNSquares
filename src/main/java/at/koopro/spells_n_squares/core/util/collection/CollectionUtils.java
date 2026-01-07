package at.koopro.spells_n_squares.core.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for common collection operations and helpers.
 * Provides safe access, filtering, mapping, and other collection operations.
 */
public final class CollectionUtils {
    private CollectionUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Safely gets an element from a list by index.
     * 
     * @param list The list
     * @param index The index
     * @return Optional containing the element, or empty if out of bounds
     */
    public static <T> Optional<T> safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(index));
    }
    
    /**
     * Safely gets an element from an array by index.
     * 
     * @param array The array
     * @param index The index
     * @return Optional containing the element, or empty if out of bounds
     */
    public static <T> Optional<T> safeGet(T[] array, int index) {
        if (array == null || index < 0 || index >= array.length) {
            return Optional.empty();
        }
        return Optional.ofNullable(array[index]);
    }
    
    /**
     * Filters a collection based on a predicate.
     * 
     * @param collection The collection to filter
     * @param predicate The filter predicate
     * @return A new list containing filtered elements
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return Collections.emptyList();
        }
        return collection.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    /**
     * Maps a collection to a new collection using a function.
     * 
     * @param collection The collection to map
     * @param mapper The mapping function
     * @return A new list containing mapped elements
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        if (collection == null || mapper == null) {
            return Collections.emptyList();
        }
        return collection.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
    
    /**
     * Partitions a collection into two lists based on a predicate.
     * 
     * @param collection The collection to partition
     * @param predicate The partition predicate
     * @return A pair of lists: [matching, non-matching]
     */
    public static <T> List<List<T>> partition(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return List.of(Collections.emptyList(), Collections.emptyList());
        }
        List<T> matching = CollectionFactory.createList();
        List<T> nonMatching = CollectionFactory.createList();
        for (T item : collection) {
            if (predicate.test(item)) {
                matching.add(item);
            } else {
                nonMatching.add(item);
            }
        }
        return List.of(matching, nonMatching);
    }
    
    /**
     * Merges multiple collections into a single list.
     * 
     * @param collections The collections to merge
     * @return A new list containing all elements
     */
    @SafeVarargs
    public static <T> List<T> merge(Collection<T>... collections) {
        if (collections == null || collections.length == 0) {
            return Collections.emptyList();
        }
        List<T> result = CollectionFactory.createList();
        for (Collection<T> collection : collections) {
            if (collection != null) {
                result.addAll(collection);
            }
        }
        return result;
    }
    
    /**
     * Checks if a collection is null or empty.
     * 
     * @param collection The collection to check
     * @return true if null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * Checks if a collection is not null and not empty.
     * 
     * @param collection The collection to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * Converts a collection to an array.
     * 
     * @param collection The collection to convert
     * @param arrayClass The array component class
     * @return An array containing the collection elements
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> collection, Class<T> arrayClass) {
        if (collection == null || arrayClass == null) {
            return (T[]) java.lang.reflect.Array.newInstance(arrayClass, 0);
        }
        return collection.toArray((T[]) java.lang.reflect.Array.newInstance(arrayClass, collection.size()));
    }
    
    /**
     * Gets the first element from a collection, or empty if null/empty.
     * 
     * @param collection The collection
     * @return Optional containing the first element
     */
    public static <T> Optional<T> first(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Optional.empty();
        }
        return Optional.ofNullable(collection.iterator().next());
    }
    
    /**
     * Gets the last element from a list, or empty if null/empty.
     * 
     * @param list The list
     * @return Optional containing the last element
     */
    public static <T> Optional<T> last(List<T> list) {
        if (isEmpty(list)) {
            return Optional.empty();
        }
        return Optional.ofNullable(list.get(list.size() - 1));
    }
    
    /**
     * Checks if a collection contains any element matching a predicate.
     * 
     * @param collection The collection
     * @param predicate The predicate
     * @return true if any element matches
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return false;
        }
        return collection.stream().anyMatch(predicate);
    }
    
    /**
     * Checks if a collection contains all elements matching a predicate.
     * 
     * @param collection The collection
     * @param predicate The predicate
     * @return true if all elements match
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return false;
        }
        return collection.stream().allMatch(predicate);
    }
    
    /**
     * Counts elements in a collection matching a predicate.
     * 
     * @param collection The collection
     * @param predicate The predicate
     * @return The count of matching elements
     */
    public static <T> long count(Collection<T> collection, Predicate<T> predicate) {
        if (collection == null || predicate == null) {
            return 0;
        }
        return collection.stream().filter(predicate).count();
    }
    
    /**
     * Creates an unmodifiable copy of a collection.
     * 
     * @param collection The collection to copy
     * @return An unmodifiable list copy
     */
    public static <T> List<T> unmodifiableCopy(Collection<T> collection) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(CollectionFactory.createListFrom(collection));
    }
}

