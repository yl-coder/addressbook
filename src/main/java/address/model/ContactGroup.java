package address.model;

import javafx.beans.property.SimpleStringProperty;

public class ContactGroup extends DataType {

    private final SimpleStringProperty name;

    public ContactGroup() {
        this.name = new SimpleStringProperty("");
    }

    public ContactGroup(String name) {
        this.name = new SimpleStringProperty(name);
    }

    // Copy constructor
    public ContactGroup(ContactGroup grp) {
        name = new SimpleStringProperty(grp.getName());
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ContactGroup update(ContactGroup group) {
        setName(group.getName());
        return this;
    }

    @Override
    public boolean equals(Object otherGroup){
        if (otherGroup == this) return true;
        if (otherGroup == null) return false;
        if (!ContactGroup.class.isAssignableFrom(otherGroup.getClass())) return false;

        final ContactGroup other = (ContactGroup) otherGroup;
        if (this.getName() == other.getName()) return true;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        return "Group: " + getName();
    }

}
