package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true) // ignora campi non mappati
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
    public List<String> damageVulnerabilities;

    @JsonProperty("damage_resistances")
    public List<String> damageResistances;

    @JsonProperty("damage_immunities")
    public List<String> damageImmunities;

    @JsonProperty("condition_immunities")
    public List<String> conditionImmunities;

    public Senses senses;
    public String languages;

    @JsonProperty("challenge_rating")
    public double challengeRating;

    @JsonProperty("proficiency_bonus")
    public int proficiencyBonus;

    public int xp; // <--- mancava

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

    // CLASSI ANNIDATE
    public static class ArmorClass {
        public String type;
        public int value;
        public List<Armor> armor;
    }

    public static class Armor {
        public String index;
        public String name;
        public String url;
    }

    public static class ProficiencyWrapper {
        public int value;
        public Proficiency proficiency;
    }

    public static class Proficiency {
        public String index;
        public String name;
        public String url;
    }

    public static class Senses {
        public String darkvision;
        public int passive_perception;
    }

    public static class SpecialAbility {
        public String name;
        public String desc;
        public List<Damage> damage;
    }

    public static class Action {
        public String name;
        public String desc;
        public int attack_bonus;
        public List<Damage> damage;
        public List<Action> actions;
    }

    public static class Damage {
        @JsonProperty("damage_type")
        public DamageType damageType;
        @JsonProperty("damage_dice")
        public String damageDice;
    }

    public static class DamageType {
        public String index;
        public String name;
        public String url;
    }

    public static class Form {
        public String index;
        public String name;
        public String url;
    }

    // metodo leggibile
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
        if (armorClass != null && !armorClass.isEmpty()) {
            for (ArmorClass ac : armorClass) {
                sb.append(ac.value).append(" (").append(ac.type).append(") ");
            }
        }
        sb.append("\n");

        sb.append("Hit Points: ").append(hitPoints)
                .append(" (").append(hitDice).append(")\n");

        // Speed
        if (speed != null && !speed.isEmpty()) {
            sb.append("Speed: ");
            speed.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" "));
            sb.append("\n");
        }

        // Stats
        sb.append("STR: ").append(strength)
                .append(", DEX: ").append(dexterity)
                .append(", CON: ").append(constitution)
                .append(", INT: ").append(intelligence)
                .append(", WIS: ").append(wisdom)
                .append(", CHA: ").append(charisma)
                .append("\n");

        // Proficiencies
        if (proficiencies != null && !proficiencies.isEmpty()) {
            sb.append("Proficiencies: ");
            for (ProficiencyWrapper pw : proficiencies) {
                sb.append(pw.proficiency.name).append(" +").append(pw.value).append("; ");
            }
            sb.append("\n");
        }

        // Damage & Condition
        sb.append("Damage Vulnerabilities: ").append(damageVulnerabilities).append("\n");
        sb.append("Damage Resistances: ").append(damageResistances).append("\n");
        sb.append("Damage Immunities: ").append(damageImmunities).append("\n");
        sb.append("Condition Immunities: ").append(conditionImmunities).append("\n");

        // Senses
        if (senses != null) {
            sb.append("Senses: ");
            if (senses.darkvision != null) sb.append("Darkvision ").append(senses.darkvision).append("; ");
            sb.append("Passive Perception: ").append(senses.passive_perception).append("\n");
        }

        sb.append("Languages: ").append(languages).append("\n");
        sb.append("Challenge Rating: ").append(challengeRating)
                .append(", Proficiency Bonus: +").append(proficiencyBonus)
                .append(", XP: ").append(xp).append("\n");

        // Special Abilities
        if (specialAbilities != null && !specialAbilities.isEmpty()) {
            sb.append("Special Abilities:\n");
            for (SpecialAbility sa : specialAbilities) {
                sb.append(" - ").append(sa.name).append(": ").append(sa.desc).append("\n");
            }
        }

        // Actions
        if (actions != null && !actions.isEmpty()) {
            sb.append("Actions:\n");
            for (Action a : actions) {
                sb.append(" - ").append(a.name).append(": ").append(a.desc).append("\n");
            }
        }

        // Legendary Actions
        if (legendary_actions != null && !legendary_actions.isEmpty()) {
            sb.append("Legendary Actions:\n");
            for (Action la : legendary_actions) {
                sb.append(" - ").append(la.name).append(": ").append(la.desc).append("\n");
            }
        }

        // Reactions
        if (reactions != null && !reactions.isEmpty()) {
            sb.append("Reactions:\n");
            for (Action r : reactions) {
                sb.append(" - ").append(r.name).append(": ").append(r.desc).append("\n");
            }
        }

        sb.append("Forms: ").append(forms).append("\n");
        sb.append("Image: ").append(image).append("\n");
        sb.append("URL: ").append(url).append("\n");
        sb.append("Updated at: ").append(updatedAt).append("\n");

        return sb.toString();
    }

}
