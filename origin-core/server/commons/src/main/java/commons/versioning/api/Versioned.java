package commons.versioning.api;

public interface Versioned<T> {
	T getLegacy();
	T getNonLegacy();
}
