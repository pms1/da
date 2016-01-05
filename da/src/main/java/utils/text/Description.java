package utils.text;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class Description {
	private static final String eol = System.lineSeparator();
	private static final Splitter eolSplitter = Splitter.on(eol);

	private static final String indent = "  ";
	private String prefix = "";
	private StringBuilder text = new StringBuilder();

	private final IdentityHashMap<Object, String> map;

	public Description() {
		this(new IdentityHashMap<>());

	}

	private Description(IdentityHashMap<Object, String> map) {
		this.map = map;
	}

	private void increase() {
		prefix += indent;
	}

	private void decrease() {
		prefix = prefix.substring(indent.length());
	}

	public Description withValue(String key, Object value) {
		// String old = map.get(value);
		// if (old != null)
		// throw new Error();
		Description d = new Description(map);
		d.describe(value);
		appendMultiLine(key + " = ", d.toString());
		return this;
	}

	public Description withList(String key, List<?> analyses) {
		text.append(prefix).append(key).append(" = [").append(eol);

		increase();
		for (int i = 0; i != analyses.size(); ++i) {
			// String old = map.get(analyses.get(i));
			// if (old != null)
			// throw new Error();

			Description d = new Description(map);
			d.describe(analyses.get(i));
			appendMultiLine("#" + i + ": ", d.toString());
		}
		decrease();
		text.append(prefix).append("]").append(eol);
		return this;
	}

	private void appendMultiLine(String key, String description) {
		LinkedList<String> split = Lists.newLinkedList(eolSplitter.split(description));

		if (split.size() > 1) {
			String last = split.getLast();
			if (last.isEmpty())
				split.removeLast();
		}

		Iterator<String> is = split.iterator();

		text.append(prefix).append(key).append(is.next()).append(eol);
		if (is.hasNext()) {
			String indent = Strings.repeat(" ", key.length());

			is.forEachRemaining((l) -> text.append(prefix).append(indent).append(l).append(eol));
		}
	}

	public Description describe(Object analysis) {
		String old = map.get(analysis);
		if (old != null) {
			text.append(prefix).append(analysis.getClass().getCanonicalName()).append(" (-> ").append(old).append(")")
					.append(eol);
			return this;
		}
		if (analysis instanceof Describable) {
			String id = "id " + map.size();
			map.put(analysis, id);

			text.append(prefix).append(analysis.getClass().getCanonicalName()).append(" (").append(id).append(")")
					.append(eol);
			increase();
			((Describable) analysis).describe(this);
			decrease();

		} else {
			appendMultiLine("", String.valueOf(analysis));
		}

		return this;
	}

	@Override
	public String toString() {
		return text.toString();
	}
}
