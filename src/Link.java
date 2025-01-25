import java.time.LocalDateTime;

public class Link {

    private String longUrl;
    private String shortUrl;
    private LocalDateTime creationDate;
    private int maxUses;
    private int lifespan;
    private int uses;
    private String username;

    public Link(String longUrl, String shortUrl, LocalDateTime creationDate, int maxUses, int lifespan, String username, int uses) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.creationDate = creationDate;
        this.maxUses = maxUses;
        this.lifespan = lifespan;
        this.username = username;
        this.uses = uses;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getLifespan() {
        return lifespan;
    }

    public String getUsername() {
        return username;
    }

    public int getUses() {
        return uses;
    }

    // Метод для преобразования ссылки в строку для записи в файл
    @Override
    public String toString() {
        return longUrl + "///" + shortUrl + "///" + creationDate + "///" + maxUses + "///" + lifespan + "///" + username + "///" + uses;
    }
}
