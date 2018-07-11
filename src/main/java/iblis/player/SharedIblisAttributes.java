package iblis.player;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class SharedIblisAttributes {

    public static final UUID ATTACK_DAMAGE_BY_SKILL_MODIFIER = UUID.fromString("73B1E248-77C17F6F-857-857-9F5F2D7");
	public static final UUID ATTACK_DAMAGE_BY_CHARACTERISTIC_MODIFIER = UUID.fromString("73B1E248-77C17F6F-857-C4A7AC7-9F5F2D7");
    public static final UUID BULLET_DAMAGE_MODIFIER = UUID.fromString("75715902-77C17F6F-857-519C4CDF-9F5F2D7");
    public static final UUID ARROW_DAMAGE_MODIFIER = UUID.fromString("73B1E248-77C17F6F-857-519C4CDF-9F5F2D7");
    public static final UUID ARMOR_BY_QUALITY_MODIFIER = UUID.fromString("3BA691F-4DA9951-857-519C4CDF-9F5F2D7");
    public static final UUID ARMOR_TOUGHNESS_BY_QUALITY_MODIFIER = UUID.fromString("3BA691F-6D892610-857-519C4CDF-9F5F2D7");
    public static final UUID SPRINTING_SPEED_MODIFIER = UUID.fromString("5719796-57EED-AA0D1F1E7-A3-C4A7AC791571C");
	public static final UUID SHIELD_RUNNING_MODIFIER = UUID.fromString("51E76-57EED-AA0D1F1E7-7E4-E9");
	public static final UUID EQUILIBRIUM_KNOCKBACK_MODIFIER = UUID.fromString("FFFFFFFF-FF62-BD91-0000-00001580FE92");
    
    public static final IAttribute MELEE_DAMAGE_BONUS = (new RangedAttribute((IAttribute)null, "iblis.melee_damage_bonus", 1.0D, 0.0D, Double.MAX_VALUE)).setDescription("Melee damage bonus").setShouldWatch(true);
    public static final IAttribute PROJECTILE_DAMAGE = (new RangedAttribute((IAttribute)null, "iblis.projectile_damage", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Projectile damage").setShouldWatch(true);
    public static final IAttribute MELEE_DAMAGE_REDUCTION = (new RangedAttribute((IAttribute)null, "iblis.melee_damage_reduction", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Incoming melee damage reduction").setShouldWatch(true);
    public static final IAttribute EXPLOSION_DAMAGE_REDUCTION = (new RangedAttribute((IAttribute)null, "iblis.explosion_damage_reduction", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Incoming explosion damage reduction").setShouldWatch(true);
    public static final IAttribute FIRE_DAMAGE_REDUCTION = (new RangedAttribute((IAttribute)null, "iblis.fire_damage_reduction", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Incoming fire damage reduction").setShouldWatch(true);
    public static final IAttribute PROJECTILE_DAMAGE_REDUCTION = (new RangedAttribute((IAttribute)null, "iblis.projectile_damage_reduction", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Incoming projectile damage reduction").setShouldWatch(true);
    public static final IAttribute INTELLIGENCE = (new RangedAttribute((IAttribute)null, "iblis.intelligence", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Intelligence").setShouldWatch(true);
    public static final IAttribute GLUTTONY = (new RangedAttribute((IAttribute)null, "iblis.gluttony", 20.0D, 0.0D, Double.MAX_VALUE)).setDescription("Gluttony").setShouldWatch(true);
    
    // Skills
    public static final IAttribute WISDOM = (new RangedAttribute(INTELLIGENCE, "iblis.wisdom", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Wisdom").setShouldWatch(true);
    
    public static final IAttribute MARTIAL_ARTS = (new RangedAttribute(WISDOM, "iblis.martial_arts", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Combat mastery").setShouldWatch(true);
    public static final IAttribute SWORDSMANSHIP = (new RangedAttribute(MARTIAL_ARTS, "iblis.swordsmanship", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Sword mastery").setShouldWatch(true);
    public static final IAttribute PARRY = (new RangedAttribute(MARTIAL_ARTS, "iblis.parry", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Parry").setShouldWatch(true);
    public static final IAttribute ARCHERY = (new RangedAttribute(MARTIAL_ARTS, "iblis.archery", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Archery").setShouldWatch(true);
    public static final IAttribute THROWING = (new RangedAttribute(MARTIAL_ARTS, "iblis.throwing", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Throwing").setShouldWatch(true);
    public static final IAttribute SHARPSHOOTING = (new RangedAttribute(MARTIAL_ARTS, "iblis.sharpshooting", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Sharpshooting").setShouldWatch(true);
    
    public static final IAttribute CRAFTMANSHIP = (new RangedAttribute(WISDOM, "iblis.craftmanship", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Craftmanship").setShouldWatch(true);
    public static final IAttribute WEAPONSMITH = (new RangedAttribute(CRAFTMANSHIP, "iblis.weaponsmith", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Weaponsmith").setShouldWatch(true);
    public static final IAttribute ARMORSMITH = (new RangedAttribute(CRAFTMANSHIP, "iblis.armorsmith", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Armorsmith").setShouldWatch(true);
    public static final IAttribute MECHANICS = (new RangedAttribute(CRAFTMANSHIP, "iblis.mechanics", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Mechanics").setShouldWatch(true);
	public static final IAttribute MEDICAL_AID = (new RangedAttribute(CRAFTMANSHIP, "iblis.medical_aid", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Medical aid").setShouldWatch(true);
	public static final IAttribute DIGGING = (new RangedAttribute(CRAFTMANSHIP, "iblis.digging", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Digging").setShouldWatch(true);
	public static final IAttribute TAMING = (new RangedAttribute(CRAFTMANSHIP, "iblis.taming", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Taming").setShouldWatch(true);
	public static final IAttribute CHEMISTRY = (new RangedAttribute(CRAFTMANSHIP, "iblis.chemistry", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Chemistry").setShouldWatch(true);
    
    public static final IAttribute ACROBATICS = (new RangedAttribute(WISDOM, "iblis.acrobatics", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Acrobatics").setShouldWatch(true);
    public static final IAttribute RUNNING = (new RangedAttribute(ACROBATICS, "iblis.running", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Running").setShouldWatch(true);
    public static final IAttribute JUMPING = (new RangedAttribute(ACROBATICS, "iblis.jumping", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Jumping").setShouldWatch(true);
	public static final IAttribute FALLING = (new RangedAttribute(ACROBATICS, "iblis.falling", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Falling").setShouldWatch(true);
	public static final IAttribute EQUILIBRIUM = (new RangedAttribute(ACROBATICS, "iblis.equilibrium", 0.0D, 0.0D, Double.MAX_VALUE)).setDescription("Knockback resitance").setShouldWatch(true);
    
	public static void registerAttributes(AbstractAttributeMap attributeMap) {
		attributeMap.registerAttribute(MELEE_DAMAGE_BONUS);
		attributeMap.registerAttribute(PROJECTILE_DAMAGE);
		attributeMap.registerAttribute(MELEE_DAMAGE_REDUCTION);
		attributeMap.registerAttribute(EXPLOSION_DAMAGE_REDUCTION);
		attributeMap.registerAttribute(FIRE_DAMAGE_REDUCTION);
		attributeMap.registerAttribute(PROJECTILE_DAMAGE_REDUCTION);
		attributeMap.registerAttribute(INTELLIGENCE);
		attributeMap.registerAttribute(GLUTTONY);
		attributeMap.registerAttribute(WISDOM);
		attributeMap.registerAttribute(MARTIAL_ARTS);
		attributeMap.registerAttribute(SWORDSMANSHIP);
		attributeMap.registerAttribute(PARRY);
		attributeMap.registerAttribute(ARCHERY);
		attributeMap.registerAttribute(THROWING);
		attributeMap.registerAttribute(SHARPSHOOTING);
		attributeMap.registerAttribute(CRAFTMANSHIP);
		attributeMap.registerAttribute(WEAPONSMITH);
		attributeMap.registerAttribute(ARMORSMITH);
		attributeMap.registerAttribute(MECHANICS);
		attributeMap.registerAttribute(MEDICAL_AID);
		attributeMap.registerAttribute(DIGGING);
		attributeMap.registerAttribute(TAMING);
		attributeMap.registerAttribute(CHEMISTRY);
		attributeMap.registerAttribute(ACROBATICS);
		attributeMap.registerAttribute(RUNNING);
		attributeMap.registerAttribute(JUMPING);
		attributeMap.registerAttribute(FALLING);
		attributeMap.registerAttribute(EQUILIBRIUM);
	}
}
