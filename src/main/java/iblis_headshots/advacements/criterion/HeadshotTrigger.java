package iblis_headshots.advacements.criterion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import iblis_headshots.IblisHeadshotsMod;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

public class HeadshotTrigger implements ICriterionTrigger<HeadshotTrigger.Instance> {

	private static final ResourceLocation ID = new ResourceLocation(IblisHeadshotsMod.MODID, "headshot");
	private final Map<PlayerAdvancements, HeadshotTrigger.Listeners> listeners = Maps.<PlayerAdvancements, HeadshotTrigger.Listeners>newHashMap();
	public static HeadshotTrigger instance;
	
	public HeadshotTrigger() {
		instance=this;
	}

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn,
			HeadshotTrigger.Listener<Instance> listener) {
		HeadshotTrigger.Listeners listeners = this.listeners
				.get(playerAdvancementsIn);

		if (listeners == null) {
			listeners = new HeadshotTrigger.Listeners(playerAdvancementsIn);
			this.listeners.put(playerAdvancementsIn, listeners);
		}

		listeners.add(listener);
	}

    public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<HeadshotTrigger.Instance> listener)
    {
        HeadshotTrigger.Listeners playerhurtentitytrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (playerhurtentitytrigger$listeners != null)
        {
            playerhurtentitytrigger$listeners.remove(listener);

            if (playerhurtentitytrigger$listeners.isEmpty())
            {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
    {
        this.listeners.remove(playerAdvancementsIn);
    }
    
	public void trigger(EntityPlayerMP player, Entity target) {
		HeadshotTrigger.Listeners levitationtrigger$listeners = this.listeners.get(player.getAdvancements());

		if (levitationtrigger$listeners != null) {
			levitationtrigger$listeners.trigger(player, target);
		}
	}

	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        EntityPredicate targetPredicate = EntityPredicate.deserialize(json.get("target"));
        return new HeadshotTrigger.Instance(targetPredicate);
	}

	public static class Instance extends AbstractCriterionInstance {
		private final EntityPredicate target;

		public Instance(EntityPredicate target) {
			super(HeadshotTrigger.ID);
			this.target = target;
		}

		public boolean test(EntityPlayerMP player, Entity targetEntity) {
			return this.target.test(player, targetEntity);
		}
	}

	static class Listeners {
		private final PlayerAdvancements playerAdvancements;
		private final Set<ICriterionTrigger.Listener<HeadshotTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<HeadshotTrigger.Instance>>newHashSet();

		public Listeners(PlayerAdvancements playerAdvancementsIn) {
			this.playerAdvancements = playerAdvancementsIn;
		}

		public boolean isEmpty() {
			return this.listeners.isEmpty();
		}

		public void add(ICriterionTrigger.Listener<HeadshotTrigger.Instance> listener) {
			this.listeners.add(listener);
		}

		public void remove(ICriterionTrigger.Listener<HeadshotTrigger.Instance> listener) {
			this.listeners.remove(listener);
		}

		public void trigger(EntityPlayerMP player, Entity targetEntity) {
			List<ICriterionTrigger.Listener<HeadshotTrigger.Instance>> list = null;

			for (ICriterionTrigger.Listener<HeadshotTrigger.Instance> listener : this.listeners) {
				if (((HeadshotTrigger.Instance) listener.getCriterionInstance()).test(player, targetEntity)) {
					if (list == null) {
						list = Lists.<ICriterionTrigger.Listener<HeadshotTrigger.Instance>>newArrayList();
					}

					list.add(listener);
				}
			}

			if (list != null) {
				for (ICriterionTrigger.Listener<HeadshotTrigger.Instance> listener1 : list) {
					listener1.grantCriterion(this.playerAdvancements);
				}
			}
		}
	}
}
