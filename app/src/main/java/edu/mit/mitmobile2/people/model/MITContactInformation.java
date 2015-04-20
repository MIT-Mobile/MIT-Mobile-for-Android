package edu.mit.mitmobile2.people.model;

/**
 * Created by grmartin on 4/20/15.
 */
public class MITContactInformation implements MITPeopleDirectoryPersonAdaptableSurrogate {
    private final MITPerson person;
    private final MITPersonIndexPath index;
    private final String name;
    private final String value;

    public MITContactInformation(MITPerson person, MITPersonIndexPath index) {
        this.person = person;
        this.index = index;

        this.name = index.getAttributeType().getKeyName();
        this.value = person.valueForIndexPath(index);
    }

    public MITPerson getPerson() {
        return person;
    }

    public MITPersonIndexPath getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public MITPersonAttribute getAttributeType() {
        return index.getAttributeType();
    }

    @Override
    public String toString() {
        return "MITContactInformation{" +
                "person=" + person +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
