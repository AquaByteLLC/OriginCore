package commons.entity.registry;

import com.google.common.collect.ConcurrentHashMultiset;
import commons.entity.EntityHelper;
import lombok.Getter;

public class EntityRegistry {
	@Getter
	private final ConcurrentHashMultiset<EntityHelper.EntityWrapper<?>> entities;

	public EntityRegistry() {
		entities = ConcurrentHashMultiset.create();
	}
}
