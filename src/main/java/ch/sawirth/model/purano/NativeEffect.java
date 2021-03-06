package ch.sawirth.model.purano;

public class NativeEffect {
    public final String owner;
    public final String name;
    public final String originOwner;
    public final String originName;
    public final boolean isDynamicEffect;

    public NativeEffect(String owner, String name, String originOwner, String originName, boolean isDynamicEffect) {
        this.owner = owner;
        this.name = name;
        this.originOwner = originOwner;
        this.originName = originName;
        this.isDynamicEffect = isDynamicEffect;
    }
}
