package dev.luaq.facchat.util;

import org.bukkit.ChatColor;

public class ChatUtils {
    public static String colorf(String input, Object... format) {
        return color(String.format(input, format));
    }

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
