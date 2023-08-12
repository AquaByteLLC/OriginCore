package tools.impl.attribute;

import java.util.concurrent.TimeUnit;

public interface ExpiringAttribute extends BaseAttribute {
	TimeUnit getTimeUnit();

	long getAmount();
}
