package address.model;

import address.events.EventManager;
import address.events.FilterCommittedEvent;
import address.events.LocalModelSyncedEvent;
import address.events.NewMirrorDataEvent;
import address.events.*;

import com.google.common.eventbus.Subscribe;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager {

    /**
     * The data as an observable list of Persons.
     */
    private final ObservableList<Person> personData = FXCollections.observableArrayList();
    private final FilteredList<Person> filteredPersonData = new FilteredList<>(personData);
    private final ObservableList<ContactGroup> groupData = FXCollections.observableArrayList();

    /**
     * @param initialPersons Initial persons to populate the model.
     * @param initialGroups Initial groups to populate the model.
     */
    public ModelManager(List<Person> initialPersons, List<ContactGroup> initialGroups) {
        if (initialPersons == null || initialGroups == null) {
            populateDummyData();
        } else {
            System.out.println("Data found.");
            System.out.println("Persons found : " + initialPersons.size());
            personData.addAll(initialPersons);
            System.out.println("Groups found : " + initialGroups.size());
            groupData.addAll(initialGroups);
        }

        //Listen to any changed to person data and raise an event
        //Note: this will not catch edits to Person objects
        personData.addListener(
                (ListChangeListener<? super Person>) (change) ->
                        EventManager.getInstance().post(new LocalModelChangedEvent(personData, groupData)));

        //Listen to any changed to group data and raise an event
        //Note: this will not catch edits to ContactGroup objects
        groupData.addListener(
                (ListChangeListener<? super ContactGroup>) (change) ->
                        EventManager.getInstance().post(new LocalModelChangedEvent(personData, groupData)));

        //Register for general events relevant to data manager
        EventManager.getInstance().registerHandler(this);
    }

    public ModelManager(AddressBookWrapper addressBook) {
        this(addressBook == null ? null : addressBook.getPersons(),
            addressBook == null ? null : addressBook.getGroups());
    }

    protected void populateDummyData() {
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));

        groupData.add(new ContactGroup("relatives"));
        groupData.add(new ContactGroup("friends"));
    }

    /**
     * Returns the data as an observable list of Persons.
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return filteredPersonData;
    }

    /**
     * Returns the contact groups as a list.
     * @return
     */
    public List<ContactGroup> getGroupData() {
        return groupData;
    }

    /**
     * Adds new data to existing data.
     * If a Person in the new data has the same
     * first name as an existing Person, the older one will be kept.
     * @param data
     */
    public synchronized void addNewData(AddressBookWrapper data) {
        System.out.println("Attempting to add a persons list of size " + data.getPersons().size());

        for (Person p : data.getPersons()) {
            Optional<Person> storedPerson = getPerson(p);
            if (!storedPerson.isPresent()) {
                personData.add(p);
                System.out.println("New data added " + p);
                continue;
            }

            Person personInModel = storedPerson.get();
            if (!p.getUpdatedAt().isBefore(personInModel.getUpdatedAt())) {
                storedPerson.get().update(p);
            }
        }

        System.out.println("Attempting to add a groups list of size " + data.getGroups().size());

        for (ContactGroup g : data.getGroups()) {
            Optional<ContactGroup> storedGroup = getGroup(g);
            if (storedGroup.isPresent()) {
                storedGroup.get().update(g);
            } else {
                groupData.add(g);
                System.out.println("New group data added " + g);
            }
        }

        EventManager.getInstance().post(new LocalModelSyncedEvent(personData, groupData));
    }

    private Optional<Person> getPerson(Person person) {
        for (Person p : personData) {
            if (p.equals(person)) {
                return Optional.of(p);
            }
        }

        return Optional.empty();
    }

    private Optional<ContactGroup> getGroup(ContactGroup group) {
        for (ContactGroup g : groupData) {
            if (g.equals(group)) {
                return Optional.of(g);
            }
        }

        return Optional.empty();
    }

    /**
     * Updates the details of a Person object. Updates to Person objects should be
     * done through this method to ensure the proper events are raised to indicate
     * a change to the model.
     * @param original The Person object to be changed.
     * @param updated The temporary Person object containing new values.
     */
    public synchronized void updatePerson(Person original, Person updated){
        assert !updated.getUpdatedAt().isBefore(original.getUpdatedAt());
        original.update(updated);
        EventManager.getInstance().post(new LocalModelChangedEvent(personData, groupData));
    }

    /**
     * Deletes the person from the model.
     * @param personToDelete
     */
    public synchronized void deletePerson(Person personToDelete){
        personData.remove(personToDelete);
    }

    /**
     * Adds a person to the model
     * @param personToAdd
     */
    public synchronized void addPerson(Person personToAdd) {
        personData.add(personToAdd);
    }

    /**
     * Updates the details of a ContactGroup object. Updates to ContactGroup objects should be
     * done through this method to ensure the proper events are raised to indicate
     * a change to the model.
     * @param original The ContactGroup object to be changed.
     * @param updated The temporary ContactGroup object containing new values.
     */
    public synchronized void updateGroup(ContactGroup original, ContactGroup updated){
        original.update(updated);
        EventManager.getInstance().post(new LocalModelChangedEvent(personData, groupData));
    }

    /**
     * Deletes the group from the model.
     * @param groupToDelete
     */
    public synchronized void deleteGroup(ContactGroup groupToDelete){
        groupData.remove(groupToDelete);
    }

    /**
     * Adds a group to the model
     * @param groupToAdd
     */
    public synchronized void addGroup(ContactGroup groupToAdd) {
        groupData.add(groupToAdd);
    }

    @Subscribe
    private void handleNewMirrorDataEvent(NewMirrorDataEvent nde){
        addNewData(nde.data);
    }

    @Subscribe
    private void handleFilterCommittedEvent(FilterCommittedEvent fce) {
        filteredPersonData.setPredicate(fce.filterExpression::satisfies);
    }

    /**
     * Clears existing model and replaces with the provided new data.
     * @param newData
     */
    public void resetData(List<Person> newData, List<ContactGroup> newGroups) {
        personData.clear();
        personData.addAll(newData);

        groupData.clear();
        groupData.addAll(newGroups);
    }
}
