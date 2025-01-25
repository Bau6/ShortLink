import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws URISyntaxException, IOException {
        while (true) {
            System.out.println("Выберите действие:");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("3. Выход из программы");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Выход из программы...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте еще раз.");
            }

            // Если пользователь авторизовался, отображаем меню пользователя
            if (currentUser != null) {
                while (true) {
                    System.out.println("Выберите действие:");
                    System.out.println("1. Ввести длинную ссылку");
                    System.out.println("2. Посмотреть все данные о себе");
                    System.out.println("3. Перейти по ссылке");
                    System.out.println("4. Выйти из аккаунта");

                    int userChoice = scanner.nextInt();
                    String shortUrl = "";
                    String longUrl = "";
                    switch (userChoice) {
                        case 1:
                            System.out.println("Введите длинную ссылку:");
                            longUrl = scanner.nextLine();
                            while (longUrl == null || longUrl.equals( "")) {
                                longUrl = scanner.nextLine();
                            }

                            // Получаем дату создания ссылки
                            LocalDateTime creationDate = LocalDateTime.now();

                            // Генерируем короткую ссылку
                            shortUrl = generateShortUrl();

                            // Получаем количество допустимых использований и время жизни ссылки в днях от пользователя
                            System.out.println("Введите количество допустимых использований:");
                            int maxUses = scanner.nextInt();

                            System.out.println("Введите время жизни ссылки в днях:");
                            int lifespan = scanner.nextInt();

                            // Создаем объект ссылки
                            Link link = new Link(longUrl, shortUrl, creationDate, maxUses, lifespan, currentUser.getUsername(), 0);

                            // Сохраняем ссылку в файл
                            try {
                                FileWriter writer = new FileWriter("MyLinks.txt", true);
                                writer.write(link.toString() + "\n");
                                writer.close();
                            } catch (IOException e) {
                                System.out.println("Не удалось сохранить ссылку в файл.");
                                return;
                            }

                            // Выводим короткую ссылку пользователю
                            System.out.println("Короткая ссылка: " + shortUrl);
                            break;
                        case 2:
                            currentUser.printUserInfo();
                            break;
                        case 3:
                            currentUser.printLinks();
                            String chooseLink = scanner.nextLine();
                            while (chooseLink == null || chooseLink.equals("")) {
                                chooseLink = scanner.nextLine();
                            }
                            currentUser.goToLink(chooseLink);
                            break;
                        case 4:
                            currentUser = null;
                            System.out.println("Вы вышли из аккаунта.");
                            break;
                        default:
                            System.out.println("Неверный выбор. Попробуйте еще раз.");
                    }

                    // Если пользователь вышел из аккаунта, возвращаем его в начальное меню
                    if (currentUser == null) {
                        break;
                    }
                }
            }
        }
    }

    // Метод для генерации случайной короткой ссылки
    private static String generateShortUrl() {
        String shortUrl = "clck.ru/";
        // Генерируем 6 случайных символов
        for (int i = 0; i < 6; i++) {
            int randomInt = (int) (Math.random() * 26);
            char randomChar = (char) (97 + randomInt);
            shortUrl += randomChar;
        }
        return shortUrl;
    }

    private static void register() {
        System.out.println("Введите имя пользователя:");
        String username = "";
        username = scanner.nextLine();
        while (username == null || username.equals( "")) {
            username = scanner.nextLine();
        }
        // Проверяем, существует ли уже такой логин
        boolean usernameExists = false;
        try {
            File file = new File("MyUsers.txt");
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("///");
                if (parts[0].equals(username)) {
                    usernameExists = true;
                    break;
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл с пользователями.");
            return;
        }

        // Если логин уже существует, выводим сообщение об ошибке
        if (usernameExists) {
            System.out.println("Пользователь с таким именем уже существует.");
            return;
        }

        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        // Создаем нового пользователя и сохраняем его данные в файл
        User user = new User(username, password);
        try {
            FileWriter writer = new FileWriter("MyUsers.txt", true);
            writer.write(user.getUsername() + "///" + user.getPassword() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Не удалось сохранить данные пользователя.");
            return;
        }

        System.out.println("Регистрация прошла успешно.");
    }

    private static void login() {
        System.out.println("Введите имя пользователя:");
        String username = "";
        username = scanner.nextLine();
        while (username == null || username.equals( "")) {
            username = scanner.nextLine();
        }
        System.out.println("Введите пароль:");
        String password = scanner.nextLine();

        // Пытаемся найти пользователя в файле
        try {
            File file = new File("MyUsers.txt");
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("///");
                // Проверяем, совпадают ли логин и пароль с данными из файла
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    // Пользователь найден, устанавливаем его как текущего пользователя
                    currentUser = new User(username, password);
                    System.out.println("Вход выполнен успешно.");
                    return;
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл с пользователями.");
            return;
        }

        System.out.println("Неверный логин или пароль.");
    }
}
