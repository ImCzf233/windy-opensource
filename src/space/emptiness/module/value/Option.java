/*
 * Decompiled with CFR 0_132.
 */
package space.emptiness.module.value;

public class Option<V>
extends Value<V> {
    public Option(String displayName, String name, V enabled) {
        super(displayName, name);
        this.setValue(enabled);
    }
}

