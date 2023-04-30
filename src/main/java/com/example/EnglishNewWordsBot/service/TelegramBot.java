package com.example.EnglishNewWordsBot.service;

import com.example.EnglishNewWordsBot.config.BotConfig;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {


    private long random;
    private long maxRandom;
    private long moduleNow;
    private String letterNow;
    private ResultSet resultSet;
    private final BotConfig CONFIG;
    private final Connection CONNECTION = DriverManager.getConnection("jdbc:mysql://localhost:3306/tg-bot", "root", "az04_super");


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start" -> startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                case "/learn" -> learnCommandReceived(chatId);
                default -> sendMessage(chatId, "I don't understand you");
            }
        } else if (update.hasCallbackQuery()) {
            try {
                callbackData(update);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void callbackData(Update update) throws SQLException {

        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText message = new EditMessageText();
        String text = "";

        switch (callbackData) {

            case "correct", "1", "2", "3", "4" -> {

                if (callbackData.equals("correct"))
                    text = EmojiParser.parseToUnicode(String.format("Правильный ответ :white_check_mark:\n\nВопрос: %s\n\nОтвет: %s", getEnglish(random), getTranslate(random)));

                else
                    text = EmojiParser.parseToUnicode(String.format("Неправильный ответ :x:\n\nВопрос: %s\n\nПравильный ответ был: %s", getEnglish(random), getTranslate(random)));

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(getInlineKeyboardForNextOrStop());
                message.setReplyMarkup(markup);
            }

            case "stop", "next", "all", "module1", "module2", "module3", "module4", "module5", "module6", "module7", "module8", "module1Letters", "module2Letters", "module3Letters", "module4Letters", "module5Letters", "module6Letters", "module7Letters", "module8Letters"  -> {

                switch (callbackData){
                    case "stop" -> sendMessage(chatId, "Чтобы начать заново, используйте команду - /learn");

                    case "next", "all", "module1", "module2", "module3", "module4", "module5", "module6", "module7", "module8" -> {

                        if (callbackData.equals("next"))
                            random = getRandomNumber(maxRandom);

                        else {
                            int thing = callbackData.equals("all") ? 3 : 2;
                            resultSet = getResultSet(thing);
                        }

                        resultSet.absolute((int) random);
                        moduleNow = resultSet.getLong("module");
                        letterNow = resultSet.getString("letter");

                        sendMessageAndAnswers(chatId);
                    }


                    case "module1Letters", "module2Letters", "module3Letters", "module4Letters", "module5Letters", "module6Letters", "module7Letters", "module8Letters" -> {
                        moduleNow = (Long.parseLong(callbackData.replaceAll("\\D+", "")));
                        moduleLettersCallbackData(chatId, Long.parseLong(callbackData.replaceAll("\\D+", "")));
                    }
                }

                text = update.getCallbackQuery().getMessage().getText();
            }

            case "A", "B", "C", "D", "E" -> {
                letterNow = callbackData;
                resultSet = getResultSet(1);
                sendMessageAndAnswers(chatId);
                text = update.getCallbackQuery().getMessage().getText();
            }

        }

        message.setChatId(String.valueOf(chatId));
        message.setMessageId((int) messageId);
        message.setText(text);
        executeMessage(message);

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode(":wave:Привет, "
                + name
                + "! Я помогу тебе подготовиться к словарному диктанту. Чтобы начать подготовку - введи команду /learn");

        sendMessage(chatId, answer);
    }

    private void moduleLettersCallbackData(long chatId, long module) {

        moduleNow = module;

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите раздел модуля");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(Collections.singletonList(getInlineKeyboardForModule()));
        message.setReplyMarkup(markup);

        executeMessage(message);
    }

    private void learnCommandReceived(long chatId) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выберите модуль, слова которого вы хотите выучить");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(getInlineKeyboardForModules());
        message.setReplyMarkup(markup);

        executeMessage(message);
    }

    private void sendMessageAndAnswers(long chatId) throws SQLException {

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String moduleAndLetter = String.format(" (Module %d, %s)", moduleNow, letterNow);
        message.setText(getEnglish((int) random) + moduleAndLetter);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.setKeyboard(getInlineKeyboardForAnswers(getShuffleListOfAnswers(getTranslate((int) random)), getTranslate((int) random)));

        message.setReplyMarkup(markupInline);

        executeMessage(message);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        executeMessage(message);
    }

    @Autowired
    public TelegramBot(BotConfig config) throws SQLException {
        this.CONFIG = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/learn", "start your training"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String getEnglish(long index) throws SQLException {

        resultSet.absolute((int) index);
        return resultSet.getString("english");
    }

    private String getTranslate(long index) throws SQLException {

        resultSet.absolute((int) index);
        return resultSet.getString("translate");
    }

    private ResultSet getResultSet(int thing) throws SQLException {

        Statement statement = CONNECTION.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String sql;

        switch (thing) {

            case (1) -> sql = String.format("SELECT * FROM english_words_table WHERE module=%d AND letter='%s'", moduleNow, letterNow);

            case (2) -> sql = String.format("SELECT * FROM english_words_table WHERE module=%d", moduleNow);

            default -> sql = "SELECT * FROM english_words_table";
        }

        ResultSet resultSet = statement.executeQuery(sql);
        int size;
        try {
            resultSet.last();
            size = resultSet.getRow();
        }
        catch(Exception ex) {
            size = 0;
        }
        maxRandom = size;
        this.random = getRandomNumber(maxRandom);

        return resultSet;
    }

    private long getRandomNumber(long count) {
        return (int)(Math.random() * count + 1);
    }

    private List<List<InlineKeyboardButton>> getInlineKeyboardForAnswers(List<String> listOfAnswers, String correctAnswer) {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0, number = 1; i < 2; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < 2; j++, number++) {
                var button = new InlineKeyboardButton();
                button.setText(listOfAnswers.get(number - 1));
                button.setCallbackData(button.getText().equals(correctAnswer) ? "correct" : "" + number);
                row.add(button);
            }
            rowsInline.add(row);
        }
        return rowsInline;
    }

    private List<List<InlineKeyboardButton>> getInlineKeyboardForModules() {

        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        buttons1.add(getButton("1", "module1Letters"));
        buttons1.add(getButton("2", "module2Letters"));
        buttons1.add(getButton("3", "module3Letters"));

        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        buttons2.add(getButton("4", "module4Letters"));
        buttons2.add(getButton("5", "module5Letters"));
        buttons2.add(getButton("6", "module6Letters"));

        List<InlineKeyboardButton> buttons3 = new ArrayList<>();
        buttons3.add(getButton("7", "module7Letters"));
        buttons3.add(getButton("8", "module8Letters"));
        buttons3.add(getButton("Все", "all"));

        List<List<InlineKeyboardButton>> result = new ArrayList<>();
        result.add(buttons1);
        result.add(buttons2);
        result.add(buttons3);

        return result;
    }

    private List<InlineKeyboardButton> getInlineKeyboardForModule() {
        List<InlineKeyboardButton> buttons = new ArrayList<>();

        buttons.add(getButton("A", "A"));
        buttons.add(getButton("B", "B"));
        buttons.add(getButton("C", "C"));
        buttons.add(getButton("D", "D"));
        buttons.add(getButton("E", "E"));
        buttons.add(getButton("Весь", "module" + moduleNow));

        return buttons;
    }

    private InlineKeyboardButton getButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private List<String> getShuffleListOfAnswers(String correct) throws SQLException {
        List<String> list = new ArrayList<>();

        String first = "", second = "", third = "";
        while (first.equals(second) || first.equals(third) || first.equals(correct) || second.equals(third) || second.equals(correct) || third.equals(correct)) {
            first = getTranslate(getRandomNumber(random));
            second = getTranslate(getRandomNumber(random));
            third = getTranslate(getRandomNumber(random));
        }

        list.add(correct);
        list.add(first);
        list.add(second);
        list.add(third);

        Collections.shuffle(list);

        return list;
    }

    private List<List<InlineKeyboardButton>> getInlineKeyboardForNextOrStop() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            var button = new InlineKeyboardButton();
            button.setText(i == 0 ? "следующее слово" : "завершить");
            button.setCallbackData(button.getText().equals("завершить") ? "stop" : "next");
            row.add(button);
            rowsInline.add(row);
        }
        return rowsInline;
    }

    private void executeMessage(EditMessageText message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return CONFIG.getBotName();
    }

    @Override
    public String getBotToken() {
        return CONFIG.getToken();
    }

    /*private void addToTable() throws IOException {

        String file = "D:\\file.txt";
        List<String> english = new ArrayList<>();
        List<String> translate = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {

            while (fileReader.ready()) {
                String line = fileReader.readLine();
                String[] split = line.split(" - ");
                english.add(split[0]);
                translate.add(split[1]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < english.size(); i++) {
            EnglishWord englishWord = new EnglishWord();
            englishWord.setId(1270 + 1L + i);
            englishWord.setEnglish(english.get(i));
            englishWord.setTranslate(translate.get(i));
            englishWord.setModule(8L);
            englishWord.setLetter("E");
            englishWordRepository.save(englishWord);
        }
    }*/




}
