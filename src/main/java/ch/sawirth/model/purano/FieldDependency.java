package ch.sawirth.model.purano;

public class FieldDependency {
    public final String name;
    public final String owner;
    public final String desc;

    public FieldDependency(String name, String owner, String type) {
        this.name = name;
        this.owner = owner;
        this.desc = type;
    }
}
