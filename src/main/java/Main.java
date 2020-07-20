import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

class Main {

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(50);

        String websiteContent = getWebsiteContentFromMultipleSites(UrlAddress.SELL_WEBSITE_WITH_PAGE.url, 100);

        Set<String> links = new HashSet<>(getSetOfOffers(websiteContent));
        AtomicLong counter = new AtomicLong(links.size());

        links.forEach(System.out::println);

        List<Offer> offers = new ArrayList<>();

        for (String link : links) {
            executorService.submit(() -> {
                counter.decrementAndGet();
                addOfferToList(offers, link);
            });
        }

        while (counter.get() != 0) {

        }

        executorService.shutdown();

        offers.removeIf(offer -> offer.getSquareMeterPrice() == 0);
        offers.forEach(System.out::println);

        long end = System.currentTimeMillis();
        System.out.println("Total links: " + links.size());
        System.out.println("Total correct offers: " + offers.size());
        System.out.println("Average price: " + getAveragePrice(offers) + "zł/m²");
        System.out.println("Time: " + ((end - start) / 1000) + " seconds");
    }

    private static Long getAveragePrice(List<Offer> offers) {
        return Math.round(offers.stream()
                .mapToDouble(Offer::getSquareMeterPrice)
                .average()
                .getAsDouble());
    }

    private static void addOfferToList(List<Offer> offers, String link) {
        double price = 0;
        double yardage = 0;
        try {
            price = getPrice(link);
            yardage = getYardage(link);
        } catch (IOException e) {
            System.err.println("Server returned HTTP response code: 500 for URL: " + link);
        }
        double squareMeterPrice = Math.round(price / yardage);
        offers.add(new Offer(link, price, yardage, squareMeterPrice));
    }

    private static Set<String> getSetOfOffers(String content) {
        Set<String> setOfLinks = new HashSet<>();
        for (int i = 0; i < content.length(); i++) {
            i = content.indexOf(UrlAddress.OFFER_WEBSITE.url, i);
            if (i < 0) {
                break;
            }
            String substring = content.substring(i);
            String link = (substring.split(".html"))[0];
            setOfLinks.add(link);
        }
        return setOfLinks;
    }

    private static String getWebsiteContentFromMultipleSites(String url, int sites) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= sites; i++) {
            String currentSiteLink = url + i;
            URL urlAddress = new URL(currentSiteLink);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlAddress.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
                stringBuilder.append(System.lineSeparator());
            }
            in.close();
        }
        return stringBuilder.toString();
    }

    private static String getWebsiteContent(String url) throws IOException {
        URL urlAddress = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(urlAddress.openStream()));

        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            stringBuilder.append(inputLine);
            stringBuilder.append(System.lineSeparator());
        }
        in.close();
        return stringBuilder.toString();
    }

    private static Double getPrice(String url) throws IOException {
        String websiteContent = getWebsiteContent(url);
        int startIndex = websiteContent.indexOf("<div class=\"css-1vr19r7\">");
        int endIndex = websiteContent.indexOf(" zł<!-- -->");
        if (startIndex == -1 || endIndex == -1) {
            return 0d;
        }
        String substring = websiteContent.substring(startIndex, endIndex);
        String stringPrice = (substring.split("css-1vr19r7\">"))[1]
                .replaceAll("\\s+", "")
                .replaceAll(",", ".");
        return Double.valueOf(stringPrice);
    }

    private static Double getYardage(String url) throws IOException {
        String websiteContent = getWebsiteContent(url);
        int startIndex = websiteContent.indexOf("<li>Powierzchnia: ");
        int endIndex = websiteContent.indexOf(" m²</strong>");
        if (startIndex == -1 || endIndex == -1) {
            return 0d;
        }
        String substring = websiteContent.substring(startIndex, endIndex);
        String stringPrice = (substring.split("<strong>"))[1]
                .replaceAll("\\s+", "")
                .replaceAll(",", ".");
        try {
            return Double.valueOf(stringPrice);
        } catch (NumberFormatException e) {
            System.out.println("Error: " + url);
        }
        return 0d;
    }
}
