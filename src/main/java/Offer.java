class Offer {
    private String link;
    private double price;
    private double yardage;
    private double squareMeterPrice;

    Offer(String link, double price, double yardage, double squareMeterPrice) {
        this.link = link;
        this.price = price;
        this.yardage = yardage;
        this.squareMeterPrice = squareMeterPrice;
    }

    String getLink() {
        return link;
    }

    double getPrice() {
        return price;
    }

    double getYardage() {
        return yardage;
    }

    double getSquareMeterPrice() {
        return squareMeterPrice;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "link='" + link + '\'' +
                ", price=" + price +
                ", yardage=" + yardage +
                ", squareMeterPrice=" + squareMeterPrice +
                '}';
    }
}
