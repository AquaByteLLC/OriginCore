package commons.versioning.api;

public interface Versioned<T> {
	T legacy();
	T nonLegacy();
}
