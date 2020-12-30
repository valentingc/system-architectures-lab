package at.fhv.dluvgo.hotel.write.domain;

public class PersonalDetails {
    private final String bookingMadeBy;
    private final int numberOfPeople;

    private PersonalDetails(String bookingMadeBy, int numberOfPeople) {
        this.bookingMadeBy = bookingMadeBy;
        this.numberOfPeople = numberOfPeople;
    }

    public static PersonalDetails create(String bookingMadeBy, int numberOfPeople) {
        if (bookingMadeBy.isEmpty()) {
            throw new IllegalArgumentException("A name is required when booking");
        }

        if (numberOfPeople < 1) {
            throw new IllegalArgumentException("At least one person must be stying in the hotel");
        }

        return new PersonalDetails(bookingMadeBy, numberOfPeople);
    }

    /* ### Getter ### */

    public String getBookingMadeBy() {
        return bookingMadeBy;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }
}
