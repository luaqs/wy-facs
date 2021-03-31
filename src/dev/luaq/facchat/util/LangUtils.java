package dev.luaq.facchat.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class LangUtils {
    @Getter @Setter
    private static ConfigurationSection langSect;

    @SuppressWarnings("unchecked")
    public static String langf(String path, Object... format) {
        if (!langSect.contains(path)) {
            return String.format("'lang.%s' was not found", path);
        }

        Object lang = langSect.get(path);
        String converted = lang instanceof List ? String.join("\n", (List<String>) lang) : (String) lang;

        return colorf(converted, format);
    }

    public static String colorf(String input, Object... format) {
        return color(String.format(input, format));
    }

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
