package generator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Ngram-base to Next-token mapping that keeps count of the identical Token values
 * to calculate word frequencies
 */
public class FrequencyHash<K, V> {

    //Helper class to count the occurrances of a single key
    class Counter<T> {
        Map<T, Integer> counterMap;
        int total;

        Counter() {
            counterMap = new HashMap<T, Integer>();
        }

        void add(T key) {
            Integer count = counterMap.get(key);
            counterMap.put(key, count == null ? 1 : count + 1);
            total++;
        }

        int getCount(T key) {
            Integer count = counterMap.get(key);
            return count == null ? 0 : count;
        }

        Collection<T> all() {
            return counterMap.keySet();
        }

        @Override
        public String toString() {
            String result = "";
            for (T key : counterMap.keySet()) {
                result += key.toString()+ " :";
                int n = counterMap.get(key);
                result += " " + n + "\t";
            }
            return result;
        }
    }


    Map<K, Counter<V>> fmap;
    int total;
    Random random = new Random();

    public FrequencyHash() {
        fmap = new HashMap<K, Counter<V>>();
    }

    void add(K key, V value) {
        Counter<V> counter = fmap.get(key);
        if (counter == null) counter = new Counter<V>();
        counter.add(value);
        fmap.put(key, counter);
        total += counter.total;
    }

    //Randomly samples the next word  according to its
    //frequency distribution (weight) next to the given base (key)
    public V sample(K key) {
        Counter<V> counter = fmap.get(key);
        if (counter == null) System.out.println(key);

        int nextFreq = random.nextInt(counter.total) + 1;

        int total = 0;
        V value = null;
        for (V v : counter.all()) {
            total += counter.getCount(v);
            if (total >= nextFreq) return v;
        }
        return value;
    }

    public Counter<V> getKey(K key) {
        return fmap.get(key);
    }

    public Collection<K> all() {
        return fmap.keySet();
    }

    @Override
    public String toString() {
        String result = "";
        for (K key : fmap.keySet()) {
            result += key + "\t" + fmap.get(key).toString();
            result += "\n";
        }
        return result;
    }
}
