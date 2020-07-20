enum UrlAddress {

    SELL_MAIN_WEBSITE("https://www.otodom.pl/sprzedaz/mieszkanie/wroclaw/"),
    SELL_WEBSITE_WITH_PAGE("https://www.otodom.pl/sprzedaz/mieszkanie/wroclaw/?page="),
    RENT_MAIN_WEBSITE("https://www.otodom.pl/wynajem/mieszkanie/wroclaw/"),
    RENT_WEBSITE_WITH_PAGE("https://www.otodom.pl/wynajem/mieszkanie/wroclaw/?page="),
    OFFER_WEBSITE("https://www.otodom.pl/oferta/");

    final String url;

    UrlAddress(String url) {
        this.url = url;
    }
}
