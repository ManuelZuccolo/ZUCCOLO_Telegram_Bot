package org.example;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Monster {

    public String index;
    public String name;
    public String size;
    public String type;
    public String subtype;
    public String alignment;

    @JsonProperty("armor_class")
    public List<ArmorClass> armorClass;

    @JsonProperty("hit_points")
    public int hitPoints;

    @JsonProperty("hit_dice")
    public String hitDice;

    @JsonProperty("hit_points_roll")
    public String hitPointsRoll;

    public Map<String, String> speed;
    public int strength;
    public int dexterity;
    public int constitution;
    public int intelligence;
    public int wisdom;
    public int charisma;

    public List<ProficiencyWrapper> proficiencies;

    @JsonProperty("damage_vulnerabilities")
    public List<Object> damageVulnerabilities;

    @JsonProperty("damage_resistances")
    public List<Object> damageResistances;

    @JsonProperty("damage_immunities")
    public List<Object> damageImmunities;

    @JsonProperty("condition_immunities")
    public List<Object> conditionImmunities;

    public Senses senses;
    public String languages;

    @JsonProperty("challenge_rating")
    public double challengeRating;

    @JsonProperty("proficiency_bonus")
    public int proficiencyBonus;

    public int xp;

    @JsonProperty("special_abilities")
    public List<SpecialAbility> specialAbilities;

    public List<Action> actions;
    public List<Action> legendary_actions;
    public List<Action> reactions;
    public List<Form> forms;
    public String image;
    public String url;

    @JsonProperty("updated_at")
    public String updatedAt;

    @JsonAnySetter
    public Map<String, Object> extraFields = new HashMap<>();

    // ---- CLASSI ANNIDATE ----
    public static class ArmorClass {
        public String type;
        public int value;
        public List<Armor> armor;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Armor {
        public String index;
        public String name;
        public String url;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class ProficiencyWrapper {
        public int value;
        public Proficiency proficiency;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Proficiency {
        public String index;
        public String name;
        public String url;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Senses {
        public String blindsight;
        public String darkvision;
        public String tremorsense;
        public String truesight;
        public String superior_senses;
        public int passive_perception;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class SpecialAbility {
        public String name;
        public String desc;
        public List<Damage> damage;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Action {
        public String name;
        public String desc;
        public Integer attack_bonus;
        public String multiattack_type;
        public String action_name;
        public List<Damage> damage;
        public List<Action> actions;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Damage {
        @JsonProperty("damage_type")
        public DamageType damageType;
        @JsonProperty("damage_dice")
        public String damageDice;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class DamageType {
        public String index;
        public String name;
        public String url;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    public static class Form {
        public String index;
        public String name;
        public String url;
        @JsonAnySetter public Map<String,Object> extraFields = new HashMap<>();
    }

    private String formatNames(List<Object> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Map) {
                Map<?,?> map = (Map<?,?>) o;
                Object name = map.get("name");
                sb.append(name != null ? name.toString() : "Unknown");
            } else {
                sb.append(o.toString());
            }
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }



    // ---- METODO LEGGBILE ----
    public String toReadableString() {
        StringBuilder sb = new StringBuilder();
        sb.append("🛡️ Name: ").append(name).append("\n");
        sb.append("Index: ").append(index).append("\n");
        sb.append("Size: ").append(size).append(", Type: ").append(type)
                .append(subtype != null && !subtype.isEmpty() ? " (" + subtype + ")" : "")
                .append("\n");
        sb.append("Alignment: ").append(alignment).append("\n");

        // Armor class
        sb.append("Armor Class: ");
        if (armorClass != null) for (ArmorClass ac : armorClass)
            sb.append(ac.value).append(" (").append(ac.type).append(") ");
        sb.append("\n");

        sb.append("Hit Points: ").append(hitPoints).append(" (").append(hitDice).append(")\n");

        // Speed
        if (speed != null) {
            sb.append("Speed: ");
            speed.forEach((k,v)->sb.append(k).append(": ").append(v).append(" "));
            sb.append("\n");
        }

        // Stats
        sb.append("STR: ").append(strength)
                .append(", DEX: ").append(dexterity)
                .append(", CON: ").append(constitution)
                .append(", INT: ").append(intelligence)
                .append(", WIS: ").append(wisdom)
                .append(", CHA: ").append(charisma).append("\n");

        // Proficiencies
        if (proficiencies != null) {
            sb.append("Proficiencies: ");
            for (ProficiencyWrapper pw : proficiencies)
                sb.append(pw.proficiency.name).append(" +").append(pw.value).append("; ");
            sb.append("\n\n");
        }

        // Damage & Condition
        sb.append("Damage Vulnerabilities: ").append(formatNames(damageVulnerabilities)).append("\n");
        sb.append("Damage Resistances: ").append(formatNames(damageResistances)).append("\n");
        sb.append("Damage Immunities: ").append(formatNames(damageImmunities)).append("\n");
        sb.append("Condition Immunities: ").append(formatNames(conditionImmunities)).append("\n\n");

        // Senses
        if (senses != null) {
            sb.append("Senses:\n");
            if (senses.blindsight != null) sb.append(" - Blindsight: ").append(senses.blindsight).append("\n");
            if (senses.darkvision != null) sb.append(" - Darkvision: ").append(senses.darkvision).append("\n");
            if (senses.tremorsense != null) sb.append(" - Tremorsense: ").append(senses.tremorsense).append("\n");
            if (senses.truesight != null) sb.append(" - Truesight: ").append(senses.truesight).append("\n");
            if (senses.superior_senses != null) sb.append(" - Superior Senses: ").append(senses.superior_senses).append("\n");
            sb.append(" - Passive Perception: ").append(senses.passive_perception).append("\n");
        }

        sb.append("Languages: ").append(languages).append("\n");
        sb.append("Challenge Rating: ").append(challengeRating)
                .append(", Proficiency Bonus: +").append(proficiencyBonus)
                .append(", XP: ").append(xp).append("\n\n");

        // Special Abilities
        if (specialAbilities != null) {
            sb.append("Special Abilities:\n");
            for (SpecialAbility sa : specialAbilities)
                sb.append(" - ").append(sa.name).append(": ").append(sa.desc).append("\n");
        }

        // Actions
        if (actions != null) {
            sb.append("Actions:\n");
            for (Action a : actions)
                sb.append(" - ").append(a.name).append(": ").append(a.desc).append("\n");
        }

        // Legendary Actions
        if (legendary_actions != null) {
            sb.append("Legendary Actions:\n");
            for (Action la : legendary_actions)
                sb.append(" - ").append(la.name).append(": ").append(la.desc).append("\n");
        }

        // Reactions
        if (reactions != null) {
            sb.append("Reactions:\n");
            for (Action r : reactions)
                sb.append(" - ").append(r.name).append(": ").append(r.desc).append("\n");
        }

        sb.append("\nForms: ").append(forms).append("\n");
        sb.append("Image: ").append(image).append("\n");
        sb.append("URL: ").append(url).append("\n");
        sb.append("Updated at: ").append(updatedAt).append("\n");

        return sb.toString();
    }
}
