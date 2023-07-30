package com.example.EnglishNewWordsBot.service;

import com.example.EnglishNewWordsBot.config.BotConfig;
import com.example.EnglishNewWordsBot.models.EnglishWord;
import com.example.EnglishNewWordsBot.repositories.EnglishWordRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {


    private long random;
    private long maxRandom;
    private long moduleNow;
    private String letterNow;
    private List<EnglishWord> englishWords;
    @Autowired
    private final BotConfig CONFIG;
    @Autowired
    private EnglishWordRepository englishWordRepository;


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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void callbackData(Update update) throws Exception {

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

            case "stop", "next", "all", "module1", "module2", "module3", "module4", "module5", "module6", "module7", "module8", "module1Letters", "module2Letters", "module3Letters", "module4Letters", "module5Letters", "module6Letters", "module7Letters", "module8Letters" -> {

                switch (callbackData) {
                    case "stop" -> sendMessage(chatId, "Чтобы начать заново, используйте команду - /learn");

                    case "next", "all", "module1", "module2", "module3", "module4", "module5", "module6", "module7", "module8" -> {

                        if (callbackData.equals("next"))
                            random = getRandomNumber(maxRandom);

                        else {
                            int thing = callbackData.equals("all") ? 3 : 2;
                            englishWords = getEnglishWords(thing);
                        }

                        EnglishWord englishWord = englishWords.get((int) random);
                        moduleNow = englishWord.getModule();
                        letterNow = englishWord.getLetter();

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
                englishWords = getEnglishWords(1);
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
        markup.setKeyboard(getInlineKeyboardForModule());
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

    private void sendMessageAndAnswers(long chatId) {

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
    public TelegramBot(BotConfig config) {
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

    private String getEnglish(long index) {
        return englishWords.get((int) index).getEnglish();
    }

    private String getTranslate(long index) {
        return englishWords.get((int) index).getTranslate();
    }

    private List<EnglishWord> getEnglishWords(int thing) {
        List<EnglishWord> englishWords = switch (thing) {
            case 1 -> englishWordRepository.findAllByModuleAndLetter(moduleNow, letterNow);
            case 2 -> englishWordRepository.findAllByModule(moduleNow);
            default -> englishWordRepository.findAll();
        };

        maxRandom = englishWords.size();
        this.random = getRandomNumber(maxRandom);

        return englishWords;
    }

    private long getRandomNumber(long count) {
        return (int) (Math.random() * count);
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
        buttons1.add(createButton("1", "module1Letters"));
        buttons1.add(createButton("2", "module2Letters"));
        buttons1.add(createButton("3", "module3Letters"));

        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        buttons2.add(createButton("4", "module4Letters"));
        buttons2.add(createButton("5", "module5Letters"));
        buttons2.add(createButton("6", "module6Letters"));

        List<InlineKeyboardButton> buttons3 = new ArrayList<>();
        buttons3.add(createButton("7", "module7Letters"));
        buttons3.add(createButton("8", "module8Letters"));
        buttons3.add(createButton("Все", "all"));

        List<List<InlineKeyboardButton>> result = new ArrayList<>();
        result.add(buttons1);
        result.add(buttons2);
        result.add(buttons3);

        return result;
    }

    private List<List<InlineKeyboardButton>> getInlineKeyboardForModule() {

        List<InlineKeyboardButton> list1 = new ArrayList<>();
        list1.add(createButton("A", "A"));
        list1.add(createButton("B", "B"));
        list1.add(createButton("C", "C"));

        List<InlineKeyboardButton> list2 = new ArrayList<>();
        list2.add(createButton("D", "D"));
        list2.add(createButton("E", "E"));
        list2.add(createButton("Весь модуль", "module" + moduleNow));

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(list1);
        buttons.add(list2);

        return buttons;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private List<String> getShuffleListOfAnswers(String correct) {
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

    public List<List<InlineKeyboardButton>> getInlineKeyboardForNextOrStop() {
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
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
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


    private void addToTable(Long module, String letter, String filePath) {

        List<String> english = new ArrayList<>();
        List<String> translate = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {

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
            englishWord.setId(englishWordRepository.count() + 1L + i);
            englishWord.setEnglish(english.get(i));
            englishWord.setTranslate(translate.get(i));
            englishWord.setModule(module);
            englishWord.setLetter(letter);
            englishWordRepository.save(englishWord);
        }
    }




}
