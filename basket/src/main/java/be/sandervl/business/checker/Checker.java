package be.sandervl.business.checker;

public interface Checker<T> {
    public boolean check(T obj);
}
