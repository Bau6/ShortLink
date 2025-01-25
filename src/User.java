import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

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
                System.out.println("Количество произведенных использований: " + (link.getUses()));
                System.out.println("Оставшееся время жизни ссылки: " + calculateRemainingLifespan(link) + " дней");
                System.out.println();
            }
        }
    }

    public void printLinks() {
        // Считываем все ссылки из файла
        List<Link> allLinks = readLinksFromFile();

        int cntLink = 1;
        System.out.println();
        System.out.println("Выберите короткую ссылку для перехода: ");

        // Фильтруем ссылки по имени пользователя и печатаем информацию о каждой ссылке
        for (Link link : allLinks) {
            System.out.println(cntLink + ": " + link.getShortUrl());
            cntLink++;
        }
        System.out.println();
    }

    public void goToLink(String chooseLink) throws URISyntaxException, IOException {
        // Считываем все ссылки из файла
        List<Link> allLinks = readLinksFromFile();

        int cntLink = 1;
        System.out.println();

        // Переход по ссылке
        for (Link link : allLinks) {
            if (chooseLink.equals(link.getShortUrl()) || chooseLink.equals(Integer.toString(cntLink))) {
                if (calculateRemainingLifespan(link) > 0 && (link.getMaxUses() - link.getUses()) > 0) {
                    Desktop.getDesktop().browse(new URI(link.getLongUrl()));
                    // Создаем объект ссылки
                    Link newLink = new Link(link.getLongUrl(), link.getShortUrl(), link.getCreationDate(), link.getMaxUses() - 1, link.getLifespan(), link.getUsername(), link.getUses() + 1);

                    // Сохраняем ссылку в файл
                    try {
                        FileWriter writer = new FileWriter("MyLinks.txt", true);
                        writer.write(newLink.toString() + "\n");
                        List<String> lines = Files.readAllLines(Paths.get("MyLinks.txt"));
                        int index = lines.indexOf(link.toString());
                        lines.remove(index);

                        try (FileWriter writer2 = new FileWriter("MyLinks.txt", false)) {
                            for (String line : lines) {
                                writer2.write(line + "\n");
                            }
                        }
                        writer.close();
                    } catch (IOException e) {
                        System.out.println("Не удалось сохранить ссылку в файл.");
                        return;
                    }
                } else {
                    if (calculateRemainingLifespan(link) <= 0) {
                        System.out.println("Время жизни ссылки истекло!");
                    } else {
                        System.out.println("Количество переходов по ссылке закончилось!");
                    }
                }
            }
            cntLink++;
        }
        System.out.println();
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
                Link link = new Link(parts[0], parts[1], creationDate, Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5],Integer.parseInt(parts[6]));
                links.add(link);
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл со ссылками.");
            return null;
        }
        return links;
    }

    private int calculateRemainingLifespan(Link link) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime creationDate = link.getCreationDate();
        int lifespan = link.getLifespan();
        long daysBetween = ChronoUnit.DAYS.between(creationDate, now);
        int remainingDays = lifespan - (int) daysBetween;
        return remainingDays;
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
