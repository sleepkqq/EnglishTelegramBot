package com.example.EnglishNewWordsBot.service;

import com.example.EnglishNewWordsBot.config.BotConfig;
import com.example.EnglishNewWordsBot.models.*;
import com.example.EnglishNewWordsBot.repositories.*;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private EnglishWordRepository englishWordRepository;
    @Autowired
    private ModuleOneRepository moduleOneRepository;
    @Autowired
    private ModuleTwoRepository moduleTwoRepository;
    @Autowired
    private ModuleThreeRepository moduleThreeRepository;
    @Autowired
    private ModuleFourRepository moduleFourRepository;
    @Autowired
    private ModuleFiveRepository moduleFiveRepository;
    @Autowired
    private ModuleSixRepository moduleSixRepository;
    @Autowired
    private ModuleSevenRepository moduleSevenRepository;
    @Autowired
    private ModuleEightRepository moduleEightRepository;
    private final Long ALL_COUNT = 319L;
    private final Long MODULE_ONE_COUNT = 184L;
    private final Long MODULE_TWO_COUNT = 134L;
    private final Long MODULE_THREE_COUNT = 175L;
    private final Long MODULE_FOUR_COUNT = 230L;
    private final Long MODULE_FIVE_COUNT = 153L;
    private final Long MODULE_SIX_COUNT = 133L;
    private final Long MODULE_SEVEN_COUNT = 111L;
    private final Long MODULE_EIGHT_COUNT = 175L;
    final BotConfig CONFIG;
    private Long random;
    private String tableNow;
    private Long beginNow;
    private Long endNow;
    private String moduleNow;
    private String letterNow = "";
    private static final List<List<HashMap<String, Long[]>>> LIST_OF_MODULES_LETTERS = new ArrayList<>();

    static {
        List<HashMap<String, Long[]>> listModule1 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 61L})),
                new HashMap<>(Map.of("B", new Long[]{62L, 93L})),
                new HashMap<>(Map.of("C", new Long[]{94L, 113L})),
                new HashMap<>(Map.of("D", new Long[]{114L, 141L})),
                new HashMap<>(Map.of("E", new Long[]{142L, 184L}))
        ));

        List<HashMap<String, Long[]>> listModule2 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 46L})),
                new HashMap<>(Map.of("B", new Long[]{47L, 69L})),
                new HashMap<>(Map.of("C", new Long[]{70L, 82L})),
                new HashMap<>(Map.of("D", new Long[]{83L, 127L})),
                new HashMap<>(Map.of("E", new Long[]{128L, 134L}))
        ));

        List<HashMap<String, Long[]>> listModule3 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 59L})),
                new HashMap<>(Map.of("B", new Long[]{60L, 83L})),
                new HashMap<>(Map.of("C", new Long[]{84L, 99L})),
                new HashMap<>(Map.of("D", new Long[]{100L, 141L})),
                new HashMap<>(Map.of("E", new Long[]{142L, 175L}))
        ));

        List<HashMap<String, Long[]>> listModule4 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 64L})),
                new HashMap<>(Map.of("B", new Long[]{65L, 102L})),
                new HashMap<>(Map.of("C", new Long[]{103L, 130L})),
                new HashMap<>(Map.of("D", new Long[]{131L, 166L})),
                new HashMap<>(Map.of("E", new Long[]{167L, 216L}))
        ));

        List<HashMap<String, Long[]>> listModule5 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 46L})),
                new HashMap<>(Map.of("B", new Long[]{47L, 66L})),
                new HashMap<>(Map.of("C", new Long[]{67L, 72L})),
                new HashMap<>(Map.of("D", new Long[]{73L, 126L})),
                new HashMap<>(Map.of("E", new Long[]{127L, 153L}))
        ));

        List<HashMap<String, Long[]>> listModule6 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 38L})),
                new HashMap<>(Map.of("B", new Long[]{39L, 72L})),
                new HashMap<>(Map.of("C", new Long[]{73L, 77L})),
                new HashMap<>(Map.of("D", new Long[]{78L, 120L})),
                new HashMap<>(Map.of("E", new Long[]{121L, 133L}))
        ));

        List<HashMap<String, Long[]>> listModule7 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 24L})),
                new HashMap<>(Map.of("B", new Long[]{25L, 50L})),
                new HashMap<>(Map.of("C", new Long[]{51L, 55L})),
                new HashMap<>(Map.of("D", new Long[]{56L, 85L})),
                new HashMap<>(Map.of("E", new Long[]{86L, 111L}))
        ));

        List<HashMap<String, Long[]>> listModule8 = new ArrayList<>(Arrays.asList(
                new HashMap<>(Map.of("A", new Long[]{1L, 61L})),
                new HashMap<>(Map.of("B", new Long[]{62L, 83L})),
                new HashMap<>(Map.of("C", new Long[]{84L, 90L})),
                new HashMap<>(Map.of("D", new Long[]{91L, 150L})),
                new HashMap<>(Map.of("E", new Long[]{151L, 175L}))
        ));

        LIST_OF_MODULES_LETTERS.add(listModule1);
        LIST_OF_MODULES_LETTERS.add(listModule2);
        LIST_OF_MODULES_LETTERS.add(listModule3);
        LIST_OF_MODULES_LETTERS.add(listModule4);
        LIST_OF_MODULES_LETTERS.add(listModule5);
        LIST_OF_MODULES_LETTERS.add(listModule6);
        LIST_OF_MODULES_LETTERS.add(listModule7);
        LIST_OF_MODULES_LETTERS.add(listModule8);
    }

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
            callbackData(update);
        }
    }

    private void callbackData(Update update) {

        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        EditMessageText message = new EditMessageText();
        String text = "";

        switch (callbackData) {
            case "correct" -> {
                text = EmojiParser.parseToUnicode("Правильный ответ :white_check_mark:\n\nВопрос: " + getEnglish(random) + "\n\nОтвет: " + getTranslate(random));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(getInlineKeyboardForNextOrStop());
                message.setReplyMarkup(markup);
            }

            case "1", "2", "3", "4" -> {
                text = EmojiParser.parseToUnicode("Неправильный ответ :x:\n\nВопрос: " + getEnglish(random) + "\n\nПравильный ответ был: " + getTranslate(random));
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(getInlineKeyboardForNextOrStop());
                message.setReplyMarkup(markup);
            }

            case "stop", "next", "all", "module1", "module2", "module3", "module4", "module5", "module6", "module7", "module8", "module1Letters", "module2Letters", "module3Letters", "module4Letters", "module5Letters", "module6Letters", "module7Letters", "module8Letters"  -> {

                switch (callbackData){
                    case "stop" -> sendMessage(chatId, "Чтобы начать заново, используйте команду - /learn");

                    case "next" -> sendMessageAndAnswers(chatId, tableNow, beginNow, endNow);

                    case "all" -> {
                        moduleNow = null;
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, ALL_COUNT);
                    }

                    case "module1" -> {
                        moduleNow = "Module 1";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_ONE_COUNT);
                    }

                    case "module2" -> {
                        moduleNow = "Module 2";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_TWO_COUNT);
                    }

                    case "module3" -> {
                        moduleNow = "Module 3";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_THREE_COUNT);
                    }

                    case "module4" -> {
                        moduleNow = "Module 4";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_FOUR_COUNT);
                    }

                    case "module5" -> {
                        moduleNow = "Module 5";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_FIVE_COUNT);
                    }

                    case "module6" -> {
                        moduleNow = "Module 6";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_SIX_COUNT);
                    }

                    case "module7" -> {
                        moduleNow = "Module 7";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_SEVEN_COUNT);
                    }

                    case "module8" -> {
                        moduleNow = "Module 8";
                        letterNow = "";
                        sendMessageAndAnswers(chatId, callbackData, 1L, MODULE_EIGHT_COUNT);
                    }

                    case "module1Letters" -> {
                        moduleLettersCallbackData(chatId, "module1");
                        moduleNow = "Module 1";
                    }

                    case "module2Letters" -> {
                        moduleLettersCallbackData(chatId, "module2");
                        moduleNow = "Module 2";
                    }

                    case "module3Letters" -> {
                        moduleLettersCallbackData(chatId, "module3");
                        moduleNow = "Module 3";
                    }

                    case "module4Letters" -> {
                        moduleLettersCallbackData(chatId, "module4");
                        moduleNow = "Module 4";
                    }

                    case "module5Letters" -> {
                        moduleLettersCallbackData(chatId, "module5");
                        moduleNow = "Module 5";
                    }

                    case "module6Letters" -> {
                        moduleLettersCallbackData(chatId, "module6");
                        moduleNow = "Module 6";
                    }

                    case "module7Letters" -> {
                        moduleLettersCallbackData(chatId, "module7");
                        moduleNow = "Module 7";
                    }

                    case "module8Letters" -> {
                        moduleLettersCallbackData(chatId, "module8");
                        moduleNow = "Module 8";
                    }
                }

                text = update.getCallbackQuery().getMessage().getText();
            }

            case "A", "B", "C", "D", "E" -> {
                letterNow = callbackData;
                Long[] beginAndEnd = getBeginAndEnd(callbackData);
                sendMessageAndAnswers(chatId, tableNow, beginAndEnd[0], beginAndEnd[1]);
                text = update.getCallbackQuery().getMessage().getText();
            }

        }

        message.setChatId(String.valueOf(chatId));
        message.setMessageId((int) messageId);
        message.setText(text);
        executeMessage(message);

    }

    private Long[] getBeginAndEnd(String letter) {
        int index;
        switch (letter) {
            case "B" -> index = 1;
            case "C" -> index = 2;
            case "D" -> index = 3;
            case "E" -> index = 4;
            default -> index = 0;
        }
        Long[] result = {};
        switch (tableNow) {
            case "module1" -> result = LIST_OF_MODULES_LETTERS.get(0).get(index).get(letter);
            case "module2" -> result = LIST_OF_MODULES_LETTERS.get(1).get(index).get(letter);
            case "module3" -> result = LIST_OF_MODULES_LETTERS.get(2).get(index).get(letter);
            case "module4" -> result = LIST_OF_MODULES_LETTERS.get(3).get(index).get(letter);
            case "module5" -> result = LIST_OF_MODULES_LETTERS.get(4).get(index).get(letter);
            case "module6" -> result = LIST_OF_MODULES_LETTERS.get(5).get(index).get(letter);
            case "module7" -> result = LIST_OF_MODULES_LETTERS.get(6).get(index).get(letter);
            case "module8" -> result = LIST_OF_MODULES_LETTERS.get(7).get(index).get(letter);
        }

        return result;
    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "hi " + name;

        sendMessage(chatId, answer);
    }

    private void moduleLettersCallbackData(long chatId, String table) {

        tableNow = table;

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

    private void sendMessageAndAnswers(long chatId, String table, Long begin, Long end) {

        beginNow = begin;
        endNow = end;
        tableNow = table;
        random = generateRandomNumber(end - (begin - 1)) + begin - 1;

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        String moduleAndLetter = " (" + moduleNow + (letterNow.equals("") ? "" : " " + letterNow) + ")";
        message.setText(getEnglish(random) + (moduleNow == null ? "" : moduleAndLetter));

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        markupInline.setKeyboard(getInlineKeyboardForAnswers(getShuffleListOfAnswers(getTranslate(random)), getTranslate(random)));

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
        listOfCommands.add(new BotCommand("/learn", "get a random english word"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getEnglish(Long random) {
        switch (tableNow) {
            case "all" -> {return englishWordRepository.findById(random).get().getEnglishWord();}
            case "module1" -> {return moduleOneRepository.findById(random).get().getEnglish();}
            case "module2" -> {return moduleTwoRepository.findById(random).get().getEnglish();}
            case "module3" -> {return moduleThreeRepository.findById(random).get().getEnglish();}
            case "module4" -> {return moduleFourRepository.findById(random).get().getEnglish();}
            case "module5" -> {return moduleFiveRepository.findById(random).get().getEnglish();}
            case "module6" -> {return moduleSixRepository.findById(random).get().getEnglish();}
            case "module7" -> {return moduleSevenRepository.findById(random).get().getEnglish();}
            case "module8" -> {return moduleEightRepository.findById(random).get().getEnglish();}
            default -> {return null;}
        }
    }

    public String getTranslate(Long random) {
        switch (tableNow) {
            case "all" -> {return englishWordRepository.findById(random).get().getTranslate();}
            case "module1" -> {return moduleOneRepository.findById(random).get().getTranslate();}
            case "module2" -> {return moduleTwoRepository.findById(random).get().getTranslate();}
            case "module3" -> {return moduleThreeRepository.findById(random).get().getTranslate();}
            case "module4" -> {return moduleFourRepository.findById(random).get().getTranslate();}
            case "module5" -> {return moduleFiveRepository.findById(random).get().getTranslate();}
            case "module6" -> {return moduleSixRepository.findById(random).get().getTranslate();}
            case "module7" -> {return moduleSevenRepository.findById(random).get().getTranslate();}
            case "module8" -> {return moduleEightRepository.findById(random).get().getTranslate();}
            default -> {return null;}
        }
    }

    private long generateRandomNumber(Long count) {
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
        buttons1.add(getButton("4", "module4Letters"));

        List<InlineKeyboardButton> buttons2 = new ArrayList<>();
        buttons2.add(getButton("5", "module5Letters"));
        buttons2.add(getButton("6", "module6Letters"));
        buttons2.add(getButton("7", "module7Letters"));
        buttons2.add(getButton("8", "module8Letters"));

        List<InlineKeyboardButton> buttons3 = new ArrayList<>();
        buttons2.add(getButton("All Modules", "all"));

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
        buttons.add(getButton("Весь Модуль", tableNow));

        return buttons;
    }

    private InlineKeyboardButton getButton(String text, String callbackData) {
        var button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private List<String> getShuffleListOfAnswers(String correct) {
        List<String> list = new ArrayList<>();

        long first = generateRandomNumber(endNow - beginNow) + beginNow, second = generateRandomNumber(endNow - beginNow) + beginNow, third = generateRandomNumber(endNow - beginNow) + beginNow;
        while (first == random || second == random || third == random || first == third || second == third || first == second) {
            first = generateRandomNumber(endNow - beginNow) + beginNow;
            second = generateRandomNumber(endNow - beginNow) + beginNow;
            third = generateRandomNumber(endNow - beginNow) + beginNow;
        }

        list.add(correct);
        list.add(getTranslate(first));
        list.add(getTranslate(second));
        list.add(getTranslate(third));

        Collections.shuffle(list);

        return list;
    }

    private List<List<InlineKeyboardButton>> getInlineKeyboardForNextOrStop() {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            var button = new InlineKeyboardButton();
            button.setText(i == 0 ? "следующий вопрос" : "завершить");
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

    private void addToTable() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String file = reader.readLine();
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
            ModuleEight moduleEight = new ModuleEight();
            moduleEight.setId(MODULE_EIGHT_COUNT + 1L + i);
            moduleEight.setEnglish(english.get(i));
            moduleEight.setTranslate(translate.get(i));
            moduleEight.setLetter("e");
            moduleEightRepository.save(moduleEight);
        }
    }




}
