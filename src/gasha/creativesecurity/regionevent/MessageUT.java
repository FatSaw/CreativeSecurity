package gasha.creativesecurity.regionevent;

import gasha.creativesecurity.regionevent.ActionBar;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUT {
    public static String centered(String message) {
        message = MessageUT.t(message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        for (char c : message.toCharArray()) {
            if (c == '\u00a7') {
                previousCode = true;
                continue;
            }
            if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                }
                isBold = false;
                continue;
            }
            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
            ++messagePxSize;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        StringBuilder sb = new StringBuilder();
        for (int compensated = 0; compensated < toCompensate; compensated += spaceLength) {
            sb.append(" ");
        }
        return sb.toString() + message;
    }

    public static String t(String colorize) {
        return ChatColor.translateAlternateColorCodes((char)'&', (String)colorize);
    }

    public static List<String> t(List<String> colorize) {
        ArrayList<String> coloredLore = new ArrayList<String>();
        for (String str : colorize) {
            coloredLore.add(MessageUT.t(str));
        }
        return coloredLore;
    }

    public static String u(String decolorize) {
        return decolorize.replace('\u00a7', '&');
    }

    public static String d(String remove) {
        remove = MessageUT.t(remove);
        for (ChatColor color : ChatColor.values()) {
            remove = remove.replaceAll(color.toString(), "");
        }
        return remove;
    }

    public static void pmessage(Player player, List<String> messages) {
        for (String m : messages) {
            MessageUT.pmessage(player, m);
        }
    }

    public static void plmessage(Player player, List<String> messages, Boolean warning) {
        for (String m : messages) {
            MessageUT.plmessage(player, m, warning);
        }
    }

    public static void plmessage(Player player, List<String> messages) {
        MessageUT.plmessage(player, messages, (Boolean)false);
    }

    public static void cmessage(String teks) {
        Bukkit.getServer().getConsoleSender().sendMessage(MessageUT.t(teks));
    }

    public static void cmessage(List<String> teks) {
        for (String tek : teks) {
            MessageUT.cmessage(MessageUT.t(tek));
        }
    }

    public static void debug(String o) {
        Bukkit.getServer().getConsoleSender().sendMessage(MessageUT.t("&8[&dDebug&8] &f" + o));
    }

    public static void debug(Player player, String o) {
        MessageUT.pmessage(player, "&8[&dDebug&8] &f" + o);
    }

    public static void pmessage(Player player, String teks) {
        MessageUT.pmessage(player, teks, false);
    }

    public static void pmessage(Player player, String teks, Boolean Action2) {
        player.sendMessage(MessageUT.t(teks));
        if (Action2.booleanValue()) {
            MessageUT.acmessage(player, MessageUT.t(teks));
        }
    }

    public static void plmessage(Player player, String teks) {
        MessageUT.plmessage(player, teks, (Boolean)false);
    }

    public static void plmessage(Player player, String teks, Boolean warning) {
        MessageUT.plmessage(player, teks, warning, false);
    }

    public static void plmessage(Player player, String teks, Boolean warning, Boolean Action2) {
        String pref = "";
        if (teks.contains("<np>")) {
            pref = "";
            teks = teks.replace("<np>", "");
        }
        if (warning.booleanValue()) {
            if (teks.contains("<title>")) {
                String title = teks = teks.replace("<title>", "");
                if (teks.contains("<subtitle>")) {
                    title = teks.split("<subtitle>")[0];
                    String subtitle = teks.split("<subtitle>")[1];
                    MessageUT.tfullmessage(player, teks, subtitle);
                } else {
                    MessageUT.ttmessage(player, title);
                }
                return;
            }
            if (teks.contains("<subtitle>")) {
                teks = teks.replaceAll("<subtitle>", "");
                MessageUT.tsubmessage(player, teks);
                return;
            }
            if (teks.contains("<center>")) {
                teks = teks.replace("<center>", "");
                MessageUT.pmessage(player, MessageUT.centered(pref + "&c" + teks), Action2);
            } else {
                MessageUT.pmessage(player, pref + "&c" + teks, Action2);
            }
        } else {
            if (teks.contains("<action>")) {
                teks = teks.replace("<action>", "");
                MessageUT.acmessage(player, teks);
                return;
            }
            if (teks.contains("<title>")) {
                String title = teks = teks.replace("<title>", "");
                if (teks.contains("<subtitle>")) {
                    title = teks.split("<subtitle>")[0];
                    String subtitle = teks.split("<subtitle>")[1];
                    MessageUT.tfullmessage(player, title, subtitle);
                } else {
                    MessageUT.ttmessage(player, title);
                }
                return;
            }
            if (teks.contains("<subtitle>")) {
                teks = teks.replaceAll("<subtitle>", "");
                MessageUT.tsubmessage(player, teks);
                return;
            }
            if (teks.contains("<center>")) {
                teks = teks.replace("<center>", "");
                MessageUT.pmessage(player, MessageUT.centered(pref + "&b" + teks), Action2);
            } else {
                MessageUT.pmessage(player, pref + "&b" + teks, Action2);
            }
        }
    }

    public static void acplmessage(Player player, String teks, Boolean warning) {
        String pref = "";
        if (teks.contains("<np>")) {
            teks = teks.replace("<np>", "");
            pref = "";
        }
        teks = teks.replace("<center>", "");
        if (warning.booleanValue()) {
            MessageUT.acmessage(player, pref + "&c" + teks);
        } else {
            MessageUT.acmessage(player, pref + "&b" + teks);
        }
    }

    public static void acplmessage(Player player, String teks) {
        MessageUT.acplmessage(player, teks, (Boolean)false);
    }

    public static void acplmessage(Player player, List<String> teks) {
        MessageUT.acplmessage(player, teks, (Boolean)false);
    }

    public static void acplmessage(Player player, List<String> messages, Boolean warning) {
        for (String m : messages) {
            MessageUT.acplmessage(player, m, warning);
        }
    }

    public static void acmessage(Player player, String teks) {
        ActionBar.sendActionBar(player, teks);
    }

    public static void tfullmessage(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        player.sendTitle(title, subtitle, fadein, stay, fadeout);
    	//TitleBar.sendTitle(player, fadein, stay, fadeout, title, subtitle);
    }

    public static void tfullmessage(Player player, String title, String subtitle) {
        MessageUT.tfullmessage(player, title, subtitle, 20, 60, 20);
    }

    public static void tsubmessage(Player player, String subtitle, int fadein, int stay, int fadeout) {
        MessageUT.tfullmessage(player, "", subtitle, fadein, stay, fadeout);
    }

    public static void tsubmessage(Player player, String subtitle) {
        MessageUT.tfullmessage(player, "", subtitle, 20, 60, 20);
    }

    public static void ttmessage(Player player, String title, int fadein, int stay, int fadeout) {
        MessageUT.tfullmessage(player, title, "", fadein, stay, fadeout);
    }

    public static void ttmessage(Player player, String title) {
        MessageUT.tfullmessage(player, "", title, 20, 60, 20);
    }

    public static enum DefaultFontInfo {
        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('\"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private char character;
        private int length;

        private DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == SPACE) {
                return this.getLength();
            }
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() != c) continue;
                return dFI;
            }
            return DEFAULT;
        }
    }
}

