package utils.collect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Collectors2 {

	private static <K, V> BinaryOperator<? extends Multimap<K, V>> mergeMultimap() {
		return (t, u) -> {
			t.putAll(u);
			return t;
		};
	}

	public static <T, K, U, M extends Multimap<K, U>> Collector<T, ?, M> toMultimap(
			Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper,
			Supplier<M> mapSupplier) {
		return Collector.of(mapSupplier,
				(map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element)), mergeMultimap(),
				Characteristics.IDENTITY_FINISH);
	}

	public static <T, K, U, M extends Multimap<K, U>> Collector<T, ?, M> toMultimapFlatened(
			Function<? super T, ? extends Stream<? extends K>> keyMapper, Function<? super T, ? extends U> valueMapper,
			Supplier<M> mapSupplier) {
		return Collector.of(mapSupplier, (map, element) -> {
			U v = valueMapper.apply(element);
			keyMapper.apply(element).forEach((k) -> map.put(k, v));
		} , mergeMultimap(), Characteristics.IDENTITY_FINISH);
	}

	public static <T, K, U> Collector<T, ?, HashMultimap<K, U>> toMultimapFlatened(
			Function<? super T, ? extends Stream<? extends K>> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return toMultimapFlatened(keyMapper, valueMapper, HashMultimap::create);
	}

	public static <T, K, U> Collector<T, ?, HashMultimap<K, U>> toMultimap(Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return toMultimap(keyMapper, valueMapper, HashMultimap::create);
	}

	class X {
		List<Integer> key;
		String value;

		List<Integer> getKey() {
			return key;
		}
	}

	public static void main(String[] args) {
		Integer only = Arrays.asList(1).stream().collect(findOnly());
		assert only == 1;
		try {
			only = Arrays.asList(0, 1).stream().collect(findOnly());
			throw new Error();
		} catch (IllegalArgumentException e) {

		}
		try {
			only = Collections.<Integer> emptyList().stream().collect(findOnly());
			throw new Error();
		} catch (NoSuchElementException e) {

		}

		Multimap<Integer, String> mm = new Random(0).ints(100, 1, 50).boxed()
				.collect(toMultimap((i) -> i % 10, (i) -> i.toString(), HashMultimap::create));

		List<X> x = null;

		Multimap<Integer, String> mm2 = x.stream()
				.collect(toMultimapFlatened((x1) -> x1.key.stream(), (x1) -> x1.value, HashMultimap::create));

	}

	@SuppressWarnings("unchecked")
	public static <T, K, U> Collector<T, ?, T> findOnly() {
		Supplier<Object[]> supplier = () -> {
			return new Object[1];
		};
		BiConsumer<Object[], T> accumulator = (k, t) -> {
			if (k[0] == null)
				k[0] = t;
			else
				throw new IllegalArgumentException("two or more");
		};
		BinaryOperator<Object[]> combiner = (k1, k2) -> {
			if (true)
				throw new Error();
			return null;
		};
		Function<Object[], T> finisher = (k) -> {
			if (k[0] == null)
				throw new NoSuchElementException();
			return (T) k[0];
		};
		return Collector.of(supplier, accumulator, combiner, finisher);
	}
}
