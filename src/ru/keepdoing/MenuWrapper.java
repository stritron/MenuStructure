package ru.keepdoing;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by yuri on 05.12.18.
 */
public class MenuWrapper {
    private static final String ASK_STRING = ">";
    private static final String UNKNOWN_COMMAND = "Unknown command\n";
    private static final String WRONG_ITEM = "Wrong item\n";
    private static final String GO_BACK = "b: go back";
    private static final String OTHER_CMDS = "q: exit app";
    private static final String MENU_BRACKET = " <-> ";
    private static final String TAB = "\t";
    private static final char NEW_LINE = '\n';
    private final long chatId;
    private Menu pointer;

    public MenuWrapper(final Menu startMenu, final long chatId){
        this.pointer = startMenu;
        this.chatId = chatId;
    }

    public long getChatId(){
        return chatId;
    }

    private void setCurrentMenu(Menu menu){
        this.pointer = menu;
    }

    private Menu getPointer(){
        return pointer;
    }

    public String dialog(final String inString){
        return process(this, inString);
    }

    private static String process(MenuWrapper wrapper, final String inString) {
        if (isNumber(inString))
            return processNumber(Integer.parseInt(inString), wrapper);

        return processCommand(inString, wrapper);
    }

    private static String processNumber(int number, MenuWrapper wrapper){

        StringBuilder sb = new StringBuilder();
        Menu selectedMenu = wrapper.getPointer().getById(number);

        if (selectedMenu == null) {
            sb.append(WRONG_ITEM).append(ASK_STRING);
            return sb.toString();
        }

        if (selectedMenu.isItem()) {
            sb.append(printCard(selectedMenu.getCard(), TAB)).
                    append(printMenu(wrapper.getPointer()));
            return sb.toString();
        }

        sb.append(printMenu(selectedMenu));
        wrapper.setCurrentMenu(selectedMenu);
        return sb.toString();
    }

    private static String processCommand(String inString, MenuWrapper wrapper){
        switch (inString) {
            case "q":
                System.exit(0);
            case "b":
                if (wrapper.getPointer().haveRoot()) {
                    wrapper.setCurrentMenu(wrapper.getPointer().getRootMenu());
                }
                return printMenu(wrapper.getPointer());
            default:
                return UNKNOWN_COMMAND + ASK_STRING;
        }
    }

    static String printMenu(Menu menu) {
        StringBuilder sb = new StringBuilder();
        sb.append(MENU_BRACKET).append(menu.getName()).append(MENU_BRACKET).append(NEW_LINE);

        for (Menu m : menu) {
            sb.append(String.format("%d: %s%c", m.getId(), m.getName(), NEW_LINE));
        }
        if (menu.haveRoot()) {
            sb.append(GO_BACK).append(NEW_LINE);
        }
        sb.append(OTHER_CMDS).append(NEW_LINE).append(ASK_STRING);
        return sb.toString();
    }

    static String printCard(Card card, String tab) {
        StringBuilder sb = new StringBuilder();
        for (String s : card) {
            sb.append(tab).append(s).append(NEW_LINE);
        }
        return sb.toString();
    }

    static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException | InputMismatchException e) {
            return false;
        }
    }

    static String printAllMenu(Menu menu, String tab) {
        StringBuilder sb = new StringBuilder();

        for (Menu m : menu) {
            sb
                    .append(tab)
                    .append(m.getId())
                    .append(":")
                    .append(TAB)
                    .append(m.getName())
                    .append(NEW_LINE);
            if (m.haveCard()) {
                sb.append(printCard(m.getCard(),tab + '\t'));
            }
            if (!m.isItem()) {
                sb.append(printAllMenu(m, tab + '\t'));
            }
        }
        return sb.toString();
    }
}
