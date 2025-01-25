import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class User {

    private String username;
    private String password;
    private Map<String, String> shortUrls = new HashMap<>(); // Карта для хранения коротких и длинных ссылок

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

//    // Метод для создания короткой ссылки
//    public String createShortUrl(String longUrl) {
//        // Генерируем случайную короткую ссылку
//        String shortUrl = generateShortUrl();
//
//        // Сохраняем пару короткая-длинная ссылка в карте
//        shortUrls.put(shortUrl, longUrl);
//
//        return shortUrl;
//    }

    // Метод для получения длинной ссылки по короткой ссылке
    public String getLongUrl(String shortUrl) {
        return shortUrls.get(shortUrl);
    }

    // Метод для печати информации о пользователе
    public void printUserInfo() {
        System.out.println("Имя пользователя: " + username);

        // Считываем все ссылки из файла
        List<Link> allLinks = readLinksFromFile();

        // Фильтруем ссылки по имени пользователя и печатаем информацию о каждой ссылке
        for (Link link : allLinks) {
            if (link.getUsername().equals(username)) {
                System.out.println("Длинная ссылка: " + link.getLongUrl());
                System.out.println("Короткая ссылка: " + link.getShortUrl());
                System.out.println("Количество оставшихся использований: " + link.getMaxUses());
                System.out.println("Количество произведенных использований: " + (link.getMaxUses()));
                System.out.println("Оставшееся время жизни ссылки: " + calculateRemainingLifespan(link));
                System.out.println();
            }
        }
    }

    private List<Link> readLinksFromFile() {
        List<Link> links = new ArrayList<>();
        try {
            File file = new File("MyLinks.txt");
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("///");
                LocalDateTime creationDate = LocalDateTime.parse(parts[2]);
                Link link = new Link(parts[0], parts[1], creationDate, Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
                links.add(link);
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл со ссылками.");
            return null;
        }
        return links;
    }

    private String calculateRemainingLifespan(Link link) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDate = link.getCreationDate();
        int lifespan = link.getLifespan();
        long daysBetween = ChronoUnit.DAYS.between(creationDate, now);
        long remainingDays = lifespan - daysBetween;
        return remainingDays + " дней";
    }


//    // Метод для генерации случайной короткой ссылки
//    private String generateShortUrl() {
//        String shortUrl = "clck.ru/";
//        // Генерируем 6 случайных символов
//        for (int i = 0; i < 6; i++) {
//            int randomInt = (int) (Math.random() * 26);
//            char randomChar = (char) (97 + randomInt);
//            shortUrl += randomChar;
//        }
//        return shortUrl;
//    }
}
