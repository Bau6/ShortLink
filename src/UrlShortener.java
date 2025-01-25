import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class UrlShortener {

    private static final String FILE_NAME = "links.txt";
    private static final String USERS_FILE_NAME = "users.txt";
    private static final int DEFAULT_LINK_LIFETIME = 24 * 60 * 60; // 1 day in seconds

    private static Map<String, Link> links = new HashMap<>();
    private static Map<String, User> users = new HashMap<>();
    private static User currentUser;

    public static void main(String[] args) throws IOException, URISyntaxException {
        // Загрузка существующих ссылок и пользователей из файлов

        loadUsers();

        // Вход в аккаунт или регистрация нового пользователя
        loginOrRegister();
        loadLinks();
        // Выбор действия (просмотр данных о себе или ввод длинной ссылки)
        System.out.println("Выберите действие:");
        System.out.println("1. Просмотреть данные о себе");
        System.out.println("2. Ввести длинную ссылку");
        int choice = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        switch (choice) {
            case 1:
                showUserData();
                break;
            case 2:
                createShortUrl();
                break;
            default:
                System.out.println("Неверный выбор");
                main(args);
        }
    }

    private static void loadLinks() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String shortUrl = parts[0];
                String longUrl = parts[1];
                int linkLimit = Integer.parseInt(parts[2]);
                int linkLifetime = Integer.parseInt(parts[3]);
                links.put(shortUrl, new Link(longUrl, linkLimit, linkLifetime));
            }
        }
    }

    private static void saveLink(String shortUrl, String longUrl, int linkLimit, int linkLifetime) throws IOException {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(String.format("%s,%s,%d,%d\n", shortUrl, longUrl, linkLimit, linkLifetime));
        }
    }

    private static void loadUsers() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String username = parts[0];
                String password = parts[1];
                users.put(username, new User(username, password));
            }
        }
    }

    private static void saveUser(String username, String password) throws IOException {
        try (FileWriter writer = new FileWriter(USERS_FILE_NAME, true)) {
            writer.write(String.format("%s,%s\n", username, password));
        }
    }

    private static void loginOrRegister() throws IOException {
        System.out.println("Введите команду (вход/регистрация):");
        String command = new BufferedReader(new InputStreamReader(System.in)).readLine();

        switch (command) {
            case "вход":
                login();
                break;
            case "регистрация":
                register();
                break;
            default:
                System.out.println("Неверная команда");
                loginOrRegister();
        }
    }

    private static void login() throws IOException {
        System.out.println("Введите имя пользователя:");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println("Введите пароль:");
        String password = new BufferedReader(new InputStreamReader(System.in)).readLine();

        if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
            currentUser = users.get(username);
            System.out.println("Вы успешно вошли в систему");
        } else {
            System.out.println("Неверное имя пользователя или пароль");
            loginOrRegister();
        }
    }

    private static void register() throws IOException {
        System.out.println("Введите имя пользователя:");
        String username = new BufferedReader(new InputStreamReader(System.in)).readLine();

        System.out.println("Введите пароль:");
        String password = new BufferedReader(new InputStreamReader(System.in)).readLine();

        if (!users.containsKey(username)) {
            users.put(username, new User(username, password));
            saveUser(username, password);
            System.out.println("Вы успешно зарегистрировались");
            login();
        } else {
            System.out.println("Имя пользователя уже занято");
            register();
        }
    }

    private static void createShortUrl() throws IOException {
        System.out.println("Введите длинную ссылку:");
        String longUrl = new BufferedReader(new InputStreamReader(System.in)).readLine();

        System.out.println("Введите количество доступных переходов:");
        int linkLimit = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        System.out.println("Введите время жизни ссылки (в днях):");
        int linkLifetime = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());

        String shortUrl = createShortUrl(longUrl);

        // Сохранение новой ссылки в файл
        saveLink(shortUrl, longUrl, linkLimit, linkLifetime * DEFAULT_LINK_LIFETIME);

        System.out.println("Короткая ссылка: " + shortUrl);
    }

    private static String createShortUrl(String longUrl) {
        String shortUrl = UUID.randomUUID().toString().substring(0, 6);
        if (links.containsKey(shortUrl)) {
            return createShortUrl(longUrl); // Рекурсивно генерируем новую короткую ссылку, если такая уже существует
        }
        return shortUrl;
    }

    private static void followShortUrl() throws IOException, URISyntaxException {
        System.out.println("Введите короткую ссылку:");
        String shortUrl = new BufferedReader(new InputStreamReader(System.in)).readLine();

        Link link = links.get(shortUrl);
        if (link == null) {
            System.out.println("Ссылка не найдена");
            return;
        }
        if (link.getRemainingTransitions() == 0) {
            System.out.println("Лимит переходов исчерпан");
            return;
        }
        if (link.isExpired()) {
            System.out.println("Срок действия ссылки истек");
            return;
        }
        link.decrementRemainingTransitions();
        Desktop.getDesktop().browse(new URI(link.getLongUrl()));
    }

    private static void showUserData() {
        System.out.println("Ваши данные:");
        System.out.println("Имя пользователя: " + currentUser.getUsername());
        System.out.println("Ваши ссылки:");
        for (Link link : links.values()) {
            if (link.getCreator().equals(currentUser.getUsername())) {
                System.out.println(String.format("%s -> %s", link.getShortUrl(), link.getLongUrl()));
            }
        }
    }

    private static class Link {
        private String longUrl;
        private int linkLimit;
        private int linkLifetime;
        private int remainingTransitions;
        private long creationTime;
        private String creator;

        public Link(String longUrl, int linkLimit, int linkLifetime) {
            this.longUrl = longUrl;
            this.linkLimit = linkLimit;
            this.linkLifetime = linkLifetime;
            this.remainingTransitions = linkLimit;
            this.creationTime = System.currentTimeMillis();
            this.creator = currentUser.getUsername();
        }

        public String getShortUrl() {
            return longUrl.substring(0, 6);
        }

        public String getLongUrl() {
            return longUrl;
        }

        public int getRemainingTransitions() {
            return remainingTransitions;
        }

        public void decrementRemainingTransitions() {
            remainingTransitions--;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - creationTime > linkLifetime * DEFAULT_LINK_LIFETIME * 1000;
        }

        public String getCreator() {
            return creator;
        }
    }
    private static class User {
        private String username;
        private String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}


