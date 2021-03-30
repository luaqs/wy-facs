package dev.luaq.facchat.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class LangUtils {
    @Getter @Setter
    private static ConfigurationSection langSect;

    public static String langf(String path, Object... format) {
        if (!langSect.contains(path)) {
            return String.format("'lang.%s' was not found", path);
        }

        String lang = langSect.getString(path);
        return colorf(lang, format);
    }

    public static String colorf(String input, Object... format) {
        return color(String.format(input, format));
    }

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
