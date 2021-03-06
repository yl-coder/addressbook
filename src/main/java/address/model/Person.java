package address.model;

import address.util.LocalDateAdapter;
import address.util.LocalDateTimeAdapter;

import javafx.beans.property.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a Person.
 *
 * @author Marco Jakob
 */
public class Person extends DataType {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final IntegerProperty postalCode;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> birthday;
    private URL webPageUrl;
    private ObjectProperty<LocalDateTime> updatedAt;
    private final List<ContactGroup> contactGroups;

    /**
     * Default constructor.
     */
    public Person() {
        this("", "");
    }

    /**
     * Constructor with some initial data.
     * 
     * @param firstName
     * @param lastName
     */
    public Person(String firstName, String lastName) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);

        // Some initial dummy data, just for convenient testing.
        this.street = new SimpleStringProperty("some street");
        this.postalCode = new SimpleIntegerProperty(1234);
        this.city = new SimpleStringProperty("some city");
        this.birthday = new SimpleObjectProperty<>(LocalDate.of(1999, 2, 21));
        this.contactGroups = new ArrayList<>();
        contactGroups.add(new ContactGroup("friends"));
        this.updatedAt = new SimpleObjectProperty<>(LocalDateTime.now());
        try {
            this.webPageUrl = new URL("https://www.github.com");
        } catch (MalformedURLException e) {
            assert false : "Error parsing a parsable URL";
        }

    }

    /**
     * Deep copy constructor
     * @param person
     */
    public Person(Person person) {
        this.firstName = new SimpleStringProperty(person.getFirstName());
        this.lastName = new SimpleStringProperty(person.getLastName());

        this.street = new SimpleStringProperty(person.getStreet());
        this.postalCode = new SimpleIntegerProperty(person.getPostalCode());
        this.city = new SimpleStringProperty(person.getCity());
        this.birthday = new SimpleObjectProperty<>(person.getBirthday());
        this.contactGroups = new ArrayList<>(person.getContactGroupsCopy());
        this.updatedAt = new SimpleObjectProperty<>(person.getUpdatedAt());
        this.webPageUrl = person.getWebPageUrl();
    }

    /**
     * @return a deep copy of the contactGroups
     */
    public List<ContactGroup> getContactGroupsCopy() {
        final List<ContactGroup> copy = new ArrayList<>();
        contactGroups.forEach((cg)->copy.add(cg));
        return copy;
    }

    /**
     * Note: references point back to argument list (no defensive copying)
     * @param contactGroups
     */
    public void setContactGroups(List<ContactGroup> contactGroups) {
        this.contactGroups.clear();
        this.contactGroups.addAll(contactGroups);
        updatedAt.set(LocalDateTime.now());
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
        updatedAt.set(LocalDateTime.now());
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
        updatedAt.set(LocalDateTime.now());
    }

    public String getFullName() {
        return getFirstName() + ' ' + getLastName();
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
        updatedAt.set(LocalDateTime.now());
    }

    public StringProperty streetProperty() {
        return street;
    }

    public int getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(int postalCode) {
        this.postalCode.set(postalCode);
        updatedAt.set(LocalDateTime.now());
    }

    public IntegerProperty postalCodeProperty() {
        return postalCode;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
        updatedAt.set(LocalDateTime.now());
    }

    public StringProperty cityProperty() {
        return city;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
        updatedAt.set(LocalDateTime.now());
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getUpdatedAt() {
        return updatedAt.get();
    }

    public URL getWebPageUrl() {
        return webPageUrl;
    }

    public void setWebPageUrl(URL webPageUrl) {
        this.webPageUrl = webPageUrl;
        updatedAt.set(LocalDateTime.now());
    }

    public ObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }

    /**
     * Updates the attributes based on the values in the parameter.
     * Mutable references are cloned.
     *
     * @param updated The object containing the new attributes.
     * @return self
     */
    public Person update(Person updated) {
        setFirstName(updated.getFirstName());
        setLastName(updated.getLastName());
        setStreet(updated.getStreet());
        setPostalCode(updated.getPostalCode());
        setCity(updated.getCity());
        setBirthday(updated.getBirthday());
        setContactGroups(updated.getContactGroupsCopy());
        setWebPageUrl(updated.getWebPageUrl());
        updatedAt.set(LocalDateTime.now());
        return this;
    }

    @Override
    public boolean equals(Object otherPerson){
        if (otherPerson == this) return true;
        if (otherPerson == null) return false;
        if (!Person.class.isAssignableFrom(otherPerson.getClass())) return false;

        final Person other = (Person) otherPerson;
        if (this.getFirstName() == other.getFirstName() && this.getLastName() == other.getLastName()) return true;
        return this.getFirstName().equals(other.getFirstName()) && this.getLastName().equals(other.getLastName());
    }

    @Override
    public int hashCode() {
        return (getFirstName()+getLastName()).hashCode();
    }

    @Override
    public String toString() {
        return "Person: " + getFullName();
    }

}
