package com.urfu.GorohSlot.commands;

import com.urfu.GorohSlot.bot.User;
import com.urfu.GorohSlot.bot.telegramBot.KeyboardStates;
import com.urfu.GorohSlot.bot.telegramBot.KeyboardsCommandTelegram;
import com.urfu.GorohSlot.database.SQLHandler;
import com.urfu.GorohSlot.games.tools.Emoji;
import com.urfu.GorohSlot.games.tools.Utils;

public class CommandsAdvertisement {
    public static String advertEnterCommand(User user) {
        user.setKeyboardState(KeyboardStates.States.ADVERTMODE.toString());
        user.getKeyboard().AddButtonOneLine(Commands.exitAdvert);
        user.getKeyboard().SaveKeyboard();
        return String.format("""
                        %s%sГде лучшая реклама? Здесь!%s
                           Ваше сообщение могут увидеть 
                           миллионы наших пользователей:
                           Цена - 1000%s за 1 символ
                           Отправляйте текст рекламы сюда.
                           Баланс %s%s
                        %s""",
                Utils.repeat(Emoji.tilda.getEmojiCode(), 15),
                Emoji.machine.getEmojiCode(),
                Emoji.machine.getEmojiCode(),
                Emoji.dollar.getEmojiCode(),
                user.getBalance(),
                Emoji.dollar.getEmojiCode(),
                Utils.repeat(Emoji.tilda.getEmojiCode(), 15));
    }

    public static String advertExitCommand(User user) {
        user.setMode(Commands.chooseMode);
        user.setKeyboardState(KeyboardStates.States.CHOOSEMODE.toString());
        KeyboardsCommandTelegram.chooseModeCommand(user);
        SQLHandler.update(user);
        return "Вы вышли из режима реклама";
    }
}
